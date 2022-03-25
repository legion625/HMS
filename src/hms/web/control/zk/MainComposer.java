package hms.web.control.zk;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Include;
import org.zkoss.zul.Menuitem;

public class MainComposer extends SelectorComposer<Component> {
	@Wire
//	private Iframe iframeMain;
	private Include icdMain;

	@Wire
	private Menuitem miConsumption;
	
	// -------------------------------------------------------------
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		System.out.println(this.getClass().getSimpleName() + ".doAfterCompose");
		
//		miConsumption_clicked();
		
		Events.postEvent(Events.ON_CLICK, miConsumption, null);
	}

	// -------------------------------------------------------------
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
}
