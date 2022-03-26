package hms.web.zk.pivot;

import org.zkoss.pivot.Calculator;
import org.zkoss.pivot.GroupHandler;
import org.zkoss.pivot.PivotField;
import org.zkoss.pivot.event.FieldDataListener;

public class LegionPivotField implements PivotField {
	private String fieldName;

	LegionPivotField(String fieldName) {
		this.fieldName = fieldName;
	}

	@Override
	public void addFieldDataListener(FieldDataListener arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getFieldName() {
		return fieldName;
	}

	@Override
	public GroupHandler getGroupHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Calculator getSubtotal(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Calculator[] getSubtotals() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Calculator getSummary() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return fieldName+"-title"; // TODO TEST
	}

	@Override
	public Type getType() {
		// TODO Auto-generated method stub
//		return null;
		return Type.UNUSED; // 預設未使用
	}

	@Override
	public void removeFieldDataListener(FieldDataListener arg0) {
		// TODO Auto-generated method stub

	}

}
