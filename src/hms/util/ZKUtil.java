package hms.util;

import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Include;

import hms_kernel.account.AccountService;
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
	
//	public static void configureConsumptionTypeCategory(Combobox _cbbCnspTypeCategory, Combobox _cbbCnspType) {
//		if (_cbbCnspTypeCategory == null || _cbbCnspType == null)
//			return;
//		configureConsumptionTypeCategory(_cbbCnspTypeCategory);
////		while (_cbbCnspType.getItemCount() > 0)
////			_cbbCnspType.removeItemAt(0);
//		_cbbCnspType.getChildren().clear();
//		
//		_cbbCnspTypeCategory.addEventListener(Events.ON_SELECT, evt -> {
////			while (_cbbCnspType.getItemCount() > 0)
////				_cbbCnspType.removeItemAt(0);
//			_cbbCnspType.getChildren().clear();
//
//			TypeCategoryEnum _cate = _cbbCnspTypeCategory.getSelectedItem().getValue();
//			
//			for (TypeEnum type : AccountService.getInstance().getTypes(_cate, true)) { // 只載入enabled的類型
//				Comboitem cbi = new Comboitem(type.getTitle());
//				cbi.setValue(type);
//				_cbbCnspType.appendChild(cbi);
//			}
//
//			if (_cbbCnspType.getItemCount() > 0)
//				_cbbCnspType.setSelectedIndex(0);
//			else
//				_cbbCnspType.setValue(null);
//			
//			System.out.println("_cbbCnspType.getValue(): "+_cbbCnspType.getValue());
//		});
//	}
	
	public static void configureConsumptionType(Combobox _cbb, boolean _enabledOnly) {
		if (_cbb == null)
			return;
		_cbb.getChildren().clear();
		for (TypeEnum e : TypeEnum.values(_enabledOnly)) {
			Comboitem cbi = new Comboitem(e.getTitle());
			cbi.setValue(e);
			_cbb.appendChild(cbi);
		}
	}

	public static void configureDirection(Combobox _cbbDirection) {
		while (_cbbDirection.getItemCount() > 0)
			_cbbDirection.removeItemAt(0);
		for (DirectionEnum e : DirectionEnum.values(false)) {
			Comboitem cbi = new Comboitem(e.getTitle());
			cbi.setValue(e);
			_cbbDirection.appendChild(cbi);
		}
	}

	public static void configurePaymentType(Combobox _cbbPaymentType) {
		while (_cbbPaymentType.getItemCount() > 0)
			_cbbPaymentType.removeItemAt(0);
		for (PaymentTypeEnum e : PaymentTypeEnum.values(false)) {
			Comboitem cbi = new Comboitem(e.getTitle());
			cbi.setValue(e);
			_cbbPaymentType.appendChild(cbi);
		}
	}
}
