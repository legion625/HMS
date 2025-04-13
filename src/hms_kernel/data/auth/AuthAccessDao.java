package hms_kernel.data.auth;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import hms_kernel.auth.User;

public class AuthAccessDao {

	// TODO
	
	 boolean saveUser(User _user) {
		// TODO Auto-generated method stub
		return false;
	}

	 boolean deleteUser(String _uid) {
		// TODO Auto-generated method stub
		return false;
	}

	 List<User> loadUsers() {
		// TODO Auto-generated method stub
		return null;
	}

	// -------------------------------------------------------------------------------
	// -------------------------------------User--------------------------------------
//	private final static String TABLE_USER = "User";
//
//	@Override
//	public boolean saveUser(User _user) {
//		String qstr = "update " + TABLE_USER;
//		qstr += " set userName=?";
//		qstr += ", oUpdateDate=? where uid = '" + _user.getUid() + "'";
//		try {
//			pstmt = conn.prepareStatement(qstr);
//			pstmt.setString(1, _user.getName());
//			
//			long now = System.currentTimeMillis();
//			System.out.println("now: " + now);
//			pstmt.setLong(2, now);
//			if (pstmt.executeUpdate() == 1)
//				return true;
//			else
//				return createUser(_user);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			System.out.println("DB linking failed!");
//			e.printStackTrace();
//
//		} finally {
//			close();
//		}
//		return false;
//	}
//
//	private boolean createUser(User _user) {
//		String qstr = "insert into " + TABLE_USER + " (";
//		qstr += "uid, userName, oCreateDate, oUpdateDate";
//		qstr += ")";
//		qstr += " values(?,?,?,?)";
//		try {
//			pstmt = conn.prepareStatement(qstr);
//			pstmt.setString(1, _user.getUid());
//			pstmt.setString(2, _user.getName());
//			pstmt.setLong(3, System.currentTimeMillis());
//			pstmt.setLong(4, System.currentTimeMillis());
//			pstmt.executeUpdate();
//			return true;
//		} catch (SQLException e) {
//			System.out.println("DB linking failed!");
//			e.printStackTrace();
//		} finally {
//			close();
//		}
//		return false;
//	}
//
//	@Override
//	public boolean deleteUser(String _uid) {
//		String qstr = "delete from " + TABLE_USER + " where uid='" + _uid + "'";
//		try {
//			pstmt = conn.prepareStatement(qstr);
//			System.out.println("pstmt.executeUpdate(): " + pstmt.executeUpdate());
//			return true;
//		} catch (SQLException e) {
//			System.out.println("DB linking failed!");
//			e.printStackTrace();
//		} finally {
//			close();
//		}
//		return false;
//	}
//
//	private void packUser(ResultSet _rs, User _user) {
//		
//	}
//	
//	@Override
//	public List<User> loadUsers() {
//		List<User> list = new ArrayList<>();
//		String qstr = "select * from " + TABLE_USER;
//		try {
//			pstmt = conn.prepareStatement(qstr);
//			rs = pstmt.executeQuery();
//			while (rs.next()) {
//				User user = User.getInstance(rs.getString("uid"), rs.getString("userName"),
//						new Date(rs.getLong("oCreateDate")), new Date(rs.getLong("oUpdateDate")));
//				list.add(user);
//			}
//
//		} catch (SQLException e) {
//			System.out.println("DB linking failed!");
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//			close();
//		}
//		return list;
//	}
}
