package hms.web.zk.pivot;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

//import org.zkoss.pivot.PivotField;

public enum CnspPivotField {
	CNSP_DATE("消費日期"), CNSP_AMT("消費金額"), DESP("摘要");

//	private String name;
	private LegionPivotField field;

	private CnspPivotField(String name) {
//		this.name = name;
		field = new LegionPivotField(name);

	}

//	public PivotField getPivotField() {
//		return field;
//	}
//
//	public static List<PivotField> getPivotFieldList0(){
//		return Arrays.asList(CnspPivotField.values()).stream().map(CnspPivotField::getPivotField)
//				.collect(Collectors.toList());
//
//	}


}
