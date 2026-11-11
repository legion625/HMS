package hms_kernel.membership;

import java.util.List;

import hms_kernel.membership.dto.GulooStampCreateObj;
import hms_kernel.membership.type.EntityType;
import legion.BusinessService;

public interface MembershipService extends BusinessService {
	// -------------------------------------------------------------------------------
	// ------------------------------------Entity-------------------------------------
	public Entity createEntity(String _alias, EntityType _type, long _birthDate);

	public boolean deleteEntity(String _uid);

	public List<Entity> loadEntityList();

	// -------------------------------------------------------------------------------
	// ----------------------------------GulooStamp-----------------------------------
	public GulooStamp createGulooStamp(GulooStampCreateObj _dto);
	
	public boolean deleteGulooStamp(String _uid);
	
	public List<GulooStamp> loadGulooStampList();

	/** 全部的咕溜-成員關聯(一次查詢)；畫面需要整批組裝時用，避免逐筆 getEntityList() 的 N+1 查詢 */
	public List<GulooStampEntityConj> loadAllGulooStampEntityConjList();

	/** 全部的咕溜-標籤關聯(一次查詢)；畫面需要整批組裝時用，避免逐筆 getCateList() 的 N+1 查詢 */
	public List<GulooStampCateConj> loadAllGulooStampCateConjList();

	// -------------------------------------------------------------------------------
	// --------------------------------GulooStampCate---------------------------------
	public GulooStampCate createGulooStampCate(String _name, String _color);
	public boolean deleteGulooStampCate(String _uid);
	public boolean updateGulooStampCateColor(String _uid, String _color);
	public List<GulooStampCate> loadGulooStampCateList();
}
