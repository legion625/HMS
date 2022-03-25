package hms.web.control.zk.account;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.event.KeyEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Include;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listfooter;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import bsh.org.objectweb.asm.Type;
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
import legion.util.DataFO;
import legion.util.DateFormatUtil;
import legion.util.NumberFormatUtil;

public class ConsumptionMainComposer extends SelectorComposer<Component> {
	// -------------------------------------------------------------------------------
	@Wire
	private Include inclAddConsumptionWindow;
	private WindowAddConsumptionComposer windowAddConsumptionComposer;

	@Wire
	private Combobox cbbTypeCategory;
	@Wire
	private Combobox cbbType;
	@Wire
	private Combobox cbbDirection;
	@Wire
	private Combobox cbbPaymentType;
	@Wire
	private Textbox txbDescription;
	@Wire
	private Datebox dtbConsumptionDateStart;
	@Wire
	private Datebox dtbConsumptionDateEnd;
	@Wire
	private Datebox dtbPayDateStart;
	@Wire
	private Datebox dtbPayDateEnd;

	@Wire
	private Listbox lbxConsumption;
	@Wire
	private Listfooter lftrSumCnspAmt, lftrSumPayedAmt;

	@Wire
	private Menupopup mpConsumption;
	@Wire
	private Menuitem miShowPaymentInfo;

	// -------------------------------------------------------------------------------
	private AccountService accountService = AccountService.getInstance();

	private List<Consumption> cnspList;

	// -------------------------------------------------------------------------------
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		System.out.println(this.getClass().getSimpleName() + ".doAfterCompose");
		// ---------------------------------------------------
		init();
	}

	// -------------------------------------------------------------------------------
	private void init() {
		Consumer<Set<Consumption>> csmAfterAddingCnsp = set -> {
			if (cnspList == null)
				cnspList = new ArrayList<>();
			cnspList.addAll(set);
			refreshConsumptionContainer(cnspList);
		};
		inclAddConsumptionWindow.setDynamicProperty(WindowAddConsumptionComposer.DYNAMIC_PROPERTY_CSM_AFTER_ADDING_CNSP,
				csmAfterAddingCnsp);
		inclAddConsumptionWindow.setSrc(WindowAddConsumptionComposer.SRC);
		windowAddConsumptionComposer = (WindowAddConsumptionComposer) inclAddConsumptionWindow.getFellow("main")
				.getAttribute("$composer");

		/* init cbbTypeCategory items */
		ZKUtil.configureConsumptionTypeCategory(cbbTypeCategory);

		/* init cbbType */
		cbbType.setItemRenderer((Comboitem cbi, TypeEnum type, int index) -> {
			cbi.setLabel(type.getTitle());
			cbi.setValue(type);
		});

		/* init cbbDirection */
		ZKUtil.configureDirection(cbbDirection);

		/* init cbbPaymentType items */
		ZKUtil.configurePaymentType(cbbPaymentType);

		/* init lbxConsumption */
		lbxConsumption.setItemRenderer(consumptionListitemRenderer);

		/* init windowPaymentInfo */
		lbxPaymentInfoPayment.setItemRenderer(paymentListitemRenderer);
	}

	// -------------------------------------------------------------------------------
	// -------------------------------------north-------------------------------------
	@Listen(Events.ON_CLICK + "=#btnAddConsumption")
	public void btnAddConsumption_clicked() {
		windowAddConsumptionComposer.showWindow();
	}

	// -------------------------------------------------------------------------------
	// -------------------------------------west--------------------------------------
	@Listen(Events.ON_SELECT + "=#cbbTypeCategory")
	public void cbbTypeCategory_selected() {
		cbbType.setValue("");

		ListModelList<TypeEnum> model = new ListModelList<>();
		if (cbbTypeCategory.getSelectedItem() != null) {
			TypeCategoryEnum typeCate = cbbTypeCategory.getSelectedItem().getValue();
			for (TypeEnum type : accountService.getTypes(typeCate, false))
				model.add(type);
		}
		cbbType.setModel(model);
	}

	@Listen(Events.ON_CLICK + "=#btnSearch")
	public void btnSearch_clicked() {
		ConsumptionSearchParam param = new ConsumptionSearchParam();

		// type
		if (cbbTypeCategory.getSelectedItem() != null) {

			TypeCategoryEnum typeCate = cbbTypeCategory.getSelectedItem().getValue();
			List<TypeEnum> typeList = new ArrayList<>();
			if (cbbType.getSelectedItem() != null)
				typeList.add(cbbType.getSelectedItem().getValue());
			else
				for (TypeEnum type : accountService.getTypes(typeCate, false))
					typeList.add(type);
			param.setTypeList(typeList);
		}

		// Direction
		if (cbbDirection.getSelectedItem() != null) {
			param.setDirection(cbbDirection.getSelectedItem().getValue());
		}

		// PaymentType
		if (cbbPaymentType.getSelectedItem() != null) {
			List<PaymentTypeEnum> paymentTypeList = new ArrayList<>();
			paymentTypeList.add(cbbPaymentType.getSelectedItem().getValue());
			param.setPaymentTypeList(paymentTypeList);
		}

		// description
		String desp = txbDescription.getValue();
		if (!DataFO.isEmptyString(desp))
			desp = "%" + desp + "%";
		param.setDescription(desp);

		// consumptionDate
		param.setConsumptionDateStart(DateFormatUtil.parseLocalDate(dtbConsumptionDateStart.getValue()));
		param.setConsumptionDateEnd(DateFormatUtil.parseLocalDate(dtbConsumptionDateEnd.getValue()));

		// payDate
		param.setPayDateStart(DateFormatUtil.parseLocalDate(dtbPayDateStart.getValue()));
		param.setPayDateEnd(DateFormatUtil.parseLocalDate(dtbPayDateEnd.getValue()));

		cnspList = accountService.searchConsumptions(param, true);
		refreshConsumptionContainer(cnspList);
	}

	@Listen(Events.ON_CLICK + "=#btnReset")
	public void btnReset_clicked() {
		cbbTypeCategory.setValue(null);
		cbbType.setValue(null);
		cbbDirection.setValue(null);
		cbbPaymentType.setValue(null);
		txbDescription.setValue(null);
		dtbConsumptionDateStart.setValue(null);
		dtbConsumptionDateEnd.setValue(null);
		dtbPayDateStart.setValue(null);
		dtbPayDateEnd.setValue(null);
	}

	@Listen(Events.ON_CLICK + "=#btnGetPayableConsumptions")
	public void btnGetPayableConsumptions_clicked() {
		cnspList = accountService.getPayableConsumptions();
		refreshConsumptionContainer(cnspList);
	}

	// -------------------------------------------------------------------------------
	// -------------------------------------------------------------------------------
	@Listen(Events.ON_CLICK + "=#btnDeleteConsumption")
	public void btnDeleteConsumption_clicked() {
		ListModelList<Consumption> model = (ListModelList) lbxConsumption.getModel();
		Set<Consumption> tobeDeletedConsumptionSet = model.getSelection();
		if (tobeDeletedConsumptionSet == null || tobeDeletedConsumptionSet.isEmpty()) {
			HmsMessageBox.exclamation("請選取欲刪除的消費。");
			return;
		}

		Consumer<Void> csmDelete = v -> {
			String msg = "";

			Set<Consumption> tempSet = new HashSet<>();

			for (Consumption cnsp : tobeDeletedConsumptionSet) {
				if (!DataFO.isEmptyString(msg))
					msg += "\n";
				msg += "刪除消費[" + cnsp.getDate().toString() + "][" + cnsp.getType().getCategory().getTitle() + "]["
						+ cnsp.getType().getTitle() + "][" + cnsp.getDirection().getTitle() + "]["
						+ NumberFormatUtil.getIntegerString(cnsp.getAmount()) + "][" + cnsp.getDescription() + "]["
						+ cnsp.getPaymentType().getTitle() + "]";

				if (accountService.deleteConsumption(cnsp)) {
					msg += "成功。";
					tempSet.add(cnsp);
				} else {
					msg += "失敗!";
				}
			}

			for (Consumption cnsp : tempSet)
				model.remove(cnsp);
			HmsMessageBox.info(msg);

		};

		HmsMessageBox.confirm("確定刪除所有選取的消費?", csmDelete);
	}

	@Listen(Events.ON_CLICK + "=#btnPayAll")
	public void btnPayAll_clicked() {
		HmsMessageBox.exclamation("請謹慎使用此功能。", v -> {
			windowPayAll_btnReset_clicked();
			windowPayAll.setVisible(true);
		});
	}

	private ListitemRenderer<Consumption> consumptionListitemRenderer = (Listitem listitem, Consumption cnsp,
			int index) -> {
		Listcell lc;

		// checkbox
		listitem.appendChild(new Listcell());
		// 消費日期
		Datebox dtbDate = new Datebox();
		dtbDate.setValue(DateFormatUtil.parse(cnsp.getDate().toString()));
		dtbDate.setHflex("1");
		dtbDate.setFormat("yyyy-MM-dd");
		dtbDate.setInplace(true);
		lc = new Listcell();
		lc.appendChild(dtbDate);
		listitem.appendChild(lc);
		EventListener<Event> updateDateEl =  evt -> {
			Date date = dtbDate.getValue();
			if (date == null) {
				HmsNotification.error("日期錯誤。");
				dtbDate.setValue(DateFormatUtil.parse(cnsp.getDate().toString()));
				return;
			}
			cnsp.setDate(DateFormatUtil.parseLocalDate(date));
			boolean r = AccountService.getInstance().updateCnsp(cnsp);
			if (r)
				HmsNotification.info("更新消費日期成功。");
			else {
				HmsNotification.error("更新消費日期失敗。");
				dtbDate.setValue(DateFormatUtil.parse(cnsp.getDate().toString()));
			}
		};
		dtbDate.addEventListener(Events.ON_CHANGE,updateDateEl);
		// 類型目錄
		listitem.appendChild(new Listcell(cnsp.getType().getCategory().getTitle()));
		// 類型
		listitem.appendChild(new Listcell(cnsp.getType().getTitle()));
		// 流向
		listitem.appendChild(new Listcell(cnsp.getDirection().getTitle()));
		// 消費金額
		Intbox itbAmount = new Intbox(cnsp.getAmount());
		itbAmount.setHflex("1");
		itbAmount.setFormat("#,##0");
		itbAmount.setStyle("text-align:right");
		itbAmount.setInplace(true);
		lc = new Listcell();
		lc.appendChild(itbAmount);
		listitem.appendChild(lc);
		itbAmount.addEventListener(Events.ON_CHANGE,evt->{
			cnsp.setAmount(itbAmount.getValue());
			boolean r = AccountService.getInstance().updateCnsp(cnsp);
			if (r)
				HmsNotification.info("更新消費金額成功。");
			else {
				HmsNotification.error("更新消費金額失敗。");
				itbAmount.setValue(cnsp.getAmount());
			}
		});
		// 付款金額
		Intbox itbPayedAmount = new Intbox(cnsp.getPayedAmount());
		itbPayedAmount.setHflex("1");
		itbPayedAmount.setFormat("#,##0");
		itbPayedAmount.setStyle("text-align:right");
		itbPayedAmount.setInplace(true);
		lc = new Listcell();
		lc.appendChild(itbPayedAmount);
		listitem.appendChild(lc);
		itbPayedAmount.addEventListener(Events.ON_CHANGE,evt->{
			List<Payment> pmList = cnsp.getPaymentList();
			if (pmList == null || pmList.size()!=1) {
				HmsNotification.error("更新付款金額失敗。");
				itbPayedAmount.setValue(cnsp.getPayedAmount());
				return;
			}
			Payment pm = pmList.get(0);
			boolean r = AccountService.getInstance().updatePayment(pm);
			if (r)
				HmsNotification.info("更新付款金額成功。");
			else {
				HmsNotification.error("更新付款金額失敗。");
				itbPayedAmount.setValue(cnsp.getPayedAmount());
			}
		});
		// 付款方式
		listitem.appendChild(new Listcell(cnsp.getPaymentType().getTitle()));
		// 說明
		listitem.appendChild(new Listcell(cnsp.getDescription()));

		// 右鍵選單
		listitem.setValue(cnsp);
		listitem.addEventListener(Events.ON_RIGHT_CLICK, e -> {
			miShowPaymentInfo.setAttribute("selectedCnsp", cnsp);
			mpConsumption.open(listitem);
		});
	};

	private void refreshConsumptionContainer(List<Consumption> _cnspList) {
		ListModelList<Consumption> model = new ListModelList<>(_cnspList);
		model.setMultiple(true);
		lbxConsumption.setModel(model);

		lftrSumCnspAmt.setLabel(NumberFormatUtil.getIntegerString(_cnspList.parallelStream()
				.mapToInt(cnsp -> DirectionEnum.OUT == cnsp.getDirection() ? cnsp.getAmount() : -cnsp.getAmount())
				.sum()));
		lftrSumPayedAmt.setLabel(NumberFormatUtil.getIntegerString(_cnspList.parallelStream().mapToInt(
				cnsp -> DirectionEnum.OUT == cnsp.getDirection() ? cnsp.getPayedAmount() : -cnsp.getPayedAmount())
				.sum()));
	}

	@Listen(Events.ON_CLICK + "=#miShowPaymentInfo")
	public void miShowPaymentInfo_clicked(Event _evt) {
		Consumption cnsp = (Consumption) miShowPaymentInfo.getAttribute("selectedCnsp");
		refreshPaymentInfo(cnsp);
		windowPaymentInfo.setVisible(true);
	}

	@Listen(Events.ON_CLICK + "=#miAddPayment")
	public void miAddPayment_clicked() {
		windowAddPayment_btnReset_clicked();
		windowAddPayment.setVisible(true);
	}

	private Consumption getTargetConsumption() {
		return (Consumption) miShowPaymentInfo.getAttribute("selectedCnsp");
	}

	// -------------------------------------------------------------------------------
	@Wire
	private Window windowPaymentInfo;
	@Wire("#windowPaymentInfo #lbCnspAmount")
	private Label lbPaymentInfoCnspAmount;
	@Wire("#windowPaymentInfo #lbPayedAmount")
	private Label lbPaymentInfoPayedAmount;
	@Wire("#windowPaymentInfo #lbPayableAmount")
	private Label lbPaymentInfoPayableAmount;
	@Wire("#windowPaymentInfo #lbxPayment")
	private Listbox lbxPaymentInfoPayment;

	@Listen(Events.ON_CLICK + "=#windowPaymentInfo #btnAddPayment")
	public void windowPaymentInfo_btnAddPayment_clicked() {
		windowAddPayment_btnReset_clicked();
		windowAddPayment.setVisible(true);
	}

	@Listen(Events.ON_CLICK + "=#windowPaymentInfo #btnDeletePayment")
	public void windowPaymentInfo_btnDeletePayment_clicked() {
		ListModelList<Payment> model = (ListModelList) lbxPaymentInfoPayment.getModel();
		Set<Payment> selectedPaymentSet = model.getSelection();
		if (selectedPaymentSet == null || selectedPaymentSet.isEmpty()) {
			HmsMessageBox.exclamation("請選取欲刪除的付款項目。");
			return;
		}

		HmsMessageBox.confirm("確定刪除被選取的付款項目?", v -> {
			StringBuffer strBuffer = new StringBuffer();
			boolean result = true;
			for (Payment p : selectedPaymentSet) {
				String str = "刪除付款[" + p.getDate().toString() + "][" + NumberFormatUtil.getIntegerString(p.getAmount())
						+ "]";
				boolean temp = accountService.deletePayment(getTargetConsumption(), p);
				str += temp ? "成功" : "失敗";
				if (!temp)
					result = false;
				strBuffer.append(str);
			}

			refreshPaymentInfo(getTargetConsumption());

			if (result) {
				HmsMessageBox.info(strBuffer.toString());
				refreshConsumptionContainer(cnspList);
			} else
				HmsMessageBox.error(strBuffer.toString());
		});
	}

	private void refreshPaymentInfo(Consumption _cnsp) {
		lbPaymentInfoCnspAmount.setValue(NumberFormatUtil.getIntegerString(_cnsp.getAmount()));
		lbPaymentInfoPayedAmount.setValue(NumberFormatUtil.getIntegerString(_cnsp.getPayedAmount()));
		lbPaymentInfoPayableAmount.setValue(NumberFormatUtil.getIntegerString(_cnsp.getPayableAmount()));

		ListModelList<Payment> model = _cnsp == null ? new ListModelList<>()
				: new ListModelList<>(_cnsp.getPaymentList());
		lbxPaymentInfoPayment.setModel(model);
		model.setMultiple(true);
	}

	private ListitemRenderer<Payment> paymentListitemRenderer = (Listitem listitem, Payment payment, int index) -> {
		// checkbox
		listitem.appendChild(new Listcell());
		listitem.appendChild(new Listcell(payment.getDate().toString()));
		listitem.appendChild(new Listcell(NumberFormatUtil.getIntegerString(payment.getAmount())));
	};

	@Listen(Events.ON_CLOSE + "=#windowPaymentInfo")
	public void windowPaymentInfo_closed(Event _evt) {
		_evt.stopPropagation();
		windowPaymentInfo.setVisible(false);
	}

	// -------------------------------------------------------------------------------
	@Wire("#windowAddPayment")
	private Window windowAddPayment;
	@Wire("#windowAddPayment #dtbPayDate")
	private Datebox dtbAddPaymentPayDate;
	@Wire("#windowAddPayment #itbPayAmount")
	private Intbox itbAddPaymentPayAmount;

	@Listen(Events.ON_CLICK + "=#windowAddPayment #btnSubmit")
	public void windowAddPayment_btnSubmit_clicked() {
		Consumption cnsp = (Consumption) miShowPaymentInfo.getAttribute("selectedCnsp");
		boolean result = accountService.addPayment(cnsp, DateFormatUtil.parseLocalDate(dtbAddPaymentPayDate.getValue()),
				itbAddPaymentPayAmount.getValue());
		if (result) {
			HmsMessageBox.info("新增付款成功。");
			refreshPaymentInfo(cnsp);
			windowAddPayment_closed(new Event("evt"));
		} else
			HmsMessageBox.error("新增付款失敗。");
	}

	@Listen(Events.ON_CLICK + "=#windowAddPayment #btnReset")
	public void windowAddPayment_btnReset_clicked() {
		Consumption cnsp = (Consumption) miShowPaymentInfo.getAttribute("selectedCnsp");

		dtbAddPaymentPayDate.setValue(new Date(System.currentTimeMillis()));
		itbAddPaymentPayAmount.setValue(cnsp.getPayableAmount());
	}

	@Listen(Events.ON_CLOSE + "=#windowAddPayment")
	public void windowAddPayment_closed(Event _evt) {
		_evt.stopPropagation();
		windowAddPayment.setVisible(false);
	}

	// -------------------------------------------------------------------------------
	@Wire("#windowPayAll")
	private Window windowPayAll;
	@Wire("#windowPayAll #dtbPayDate")
	private Datebox dtbPayAllPayDate;

	@Listen(Events.ON_CLICK + "=#windowPayAll #btnSubmit")
	public void windowPayAll_btnSubmit_clicked() {
		ListModelList<Consumption> model = (ListModelList) lbxConsumption.getModel();

		String msg = "";
		boolean success = true;
		for (Consumption cnsp : model.getSelection()) {
			boolean result = accountService.addPayment(cnsp, DateFormatUtil.parseLocalDate(dtbPayAllPayDate.getValue()),
					cnsp.getPayableAmount());
			if (!DataFO.isEmptyString(msg))
				msg += "\n";
			msg += "消費" + cnsp.getInfo() + "付款" + (result ? "成功。" : "失敗！");
			success = success && result;
		}

		if (success) {
			HmsMessageBox.info(msg);
			refreshConsumptionContainer(cnspList);
			windowPayAll_closed(new Event("evt"));
		} else
			HmsMessageBox.error(msg);
	}

	@Listen(Events.ON_CLICK + "=#windowPayAll #btnReset")
	public void windowPayAll_btnReset_clicked() {
		dtbPayAllPayDate.setValue(new Date(System.currentTimeMillis()));
	}

	@Listen(Events.ON_CLOSE + "=#windowPayAll")
	public void windowPayAll_closed(Event _evt) {
		_evt.stopPropagation();
		windowPayAll.setVisible(false);
	}

	// -------------------------------------------------------------------------------

}
