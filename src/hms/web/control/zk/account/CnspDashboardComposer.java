package hms.web.control.zk.account;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

import hms.account.CnspStatistics;
import hms.util.ZKUtil;
import hms.web.zk.HmsMessageBox;
import hms_kernel.account.AccountService;
import hms_kernel.account.Consumption;
import hms_kernel.account.ConsumptionSearchParam;
import hms_kernel.account.PaymentTypeEnum;
import hms_kernel.account.TypeCategoryEnum;
import hms_kernel.account.TypeEnum;
import legion.BusinessServiceFactory;
import legion.util.DateFormatUtil;
import legion.util.NumberFormatUtil;
import legion.web.zk.ZkUtil;

/** 消費統計 dashboard(電腦版) */
public class CnspDashboardComposer extends SelectorComposer<Component> {
	private Logger log = LoggerFactory.getLogger(getClass());

	// -------------------------------------------------------------------------------
	@Wire
	private Datebox dtbStart, dtbEnd;
	@Wire
	private Combobox cbbTypeCategory, cbbType;
	@Wire
	private Textbox txbKeyword;

	@Wire
	private Label lbCnspAmount, lbPayAmount, lbPayableAmount, lbCnspCount, lbCnspAvg, lbPayAvg;

	// -------------------------------------------------------------------------------
	private AccountService accountService = BusinessServiceFactory.getInstance().getService(AccountService.class);

	// -------------------------------------------------------------------------------
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		init();
	}

	private void init() {
		ZKUtil.configureConsumptionTypeCategory(cbbTypeCategory, true);
		btnLast3Years_clicked();
	}

	// -------------------------------------------------------------------------------
	// -----------------------------------condition-----------------------------------
	@Listen(Events.ON_SELECT + "=#cbbTypeCategory")
	public void cbbTypeCategory_selected() {
		cbbType.setValue("");
		cbbType.getChildren().clear();
		TypeCategoryEnum typeCate = getSelectedValue(cbbTypeCategory);
		if (typeCate != null)
			ZkUtil.initCbb(cbbType, accountService.getTypes(typeCate, false), true);
	}

	@Listen(Events.ON_CLICK + "=#btnReset")
	public void btnReset_clicked() {
		cbbTypeCategory.setValue("");
		cbbTypeCategory.setSelectedItem(null);
		cbbType.setValue("");
		cbbType.getChildren().clear();
		txbKeyword.setValue("");
		btnSearch_clicked();
	}

	/** 類型目錄、類型、說明關鍵字(空白分隔，符合任一即可，不分大小寫) */
	private Predicate<Consumption> buildFilter() {
		TypeCategoryEnum typeCate = getSelectedValue(cbbTypeCategory);
		TypeEnum type = getSelectedValue(cbbType);
		String keyword = txbKeyword.getValue() == null ? "" : txbKeyword.getValue().trim().toLowerCase();
		List<String> keywords = keyword.isEmpty() ? List.of() : Arrays.asList(keyword.split("\\s+"));

		return c -> {
			if (typeCate != null && c.getType().getCategory() != typeCate)
				return false;
			if (type != null && c.getType() != type)
				return false;
			if (keywords.isEmpty())
				return true;
			String desp = c.getDescription() == null ? "" : c.getDescription().toLowerCase();
			return keywords.stream().anyMatch(desp::contains);
		};
	}

	private static <T> T getSelectedValue(Combobox _cbb) {
		return _cbb.getSelectedItem() == null ? null : _cbb.getSelectedItem().getValue();
	}

	// -------------------------------------------------------------------------------
	// ----------------------------------quick range----------------------------------
	@Listen(Events.ON_CLICK + "=#btnThisMonth")
	public void btnThisMonth_clicked() {
		LocalDate today = LocalDate.now();
		search(today.withDayOfMonth(1), today);
	}

	@Listen(Events.ON_CLICK + "=#btnThisYear")
	public void btnThisYear_clicked() {
		LocalDate today = LocalDate.now();
		search(today.withDayOfYear(1), today);
	}

	@Listen(Events.ON_CLICK + "=#btnLastYear")
	public void btnLastYear_clicked() {
		LocalDate firstDay = LocalDate.now().minusYears(1).withDayOfYear(1);
		search(firstDay, firstDay.plusYears(1).minusDays(1));
	}

	@Listen(Events.ON_CLICK + "=#btnLast12Months")
	public void btnLast12Months_clicked() {
		LocalDate today = LocalDate.now();
		search(today.minusMonths(11).withDayOfMonth(1), today);
	}

	@Listen(Events.ON_CLICK + "=#btnLast3Years")
	public void btnLast3Years_clicked() {
		LocalDate today = LocalDate.now();
		search(today.minusYears(2).withDayOfYear(1), today);
	}

	@Listen(Events.ON_CLICK + "=#btnSearch; " + Events.ON_OK + "=#txbKeyword")
	public void btnSearch_clicked() {
		LocalDate start = DateFormatUtil.parseLocalDate(dtbStart.getValue());
		LocalDate end = DateFormatUtil.parseLocalDate(dtbEnd.getValue());
		if (start == null || end == null || start.isAfter(end)) {
			HmsMessageBox.error("請輸入正確的日期區間。");
			return;
		}
		search(start, end);
	}

	// -------------------------------------------------------------------------------
	private void search(LocalDate _start, LocalDate _end) {
		dtbStart.setValue(toDate(_start));
		dtbEnd.setValue(toDate(_end));

		Predicate<Consumption> filter = buildFilter();

		// 消費日期在期間內的消費
		ConsumptionSearchParam cnspParam = new ConsumptionSearchParam();
		cnspParam.setConsumptionDateStart(_start);
		cnspParam.setConsumptionDateEnd(_end);
		List<Consumption> cnspList = accountService.searchConsumptions(cnspParam, true);

		// 付款日期在期間內的消費
		ConsumptionSearchParam payParam = new ConsumptionSearchParam();
		payParam.setPayDateStart(_start);
		payParam.setPayDateEnd(_end);
		List<Consumption> payCnspList = accountService.searchConsumptions(payParam, true);

		CnspStatistics stat = CnspStatistics.of(_start, _end, cnspList, payCnspList,
				accountService.getPayableConsumptions(), filter);
		log.debug("cnsp: {}, pay: {}", stat.getCnspAmount(), stat.getPayAmount());

		lbCnspAmount.setValue(NumberFormatUtil.getIntegerString(stat.getCnspAmount()));
		lbPayAmount.setValue(NumberFormatUtil.getIntegerString(stat.getPayAmount()));
		lbPayableAmount.setValue(NumberFormatUtil.getIntegerString(stat.getPayableAmount()));
		lbCnspCount.setValue(NumberFormatUtil.getIntegerString(stat.getCnspCount()));
		String avgSuffix = "(共 " + stat.getMonthCount() + " 個月)";
		lbCnspAvg.setValue("月平均 " + NumberFormatUtil.getIntegerString(stat.getCnspMonthlyAvg()) + avgSuffix);
		lbPayAvg.setValue("月平均 " + NumberFormatUtil.getIntegerString(stat.getPayMonthlyAvg()) + avgSuffix);

		// 等 chart.js、cnspDashboard.js 載入後才繪製
		Clients.evalJavaScript("(function f(d){if(window.hmsCnspDashboard&&window.Chart)hmsCnspDashboard.render(d);"
				+ "else setTimeout(function(){f(d);},50);})(" + toJson(stat) + ")");
	}

	@SuppressWarnings("unchecked")
	private String toJson(CnspStatistics _stat) {
		JSONObject json = new JSONObject();

		// 每月趨勢
		JSONArray months = new JSONArray(), monthCnsp = new JSONArray(), monthPay = new JSONArray();
		for (Map.Entry<YearMonth, int[]> e : _stat.getMonthlyMap().entrySet()) {
			months.add(e.getKey().toString());
			monthCnsp.add(e.getValue()[0]);
			monthPay.add(e.getValue()[1]);
		}
		json.put("months", months);
		json.put("monthCnsp", monthCnsp);
		json.put("monthPay", monthPay);
		json.put("cnspAvg", _stat.getCnspMonthlyAvg());
		json.put("payAvg", _stat.getPayMonthlyAvg());

		// 類型目錄(含各類型明細，供點選展開)
		JSONArray categories = new JSONArray();
		for (Map.Entry<TypeCategoryEnum, Integer> e : _stat.getCategoryAmountMap().entrySet()) {
			JSONArray types = new JSONArray();
			for (Map.Entry<TypeEnum, Integer> te : _stat.getTypeAmountMap(e.getKey()).entrySet())
				types.add(item(te.getKey().getName(), te.getValue()));
			JSONObject cate = item(e.getKey().getTitle(), e.getValue());
			cate.put("types", types);
			categories.add(cate);
		}
		json.put("categories", categories);

		// 付款方式
		JSONArray paymentTypes = new JSONArray();
		for (Map.Entry<PaymentTypeEnum, int[]> e : _stat.getPaymentTypeMap().entrySet()) {
			JSONObject pt = new JSONObject();
			pt.put("name", e.getKey().getName());
			pt.put("payed", e.getValue()[0]);
			pt.put("payable", e.getValue()[1]);
			paymentTypes.add(pt);
		}
		json.put("paymentTypes", paymentTypes);

		return json.toJSONString();
	}

	@SuppressWarnings("unchecked")
	private static JSONObject item(String _name, int _amount) {
		JSONObject obj = new JSONObject();
		obj.put("name", _name);
		obj.put("amount", _amount);
		return obj;
	}

	private static Date toDate(LocalDate _ld) {
		return Date.from(_ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}
}
