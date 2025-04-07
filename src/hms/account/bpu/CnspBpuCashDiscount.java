package hms.account.bpu;

import java.time.LocalDate;

import hms_kernel.account.Consumption;
import hms_kernel.account.DirectionEnum;
import hms_kernel.account.PaymentTypeEnum;
import hms_kernel.account.TypeEnum;
import legion.biz.Bpu;
import legion.biz.BpuFacade;
import legion.util.TimeTraveler;

public class CnspBpuCashDiscount extends Bpu<Boolean> {
	/* base */
	// none

	/* data */
	private CnspBuilder1 cnspBuilder, discountBuilder;
	
	
//	private TypeEnum type = TypeEnum.UNDEFINED; // 消費類型
//	private int amount; // 消費金額
//	private String description; // 說明
//	private LocalDate date; // 消費日期
//	
//	private String discountMediaName; // 折抵媒介名稱

	// -------------------------------------------------------------------------------
	@Override
	protected CnspBpuCashDiscount appendBase() {
		/* base */
		// none

		/* data */
		cnspBuilder = new CnspBuilder1();
		cnspBuilder.appendDirection(DirectionEnum.OUT).appendPaymentType(PaymentTypeEnum.CASH);
		
		discountBuilder = new CnspBuilder1();
		discountBuilder.appendDirection(DirectionEnum.IN).appendPaymentType(PaymentTypeEnum.CASH);
		
//		appendPaymentType(PaymentTypeEnum.CARD);

		return this;
	}

	// -------------------------------------------------------------------------------
	public CnspBpuCashDiscount appendCnspType(TypeEnum type) {
		cnspBuilder.appendType(type);
		return this;
	}
	
	public CnspBpuCashDiscount appendDiscountType(TypeEnum type) {
		discountBuilder.appendType(type);
		return this;
	}

//	@Override
//	public CnspBuilderCashDiscount appendDirection(DirectionEnum direction) {
//		return (CnspBuilderCashDiscount) super.appendDirection(direction);
//	}

	public CnspBpuCashDiscount appendAmount(int amount) {
		cnspBuilder.appendAmount(amount);
		discountBuilder.appendAmount(amount);
		return this;
	}

	public CnspBpuCashDiscount appendDescription(String description) {
		cnspBuilder.appendDescription(description);
		return this;
	}
	
	public CnspBpuCashDiscount appendDiscountMediaName(String discountMediaName) {
//		this.description = description;
		discountBuilder.appendDescription(discountMediaName+"折抵");
		return this;
	}

//	@Override
//	public CnspBuilderCashDiscount appendPaymentType(PaymentTypeEnum paymentType) {
//		return (CnspBuilderCashDiscount) super.appendPaymentType(paymentType);
//	}

	public CnspBpuCashDiscount appendDate(LocalDate date) {
		cnspBuilder.appendDate(date);
		discountBuilder.appendDate(date);
		return this;
	}

	
	// -------------------------------------------------------------------------------
	@Override
	public boolean validate(StringBuilder _msg) {
		return true;
	}

	@Override
	public boolean verify(StringBuilder _msg) {
		boolean v = true;
		
		if (!cnspBuilder.verify(_msg))
			v = false;
		
		if(!discountBuilder.verify(_msg))
			v = false;
		
		return v;
	}

	
	// -------------------------------------------------------------------------------
	private boolean build = false;
	private Consumption cnsp, discount;
	
	@Override
	protected Boolean buildProcess(TimeTraveler _tt) {
		TimeTraveler tt = new TimeTraveler();

		Consumption cnsp = cnspBuilder.build(new StringBuilder(), tt);
		if (cnsp == null) {
			tt.travel();
			log.error("cnspBuilder.build return null.");
			return false;
		} // copy sites inside

		Consumption discount = discountBuilder.build(new StringBuilder(), tt);
		if (discount == null) {
			tt.travel();
			log.error("discountBuilder.build return null.");
			return false;
		} // copy sites inside

		//
		if (_tt != null)
			_tt.copySitesFrom(tt);

		//
		build = true;
		this.cnsp = cnsp;
		this.discount = discount;

		return true;
	}

	public Consumption getCnsp() {
		return build ? cnsp : null;
	}

	public Consumption getDiscount() {
		return build ? discount : null;
	}
	
	
	
}
