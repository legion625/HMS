package hms_kernel.data.account;

import java.util.List;

import hms_kernel.account.Consumption;
import hms_kernel.account.ConsumptionSearchParam;
import hms_kernel.account.CreditCard;
import hms_kernel.account.Payment;
import legion.IntegrationService;

public interface AccountDataService extends IntegrationService{

	// -------------------------------------------------------------------------------
	// ----------------------------------Consumption----------------------------------
	public boolean saveConsumption(Consumption _cnsp);

	public boolean deleteConsumption(String _uid);
	
	public Consumption loadConsumption(String _uid);

	public List<Consumption> searchConsumptions(ConsumptionSearchParam _searchParam);

	// -------------------------------------------------------------------------------
	// ------------------------------------Payment------------------------------------
	public boolean savePayment(Payment _payment);

	public boolean deletePayment(String _uid);

	public Payment loadPayment(String _uid);
	
	public List<Payment> loadPayments(String _consumptionUid);

	public List<Payment> searchPayments(ConsumptionSearchParam _searchParam);
	
	// -------------------------------------------------------------------------------
	// ----------------------------------CreditCard-----------------------------------
	public boolean saveCreditCard(CreditCard _creditCard);
	public boolean deleteCreditCard(String _uid);
	public List<CreditCard> loadCreditCards();
}
