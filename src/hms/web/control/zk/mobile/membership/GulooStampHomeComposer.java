package hms.web.control.zk.mobile.membership;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.event.Level;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vlayout;

import hms.membership.bpu.GulooStampBuilder1;
import hms.membership.bpu.MbrBpuType;
import hms.web.control.zk.common.SwipeDivComposer;
import hms.web.zk.HmsMessageBox;
import hms.web.zk.HmsNotification;
import hms.web.zk.ModelHlayout;
import hms.web.zk.ModelHlayout.ModelHlayoutItemRenderer;
import hms.web.zk.ModelVlayout;
import hms.web.zk.ModelVlayout.ModelVlayoutItemRenderer;
import hms_kernel.membership.Entity;
import hms_kernel.membership.GulooStamp;
import hms_kernel.membership.GulooStampCate;
import hms_kernel.membership.MembershipService;
import hms_kernel.membership.dto.GulooStampCreateObj;
import legion.BusinessServiceFactory;
import legion.biz.BpuFacade;
import legion.util.DataFO;
import legion.util.DateFormatUtil;
import legion.util.LogUtil;
import legion.util.TimeTraveler;
import legion.web.zk.ZkUtil;

public class GulooStampHomeComposer extends SelectorComposer<Component> {
	private final static String SRC = "/mobile/membership/gulooStampHome.zul";

	public static GulooStampHomeComposer of(Include _icd) {
		return ZkUtil.of(_icd, SRC, "vlyGulooStampHome");
	}

	// -------------------------------------------------------------------------------
	/* Create */
	@Wire
	private Datebox dtbStampDate;

	@Wire
	private Textbox txbDesp, txbRemark;
//	@Wire
//	private Combobox cbbStampCate;

	/* Entity */
	@Wire
	private Combobox cbbEntity;
	@Wire
	private ModelHlayout<Entity> hlyEntitiesAppended;
	private List<Entity> entitiesAppended = new ArrayList<>();

	/* GulooStampCate */
	@Wire
	private Combobox cbbGsc;
	@Wire
	private ModelHlayout<GulooStampCate> hlyCatesAppended;
	private List<GulooStampCate> catesAppended = new ArrayList<>();

	/* list */
	@Wire
	private ModelVlayout<GulooStamp> vlyGulooStampList;

	// -------------------------------------------------------------------------------
	private MembershipService mbrService = BusinessServiceFactory.getInstance().getService(MembershipService.class);

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
		/* AddGulooStamp -> entity */
		cbbEntity.setItemRenderer((cbi, data, i) -> {
			Entity entity = (Entity) data;
			cbi.setLabel(entity.getAlias());
			cbi.setValue(entity);
		});
		cbbEntity.setModel(new ListModelList<>(mbrService.loadEntityList()));
		ModelHlayoutItemRenderer<Entity> entitiesAppendedRenderer = (parent, entity, i) -> {
			Label lb = new Label(entity.getAlias());
			lb.setClass("z-toolbarbutton");
			lb.setStyle("padding: 2px 3px");
			lb.addEventListener(Events.ON_DOUBLE_CLICK, evt -> {
				entitiesAppended.remove(entity);
				hlyEntitiesAppended.setModel(new ListModelList<>(entitiesAppended));
			});
			parent.appendChild(lb);
		};
		hlyEntitiesAppended.setItemRenderer(entitiesAppendedRenderer);

		/* AddGulooStamp -> cate */
		cbbGsc.setItemRenderer((cbi, data, i) -> {
			GulooStampCate cate = (GulooStampCate) data;
			cbi.setLabel(cate.getName());
			cbi.setValue(cate);
		});
		cbbGsc.setModel(new ListModelList<>(mbrService.loadGulooStampCateList()));
		ModelHlayoutItemRenderer<GulooStampCate> catesAppendedRenderer = (parent, cate, i) -> {
			Label lb = new Label(cate.getName());
			lb.setClass("z-toolbarbutton");
			lb.setStyle("padding: 2px 3px");
			lb.addEventListener(Events.ON_DOUBLE_CLICK, evt -> {
				catesAppended.remove(cate);
				hlyCatesAppended.setModel(new ListModelList<>(catesAppended));
			});
			parent.appendChild(lb);
		};
		hlyCatesAppended.setItemRenderer(catesAppendedRenderer);

		/* GulooStamp */
		ModelVlayoutItemRenderer<GulooStamp> renderer = (parent, gs, i) -> {
			Include icd = new Include();

			SwipeDivComposer c = SwipeDivComposer.of(icd);

			Div divContent = c.getDivContent();
			Vlayout vly = new Vlayout();
			
			/**/
			// 日期、標述
			Hlayout hly = new Hlayout();
//			Label lb = new Label(gs.getStampDateStr() + " / " + gs.getDesp() + " / " + "[category]");
			Label lb = new Label(gs.getStampDateStr() + " / " + gs.getDesp());
			lb.setSclass("record-desc");
			hly.appendChild(lb);
			//
			List<GulooStampCate> cateList = gs.getCateList();
			for (GulooStampCate cate : cateList) {
				hly.appendChild(new Label(cate.getName()));
			}
			//			
			vly.appendChild(hly);

			/**/
			Hlayout hlyBottom = new Hlayout();
			List<Entity> entityList = gs.getEntityList();
			for (Entity entity : entityList) {
				hlyBottom.appendChild(new Label(entity.getAlias()));
			}

			String remark = gs.getRemark();
			if (!DataFO.isEmptyString(remark))
				hlyBottom.appendChild(new Label(remark));

			vly.appendChild(hlyBottom);

			/**/
			divContent.appendChild(vly);

			/**/
			Button btnR1 = c.getBtnR1();
			btnR1.setIconSclass("fa fa-trash fa-2x");
			btnR1.addEventListener(Events.ON_CLICK, e -> {
				HmsMessageBox.confirm("確認刪除咕溜[" + gs.getDesp() + "]?", () -> {
					boolean b = mbrService.deleteGulooStamp(gs.getUid());
					if (b) {
						HmsNotification.info("刪除咕溜[" + gs.getDesp() + "]成功。");
						loadGulooStamps();
					} else {
						HmsNotification.error();
					}
				});
			});

			btnR1.setVisible(true);

			//
			Button btnR2 = c.getBtnR2();
			btnR2.setVisible(false);

			parent.appendChild(icd);
		};
		vlyGulooStampList.setItemRenderer(renderer);

	}

	@Wire
	private Groupbox gbxCreateArea;

	@Listen(Events.ON_CLICK + "=#btnShowCreateArea")
	public void btnShowCreateArea_clicked() {
		gbxCreateArea.setOpen(!gbxCreateArea.isOpen());
	}

	@Listen(Events.ON_CLICK + "=#btnAppendEntity")
	public void btnAppendEntity_clicked() {
		if (cbbEntity.getSelectedItem() == null) {
			HmsNotification.warning("請選取選單中的成員。");
			return;
		}
		Entity entity = cbbEntity.getSelectedItem().getValue();
		entitiesAppended.add(entity);
		hlyEntitiesAppended.setModel(new ListModelList<>(entitiesAppended));
	}

	@Listen(Events.ON_CLICK + "=#btnAppendCate")
	public void btnAppendCate_clicked() {
		if (cbbGsc.getSelectedItem() == null) {
			HmsNotification.warning("請選取選單中的標籤。");
			return;
		}
		GulooStampCate cate = cbbGsc.getSelectedItem().getValue();
		catesAppended.add(cate);
		hlyCatesAppended.setModel(new ListModelList<>(catesAppended));
	}

	@Listen(Events.ON_CLICK + "=#btnAddGulooStamp")
	public void btnAddGulooStamp_clicked() {

		//
		GulooStampBuilder1 bpu = BpuFacade.getInstance().getBuilder(MbrBpuType.GulooStamp$Create1);
		if (bpu == null) {
			HmsNotification.error();
			return;
		}

		long stampDate = dtbStampDate.getValue() == null ? 0 : dtbStampDate.getValue().getTime();
		String desp = txbDesp.getValue();
		String remark = txbRemark.getValue();
		bpu.appendStampDate(stampDate);
		bpu.appendDesp(desp);
		bpu.appendRemark(remark);

		//
		bpu.appendEntityList(entitiesAppended);
		bpu.appendCateList(catesAppended);

		//
		StringBuilder msg = new StringBuilder();
		if (!bpu.verify(msg)) {
			HmsMessageBox.exclamation(msg.toString());
			return;
		}

		//
		GulooStamp gs = bpu.build(new StringBuilder(), null);
		if (gs == null) {
			HmsNotification.error();
			return;
		}

		HmsNotification.info("咕溜[" + gs.getStampDateStr() + "][" + gs.getDesp() + "]成功。");
		loadGulooStamps();
	}

	@Listen(Events.ON_CLICK + "=#btnAddGulooStampClear")
	public void btnAddGulooStampClear_clicked() {

		dtbStampDate.setValue(null);
		txbDesp.setValue("");
		txbRemark.setValue("");
		
		//
		cbbEntity.setValue("");
		entitiesAppended.clear();
		hlyEntitiesAppended.setModel(new ListModelList<>());
		//
		cbbGsc.setValue("");
		catesAppended.clear();
		hlyCatesAppended.setModel(new ListModelList<>());

	}

	public void loadGulooStamps() {
		List<GulooStamp> list = mbrService.loadGulooStampList();
		vlyGulooStampList.setModel(new ListModelList<>(list));
	}

}
