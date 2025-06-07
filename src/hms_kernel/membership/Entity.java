package hms_kernel.membership;

import java.util.Date;

import hms_kernel.HmsObjectModel;
import hms_kernel.data.membership.MembershipDataService;
import hms_kernel.membership.type.EntityType;
import legion.DataServiceFactory;
import legion.util.DateFormatUtil;
import legion.util.DateUtil;
import legion.util.NumberFormatUtil;

public class Entity extends HmsObjectModel {

	// -------------------------------------------------------------------------------
	// ----------------------------------attributes-----------------------------------
	private String alias; // 簡稱、暱稱
	private EntityType type;
	private long birthDate;

	// -------------------------------------------------------------------------------
	// ----------------------------------constructor----------------------------------
	private Entity() {
	}

	static Entity newInstance() {
		Entity entity = new Entity();
		entity.configNewInstance();
		return entity;
	}

	public static Entity getInstance(String _uid, long _objectCreateTime, long _objectUpdateTime) {
		Entity entity = new Entity();
		entity.configGetInstance(_uid, _objectCreateTime, _objectUpdateTime);
		return entity;
	}

	// -------------------------------------------------------------------------------
	// ---------------------------------getter&setter---------------------------------
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public EntityType getType() {
		return type;
	}

	public void setType(EntityType type) {
		this.type = type;
	}

	public long getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(long birthDate) {
		this.birthDate = birthDate;
	}

	// -------------------------------------------------------------------------------
	public int getTypeIdx() {
		return (getType() == null ? EntityType.UNDEFINED : getType()).getIdx();
	}
	
	public String getTypeName() {
		return (getType() == null ? EntityType.UNDEFINED : getType()).getName();
	}

	public String getBirthDateStr() {
		return getBirthDate() <= 0 ? "(未指定)" : DateFormatUtil.transToDate(new Date(getBirthDate()));
	}
	
	// -------------------------------------------------------------------------------
	// ----------------------------------ObjectModel----------------------------------
	@Override
	protected boolean save() {
		return DataServiceFactory.getInstance().getService(MembershipDataService.class).saveEntity(this);
	}

	@Override
	protected boolean delete() {
		return DataServiceFactory.getInstance().getService(MembershipDataService.class).deleteEntity(this.getUid());
	}

	// -------------------------------------------------------------------------------
	// ------------------------------------Entity-------------------------------------
	public static Entity create(String _alias, EntityType _type, long _birthDate) {
		Entity ett = newInstance();
		ett.setAlias(_alias);
		ett.setType(_type);
		ett.setBirthDate(_birthDate);
		return ett.save() ? ett : null;
	}

	
	
	// -------------------------------------------------------------------------------
	public String getInfo() {
		return "[" + getAlias() + "][" + getTypeName() + "][" + DateFormatUtil.transToDate(new Date(getBirthDate()))  + "]";
	}
	
}
