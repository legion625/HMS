package hms.web.control.zk.mobile;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Include;

import hms.web.control.zk.account.GridAddCnspComposer;
import hms.web.control.zk.mobile.account.CnspSearchFnComposer;

public class MobileHomeComposer extends SelectorComposer<Component> {

	@Wire
	private Include icdMainContent;

	@Listen(Events.ON_CLICK + "=#btnCreateCnsp")
	public void btnCreateCnsp_clicked() {
		GridAddCnspComposer c = GridAddCnspComposer.of(icdMainContent);
		c.init(null);
	}
	
	@Listen(Events.ON_CLICK + "=#btnGotoCnspSearchFnPage")
	public void btnGotoCnspSearchFnPage_clicked() {
		CnspSearchFnComposer c = CnspSearchFnComposer.of(icdMainContent);
		c.defaultSearch();
	}
	
	

}
