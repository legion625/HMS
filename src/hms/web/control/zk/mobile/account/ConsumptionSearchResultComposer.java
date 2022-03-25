package hms.web.control.zk.mobile.account;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import hms.web.zk.HmsMessageBox;
import hms_kernel.account.AccountService;
import hms_kernel.account.Consumption;
import hms_kernel.account.ConsumptionSearchParam;
import legion.util.DataFO;
import legion.util.NumberFormatUtil;

public class ConsumptionSearchResultComposer extends SelectorComposer<Component> {

	@Wire
	private Listbox lbxConsumption;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		init();
		
		// data
		ConsumptionSearchParam param = new ConsumptionSearchParam();
		param.setConsumptionDateStart(LocalDate.now());
		param.setConsumptionDateEnd(LocalDate.now());
		List<Consumption> cnspList = AccountService.getInstance().searchConsumptions(param, true);
		refreshConsumptionContainer(cnspList);
	}

	private void init() {
		/* init lbxConsumption */
		lbxConsumption.setItemRenderer(consumptionListitemRenderer);
	}

	// -------------------------------------------------------------------------------
	@Listen(Events.ON_CLICK + "=#btnDelete")
	public void btnDelete_clicked() {
		ListModelList<Consumption> model = (ListModelList) lbxConsumption.getModel();
		Set<Consumption> tobeDeletedConsumptionSet = model.getSelection();
		if (tobeDeletedConsumptionSet == null || tobeDeletedConsumptionSet.isEmpty()) {
			HmsMessageBox.exclamation("請選取欲刪除的消費。");
			return;
		}

		Consumer<Void> csmDelete = v -> {
			String msg = "";

			Set<Consumption> tempSet = new HashSet<>();

			for (Consumption cnsp : tobeDeletedConsumptionSet) {
				if (!DataFO.isEmptyString(msg))
					msg += "\n";
				msg += "刪除消費[" + cnsp.getDate().toString() + "][" + cnsp.getType().getCategory().getTitle() + "]["
						+ cnsp.getType().getTitle() + "][" + cnsp.getDirection().getTitle() + "]["
						+ NumberFormatUtil.getIntegerString(cnsp.getAmount()) + "][" + cnsp.getDescription() + "]["
						+ cnsp.getPaymentType().getTitle() + "]";

				if (AccountService.getInstance().deleteConsumption(cnsp)) {
					msg += "成功。";
					tempSet.add(cnsp);
				} else {
					msg += "失敗!";
				}
			}

			for (Consumption cnsp : tempSet)
				model.remove(cnsp);
			HmsMessageBox.info(msg);

		};

		HmsMessageBox.confirm("確定刪除所有選取的消費?", csmDelete);
	}
	
	// -------------------------------------------------------------------------------
	private ListitemRenderer<Consumption> consumptionListitemRenderer = (Listitem listitem, Consumption cnsp,
			int index) -> {
		// checkbox
		listitem.appendChild(new Listcell());
		// 消費日期
		listitem.appendChild(new Listcell(cnsp.getDate().toString()));
		// 類型目錄
		listitem.appendChild(new Listcell(cnsp.getType().getCategory().getTitle()));
		// 類型
		listitem.appendChild(new Listcell(cnsp.getType().getTitle()));
		// 流向
		listitem.appendChild(new Listcell(cnsp.getDirection().getTitle()));
		// 消費金額
		listitem.appendChild(new Listcell(NumberFormatUtil.getIntegerString(cnsp.getAmount())));
		// 付款金額
		listitem.appendChild(new Listcell(NumberFormatUtil.getIntegerString(cnsp.getPayedAmount())));
		// 付款方式
		listitem.appendChild(new Listcell(cnsp.getPaymentType().getTitle()));
		// 說明
		listitem.appendChild(new Listcell(cnsp.getDescription()));

		// // 右鍵選單
		// listitem.setValue(cnsp);
		// listitem.addEventListener(Events.ON_RIGHT_CLICK, e -> {
		// miShowPaymentInfo.setAttribute("selectedCnsp", cnsp);
		// mpConsumption.open(listitem);
		// });
	};

	private void refreshConsumptionContainer(List<Consumption> _cnspList) {
		ListModelList<Consumption> model = new ListModelList<>(_cnspList);
		model.setMultiple(true);
		lbxConsumption.setModel(model);
	}

}
