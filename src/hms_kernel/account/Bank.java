package hms_kernel.account;

public enum Bank {
	UNDEFINED("XXX", "未定義"), TAIPEI_FUBON("012", "台北富邦"), SINO_PAC("807", "永豐"), TAISHIN("812",
			"台新"), CHINA_TRUST("822", "中國信託");

	private String code;
	private String title;

	// -------------------------------------------------------------------------------
	// ----------------------------------constructor----------------------------------
	private Bank(String code, String title) {
		this.code = code;
		this.title = title;
	}

	// -------------------------------------------------------------------------------
	// ---------------------------------getter&setter---------------------------------
	public String getCode() {
		return code;
	}

	public String getTitle() {
		return title;
	}

	// -------------------------------------------------------------------------------
	public static Bank getInstance(String _code) {
		for (Bank bank : Bank.values())
			if (bank.getCode().equalsIgnoreCase(_code))
				return bank;
		return Bank.UNDEFINED;
	}

}
