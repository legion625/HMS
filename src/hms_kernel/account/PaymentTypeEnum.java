package hms_kernel.account;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import legion.type.IdxEnum;

public enum PaymentTypeEnum implements IdxEnum{
	UNDEFINED(0, "未定義"), CASH(1, "現金"), CARD(2, "刷卡"), CARD_INSTALLMENT(3, "刷卡分期"), LOAN(4, "貸款");

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
	private PaymentTypeEnum(int dbIndex, String title) {
		this.dbIndex = dbIndex;
		this.title = title;
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
	public static PaymentTypeEnum[] values(boolean _containsUndefined) {
		if (_containsUndefined)
			return values();
		List<PaymentTypeEnum> list = new ArrayList<>();
		for (PaymentTypeEnum e : values()) {
			if (e.equals(UNDEFINED))
				continue;
			list.add(e);
		}
		return list.toArray(new PaymentTypeEnum[0]);
	}

	public static PaymentTypeEnum getInstance(int _dbIndex) {
		for (PaymentTypeEnum e : values())
			if (e.dbIndex == _dbIndex)
				return e;
		return PaymentTypeEnum.UNDEFINED;
	}

	public static PaymentTypeEnum getInstance(String _title) {
		for (PaymentTypeEnum e : values())
			if (e.title.equalsIgnoreCase(_title))
				return e;
		return PaymentTypeEnum.UNDEFINED;
	}

	public static Comparator<String> getTitleComparator() {
		return COMPARATOR_TITLE;
	}
	
}
