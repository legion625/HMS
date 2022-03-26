package hms.web.zk.pivot;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.pivot.Calculator;
import org.zkoss.pivot.PivotField;
import org.zkoss.pivot.PivotField.Type;
import org.zkoss.pivot.PivotHeaderNode;
import org.zkoss.pivot.PivotHeaderTree;
import org.zkoss.pivot.PivotModel;
import org.zkoss.pivot.PivotModelExt;
import org.zkoss.pivot.event.PivotDataListener;
import org.zkoss.pivot.impl.SimplePivotField;
import org.zkoss.pivot.impl.SimplePivotHeaderTree;
import org.zkoss.pivot.impl.StandardCalculator;
import org.zkoss.util.logging.Log;

public class LegionPivotModel implements PivotModelExt {
	private Logger log = LoggerFactory.getLogger(getClass());
	
//	private PivotField[] fields;
	private List<PivotField> fieldList;

	public LegionPivotModel(List<PivotField> fieldList) {
		this.fieldList = fieldList;
		log.debug("test LegionPivotModel constructor");
//		SimplePivotField fieldDesp = new  SimplePivotField("desp");
//		
//		pivotFields = new SimplePivotField[] { fieldDesp
//
//		};
	}

	// -------------------------------------------------------------------------------
	// PivotModel

	@Override
	public void addPivotDataListener(PivotDataListener arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public PivotHeaderTree getColumnHeaderTree() {
		PivotHeaderTree t =new SimplePivotHeaderTree(getFields());
		log.debug("test PivotHeaderTree t: {}", t);
		return t; // TODO TEST
	}

	@Override
	public PivotField[] getFields() {
		return fieldList.toArray(new PivotField[0]);
	}

	@Override
	public PivotField[] getFields(Type type) {
		return fieldList.stream().filter(f -> type == f.getType()).collect(Collectors.toList())
				.toArray(new PivotField[0]);
	}

	@Override
	public PivotHeaderTree getRowHeaderTree() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getValue(PivotHeaderNode arg0, int arg1, PivotHeaderNode arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removePivotDataListener(PivotDataListener arg0) {
		// TODO Auto-generated method stub

	}

	// -------------------------------------------------------------------------------
	// PivotModelExt

	@Override
	public Calculator[] getSupportedCalculators() {
		// TODO Auto-generated method stub
//		return null;
		return new Calculator[] {StandardCalculator.COUNT}; // TODO test
	}

	@Override
	public void setFieldSubtotals(PivotField arg0, Calculator[] arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFieldSummary(PivotField arg0, Calculator arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFieldType(PivotField arg0, Type arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFieldType(PivotField arg0, Type arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
}
