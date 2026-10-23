package hms.account;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import hms_kernel.account.Consumption;
import hms_kernel.account.DirectionEnum;
import hms_kernel.account.Payment;
import hms_kernel.account.PaymentTypeEnum;
import hms_kernel.account.TypeCategoryEnum;
import hms_kernel.account.TypeEnum;

/**
 * 消費／付款統計(供 dashboard 使用)。
 * <p>
 * 金額一律以淨額計算：流向「出帳」為正，其餘(入帳、入帳-代墊款)為負，與消費管理頁的小計一致。
 */
public class CnspStatistics {
	private final LocalDate start, end;

	private int cnspCount; // 期間消費筆數
	private int cnspAmount; // 期間消費總額
	private int payAmount; // 期間付款總額
	private int payableAmount; // 未付餘額(不限期間)

	/** 每月統計：[0]消費金額(依消費日期)、[1]付款金額(依付款日期) */
	private final Map<YearMonth, int[]> monthlyMap = new LinkedHashMap<>();
	/** 消費類型金額 */
	private final Map<TypeEnum, Integer> typeAmountMap = new EnumMap<>(TypeEnum.class);
	/** 付款方式：[0]已付、[1]未付 */
	private final Map<PaymentTypeEnum, int[]> paymentTypeMap = new EnumMap<>(PaymentTypeEnum.class);

	// -------------------------------------------------------------------------------
	// ----------------------------------constructor----------------------------------
	public CnspStatistics(LocalDate _start, LocalDate _end) {
		this.start = _start;
		this.end = _end;
		for (YearMonth ym = YearMonth.from(_start); !ym.isAfter(YearMonth.from(_end)); ym = ym.plusMonths(1))
			monthlyMap.put(ym, new int[2]);
		for (PaymentTypeEnum pt : PaymentTypeEnum.values())
			paymentTypeMap.put(pt, new int[2]);
	}

	/**
	 * @param _cnspList    消費日期在期間內的消費(需含付款資訊)
	 * @param _payCnspList 付款日期在期間內的消費(需含付款資訊，期間外的付款會被忽略)
	 * @param _payableList 未付清的消費
	 * @param _filter      消費篩選條件(例如類型目錄)，null 表示不篩選
	 */
	public static CnspStatistics of(LocalDate _start, LocalDate _end, List<Consumption> _cnspList,
			List<Consumption> _payCnspList, List<Consumption> _payableList, Predicate<Consumption> _filter) {
		Predicate<Consumption> filter = _filter == null ? c -> true : _filter;
		CnspStatistics stat = new CnspStatistics(_start, _end);
		for (Consumption c : _cnspList)
			if (filter.test(c))
				stat.addConsumption(c.getDate(), c.getType(), c.getDirection(), c.getPaymentType(), c.getAmount(),
						c.getPayedAmount());
		for (Consumption c : _payCnspList)
			if (filter.test(c))
				for (Payment p : c.getPaymentList())
					stat.addPayment(p.getDate(), c.getDirection(), p.getAmount());
		for (Consumption c : _payableList)
			if (filter.test(c))
				stat.addPayable(c.getDirection(), c.getPayableAmount());
		return stat;
	}

	// -------------------------------------------------------------------------------
	/** 出帳為正，其餘為負 */
	public static int sign(DirectionEnum _direction) {
		return DirectionEnum.OUT == _direction ? 1 : -1;
	}

	private boolean inRange(LocalDate _date) {
		return _date != null && !_date.isBefore(start) && !_date.isAfter(end);
	}

	public void addConsumption(LocalDate _date, TypeEnum _type, DirectionEnum _direction, PaymentTypeEnum _paymentType,
			int _amount, int _payedAmount) {
		if (!inRange(_date))
			return;
		int s = sign(_direction);
		cnspCount++;
		cnspAmount += s * _amount;
		monthlyMap.get(YearMonth.from(_date))[0] += s * _amount;

		TypeEnum type = _type == null ? TypeEnum.UNDEFINED : _type;
		typeAmountMap.merge(type, s * _amount, Integer::sum);

		int[] pt = paymentTypeMap.get(_paymentType == null ? PaymentTypeEnum.UNDEFINED : _paymentType);
		pt[0] += s * _payedAmount;
		pt[1] += s * (_amount - _payedAmount);
	}

	public void addPayment(LocalDate _date, DirectionEnum _direction, int _amount) {
		if (!inRange(_date))
			return;
		int s = sign(_direction);
		payAmount += s * _amount;
		monthlyMap.get(YearMonth.from(_date))[1] += s * _amount;
	}

	public void addPayable(DirectionEnum _direction, int _payableAmount) {
		payableAmount += sign(_direction) * _payableAmount;
	}

	// -------------------------------------------------------------------------------
	// ------------------------------------getter-------------------------------------
	public int getCnspCount() {
		return cnspCount;
	}

	public int getCnspAmount() {
		return cnspAmount;
	}

	public int getPayAmount() {
		return payAmount;
	}

	public int getPayableAmount() {
		return payableAmount;
	}

	public Map<YearMonth, int[]> getMonthlyMap() {
		return Collections.unmodifiableMap(monthlyMap);
	}

	/** 各類型目錄金額(依 TypeCategoryEnum 順序，只列出有資料的目錄) */
	public Map<TypeCategoryEnum, Integer> getCategoryAmountMap() {
		Map<TypeCategoryEnum, Integer> map = new EnumMap<>(TypeCategoryEnum.class);
		typeAmountMap.forEach((type, amt) -> map.merge(type.getCategory(), amt, Integer::sum));
		return map;
	}

	/** 指定類型目錄下各類型金額(依 TypeEnum 順序，只列出有資料的類型) */
	public Map<TypeEnum, Integer> getTypeAmountMap(TypeCategoryEnum _category) {
		Map<TypeEnum, Integer> map = new EnumMap<>(TypeEnum.class);
		typeAmountMap.forEach((type, amt) -> {
			if (type.getCategory() == _category)
				map.put(type, amt);
		});
		return map;
	}

	/** 各付款方式的已付[0]／未付[1]金額(只列出有資料的付款方式) */
	public Map<PaymentTypeEnum, int[]> getPaymentTypeMap() {
		Map<PaymentTypeEnum, int[]> map = new EnumMap<>(PaymentTypeEnum.class);
		paymentTypeMap.forEach((pt, amts) -> {
			if (amts[0] != 0 || amts[1] != 0)
				map.put(pt, amts);
		});
		return map;
	}
}
