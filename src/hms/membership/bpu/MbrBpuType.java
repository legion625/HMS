package hms.membership.bpu;

import hms_kernel.membership.GulooStamp;
import legion.biz.BpuType;

public enum MbrBpuType implements BpuType {
	/* GulooStamp */
	GulooStamp$Create1(GulooStampBuilder1.class), //
	GulooStamp$Update(GulooStampBpuUpdate.class, GulooStamp.class), //

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
		case GulooStamp$Update:
			return true;
		default:
			throw new IllegalArgumentException("Unexpected value: " + this);
		}
	}
}
