package hms.web.control.zk.mobile;

import java.util.Date;

import org.slf4j.event.Level;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Include;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import hms.web.control.zk.account.GridAddCnspComposer;
import legion.util.LogUtil;

public class AccountingComposer extends SelectorComposer<Component> {
	
	@Wire
	private Window wdMobileAcnt;
	
	@Wire
	private Intbox amountBox;
	@Wire
	private Combobox categoryBox;
	@Wire
	private Textbox noteBox;
	@Wire
	private Datebox dateBox;
	@Wire
	private Listbox recordListbox;
	@Wire
	private Datebox startDateBox;
	@Wire
	private Datebox endDateBox;
	
	
	@Wire
	private Include icdAddCnspGrid;
	private GridAddCnspComposer addCnspGridComposer;

	// -------------------------------------------------------------------------------
	@Override
	public void doAfterCompose(Component comp) {
		try {
			super.doAfterCompose(comp);
			
			addCnspGridComposer = GridAddCnspComposer.of(icdAddCnspGrid);
			addCnspGridComposer.init(null);
			
		} catch (Throwable e) {
			LogUtil.log(e, Level.ERROR);
		}
	}
	
	
	// -------------------------------------------------------------------------------
	@Listen(Events.ON_CLOSE + "=#wdMobileAcnt")
	public void wdMobileAcnt_closed(Event evt) {
		wdMobileAcnt.setVisible(false);
		evt.stopPropagation();
	}
	
	@Listen("onClick = #saveBtn")
	public void saveRecord() {
		Integer amount = amountBox.getValue();
		String category = categoryBox.getValue();
		String note = noteBox.getValue();
		Date date = dateBox.getValue();

		// 呼叫你自己的 service 存資料
		System.out.println("儲存: " + date + " / " + amount + " / " + category + " / " + note);

		// 清空欄位
		amountBox.setValue(null);
		categoryBox.setValue(null);
		noteBox.setValue("");
		dateBox.setValue(new Date());
	}

	@Listen("onClick = #queryBtn")
	public void queryRecords() {
		Date startDate = startDateBox.getValue();
		Date endDate = endDateBox.getValue();

		// 從資料庫撈資料
		System.out.println("查詢區間: " + startDate + " ~ " + endDate);

		// 假資料
		recordListbox.getItems().clear();
		for(int i=0;i<30;i++) {
			Listitem item = new Listitem();
			item.appendChild(new Listcell("2025-04-16"));
			item.appendChild(new Listcell("100"));
			item.appendChild(new Listcell("餐飲"));
			item.appendChild(new Listcell("午餐"));
			recordListbox.appendChild(item);	
		}
		
	}
}
