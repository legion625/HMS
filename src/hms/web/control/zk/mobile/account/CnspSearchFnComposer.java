package hms.web.control.zk.mobile.account;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.event.Level;
import org.zkoss.util.logging.Log;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vlayout;

import hms.util.ZKUtil;
import hms.web.control.zk.account.GridAddCnspComposer;
import hms.web.zk.ModelVlayout;
import hms.web.zk.ModelVlayout.ItemRenderer;
import hms_kernel.account.AccountService;
import hms_kernel.account.Consumption;
import hms_kernel.account.ConsumptionSearchParam;
import hms_kernel.account.DirectionEnum;
import hms_kernel.account.PaymentTypeEnum;
import hms_kernel.account.TypeCategoryEnum;
import hms_kernel.account.TypeEnum;
import legion.BusinessServiceFactory;
import legion.util.DataFO;
import legion.util.DateFormatUtil;
import legion.util.DateUtil;
import legion.util.LogUtil;
import legion.util.NumberFormatUtil;
import legion.web.zk.ZkUtil;

public class CnspSearchFnComposer  extends SelectorComposer<Component>{
	
	private final static String SRC = "/mobile/account/cnspSearchFn.zul";

	public static CnspSearchFnComposer of(Include _icd) {
		return ZkUtil.of(_icd, SRC, "vlyCnspSearchFn");
	}

	@Wire
	private Combobox cbbRecentCnspPeriod;
	@Wire
	private Combobox cbbTypeCate;

	@Wire 
	private Button btnAdvancedFilter;
	@Wire
	private Vlayout vlyAdvancedFilter;
	@Wire
	private Textbox txbDesp;
	@Wire
	private Combobox cbbPmType, cbbDirection;

	@Wire
	private ModelVlayout<Consumption> vlyCnspList;

	// -------------------------------------------------------------------------------
	@Override
	public void doAfterCompose(Component comp) {
		try {
			super.doAfterCompose(comp);
			init();

		} catch (Throwable e) {
			LogUtil.log(e, Level.ERROR);
		}
	}
	
	private void init() {
		
		ZKUtil.configureConsumptionTypeCategory(cbbTypeCate, true);
		ZkUtil.initCbb(cbbPmType, PaymentTypeEnum.values(false), true);
		ZkUtil.initCbb(cbbDirection, DirectionEnum.values(false), true);
		
		//
		ItemRenderer<Consumption> renderer =  (Vlayout parent, Consumption cnsp, int i) -> {
			Vlayout card = new Vlayout();
            card.setSclass("record-card");

//			Label lbCnspDate = new Label(DateFormatUtil.transToDate(DateUtil.toDate(cnsp.getDate()))); FIXME 這個轉換有錯。
            Label lbCnspDate = new Label(cnsp.getDate().toString());
			lbCnspDate.setSclass("record-time");
			card.appendChild(lbCnspDate);

            Label lbDesp = new Label(cnsp.getDescription()+" / "+cnsp.getType().getCategory().getTitle()+"-" +cnsp.getType().getName());
            lbDesp.setSclass("record-desc");
            card.appendChild(lbDesp);

			Hlayout bottom = new Hlayout();
			bottom.setSclass("record-bottom");

//			Label lbAmount = new Label(NumberFormatUtil.getIntegerString(cnsp.getAmount())+"  "+ cnsp.getDirection().getName());
			Label lbAmount = new Label(NumberFormatUtil.getIntegerString(cnsp.getAmount()));
			if (DirectionEnum.OUT == cnsp.getDirection())
				lbAmount.setSclass("record-amount-out");
			else if (DirectionEnum.IN == cnsp.getDirection() || DirectionEnum.IN_ADV == cnsp.getDirection())
				lbAmount.setSclass("record-amount-in");
			bottom.appendChild(lbAmount);

            Label lbPmType = new Label(cnsp.getPaymentType().getName()+"  "+ cnsp.getDirection().getName());
            bottom.appendChild(lbPmType);
            card.appendChild(bottom);
            parent.appendChild(card);
			};
		vlyCnspList.setItemRenderer(renderer);
	}

	
	// -------------------------------------------------------------------------------
	@Listen(Events.ON_CLICK + "=#btnAdvancedFilter")
	public void btnAdvancedFilter_clicked() {
		if (vlyAdvancedFilter.isVisible())
			hideAdvancedFilter();
		else
			showAdvancedFilter();
	}

	private void showAdvancedFilter() {
		vlyAdvancedFilter.setVisible(true);
		btnAdvancedFilter.setIconSclass("fa fa-arrow-up");
		btnAdvancedFilter.setLabel("收合條件");
	}

	private void hideAdvancedFilter() {
		vlyAdvancedFilter.setVisible(false);
		btnAdvancedFilter.setIconSclass("fa fa-arrow-down");
		btnAdvancedFilter.setLabel("更多條件");
	}

	// -------------------------------------------------------------------------------
	public void defaultSearch() {
		cbbRecentCnspPeriod.setSelectedIndex(0);
		btnSearch_clicked();
	}
	
	@Listen(Events.ON_CLICK + "=#btnSearch")
	public void btnSearch_clicked() {
		AccountService acntService = BusinessServiceFactory.getInstance().getService(AccountService.class);

		ConsumptionSearchParam param = new ConsumptionSearchParam();

		/* conditions */
		//
		LocalDate nowDate = LocalDate.now();
		switch (cbbRecentCnspPeriod.getSelectedIndex()) {
		case 0: // 當日
			param.setConsumptionDateStart(nowDate);
			break;
		case 1: // 近3天
			param.setConsumptionDateStart(nowDate.minusDays(3));
			break;
		case 2: // 近1週
			param.setConsumptionDateStart(nowDate.minusWeeks(1));
			break;
		case 3: // 近1月
			param.setConsumptionDateStart(nowDate.minusMonths(1));
			break;
		default: // 不限
			break;
		}

		//
		if (cbbTypeCate.getSelectedItem() != null && cbbTypeCate.getSelectedItem().getValue()!=null) {
			TypeCategoryEnum typeCate = cbbTypeCate.getSelectedItem().getValue();
			List<TypeEnum> typeList = new ArrayList<>();
			for (TypeEnum type : acntService.getTypes(typeCate, false))
				typeList.add(type);
			param.setTypeList(typeList);

		}

		if (vlyAdvancedFilter.isVisible()) {
			// 消費說明
			String desp = txbDesp.getValue();
			if (!DataFO.isEmptyString(desp)) {
				desp = "%" + desp + "%";
				param.setDescription(desp);
			}
			// 付款方式
			if (cbbPmType.getSelectedItem() != null &&  cbbPmType.getSelectedItem().getValue()!=null) {
				List<PaymentTypeEnum> list = new ArrayList<>();
				list.add(cbbPmType.getSelectedItem().getValue());
				param.setPaymentTypeList(list);
			}
			
			// 流向
			if (cbbDirection.getSelectedItem() != null &&  cbbDirection.getSelectedItem().getValue()!=null) {
				param.setDirection(cbbDirection.getSelectedItem().getValue());
			}
		}
		

		/* search */
		// data
		List<Consumption> cnspList = acntService.searchConsumptions(param, false);
		cnspList = cnspList.stream().sorted(Comparator.comparing(Consumption::getDate).thenComparing(Consumption::getObjectCreateTime).reversed())
				.collect(Collectors.toList());

		// 轉成model
		vlyCnspList.setModel(new ListModelList<>(cnspList));
	}
	
	// -------------------------------------------------------------------------------
}
