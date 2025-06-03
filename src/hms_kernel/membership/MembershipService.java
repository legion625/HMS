package hms_kernel.membership;

import java.util.List;

import hms_kernel.membership.type.EntityType;
import legion.BusinessService;

public interface MembershipService extends BusinessService {
	// -------------------------------------------------------------------------------
	// ------------------------------------Entity-------------------------------------
	public Entity createEntity(String _alias, EntityType _type);
	
	public List<Entity> loadEntityList();
}
