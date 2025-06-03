package hms_kernel.data.membership;

import java.util.List;

import hms_kernel.membership.Entity;
import legion.IntegrationService;

public interface MembershipDataService extends IntegrationService {
	// -------------------------------------------------------------------------------
	// ------------------------------------Entity-------------------------------------
	public boolean saveEntity(Entity _ett);

	public boolean deleteEntity(String _uid);

	public Entity loadEntity(String _uid);

	public List<Entity> loadEntityList();

}
