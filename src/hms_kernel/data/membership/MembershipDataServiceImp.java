package hms_kernel.data.membership;

import java.util.List;
import java.util.Map;

import hms_kernel.membership.Entity;

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

}
