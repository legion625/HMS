package hms_kernel.account;

import java.time.LocalDate;

public class PaymentInfoDto {
	/* payment info */
	private String uid;
	private String consumptionUid;
	private LocalDate payDate;
	private int payAmount;
	/* consumption info */
	private TypeEnum type = TypeEnum.UNDEFINED; // 消費類型
	private int consumptionAmount; // 消費金額
	private String description; // 說明
	private PaymentTypeEnum consumptionType = PaymentTypeEnum.UNDEFINED; // 消費方式
	private LocalDate consumptionDate; // 消費時間

	// -----------------------------------------------------------
	// ------------------------constructor------------------------
	protected PaymentInfoDto(Payment _payment, Consumption _cnsp) {
		setUid(_payment.getUid());
		setConsumptionUid(_payment.getConsumptionUid());
		setPayDate(_payment.getDate());
		setPayAmount(_payment.getAmount());

		setType(_cnsp.getType());
		setConsumptionAmount(_cnsp.getAmount());
		setDescription(_cnsp.getDescription());
		setConsumptionType(_cnsp.getPaymentType());
		setConsumptionDate(_cnsp.getDate());
		// 若流向為「入帳」，把金額用負數表示。
		if (_cnsp.getDirection().equals(DirectionEnum.IN)) {
			setPayAmount(-payAmount);
			setConsumptionAmount(-consumptionAmount);
		}
	}

	// -----------------------------------------------------------
	// -----------------------getter&setter-----------------------
	public String getUid() {
		return uid;
	}

	private void setUid(String uid) {
		this.uid = uid;
	}

	public String getConsumptionUid() {
		return consumptionUid;
	}

	private void setConsumptionUid(String consumptionUid) {
		this.consumptionUid = consumptionUid;
	}

	public LocalDate getPayDate() {
		return payDate;
	}

	private void setPayDate(LocalDate payDate) {
		this.payDate = payDate;
	}

	public int getPayAmount() {
		return payAmount;
	}

	private void setPayAmount(int payAmount) {
		this.payAmount = payAmount;
	}

	public TypeEnum getType() {
		return type;
	}

	private void setType(TypeEnum type) {
		this.type = type;
	}

	public int getConsumptionAmount() {
		return consumptionAmount;
	}

	private void setConsumptionAmount(int consumptionAmount) {
		this.consumptionAmount = consumptionAmount;
	}

	public String getDescription() {
		return description;
	}

	private void setDescription(String description) {
		this.description = description;
	}

	public PaymentTypeEnum getConsumptionType() {
		return consumptionType;
	}

	private void setConsumptionType(PaymentTypeEnum consumptionType) {
		this.consumptionType = consumptionType;
	}

	public LocalDate getConsumptionDate() {
		return consumptionDate;
	}

	private void setConsumptionDate(LocalDate consumptionDate) {
		this.consumptionDate = consumptionDate;
	}
}
