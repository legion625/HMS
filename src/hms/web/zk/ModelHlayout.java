package hms.web.zk;

import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Vlayout;


public class ModelHlayout<T> extends Hlayout implements AfterCompose {
	private ListModel<T> model;
	private ModelHlayoutItemRenderer<T> itemRenderer;
	
	public interface ModelHlayoutItemRenderer<T> {
		void render(Hlayout parent, T data, int index) throws Exception;
	}

	public void setModel(ListModel<T> model) {
		this.model = model;
		renderModel();
	}

	public void setItemRenderer(ModelHlayoutItemRenderer<T> itemRenderer) {
		this.itemRenderer = itemRenderer;
	}

	private void renderModel() {
		if (model == null || itemRenderer == null) {
			return;
		}
		// 先清空舊資料
		this.getChildren().clear();

		try {
			for (int i = 0; i < model.getSize(); i++) {
				T data = model.getElementAt(i);
				// 將子元件 attach 到這個 Hlayout
				itemRenderer.render(this, data, i);
			}
		} catch (Exception e) {
			throw UiException.Aide.wrap(e);
		}
	}

	@Override
	public void afterCompose() {
		// 如果一開始就有 model，要先 render
		renderModel();
	}
	
}
