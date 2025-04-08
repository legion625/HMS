package hms_kernel.account;

import java.time.LocalDate;
import java.util.List;

import hms_kernel.HmsObjectModel;
import hms_kernel.account.dto.ConsumptionCreateObj;
import hms_kernel.data.BizObjLoader;
import hms_kernel.data.account.AccountDataService;
import legion.DataServiceFactory;
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
	private Consumption() {
	}

	static Consumption newInstance() {
		Consumption cnsp = new Consumption();
		cnsp.configNewInstance();
		return cnsp;
	}

	public static Consumption getInstance(String _uid, long _oCreateDate, long _oUpdateDate) {
		Consumption cnsp = new Consumption();
		cnsp.configGetInstance(_uid, _oCreateDate, _oUpdateDate);
		return cnsp;
	}

	// -------------------------------------------------------------------------------
	// ----------------------------------Consumption----------------------------------
	public static Consumption create(ConsumptionCreateObj _dto) {
		Consumption cnsp = Consumption.newInstance();
		cnsp.setType(_dto.getType());
		cnsp.setDirection(_dto.getDirection());
		cnsp.setAmount(_dto.getAmount());
		cnsp.setDescription(_dto.getDescription());
		cnsp.setPaymentType(_dto.getPaymentType());
		cnsp.setDate(_dto.getDate());
		return cnsp.save() ? cnsp : null;
	}

	// -------------------------------------------------------------------------------
	// ---------------------------------getter&setter---------------------------------
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

	// -------------------------------------------------------------------------------
	public int getTypeIndex() {
		return (getType() == null ? TypeEnum.UNDEFINED : getType()).getIdx();
	}

	public int getDirectionIndex() {
		return (getDirection() == null ? DirectionEnum.UNDEFINED : getDirection()).getIdx();
	}

	public int getPaymentTypeIndex() {
		return (getPaymentType() == null ? PaymentTypeEnum.UNDEFINED : getPaymentType()).getIdx();
	}

	// -------------------------------------------------------------------------------
	// ----------------------override_method----------------------
	@Override
	protected boolean save() {
		return DataServiceFactory.getInstance().getService(AccountDataService.class).saveConsumption(this);
	}

	@Override
	public boolean delete() {
		return DataServiceFactory.getInstance().getService(AccountDataService.class).deleteConsumption(this.getUid());
	}

	// -----------------------------------------------------------
	// ------------------------consumption------------------------
	/** 各欄位摘要資訊 */
	public String getInfo() {
		return "[" + date.toString() + "][" + type.getCategory().getTitle() + "][" + type.getName() + "]["
				+ direction.getName() + "][" + NumberFormatUtil.getIntegerString(amount) + "][" + description + "]["
				+ paymentType.getName() + "]";
	}

	// -----------------------------------------------------------
	// --------------------------payment--------------------------
	private BizObjLoader<List<Payment>> paymentListLoader = BizObjLoader
			.of(DataServiceFactory.getInstance().getService(AccountDataService.class)::loadPayments);

	public void clearPaymentList() {
		paymentListLoader.clear();
	}

	protected void setPaymentList(List<Payment> paymentList) {
		paymentListLoader.setObj(this.getUid(), paymentList);
	}

	public List<Payment> getPaymentList(boolean _reload) {
		return paymentListLoader.getObj(this.getUid(), _reload);
	}

	public List<Payment> getPaymentList() {
		return getPaymentList(false);
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
