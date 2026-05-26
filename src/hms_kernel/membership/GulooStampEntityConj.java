package hms_kernel.membership;

import hms_kernel.HmsObjectModel;
import hms_kernel.data.BizObjLoader;
import hms_kernel.data.membership.MembershipDataService;
import legion.DataServiceFactory;

public class GulooStampEntityConj extends HmsObjectModel {

	// -------------------------------------------------------------------------------
	// ----------------------------------attributes-----------------------------------
	private String stampUid;
	private String entityUid;

	// -------------------------------------------------------------------------------
	// ----------------------------------constructor----------------------------------
	private GulooStampEntityConj(String stampUid, String entityUid) {
		this.stampUid = stampUid;
		this.entityUid = entityUid;
	}

	static GulooStampEntityConj newInstance(String stampUid, String entityUid) {
		GulooStampEntityConj conj = new GulooStampEntityConj(stampUid, entityUid);
		conj.configNewInstance();
		return conj;
	}

	public static GulooStampEntityConj getInstance(String _uid, String stampUid, String entityUid,
			long _objectCreateTime, long _objectUpdateTime) {
		GulooStampEntityConj conj = new GulooStampEntityConj(stampUid, entityUid);
		conj.configGetInstance(_uid, _objectCreateTime, _objectUpdateTime);
		return conj;
	}

	// -------------------------------------------------------------------------------
	// ---------------------------------getter&setter---------------------------------
	public String getStampUid() {
		return stampUid;
	}

	public void setStampUid(String stampUid) {
		this.stampUid = stampUid;
	}

	public String getEntityUid() {
		return entityUid;
	}

	public void setEntityUid(String entityUid) {
		this.entityUid = entityUid;
	}

	// -------------------------------------------------------------------------------
	// ----------------------------------ObjectModel----------------------------------
	@Override
	protected boolean save() {
		return DataServiceFactory.getInstance().getService(MembershipDataService.class).saveGulooStampEntityConj(this);
	}

	@Override
	public boolean delete() {
		return DataServiceFactory.getInstance().getService(MembershipDataService.class)
				.deleteGulooStampEntityConj(this.getUid());
	}

	// -------------------------------------------------------------------------------
	// -----------------------------GulooStampEntityConj------------------------------
	public static GulooStampEntityConj create(String stampUid, String entityUid) {
		GulooStampEntityConj conj = newInstance(stampUid, entityUid);
		return conj.save() ? conj : null;
	}

	// -------------------------------------------------------------------------------
	private BizObjLoader<GulooStamp> gulooStampLoader = BizObjLoader
			.of(DataServiceFactory.getInstance().getService(MembershipDataService.class)::loadGulooStamp);

	public GulooStamp getGulooStamp() {
		return gulooStampLoader.getObj(getStampUid());
	}

	// -------------------------------------------------------------------------------
	private BizObjLoader<Entity> entityLoader = BizObjLoader
			.of(DataServiceFactory.getInstance().getService(MembershipDataService.class)::loadEntity);

	public Entity getEntity() {
		return entityLoader.getObj(getEntityUid());
	}

}
