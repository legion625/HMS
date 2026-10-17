package hms.web.control.zk.mobile;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Include;

import hms.web.control.zk.account.GridAddCnspComposer;
import hms.web.control.zk.mobile.account.CnspSearchFnComposer;
import hms.web.control.zk.mobile.membership.EntityHomeComposer;
import hms.web.control.zk.mobile.membership.GulooStampHomeComposer;

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
		// 複製：切到新增畫面，並帶入該筆消費的內容(消費日期為當天)
		c.setCopyHandler(cnsp -> {
			GridAddCnspComposer addComposer = GridAddCnspComposer.of(icdMainContent);
			addComposer.init(null);
			addComposer.copyCnsp(cnsp);
		});
		c.defaultSearch();
	}
	
	
	@Listen(Events.ON_CLICK+"=#btnGogoGulooStampPage")
	public void btnGogoGulooStampPage_clicked() {
		GulooStampHomeComposer c = GulooStampHomeComposer.of(icdMainContent);
		c.loadGulooStamps();
	}
	
	@Listen(Events.ON_CLICK + "=#btnGotoEntityPage")
	public void btnGotoEntityPage_clicked() {
		EntityHomeComposer c = EntityHomeComposer.of(icdMainContent);
		c.loadEntities();
		c.loadGulooStampCates();
	}
	
	

}
