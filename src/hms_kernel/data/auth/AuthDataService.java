package hms_kernel.data.auth;

import java.util.List;

import hms_kernel.auth.User;
import legion.IntegrationService;

public interface AuthDataService extends IntegrationService {

	// -------------------------------------------------------------------------------
	// -------------------------------------User--------------------------------------
	public boolean saveUser(User _user);

	public boolean deleteUser(String _uid);

	public List<User> loadUsers();
}
