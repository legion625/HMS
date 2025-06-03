package hms.web.control.zk.mobile.membership;

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
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vlayout;

import com.google.javascript.rhino.jstype.EnumType;

import hms.web.zk.HmsMessageBox;
import hms.web.zk.HmsNotification;
import hms.web.zk.ModelVlayout;
import hms.web.zk.ModelVlayout.ItemRenderer;
import hms_kernel.membership.Entity;
import hms_kernel.membership.MembershipService;
import hms_kernel.membership.type.EntityType;
import legion.BusinessServiceFactory;
import legion.util.DataFO;
import legion.util.LogUtil;
import legion.web.zk.ZkUtil;

public class EntityHomeComposer extends SelectorComposer<Component> {
	
	private final static String SRC = "/mobile/membership/entityHome.zul";
	
	public static  EntityHomeComposer  of(Include _icd) {
		return ZkUtil.of(_icd, SRC, "vlyEntityHome");
	}

	// -------------------------------------------------------------------------------
	@Wire
	private Textbox txbAlias;
	@Wire
	private Combobox cbbType;
//	@Wire
//	private Datebox dtbBirthDate;
	
	@Wire
	private ModelVlayout<Entity> vlyEntityList;
	

	private MembershipService mbrService = BusinessServiceFactory.getInstance().getService(MembershipService.class);
	
	
	
//	private EntityService entityService = new EntityServiceImpl(); // 你實作的 service 層

	// -------------------------------------------------------------------------------
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		try {
			super.doAfterCompose(comp);
			init();

		} catch (Throwable e) {
			LogUtil.log(e, Level.ERROR);
		}
	}

	private void init() {
		ZkUtil.initCbb(cbbType, EntityType.values(), true);
// TODO
//		//
//		ItemRenderer<Entity> renderer = (parent, ett, i) -> {
//			Label lb = new Label(ett.getInfo());
//			parent.appendChild(lb);
//		};
//		vlyEntityList.setItemRenderer(renderer);
	}

	@Listen(Events.ON_CLICK+"=#btnAddEntity")
	public void btnAddEntity_clicked() {
		String alias = txbAlias.getValue();
		EntityType type = cbbType.getSelectedItem() == null ? null : cbbType.getSelectedItem().getValue();

		boolean b = true;
		StringBuilder msg = new StringBuilder();
		if(DataFO.isEmptyString(alias)) {
			msg.append("請填寫名稱。").append(System.lineSeparator());
			b = false;
		}
		if(type==null) {
			msg.append("請選擇類型。").append(System.lineSeparator());
			b = false;
		}

		if (!b) {
			return;
		}

		Entity ett = mbrService.createEntity(alias, type);
		if (ett == null) {
			HmsNotification.error();
			return;
		}

//		entityService.insertEntity(new Entity(name, type, note)); TODO
//		HmsMessageBox.exclamation("施工中...");
		
		
		HmsNotification.info("新增成員["+alias+"]["+type.getName()+"]成功。");
		loadEntities();
		
//		nameBox.setValue("");
//		noteBox.setValue("");
//		typeBox.setSelectedItem(null);
	}
	

	public void loadEntities() {
		List<Entity> entities = mbrService.loadEntityList();
		vlyEntityList.setModel(new ListModelList<>(entities));
		
//		entityList.getChildren().clear();
//		List<Entity> entities = entityService.findAllEntities();
		
		// TODO
//		for (Entity e : entities) {
//			Vlayout card = new Vlayout();
////			card.setSclass("record-card");
//
//			Label lbName = new Label(e.getAlias() + " / " + e.getType());
////			lbName.setSclass("record-desc");
//
//			Label lbNote = new Label(e.getTypeName());
////			lbNote.setSclass("record-note");
//
//			Button btnDel = new Button("刪除");
//			btnDel.addEventListener(Events.ON_CLICK, ev -> {
//				// TODO
//				HmsMessageBox.exclamation("施工中...");
////				entityService.deleteEntity(e.getId());
////				loadEntities();
//			});
//
//			card.appendChild(lbName);
//			card.appendChild(lbNote);
//			card.appendChild(btnDel);
//			entityList.appendChild(card);
//		}
	}
	
	
	@Listen(Events.ON_CLICK+"=#btnTest")
	public void btnTest_clicked() {
		HmsNotification.info("btnTest");
	}
}
