package hms_kernel.membership;

import hms_kernel.HmsObjectModel;
import hms_kernel.data.BizObjLoader;
import hms_kernel.data.membership.MembershipDataService;
import legion.DataServiceFactory;

public class GulooStampCateConj extends HmsObjectModel {

	// -------------------------------------------------------------------------------
	// ----------------------------------attributes-----------------------------------
	private String stampUid;
	private String cateUid;

	// -------------------------------------------------------------------------------
	// ----------------------------------constructor----------------------------------
	private GulooStampCateConj(String stampUid, String cateUid) {
		this.stampUid = stampUid;
		this.cateUid = cateUid;
	}

	static GulooStampCateConj newInstance(String stampUid, String cateUid) {
		GulooStampCateConj conj = new GulooStampCateConj(stampUid, cateUid);
		conj.configNewInstance();
		return conj;
	}

	public static GulooStampCateConj getInstance(String _uid, String stampUid, String cateUid, long _objectCreateTime,
			long _objectUpdateTime) {
		GulooStampCateConj conj = new GulooStampCateConj(stampUid, cateUid);
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

	public String getCateUid() {
		return cateUid;
	}

	public void setCateUid(String cateUid) {
		this.cateUid = cateUid;
	}

	// -------------------------------------------------------------------------------
	// ----------------------------------ObjectModel----------------------------------
	@Override
	protected boolean save() {
		return DataServiceFactory.getInstance().getService(MembershipDataService.class).saveGulooStampCateConj(this);
	}

	@Override
	public boolean delete() {
		return DataServiceFactory.getInstance().getService(MembershipDataService.class)
				.deleteGulooStampCateConj(this.getUid());
	}

	// -------------------------------------------------------------------------------
	// ------------------------------GulooStampCateConj-------------------------------
	public static GulooStampCateConj create(String stampUid, String cateUid) {
		GulooStampCateConj conj = newInstance(stampUid, cateUid);
		return conj.save() ? conj : null;
	}

	// -------------------------------------------------------------------------------
	private BizObjLoader<GulooStamp> gulooStampLoader = BizObjLoader
			.of(DataServiceFactory.getInstance().getService(MembershipDataService.class)::loadGulooStamp);

	public GulooStamp getGulooStamp() {
		return gulooStampLoader.getObj(getStampUid());
	}

	// -------------------------------------------------------------------------------
	private BizObjLoader<GulooStampCate> cateLoader = BizObjLoader
			.of(DataServiceFactory.getInstance().getService(MembershipDataService.class)::loadGulooStampCate);

	public GulooStampCate getCate() {
		return cateLoader.getObj(getCateUid());
	}

}
