package hms.web.control.zk.account;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Include;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;

import hms.account.bpu.AcntBpuType;
import hms.account.bpu.CnspBpuCashDiscount;
import hms.account.bpu.CnspBuilder1;
import hms.util.ZKUtil;
import hms.web.zk.HmsMessageBox;
import hms.web.zk.HmsNotification;
import hms_kernel.account.AccountService;
import hms_kernel.account.Consumption;
import hms_kernel.account.DirectionEnum;
import hms_kernel.account.PaymentTypeEnum;
import hms_kernel.account.TypeCategoryEnum;
import hms_kernel.account.TypeEnum;
import legion.BusinessServiceFactory;
import legion.biz.BpuFacade;
import legion.util.DateFormatUtil;
import legion.util.NumberFormatUtil;
import legion.util.TimeTraveler;
import legion.web.zk.ZkUtil;

/**
 * 消費管理(新版)的新增消費側欄。
 * <p>
 * 新增邏輯與 {@link GridAddCnspComposer} 相同(一般消費、各種折抵、代墊款)，另外支援連續輸入：送出後保留類型、付款方式與日期，只清空說明和金額。
 * GridAddCnspComposer 仍由舊版消費管理與手機版使用，這裡不去動它。
 */
public class CnspManageAddPanelComposer extends SelectorComposer<Component> {
	private final static String SRC = "/account/cnspManageAddPanel.zul";

	private Logger log = LoggerFactory.getLogger(CnspManageAddPanelComposer.class);

	public static CnspManageAddPanelComposer of(Include _icd) {
		return ZkUtil.of(_icd, SRC, "divCnspAddPanel");
	}

	// -------------------------------------------------------------------------------
	@Wire
	private Label lbApCopied;
	@Wire
	private Combobox cbbApBehavior;
	@Wire
	private Combobox cbbApTypeCategory;
	@Wire
	private Combobox cbbApType;
	@Wire
	private Div fldApDirection;
	@Wire
	private Radiogroup rgApDirection;
	@Wire
	private Textbox txbApDescription;
	@Wire
	private Intbox itbApAmount;
	@Wire
	private Div fldApPaymentType;
	@Wire
	private Combobox cbbApPaymentType;
	@Wire
	private Checkbox chbApInAdv;
	@Wire
	private Intbox itbApInAdv;
	@Wire
	private Datebox dtbApDate;
	@Wire
	private Label lbApLogSummary;
	@Wire
	private Listbox lbxApLog;

	// -------------------------------------------------------------------------------
	private AccountService accountService = BusinessServiceFactory.getInstance().getService(AccountService.class);

	private Consumer<Set<Consumption>> csmAfterAdding = set -> {
	};

	/** 本次開啟畫面後新增的消費(最新的在前)。 */
	private final List<Consumption> addedLog = new ArrayList<>();

	// -------------------------------------------------------------------------------
	public void init(Consumer<Set<Consumption>> _csmAfterAdding) {
		if (_csmAfterAdding != null)
			csmAfterAdding = _csmAfterAdding;

		ZKUtil.configureConsumptionTypeCategory(cbbApTypeCategory, false);

		rgApDirection.getChildren().clear();
		for (DirectionEnum d : DirectionEnum.values(false)) {
			Radio rd = new Radio(d.getName());
			rd.setValue(d);
			rgApDirection.appendChild(rd);
		}

		ZkUtil.initCbb(cbbApPaymentType, PaymentTypeEnum.values(false), false);

		lbxApLog.setItemRenderer((li, cnsp, i) -> {
			Consumption c = (Consumption) cnsp;
			li.appendChild(new Listcell(c.getDate().toString().substring(5))); // MM-dd
			li.appendChild(new Listcell(c.getType().getName() + " " + nvl(c.getDescription())));
			li.appendChild(new Listcell(NumberFormatUtil.getIntegerString(c.getAmount())));
		});

		resetAll();
	}

	// -------------------------------------------------------------------------------
	/** 全部欄位回到預設值。 */
	public void resetAll() {
		cbbApBehavior.setSelectedIndex(0);
		cbbApTypeCategory.setSelectedIndex(0);
		refreshTypeItems(true);
		rgApDirection.setSelectedIndex(0);
		cbbApPaymentType.setSelectedIndex(0);
		dtbApDate.setValue(new Date(System.currentTimeMillis()));
		clearForNext();
		lbApCopied.setVisible(false);
		behaviorChanged();
	}

	/** 連續輸入：只清空說明、金額與代墊款，其他欄位保留。 */
	private void clearForNext() {
		txbApDescription.setValue(null);
		itbApAmount.setValue(null);
		chbApInAdv.setChecked(false);
		itbApInAdv.setDisabled(true);
		itbApInAdv.setValue(null);
	}

	public void focusFirst() {
		txbApDescription.focus();
	}

	/** 帶入一筆既有消費的內容，消費日期預設為今天。 */
	public void copyCnsp(Consumption _cnsp) {
		if (_cnsp == null)
			return;
		cbbApBehavior.setSelectedIndex(0);
		behaviorChanged();

		selectItemByValue(cbbApTypeCategory, _cnsp.getType().getCategory());
		refreshTypeItems(false);
		if (!selectItemByValue(cbbApType, _cnsp.getType())) {
			// 已停用的類型不在選單中，補一個選項讓它仍可沿用
			Comboitem ci = new Comboitem(_cnsp.getType().getName());
			ci.setValue(_cnsp.getType());
			cbbApType.appendChild(ci);
			cbbApType.setSelectedItem(ci);
		}

		for (Component c : rgApDirection.getChildren()) {
			Radio rd = (Radio) c;
			if (rd.getValue() == _cnsp.getDirection())
				rgApDirection.setSelectedItem(rd);
		}

		selectItemByValue(cbbApPaymentType, _cnsp.getPaymentType());
		txbApDescription.setValue(_cnsp.getDescription());
		itbApAmount.setValue(_cnsp.getAmount());
		chbApInAdv.setChecked(false);
		itbApInAdv.setDisabled(true);
		itbApInAdv.setValue(null);
		dtbApDate.setValue(new Date(System.currentTimeMillis()));

		lbApCopied.setValue("已從「" + _cnsp.getDate() + " " + nvl(_cnsp.getDescription()) + "」複製");
		lbApCopied.setVisible(true);
		itbApAmount.select();
		itbApAmount.focus();
	}

	// -------------------------------------------------------------------------------
	@Listen(Events.ON_SELECT + "=#cbbApTypeCategory")
	public void cbbApTypeCategory_selected() {
		refreshTypeItems(true);
	}

	private void refreshTypeItems(boolean _selectFirst) {
		cbbApType.getChildren().clear();
		cbbApType.setValue(null);
		if (cbbApTypeCategory.getSelectedItem() == null)
			return;
		TypeCategoryEnum cate = cbbApTypeCategory.getSelectedItem().getValue();
		ZkUtil.initCbb(cbbApType, accountService.getTypes(cate, true), false);
		if (_selectFirst && cbbApType.getItemCount() > 0)
			cbbApType.setSelectedIndex(0);
	}

	@Listen(Events.ON_SELECT + "=#cbbApBehavior")
	public void behaviorChanged() {
		boolean normal = cbbApBehavior.getSelectedIndex() <= 0;
		fldApDirection.setVisible(normal);
		fldApPaymentType.setVisible(normal);
	}

	@Listen(Events.ON_CHECK + "=#chbApInAdv")
	public void chbApInAdv_checked() {
		itbApInAdv.setDisabled(!chbApInAdv.isChecked());
		if (chbApInAdv.isChecked())
			itbApInAdv.focus();
	}

	@Listen(Events.ON_CLICK + "=#btnApToday")
	public void btnApToday_clicked() {
		dtbApDate.setValue(new Date(System.currentTimeMillis()));
	}

	@Listen(Events.ON_CLICK + "=#btnApYesterday")
	public void btnApYesterday_clicked() {
		Date d = dtbApDate.getValue() == null ? new Date(System.currentTimeMillis()) : dtbApDate.getValue();
		dtbApDate.setValue(new Date(d.getTime() - 24L * 60 * 60 * 1000));
	}

	@Listen(Events.ON_CLICK + "=#btnApClear")
	public void btnApClear_clicked() {
		resetAll();
	}

	// -------------------------------------------------------------------------------
	@Listen(Events.ON_CLICK + "=#btnApAdd; " + Events.ON_OK + "=#txbApDescription; " + Events.ON_OK
			+ "=#itbApAmount; " + Events.ON_OK + "=#itbApInAdv")
	public void btnApAdd_clicked() {
		if (cbbApType.getSelectedItem() == null || cbbApType.getSelectedItem().getValue() == null) {
			HmsNotification.warning("請選擇類型。");
			return;
		}
		if (dtbApDate.getValue() == null) {
			HmsNotification.warning("請填消費日期。");
			return;
		}
		if (itbApAmount.getValue() == null) {
			HmsNotification.warning("請填消費金額。");
			itbApAmount.focus();
			return;
		}

		TimeTraveler tt = new TimeTraveler();
		Set<Consumption> set = new HashSet<>();
		StringBuilder msg = new StringBuilder();
		boolean ok;
		switch (cbbApBehavior.getSelectedIndex()) {
		case 1:
			ok = behaviorDiscount(set, msg, tt, TypeEnum.LIFE_OTHER, "LINE POINTS");
			break;
		case 2:
			ok = behaviorDiscount(set, msg, tt, TypeEnum.TRAFFIC_OTHER, "悠遊卡");
			break;
		case 3:
			ok = behaviorDiscount(set, msg, tt, TypeEnum.TRAFFIC_OTHER, "一卡通");
			break;
		case 4:
			ok = behaviorDiscount(set, msg, tt, TypeEnum.LIFE_OTHER, "P幣");
			break;
		case 5:
			ok = behaviorDiscount(set, msg, tt, TypeEnum.LIFE_OTHER, "全聯儲值金");
			break;
		case 6:
			ok = behaviorDiscount(set, msg, tt, TypeEnum.FOOD, "麥當勞點點卡");
			break;
		default:
			ok = behaviorNormal(set, msg, tt);
			break;
		}
		if (!ok) {
			HmsNotification.warning(msg.toString());
			return;
		}

		/* 代墊款 */
		TypeEnum type = cbbApType.getSelectedItem().getValue();
		String description = txbApDescription.getValue();
		LocalDate cnspDate = DateFormatUtil.parseLocalDate(dtbApDate.getValue());
		Integer inAdvAmt = itbApInAdv.getValue();
		if (chbApInAdv.isChecked() && inAdvAmt != null && inAdvAmt > 0) {
			CnspBuilder1 inAdvBuilder = BpuFacade.getInstance().getBuilder(AcntBpuType.CNSP_1);
			if (inAdvBuilder == null) {
				tt.travel();
				HmsNotification.error();
				return;
			}
			inAdvBuilder.appendType(type).appendDirection(DirectionEnum.IN_ADV).appendAmount(inAdvAmt)
					.appendDescription("代付-" + description).appendPaymentType(PaymentTypeEnum.CASH)
					.appendDate(cnspDate);
			StringBuilder inAdvMsg = new StringBuilder();
			Consumption cnspInAdv = inAdvBuilder.build(inAdvMsg, tt);
			String msgInAdv = "新增代付流入[" + type.getName() + "][代付-" + description + "]["
					+ NumberFormatUtil.getIntegerString(inAdvAmt) + "]";
			if (cnspInAdv != null) {
				set.add(cnspInAdv);
				HmsNotification.info(msg + System.lineSeparator() + msgInAdv + "成功。");
			} else {
				HmsMessageBox.error(msg + System.lineSeparator() + msgInAdv + "失敗。" + System.lineSeparator()
						+ inAdvMsg);
			}
		} else {
			HmsNotification.info(msg.toString());
		}

		// 連續輸入記錄
		addedLog.addAll(0, set);
		lbxApLog.setModel(new ListModelList<>(addedLog));
		int sum = addedLog.stream()
				.mapToInt(c -> DirectionEnum.OUT == c.getDirection() ? c.getAmount() : -c.getAmount()).sum();
		lbApLogSummary.setValue("本次已新增 " + addedLog.size() + " 筆 · 淨額 " + NumberFormatUtil.getIntegerString(sum));

		csmAfterAdding.accept(set);

		clearForNext();
		lbApCopied.setVisible(false);
		txbApDescription.focus();
	}

	/** 一般消費 */
	private boolean behaviorNormal(Set<Consumption> _set, StringBuilder _msg, TimeTraveler _tt) {
		if (rgApDirection.getSelectedItem() == null || cbbApPaymentType.getSelectedItem() == null) {
			_msg.append("請選擇流向和付款方式。");
			return false;
		}
		TypeEnum type = cbbApType.getSelectedItem().getValue();
		DirectionEnum direction = rgApDirection.getSelectedItem().getValue();
		String description = txbApDescription.getValue();
		Integer amount = itbApAmount.getValue();
		PaymentTypeEnum paymentType = cbbApPaymentType.getSelectedItem().getValue();
		LocalDate cnspDate = DateFormatUtil.parseLocalDate(dtbApDate.getValue());

		CnspBuilder1 bpu = BpuFacade.getInstance().getBuilder(AcntBpuType.CNSP_1);
		if (bpu == null) {
			log.error("bpu null.");
			_msg.append("系統錯誤，無法新增。");
			return false;
		}
		bpu.appendType(type).appendDirection(direction).appendDescription(description)
				.appendAmount(amount == null ? 0 : amount).appendPaymentType(paymentType).appendDate(cnspDate);
		if (!bpu.verify(_msg))
			return false;

		TimeTraveler tt = new TimeTraveler();
		Consumption cnsp = bpu.build(_msg, tt);
		if (_tt != null)
			_tt.copySitesFrom(tt);

		String msg = "新增一般消費[" + type.getCategory().getTitle() + "][" + type.getName() + "][" + direction.getName()
				+ "][" + description + "][" + NumberFormatUtil.getIntegerString(amount) + "][" + paymentType.getName()
				+ "][" + cnspDate + "]";
		if (cnsp != null) {
			_msg.append(msg + "成功。");
			_set.add(cnsp);
			return true;
		}
		_msg.append(msg + "失敗!");
		log.error("bpu.build return null.");
		return false;
	}

	/** 各種折抵 */
	private boolean behaviorDiscount(Set<Consumption> _set, StringBuilder _msg, TimeTraveler _tt,
			TypeEnum _discountType, String _discountMediaName) {
		CnspBpuCashDiscount bpu = BpuFacade.getInstance().getBuilder(AcntBpuType.CNSP_CASH_DISCOUNT);
		if (bpu == null) {
			log.error("bpu null.");
			_msg.append("系統錯誤，無法新增。");
			return false;
		}
		TypeEnum type = cbbApType.getSelectedItem().getValue();
		String description = txbApDescription.getValue();
		int amount = itbApAmount.getValue();
		LocalDate cnspDate = DateFormatUtil.parseLocalDate(dtbApDate.getValue());

		bpu.appendDiscountType(_discountType).appendDiscountMediaName(_discountMediaName);
		bpu.appendCnspType(type).appendDescription(description).appendAmount(amount).appendDate(cnspDate);

		String msg = "新增" + _discountMediaName + "折抵[" + type.getCategory().getTitle() + "][" + type.getName() + "]["
				+ description + "][" + NumberFormatUtil.getIntegerString(amount) + "][" + cnspDate + "]";
		if (bpu.build(_msg, _tt)) {
			_msg.append(msg + "成功。");
			_set.add(bpu.getCnsp());
			_set.add(bpu.getDiscount());
			return true;
		}
		_msg.append(msg + "失敗!");
		return false;
	}

	// -------------------------------------------------------------------------------
	private static boolean selectItemByValue(Combobox _cbb, Object _value) {
		for (Comboitem ci : _cbb.getItems()) {
			if (ci.getValue() == _value) {
				_cbb.setSelectedItem(ci);
				return true;
			}
		}
		return false;
	}

	private static String nvl(String _s) {
		return _s == null ? "" : _s;
	}
}
