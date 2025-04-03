package hms_kernel.account;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import hms_kernel.HmsObjectModel;
import hms_kernel.data.account.AccountDataService;
import legion.DataServiceFactory;
import legion.kernel.LegionObject;
import legion.util.DataFO;
import legion.util.DateFormatUtil;
import legion.util.NumberFormatUtil;

public class Payment extends HmsObjectModel {
	private String consumptionUid; // 消費uid
	private LocalDate date; // 付款日期
	private int amount; // 付款金額

	// -----------------------------------------------------------
	// ------------------------constructor------------------------
	private Payment(String _consumptionUid) {
		this.setConsumptionUid(_consumptionUid);
	}
	// TODO

	protected static Payment newInstance(String _consumptionUid) {
		Payment payment = new Payment(_consumptionUid);
		payment.configNewInstance();
		return payment;
	}

//	public static Payment getInstance(String _uid, String _consumptionUid, LocalDateTime _oCreateDate,
//			LocalDateTime _oUpdateDate) {
	public static Payment getInstance(String _uid, String _consumptionUid, long _oCreateDate,
			long _oUpdateDate) {
		Payment payment = new Payment(_consumptionUid);
		payment.configGetInstance(_uid, _oCreateDate, _oUpdateDate);
		return payment;
	}

	// -----------------------------------------------------------
	// -----------------------getter&setter-----------------------
	public String getConsumptionUid() {
		return consumptionUid;
	}

	private void setConsumptionUid(String consumptionUid) {
		this.consumptionUid = consumptionUid;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	// -----------------------------------------------------------
	// ----------------------override_method----------------------
	@Override
	public boolean save() {
//		return AccountDataService.getInstance().savePayment(this);
		return DataServiceFactory.getInstance().getService(AccountDataService.class).savePayment(this);
	}

	@Override
	public boolean delete() {
//		return AccountDataService.getInstance().deletePayment(this.getUid());
		return DataServiceFactory.getInstance().getService(AccountDataService.class).deletePayment(this.getUid());
	}

	// -----------------------------------------------------------
	/** 各欄位摘要資訊 */
	public String getInfo() {
		return "[" + consumptionUid + "][" + date.toString() + "]["
				+ NumberFormatUtil.getIntegerString(amount) + "]";
	}
}
