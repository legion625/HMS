package hms_kernel.membership.type;

import legion.type.IdxEnum;

public enum EntityType implements IdxEnum {
	UNDEFINED(0, "未定義"), HUMAN(1, "人"),  PROPERTY(2, "不動產"),  VEHICLE(3, "動產"), STUFF(4,"物品");

	private int idx;
	private String name;

	// -------------------------------------------------------------------------------
	// ----------------------------------constructor----------------------------------
	private EntityType(int idx, String name) {
		this.idx = idx;
		this.name = name;
	}

	// -------------------------------------------------------------------------------
	// ------------------------------------getter-------------------------------------
	@Override
	public int getIdx() {
		return idx;
	}

	@Override
	public String getName() {
		return name;
	}

	public static EntityType get(int _idx) {
		for (EntityType t : values())
			if (t.idx == _idx)
				return t;
		return UNDEFINED;

	}

}
