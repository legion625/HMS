package hms_kernel.account;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import legion.BusinessService;

public interface AccountService extends BusinessService {

	// -------------------------------------------------------------------------------
	// ----------------------------------Consumption----------------------------------
	public Consumption createNewConsumption(TypeEnum _type, DirectionEnum _direction, int _consumptionAmount,
			String _description, PaymentTypeEnum _paymentType, LocalDate _consumptionDate);

	public boolean updateCnsp(Consumption _cnsp);

	public boolean deleteConsumption(Consumption _cnsp);

	/** 查詢消費 */
	public List<Consumption> searchConsumptions(ConsumptionSearchParam _queryParam, boolean _loadPayments);

	public List<Consumption> getPayableConsumptions();

	// -------------------------------------------------------------------------------
	// -------------------------------------Type--------------------------------------
	public TypeEnum[] getTypes(TypeCategoryEnum _category, boolean _enabledOnly);

	// -------------------------------------------------------------------------------
	// ------------------------------------Payment------------------------------------
	public boolean addPayment(Consumption _cnsp, LocalDate _payDate, int _payAmount);

	public boolean updatePayment(Payment _pm);

	public boolean deletePayment(Consumption _cnsp, Payment _payment);

	public List<PaymentInfoDto> queryPaymentInfos(ConsumptionSearchParam _queryParam);
}
