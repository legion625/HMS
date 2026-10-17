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

	/** 右側按鈕的樣式(對應 swipe.css)。 */
	public enum ActionStyle {
		/** 一般操作(藍底) */
		NORMAL("swipe-action"),
		/** 危險操作，例如刪除(紅底) */
		DANGER("swipe-action swipe-action-danger");

		private final String sclass;

		private ActionStyle(String _sclass) {
			this.sclass = _sclass;
		}
	}

	public void setBtnR1Style(ActionStyle _style) {
		btnR1.setSclass(_style.sclass);
	}

	public void setBtnR2Style(ActionStyle _style) {
		btnR2.setSclass(_style.sclass);
	}


}
