package hms_kernel.account;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import legion.kernel.Field;
import legion.type.IdxEnum;

public enum DirectionEnum implements IdxEnum {
	UNDEFINED(-1, "未定義"), OUT(1, "出帳"), IN(2, "入帳"), IN_ADV(3,"入帳-代墊款");
	
	// -----------------------------------------------------------
	private int dbIndex;
	private String title;

	// -----------------------------------------------------------
	private final static Comparator<String> COMPARATOR_TITLE = new Comparator<String>() {
		@Override
		public int compare(String o1, String o2) {
			return getInstance(o1).compareTo(getInstance(o2));
		}
	};

	// -----------------------------------------------------------
	// ------------------------constructor------------------------
	private DirectionEnum(int dbIndex, String description) {
		this.dbIndex = dbIndex;
		this.title = description;
	}

	// -----------------------------------------------------------
	// -----------------------getter&setter-----------------------
	@Override
	public int getIdx() {
		return dbIndex;
	}

	@Override
	public String getName() {
		return title;
	}

	// -----------------------------------------------------------
	// --------------------------method---------------------------
	public static DirectionEnum[] values(boolean _containsUndefined) {
		if (_containsUndefined)
			return values();
		List<DirectionEnum> list = new ArrayList<>();
		for (DirectionEnum e : values()) {
			if (e.equals(UNDEFINED))
				continue;
			list.add(e);
		}
		return list.toArray(new DirectionEnum[0]);
	}

	public static DirectionEnum getInstance(int _dbIndex) {
		for (DirectionEnum e : values())
			if (e.dbIndex == _dbIndex)
				return e;
		return DirectionEnum.UNDEFINED;
	}

	public static DirectionEnum getInstance(String _title) {
		for (DirectionEnum e : values())
			if (e.title.equalsIgnoreCase(_title))
				return e;
		return DirectionEnum.UNDEFINED;
	}

	public static Comparator<String> getTitleComparator() {
		return COMPARATOR_TITLE;
	}
}
