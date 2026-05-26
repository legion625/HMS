package hms.web.control.zk.account;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Include;
import org.zkoss.zul.Window;

import hms_kernel.account.Consumption;
import legion.util.LogUtil;
import legion.web.zk.ZkUtil;

public class WindowAddConsumptionComposer extends SelectorComposer<Component> {
	// -------------------------------------------------------------------------------
	private final static String SRC = "/account/windowAddConsumption.zul";

	// -------------------------------------------------------------------------------

	private Logger log = LoggerFactory.getLogger(WindowAddConsumptionComposer.class);

	public static WindowAddConsumptionComposer of(Include _icd) {
		return ZkUtil.of(_icd, SRC, "wdAddCnsp");
	}

	// -------------------------------------------------------------------------------
	@WireVariable("requestScope")
	private Map<String, Object> requestScope;

	@Wire
	private Window wdAddCnsp;

	@Wire
	private Include icdAddCnspGrid;
	private GridAddCnspComposer addCnspGridComposer;

	// -------------------------------------------------------------------------------
	@Override
	public void doAfterCompose(Component comp) {
		try {
			super.doAfterCompose(comp);

		} catch (Throwable e) {
			LogUtil.log(e, Level.ERROR);
		}
	}

	// -------------------------------------------------------------------------------
	void init(Consumer<Set<Consumption>> csmAfterAddingCnsp) {
		addCnspGridComposer = GridAddCnspComposer.of(icdAddCnspGrid);
		addCnspGridComposer.init(csmAfterAddingCnsp);
	}

	// -------------------------------------------------------------------------------
	public void showWindow() {
		addCnspGridComposer.resetBlanks();
		wdAddCnsp.setVisible(true);
	}

	// -------------------------------------------------------------------------------
	void copyCnsp(Consumption _cnsp) {
		addCnspGridComposer.copyCnsp(_cnsp);
	}

	@Listen(Events.ON_CLOSE + "=#wdAddCnsp")
	public void wdAddConsumption_closed(Event _evt) {
		_evt.stopPropagation();
		wdAddCnsp.setVisible(false);
	}
}
