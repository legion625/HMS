package hms.web.control.zk.account.cnspPivot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public enum CnspPivotCol {
	TYPE_CATE_TITLE("消費類型", CnspPivotData::getTypeCategoryTitle), //
	TYPE_TITLE("消費子類型", CnspPivotData::getTypeTitle), //
	DIRECTION("流向", CnspPivotData::getDirectionTitle), //
	CNSP_AMOUNT("消費金額", CnspPivotData::getAmount), //
	CNSP_AMOUNT_OUT("消費出帳金額", CnspPivotData::getOutAmount), //
	PAYMENT_TYPE_TITLE("付款方式", CnspPivotData::getPaymentTypeTitle), //
	CNSP_YEARSEASON("消費年季", CnspPivotData::getCnspYearSeason), //
	CNSP_YEARMONTH("消費年月", CnspPivotData::getCnspYearMonth), //
	CNSP_DATE("消費日期", CnspPivotData::getCnspDate), //
	CNSP_YEAR("消費年度", CnspPivotData::getCnspYear), //
	CNSP_MONTH("消費月份", CnspPivotData::getCnspMonth), //
	CNSP_SEASON("消費季度", CnspPivotData::getCnspSeason), //

	;

	private String colLabel;
	private Function<CnspPivotData, Object> fnParse;

	private CnspPivotCol(String colLabel, Function<CnspPivotData, Object> fnParse) {
		this.colLabel = colLabel;
		this.fnParse = fnParse;
	}

	public String getColLabel() {
		return colLabel;
	}

	public Function<CnspPivotData, Object> getFnParse() {
		return fnParse;
	}

	public static List<String> getColumns() {
		List<String> objList = new ArrayList<>();
		for (CnspPivotCol c : values())
			objList.add(c.colLabel);
		return objList;
	}

	public static List<Object> parse(CnspPivotData _cnspPivotData) {
		List<Object> objList = new ArrayList<>();
		for (CnspPivotCol c : values())
			objList.add(c.fnParse.apply(_cnspPivotData));
		return objList;
	}

}
