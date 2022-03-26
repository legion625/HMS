package hms.web.control.zk.account;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
	
	public class PivotData {
		private static final long TODAY = new Date().getTime();
		private static final long DAY = 1000 * 60 * 60 * 24;

		public static List<List<Object>> getData() {
			Object[][] objs = new Object[][] {
					{ "Carlene Valone", "Tameka Meserve", "ATB Air", "AT15", dt(-7), "Berlin", "Paris", 186.6, 545 },
					{ "Antonio Mattos", "Sharon Roundy", "Jasper", "JS1", dt(-5), "Frankfurt", "Berlin", 139.5, 262 },
					{ "Russell Testa", "Carl Whitmore", "Epsilon", "EP2", dt(-3), "Dublin", "London", 108.0, 287 },
					{ "Antonio Mattos", "Velma Sutherland", "Epsilon", "EP5", dt(-1), "Berlin", "London", 133.5, 578 },
					{ "Carlene Valone", "Cora Million", "Jasper", "JS30", dt(-4), "Paris", "Frankfurt", 175.4, 297 },
					{ "Richard Hung", "Candace Marek", "DTB Air", "BK201", dt(-5), "Manchester", "Paris", 168.5, 376 },
					{ "Antonio Mattos", "Albert Briseno", "Fujito", "FJ1", dt(-7), "Berlin", "Osaka", 886.9, 5486 },
					{ "Russell Testa", "Louise Knutson", "HST Air", "HT6", dt(-2), "Prague", "London", 240.6, 643 },
					{ "Antonio Mattos", "Jessica Lunsford", "Jasper", "JS9", dt(-4), "Munich", "Lisbon", 431.6, 1222 },
					{ "Becky Schafer", "Lula Lundberg", "Jasper", "JS1", dt(-3), "Frankfurt", "Berlin", 160.5, 262 },
					{ "Carlene Valone", "Tameka Meserve", "Epsilon", "EP5", dt(-3), "Berlin", "London", 104.6, 578 },
					{ "Antonio Mattos", "Yvonne Melendez", "Epsilon", "EP5", dt(-2), "Berlin", "London", 150.5, 578 },
					{ "Antonio Mattos", "Josephine Whitley", "ATB Air", "AT15", dt(-6), "Berlin", "Paris", 192.6, 545 },
					{ "Antonio Mattos", "Velma Sutherland", "DTB Air", "BK201", dt(-6), "Manchester", "Paris", 183.8,
							376 },
					{ "Richard Hung", "Blanca Samuel", "Fujito", "FJ2", dt(-7), "Berlin", "Osaka", 915.3, 5486 },
					{ "Russell Testa", "Katherine Bennet", "Epsilon", "EP23", dt(-4), "Lisbon", "London", 214.8, 987 },
					{ "Joann Cleaver", "Alison Apodaca", "Jasper", "JS1", dt(-5), "Frankfurt", "Berlin", 166.3, 262 },
					{ "Antonio Mattos", "Tameka Meserve", "Epsilon", "EP21", dt(-1), "London", "Lisbon", 153.8, 987 },
					{ "Carlene Valone", "Janie Harper", "KST Air", "KT10", dt(-2), "Prague", "Paris", 187.9, 550 },
					{ "Russell Testa", "Myrtle Fournier", "Jasper", "JS30", dt(-4), "Paris", "Frankfurt", 207.5, 297 },
					{ "Joann Cleaver", "Victor Michalski", "Jasper", "JS2", dt(-3), "Frankfurt", "Amsterdam", 470.3,
							224 },
					{ "Carlene Valone", "Renee Marrow", "Epsilon", "EP19", dt(-4), "London", "Dublin", 133.6, 287 },
					{ "Carlene Valone", "Harold Fletcher", "Jasper", "JS2", dt(-4), "Frankfurt", "Amsterdam", 435.3,
							224 },
					{ "Antonio Mattos", "Velma Sutherland", "Jasper", "JS7", dt(-4), "Munich", "Amsterdam", 421.1,
							413 },
					{ "Becky Schafer", "Dennis Labbe", "Epsilon", "EP8", dt(-6), "London", "Paris", 134.4, 213 },
					{ "Joann Cleaver", "Louis Brumfield", "Epsilon", "EP4", dt(-2), "London", "Berlin", 132.3, 578 },
					{ "Antonio Mattos", "Eunice Alcala", "Jasper", "JS11", dt(-1), "Munich", "Frankfurt", 178.4, 189 },
					{ "Russell Testa", "Velma Sutherland", "Epsilon", "EP4", dt(-7), "London", "Berlin", 155.7, 578 } };

			List<List<Object>> list = new ArrayList<>();
			for (Object[] a : objs)
				list.add(Arrays.asList(a));
			return list;

		}
		
		/**
	     * Return column labels
	     */
	    public static List<String> getColumns() {
	        return Arrays.asList(new String[] { "Agent", "Customer", "Airline", "Flight", "Date", "Origin", "Destination",
	                "Price", "Mileage" });
	    }

		private static Date dt(int i) {
			return new Date(TODAY + i * DAY);
		}
	}

}
