package hms.web.control.zk.account.cnspPivot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import hms_kernel.account.AccountServiceImp;
import hms_kernel.account.Consumption;
import hms_kernel.account.ConsumptionSearchParam;
import hms_kernel.account.DirectionEnum;
import hms_kernel.account.PaymentTypeEnum;
import hms_kernel.account.TypeCategoryEnum;
import hms_kernel.account.TypeEnum;
import legion.util.DataFO;

public class CnspPivotData {
	
	/* attributes */
	private Consumption cnsp;

	/* constructor */
	public CnspPivotData(Consumption cnsp) {
		this.cnsp = cnsp;
	}

	/* getter */
	public Consumption getCnsp() {
		return cnsp;
	}

	/* getter-ext */
	public TypeEnum getType() {
		return getCnsp() == null ? TypeEnum.UNDEFINED : getCnsp().getType();
	}

	public String getTypeTitle() {
		return getType().getName();
	}
	
	public TypeCategoryEnum getTypeCategory() {
		return getType().getCategory();
	}
	
	public String getTypeCategoryTitle() {
		return getTypeCategory().getTitle();
	}
	
	public DirectionEnum getDirection() {
		return getCnsp() == null ? DirectionEnum.UNDEFINED : getCnsp().getDirection();
	}
	
	public String getDirectionTitle() {
		return getDirection().getName();
	}
	
	public int getAmount() {
		return getCnsp() == null ? 0 : getCnsp().getAmount();
	}
	
	/**
	 * 出帳為正數、入帳為負數
	 * @return
	 */
	public int getOutAmount() {
		switch (getDirection()) {
		case IN:
		case IN_ADV:
			return -getAmount();
		default:
			return getAmount();
		}
	}
	
	public PaymentTypeEnum getPaymentType() {
		return getCnsp() == null ? PaymentTypeEnum.UNDEFINED : getCnsp().getPaymentType();
	}
	
	public String getPaymentTypeTitle() {
		return getPaymentType().getName();
	}
	
	public LocalDate getCnspDate() {
		return getCnsp() == null ? null : getCnsp().getDate();
	}

	public int getCnspYear() {
		return getCnspDate() == null ? 0 : getCnspDate().getYear();
	}

	public int getCnspMonth() {
		return getCnspDate() == null ? 0 : getCnspDate().getMonthValue();
	}
	
	public int getCnspSeason() {
		int cnspMonth = getCnspMonth();
		return cnspMonth <= 0 ? 0 : (cnspMonth / 4) + 1;
	}
	
	/**
	 * 回傳yyyyMM格式
	 * @return
	 */
	public String getCnspYearMonth() {
		return DataFO.fillString(getCnspYear()+"", 4, '0')+DataFO.fillString(getCnspMonth()+"", 2, '0');
	}
	
	public String getCnspYearSeason() {
		return DataFO.fillString(getCnspYear()+"", 4, '0')+"Q"+getCnspSeason();
	}
	
//	private TypeEnum type = TypeEnum.UNDEFINED;
//	private TypeCategoryEnum typeCate = TypeCategoryEnum.UNDEFINED;
//	private DirectionEnum direction = DirectionEnum.UNDEFINED;
//	private int amount; // 消費金額
//	private PaymentTypeEnum paymentType; // 付款方式
	
	
//	private static final long TODAY = new Date().getTime();
//	private static final long DAY = 1000 * 60 * 60 * 24;

//	private static  AccountService accService = AccountService.getInstance();

//	private List<List<Object>> data;
	
//	public static List<List<Object>> parseData(List<Consumption> cnspList) {
//		
//	}
	
	// -------------------------------------------------------------------------------
	

	// -------------------------------------------------------------------------------
//	public static List<Object> parse(Consumption _cnsp){
//		Object[] objs = new Object[] { _cnsp.getType(), _cnsp.getDirection(), _cnsp.getAmount(), _cnsp.getDescription(),
//				_cnsp.getPaymentType(), _cnsp.getDate() };
//		return Arrays.asList(objs);
//	}
//	
	
	
	
//	public static List<List<Object>> getData() {
//		Object[][] objs = new Object[][] {
//				{ "Carlene Valone", "Tameka Meserve", "ATB Air", "AT15", dt(-7), "Berlin", "Paris", 186.6, 545 },
//				{ "Antonio Mattos", "Sharon Roundy", "Jasper", "JS1", dt(-5), "Frankfurt", "Berlin", 139.5, 262 },
//				{ "Russell Testa", "Carl Whitmore", "Epsilon", "EP2", dt(-3), "Dublin", "London", 108.0, 287 },
//				{ "Antonio Mattos", "Velma Sutherland", "Epsilon", "EP5", dt(-1), "Berlin", "London", 133.5, 578 },
//				{ "Carlene Valone", "Cora Million", "Jasper", "JS30", dt(-4), "Paris", "Frankfurt", 175.4, 297 },
//				{ "Richard Hung", "Candace Marek", "DTB Air", "BK201", dt(-5), "Manchester", "Paris", 168.5, 376 },
//				{ "Antonio Mattos", "Albert Briseno", "Fujito", "FJ1", dt(-7), "Berlin", "Osaka", 886.9, 5486 },
//				{ "Russell Testa", "Louise Knutson", "HST Air", "HT6", dt(-2), "Prague", "London", 240.6, 643 },
//				{ "Antonio Mattos", "Jessica Lunsford", "Jasper", "JS9", dt(-4), "Munich", "Lisbon", 431.6, 1222 },
//				{ "Becky Schafer", "Lula Lundberg", "Jasper", "JS1", dt(-3), "Frankfurt", "Berlin", 160.5, 262 },
//				{ "Carlene Valone", "Tameka Meserve", "Epsilon", "EP5", dt(-3), "Berlin", "London", 104.6, 578 },
//				{ "Antonio Mattos", "Yvonne Melendez", "Epsilon", "EP5", dt(-2), "Berlin", "London", 150.5, 578 },
//				{ "Antonio Mattos", "Josephine Whitley", "ATB Air", "AT15", dt(-6), "Berlin", "Paris", 192.6, 545 },
//				{ "Antonio Mattos", "Velma Sutherland", "DTB Air", "BK201", dt(-6), "Manchester", "Paris", 183.8,
//						376 },
//				{ "Richard Hung", "Blanca Samuel", "Fujito", "FJ2", dt(-7), "Berlin", "Osaka", 915.3, 5486 },
//				{ "Russell Testa", "Katherine Bennet", "Epsilon", "EP23", dt(-4), "Lisbon", "London", 214.8, 987 },
//				{ "Joann Cleaver", "Alison Apodaca", "Jasper", "JS1", dt(-5), "Frankfurt", "Berlin", 166.3, 262 },
//				{ "Antonio Mattos", "Tameka Meserve", "Epsilon", "EP21", dt(-1), "London", "Lisbon", 153.8, 987 },
//				{ "Carlene Valone", "Janie Harper", "KST Air", "KT10", dt(-2), "Prague", "Paris", 187.9, 550 },
//				{ "Russell Testa", "Myrtle Fournier", "Jasper", "JS30", dt(-4), "Paris", "Frankfurt", 207.5, 297 },
//				{ "Joann Cleaver", "Victor Michalski", "Jasper", "JS2", dt(-3), "Frankfurt", "Amsterdam", 470.3,
//						224 },
//				{ "Carlene Valone", "Renee Marrow", "Epsilon", "EP19", dt(-4), "London", "Dublin", 133.6, 287 },
//				{ "Carlene Valone", "Harold Fletcher", "Jasper", "JS2", dt(-4), "Frankfurt", "Amsterdam", 435.3,
//						224 },
//				{ "Antonio Mattos", "Velma Sutherland", "Jasper", "JS7", dt(-4), "Munich", "Amsterdam", 421.1,
//						413 },
//				{ "Becky Schafer", "Dennis Labbe", "Epsilon", "EP8", dt(-6), "London", "Paris", 134.4, 213 },
//				{ "Joann Cleaver", "Louis Brumfield", "Epsilon", "EP4", dt(-2), "London", "Berlin", 132.3, 578 },
//				{ "Antonio Mattos", "Eunice Alcala", "Jasper", "JS11", dt(-1), "Munich", "Frankfurt", 178.4, 189 },
//				{ "Russell Testa", "Velma Sutherland", "Epsilon", "EP4", dt(-7), "London", "Berlin", 155.7, 578 } };
//
//		List<List<Object>> list = new ArrayList<>();
//		for (Object[] a : objs)
//			list.add(Arrays.asList(a));
//		return list;
//
//	}
//	
//	/**
//     * Return column labels
//     */
//    public static List<String> getColumns() {
//        return Arrays.asList(new String[] { "Type", "Direction", "Amount", "Description", "PaymentType", "Date" });
//    }

//	private static Date dt(int i) {
//		return new Date(TODAY + i * DAY);
//	}
}
