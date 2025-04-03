package hms_kernel.auth;

import java.time.LocalDateTime;
import java.util.Date;

import hms_kernel.HmsObjectModel;
import hms_kernel.data.auth.AuthDataService;
import legion.kernel.LegionObject;

public class User extends HmsObjectModel {
	// -------------------------------------------------------------------------------
	// -----------------------------------attribute-----------------------------------
	private String name;

	// -------------------------------------------------------------------------------
	// ----------------------------------constructor----------------------------------
	private User() {
	}

	protected static User newInstance(String name) {
		User user = new User();
		user.configNewInstance();
		user.setName(name);
		if (user.save())
			return user;
		else
			return null;
	}

	public static User getInstance(String uid, String name, long oCreateDate, long oUpdateDate) {
		User user = new User();
		user.configGetInstance(uid, oCreateDate, oUpdateDate);
		user.setName(name);
		return user;
	}

	// -------------------------------------------------------------------------------
	// ---------------------------------getter&setter---------------------------------
	public String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}

	// -------------------------------------------------------------------------------
	// ---------------------------------LegionObject----------------------------------
	@Override
	protected boolean save() {
		return AuthDataService.getInstance().saveUser(this);
	}

	@Override
	protected boolean delete() {
		return AuthDataService.getInstance().deleteUser(this.getUid());
	}

	// -------------------------------------------------------------------------------
	public static User getMock() {
		User user = new User();
		user.name="John Dowd";
		return user;
	}
}
