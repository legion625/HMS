package hms_kernel.membership;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hms_kernel.data.membership.MembershipDataService;
import hms_kernel.membership.dto.GulooStampCreateObj;
import hms_kernel.membership.type.EntityType;
import legion.DataServiceFactory;

public class MembershipServiceImp implements MembershipService {

	private Logger log = LoggerFactory.getLogger(MembershipServiceImp.class);

	private static MembershipDataService dataService;

	@Override
	public void register(Map<String, String> _params) {
		dataService = DataServiceFactory.getInstance().getService(MembershipDataService.class);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	// -------------------------------------------------------------------------------
	// ------------------------------------Entity-------------------------------------
	@Override
	public Entity createEntity(String _alias, EntityType _type, long _birthDate) {
		return Entity.create(_alias, _type, _birthDate);
	}

	@Override
	public boolean deleteEntity(String _uid) {
		Entity ett = dataService.loadEntity(_uid);
		if (ett == null) {
			log.error("ett null.");
			return false;
		}
		return ett.delete();
	}

	@Override
	public List<Entity> loadEntityList() {
		return dataService.loadEntityList();
	}

	// -------------------------------------------------------------------------------
	// ----------------------------------GulooStamp-----------------------------------
	@Override
	public GulooStamp createGulooStamp(GulooStampCreateObj _dto) {
		return GulooStamp.create(_dto);
	}

	@Override
	public boolean deleteGulooStamp(String _uid) {
		GulooStamp gs = dataService.loadGulooStamp(_uid);
		if (gs == null) {
			log.error("GulooStamp null.");
			return false;
		}
		return gs.delete();
	}

	@Override
	public List<GulooStamp> loadGulooStampList() {
		return dataService.loadGulooStampList();
	}

	// -------------------------------------------------------------------------------
	// --------------------------------GulooStampCate---------------------------------
	@Override
	public GulooStampCate createGulooStampCate(String _name, String _color) {
		return GulooStampCate.create(_name, _color);
	}

	@Override
	public boolean deleteGulooStampCate(String _uid) {
		GulooStampCate gsc = dataService.loadGulooStampCate(_uid);
		if (gsc == null) {
			log.error("GulooStampCate null.");
			return false;
		}
		return gsc.delete();
	}

	@Override
	public List<GulooStampCate> loadGulooStampCateList() {
		return dataService.loadGulooStampCateList();
	}

}
