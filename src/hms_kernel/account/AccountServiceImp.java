package hms_kernel.account;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hms_kernel.account.dto.ConsumptionCreateObj;
import hms_kernel.auth.User;
import hms_kernel.data.account.AccountDataService;
import legion.DataServiceFactory;

public class AccountServiceImp implements AccountService {
	
	private static AccountDataService dataService;
	
	@Override
	public void register(Map<String, String> _params) {
		dataService = DataServiceFactory.getInstance().getService(AccountDataService.class);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	// -------------------------------------------------------------------------------
	// ----------------------------------Consumption----------------------------------
	@Override
	public boolean updateCnsp(Consumption _cnsp) {
		return _cnsp.save();
	}
	
	
	@Override
	public List<Consumption> searchConsumptions(ConsumptionSearchParam _queryParam, boolean _loadPayments) {
		List<Consumption> cnspList = dataService.searchConsumptions(_queryParam);
		if (_loadPayments) {
			List<Payment> paymentList = dataService.searchPayments(_queryParam);
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
	@Override
	public List<Consumption> getPayableConsumptions() {
		ConsumptionSearchParam param = new ConsumptionSearchParam();
		List<Consumption> cnspList = dataService.searchConsumptions(param);
		List<Payment> paymentList = dataService.searchPayments(param);

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
	
	
	/**
	 * 執行消費沖銷折抵程序
	 * @param targetCnsp 原消費項目
	 * @param offsetDescription 沖銷說明的文字
	 * @param offsetDate 沖銷對帳的日期
	 * @return 新產生的反向沖銷消費物件
	 */
	@Override
	public Consumption offsetConsumption(Consumption targetCnsp, String offsetDescription, LocalDate offsetDate) {
	    // 1. 為「原消費」建立一筆付款紀錄 (使其在台幣帳上認列付清，PayableAmount 歸零)
	    int offsetAmount = targetCnsp.getPayableAmount(); // 取得尚未付款的剩餘應付帳款
	    if (offsetAmount <= 0) {
	        throw new IllegalArgumentException("該筆消費已付清，不需沖銷。");
	    }
	    
	    // 建立原消費的 Payment
	    Payment primaryPayment = Payment.create(targetCnsp.getUid(), offsetDate, offsetAmount);
	    if (primaryPayment != null) {
	        targetCnsp.clearPaymentList();
	    }

	    // 2. 複製原消費屬性，建立一筆「流向相反」的反向沖銷消費
	    ConsumptionCreateObj _dto = new ConsumptionCreateObj();
	    _dto.setDate(targetCnsp.getDate()); // 消費日期與原消費相同，方便同區間報表沖平
	    _dto.setType(targetCnsp.getType());
	    _dto.setPaymentType(targetCnsp.getPaymentType());
	    _dto.setDescription(offsetDescription + " (沖銷: " + targetCnsp.getDescription() + ")");
	    
	    // 流向取反：如果是 OUT 變 IN，IN 變 OUT
	    DirectionEnum oppositeDirection = (targetCnsp.getDirection() == DirectionEnum.OUT) 
	                                      ? DirectionEnum.IN : DirectionEnum.OUT;
	    _dto.setDirection(oppositeDirection);
	    _dto.setAmount(offsetAmount);
	    
	    Consumption offsetCnsp = Consumption.create(_dto);
	    
	   

//	    // 3. 儲存這筆反向沖銷消費到資料庫
//	    accountDataService.saveConsumption(offsetCnsp);

	    // 4. 重要：因為這筆沖銷是虛擬的，台幣並未真正流入/流出，所以它一出生就必須是「已付清」狀態
	    // 我們直接幫反向消費生一筆等額的 Payment，沖平它本身的金額
	    Payment offsetPayment = Payment.create(offsetCnsp.getUid(), offsetDate, offsetAmount);
	    if (offsetPayment != null) {
	        offsetCnsp.clearPaymentList();
	    }

	    return offsetCnsp;
	}
	
	
	// -------------------------------------------------------------------------------
	// -------------------------------------Type--------------------------------------
	@Override
	public TypeEnum[] getTypes(TypeCategoryEnum _category, boolean _enabledOnly) {
		return TypeEnum.values(_category, _enabledOnly);
	}
	
	// -------------------------------------------------------------------------------
	// ------------------------------------Payment------------------------------------
//	@Override
//	public boolean addPayment(Consumption _cnsp, LocalDate _payDate, int _payAmount) {
//		return _cnsp.addPayment(_payDate, _payAmount);
//	}
	
	@Override
	public Payment createPayment(Consumption _cnsp, LocalDate _payDate, int _payAmount) {
		return _cnsp == null ? null : Payment.create(_cnsp.getUid(), _payDate, _payAmount);
	}

	@Override
	public boolean updatePayment(Payment _pm) {
		return _pm.save();
	}

//	@Override
//	public boolean deletePayment(Consumption _cnsp, Payment _payment) {
//		return _cnsp.deletePayment(_payment);
//	}
	
	@Override
	public List<PaymentInfoDto> queryPaymentInfos(ConsumptionSearchParam _queryParam) {
		// 查詢消費
		List<Consumption> cnspList = dataService.searchConsumptions(_queryParam);
		Map<String, Consumption> cnspMap = new HashMap<>();
		for (Consumption cnsp : cnspList)
			cnspMap.put(cnsp.getUid(), cnsp);
		// 查詢付款
		List<Payment> paymentList = dataService.searchPayments(_queryParam);

		// 回傳資料
		List<PaymentInfoDto> resultList = new ArrayList<>();
		for (Payment payment : paymentList)
			resultList.add(new PaymentInfoDto(payment, cnspMap.get(payment.getConsumptionUid())));
		return resultList;
	}
	
	// -------------------------------------------------------------------------------
	// ----------------------------------CreditCard-----------------------------------
//	/** 建立信用卡 */
//	public CreditCard createNewCreditCard(String title, String[] cardNo, User cardHolder, Bank bank,
//			CardIssuer cardIssuer, int closingDayInMonth) {
//		return CreditCard.createNewCreditCard(title, cardNo, cardHolder, bank, cardIssuer, closingDayInMonth);
//	}

	

}
