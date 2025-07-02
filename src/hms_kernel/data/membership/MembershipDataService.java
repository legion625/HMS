package hms_kernel.data.membership;

import java.util.List;

import hms_kernel.membership.Entity;
import hms_kernel.membership.GulooStamp;
import hms_kernel.membership.GulooStampCate;
import hms_kernel.membership.GulooStampCateConj;
import hms_kernel.membership.GulooStampEntityConj;
import legion.IntegrationService;

public interface MembershipDataService extends IntegrationService {
	
	// -------------------------------------------------------------------------------
	// ------------------------------------Entity-------------------------------------
	public boolean saveEntity(Entity _ett);

	public boolean deleteEntity(String _uid);

	public Entity loadEntity(String _uid);

	public List<Entity> loadEntityList();

	// -------------------------------------------------------------------------------
	// ----------------------------------GulooStamp-----------------------------------
	public boolean saveGulooStamp(GulooStamp _gs);

	public boolean deleteGulooStamp(String _uid);

	public GulooStamp loadGulooStamp(String _uid);

	public List<GulooStamp> loadGulooStampList();

	// -------------------------------------------------------------------------------
	// -----------------------------GulooStampEntityConj------------------------------
	public boolean saveGulooStampEntityConj(GulooStampEntityConj _gsEntityConj);

	public boolean deleteGulooStampEntityConj(String _uid);

	public GulooStampEntityConj loadGulooStampEntityConj(String _uid);

	public List<GulooStampEntityConj> loadGulooStampEntityConjList(String _gsUid);

	public List<GulooStampEntityConj> loadGulooStampEntityConjListByEntity(String _entityUid);

	// -------------------------------------------------------------------------------
	// --------------------------------GulooStampCate---------------------------------
	public boolean saveGulooStampCate(GulooStampCate _gsc);

	public boolean deleteGulooStampCate(String _uid);

	public GulooStampCate loadGulooStampCate(String _uid);

	public List<GulooStampCate> loadGulooStampCateList();
	
	// -------------------------------------------------------------------------------
	// ------------------------------GulooStampCateConj-------------------------------
	public boolean saveGulooStampCateConj(GulooStampCateConj _gscc);
	public boolean deleteGulooStampCateConj(String _uid);
	public GulooStampCateConj loadGulooStampCateConj(String _uid);
	public List<GulooStampCateConj> loadGulooStampCateConjList(String _gsUid);
	public List<GulooStampCateConj> loadGulooStampCateConjListByCate(String _cateUid);

}
