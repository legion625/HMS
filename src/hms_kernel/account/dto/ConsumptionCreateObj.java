package hms_kernel.account.dto;

import java.time.LocalDate;

import hms_kernel.account.DirectionEnum;
import hms_kernel.account.PaymentTypeEnum;
import hms_kernel.account.TypeEnum;

public class ConsumptionCreateObj {
	private TypeEnum type = TypeEnum.UNDEFINED; // 消費類型
	private DirectionEnum direction = DirectionEnum.UNDEFINED; // 流向
	private int amount; // 消費金額
	private String description; // 說明
	private PaymentTypeEnum paymentType = PaymentTypeEnum.UNDEFINED; // 付款方式
	private LocalDate date; // 消費日期

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

}
