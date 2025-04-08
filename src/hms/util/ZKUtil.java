package hms.util;

import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Include;

import hms_kernel.account.AccountServiceImp;
import hms_kernel.account.DirectionEnum;
import hms_kernel.account.PaymentTypeEnum;
import hms_kernel.account.TypeCategoryEnum;
import hms_kernel.account.TypeEnum;

public class ZKUtil {
	
//	public static SelectorComposer of(Include _icd, String _id) {
//	return _icd.getFellow(_id).getAttribute("")	
//	}
	
	public static <T extends SelectorComposer> T of(Include _icd, String _cpnId) {
		return (T) _icd.getFellow(_cpnId).getAttribute("$composer");
	}
	
	public static void configureConsumptionTypeCategory(Combobox _cbb) {
		if(_cbb==null)
			return;
		_cbb.getChildren().clear();
		for (TypeCategoryEnum e : TypeCategoryEnum.values(false)) {
			Comboitem cbi = new Comboitem(e.getTitle());
			cbi.setValue(e);
			_cbb.appendChild(cbi);
		}
	}
	
	public static void configureConsumptionType(Combobox _cbb, boolean _enabledOnly) {
		if (_cbb == null)
			return;
		_cbb.getChildren().clear();
		for (TypeEnum e : TypeEnum.values(_enabledOnly)) {
			Comboitem cbi = new Comboitem(e.getName());
			cbi.setValue(e);
			_cbb.appendChild(cbi);
		}
	}
	@Deprecated
	public static void configureDirection(Combobox _cbbDirection) {
		while (_cbbDirection.getItemCount() > 0)
			_cbbDirection.removeItemAt(0);
		for (DirectionEnum e : DirectionEnum.values(false)) {
			Comboitem cbi = new Comboitem(e.getName());
			cbi.setValue(e);
			_cbbDirection.appendChild(cbi);
		}
	}

	@Deprecated
	public static void configurePaymentType(Combobox _cbbPaymentType) {
		while (_cbbPaymentType.getItemCount() > 0)
			_cbbPaymentType.removeItemAt(0);
		for (PaymentTypeEnum e : PaymentTypeEnum.values(false)) {
			Comboitem cbi = new Comboitem(e.getName());
			cbi.setValue(e);
			_cbbPaymentType.appendChild(cbi);
		}
	}
}
