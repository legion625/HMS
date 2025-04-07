package hms.account.bpu;

import legion.biz.BpuType;

public enum AcntBpuType implements BpuType {
	/* Consumption */
	CNSP_1(CnspBuilder1.class), //
	CNSP_CASH_DISCOUNT(CnspBpuCashDiscount.class), //
	;

	// -------------------------------------------------------------------------------
	private Class builderClass;
	private Class[] argsClasses;

	private AcntBpuType(Class builderClass, Class... argsClasses) {
		this.builderClass = builderClass;
		this.argsClasses = argsClasses;
	}

	// -------------------------------------------------------------------------------
	@Override
	public Class getBuilderClass() {
		return builderClass;
	}

	@Override
	public Class[] getArgsClasses() {
		return argsClasses;
	}

	// -------------------------------------------------------------------------------
	@Override
	public boolean matchBiz(Object... _args) {
		switch (this) {
		case CNSP_1:
		case CNSP_CASH_DISCOUNT:
			return true;
		default:
			throw new IllegalArgumentException("Unexpected value: " + this);
		}
	}

}
