package hms_kernel.auth;

public class AuthService {
	// -------------------------------------------------------------------------------
	private final static AuthService INSTANCE = new AuthService();
	// -------------------------------------------------------------------------------
	private AuthService() {}
	public static AuthService getInstance() {
		return INSTANCE;
	}
	
	// -------------------------------------------------------------------------------
	// -------------------------------------User--------------------------------------
	public User createUser(String _name) {
		return User.newInstance(_name);

	}
	

}
