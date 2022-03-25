package hms.web.control.zk.mobile.account;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;

public class AccountFunctionComposer extends SelectorComposer<Component>{
	
	@Listen(Events.ON_CLICK + "=#btnAddConsumption")
	public void btnAddConsumption_clicked() {
		System.out.println("btnAddConsumption_clicked");
		Executions.sendRedirect("/mobile/account/addConsumptionView.zul");
		// Executions.sendRedirect("addConsumptionView.zul");
	}

	@Listen(Events.ON_CLICK + "=#btnShowTodayConsumptions")
	public void btnShowTodayConsumptions_clicked() {
		System.out.println("btnShowTodayConsumptions");
		Executions.sendRedirect("/mobile/account/consumptionSearchResult.zul");
	}

}
