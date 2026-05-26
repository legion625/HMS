package hms.membership.bpu;

import legion.biz.BpuType;

public enum MbrBpuType implements BpuType {
	/* GulooStamp */
	GulooStamp$Create1(GulooStampBuilder1.class), //

	;

	// -------------------------------------------------------------------------------
	private Class builderClass;
	private Class[] argsClasses;

	private MbrBpuType(Class builderClass, Class... argsClasses) {
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
		case GulooStamp$Create1:
			return true;
		default:
			throw new IllegalArgumentException("Unexpected value: " + this);
		}
	}
}
