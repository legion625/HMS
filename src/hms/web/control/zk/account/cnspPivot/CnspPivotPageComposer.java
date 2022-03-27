package hms.web.control.zk.account.cnspPivot;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.pivot.Pivottable;
import org.zkoss.pivot.impl.TabularPivotModel;
import org.zkoss.pivot.ui.PivotFieldControl;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.CheckEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Include;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Vlayout;

import hms.util.ZKUtil;
import hms.web.zk.HmsNotification;
import hms_kernel.account.Consumption;

public class CnspPivotPageComposer extends SelectorComposer<Component>{
	private Logger log = LoggerFactory.getLogger(getClass());

	public final static String SRC = "/account/cnspPivot/cnspPivotPage.zul";

	public static CnspPivotPageComposer of(Include _icd) {
		return ZKUtil.of(_icd, "wdCnspPivotPageMain");
	}

	// -------------------------------------------------------------------------------
    private static final long serialVersionUID = 1L;
    @Wire
    private Pivottable pivot;
    @Wire
    private PivotFieldControl pfc;
    @Wire
    private Checkbox colGrandTotal, rowGrandTotal;
    @Wire
    private Radio colOrient, rowOrient;
    @Wire
    private Vlayout rawDataLayout;
    @Wire
    private Hlayout preDef;

    private TabularPivotModel pivotModel;

    @Override
    public void doAfterCompose(Component comp) {
        try {
			super.doAfterCompose(comp);
			init(null); // XXX 不知道為什麼，若一開始沒呼叫這行，消費管理頁面那邊就會卡住。
		} catch (Throwable e) {
			e.printStackTrace();
			log.error(e.getMessage());
			HmsNotification.error();
		}

//        CnspPivotModelFactory pmf = CnspPivotModelFactory.INSTANCE;
//        pivotModel = pmf.build();
//        pivot.setModel(pivotModel);
//        pfc.setModel(pivotModel);
//        loadConfiguration(pmf.getDefaultConfigurator());
//
//        // load predefined scenario
//        for (CnspPivotConfigurator conf : pmf.getConfigurators())
//            preDef.appendChild(getPreDefDiv(conf));
    }

//    public void init() {
//    	/* search cnsp */
//		ConsumptionSearchParam param = new ConsumptionSearchParam();
//		List<Consumption> cnspList = AccountService.getInstance().searchConsumptions(param, false);
//		log.debug("cnspList.size(): {}", cnspList.size());
//
//		init(cnspList);
//    }

	public void init(List<Consumption> cnspList) {
		if(cnspList==null)
			cnspList = new ArrayList<>();

		/* transform to pivotData format */
//		List<List<Object>> pivotData = cnspList.stream().map(CnspPivotData::parse).collect(Collectors.toList());

		/* model */
		CnspPivotModelFactory pmf = CnspPivotModelFactory.INSTANCE;
//		pivotModel = pmf.build(pivotData);
		pivotModel = pmf.build(cnspList);
		pivot.setModel(pivotModel);
		pfc.setModel(pivotModel);
		loadConfiguration(pmf.getDefaultConfigurator());

		// load predefined scenario
		preDef.getChildren().clear();
		for (CnspPivotConfigurator conf : pmf.getConfigurators())
			preDef.appendChild(getPreDefDiv(conf));

	}

    @Listen("onCheck = #colGrandTotal")
    public void enableColumnGrandTotal(CheckEvent event) {
        pivot.setGrandTotalForColumns(event.isChecked());
    }

    @Listen("onCheck = #rowGrandTotal")
    public void enableRowGrandTotal(CheckEvent event) {
        pivot.setGrandTotalForRows(event.isChecked());
    }

    @Listen("onCheck = #dataOrient")
    public void enableDataOrient(CheckEvent event) {
        pivot.setDataFieldOrient(((Radio) event.getTarget()).getLabel());
    }

    @Listen("onCheck = #autowrap")
    public void enableAutowrap(CheckEvent event) {
        pivot.setAutowrap(event.isChecked());
    }

    private void initControls() {
        // grand totals
        colGrandTotal.setChecked(pivot.isGrandTotalForColumns());
        rowGrandTotal.setChecked(pivot.isGrandTotalForRows());

        // data orientation
        ("column".equals(pivot.getDataFieldOrient()) ? colOrient : rowOrient).setChecked(true);

        pfc.syncModel(); // field control
    }

//    private String renderRawData(Object object, String fname) {
//        if ("Agent".equals(fname) || "Customer".equals(fname)) {
//            String[] names = ((String) object).split(" ", 2);
//            return Character.toUpperCase(names[0].charAt(0)) + ". " + names[1];
//        } else if ("Date".equals(fname)) {
//            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
//            return format.format((Date) object);
//        }
//        return object == null ? "(null)" : object.toString();
//    }

    private Component getPreDefDiv(final CnspPivotConfigurator conf) {
        Button scenarioBtn = new Button(conf.getTitle());
        scenarioBtn.setSclass("predef");
        scenarioBtn.addEventListener("onClick", new EventListener<Event>() {
            @Override
			public void onEvent(Event event) throws Exception {
                loadConfiguration(conf);
            }
        });
        return scenarioBtn;
    }

    private void loadConfiguration(CnspPivotConfigurator conf) {
        pivotModel.clearAllFields(true);
        conf.configure(pivotModel);
        conf.configure(pivot);
        pivot.setPivotRenderer(conf.getRenderer());
        initControls();
    }
}
