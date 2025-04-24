package hms.web.control.zk;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.GenericForwardComposer;

public class MainComposer extends GenericForwardComposer<Component> {

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		// 根據瀏覽器 User-Agent 判斷是否為手機
		boolean isMobile = Executions.getCurrent().getBrowser("mobile") != null;

		if (isMobile) {
			Executions.sendRedirect("mobileHome.zul");
		} else {
			Executions.sendRedirect("legionmodule/main.zul");
		}
	}
}
