package hms.web.control.zk.account;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.A;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.East;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Include;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

import hms.account.bpu.AcntBpuType;
import hms.account.bpu.CnspBpuDel;
import hms.util.ZKUtil;
import hms.web.zk.HmsMessageBox;
import hms.web.zk.HmsNotification;
import hms_kernel.account.AccountService;
import hms_kernel.account.Consumption;
import hms_kernel.account.ConsumptionSearchParam;
import hms_kernel.account.DirectionEnum;
import hms_kernel.account.Payment;
import hms_kernel.account.PaymentTypeEnum;
import hms_kernel.account.TypeCategoryEnum;
import hms_kernel.account.TypeEnum;
import legion.BusinessServiceFactory;
import legion.biz.BpuFacade;
import legion.util.DataFO;
import legion.util.DateFormatUtil;
import legion.util.DateUtil;
import legion.util.NumberFormatUtil;
import legion.web.zk.ZkUtil;

/**
 * 消費管理(新版)。與舊版 {@link ConsumptionMainComposer} 並存。
 * <ul>
 * <li>全部明細：依期間/說明/進階條件查詢，列內可直接編輯。</li>
 * <li>待付款：列出所有未付清消費，可依付款方式、結帳日篩選；高密度列表方便大量勾選後一次付清。</li>
 * <li>新增消費改為右側側欄，不鎖定畫面，可一邊查詢一邊複製、連續輸入。</li>
 * </ul>
 */
public class CnspManageComposer extends SelectorComposer<Component> {
	private Logger log = LoggerFactory.getLogger(getClass());

	private enum Mode {
		ALL, PAYABLE
	}

	// -------------------------------------------------------------------------------
	@Wire
	private Vlayout vlMain;
	@Wire
	private Button btnModeAll, btnModePayable, btnToggleFilter;
	@Wire
	private Hlayout hlPeriod;
	@Wire
	private Datebox dtbStart, dtbEnd;
	@Wire
	private Textbox txbKeyword;
	@Wire
	private Label lbFilterHint;

	@Wire
	private Div divFilter;
	@Wire
	private Combobox cbbTypeCategory, cbbType, cbbDirection, cbbPaymentType;
	@Wire
	private Datebox dtbPayDateStart, dtbPayDateEnd;

	@Wire
	private Div divPayableBar;
	@Wire
	private Hlayout hlPayTypeToggles;
	@Wire
	private Datebox dtbPayableEnd;

	@Wire
	private Label lbKpiOut, lbKpiIn, lbKpiNetTitle, lbKpiNet, lbKpiPayed, lbKpiPayable;

	@Wire
	private Listbox lbxCnsp;

	@Wire
	private Label lbSelCount, lbSelPayable, lbSelAmount, lbBillDiff;
	@Wire
	private Intbox itbBillAmount;

	@Wire
	private East eastAdd;
	@Wire
	private Include icdAddPanel;
	private CnspManageAddPanelComposer addPanelComposer;

	@Wire
	private Menupopup mpCnsp;

	// -------------------------------------------------------------------------------
	private AccountService accountService = BusinessServiceFactory.getInstance().getService(AccountService.class);

	private Mode mode = Mode.ALL;
	/** 查詢(或待付款)回來的原始資料；畫面顯示的是套用待付款篩選後的結果。 */
	private List<Consumption> baseList = new ArrayList<>();
	private ListModelList<Consumption> model;

	/** 待付款模式的付款方式篩選，null 表示全部。 */
	private PaymentTypeEnum payableTypeFilter;

	/** 右鍵選單作用中的消費。 */
	private Consumption targetCnsp;

	// -------------------------------------------------------------------------------
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		addPanelComposer = CnspManageAddPanelComposer.of(icdAddPanel);
		addPanelComposer.init(this::afterAdding);

		ZKUtil.configureConsumptionTypeCategory(cbbTypeCategory, true);
		ZkUtil.initCbb(cbbDirection, DirectionEnum.values(false), true);
		ZkUtil.initCbb(cbbPaymentType, PaymentTypeEnum.values(false), true);

		lbxCnsp.setItemRenderer(cnspRenderer);
		lbxPayment.setItemRenderer((ListitemRenderer<Payment>) (li, pm, i) -> {
			li.appendChild(new Listcell());
			li.appendChild(new Listcell(pm.getDate().toString()));
			li.appendChild(new Listcell(NumberFormatUtil.getIntegerString(pm.getAmount())));
		});

		// 預設：本月
		btnThisMonth_clicked();
	}

	// ===============================================================================
	// 模式切換
	// ===============================================================================
	@Listen(Events.ON_CLICK + "=#btnModeAll")
	public void btnModeAll_clicked() {
		if (mode == Mode.ALL)
			return;
		switchMode(Mode.ALL);
		search();
	}

	@Listen(Events.ON_CLICK + "=#btnModePayable")
	public void btnModePayable_clicked() {
		if (mode == Mode.PAYABLE)
			return;
		switchMode(Mode.PAYABLE);
		loadPayable();
	}

	private void switchMode(Mode _mode) {
		mode = _mode;
		boolean all = mode == Mode.ALL;
		toggleSclass(btnModeAll, "cm-on", all);
		toggleSclass(btnModePayable, "cm-on", !all);
		hlPeriod.setVisible(all);
		btnToggleFilter.setVisible(all);
		lbFilterHint.setVisible(all);
		if (!all)
			divFilter.setVisible(false);
		divPayableBar.setVisible(!all);
		lbKpiNetTitle.setValue(all ? "小計(淨額)" : "未付消費金額(淨額)");
		itbBillAmount.setValue(null);
		Clients.resize(vlMain);
	}

	// ===============================================================================
	// 全部明細：查詢
	// ===============================================================================
	@Listen(Events.ON_CLICK + "=#btnThisMonth")
	public void btnThisMonth_clicked() {
		LocalDate today = DateUtil.toLocalDate(System.currentTimeMillis());
		setPeriod(today.withDayOfMonth(1), today.withDayOfMonth(today.lengthOfMonth()));
	}

	@Listen(Events.ON_CLICK + "=#btnLastMonth")
	public void btnLastMonth_clicked() {
		LocalDate d = DateUtil.toLocalDate(System.currentTimeMillis()).minusMonths(1);
		setPeriod(d.withDayOfMonth(1), d.withDayOfMonth(d.lengthOfMonth()));
	}

	@Listen(Events.ON_CLICK + "=#btnThisYear")
	public void btnThisYear_clicked() {
		LocalDate today = DateUtil.toLocalDate(System.currentTimeMillis());
		setPeriod(today.withDayOfYear(1), today.withDayOfYear(today.lengthOfYear()));
	}

	private void setPeriod(LocalDate _start, LocalDate _end) {
		dtbStart.setValue(DateUtil.toDate(_start));
		dtbEnd.setValue(DateUtil.toDate(_end));
		if (mode != Mode.ALL)
			switchMode(Mode.ALL);
		search();
	}

	@Listen(Events.ON_CLICK + "=#btnSearch; " + Events.ON_OK + "=#txbKeyword")
	public void btnSearch_clicked() {
		if (mode == Mode.ALL)
			search();
		else
			applyView(); // 待付款模式：說明關鍵字在畫面上直接篩選
	}

	@Listen(Events.ON_CLICK + "=#btnToggleFilter")
	public void btnToggleFilter_clicked() {
		divFilter.setVisible(!divFilter.isVisible());
		Clients.resize(vlMain);
	}

	@Listen(Events.ON_SELECT + "=#cbbTypeCategory")
	public void cbbTypeCategory_selected() {
		cbbType.getChildren().clear();
		cbbType.setValue(null);
		if (cbbTypeCategory.getSelectedItem() != null && cbbTypeCategory.getSelectedItem().getValue() != null) {
			TypeCategoryEnum cate = cbbTypeCategory.getSelectedItem().getValue();
			ZkUtil.initCbb(cbbType, accountService.getTypes(cate, false), true);
		}
	}

	@Listen(Events.ON_CLICK + "=#btnResetFilter")
	public void btnResetFilter_clicked() {
		cbbTypeCategory.setSelectedItem(null);
		cbbTypeCategory.setValue(null);
		cbbType.getChildren().clear();
		cbbType.setValue(null);
		cbbDirection.setSelectedItem(null);
		cbbDirection.setValue(null);
		cbbPaymentType.setSelectedItem(null);
		cbbPaymentType.setValue(null);
		dtbPayDateStart.setValue(null);
		dtbPayDateEnd.setValue(null);
		search();
	}

	private void search() {
		ConsumptionSearchParam param = new ConsumptionSearchParam();
		List<String> hints = new ArrayList<>();

		// 類型
		Object cate = selectedValue(cbbTypeCategory);
		if (cate != null) {
			List<TypeEnum> typeList = new ArrayList<>();
			Object type = selectedValue(cbbType);
			if (type != null) {
				typeList.add((TypeEnum) type);
				hints.add(((TypeEnum) type).getName());
			} else {
				for (TypeEnum t : accountService.getTypes((TypeCategoryEnum) cate, false))
					typeList.add(t);
				hints.add(((TypeCategoryEnum) cate).getTitle());
			}
			param.setTypeList(typeList);
		}
		// 流向
		Object dir = selectedValue(cbbDirection);
		if (dir != null) {
			param.setDirection((DirectionEnum) dir);
			hints.add(((DirectionEnum) dir).getName());
		}
		// 付款方式
		Object pt = selectedValue(cbbPaymentType);
		if (pt != null) {
			List<PaymentTypeEnum> ptList = new ArrayList<>();
			ptList.add((PaymentTypeEnum) pt);
			param.setPaymentTypeList(ptList);
			hints.add(((PaymentTypeEnum) pt).getName());
		}
		// 說明(模糊查詢)
		String kw = txbKeyword.getValue();
		param.setDescription(DataFO.isEmptyString(kw) ? null : "%" + kw.trim() + "%");
		// 消費日期
		param.setConsumptionDateStart(DateFormatUtil.parseLocalDate(dtbStart.getValue()));
		param.setConsumptionDateEnd(DateFormatUtil.parseLocalDate(dtbEnd.getValue()));
		// 付款日期
		param.setPayDateStart(DateFormatUtil.parseLocalDate(dtbPayDateStart.getValue()));
		param.setPayDateEnd(DateFormatUtil.parseLocalDate(dtbPayDateEnd.getValue()));
		if (dtbPayDateStart.getValue() != null || dtbPayDateEnd.getValue() != null)
			hints.add("付款日期");

		lbFilterHint.setValue(hints.isEmpty() ? "" : "篩選中：" + String.join("、", hints));

		baseList = new ArrayList<>(accountService.searchConsumptions(param, true));
		sortByDate(baseList);
		applyView();
	}

	// ===============================================================================
	// 待付款
	// ===============================================================================
	private void loadPayable() {
		baseList = new ArrayList<>(accountService.getPayableConsumptions());
		sortByDate(baseList);
		refreshPayTypeToggles();
		applyView();
	}

	@Listen(Events.ON_CLICK + "=#btnPayableReload")
	public void btnPayableReload_clicked() {
		loadPayable();
	}

	@Listen(Events.ON_CHANGE + "=#dtbPayableEnd")
	public void dtbPayableEnd_changed() {
		applyView();
	}

	/** 付款方式快速篩選按鈕：全部 + 有未付消費的付款方式(附筆數)，單選。 */
	private void refreshPayTypeToggles() {
		hlPayTypeToggles.getChildren().clear();
		Map<PaymentTypeEnum, Integer> counts = new LinkedHashMap<>();
		for (PaymentTypeEnum pt : PaymentTypeEnum.values(false))
			counts.put(pt, 0);
		for (Consumption c : baseList)
			counts.merge(c.getPaymentType(), 1, Integer::sum);

		if (payableTypeFilter != null && counts.getOrDefault(payableTypeFilter, 0) == 0)
			payableTypeFilter = null;

		hlPayTypeToggles.appendChild(newPayTypeToggle(null, "全部 " + baseList.size()));
		counts.forEach((pt, n) -> {
			if (n > 0)
				hlPayTypeToggles.appendChild(newPayTypeToggle(pt, pt.getName() + " " + n));
		});
		btnModePayable.setLabel("待付款 " + baseList.size());
	}

	private Button newPayTypeToggle(PaymentTypeEnum _pt, String _label) {
		Button btn = new Button(_label);
		btn.setSclass("cm-btn" + (payableTypeFilter == _pt ? " cm-on" : ""));
		btn.addEventListener(Events.ON_CLICK, e -> {
			payableTypeFilter = _pt;
			for (Component c : hlPayTypeToggles.getChildren())
				toggleSclass((Button) c, "cm-on", c == btn);
			applyView();
		});
		return btn;
	}

	// ===============================================================================
	// 列表、KPI、選取列
	// ===============================================================================
	/** 依目前模式的畫面篩選條件，從 baseList 產生列表。 */
	private void applyView() {
		List<Consumption> view = baseList;
		if (mode == Mode.PAYABLE) {
			LocalDate end = DateFormatUtil.parseLocalDate(dtbPayableEnd.getValue());
			String kw = txbKeyword.getValue() == null ? "" : txbKeyword.getValue().trim();
			view = baseList.stream() //
					.filter(c -> payableTypeFilter == null || c.getPaymentType() == payableTypeFilter)
					.filter(c -> end == null || !c.getDate().isAfter(end))
					.filter(c -> kw.isEmpty() || (c.getDescription() != null && c.getDescription().contains(kw)))
					.collect(Collectors.toList());
		}
		model = new ListModelList<>(view);
		model.setMultiple(true);
		lbxCnsp.setModel(model);
		lbxCnsp.setSclass(mode == Mode.PAYABLE ? "cm-list cm-pick" : "cm-list");
		refreshKpi();
		refreshSelectionBar();
	}

	/** 資料內容變了(新增/付款/刪除)但不需重新查詢時，重繪列表並保留勾選。 */
	private void redrawKeepSelection() {
		Set<Consumption> sel = model == null ? new HashSet<>() : new HashSet<>(model.getSelection());
		applyView();
		sel.retainAll(model);
		model.setSelection(sel);
		refreshSelectionBar();
	}

	private void refreshKpi() {
		int out = 0, in = 0, payed = 0, payable = 0;
		for (Consumption c : model) {
			int sign = sign(c);
			if (sign > 0)
				out += c.getAmount();
			else
				in += c.getAmount();
			payed += sign * c.getPayedAmount();
			payable += sign * c.getPayableAmount();
		}
		lbKpiOut.setValue(fmt(out));
		lbKpiIn.setValue(fmt(in));
		lbKpiNet.setValue(fmt(out - in) + "  (" + model.size() + " 筆)");
		lbKpiPayed.setValue(fmt(payed));
		lbKpiPayable.setValue(fmt(payable));
	}

	@Listen(Events.ON_SELECT + "=#lbxCnsp")
	public void lbxCnsp_selected() {
		refreshSelectionBar();
	}

	@Listen(Events.ON_CHANGE + "=#itbBillAmount; " + Events.ON_OK + "=#itbBillAmount")
	public void itbBillAmount_changed() {
		refreshSelectionBar();
	}

	private void refreshSelectionBar() {
		Set<Consumption> sel = model == null ? new HashSet<>() : model.getSelection();
		int payable = sel.stream().mapToInt(c -> sign(c) * c.getPayableAmount()).sum();
		int amount = sel.stream().mapToInt(c -> sign(c) * c.getAmount()).sum();
		lbSelCount.setValue(String.valueOf(sel.size()));
		lbSelPayable.setValue(fmt(payable));
		lbSelAmount.setValue(fmt(amount));

		Integer bill = itbBillAmount.getValue();
		if (bill == null) {
			lbBillDiff.setVisible(false);
		} else {
			int diff = payable - bill;
			lbBillDiff.setVisible(true);
			if (diff == 0) {
				lbBillDiff.setValue("相符");
				lbBillDiff.setSclass("cm-pill cm-pill-ok");
			} else {
				lbBillDiff.setValue((diff > 0 ? "多選 " : "還差 ") + fmt(Math.abs(diff)));
				lbBillDiff.setSclass("cm-pill cm-pill-no");
			}
		}
	}

	@Listen(Events.ON_CLICK + "=#btnSelectAll")
	public void btnSelectAll_clicked() {
		model.setSelection(new ArrayList<>(model));
		refreshSelectionBar();
	}

	@Listen(Events.ON_CLICK + "=#btnSelectInvert")
	public void btnSelectInvert_clicked() {
		Set<Consumption> cur = new HashSet<>(model.getSelection());
		List<Consumption> inverted = model.stream().filter(c -> !cur.contains(c)).collect(Collectors.toList());
		model.setSelection(inverted);
		refreshSelectionBar();
	}

	@Listen(Events.ON_CLICK + "=#btnSelectClear")
	public void btnSelectClear_clicked() {
		model.clearSelection();
		refreshSelectionBar();
	}

	// -------------------------------------------------------------------------------
	private final ListitemRenderer<Consumption> cnspRenderer = (Listitem li, Consumption cnsp, int index) -> {
		boolean editable = mode == Mode.ALL;
		int sign = sign(cnsp);

		li.appendChild(new Listcell()); // checkmark

		// 消費日期
		if (editable) {
			Datebox dtb = new Datebox(DateUtil.toDate(cnsp.getDate()));
			dtb.setFormat("yyyy-MM-dd");
			dtb.setInplace(true);
			dtb.setHflex("1");
			dtb.addEventListener(Events.ON_CHANGE, e -> {
				if (dtb.getValue() == null) {
					HmsNotification.error("日期錯誤。");
					dtb.setValue(DateUtil.toDate(cnsp.getDate()));
					return;
				}
				LocalDate old = cnsp.getDate();
				cnsp.setDate(DateFormatUtil.parseLocalDate(dtb.getValue()));
				if (accountService.updateCnsp(cnsp))
					HmsNotification.info("更新消費日期成功。");
				else {
					cnsp.setDate(old);
					dtb.setValue(DateUtil.toDate(old));
					HmsNotification.error("更新消費日期失敗。");
				}
			});
			li.appendChild(cellOf(dtb));
		} else
			li.appendChild(new Listcell(cnsp.getDate().toString()));

		// 目錄
		Listcell lcCate = new Listcell(cnsp.getType().getCategory().getTitle());
		li.appendChild(lcCate);

		// 類型
		if (editable) {
			Combobox cbb = new Combobox(cnsp.getType().getName());
			cbb.setInplace(true);
			cbb.setHflex("1");
			lazyItems(cbb, () -> ZKUtil.configureConsumptionType(cbb, true));
			cbb.addEventListener(Events.ON_CHANGE, e -> {
				if (cbb.getSelectedItem() == null || cbb.getSelectedItem().getValue() == null) {
					HmsNotification.warning("請選取選單中的類型。");
					cbb.setValue(cnsp.getType().getName());
					return;
				}
				TypeEnum old = cnsp.getType();
				cnsp.setType(cbb.getSelectedItem().getValue());
				if (accountService.updateCnsp(cnsp)) {
					HmsNotification.info("更新類型成功。");
					lcCate.setLabel(cnsp.getType().getCategory().getTitle());
				} else {
					cnsp.setType(old);
					cbb.setValue(old.getName());
					HmsNotification.error("更新類型失敗。");
				}
			});
			li.appendChild(cellOf(cbb));
		} else
			li.appendChild(new Listcell(cnsp.getType().getName()));

		// 流向
		li.appendChild(new Listcell(cnsp.getDirection().getName()));

		// 金額
		if (editable) {
			Intbox itb = new Intbox(cnsp.getAmount());
			itb.setFormat("#,##0");
			itb.setInplace(true);
			itb.setHflex("1");
			itb.setStyle("text-align:right");
			itb.addEventListener(Events.ON_CHANGE, e -> {
				if (itb.getValue() == null) {
					HmsNotification.error("未填消費金額，更新失敗。");
					itb.setValue(cnsp.getAmount());
					return;
				}
				int old = cnsp.getAmount();
				cnsp.setAmount(itb.getValue());
				if (accountService.updateCnsp(cnsp)) {
					HmsNotification.info("更新消費金額成功。");
					rowChanged(cnsp);
				} else {
					cnsp.setAmount(old);
					itb.setValue(old);
					HmsNotification.error("更新消費金額失敗。");
				}
			});
			li.appendChild(cellOf(itb));
		} else
			li.appendChild(numCell(cnsp.getAmount(), sign));

		// 已付
		if (editable) {
			Intbox itb = new Intbox(cnsp.getPayedAmount());
			itb.setFormat("#,##0");
			itb.setInplace(true);
			itb.setHflex("1");
			itb.setStyle("text-align:right");
			itb.addEventListener(Events.ON_CHANGE, e -> {
				List<Payment> pmList = cnsp.getPaymentList();
				if (pmList == null || pmList.size() != 1) {
					HmsNotification.error("只有一筆付款時才能直接修改已付金額，請用右鍵「付款資訊」調整。");
					itb.setValue(cnsp.getPayedAmount());
					return;
				}
				if (itb.getValue() == null) {
					HmsNotification.error("未填付款金額，更新失敗。");
					itb.setValue(cnsp.getPayedAmount());
					return;
				}
				Payment pm = pmList.get(0);
				int old = pm.getAmount();
				pm.setAmount(itb.getValue());
				if (accountService.updatePayment(pm)) {
					HmsNotification.info("更新付款金額成功。");
					rowChanged(cnsp);
				} else {
					pm.setAmount(old);
					itb.setValue(cnsp.getPayedAmount());
					HmsNotification.error("更新付款金額失敗。");
				}
			});
			li.appendChild(cellOf(itb));
		} else
			li.appendChild(numCell(cnsp.getPayedAmount(), sign));

		// 應付
		li.appendChild(numCell(cnsp.getPayableAmount(), sign));

		// 付款方式
		if (editable) {
			Combobox cbb = new Combobox(cnsp.getPaymentType().getName());
			cbb.setInplace(true);
			cbb.setReadonly(true);
			cbb.setHflex("1");
			lazyItems(cbb, () -> ZkUtil.initCbb(cbb, PaymentTypeEnum.values(false), false));
			cbb.addEventListener(Events.ON_SELECT, e -> {
				if (cbb.getSelectedItem() == null)
					return;
				PaymentTypeEnum newType = cbb.getSelectedItem().getValue();
				PaymentTypeEnum old = cnsp.getPaymentType();
				if (newType == old)
					return;
				cnsp.setPaymentType(newType);
				if (accountService.updateCnsp(cnsp))
					HmsNotification.info("更新付款方式成功。");
				else {
					cnsp.setPaymentType(old);
					cbb.setValue(old.getName());
					HmsNotification.error("更新付款方式失敗。");
				}
			});
			li.appendChild(cellOf(cbb));
		} else
			li.appendChild(new Listcell(cnsp.getPaymentType().getName()));

		// 說明
		if (editable) {
			Textbox txb = new Textbox(cnsp.getDescription());
			txb.setInplace(true);
			txb.setHflex("1");
			txb.addEventListener(Events.ON_CHANGE, e -> {
				String old = cnsp.getDescription();
				cnsp.setDescription(txb.getValue());
				if (accountService.updateCnsp(cnsp))
					HmsNotification.info("更新說明成功。");
				else {
					cnsp.setDescription(old);
					txb.setValue(old);
					HmsNotification.error("更新說明失敗。");
				}
			});
			li.appendChild(cellOf(txb));
		} else {
			Listcell lc = new Listcell(cnsp.getDescription());
			lc.setTooltiptext(cnsp.getDescription());
			li.appendChild(lc);
		}

		// 狀態
		li.appendChild(cellOf(statusPill(cnsp)));

		// 複製
		A aCopy = new A();
		aCopy.setIconSclass("z-icon-copy");
		aCopy.setSclass("cm-copy");
		aCopy.setTooltiptext("複製到新增消費");
		// 點複製不會勾選該列：點擊目標是 <a> 時 ZK 不做列選取(見 css .cm-copy i)
		aCopy.addEventListener(Events.ON_CLICK, e -> openAddPanel(cnsp));
		li.appendChild(cellOf(aCopy));

		// 右鍵選單
		li.setValue(cnsp);
		li.addEventListener(Events.ON_RIGHT_CLICK, e -> {
			targetCnsp = cnsp;
			mpCnsp.open(li, "at_pointer");
		});
	};

	private Label statusPill(Consumption _cnsp) {
		Label lb = new Label();
		int payable = _cnsp.getPayableAmount();
		int payed = _cnsp.getPayedAmount();
		if (payable == 0) {
			lb.setValue("已付清");
			lb.setSclass("cm-pill cm-pill-ok");
		} else if (payed == 0) {
			lb.setValue("未付");
			lb.setSclass("cm-pill cm-pill-no");
		} else {
			int n = _cnsp.getPaymentList() == null ? 0 : _cnsp.getPaymentList().size();
			lb.setValue("已付" + n + "筆");
			lb.setSclass("cm-pill cm-pill-part");
			lb.setTooltiptext("已付 " + fmt(payed) + "，尚欠 " + fmt(payable));
		}
		return lb;
	}

	// ===============================================================================
	// 新增消費側欄
	// ===============================================================================
	@Listen(Events.ON_CLICK + "=#btnAdd")
	public void btnAdd_clicked() {
		eastAdd.setOpen(true);
		addPanelComposer.focusFirst();
	}

	private void openAddPanel(Consumption _cnsp) {
		eastAdd.setOpen(true);
		addPanelComposer.copyCnsp(_cnsp);
	}

	private void afterAdding(Set<Consumption> _set) {
		for (Consumption c : _set) {
			if (mode == Mode.PAYABLE && c.getPayableAmount() == 0)
				continue;
			baseList.add(c);
		}
		sortByDate(baseList);
		if (mode == Mode.PAYABLE)
			refreshPayTypeToggles();
		redrawKeepSelection();
	}

	// ===============================================================================
	// 批次：付清所選、刪除
	// ===============================================================================
	@Wire
	private Window windowPayAll;
	@Wire("#windowPayAll #lbPaSummary")
	private Label lbPaSummary;
	@Wire("#windowPayAll #dtbPaPayDate")
	private Datebox dtbPaPayDate;

	@Listen(Events.ON_CLICK + "=#btnPaySelected")
	public void btnPaySelected_clicked() {
		List<Consumption> targets = model.getSelection().stream().filter(c -> c.getPayableAmount() != 0)
				.collect(Collectors.toList());
		if (targets.isEmpty()) {
			HmsMessageBox.exclamation("請先勾選有應付餘額的消費。");
			return;
		}
		int sum = targets.stream().mapToInt(c -> sign(c) * c.getPayableAmount()).sum();
		lbPaSummary.setValue("付清 " + targets.size() + " 筆，應付合計 " + fmt(sum));
		dtbPaPayDate.setValue(new Date(System.currentTimeMillis()));
		windowPayAll.setVisible(true);
	}

	@Listen(Events.ON_CLICK + "=#windowPayAll #btnPaSubmit")
	public void windowPayAll_submit() {
		if (dtbPaPayDate.getValue() == null) {
			HmsNotification.warning("請填付款日期。");
			return;
		}
		LocalDate payDate = DateFormatUtil.parseLocalDate(dtbPaPayDate.getValue());
		List<Consumption> targets = model.getSelection().stream().filter(c -> c.getPayableAmount() != 0)
				.sorted(Comparator.comparing(Consumption::getDate)).collect(Collectors.toList());

		StringBuilder fail = new StringBuilder();
		int okCount = 0;
		for (Consumption c : targets) {
			Payment pm = accountService.createPayment(c, payDate, c.getPayableAmount());
			if (pm != null) {
				okCount++;
				c.clearPaymentList();
			} else
				fail.append(System.lineSeparator()).append("消費").append(c.getInfo()).append("付款失敗！");
		}
		windowPayAll.setVisible(false);

		if (mode == Mode.PAYABLE) {
			baseList.removeIf(c -> c.getPayableAmount() == 0);
			refreshPayTypeToggles();
			applyView();
		} else {
			for (Consumption c : targets)
				model.notifyChange(c);
			model.clearSelection();
			refreshKpi();
			refreshSelectionBar();
		}

		if (fail.length() == 0)
			HmsNotification.info("已付清 " + okCount + " 筆消費。");
		else
			HmsMessageBox.error("成功 " + okCount + " 筆，失敗 " + (targets.size() - okCount) + " 筆：" + fail);
	}

	@Listen(Events.ON_CLICK + "=#windowPayAll #btnPaCancel")
	public void windowPayAll_cancel() {
		windowPayAll.setVisible(false);
	}

	@Listen(Events.ON_CLICK + "=#btnDeleteSelected")
	public void btnDeleteSelected_clicked() {
		Set<Consumption> sel = new HashSet<>(model.getSelection());
		if (sel.isEmpty()) {
			HmsMessageBox.exclamation("請勾選要刪除的消費。");
			return;
		}
		HmsMessageBox.confirm("確定刪除所選的 " + sel.size() + " 筆消費?", () -> {
			StringBuilder msg = new StringBuilder();
			Set<Consumption> deleted = new HashSet<>();
			for (Consumption c : sel) {
				CnspBpuDel bpu = BpuFacade.getInstance().getBuilder(AcntBpuType.CNSP$DEL, c);
				boolean ok = bpu != null && bpu.build(new StringBuilder(), null);
				if (msg.length() > 0)
					msg.append("\n");
				msg.append("刪除消費").append(c.getInfo()).append(ok ? "成功。" : "失敗!");
				if (ok)
					deleted.add(c);
			}
			baseList.removeAll(deleted);
			if (mode == Mode.PAYABLE)
				refreshPayTypeToggles();
			model.removeAll(deleted);
			refreshKpi();
			refreshSelectionBar();
			if (deleted.size() == sel.size())
				HmsNotification.info("已刪除 " + deleted.size() + " 筆消費。");
			else
				HmsMessageBox.error(msg.toString());
		});
	}

	// ===============================================================================
	// 右鍵選單
	// ===============================================================================
	@Listen(Events.ON_CLICK + "=#miShowPaymentInfo")
	public void miShowPaymentInfo_clicked() {
		refreshPaymentInfo();
		windowPaymentInfo.setVisible(true);
	}

	@Listen(Events.ON_CLICK + "=#miAddPayment")
	public void miAddPayment_clicked() {
		showAddPayment();
	}

	@Listen(Events.ON_CLICK + "=#miCopyCnsp")
	public void miCopyCnsp_clicked() {
		openAddPanel(targetCnsp);
	}

	// -------------------------------------------------------------------------------
	// 付款資訊
	@Wire
	private Window windowPaymentInfo;
	@Wire("#windowPaymentInfo #lbPiCnspInfo")
	private Label lbPiCnspInfo;
	@Wire("#windowPaymentInfo #lbPiCnspAmount")
	private Label lbPiCnspAmount;
	@Wire("#windowPaymentInfo #lbPiPayedAmount")
	private Label lbPiPayedAmount;
	@Wire("#windowPaymentInfo #lbPiPayableAmount")
	private Label lbPiPayableAmount;
	@Wire("#windowPaymentInfo #lbxPayment")
	private Listbox lbxPayment;

	private void refreshPaymentInfo() {
		Consumption c = targetCnsp;
		lbPiCnspInfo.setValue(c.getDate() + " " + c.getType().getName() + " " + nvl(c.getDescription()));
		lbPiCnspAmount.setValue(fmt(c.getAmount()));
		lbPiPayedAmount.setValue(fmt(c.getPayedAmount()));
		lbPiPayableAmount.setValue(fmt(c.getPayableAmount()));
		ListModelList<Payment> pmModel = new ListModelList<>(c.getPaymentList());
		pmModel.setMultiple(true);
		lbxPayment.setModel(pmModel);
	}

	@Listen(Events.ON_CLICK + "=#windowPaymentInfo #btnPiAddPayment")
	public void windowPaymentInfo_addPayment() {
		showAddPayment();
	}

	@Listen(Events.ON_CLICK + "=#windowPaymentInfo #btnPiDeletePayment")
	public void windowPaymentInfo_deletePayment() {
		@SuppressWarnings("unchecked")
		Set<Payment> sel = new HashSet<>(((ListModelList<Payment>) (Object) lbxPayment.getModel()).getSelection());
		if (sel.isEmpty()) {
			HmsMessageBox.exclamation("請勾選要刪除的付款。");
			return;
		}
		HmsMessageBox.confirm("確定刪除所選的 " + sel.size() + " 筆付款?", () -> {
			StringBuilder msg = new StringBuilder();
			boolean allOk = true;
			for (Payment p : sel) {
				boolean ok = p.delete();
				allOk &= ok;
				msg.append("刪除付款[").append(p.getDate()).append("][").append(fmt(p.getAmount())).append("]")
						.append(ok ? "成功。" : "失敗!").append("\n");
			}
			targetCnsp.clearPaymentList();
			refreshPaymentInfo();
			afterPaymentChanged(targetCnsp);
			if (allOk)
				HmsNotification.info("已刪除 " + sel.size() + " 筆付款。");
			else
				HmsMessageBox.error(msg.toString());
		});
	}

	// -------------------------------------------------------------------------------
	// 新增付款
	@Wire
	private Window windowAddPayment;
	@Wire("#windowAddPayment #dtbApPayDate")
	private Datebox dtbApPayDate;
	@Wire("#windowAddPayment #itbApPayAmount")
	private Intbox itbApPayAmount;

	private void showAddPayment() {
		windowAddPayment_reset();
		windowAddPayment.setVisible(true);
		itbApPayAmount.focus();
	}

	@Listen(Events.ON_CLICK + "=#windowAddPayment #btnApReset")
	public void windowAddPayment_reset() {
		dtbApPayDate.setValue(new Date(System.currentTimeMillis()));
		// 有上一筆付款時帶上次金額(分期)，否則帶剩餘應付
		Payment last = targetCnsp.getLastPayment();
		itbApPayAmount.setValue(last != null ? last.getAmount() : targetCnsp.getPayableAmount());
	}

	@Listen(Events.ON_CLICK + "=#windowAddPayment #btnApSubmit; " + Events.ON_OK + "=#windowAddPayment #itbApPayAmount")
	public void windowAddPayment_submit() {
		if (dtbApPayDate.getValue() == null || itbApPayAmount.getValue() == null) {
			HmsNotification.warning("請填付款日期和金額。");
			return;
		}
		Payment pm = accountService.createPayment(targetCnsp, DateFormatUtil.parseLocalDate(dtbApPayDate.getValue()),
				itbApPayAmount.getValue());
		if (pm == null) {
			HmsMessageBox.error("新增付款失敗。");
			return;
		}
		HmsNotification.info("新增付款成功。");
		targetCnsp.clearPaymentList();
		windowAddPayment.setVisible(false);
		if (windowPaymentInfo.isVisible())
			refreshPaymentInfo();
		afterPaymentChanged(targetCnsp);
	}

	/** 某筆消費的付款變動後，更新列表(待付款模式下已付清的會移除)。 */
	private void afterPaymentChanged(Consumption _cnsp) {
		if (mode == Mode.PAYABLE && _cnsp.getPayableAmount() == 0) {
			baseList.remove(_cnsp);
			model.remove(_cnsp);
			refreshPayTypeToggles();
			refreshKpi();
			refreshSelectionBar();
		} else
			rowChanged(_cnsp);
	}

	/** 只重繪該列，並更新 KPI 與選取列(不會跳回第一頁)。 */
	private void rowChanged(Consumption _cnsp) {
		model.notifyChange(_cnsp);
		refreshKpi();
		refreshSelectionBar();
	}

	// -------------------------------------------------------------------------------
	// 沖銷折抵
	@Wire
	private Window windowOffset;
	@Wire("#windowOffset #txbOffsetDescription")
	private Textbox txbOffsetDescription;
	@Wire("#windowOffset #dtbOffsetPayDate")
	private Datebox dtbOffsetPayDate;

	@Listen(Events.ON_CLICK + "=#miOffsetConsumption")
	public void miOffsetConsumption_clicked() {
		if (targetCnsp.getPayableAmount() <= 0) {
			HmsMessageBox.exclamation("此筆消費已付清，無法進行沖銷折抵。");
			return;
		}
		txbOffsetDescription.setValue("以美金餘額沖銷折抵");
		dtbOffsetPayDate.setValue(new Date(System.currentTimeMillis()));
		windowOffset.setVisible(true);
	}

	@Listen(Events.ON_CLICK + "=#windowOffset #btnOffsetSubmit")
	public void windowOffset_submit() {
		try {
			Consumption offsetCnsp = accountService.offsetConsumption(targetCnsp, txbOffsetDescription.getValue(),
					DateFormatUtil.parseLocalDate(dtbOffsetPayDate.getValue()));
			if (offsetCnsp == null) {
				HmsMessageBox.error("沖銷失敗。");
				return;
			}
			HmsNotification.info("沖銷折抵處理成功。");
			windowOffset.setVisible(false);
			targetCnsp.clearPaymentList();
			if (mode == Mode.ALL) { // 全部明細才顯示反向沖銷紀錄
				baseList.add(offsetCnsp);
				model.add(offsetCnsp);
			}
			afterPaymentChanged(targetCnsp);
		} catch (Exception e) {
			log.error("offsetConsumption error.", e);
			HmsMessageBox.error("沖銷失敗: " + e.getMessage());
		}
	}

	@Listen(Events.ON_CLICK + "=#windowOffset #btnOffsetCancel")
	public void windowOffset_cancel() {
		windowOffset.setVisible(false);
	}

	// -------------------------------------------------------------------------------
	/** 子視窗按 X 只隱藏，不要被 detach。 */
	@Listen(Events.ON_CLOSE + "=#windowPaymentInfo, #windowAddPayment, #windowPayAll, #windowOffset")
	public void window_closed(Event _evt) {
		_evt.stopPropagation();
		_evt.getTarget().setVisible(false);
	}

	// ===============================================================================
	// utils
	// ===============================================================================
	/** 出帳為正，入帳、入帳-代墊款為負(與舊版小計一致)。 */
	private static int sign(Consumption _c) {
		return DirectionEnum.OUT == _c.getDirection() ? 1 : -1;
	}

	private static void sortByDate(List<Consumption> _list) {
		_list.sort(Comparator.comparing(Consumption::getDate).thenComparing(Consumption::getObjectCreateTime));
	}

	private static String fmt(int _n) {
		return NumberFormatUtil.getIntegerString(_n);
	}

	private static String nvl(String _s) {
		return _s == null ? "" : _s;
	}

	private static Listcell cellOf(Component _c) {
		Listcell lc = new Listcell();
		lc.appendChild(_c);
		return lc;
	}

	private static Listcell numCell(int _n, int _sign) {
		Listcell lc = new Listcell(fmt(_n));
		lc.setSclass(_sign < 0 ? "cm-num cm-in" : "cm-num");
		return lc;
	}

	private static Object selectedValue(Combobox _cbb) {
		Comboitem ci = _cbb.getSelectedItem();
		return ci == null ? null : ci.getValue();
	}

	/** 列內下拉選單在第一次展開/聚焦時才建立選項，避免一頁 100 筆時元件過多。 */
	private static void lazyItems(Combobox _cbb, Runnable _init) {
		String text = _cbb.getValue();
		org.zkoss.zk.ui.event.EventListener<Event> el = e -> {
			if (_cbb.getItemCount() == 0) {
				_init.run();
				for (Comboitem ci : _cbb.getItems())
					if (ci.getLabel().equals(text))
						_cbb.setSelectedItem(ci);
			}
		};
		_cbb.addEventListener(Events.ON_FOCUS, el);
		_cbb.addEventListener(Events.ON_OPEN, el);
	}

	private static void toggleSclass(Button _btn, String _cls, boolean _on) {
		String s = _btn.getSclass() == null ? "" : _btn.getSclass().replace(" " + _cls, "").replace(_cls, "").trim();
		_btn.setSclass(_on ? s + " " + _cls : s);
	}
}
