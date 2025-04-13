package hms_kernel.data.auth;

import java.util.List;
import java.util.Map;

import hms_kernel.auth.User;

public class AuthDataServiceImp implements AuthDataService {

	private String source;
	
	// dao
	private AuthAccessDao authAccessDao;
	
	@Override
	public void register(Map<String, String> _params) {
		if (_params == null || _params.isEmpty())
			return;

		source = _params.get("source");

		// dao
//		authAccessDao = new AuthAccessDao(source);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	// -------------------------------------------------------------------------------
	// -------------------------------------User--------------------------------------
	@Override
	public boolean saveUser(User _user) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteUser(String _uid) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<User> loadUsers() {
		// TODO Auto-generated method stub
		return null;
	}

}
