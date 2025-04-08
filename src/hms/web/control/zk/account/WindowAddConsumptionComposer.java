package hms.web.control.zk.account;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Include;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

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
import legion.util.LogUtil;
import legion.util.NumberFormatUtil;
import legion.util.TimeTraveler;
import legion.web.zk.ZkUtil;

public class WindowAddConsumptionComposer extends SelectorComposer<Component> {
	// -------------------------------------------------------------------------------
	private final static String SRC = "/account/windowAddConsumption.zul";

//	final static String DYNAMIC_PROPERTY_CSM_AFTER_ADDING_CNSP = "DYNAMIC_PROPERTY_CSM_AFTER_ADDING_CNSP";
	// -------------------------------------------------------------------------------

	private Logger log = LoggerFactory.getLogger(WindowAddConsumptionComposer.class);

	public static WindowAddConsumptionComposer of(Include _icd) {
		return ZkUtil.of(_icd, SRC, "wdAddCnsp");
	}

	// -------------------------------------------------------------------------------
	@WireVariable("requestScope")
	private Map<String, Object> requestScope;

	@Wire
	private Window wdAddCnsp;
	@Wire
	private Combobox cbbBehavior;
	@Wire
	private Combobox cbbTypeCategory;
	@Wire
	private Combobox cbbType;
	@Wire
	private Row rowDirection;
	@Wire
	private Radiogroup rgDirection;
	@Wire
	private Textbox txbDescription;
	@Wire
	private Intbox itbConsumptionAmount;
	@Wire
	private Row rowPaymentType;
	@Wire
	private Combobox cbbPaymentType;

	/* 代墊款(付款方式為信用卡時才會出現此列) */
//	@Wire
//	private Row rowInAdv;
	@Wire
	private Checkbox chbInAdv;
	@Wire
	private Intbox itbInAdv;

	@Wire
	private Datebox dtbConsumptionDate;

	// -------------------------------------------------------------------------------
	private Consumer<Set<Consumption>> csmAfterAddingCnsp = set->{}; // default do nothing

	// -------------------------------------------------------------------------------
	@Override
	public void doAfterCompose(Component comp) {
		try {
			super.doAfterCompose(comp);
//			csmAfterAddingCnsp = (Consumer<Set<Consumption>>) requestScope.get(DYNAMIC_PROPERTY_CSM_AFTER_ADDING_CNSP);
//			if (csmAfterAddingCnsp == null)
//				csmAfterAddingCnsp = set -> {
//				};

//			init();
		} catch (Throwable e) {
			LogUtil.log(e, Level.ERROR);
		}
	}

	// -------------------------------------------------------------------------------
//	private void init() {
	 void init(Consumer<Set<Consumption>> csmAfterAddingCnsp) {
		/* init cbbTypeCategory items */
		ZKUtil.configureConsumptionTypeCategory(cbbTypeCategory);
		cbbTypeCategory.addEventListener(Events.ON_SELECT, cnspCateSyncEl);

		/* init rgDirection radios */
		rgDirection.getChildren().clear();
		for (DirectionEnum d : DirectionEnum.values(false)) {
			Radio rd = new Radio(d.getName());
			rd.setValue(d);
			rgDirection.appendChild(rd);
		}

		/* init cbbPaymentType items */
		ZKUtil.configurePaymentType(cbbPaymentType);

		//
		resetBlanks();

		//
		if (csmAfterAddingCnsp != null)
			this.csmAfterAddingCnsp = csmAfterAddingCnsp;
	}

	private boolean defaultTypeFlag = true; // 當cbbType隨cbbTypeCate變動時，是否要帶入預設值

	private EventListener<Event> cnspCateSyncEl =  evt -> {
		cbbType.getChildren().clear();

		TypeCategoryEnum _cate = cbbTypeCategory.getSelectedItem().getValue();

//		for (TypeEnum type : BusinessServiceFactory.getInstance().getService(AccountService.class).getTypes(_cate, true)) { // 只載入enabled的類型
//			Comboitem cbi = new Comboitem(type.getName());
//			cbi.setValue(type);
//			cbbType.appendChild(cbi);
//		}
		ZkUtil.initCbb(cbbType, BusinessServiceFactory.getInstance().getService(AccountService.class).getTypes(_cate, true), false);

		if(defaultTypeFlag) {
			if (cbbType.getItemCount() > 0)
				cbbType.setSelectedIndex(0);
			else
				cbbType.setValue(null);
		}


		System.out.println("cbbType.getValue(): "+cbbType.getValue());
	};

	private void resetBlanks() {
		defaultTypeFlag = true;

		cbbBehavior.setSelectedIndex(0);
		cbbTypeCategory.setSelectedIndex(0);
		Events.postEvent(Events.ON_SELECT, cbbTypeCategory, null);
		rgDirection.setSelectedIndex(0);
		txbDescription.setValue(null);
		itbConsumptionAmount.setValue(null);
		cbbPaymentType.setSelectedIndex(0);
		chbInAdv.setChecked(false);
		itbInAdv.setDisabled(true);
		itbInAdv.setValue(null);
		dtbConsumptionDate.setValue(new Date(System.currentTimeMillis()));

		cbbBehavior_selected();
	}

	// -------------------------------------------------------------------------------
	public void showWindow() {
		resetBlanks();
		wdAddCnsp.setVisible(true);
	}

	// -------------------------------------------------------------------------------
	@Listen(Events.ON_SELECT + "=#cbbBehavior")
	public void cbbBehavior_selected() {
//		rowInAdv.setVisible(false);
//		chbInAdv.setDisabled(true);

		switch (cbbBehavior.getSelectedIndex()) {
		case 0:
			rowDirection.setVisible(true);
			rowPaymentType.setVisible(true);
			break;
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
			rowDirection.setVisible(false);
			rowPaymentType.setVisible(false);
			break;
		}
	}

//	@Listen(Events.ON_SELECT +"=#cbbPaymentType")
//	public void cbbPaymentType_selected() {
//		if (cbbPaymentType.getSelectedItem() == null)
//			return;
//		PaymentTypeEnum pmType =cbbPaymentType.getSelectedItem().getValue();
////		rowInAdv.setVisible(PaymentTypeEnum.CARD == pmType);
//		chbInAdv.setDisabled(PaymentTypeEnum.CARD != pmType);
//	}

	@Listen(Events.ON_CHECK + "=#chbInAdv")
	public void chbInAdv_checked() {
		itbInAdv.setDisabled(!chbInAdv.isChecked());
	}


	void copyCnsp(Consumption _cnsp) {
		defaultTypeFlag = false;

		cbbBehavior.setSelectedIndex(0);
		cbbTypeCategory.setValue(_cnsp.getType().getCategory().getTitle());

		cbbType.setValue(_cnsp.getType().getName());

		rgDirection.setSelectedIndex(_cnsp.getDirection().getIdx() - 1);
		txbDescription.setValue(_cnsp.getDescription());
		itbConsumptionAmount.setValue(_cnsp.getAmount());
		cbbPaymentType.setValue(_cnsp.getPaymentType().getName());
		chbInAdv.setChecked(false);
		itbInAdv.setDisabled(true);
		itbInAdv.setValue(null);
		dtbConsumptionDate.setValue(new Date(System.currentTimeMillis()));
	}

	@Listen(Events.ON_CLICK + "=#btnConfirmAdd")
	public void wdAddConsumption_btnConfirmAdd_clicked() {
		TimeTraveler tt = new TimeTraveler();

		Set<Consumption> set = new HashSet<>();
		boolean b1 = false;
		StringBuilder msg = new StringBuilder();
		switch (cbbBehavior.getSelectedIndex()) {
		case 0: // 一般消費
			b1 = behavior0(set, msg, tt);
			break;
		case 1: // LINE POINTS折抵
			b1 = behaviorDiscount(set, msg, tt, TypeEnum.LIFE_OTHER, "LINE POINTS");
			break;
		case 2: // 悠遊卡折抵
			b1 = behaviorDiscount(set, msg, tt, TypeEnum.TRAFFIC_OTHER, "悠遊卡");
			break;
		case 3: // 一卡通折抵
			b1 = behaviorDiscount(set, msg, tt, TypeEnum.TRAFFIC_OTHER, "一卡通");
			break;
		case 4: // P幣折抵
			b1 = behaviorDiscount(set, msg, tt, TypeEnum.LIFE_OTHER, "P幣");
			break;
		case 5: // 全聯儲值金折抵
			b1 = behaviorDiscount(set, msg, tt, TypeEnum.LIFE_OTHER, "全聯儲值金");
			break;
		case 6: // 麥當勞儲值金折抵
			b1 = behaviorDiscount(set, msg, tt, TypeEnum.FOOD, "麥當勞點點卡");
			break;
		}
		if (b1) {

		} else {
			HmsMessageBox.error(msg.toString());
			return;
		}


		/* InAdv */
		TypeEnum type = cbbType.getSelectedItem().getValue();
		String description = txbDescription.getValue();
		LocalDate cnspDate = DateFormatUtil.parseLocalDate(dtbConsumptionDate.getValue());
		boolean inAdv = chbInAdv.isChecked();
		Integer inAdvAmt = itbInAdv.getValue();
		if (inAdv && inAdvAmt!=null && inAdvAmt > 0) {

			CnspBuilder1 inAdvCnspBuilder = BpuFacade.getInstance().getBuilder(AcntBpuType.CNSP_1);
			if (inAdvCnspBuilder == null) {
				tt.travel();
				HmsNotification.error();
				return;
			}
			inAdvCnspBuilder.appendType(type).appendDirection(DirectionEnum.IN_ADV).appendAmount(inAdvAmt)
			.appendDescription("代付-"+description).appendPaymentType(PaymentTypeEnum.CASH).appendDate(cnspDate);

			StringBuilder inAdvMsg = new StringBuilder();
			Consumption cnspInAdv = inAdvCnspBuilder.build(inAdvMsg, tt);


			String msgInAdv = "新增代付流入[" + type.getCategory().getTitle() + "][" + type.getName() + "][" + DirectionEnum.IN_ADV.getName()
			+ "][" + "代付-"+description + "][" + NumberFormatUtil.getIntegerString(inAdvAmt) + "]["
			+ PaymentTypeEnum.CASH.getName() + "][" + cnspDate.toString() + "]";
			if(cnspInAdv!=null) {
				msgInAdv+="成功。";
				HmsMessageBox.info(msg.toString() + System.lineSeparator() + msgInAdv);
				set.add(cnspInAdv);
			}else {
				msgInAdv+="失敗。";
				HmsMessageBox.error(msg.toString() + System.lineSeparator() + msgInAdv+ System.lineSeparator() +inAdvMsg.toString());
			}
		}
		//
		else {
			HmsMessageBox.info(msg.toString());
		}


		csmAfterAddingCnsp.accept(set);
	}

	/** 行為0：一般消費 */
	private boolean behavior0(Set<Consumption> _set, StringBuilder _msg, TimeTraveler _tt) {
		TypeEnum type = cbbType.getSelectedItem().getValue();
		DirectionEnum direction = rgDirection.getSelectedItem().getValue();

		String description = txbDescription.getValue();
		int cnspAmount = itbConsumptionAmount.getValue();
		PaymentTypeEnum paymentType = cbbPaymentType.getSelectedItem().getValue();
		LocalDate cnspDate = DateFormatUtil.parseLocalDate(dtbConsumptionDate.getValue());

		/* createNewConsumption */
		CnspBuilder1 bpu = BpuFacade.getInstance().getBuilder(AcntBpuType.CNSP_1);
		if (bpu == null) {
			HmsNotification.error();
			log.error("bpu null.");
			return false;
		}

		//
		bpu.appendType(type).appendDirection(direction).appendDescription(description).appendAmount(cnspAmount)
				.appendPaymentType(paymentType).appendDate(cnspDate);

		//
		if (!bpu.verify(_msg))
			return false;

		//
		TimeTraveler tt = new TimeTraveler();
		Consumption cnsp = bpu.build(_msg, tt);

		String msg = "新增一般消費[" + type.getCategory().getTitle() + "][" + type.getName() + "][" + direction.getName()
				+ "][" + description + "][" + NumberFormatUtil.getIntegerString(cnspAmount) + "]["
				+ paymentType.getName() + "][" + cnspDate.toString() + "]";

		//
		if (_tt != null)
			_tt.copySitesFrom(tt);

		//
		if (cnsp != null) {
			_msg.append(msg + "成功。");
			_set.add(cnsp);
			return true;
		} else {
			_msg.append(msg + "失敗!");
			log.error("bpu.build return null.");
			return false;
		}
	}

	private boolean behaviorDiscount(Set<Consumption> _set, StringBuilder _msg, TimeTraveler _tt,
			TypeEnum _discountType, String _discountMediaName) {
		CnspBpuCashDiscount bpu = BpuFacade.getInstance().getBuilder(AcntBpuType.CNSP_CASH_DISCOUNT);
		if (bpu == null) {
			HmsNotification.error();
			log.error("bpu null.");
			return false;
		}

		bpu.appendDiscountType(_discountType).appendDiscountMediaName(_discountMediaName);

		TypeEnum type = cbbType.getSelectedItem().getValue();
		String description = txbDescription.getValue();
		int cnspAmount = itbConsumptionAmount.getValue();
		LocalDate cnspDate = DateFormatUtil.parseLocalDate(dtbConsumptionDate.getValue());

		bpu.appendCnspType(type).appendDescription(description).appendAmount(cnspAmount).appendDate(cnspDate);

		String msg = "新增"+_discountMediaName+"折抵[" + type.getCategory().getTitle() + "][" + type.getName() + "][" + description
				+ "][" + NumberFormatUtil.getIntegerString(cnspAmount) + "][" + cnspDate.toString() + "]";

		if (bpu.build(_msg, _tt)) {
			_msg.append(msg + "成功。");
			_set.add(bpu.getCnsp());
			_set.add(bpu.getDiscount());
			return true;
		} else {
			_msg.append(msg + "失敗!");
			return false;
		}
	}

	@Listen(Events.ON_CLICK + "=#btnResetBlanks")
	public void btnResetBlanks_clicked() {
		resetBlanks();
	}

	@Listen(Events.ON_CLOSE + "=#wdAddCnsp")
	public void wdAddConsumption_closed(Event _evt) {
		_evt.stopPropagation();
		wdAddCnsp.setVisible(false);
	}
}
