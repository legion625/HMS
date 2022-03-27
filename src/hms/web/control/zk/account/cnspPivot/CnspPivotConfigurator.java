package hms.web.control.zk.account.cnspPivot;

import org.zkoss.pivot.PivotRenderer;
import org.zkoss.pivot.Pivottable;
import org.zkoss.pivot.impl.TabularPivotModel;

public abstract class CnspPivotConfigurator {
	private final String title;

	public CnspPivotConfigurator(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public abstract void configure(TabularPivotModel model);

	public abstract void configure(Pivottable table);

	public abstract PivotRenderer getRenderer();
}
