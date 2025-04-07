package hms.account.bpu;

import java.time.LocalDate;

import hms_kernel.account.Consumption;
import hms_kernel.account.DirectionEnum;
import hms_kernel.account.PaymentTypeEnum;
import hms_kernel.account.TypeEnum;
import hms_kernel.account.dto.ConsumptionCreateObj;
import legion.biz.Bpu;
import legion.util.DataFO;
import legion.util.TimeTraveler;

public abstract class CnspBuilder extends Bpu<Consumption> {

	/* base */
	private TypeEnum type = TypeEnum.UNDEFINED; // 消費類型
	private DirectionEnum direction = DirectionEnum.UNDEFINED; // 流向
	private int amount; // 消費金額
	private String description; // 說明
	private PaymentTypeEnum paymentType = PaymentTypeEnum.UNDEFINED; // 付款方式
	private LocalDate date; // 消費日期

	/* data */
	// none

	// -------------------------------------------------------------------------------
	// -----------------------------------appender------------------------------------
	protected CnspBuilder appendType(TypeEnum type) {
		this.type = type;
		return this;
	}

	protected CnspBuilder appendDirection(DirectionEnum direction) {
		this.direction = direction;
		return this;
	}

	protected CnspBuilder appendAmount(int amount) {
		this.amount = amount;
		return this;
	}

	protected CnspBuilder appendDescription(String description) {
		this.description = description;
		return this;
	}

	protected CnspBuilder appendPaymentType(PaymentTypeEnum paymentType) {
		this.paymentType = paymentType;
		return this;
	}

	protected CnspBuilder appendDate(LocalDate date) {
		this.date = date;
		return this;
	}

	// -------------------------------------------------------------------------------
	// ------------------------------------getter-------------------------------------
	public TypeEnum getType() {
		return type;
	}

	public DirectionEnum getDirection() {
		return direction;
	}

	public int getAmount() {
		return amount;
	}

	public String getDescription() {
		return description;
	}

	public PaymentTypeEnum getPaymentType() {
		return paymentType;
	}

	public LocalDate getDate() {
		return date;
	}

	// -------------------------------------------------------------------------------
	private ConsumptionCreateObj packConsumptionCreateObj() {
		ConsumptionCreateObj dto = new ConsumptionCreateObj();
		dto.setType(getType());
		dto.setDirection(getDirection());
		dto.setAmount(getAmount());
		dto.setDescription(getDescription());
		dto.setPaymentType(getPaymentType());
		dto.setDate(getDate());
		return dto;
	}

	// -------------------------------------------------------------------------------
	@Override
	public boolean validate(StringBuilder _msg) {
		return true;
	}

	@Override
	public boolean verify(StringBuilder _msg) {
		boolean v = true;

		// type
		if (getType() == null || TypeEnum.UNDEFINED == getType()) {
			_msg.append("Consumption type NOT assigned.").append(System.lineSeparator());
			v = false;
		}

		// DirectionEnum
		if (getDirection() == null || DirectionEnum.UNDEFINED == getDirection()) {
			_msg.append("Direction NOT assigned.").append(System.lineSeparator());
			v = false;
		}

		// amount
		if (getAmount() == 0) {
			_msg.append("Amount should NOT be 0.").append(System.lineSeparator());
			v = false;
		}

		// description
		if (DataFO.isEmptyString(getDescription())) {
			_msg.append("Description should NOT be empty.").append(System.lineSeparator());
			v = false;
		}
		
		
		// PaymentType
		if (getPaymentType() == null || PaymentTypeEnum.UNDEFINED == getPaymentType()) {
			_msg.append("Payment type NOT assigned.").append(System.lineSeparator());
			v = false;
		}

		return v;
	}
	
	// -------------------------------------------------------------------------------
	protected Consumption buildCnsp(TimeTraveler _tt) {
		TimeTraveler tt = new TimeTraveler();

		Consumption cnsp = Consumption.create(packConsumptionCreateObj());
		if (cnsp == null) {
			tt.travel();
			log.error("Consumption.create return null.");
			return null;
		}
		tt.addSite("revert Consumption.create", () -> cnsp.delete());
		log.info("Consumption.create [{}][{}]", cnsp.getUid(), cnsp.getInfo());

		//
		if (_tt != null)
			_tt.copySitesFrom(tt);

		return cnsp;
	}

}
