package hms_kernel.membership;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hms_kernel.data.account.AccountDataService;
import hms_kernel.data.membership.MembershipDataService;
import hms_kernel.membership.type.EntityType;
import legion.DataServiceFactory;

public class MembershipServiceImp implements MembershipService{

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
	
}
