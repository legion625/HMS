package hms_kernel.account;

import legion.kernel.Field;

public enum CardIssuer implements Field{
	UNDEFINED(-1, "未定義"), VISA(1, "VISA"), MASTER(2, "MASTER"), JCB(3, "JCB");
	
	// -------------------------------------------------------------------------------
	private int dbIndex;
	private String title;
	
	// -------------------------------------------------------------------------------
	// ----------------------------------constructor----------------------------------
	private CardIssuer(int dbIndex, String title) {
		this.dbIndex = dbIndex;
		this.title = title;
	}
	
	// -------------------------------------------------------------------------------
	// ---------------------------------getter&setter---------------------------------
	@Override
	public int getDbIndex() {
		return dbIndex;
	}

	@Override
	public String getTitle() {
		return title;
	}
	
	// -------------------------------------------------------------------------------
	// ------------------------------------method-------------------------------------
	public static CardIssuer getInstance(int _dbIndex) {
		for (CardIssuer e : CardIssuer.values())
			if (e.dbIndex == _dbIndex)
				return e;
		return CardIssuer.UNDEFINED;
	}

}
