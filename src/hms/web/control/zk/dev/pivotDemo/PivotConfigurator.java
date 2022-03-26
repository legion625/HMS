package hms.web.control.zk.dev.pivotDemo;

import org.zkoss.pivot.PivotRenderer;
import org.zkoss.pivot.Pivottable;
import org.zkoss.pivot.impl.TabularPivotModel;

public abstract class PivotConfigurator {
	private final String title;

	public PivotConfigurator(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public abstract void configure(TabularPivotModel model);

	public abstract void configure(Pivottable table);

	public abstract PivotRenderer getRenderer();
}
