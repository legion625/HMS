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

public class BizObjConsumptionTest extends AbstractHmsKernelInitTest {
	private static AccountDataService dataService = DataServiceFactory.getInstance()
			.getService(AccountDataService.class);

	private String targetUid;

	private Target target1, target2;

	@Before
	public void initMethod() {
		LocalDate ld1 = LocalDate.now();
		LocalDate ld2 = LocalDate.now().plusDays(1);

		target1 = new Target(TypeEnum.FOOD, DirectionEnum.OUT, 12345678, "description1", PaymentTypeEnum.CASH, ld1);
		target2 = new Target(TypeEnum.TRAFFIC_CITY, DirectionEnum.IN, 12345678, "description2", PaymentTypeEnum.CARD,
				ld2);
	}

	@Test
	public void testCRUD() throws Throwable {
		testCreateConsumption();
		testUpdateConsumption();
		testDeleteConsumption();
	}

	@Test
	@Ignore
	public void testCreateConsumption() throws Throwable {
		/* create */
		Consumption obj = Consumption.newInstance();
		PropertyUtils.copyProperties(obj, target1);
		assert obj.save();
		targetUid = obj.getUid();
		/* load */
		obj = dataService.loadConsumption(targetUid);
		TestUtil.assertObjEqual(target1, obj);
	}

	@Test
	@Ignore
	public void testUpdateConsumption() throws Throwable {
		Consumption obj = dataService.loadConsumption(targetUid);
		PropertyUtils.copyProperties(obj, target2);
		assert obj.save();
		/* load */
		obj = dataService.loadConsumption(targetUid);
		TestUtil.assertObjEqual(target2, obj);
	}

	@Test
	@Ignore
	public void testDeleteConsumption() {
		assert dataService.loadConsumption(targetUid).delete();
	}

	// -------------------------------------------------------------------------------
	public class Target {
		private TypeEnum type = TypeEnum.UNDEFINED; // 消費類型
		private DirectionEnum direction = DirectionEnum.UNDEFINED; // 流向
		private int amount; // 消費金額
		private String description; // 說明
		private PaymentTypeEnum paymentType = PaymentTypeEnum.UNDEFINED; // 付款方式
		private LocalDate date; // 消費日期

		private Target(TypeEnum type, DirectionEnum direction, int amount, String description,
				PaymentTypeEnum paymentType, LocalDate date) {
			this.type = type;
			this.direction = direction;
			this.amount = amount;
			this.description = description;
			this.paymentType = paymentType;
			this.date = date;
		}

		public TypeEnum getType() {
			return type;
		}

		public DirectionEnum getDirection() {
			return direction;
		}

		public int getAmount() {
			return amount;
		}

		public String getDescription() {
			return description;
		}

		public PaymentTypeEnum getPaymentType() {
			return paymentType;
		}

		public LocalDate getDate() {
			return date;
		}

	}
}
