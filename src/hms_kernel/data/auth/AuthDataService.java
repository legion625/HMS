package hms_kernel.data.auth;

import java.util.List;

import hms_kernel.auth.User;

public interface AuthDataService {
	public static AuthDataService getInstance() {
		return AuthAccessDao.getInstance();
	}
	// -------------------------------------------------------------------------------
	// -------------------------------------User--------------------------------------
	public boolean saveUser(User _user);
	public boolean deleteUser(String _uid);
	public List<User> loadUsers();
}
