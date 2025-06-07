package hms.web.control.zk.mobile.membership;

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
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vlayout;

import com.google.javascript.rhino.jstype.EnumType;

import hms.web.control.zk.common.SwipeDivComposer;
import hms.web.zk.HmsMessageBox;
import hms.web.zk.HmsNotification;
import hms.web.zk.ModelVlayout;
import hms.web.zk.ModelVlayout.ItemRenderer;
import hms_kernel.membership.Entity;
import hms_kernel.membership.MembershipService;
import hms_kernel.membership.type.EntityType;
import legion.BusinessServiceFactory;
import legion.util.DataFO;
import legion.util.DateFormatUtil;
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
	@Wire
	private Datebox dtbBirthDate;
	
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
		//
		ItemRenderer<Entity> renderer = (parent, ett, i) -> {
			Include icd = new Include();

			SwipeDivComposer c = SwipeDivComposer.of(icd);

			Div divContent = c.getDivContent();
			Label lb = new Label(ett.getAlias() + " / " + ett.getTypeName() + " / " + ett.getBirthDateStr());
			lb.setSclass("record-desc");
			divContent.appendChild(lb);

			Button btnR1 = c.getBtnR1();
			btnR1.setLabel("按鈕1");
			btnR1.addEventListener(Events.ON_CLICK,e->HmsNotification.info("施工中..."));
			btnR1.setVisible(false);

			Button btnR2 = c.getBtnR2();
//			btnR2.setLabel("按鈕2");
			btnR2.setIconSclass("fa fa-trash fa-2x");
			btnR2.addEventListener(Events.ON_CLICK,e->{
				HmsMessageBox.confirm("確認刪除家庭成員["+ett.getAlias()+"]?", ()->{
					boolean b = mbrService.deleteEntity(ett.getUid());
					if(b) {
						HmsNotification.info("刪除家庭成員["+ett.getAlias()+"]成功。");
						loadEntities();
					}else {
						HmsNotification.error();
					}
				});
			});
			// TODO
			
			parent.appendChild(icd);
		};
		vlyEntityList.setItemRenderer(renderer);
	}

	@Listen(Events.ON_CLICK+"=#btnAddEntity")
	public void btnAddEntity_clicked() {
		String alias = txbAlias.getValue();
		EntityType type = cbbType.getSelectedItem() == null ? null : cbbType.getSelectedItem().getValue();
		long birthDate =dtbBirthDate.getValue()==null?0:dtbBirthDate.getValue().getTime(); 

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

		Entity ett = mbrService.createEntity(alias, type, birthDate);
		if (ett == null) {
			HmsNotification.error();
			return;
		}
		
		HmsNotification.info("新增成員["+alias+"]["+type.getName()+"]["+DateFormatUtil.transToDate(new Date(birthDate)) +"]成功。");
		loadEntities();
	}
	
	@Listen(Events.ON_CLICK+"=#btnAddEntityClear")
	public void btnAddEntityClear_clicked() {
		txbAlias.setValue("");
		cbbType.setValue("");	
		dtbBirthDate.setValue(null);	
	}
	
	
	public void loadEntities() {
		List<Entity> entities = mbrService.loadEntityList();
		vlyEntityList.setModel(new ListModelList<>(entities));
	}
	
}
