package hms.web.control.zk.account.cnspPivot;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.pivot.GroupHandler;
import org.zkoss.pivot.PivotField;
import org.zkoss.pivot.PivotHeaderContext;
import org.zkoss.pivot.PivotRenderer;
import org.zkoss.pivot.Pivottable;
import org.zkoss.pivot.impl.SimplePivotRenderer;
import org.zkoss.pivot.impl.StandardCalculator;
import org.zkoss.pivot.impl.TabularPivotField;
import org.zkoss.pivot.impl.TabularPivotModel;

import hms_kernel.account.Consumption;
import hms_kernel.account.TypeCategoryEnum;

public class CnspPivotModelFactory {
	private Logger log = LoggerFactory.getLogger(getClass());
	
	 public static final CnspPivotModelFactory INSTANCE = new CnspPivotModelFactory();
	 
	    private CnspPivotModelFactory() {
	    }
	 
	    public TabularPivotModel build(List<Consumption> _cnspList) {
			List<List<Object>> pivotData = _cnspList.stream().map(CnspPivotData::new).map(CnspPivotCol::parse)
					.collect(Collectors.toList());
	    	return new TabularPivotModel(pivotData, CnspPivotCol.getColumns());
	    }
	    
//	    public TabularPivotModel build(List<List<Object>> _data) {
//	    	return new TabularPivotModel(_data, CnspPivotCol.getColumns());
//	    }
	    
//	    public TabularPivotModel build() {
//	        return new TabularPivotModel(CnspPivotData.getData(), CnspPivotData.getColumns());
//	    }
//	 
	    // configurator //
	 
	    public CnspPivotConfigurator getDefaultConfigurator() {
//	        return CONFIG_CITY_SALES;
	    	return CONF_0;
	    }
	 
	    public CnspPivotConfigurator[] getConfigurators() {
//	        return new CnspPivotConfigurator[] { CONFIG_PERFORMANCE, CONFIG_CITY_SALES, CONFIG_SALES_RACE };
	    	 return new CnspPivotConfigurator[] {CONF_0 };
	    }
	    
	    private static final CnspPivotConfigurator CONF_0 = new CnspPivotConfigurator("Default") {
	    	private Logger log = LoggerFactory.getLogger(getClass());
	    	
	    	@Override
			public void configure(TabularPivotModel model) {
//	    		 model.setFieldType("Type", PivotField.Type.ROW);
	    		
	    		model.setFieldType(CnspPivotCol.TYPE_CATE_TITLE.getColLabel(), PivotField.Type.ROW);
	    		model.setFieldType(CnspPivotCol.CNSP_YEARMONTH.getColLabel(), PivotField.Type.COLUMN);
	    		model.setFieldType(CnspPivotCol.CNSP_AMOUNT_OUT.getColLabel(), PivotField.Type.DATA);
	    		
//	    		model.getField(CnspPivotCol.TYPE_CATE_TITLE.getColLabel()).getComparator().
	    		/* configurator */
				model.setFieldKeyComparator(CnspPivotCol.TYPE_CATE_TITLE.getColLabel(),
						(o1, o2) -> TypeCategoryEnum.getTitleComparator().compare((String) o1, (String) o2));
	    		 
//	    		 TabularPivotField field =  		 model.getField("Type");
//				log.debug("{}\t{}\t{}", field.getFieldName(), field.getTitle(), field.getType());
//				field.
//				model.getd
			}
	    	
	    	@Override
			public void configure(Pivottable table) {
	    		table.setDataFieldOrient("row");
			}
	    	
			@Override
			public PivotRenderer getRenderer() {
				return null; // use default
				
//				SimplePivotRenderer r =new SimplePivotRenderer() {
//					@Override
//					public String renderDataField(PivotField _field) {
//						String v = super.renderDataField(_field);
//log.debug("super.renderDataField(_field): {}", super.renderDataField(_field));
//return v;
//					}
//				};
//				return r;
			}
			
			
			
		};
	 
//	    public static final CnspPivotConfigurator CONFIG_PERFORMANCE = new CnspPivotConfigurator("Performance") {
//	        public void configure(TabularPivotModel model) {
//	            model.setFieldType("Airline", PivotField.Type.COLUMN);
//	            model.setFieldType("Flight", PivotField.Type.COLUMN);
//	            model.setFieldType("Agent", PivotField.Type.ROW);
//	            model.setFieldType("Customer", PivotField.Type.ROW);
//	            model.setFieldType("Price", PivotField.Type.DATA);
//	            model.setFieldType("Mileage", PivotField.Type.DATA);
//	 
//	            model.setFieldSubtotals("Airline", new StandardCalculator[] {
//	                    StandardCalculator.AVERAGE, StandardCalculator.COUNT
//	            });
//	            model.setFieldSubtotals("Agent", new StandardCalculator[] {
//	                    StandardCalculator.AVERAGE, StandardCalculator.COUNT
//	            });
//	        }
//	 
//	        public void configure(Pivottable table) {
//	            table.setDataFieldOrient("column");
//	        }
//	         
//	        public PivotRenderer getRenderer() {
//	            return null;//use default
//	        }
//	    };
	 
//	    public static final CnspPivotConfigurator CONFIG_CITY_SALES = new CnspPivotConfigurator("Sales by City") {
//	        public void configure(TabularPivotModel model) {
//	            model.setFieldType("Origin", PivotField.Type.COLUMN);
//	            model.setFieldType("Destination", PivotField.Type.COLUMN);
//	            model.setFieldType("Airline", PivotField.Type.ROW);
//	            model.setFieldType("Flight", PivotField.Type.ROW);
//	            model.setFieldType("Customer", PivotField.Type.DATA);
//	            model.setFieldType("Price", PivotField.Type.DATA);
//	        }
//	 
//	        public void configure(Pivottable table) {
//	            table.setDataFieldOrient("row");
//	        }
//	         
//	        public PivotRenderer getRenderer() {
//	            return null;//use default
//	        }
//	    };
	 
//	    public static final CnspPivotConfigurator CONFIG_SALES_RACE = new CnspPivotConfigurator("Sales Race!") {
//	        public void configure(TabularPivotModel model) {
//	            model.setFieldType("Agent", PivotField.Type.COLUMN);
//	            model.setFieldType("Date", PivotField.Type.ROW);
//	            model.setFieldType("Customer", PivotField.Type.DATA);
//	            model.setFieldType("Price", PivotField.Type.DATA);
//	 
//	            // sort by last name, then first name
//	            model.setFieldKeyComparator("Agent", new Comparator<Object>() {
//	                public int compare(Object k1, Object k2) {
//	                    String s1 = (String) k1;
//	                    String s2 = (String) k2;
//	                    int i1 = s1.lastIndexOf(' ');
//	                    int i2 = s2.lastIndexOf(' ');
//	                    int cmp = s1.substring(i1 + 1).compareTo(s2.substring(i2 + 1));
//	                    if (cmp != 0)
//	                        return cmp;
//	                    String fname1 = i1 < 0 ? "" : s1.substring(0, i1).trim();
//	                    String fname2 = i2 < 0 ? "" : s2.substring(0, i2).trim();
//	                    return fname1.compareTo(fname2);
//	                }
//	            });
//	 
//	            // sort date by descending order
//	            model.getField("Date").setGroupHandler(new GroupHandler() {
//	                public Object getGroup(Object data) {
//	                    SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
//	                    return format.format((Date) data);
//	                }
//	            });
//	            model.setFieldKeyOrder("Date", false);
//	        }
//	 
//	        public void configure(Pivottable table) {
//	            table.setDataFieldOrient("column");
//	        }
//	 
//	        public PivotRenderer getRenderer() {
//	            return SALES_RACE_RENDERER;
//	        }
//	    };
	 
//	    private static final PivotRenderer SALES_RACE_RENDERER = new SimplePivotRenderer() {
//	 
//	        public int getColumnSize(Pivottable table, PivotHeaderContext colc, PivotField dataField) {
//	            if (dataField != null && "Price".equals(dataField.getFieldName()))
//	                return 200;
//	            return colc.isGrandTotal() && dataField != null ? 150 : 100;
//	        }
//	 
//	        public String renderCellSClass(Number data, Pivottable table, PivotHeaderContext rowContext,
//	                PivotHeaderContext columnContext, PivotField dataField) {
//	            if (dataField != null && "Price".equals(dataField.getFieldName())) {
//	                String sclass = "highlight";
//	                if (!rowContext.isGrandTotal() && !columnContext.isGrandTotal() && data != null
//	                        && data.doubleValue() > 300)
//	                    sclass += " important";
//	                return sclass;
//	            }
//	            return null;
//	        }
//	 
//	        public String renderCellStyle(Number data, Pivottable table, PivotHeaderContext rowContext,
//	                PivotHeaderContext columnContext, PivotField dataField) {
//	            if (columnContext.isGrandTotal())
//	                return "color: #11EE11; font-weight: bold";
//	            return null;
//	        }
//	    };
}
