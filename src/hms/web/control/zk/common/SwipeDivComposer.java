package hms.web.control.zk.common;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Include;

import legion.web.zk.ZkUtil;

public class SwipeDivComposer extends SelectorComposer<Component> {

	private final static String SRC = "/common/swipeDiv.zul";

	public static SwipeDivComposer of(Include _icd) {
		return ZkUtil.of(_icd, SRC, "divSwipe");
	}

	@Wire
	private Div divContent;

	public Div getDivContent() {
		return divContent;
	}

	@Wire
	private Button btnR1, btnR2;

	public Button getBtnR1() {
		return btnR1;
	}

	public Button getBtnR2() {
		return btnR2;
	}


}
