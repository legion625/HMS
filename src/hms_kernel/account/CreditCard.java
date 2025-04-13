package hms_kernel.account;

import hms_kernel.HmsObjectModel;
import hms_kernel.auth.User;

public class CreditCard extends HmsObjectModel {
	private String title;
	private String[] cardNo;
	private String cardHolderUid;
	private String cardHolderName;
	private Bank bank;
	private CardIssuer cardIssuer;
	private int closingDayInMonth;
	
	// -------------------------------------------------------------------------------
	// ----------------------------------constructor----------------------------------
	private CreditCard() {
	}
	
	private static CreditCard newInstance(String title, String[] cardNo, User cardHolder, Bank bank,
			CardIssuer cardIssuer, int closingDayInMonth) {
		CreditCard cc = new CreditCard();
		cc.configNewInstance();
		cc.setTitle(title);
		cc.setCardNo(cardNo);
		cc.setCardHolderUid(cardHolder.getUid());
		cc.setCardHolderName(cardHolder.getName());
		cc.setBank(bank);
		cc.setCardIssuer(cardIssuer);
		cc.setClosingDayInMonth(closingDayInMonth);
		if (cc.save())
			return cc;
		else
			return null;
	}
	
	public static CreditCard getInstance(String uid, String[] cardNo, String cardHolderUid, String cardHolderName,
			Bank bank, CardIssuer cardIssuer, long oCreateDate, long oUpdateDate) {
		CreditCard cc = new CreditCard();
		cc.configGetInstance(uid, oCreateDate, oUpdateDate);
		cc.setCardNo(cardNo);
		cc.setCardHolderUid(cardHolderUid);
		cc.setCardHolderName(cardHolderName);
		cc.setBank(bank);
		cc.setCardIssuer(cardIssuer);
		return cc;
	}
	
	// -------------------------------------------------------------------------------
	// ---------------------------------getter&setter---------------------------------
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String[] getCardNo() {
		return cardNo;
	}

	private void setCardNo(String[] cardNo) {
		this.cardNo = cardNo;
	}

	public String getCardHolderUid() {
		return cardHolderUid;
	}

	private void setCardHolderUid(String cardHolderUid) {
		this.cardHolderUid = cardHolderUid;
	}

	public String getCardHolderName() {
		return cardHolderName;
	}

	private void setCardHolderName(String cardHolderName) {
		this.cardHolderName = cardHolderName;
	}

	public Bank getBank() {
		return bank;
	}

	private void setBank(Bank bank) {
		this.bank = bank;
	}

	public CardIssuer getCardIssuer() {
		return cardIssuer;
	}

	private void setCardIssuer(CardIssuer cardIssuer) {
		this.cardIssuer = cardIssuer;
	}

	public int getClosingDayInMonth() {
		return closingDayInMonth;
	}

	public void setClosingDayInMonth(int closingDayInMonth) {
		this.closingDayInMonth = closingDayInMonth;
	}
	
	// -------------------------------------------------------------------------------
	// ---------------------------------LegionObject----------------------------------
	@Override
	protected boolean save() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean delete() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	// -------------------------------------------------------------------------------
	// ---------------------------------static_method---------------------------------
	protected static CreditCard createNewCreditCard(String title, String[] cardNo, User cardHolder, Bank bank,
			CardIssuer cardIssuer, int closingDayInMonth) {
		return CreditCard.newInstance(title, cardNo, cardHolder, bank, cardIssuer, closingDayInMonth);
	}

}
