package hms_kernel.membership;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import hms_kernel.HmsObjectModel;
import hms_kernel.data.BizObjLoader;
import hms_kernel.data.membership.MembershipDataService;
import hms_kernel.membership.dto.GulooStampCreateObj;
import legion.DataServiceFactory;
import legion.util.DataFO;
import legion.util.DateFormatUtil;

public class GulooStamp extends HmsObjectModel {

	// -------------------------------------------------------------------------------
	// ----------------------------------attributes-----------------------------------
	private long stampDate; // 日期
	private String desp; // 簡述
	private String remark; // 備註

//	//
//	private boolean assignStampCate; // 是否已指定分類
//	private String stampCateUid; // 分類uid
	
	// 對應的分類標籤(stampTag)用關連物件紀錄。
	
	// 對應的家庭成員(entity)用關連物件紀錄。
	

	// -------------------------------------------------------------------------------
	// ----------------------------------constructor----------------------------------
	private GulooStamp() {
	}

	static GulooStamp newInstance() {
		GulooStamp s = new GulooStamp();
		s.configNewInstance();
		return s;
	}

	public static GulooStamp getInstance(String _uid, long _objectCreateTime, long _objectUpdateTime) {
		GulooStamp s = new GulooStamp();
		s.configGetInstance(_uid, _objectCreateTime, _objectUpdateTime);
		return s;
	}

	// -------------------------------------------------------------------------------
	// ---------------------------------getter&setter---------------------------------
	public long getStampDate() {
		return stampDate;
	}

	public void setStampDate(long stampDate) {
		this.stampDate = stampDate;
	}

	public String getDesp() {
		return desp;
	}

	public void setDesp(String desp) {
		this.desp = desp;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

//	public boolean isAssignStampCate() {
//		return assignStampCate;
//	}
//
//	public void setAssignStampCate(boolean assignStampCate) {
//		this.assignStampCate = assignStampCate;
//	}
//
//	public String getStampCateUid() {
//		return stampCateUid;
//	}
//
//	public void setStampCateUid(String stampCateUid) {
//		this.stampCateUid = stampCateUid;
//	}

	// -------------------------------------------------------------------------------
	public String getStampDateStr() {
		return getStampDate() <= 0 ? "(未指定)" : DateFormatUtil.transToDate(new Date(getStampDate()));
	}
	
	// -------------------------------------------------------------------------------
	// ----------------------------------ObjectModel----------------------------------
	@Override
	protected boolean save() {
		return DataServiceFactory.getInstance().getService(MembershipDataService.class).saveGulooStamp(this);
	}

	@Override
	public boolean delete() {
		return DataServiceFactory.getInstance().getService(MembershipDataService.class)
				.deleteGulooStamp(this.getUid());
	}

	// -------------------------------------------------------------------------------
	// ----------------------------------GulooStamp-----------------------------------
	public static GulooStamp create(GulooStampCreateObj _dto) {
		GulooStamp s = newInstance();
		s.setStampDate(_dto.getStampDate());
		s.setDesp(_dto.getDesp());
		s.setRemark(_dto.getRemark());
//		s.setAssignStampCate(false); // 未指定
//		s.setStampCateUid(""); // 未指定
		return s.save() ? s : null; 
	}

//	// -------------------------------------------------------------------------------
//	public boolean assignStampCate(String _stampCateUid) {
//		setAssignStampCate(!DataFO.isEmptyString(_stampCateUid));
//		setStampCateUid(_stampCateUid);
//		return save();
//	}
	
	// -------------------------------------------------------------------------------
	private BizObjLoader<List<GulooStampEntityConj>> entityConjListLoader = BizObjLoader
			.of(DataServiceFactory.getInstance().getService(MembershipDataService.class)::loadGulooStampEntityConjList);
	
	public List<GulooStampEntityConj> getEntityConjList(){
		return entityConjListLoader.getObj(getUid());
	}
	
	public List<Entity> getEntityList(){
		return getEntityConjList().stream().map(GulooStampEntityConj::getEntity).collect(Collectors.toList());
	}
	
	// -------------------------------------------------------------------------------
	private BizObjLoader<List<GulooStampCateConj>> cateConjListLoader = BizObjLoader
			.of(DataServiceFactory.getInstance().getService(MembershipDataService.class)::loadGulooStampCateConjList);
	
	public List<GulooStampCateConj> getCateConjList(){
		return cateConjListLoader.getObj(getUid());
	}
	
	public List<GulooStampCate> getCateList(){
		return getCateConjList().stream().map(GulooStampCateConj::getCate).collect(Collectors.toList());
	}

}
