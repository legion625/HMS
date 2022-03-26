package hms.web.control.zk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Include;
import org.zkoss.zul.Menuitem;

import hms.web.control.zk.account.cnspPivot.CnspPivotPageComposer;
import hms.web.control.zk.developing.pivotDemo.PivotDemoComposer;

public class MainComposer extends SelectorComposer<Component> {
	private Logger log = LoggerFactory.getLogger(MainComposer.class);
	
	@Wire
//	private Iframe iframeMain;
	private Include icdMain;

	@Wire
	private Menuitem miConsumption;
	
	// -------------------------------------------------------------------------------
	@Override
	public void doAfterCompose(Component comp) {
		try {
			super.doAfterCompose(comp);
			
			Events.postEvent(Events.ON_CLICK, miConsumption, null);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		
	}

	// -------------------------------------------------------------------------------
//	@Listen(Events.ON_CLICK+"=#miHome")
//	public void miHome_clicked() {
//		iframeMain.invalidate();
//		iframeMain.setSrc("/home.zul");
//	}
	
	@Listen(Events.ON_CLICK + "=#miConsumption")
	public void miConsumption_clicked() {
		System.out.println("miConsumption_clicked");
//		iframeMain.invalidate();
//		iframeMain.setSrc("/account/consumptionMain.zul");
		icdMain.invalidate();
		icdMain.setSrc("/account/consumptionMain.zul");
	}
	
	@Listen(Events.ON_CLICK + "=#miAccountMobile")
	public void miAccountMobile_clicked() {
		System.out.println("miAccountMobile_clicked");
//		iframeMain.invalidate();
//		iframeMain.setSrc("/mobile/account/accountFunction.zul");
		icdMain.invalidate();
		icdMain.setSrc("/mobile/account/accountFunction.zul");
	}
	
	// -------------------------------------------------------------------------------
	@Listen(Events.ON_CLICK + "=#miVersion")
	public void miVersion_clicked() {
		icdMain.invalidate();
		icdMain.setSrc("/version.zul");
	}
	
	// -------------------------------------------------------------------------------
	@Listen(Events.ON_CLICK+"=#miPivotDemo")
	public void miPivotDemo_clicked() {
		icdMain.invalidate();
		icdMain.setSrc(PivotDemoComposer.SRC);
	}
	
	@Listen(Events.ON_CLICK+"=#miCnspPivotPage")
	public void miCnspPivotPage_clicked() {
		icdMain.invalidate();
		icdMain.setSrc(CnspPivotPageComposer.SRC);
	}
}
