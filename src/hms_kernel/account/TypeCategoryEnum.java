package hms_kernel.account;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import legion.kernel.Field;

public enum TypeCategoryEnum {
	UNDEFINED("未定義"), FOOD("餐飲費"), TRAFFIC("交通費"), CLOTHES("治裝費"), LIFE("生活費"), ADDITIONAL("額外開支"), DUTY("孝親費");

	// -----------------------------------------------------------
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
	private TypeCategoryEnum(String title) {
		this.title = title;
	}

	// -----------------------------------------------------------
	// -----------------------getter&setter-----------------------
	public String getTitle() {
		return title;
	}

	// -----------------------------------------------------------
	// --------------------------method---------------------------
	public static TypeCategoryEnum[] values(boolean _containsUndefined) {
		if (_containsUndefined)
			return values();
		List<TypeCategoryEnum> list = new ArrayList<>();
		for (TypeCategoryEnum e : values()) {
			if (e.equals(UNDEFINED))
				continue;
			list.add(e);
		}
		return list.toArray(new TypeCategoryEnum[0]);
	}

	public static TypeCategoryEnum getInstance(String _title) {
		for (TypeCategoryEnum e : values())
			if (e.title.equalsIgnoreCase(_title))
				return e;
		return TypeCategoryEnum.UNDEFINED;
	}

	public static Comparator<String> getTitleComparator() {
		return COMPARATOR_TITLE;
	}
	

}
