package hms.web.control.zk.account;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.pivot.Calculator;
import org.zkoss.pivot.PivotField;
import org.zkoss.pivot.PivotHeaderContext;
import org.zkoss.pivot.PivotRenderer;
import org.zkoss.pivot.Pivottable;
import org.zkoss.pivot.impl.SimplePivotRenderer;
import org.zkoss.pivot.impl.TabularPivotModel;
import org.zkoss.pivot.ui.PivotFieldControl;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;

import hms.web.zk.HmsNotification;
import hms.web.zk.pivot.CnspPivotField;
import hms.web.zk.pivot.LegionPivotModel;

public class CnspPivotPageComposer extends SelectorComposer<Component> {
	public final static String SRC = "account/cnspPivotPage.zul";
	
	private Logger log = LoggerFactory.getLogger(getClass());

	@Wire
	private Pivottable pvt;
//	@Wire
//	private PivotFieldControl pfc;
	
	// 
//	private TabularPivotModel model;
	
	// -------------------------------------------------------------------------------
	@Override
	public void doAfterCompose(Component comp) {
		try {
			super.doAfterCompose(comp);
			
//			init();
		} catch (Throwable e) {
			log.error(e.getMessage());
			e.printStackTrace();
			HmsNotification.error();
		}
	}
	
	private void init() {
		/* generate model */
//		StaticPivotModelFactory spmf = StaticPivotModelFactory.getInstance();
//		TabularPivotModel model  = spmf.build();
		log.debug("init test 1");
		LegionPivotModel model = new LegionPivotModel(CnspPivotField.getPivotFieldList0());
		log.debug("init test 2");
		/* set model */
		pvt.setModel(model);
		log.debug("init test 3");
//		pfc.setModel(model);
		
		/* renderer */
//		log.debug("init test 4");
//		PivotRenderer pvtRenderer = new SimplePivotRenderer();
//		pvtRenderer.renderDataField(null);
//		log.debug("init test 5");
//		pvt.setPivotRenderer(pvtRenderer);
//		log.debug("init test 6");
		
		
	}

}
