package hms_kernel.account;

import java.time.LocalDate;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import hms_kernel.AbstractHmsKernelInitTest;
import hms_kernel.TestUtil;
import hms_kernel.data.account.AccountDataService;
import legion.DataServiceFactory;

public class BizObjPaymentTest extends AbstractHmsKernelInitTest{
	private static AccountDataService dataService = DataServiceFactory.getInstance()
			.getService(AccountDataService.class);

	private String targetUid;

	private Target target1, target2;

	@Before
	public void initMethod() {
		LocalDate ld1 = LocalDate.now();
		LocalDate ld2 = LocalDate.now().plusDays(1);

		target1 = new Target("consumptionUid1", ld1, 12345678);
		target2 = new Target("consumptionUid2", ld2, 22345678);
		
	}

	@Test
	public void testCRUD() throws Throwable {
		testCreatePayment();
		testUpdatePayment();
		testDeletePayment();
	}

	@Test
	@Ignore
	public void testCreatePayment() throws Throwable {
		/* create */
		Payment obj = Payment.newInstance(target1.consumptionUid);
		PropertyUtils.copyProperties(obj, target1);
		assert obj.save();
		targetUid = obj.getUid();
		/* load */
		obj = dataService.loadPayment(targetUid);
		TestUtil.assertObjEqual(target1, obj);
	}

	@Test
	@Ignore
	public void testUpdatePayment() throws Throwable {
		Payment obj = dataService.loadPayment(targetUid);
		PropertyUtils.copyProperties(obj, target2);
		assert obj.save();
		/* load */
		obj = dataService.loadPayment(targetUid);
		TestUtil.assertObjEqual(target2, obj);
	}

	@Test
	@Ignore
	public void testDeletePayment() {
		assert dataService.loadPayment(targetUid).delete();
	}

	// -------------------------------------------------------------------------------
	public class Target {
		private String consumptionUid; // 消費uid
		private LocalDate date; // 付款日期
		private int amount; // 付款金額

		private Target(String consumptionUid, LocalDate date, int amount) {
			this.consumptionUid = consumptionUid;
			this.date = date;
			this.amount = amount;
		}

		public String getConsumptionUid() {
			return consumptionUid;
		}

		public LocalDate getDate() {
			return date;
		}

		public int getAmount() {
			return amount;
		}

	}
}
