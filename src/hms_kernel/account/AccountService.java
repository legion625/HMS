package hms_kernel.account;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hms_kernel.auth.User;
import hms_kernel.data.account.AccountDataService;

public class AccountService {
	// -----------------------------------------------------------
	private final static AccountService INSTANCE = new AccountService();
	// -----------------------------------------------------------
	private AccountService() {
	}

	public static AccountService getInstance() {
		return INSTANCE;
	}
	
	// -----------------------------------------------------------
	// ------------------------Consumption------------------------
	public Consumption createNewConsumption(TypeEnum _type, DirectionEnum _direction, int _consumptionAmount,
			String _description, PaymentTypeEnum _paymentType, LocalDate _consumptionDate) {
		return Consumption.create(_type, _direction, _consumptionAmount, _description, _paymentType, _consumptionDate);
	}

	public boolean updateCnsp(Consumption _cnsp) {
		return _cnsp.save();
	}

	public boolean deleteConsumption(Consumption _cnsp) {
		return _cnsp.delete();
	}
	
	/** 查詢消費 */
	public List<Consumption> searchConsumptions(ConsumptionSearchParam _queryParam, boolean _loadPayments) {
		List<Consumption> cnspList = AccountDataService.getInstance().searchConsumptions(_queryParam);
		if (_loadPayments) {
			List<Payment> paymentList = AccountDataService.getInstance().searchPayments(_queryParam);
			Map<String, List<Payment>> cnspPaymentListMap = new HashMap<>();
			for (Payment payment : paymentList) {
				String cnspUid = payment.getConsumptionUid();
				if (!cnspPaymentListMap.containsKey(cnspUid))
					cnspPaymentListMap.put(cnspUid, new ArrayList<>());
				cnspPaymentListMap.get(cnspUid).add(payment);
			}

			for (Consumption cnsp : cnspList)
				if (cnspPaymentListMap.containsKey(cnsp.getUid()))
					cnsp.setPaymentList(cnspPaymentListMap.get(cnsp.getUid()));
		}

		return cnspList;
	}
	
	/** 查詢未付清消費(包含付款資訊) */
	public List<Consumption> getPayableConsumptions() {
		ConsumptionSearchParam param = new ConsumptionSearchParam();
		List<Consumption> cnspList = AccountDataService.getInstance().searchConsumptions(param);
		List<Payment> paymentList = AccountDataService.getInstance().searchPayments(param);

		Map<String, List<Payment>> cnspPaymentListMap = new HashMap<>();
		for (Payment payment : paymentList) {
			String cnspUid = payment.getConsumptionUid();
			if (!cnspPaymentListMap.containsKey(cnspUid))
				cnspPaymentListMap.put(cnspUid, new ArrayList<>());
			cnspPaymentListMap.get(cnspUid).add(payment);
		}

		for (Consumption cnsp : cnspList)
			if (cnspPaymentListMap.containsKey(cnsp.getUid()))
				cnsp.setPaymentList(cnspPaymentListMap.get(cnsp.getUid()));

		List<Consumption> resultList = new ArrayList<>();
		for (Consumption cnsp : cnspList)
			if (cnsp.getAmount() != cnsp.getPayedAmount())
				resultList.add(cnsp);
		return resultList;
	}
	
	// -----------------------------------------------------------
	// ---------------------------Type----------------------------
	public TypeEnum[] getTypes(TypeCategoryEnum _category, boolean _enabledOnly) {
		return TypeEnum.values(_category, _enabledOnly);
	}
	
	// -----------------------------------------------------------
	// --------------------------Payment--------------------------
	public boolean addPayment(Consumption _cnsp, LocalDate _payDate, int _payAmount) {
		return _cnsp.addPayment(_payDate, _payAmount);
	}
	
	public boolean updatePayment(Payment _pm) {
		return _pm.save();
	}

	public boolean deletePayment(Consumption _cnsp, Payment _payment) {
		return _cnsp.deletePayment(_payment);
	}
	
	public List<PaymentInfoDto> queryPaymentInfos(ConsumptionSearchParam _queryParam) {
		// 查詢消費
		List<Consumption> cnspList = AccountDataService.getInstance().searchConsumptions(_queryParam);
		Map<String, Consumption> cnspMap = new HashMap<>();
		for (Consumption cnsp : cnspList)
			cnspMap.put(cnsp.getUid(), cnsp);
		// 查詢付款
		List<Payment> paymentList = AccountDataService.getInstance().searchPayments(_queryParam);

		// 回傳資料
		List<PaymentInfoDto> resultList = new ArrayList<>();
		for (Payment payment : paymentList)
			resultList.add(new PaymentInfoDto(payment, cnspMap.get(payment.getConsumptionUid())));
		return resultList;
	}
	
	// -------------------------------------------------------------------------------
	// ----------------------------------CreditCard-----------------------------------
	/** 建立信用卡 */
	public CreditCard createNewCreditCard(String title, String[] cardNo, User cardHolder, Bank bank,
			CardIssuer cardIssuer, int closingDayInMonth) {
		return CreditCard.createNewCreditCard(title, cardNo, cardHolder, bank, cardIssuer, closingDayInMonth);
	}

}
