package hms_kernel.account;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import legion.kernel.Field;
import legion.type.IdxEnum;

public enum TypeEnum implements IdxEnum{
	UNDEFINED(0,"未定義",TypeCategoryEnum.UNDEFINED),
	FOOD(10, "餐飲費", TypeCategoryEnum.FOOD),
	
	TRAFFIC_EASY(21, "小明",TypeCategoryEnum.TRAFFIC), TRAFFIC_CITY(22, "小蝸", TypeCategoryEnum.TRAFFIC), //
	TRAFFIC_EXSIOR(23,"老牛",TypeCategoryEnum.TRAFFIC),TRAFFIC_YARIS(24,"小鴨",TypeCategoryEnum.TRAFFIC), 
	TRAFFIC_GOGORO3P(25,"GOGORO", TypeCategoryEnum.TRAFFIC),
	TRAFFIC_OTHER(29, "其他", TypeCategoryEnum.TRAFFIC), 
	CLOTHES(30, "衣物", TypeCategoryEnum.CLOTHES), CLOTHES_BEAUTY(31, "美妝", TypeCategoryEnum.CLOTHES), // 
	LIFE_LIVE(41, "住",TypeCategoryEnum.LIFE), LIFE_LIVE_SHULIN(46,"住-樹林",TypeCategoryEnum.LIFE),
	LIFE_COMMODITY(42, "日用品",TypeCategoryEnum.LIFE), LIFE_HEALTH(43,"醫療保健",TypeCategoryEnum.LIFE),LIFE_INSURANCE(44,"保險", TypeCategoryEnum.LIFE), LIFE_EDU(45,"教養", TypeCategoryEnum.LIFE), LIFE_OTHER(49, "其他",TypeCategoryEnum.LIFE), 
	ADDITIONAL_TICKET(51,"票券",TypeCategoryEnum.ADDITIONAL),
	ADDITIONAL_STATIONERY(52, "文具",TypeCategoryEnum.ADDITIONAL),
	ADDITIONAL_YARN(53, "毛線",TypeCategoryEnum.ADDITIONAL),
	ADDITIONAL_OTHER(59, "其他",TypeCategoryEnum.ADDITIONAL), 
	DUTY(60, "孝親費", TypeCategoryEnum.DUTY), // 
	DUTY_POCKET_HANA(61,"零用金-HANA",TypeCategoryEnum.DUTY ), //
	DUTY_POCKET_KUNI(62,"零用金-KUNI",TypeCategoryEnum.DUTY ), //
	;
	
	// -----------------------------------------------------------
	private int dbIndex;
	private String title;
	private TypeCategoryEnum category;

	// -----------------------------------------------------------
	private final static Comparator<String> COMPARATOR_TITLE = new Comparator<String>() {
		@Override
		public int compare(String o1, String o2) {
			return getInstance(o1).compareTo(getInstance(o2));
		}
	};
	
	// -----------------------------------------------------------
	// ------------------------constructor------------------------
	private TypeEnum(int dbIndex, String description, TypeCategoryEnum category) {
		this.dbIndex = dbIndex;
		this.title = description;
		this.category = category;
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
	
//	@Override
//	public String getTitle() {
//		return title;
//	}
	
	public TypeCategoryEnum getCategory() {
		return category;
	}
	
	
	public boolean isEnabled() {
		switch (this) {
		case TRAFFIC_EASY:
		case TRAFFIC_EXSIOR:
			return false;
		default:
			return true;
		}
	}
	
	// -----------------------------------------------------------
	// --------------------------method---------------------------
	
	public static TypeEnum getInstance(int _dbIndex) {
		for (TypeEnum e : values())
			if (e.dbIndex == _dbIndex)
				return e;
		return TypeEnum.UNDEFINED;
	}
	
	public static TypeEnum getInstance(String _title) {
		for (TypeEnum e : values())
			if (e.title.equalsIgnoreCase(_title))
				return e;
		return TypeEnum.UNDEFINED;
	}
	
	public static TypeEnum[] values(boolean _enabledOnly) {
		List<TypeEnum> list = new ArrayList<>();
		for (TypeEnum e : values())
			if (!_enabledOnly || e.isEnabled())
				list.add(e);
		return list.toArray(new TypeEnum[0]);
	}
	
	static TypeEnum[] values(TypeCategoryEnum _category, boolean _enabledOnly) {
		List<TypeEnum> list = new ArrayList<>();
		for (TypeEnum e : values())
			if (e.category.equals(_category)) {
				if (!_enabledOnly || e.isEnabled())
					list.add(e);
			}
		return list.toArray(new TypeEnum[0]);
	}

	public static String[] getTitles(TypeCategoryEnum _category) {
		List<String> list = new ArrayList<>();
		for (TypeEnum e : values())
			if (e.category.equals(_category))
				list.add(e.title);
		return list.toArray(new String[0]);
	}
	
	public static Comparator<String> getTitleComparator() {
		return COMPARATOR_TITLE;
	}

	
	
	

}
