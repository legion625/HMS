package hms_kernel.data.membership;

import java.util.List;
import java.util.Map;

import hms_kernel.membership.Entity;
import hms_kernel.membership.GulooStamp;
import hms_kernel.membership.GulooStampCate;
import hms_kernel.membership.GulooStampCateConj;
import hms_kernel.membership.GulooStampEntityConj;

public class MembershipDataServiceImp implements MembershipDataService {

	private String source;

	// dao
	private MembershipDao mbrDao;

	@Override
	public void register(Map<String, String> _params) {
		if (_params == null || _params.isEmpty())
			return;

		source = _params.get("source");

		// dao
		mbrDao = new MembershipDao(source);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	// -------------------------------------------------------------------------------
	// ------------------------------------Entity-------------------------------------
	@Override
	public boolean saveEntity(Entity _ett) {
		return mbrDao.saveEntity(_ett);
	}

	@Override
	public boolean deleteEntity(String _uid) {
		return mbrDao.deleteEntity(_uid);
	}

	@Override
	public Entity loadEntity(String _uid) {
		return mbrDao.loadEntity(_uid);
	}

	@Override
	public List<Entity> loadEntityList() {
		return mbrDao.loadEntityList();
	}

	// -------------------------------------------------------------------------------
	// ----------------------------------GulooStamp-----------------------------------
	@Override
	public boolean saveGulooStamp(GulooStamp _gs) {
		return mbrDao.saveGulooStamp(_gs);
	}

	@Override
	public boolean deleteGulooStamp(String _uid) {
		return mbrDao.deleteGulooStamp(_uid);
	}

	@Override
	public GulooStamp loadGulooStamp(String _uid) {
		return mbrDao.loadGulooStamp(_uid);
	}

	@Override
	public List<GulooStamp> loadGulooStampList() {
		return mbrDao.loadGulooStampList();
	}

	// -------------------------------------------------------------------------------
	// -----------------------------GulooStampEntityConj------------------------------
	@Override
	public boolean saveGulooStampEntityConj(GulooStampEntityConj _gsEntityConj) {
		return mbrDao.saveGulooStampEntityConj(_gsEntityConj);
	}

	@Override
	public boolean deleteGulooStampEntityConj(String _uid) {
		return mbrDao.deleteGulooStampEntityConj(_uid);
	}

	@Override
	public GulooStampEntityConj loadGulooStampEntityConj(String _uid) {
		return mbrDao.loadGulooStampEntityConj(_uid);
	}

	@Override
	public List<GulooStampEntityConj> loadGulooStampEntityConjList(String _gsUid) {
		return mbrDao.loadGulooStampEntityConjList(_gsUid);
	}

	@Override
	public List<GulooStampEntityConj> loadGulooStampEntityConjListByEntity(String _entityUid) {
		return mbrDao.loadGulooStampEntityConjListByEntity(_entityUid);
	}

	// -------------------------------------------------------------------------------
	// --------------------------------GulooStampCate---------------------------------
	@Override
	public boolean saveGulooStampCate(GulooStampCate _gsc) {
		return mbrDao.saveGulooStampCate(_gsc);
	}

	@Override
	public boolean deleteGulooStampCate(String _uid) {
		return mbrDao.deleteGulooStampCate(_uid);
	}

	@Override
	public GulooStampCate loadGulooStampCate(String _uid) {
		return mbrDao.loadGulooStampCate(_uid);
	}

	@Override
	public List<GulooStampCate> loadGulooStampCateList() {
		return mbrDao.loadGulooStampCateList();
	}

	// -------------------------------------------------------------------------------
	// ------------------------------GulooStampCateConj-------------------------------
	@Override
	public boolean saveGulooStampCateConj(GulooStampCateConj _gscc) {
		return mbrDao.saveGulooStampCateConj(_gscc);
	}

	@Override
	public boolean deleteGulooStampCateConj(String _uid) {
		return mbrDao.deleteGulooStampCateConj(_uid);
	}

	@Override
	public GulooStampCateConj loadGulooStampCateConj(String _uid) {
		return mbrDao.loadGulooStampCateConj(_uid);
	}

	@Override
	public List<GulooStampCateConj> loadGulooStampCateConjList(String _gsUid) {
		return mbrDao.loadGulooStampCateConjList(_gsUid);
	}

	@Override
	public List<GulooStampCateConj> loadGulooStampCateConjListByCate(String _cateUid) {
		return mbrDao.loadGulooStampCateConjListByCate(_cateUid);
	}

}
