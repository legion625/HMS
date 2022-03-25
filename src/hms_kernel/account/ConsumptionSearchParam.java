package hms_kernel.account;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConsumptionSearchParam {
	// -----------------------------------------------------------
	private List<TypeEnum> typeList = new ArrayList<>();
	private DirectionEnum direction = null;
	private List<PaymentTypeEnum> paymentTypeList = new ArrayList<>();
	private String description = null;
	private LocalDate consumptionDateStart = null;
	private LocalDate consumptionDateEnd = null;
	private LocalDate payDateStart = null;
	private LocalDate payDateEnd = null;
	
	private int limit;

	// -----------------------------------------------------------
	public List<TypeEnum> getTypeList() {
		return typeList;
	}

	public void setTypeList(List<TypeEnum> typeList) {
		this.typeList = typeList;
	}

	public DirectionEnum getDirection() {
		return direction;
	}

	public void setDirection(DirectionEnum direction) {
		this.direction = direction;
	}

	public List<PaymentTypeEnum> getPaymentTypeList() {
		return paymentTypeList;
	}

	public void setPaymentTypeList(List<PaymentTypeEnum> paymentTypeList) {
		this.paymentTypeList = paymentTypeList;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDate getConsumptionDateStart() {
		return consumptionDateStart;
	}

	public void setConsumptionDateStart(LocalDate consumptionDateStart) {
		this.consumptionDateStart = consumptionDateStart;
	}

	public LocalDate getConsumptionDateEnd() {
		return consumptionDateEnd;
	}

	public void setConsumptionDateEnd(LocalDate consumptionDateEnd) {
		this.consumptionDateEnd = consumptionDateEnd;
	}

	public LocalDate getPayDateStart() {
		return payDateStart;
	}

	public void setPayDateStart(LocalDate payDateStart) {
		this.payDateStart = payDateStart;
	}

	public LocalDate getPayDateEnd() {
		return payDateEnd;
	}

	public void setPayDateEnd(LocalDate payDateEnd) {
		this.payDateEnd = payDateEnd;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}
	
	
}
