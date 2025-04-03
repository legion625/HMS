package hms_kernel.account;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import hms_kernel.HmsObjectModel;
import hms_kernel.data.account.AccountDataService;
import legion.DataServiceFactory;
import legion.data.MySqlDataSource;
import legion.util.DateFormatUtil;
import legion.util.NumberFormatUtil;

public class Consumption extends HmsObjectModel {
	private TypeEnum type = TypeEnum.UNDEFINED; // 消費類型
	private DirectionEnum direction = DirectionEnum.UNDEFINED; // 流向
	private int amount; // 消費金額
	private String description; // 說明
	private PaymentTypeEnum paymentType = PaymentTypeEnum.UNDEFINED; // 付款方式
	private LocalDate date; // 消費日期

	// -----------------------------------------------------------
	// ------------------------constructor------------------------
	// TODO
	private Consumption() {
	}

	private static Consumption newInstance() {
		Consumption cnsp = new Consumption();
		cnsp.configNewInstance(); // TODO
		cnsp.paymentList = new ArrayList<>();
		if (cnsp.save())
			return cnsp;
		else
			return null;
	}

	public static Consumption getInstance(String _uid, long _oCreateDate, long _oUpdateDate) {
		Consumption cnsp = new Consumption();
		cnsp.configGetInstance(_uid, _oCreateDate, _oUpdateDate);
		return cnsp;
	}

	// -----------------------------------------------------------
	// -----------------------static_method-----------------------
	protected static Consumption create(TypeEnum _type, DirectionEnum _direction, int _consumptionAmount,
			String _description, PaymentTypeEnum _consumptionType, LocalDate _consumptionDate) {
		/* consumption */
		Consumption cnsp = Consumption.newInstance();
		cnsp.setType(_type);
		cnsp.setDirection(_direction);
		cnsp.setAmount(_consumptionAmount);
		cnsp.setDescription(_description);
		cnsp.setPaymentType(_consumptionType);
		cnsp.setDate(_consumptionDate);

		/* payment */
		Payment payment;
		switch (_consumptionType) {
		case CASH:
			payment = Payment.newInstance(cnsp.getUid());
			payment.setDate(cnsp.getDate());
			payment.setAmount(cnsp.getAmount());
			cnsp.paymentList.add(payment);
			break;
		case CARD:
		case CARD_INSTALLMENT:
		case LOAN:
			break;
		default:
			break;
		}
		cnsp.hasLoadedPaymentList = true;

		if (cnsp.save())
			return cnsp;
		else
			return null;
	}

	// -----------------------------------------------------------
	// -----------------------getter&setter-----------------------
	public TypeEnum getType() {
		return type;
	}

	public void setType(TypeEnum type) {
		this.type = type;
	}

	public DirectionEnum getDirection() {
		return direction;
	}

	public void setDirection(DirectionEnum direction) {
		this.direction = direction;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public PaymentTypeEnum getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentTypeEnum paymentType) {
		this.paymentType = paymentType;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public List<Payment> getPaymentList() {
		if (!hasLoadedPaymentList)
			loadPaymentList();
		return paymentList;
	}

	protected void setPaymentList(List<Payment> paymentList) {
		this.paymentList = paymentList;
		hasLoadedPaymentList = true;
	}
	
	// -------------------------------------------------------------------------------
	public int getTypeIndex() {
		return (getType() == null ? TypeEnum.UNDEFINED : getType()).getDbIndex();
	}

	public int getDirectionIndex() {
		return (getDirection() == null ? DirectionEnum.UNDEFINED : getDirection()).getDbIndex();
	}
	
	public int getPaymentTypeIndex() {
		return (getPaymentType() == null ? PaymentTypeEnum.UNDEFINED : getPaymentType()).getDbIndex();
	}

	// -------------------------------------------------------------------------------
	// ----------------------override_method----------------------
	@Override
	protected boolean save() {
		// TODO Transaction
		/* Payment */
		System.out.println("paymentList: " + paymentList);
		for (Payment payment : paymentList)
			if (!payment.save())
				return false;
		/* self */
//		return AccountDataService.getInstance().saveConsumption(this);
		return DataServiceFactory.getInstance().getService(AccountDataService.class).saveConsumption(this);
	}

	@Override
	protected boolean delete() {
		// TODO Transaction
		/* Payment */
		for (Payment payment : getPaymentList())
			if (!payment.delete())
				return false;
//		return AccountDataService.getInstance().deleteConsumption(this.getUid());
		return DataServiceFactory.getInstance().getService(AccountDataService.class).deleteConsumption(this.getUid());
	}

	// -----------------------------------------------------------
	// ------------------------consumption------------------------
	/** 各欄位摘要資訊 */
	public String getInfo() {
		return "[" + date.toString() + "][" + type.getCategory().getTitle() + "][" + type.getTitle() + "]["
				+ direction.getTitle() + "][" + NumberFormatUtil.getIntegerString(amount) + "][" + description + "]["
				+ paymentType.getTitle() + "]";
	}

	// -----------------------------------------------------------
	// --------------------------payment--------------------------
	private boolean hasLoadedPaymentList = false;
	private List<Payment> paymentList; // 付款資訊
	
	private void loadPaymentList() {
//		setPaymentList(AccountDataService.getInstance().loadPayments(this.getUid()));
		setPaymentList(DataServiceFactory.getInstance().getService(AccountDataService.class).loadPayments(this.getUid()));
	}

	protected boolean addPayment(LocalDate _payDate, int _payAmount) {
		Payment payment = Payment.newInstance(getUid());
		payment.setDate(_payDate);
		payment.setAmount(_payAmount);
		if (payment.save()) {
			if (hasLoadedPaymentList)
				paymentList.add(payment);
			return true;
		} else
			return false;
	}

	protected boolean deletePayment(Payment _payment) {
		if (_payment.delete()) {
			if (hasLoadedPaymentList)
				paymentList.remove(_payment);
			return true;
		} else
			return false;
	}

	/** 已付帳款 */
	public int getPayedAmount() {
		int payedAmount = 0;
		for (Payment p : getPaymentList())
			payedAmount += p.getAmount();
		return payedAmount;
	}

	/** 應付帳款 */
	public int getPayableAmount() {
		return amount - getPayedAmount();
	}

	/** 取得最近一筆付款 */
	public Payment getLastPayment() {
		if (getPaymentList() == null || getPaymentList().size() <= 0)
			return null;
		Payment result = null;
		for (Payment p : getPaymentList())
			if (result == null || p.getDate().isAfter(result.getDate()))
				result = p;
		return result;
	}



}
