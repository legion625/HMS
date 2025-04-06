package hms.web.control.zk.account;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.zkoss.lang.Threads;
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
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import hms.util.ZKUtil;
import hms.web.zk.HmsMessageBox;
import hms_kernel.account.AccountService;
import hms_kernel.account.AccountServiceImp;
import hms_kernel.account.Consumption;
import hms_kernel.account.DirectionEnum;
import hms_kernel.account.PaymentTypeEnum;
import hms_kernel.account.TypeCategoryEnum;
import hms_kernel.account.TypeEnum;
import legion.BusinessServiceFactory;
import legion.util.DataFO;
import legion.util.DateFormatUtil;
import legion.util.NumberFormatUtil;

public class WindowAddConsumptionComposer extends SelectorComposer<Component> {
	// -------------------------------------------------------------------------------
	public final static String SRC = "/account/windowAddConsumption.zul";

	final static String DYNAMIC_PROPERTY_CSM_AFTER_ADDING_CNSP = "DYNAMIC_PROPERTY_CSM_AFTER_ADDING_CNSP";
	// -------------------------------------------------------------------------------
	@WireVariable("requestScope")
	private Map<String, Object> requestScope;

	@Wire
	private Window main;
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
	private Consumer<Set<Consumption>> csmAfterAddingCnsp;

	// -------------------------------------------------------------------------------
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		csmAfterAddingCnsp = (Consumer<Set<Consumption>>) requestScope.get(DYNAMIC_PROPERTY_CSM_AFTER_ADDING_CNSP);
		if (csmAfterAddingCnsp == null)
			csmAfterAddingCnsp = set -> {
			};

		init();
	}

	// -------------------------------------------------------------------------------
	private void init() {
		/* init cbbTypeCategory items */
//		ZKUtil.configureConsumptionTypeCategory(cbbTypeCategory, cbbType);
		ZKUtil.configureConsumptionTypeCategory(cbbTypeCategory);
		cbbTypeCategory.addEventListener(Events.ON_SELECT, cnspCateSyncEl);
		
		/* init rgDirection radios */
		rgDirection.getChildren().clear();
		for (DirectionEnum d : DirectionEnum.values(false)) {
			Radio rd = new Radio(d.getTitle());
			rd.setValue(d);
			rgDirection.appendChild(rd);
		}

		/* init cbbPaymentType items */
		ZKUtil.configurePaymentType(cbbPaymentType);

		resetBlanks();
	}
	
	private boolean defaultTypeFlag = true; // 當cbbType隨cbbTypeCate變動時，是否要帶入預設值

	private EventListener<Event> cnspCateSyncEl =  evt -> {
		cbbType.getChildren().clear();

		TypeCategoryEnum _cate = cbbTypeCategory.getSelectedItem().getValue();
		
//		for (TypeEnum type : AccountServiceImp.getInstance().getTypes(_cate, true)) { // 只載入enabled的類型
		for (TypeEnum type : BusinessServiceFactory.getInstance().getService(AccountService.class).getTypes(_cate, true)) { // 只載入enabled的類型
			Comboitem cbi = new Comboitem(type.getTitle());
			cbi.setValue(type);
			cbbType.appendChild(cbi);
		}

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
		main.setVisible(true);
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
//		cbbTypeCategory.removeEventListener(Events.ON_SELECT, cnspCateSyncEl);
		cbbTypeCategory.setValue(_cnsp.getType().getCategory().getTitle());
		
//		Runnable runSetCnspType = () ->{
//			cbbType.setValue(_cnsp.getType().getTitle());
//			
//		} ;
		cbbType.setValue(_cnsp.getType().getTitle());
		System.out.println("cbbType.getValue(): "+cbbType.getValue());	
		
//		Events.postEvent(Events.ON_SELECT, cbbTypeCategory, null);
//		rgDirection.setSelectedIndex(0);
		rgDirection.setSelectedIndex(_cnsp.getDirection().getDbIndex() - 1);
		txbDescription.setValue(_cnsp.getDescription());
		itbConsumptionAmount.setValue(_cnsp.getAmount());
//		cbbPaymentType.setSelectedIndex(0);
		cbbPaymentType.setValue(_cnsp.getPaymentType().getTitle());
		chbInAdv.setChecked(false);
		itbInAdv.setDisabled(true);
		itbInAdv.setValue(null);
		dtbConsumptionDate.setValue(new Date(System.currentTimeMillis()));
		
//		cbbBehavior_selected();
	}
	
	@Listen(Events.ON_CLICK + "=#btnConfirmAdd")
	public void wdAddConsumption_btnConfirmAdd_clicked() {
		/* verification */
		if (DataFO.isEmptyString(txbDescription.getValue()) || itbConsumptionAmount.getValue() == null) {
			HmsMessageBox.exclamation("資料不全。");
			return;
		}
		
		Set<Consumption> set = new HashSet<>();
		boolean b1 = false;
		StringBuilder msg = new StringBuilder();
		switch (cbbBehavior.getSelectedIndex()) {
		case 0: // 一般消費
			b1 = behavior0(set, msg);
			break;
		case 1: // LINE POINTS折抵
			b1 = behavior1(set, msg);
			break;
		case 2: // 悠遊卡折抵
			b1 = behavior2(set, msg);
			break;
		case 3: // 一卡通折抵
//			behavior3(set);
			b1 = behavior3(set, msg);
			break;
		case 4: // P幣折抵
//			behavior4(set);
			b1 = behavior4(set, msg);
			break;
		case 5: // 全聯儲值金折抵
//			behavior5(set)
			b1 = behavior5(set, msg);
			break;
		case 6: // 麥當勞儲值金折抵
//			behavior6(set);
			b1 = behavior6(set, msg);
			break;
		}
		if (b1) {
//			HmsMessageBox.info(msg.toString());
			;
		} else {
			HmsMessageBox.error(msg.toString());
			return;
		}
		
		
		/* InAdv */
		TypeEnum type = cbbType.getSelectedItem().getValue();
		String description = txbDescription.getValue();
//		int cnspAmount = itbConsumptionAmount.getValue();
		LocalDate cnspDate = DateFormatUtil.parseLocalDate(dtbConsumptionDate.getValue());
//		if (PaymentTypeEnum.CARD == paymentType) {
		boolean inAdv = chbInAdv.isChecked();
		Integer inAdvAmt = itbInAdv.getValue();
		if (inAdv && inAdvAmt!=null && inAdvAmt > 0) {
			Consumption cnspInAdv = BusinessServiceFactory.getInstance().getService(AccountService.class).createNewConsumption(type, DirectionEnum.IN_ADV, inAdvAmt, "代付-"+description,
					PaymentTypeEnum.CASH, cnspDate);
			String msgInAdv = "新增代付流入[" + type.getCategory().getTitle() + "][" + type.getTitle() + "][" + DirectionEnum.IN_ADV.getTitle()
			+ "][" + "代付-"+description + "][" + NumberFormatUtil.getIntegerString(inAdvAmt) + "]["
			+ PaymentTypeEnum.CASH.getTitle() + "][" + cnspDate.toString() + "]";
			if(cnspInAdv!=null) {
				msgInAdv+="成功。";
				HmsMessageBox.info(msg.toString() + System.lineSeparator() + msgInAdv);
				set.add(cnspInAdv);
			}else {
				msgInAdv+="失敗。";
				HmsMessageBox.error(msg.toString() + System.lineSeparator() + msgInAdv);
			}
		}
		// 
		else {
			HmsMessageBox.info(msg.toString());
		}
//		}
		
		
		csmAfterAddingCnsp.accept(set);
	}

	/** 行為0：一般消費 */
	private boolean behavior0(Set<Consumption> _set, StringBuilder _msg) {
		TypeEnum type = cbbType.getSelectedItem().getValue();
//		DirectionEnum direction = rgDirection.getSelectedIndex() == 1 ? DirectionEnum.IN : DirectionEnum.OUT;
		DirectionEnum direction = rgDirection.getSelectedItem().getValue();
		
		String description = txbDescription.getValue();
		int cnspAmount = itbConsumptionAmount.getValue();
		PaymentTypeEnum paymentType = cbbPaymentType.getSelectedItem().getValue();
		LocalDate cnspDate = DateFormatUtil.parseLocalDate(dtbConsumptionDate.getValue());
		

		/* createNewConsumption */
		Consumption cnsp = BusinessServiceFactory.getInstance().getService(AccountService.class).createNewConsumption(type, direction, cnspAmount, description,
				paymentType, cnspDate);
		String msg = "新增一般消費[" + type.getCategory().getTitle() + "][" + type.getTitle() + "][" + direction.getTitle()
				+ "][" + description + "][" + NumberFormatUtil.getIntegerString(cnspAmount) + "]["
				+ paymentType.getTitle() + "][" + cnspDate.toString() + "]";
		if (cnsp != null) {
//			HmsMessageBox.info(msg + "成功。");
			_msg.append(msg + "成功。");
			_set.add(cnsp);
			return true;
		} else {
//			HmsMessageBox.error(msg + "失敗!");
			_msg.append(msg + "失敗!");
			return false;
		}
		
		

	}

	/** 行為1:LINE POINTS折抵 */
	private boolean behavior1(Set<Consumption> _set, StringBuilder _msg) {
		TypeEnum type = cbbType.getSelectedItem().getValue();
		String description = txbDescription.getValue();
		int cnspAmount = itbConsumptionAmount.getValue();
		LocalDate cnspDate = DateFormatUtil.parseLocalDate(dtbConsumptionDate.getValue());

		/* 消費 */
		Consumption cnsp = BusinessServiceFactory.getInstance().getService(AccountService.class).createNewConsumption(type, DirectionEnum.OUT, cnspAmount,
				description, PaymentTypeEnum.CASH, cnspDate);
		/* 折抵 */
		Consumption discount = BusinessServiceFactory.getInstance().getService(AccountService.class).createNewConsumption(TypeEnum.LIFE_OTHER, DirectionEnum.IN,
				cnspAmount, "LINE POINTS消費", PaymentTypeEnum.CASH, cnspDate);

		String msg = "新增LINE POINTS折抵[" + type.getCategory().getTitle() + "][" + type.getTitle() + "][" + description
				+ "][" + NumberFormatUtil.getIntegerString(cnspAmount) + "][" + cnspDate.toString() + "]";
		if (cnsp != null && discount != null) {
//			HmsMessageBox.info(msg + "成功。");
			_msg.append(msg + "成功。");
			_set.add(cnsp);
			_set.add(discount);
			return true;
		} else {
//			HmsMessageBox.error(msg + "失敗!");
			_msg.append(msg + "失敗!");
			return false;
		}
			
	}

	/** 行為2:悠遊卡折抵 */
	private boolean behavior2(Set<Consumption> _set, StringBuilder _msg) {
		TypeEnum type = cbbType.getSelectedItem().getValue();
		String description = txbDescription.getValue();
		int cnspAmount = itbConsumptionAmount.getValue();
		LocalDate cnspDate = DateFormatUtil.parseLocalDate(dtbConsumptionDate.getValue());

		/* 消費 */
		Consumption cnsp = BusinessServiceFactory.getInstance().getService(AccountService.class).createNewConsumption(type, DirectionEnum.OUT, cnspAmount,
				description, PaymentTypeEnum.CASH, cnspDate);
		/* 折抵 */
		Consumption discount = BusinessServiceFactory.getInstance().getService(AccountService.class).createNewConsumption(TypeEnum.TRAFFIC_OTHER,
				DirectionEnum.IN, cnspAmount, "悠遊卡消費", PaymentTypeEnum.CASH, cnspDate);

		String msg = "新增悠遊卡折抵[" + type.getCategory().getTitle() + "][" + type.getTitle() + "][" + description + "]["
				+ NumberFormatUtil.getIntegerString(cnspAmount) + "][" + cnspDate.toString() + "]";
		if (cnsp != null && discount != null) {
//			HmsMessageBox.info(msg + "成功。");
			_msg.append(msg + "成功。");
			_set.add(cnsp);
			_set.add(discount);
			return true;
		} else {
//			HmsMessageBox.error(msg + "失敗!");
			_msg.append(msg + "失敗!");
			return false;
		}
			
	}

	/** 行為3:一卡通折抵 */
	private boolean behavior3(Set<Consumption> _set, StringBuilder _msg) {
		TypeEnum type = cbbType.getSelectedItem().getValue();
		String description = txbDescription.getValue();
		int cnspAmount = itbConsumptionAmount.getValue();
		LocalDate cnspDate = DateFormatUtil.parseLocalDate(dtbConsumptionDate.getValue());

		/* 消費 */
		Consumption cnsp = BusinessServiceFactory.getInstance().getService(AccountService.class).createNewConsumption(type, DirectionEnum.OUT, cnspAmount,
				description, PaymentTypeEnum.CASH, cnspDate);
		/* 折抵 */
		Consumption discount = BusinessServiceFactory.getInstance().getService(AccountService.class).createNewConsumption(TypeEnum.TRAFFIC_OTHER,
				DirectionEnum.IN, cnspAmount, "一卡通消費", PaymentTypeEnum.CASH, cnspDate);

		String msg = "新增一卡通折抵[" + type.getCategory().getTitle() + "][" + type.getTitle() + "][" + description + "]["
				+ NumberFormatUtil.getIntegerString(cnspAmount) + "][" + cnspDate.toString() + "]";
		if (cnsp != null && discount != null) {
//			HmsMessageBox.info(msg + "成功。");
			_msg.append(msg + "成功。");
			_set.add(cnsp);
			_set.add(discount);
			return true;
		} else {
//			HmsMessageBox.error(msg + "失敗!");
			_msg.append(msg + "失敗!");
			return false;
		}
	}

	/** 行為4:P幣折抵 */
	private boolean behavior4(Set<Consumption> _set, StringBuilder _msg) {
		TypeEnum type = cbbType.getSelectedItem().getValue();
		String description = txbDescription.getValue();
		int cnspAmount = itbConsumptionAmount.getValue();
		LocalDate cnspDate = DateFormatUtil.parseLocalDate(dtbConsumptionDate.getValue());

		/* 消費 */
		Consumption cnsp = BusinessServiceFactory.getInstance().getService(AccountService.class).createNewConsumption(type, DirectionEnum.OUT, cnspAmount,
				description, PaymentTypeEnum.CASH, cnspDate);
		/* 折抵 */
		Consumption discount = BusinessServiceFactory.getInstance().getService(AccountService.class).createNewConsumption(TypeEnum.LIFE_OTHER, DirectionEnum.IN,
				cnspAmount, "P幣消費", PaymentTypeEnum.CASH, cnspDate);

		String msg = "新增P幣折抵[" + type.getCategory().getTitle() + "][" + type.getTitle() + "][" + description + "]["
				+ NumberFormatUtil.getIntegerString(cnspAmount) + "][" + cnspDate.toString() + "]";
		if (cnsp != null && discount != null) {
//			HmsMessageBox.info(msg + "成功。");
			_msg.append(msg + "成功。");
			_set.add(cnsp);
			_set.add(discount);
			return true;
		} else {
//			HmsMessageBox.error(msg + "失敗!");
			_msg.append(msg + "失敗!");
			return false;
		}
	}
	
	/** 行為5:全聯儲值金折抵 */
	private boolean behavior5(Set<Consumption> _set, StringBuilder _msg) {
		TypeEnum type = cbbType.getSelectedItem().getValue();
		String description = txbDescription.getValue();
		int cnspAmount = itbConsumptionAmount.getValue();
		LocalDate cnspDate = DateFormatUtil.parseLocalDate(dtbConsumptionDate.getValue());

		/* 消費 */
		Consumption cnsp = BusinessServiceFactory.getInstance().getService(AccountService.class).createNewConsumption(type, DirectionEnum.OUT, cnspAmount,
				description, PaymentTypeEnum.CASH, cnspDate);
		/* 折抵 */
		Consumption discount = BusinessServiceFactory.getInstance().getService(AccountService.class).createNewConsumption(TypeEnum.LIFE_OTHER, DirectionEnum.IN,
				cnspAmount, "全聯儲值金折抵", PaymentTypeEnum.CASH, cnspDate);

		String msg = "新增全聯儲值金折抵[" + type.getCategory().getTitle() + "][" + type.getTitle() + "][" + description + "]["
				+ NumberFormatUtil.getIntegerString(cnspAmount) + "][" + cnspDate.toString() + "]";
		if (cnsp != null && discount != null) {
//			HmsMessageBox.info(msg + "成功。");
			_msg.append(msg + "成功。");
			_set.add(cnsp);
			_set.add(discount);
			return true;
		} else {
//			HmsMessageBox.error(msg + "失敗!");
			_msg.append(msg + "失敗!");
			return false;
		}
	}

	/** 行為5:麥當勞儲值金折抵 */
	private boolean behavior6(Set<Consumption> _set, StringBuilder _msg) {
		TypeEnum type = cbbType.getSelectedItem().getValue();
		String description = txbDescription.getValue();
		int cnspAmount = itbConsumptionAmount.getValue();
		LocalDate cnspDate = DateFormatUtil.parseLocalDate(dtbConsumptionDate.getValue());

		/* 消費 */
		Consumption cnsp = BusinessServiceFactory.getInstance().getService(AccountService.class).createNewConsumption(type, DirectionEnum.OUT, cnspAmount,
				description, PaymentTypeEnum.CASH, cnspDate);
		/* 折抵 */
		Consumption discount = BusinessServiceFactory.getInstance().getService(AccountService.class).createNewConsumption(TypeEnum.FOOD, DirectionEnum.IN,
				cnspAmount, "麥當勞儲值金折抵", PaymentTypeEnum.CASH, cnspDate);

		String msg = "新增麥當勞儲值金折抵[" + type.getCategory().getTitle() + "][" + type.getTitle() + "][" + description + "]["
				+ NumberFormatUtil.getIntegerString(cnspAmount) + "][" + cnspDate.toString() + "]";
		if (cnsp != null && discount != null) {
//			HmsMessageBox.info(msg + "成功。");
			_msg.append(msg + "成功。");
			_set.add(cnsp);
			_set.add(discount);
			return true;
		} else {
//			HmsMessageBox.error(msg + "失敗!");
			_msg.append(msg + "失敗!");
			return false;
		}
	}

	@Listen(Events.ON_CLICK + "=#btnResetBlanks")
	public void btnResetBlanks_clicked() {
		resetBlanks();
	}

	@Listen(Events.ON_CLOSE + "=#main")
	public void wdAddConsumption_closed(Event _evt) {
		_evt.stopPropagation();
		main.setVisible(false);
	}
}
