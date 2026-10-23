package hms.account;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;

import org.junit.Test;

import hms_kernel.account.DirectionEnum;
import hms_kernel.account.PaymentTypeEnum;
import hms_kernel.account.TypeCategoryEnum;
import hms_kernel.account.TypeEnum;

public class CnspStatisticsTest {

	private static final LocalDate START = LocalDate.of(2026, 1, 15);
	private static final LocalDate END = LocalDate.of(2026, 4, 10);

	@Test
	public void testMonthsFilled() {
		CnspStatistics stat = new CnspStatistics(START, END);
		Map<YearMonth, int[]> map = stat.getMonthlyMap();
		assertEquals(4, map.size());
		assertArrayEquals(new int[2], map.get(YearMonth.of(2026, 2)));
	}

	@Test
	public void testNetAmount() {
		CnspStatistics stat = new CnspStatistics(START, END);
		stat.addConsumption(LocalDate.of(2026, 2, 1), TypeEnum.FOOD, DirectionEnum.OUT, PaymentTypeEnum.CASH, 1000, 1000);
		stat.addConsumption(LocalDate.of(2026, 2, 2), TypeEnum.FOOD, DirectionEnum.IN, PaymentTypeEnum.CASH, 300, 300);
		stat.addConsumption(LocalDate.of(2026, 3, 1), TypeEnum.LIFE_LIVE, DirectionEnum.IN_ADV, PaymentTypeEnum.CARD,
				200, 0);
		// 期間外，不計
		stat.addConsumption(LocalDate.of(2026, 1, 14), TypeEnum.FOOD, DirectionEnum.OUT, PaymentTypeEnum.CASH, 999, 0);

		assertEquals(3, stat.getCnspCount());
		assertEquals(1000 - 300 - 200, stat.getCnspAmount());
		assertEquals(700, stat.getMonthlyMap().get(YearMonth.of(2026, 2))[0]);
		assertEquals(-200, stat.getMonthlyMap().get(YearMonth.of(2026, 3))[0]);
		assertEquals(700, (int) stat.getCategoryAmountMap().get(TypeCategoryEnum.FOOD));
		assertEquals(-200, (int) stat.getTypeAmountMap(TypeCategoryEnum.LIFE).get(TypeEnum.LIFE_LIVE));
	}

	@Test
	public void testPayment() {
		CnspStatistics stat = new CnspStatistics(START, END);
		stat.addPayment(LocalDate.of(2026, 4, 10), DirectionEnum.OUT, 500);
		stat.addPayment(LocalDate.of(2026, 4, 11), DirectionEnum.OUT, 999); // 期間外
		stat.addPayment(LocalDate.of(2026, 1, 15), DirectionEnum.IN, 100);
		assertEquals(400, stat.getPayAmount());
		assertEquals(500, stat.getMonthlyMap().get(YearMonth.of(2026, 4))[1]);
		assertEquals(-100, stat.getMonthlyMap().get(YearMonth.of(2026, 1))[1]);
	}

	@Test
	public void testMonthlyAvg() {
		CnspStatistics stat = new CnspStatistics(START, END); // 1~4 月，共 4 個月
		stat.addConsumption(LocalDate.of(2026, 2, 1), TypeEnum.FOOD, DirectionEnum.OUT, PaymentTypeEnum.CASH, 1000, 0);
		stat.addPayment(LocalDate.of(2026, 3, 1), DirectionEnum.OUT, 1002);
		assertEquals(4, stat.getMonthCount());
		assertEquals(250, stat.getCnspMonthlyAvg());
		assertEquals(251, stat.getPayMonthlyAvg()); // 250.5 四捨五入
	}

	@Test
	public void testPaymentTypeSplit() {
		CnspStatistics stat = new CnspStatistics(START, END);
		stat.addConsumption(LocalDate.of(2026, 2, 1), TypeEnum.FOOD, DirectionEnum.OUT,
				PaymentTypeEnum.CARD_INSTALLMENT, 1200, 400);
		stat.addConsumption(LocalDate.of(2026, 2, 1), TypeEnum.FOOD, DirectionEnum.OUT, PaymentTypeEnum.CASH, 100,
				100);
		Map<PaymentTypeEnum, int[]> map = stat.getPaymentTypeMap();
		assertArrayEquals(new int[] { 400, 800 }, map.get(PaymentTypeEnum.CARD_INSTALLMENT));
		assertArrayEquals(new int[] { 100, 0 }, map.get(PaymentTypeEnum.CASH));
		assertFalse(map.containsKey(PaymentTypeEnum.LOAN));

		stat.addPayable(DirectionEnum.OUT, 800);
		stat.addPayable(DirectionEnum.IN, 50);
		assertEquals(750, stat.getPayableAmount());
	}
}
