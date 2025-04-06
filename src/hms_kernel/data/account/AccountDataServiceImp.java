package hms_kernel.data.account;

import java.util.List;
import java.util.Map;

import hms_kernel.account.Consumption;
import hms_kernel.account.ConsumptionSearchParam;
import hms_kernel.account.CreditCard;
import hms_kernel.account.Payment;

public class AccountDataServiceImp implements AccountDataService {

	private String source;

	// dao
	private AccountDao acntDao;

	@Override
	public void register(Map<String, String> _params) {
		if (_params == null || _params.isEmpty())
			return;

		source = _params.get("source");

		// dao
		acntDao = new AccountDao(source);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	// -------------------------------------------------------------------------------
	// ----------------------------------Consumption----------------------------------
	@Override
	public boolean saveConsumption(Consumption _cnsp) {
		return acntDao.saveConsumption(_cnsp);
	}

	@Override
	public boolean deleteConsumption(String _uid) {
		return acntDao.deleteConsumption(_uid);
	}
	
	@Override
	public Consumption loadConsumption(String _uid) {
		return acntDao.loadConsumption(_uid);
	}
	
	@Override
	public List<Consumption> searchConsumptions(ConsumptionSearchParam _searchParam) {
		return acntDao.searchConsumptions(_searchParam);
	}

	// -------------------------------------------------------------------------------
	// ------------------------------------Payment------------------------------------
	@Override
	public boolean savePayment(Payment _payment) {
		return acntDao.savePayment(_payment);
	}

	@Override
	public boolean deletePayment(String _uid) {
		return acntDao.deletePayment(_uid);
	}
	
	@Override
	public Payment loadPayment(String _uid) {
		return acntDao.loadPayment(_uid);
	}
	
	@Override
	public List<Payment> loadPayments(String _consumptionUid) {
		return acntDao.loadPayments(_consumptionUid);
	}

	@Override
	public List<Payment> searchPayments(ConsumptionSearchParam _searchParam) {
		return acntDao.searchPayments(_searchParam);
	}

	// -------------------------------------------------------------------------------
	// ------------------------------------Payment------------------------------------
	@Override
	public boolean saveCreditCard(CreditCard _creditCard) {
		// TODO 
		return false;
//		return acntDao.saveCreditCard(_creditCard);
	}

	@Override
	public boolean deleteCreditCard(String _uid) {
		// TODO 
				return false;
//		return acntDao.deleteCreditCard(_uid);
	}
	

	@Override
	public List<CreditCard> loadCreditCards() {
//		return acntDao.loadCreditCards();
		// TODO 
				return null;
	}

}
