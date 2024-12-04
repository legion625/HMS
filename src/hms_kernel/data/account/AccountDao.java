package hms_kernel.data.account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import hms_kernel.account.Bank;
import hms_kernel.account.CardIssuer;
import hms_kernel.account.Consumption;
import hms_kernel.account.ConsumptionSearchParam;
import hms_kernel.account.CreditCard;
import hms_kernel.account.DirectionEnum;
import hms_kernel.account.Payment;
import hms_kernel.account.PaymentTypeEnum;
import hms_kernel.account.TypeEnum;
import hms_kernel.data.DataSource;
import legion.data.MySqlDao;
import legion.data.MySqlDataSource;
import legion.util.DataFO;
import legion.util.JsonUtil;

public class AccountDao extends MySqlDao implements AccountDataService {
	private static MySqlDataSource ds = DataSource.getDataSource();

	protected AccountDao() {
		super(ds);
	}

	private final static AccountDao INSTANCE = new AccountDao();

	protected final static AccountDao getInstance() {
		return INSTANCE;
	}

	// -----------------------------------------------------------
	// ------------------------Consumption------------------------
	private final static String TABLE_CONSUMPTION = "Consumption";
	private final static String COL_CONSUMPTION_TYPE_INDEX = "type_index";
	private final static String COL_CONSUMPTION_DIRECTION_INDEX = "direction_index";
	private final static String COL_CONSUMPTION_AMOUNT = "amount";
	private final static String COL_CONSUMPTION_DESCRIPTION = "description";
	private final static String COL_CONSUMPTION_PAYMENT_TYPE_INDEX = "payment_type_index";
	private final static String COL_CONSUMPTION_DATE = "date";

	@Override
	public boolean saveConsumption(Consumption _cnsp) {
		DbColumn<Consumption>[] cols = new DbColumn[] {
				new DbColumn<Consumption>(COL_CONSUMPTION_TYPE_INDEX, ColType.INT, cnsp->cnsp.getType().getDbIndex()),
				new DbColumn<Consumption>(COL_CONSUMPTION_DIRECTION_INDEX, ColType.INT, cnsp->cnsp.getDirection().getDbIndex()),
				new DbColumn<Consumption>(COL_CONSUMPTION_AMOUNT, ColType.INT, cnsp->cnsp.getAmount()),
				new DbColumn<Consumption>(COL_CONSUMPTION_DESCRIPTION, ColType.STRING, cnsp->cnsp.getDescription()),
				new DbColumn<Consumption>(COL_CONSUMPTION_PAYMENT_TYPE_INDEX, ColType.INT, cnsp->cnsp.getPaymentType().getDbIndex()),
				new DbColumn<Consumption>(COL_CONSUMPTION_DATE, ColType.STRING, cnsp->cnsp.getDate() == null ? "" : cnsp.getDate().toString()),
		};
		return saveObject(TABLE_CONSUMPTION, cols, _cnsp);
		
		
//		Connection conn = getConn();
//		PreparedStatement pstmt = null;
//		try {
//			// statement
//			String qstr = "update " + TABLE_CONSUMPTION;
//			qstr += " set " + COL_CONSUMPTION_TYPE_INDEX + " = ?, " + COL_CONSUMPTION_DIRECTION_INDEX + "=?, "
//					+ COL_CONSUMPTION_AMOUNT + "=?, " + COL_CONSUMPTION_DESCRIPTION + "=?, "
//					+ COL_CONSUMPTION_PAYMENT_TYPE_INDEX + "=?, " + COL_CONSUMPTION_DATE + "=?";
//			qstr += ", " + COL_OBJECT_UPDATE_TIME + "=?" + " where uid = '" + _cnsp.getUid() + "'";
//			pstmt = conn.prepareStatement(qstr);
//			int colIndex = 1;
//			pstmt.setInt(colIndex++, _cnsp.getType().getDbIndex());
//			pstmt.setInt(colIndex++, _cnsp.getDirection().getDbIndex());
//			pstmt.setInt(colIndex++, _cnsp.getAmount());
//			pstmt.setString(colIndex++, _cnsp.getDescription());
//			pstmt.setInt(colIndex++, _cnsp.getPaymentType().getDbIndex());
//			pstmt.setString(colIndex++, _cnsp.getDate() == null ? "" : _cnsp.getDate().toString());
//			
//			pstmt.setString(colIndex++, LocalDateTime.now().toString());
//
//			if (pstmt.executeUpdate() == 1)
//				return true;
//			else
//				return createConsumption(_cnsp);
//		} catch (SQLException e) {
//			System.out.println("DB linking failed!");
//			e.printStackTrace();
//			return false;
//		} finally {
//			close(conn, pstmt, null);
//		}
	}

//	private boolean createConsumption(Consumption _cnsp) {
//		Connection conn = getConn();
//		PreparedStatement pstmt = null;
//		try {
//			// statement
//			String qstr = "insert into " + TABLE_CONSUMPTION + " (";
//			qstr += COL_UID;
//			qstr += "," + COL_CONSUMPTION_TYPE_INDEX + "," + COL_CONSUMPTION_DIRECTION_INDEX + ","
//					+ COL_CONSUMPTION_AMOUNT + "," + COL_CONSUMPTION_DESCRIPTION + ","
//					+ COL_CONSUMPTION_PAYMENT_TYPE_INDEX + "," + COL_CONSUMPTION_DATE;
//			qstr += "," + COL_OBJECT_CREATE_TIME + "," + COL_OBJECT_UPDATE_TIME;
//			qstr += ") values(?,?,?,?,?,?,?,?,?)";
//			pstmt = conn.prepareStatement(qstr);
//			int colIndex = 1;
//			pstmt.setString(colIndex++, _cnsp.getUid());
//			pstmt.setInt(colIndex++, _cnsp.getType().getDbIndex());
//			pstmt.setInt(colIndex++, _cnsp.getDirection().getDbIndex());
//			pstmt.setInt(colIndex++, _cnsp.getAmount());
//			pstmt.setString(colIndex++, _cnsp.getDescription());
//			pstmt.setInt(colIndex++, _cnsp.getPaymentType().getDbIndex());
//			pstmt.setString(colIndex++, _cnsp.getDate() == null ? null : _cnsp.getDate().toString());
//			pstmt.setString(colIndex++, LocalDateTime.now().toString());
//			pstmt.setString(colIndex++, LocalDateTime.now().toString());
//			pstmt.executeUpdate();
//			return true;
//		} catch (SQLException e) {
//			System.out.println("DB linking failed!");
//			e.printStackTrace();
//		} finally {
//			close(conn, pstmt, null);
//		}
//		return false;
//	}

	@Override
	public boolean deleteConsumption(String _uid) {
		return deleteObject(TABLE_CONSUMPTION, _uid);
	}

	private Consumption parseConsumption(ResultSet _rs) {
		Consumption cnsp = null;
		String testStr = null;
		try {
			String uid = _rs.getString(COL_UID);
			LocalDateTime oCreateTime = LocalDateTime.parse(_rs.getString(COL_OBJECT_CREATE_TIME));
			LocalDateTime oUpdateTime = LocalDateTime.parse(_rs.getString(COL_OBJECT_UPDATE_TIME));
			cnsp = Consumption.getInstance(uid, oCreateTime, oUpdateTime);
			/* pack attributes */
			cnsp.setType(TypeEnum.getInstance(_rs.getInt(COL_CONSUMPTION_TYPE_INDEX)));
			cnsp.setDirection(DirectionEnum.getInstance(_rs.getInt(COL_CONSUMPTION_DIRECTION_INDEX)));
			cnsp.setAmount(_rs.getInt(COL_CONSUMPTION_AMOUNT));
			cnsp.setDescription(_rs.getString(COL_CONSUMPTION_DESCRIPTION));
			cnsp.setPaymentType(PaymentTypeEnum.getInstance(_rs.getInt(COL_CONSUMPTION_PAYMENT_TYPE_INDEX)));
			testStr = _rs.getString(COL_CONSUMPTION_DATE);
			cnsp.setDate(LocalDate.parse(_rs.getString(COL_CONSUMPTION_DATE)));
		} catch (Throwable e) {
			System.out.println("_rs.getString(COL_CONSUMPTION_DATE): " +testStr);
			e.printStackTrace();
			return null;
		}
		return cnsp;
	}

//	private void packConsumption(ResultSet _rs, Consumption _cnsp) throws SQLException {
//		_cnsp.setType(TypeEnum.getInstance(_rs.getInt(COL_CONSUMPTION_TYPE_INDEX)));
//		_cnsp.setDirection(DirectionEnum.getInstance(_rs.getInt(COL_CONSUMPTION_DIRECTION_INDEX)));
//		_cnsp.setAmount(_rs.getInt(COL_CONSUMPTION_AMOUNT));
//		_cnsp.setDescription(_rs.getString(COL_CONSUMPTION_DESCRIPTION));
//		_cnsp.setPaymentType(PaymentTypeEnum.getInstance(_rs.getInt(COL_CONSUMPTION_PAYMENT_TYPE_INDEX)));
//		_cnsp.setDate(LocalDate.parse(_rs.getString(COL_CONSUMPTION_DATE)));
//	}

	@Override
	public List<Consumption> searchConsumptions(ConsumptionSearchParam _searchParam) {
		List<Consumption> resultList = new ArrayList<>();

		Connection conn = getConn();
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			// statement
			String qstr = "select * from " + TABLE_CONSUMPTION + " where " + COL_UID + " is not null";
			String wstr;
			/* type */
			wstr = "";
			for (TypeEnum type : _searchParam.getTypeList()) {
				if (!DataFO.isEmptyString(wstr))
					wstr += " or ";
				wstr += COL_CONSUMPTION_TYPE_INDEX + " = " + type.getDbIndex();
			}
			if (!DataFO.isEmptyString(wstr))
				qstr += " and (" + wstr + ")";
			/* direction */
			if (_searchParam.getDirection() != null)
				qstr += " and " + COL_CONSUMPTION_DIRECTION_INDEX + " = " + _searchParam.getDirection().getDbIndex();
			/* paymentType */
			wstr = "";
			for (PaymentTypeEnum paymentType : _searchParam.getPaymentTypeList()) {
				if (!DataFO.isEmptyString(wstr))
					wstr += " or ";
				wstr += COL_CONSUMPTION_PAYMENT_TYPE_INDEX + " = " + paymentType.getDbIndex();
			}
			if (!DataFO.isEmptyString(wstr))
				qstr += " and (" + wstr + ")";
			/* description */
			if (!DataFO.isEmptyString(_searchParam.getDescription()))
				qstr += " and " + COL_CONSUMPTION_DESCRIPTION + " like '" + _searchParam.getDescription() + "'";
			/* consumptionDateStart */
			if (_searchParam.getConsumptionDateStart() != null)
				qstr += " and " + COL_CONSUMPTION_DATE + " >= '" + _searchParam.getConsumptionDateStart().toString()
						+ "'";
			/* consumptionDateEnd */
			if (_searchParam.getConsumptionDateEnd() != null)
				qstr += " and " + COL_CONSUMPTION_DATE + " <= '" + _searchParam.getConsumptionDateEnd().toString()
						+ "'";
			/* payDateStart */
			if (_searchParam.getPayDateStart() != null)
				qstr += " and " + COL_UID + " in (select consumptionUid from " + TABLE_PAYMENT + " where "
						+ COL_PAYMENT_DATE + " >= '" + _searchParam.getPayDateStart().toString() + "'" + ")";
			/* payDateEnd */
			if (_searchParam.getPayDateEnd() != null)
				qstr += " and " + COL_UID + " in (select consumptionUid from " + TABLE_PAYMENT + " where "
						+ COL_PAYMENT_DATE + " <= '" + _searchParam.getPayDateEnd().toString() + "'" + ")";

//			qstr += " order by " + COL_CONSUMPTION_DATE + " desc";
			qstr += " order by " + COL_CONSUMPTION_DATE + " asc";

			/* 數量上限 */
			if (_searchParam.getLimit() > 0) {
				qstr += " limit " + _searchParam.getLimit();
			}
			
			System.out.println("qstr: " + qstr);
			pstmt = conn.prepareStatement(qstr);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Consumption cnsp = parseConsumption(rs);
				resultList.add(cnsp);
			}
			return resultList;
		} catch (SQLException e) {
			System.out.println("DB linking failed!");
			e.printStackTrace();
			return new ArrayList<>();
		} finally {
			close(conn, pstmt, rs);
		}
	}

	// -----------------------------------------------------------
	// --------------------------Payment--------------------------
	private final static String TABLE_PAYMENT = "Payment";
	private final static String COL_PAYMENT_CONSUMPTION_UID = "consumption_uid";
	private final static String COL_PAYMENT_DATE = "date";
	private final static String COL_PAYMENT_AMOUNT = "amount";

	@Override
	public boolean savePayment(Payment _payment) {
		DbColumn<Payment>[] cols = new DbColumn[] {
				new DbColumn<Payment>(COL_PAYMENT_CONSUMPTION_UID, ColType.STRING, pm->pm.getConsumptionUid()),
				new DbColumn<Payment>(COL_PAYMENT_DATE, ColType.STRING, pm->pm.getDate() == null ? null : pm.getDate().toString()),
				new DbColumn<Payment>(COL_PAYMENT_AMOUNT, ColType.INT, pm->pm.getAmount()),
		};
		return saveObject(TABLE_PAYMENT, cols, _payment);
		
	}

	@Override
	public boolean deletePayment(String _uid) {
		return deleteObject(TABLE_PAYMENT, _uid);
	}

	private Payment parsePayment(ResultSet _rs) {
		Payment pm = null;
		try {
			String uid = _rs.getString(COL_UID);
			String _consumptionUid = _rs.getString(COL_PAYMENT_CONSUMPTION_UID);
			LocalDateTime oCreateTime = LocalDateTime.parse(_rs.getString(COL_OBJECT_CREATE_TIME));
			LocalDateTime oUpdateTime = LocalDateTime.parse(_rs.getString(COL_OBJECT_UPDATE_TIME));
			pm = Payment.getInstance(uid, _consumptionUid, oCreateTime, oUpdateTime);
			/* pack attributes */
			pm.setDate(LocalDate.parse(_rs.getString(COL_PAYMENT_DATE)));
			pm.setAmount(_rs.getInt(COL_PAYMENT_AMOUNT));
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
		return pm;
	}

	@Override
	public List<Payment> loadPayments(String _consumptionUid) {
		List<Payment> resultList = new ArrayList<>();
		if (DataFO.isEmptyString(_consumptionUid))
			return resultList;

		Connection conn = getConn();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			// statement
			String qstr = "select * from " + TABLE_PAYMENT + " where " + COL_PAYMENT_CONSUMPTION_UID + " = '"
					+ _consumptionUid + "'";
			pstmt = conn.prepareStatement(qstr);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Payment payment = parsePayment(rs);
				resultList.add(payment);
			}
			return resultList;
		} catch (SQLException e) {
			System.out.println("DB linking failed!");
			e.printStackTrace();
			return new ArrayList<>();
		} finally {
			close(conn, pstmt, rs);
		}
	}

	@Override
	public List<Payment> searchPayments(ConsumptionSearchParam _searchParam) {
		List<Payment> resultList = new ArrayList<>();

		Connection conn = getConn();
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			// statement
			String qstr = "select * from " + TABLE_PAYMENT + " where " + COL_UID + " is not null";
			qstr += " and " + COL_PAYMENT_CONSUMPTION_UID + " in (select " + COL_UID + " from " + TABLE_CONSUMPTION
					+ " where " + COL_UID + " is not null";

			String wstr;
			/* type */
			wstr = "";
			for (TypeEnum type : _searchParam.getTypeList()) {
				if (!DataFO.isEmptyString(wstr))
					wstr += " or ";
				wstr += COL_CONSUMPTION_TYPE_INDEX + " = " + type.getDbIndex();
			}
			if (!DataFO.isEmptyString(wstr))
				qstr += " and (" + wstr + ")";
			
			/* direction */
			if (_searchParam.getDirection() != null)
				qstr += " and " + COL_CONSUMPTION_DIRECTION_INDEX + " = " + _searchParam.getDirection().getDbIndex();

			/* paymentType */
			wstr = "";
			for (PaymentTypeEnum paymentType : _searchParam.getPaymentTypeList()) {
				if (!DataFO.isEmptyString(wstr))
					wstr += " or ";
				wstr += COL_CONSUMPTION_PAYMENT_TYPE_INDEX + " = " + paymentType.getDbIndex();
			}
			if (!DataFO.isEmptyString(wstr))
				qstr += " and (" + wstr + ")";
			/* description */
			if (!DataFO.isEmptyString(_searchParam.getDescription()))
				qstr += " and " + COL_CONSUMPTION_DESCRIPTION + " like '" + _searchParam.getDescription() + "'";
			/* consumptionDateStart */
			if (_searchParam.getConsumptionDateStart() != null)
				qstr += " and " + COL_CONSUMPTION_DATE + " >= '" + _searchParam.getConsumptionDateStart().toString()
						+ "'";
			/* consumptionDateEnd */
			if (_searchParam.getConsumptionDateEnd() != null)
				qstr += " and " + COL_CONSUMPTION_DATE + " <= '" + _searchParam.getConsumptionDateEnd().toString()
						+ "'";

			qstr += ")";

			/* payDateStart */
			if (_searchParam.getPayDateStart() != null)
				qstr += " and " + COL_PAYMENT_DATE + " >= '" + _searchParam.getPayDateStart().toString() + "'";
			/* payDateEnd */
			if (_searchParam.getPayDateEnd() != null)
				qstr += " and " + COL_PAYMENT_DATE + " <= '" + _searchParam.getPayDateEnd().toString() + "'";

//			qstr += " order by " + COL_PAYMENT_DATE + " desc";
			qstr += " order by " + COL_PAYMENT_DATE + " asc";

			/* 預設取出最新的500筆。 */
			if (_searchParam.getLimit() > 0) {
				qstr += " limit " + _searchParam.getLimit();
			}

			pstmt = conn.prepareStatement(qstr);
			rs = pstmt.executeQuery();
			while (rs.next()) {
//				String uid = rs.getString(COL_UID);
//				String consumptionUid = rs.getString(COL_PAYMENT_CONSUMPTION_UID);
//				LocalDateTime oCreateTime = LocalDateTime.parse(rs.getString(COL_OBJECT_CREATE_TIME));
//				LocalDateTime oUpdateTime = LocalDateTime.parse(rs.getString(COL_OBJECT_UPDATE_TIME));
//				Payment payment = Payment.getInstance(uid, consumptionUid, oCreateTime, oUpdateTime);
//				packPayment(rs, payment);
				Payment payment = parsePayment(rs);
				resultList.add(payment);
			}
			return resultList;
		} catch (SQLException e) {
			System.out.println("DB linking failed!");
			e.printStackTrace();
			return new ArrayList<>();
		} finally {
			close(conn, pstmt, rs);
		}
	}

	// -------------------------------------------------------------------------------
	// ----------------------------------CreditCard-----------------------------------
	private final static String TABLE_CREDIT_CARD = "CreditCard";

	private String parseCardNoDbStr(String[] _cardNo) {
		return JsonUtil.getJsonArrayString(_cardNo);
	}

	private String[] parseCardNo(String _cardNoDbStr) {
		return (String[]) JsonUtil.parseJsonArrayStringToObjs(_cardNoDbStr);
	}

	@Override
	public boolean saveCreditCard(CreditCard _creditCard) {
		Connection conn = getConn();
		PreparedStatement pstmt = null;
		try {
			// statement
			String qstr = "update " + TABLE_CREDIT_CARD;
			qstr += "set title=?,cardNo=?,cardHolderUid=?,cardHolderName=?,bankCode=?,cardIssuerIndex=?,closingDayInMonth=?";
			qstr += ", oUpdateDate=? where uid='" + _creditCard.getUid() + "'";
			pstmt = conn.prepareStatement(qstr);
			int colIndex = 1;
			pstmt.setString(colIndex++, _creditCard.getTitle());
			pstmt.setString(colIndex++, parseCardNoDbStr(_creditCard.getCardNo()));
			pstmt.setString(colIndex++, _creditCard.getCardHolderUid());
			pstmt.setString(colIndex++, _creditCard.getCardHolderName());
			pstmt.setString(colIndex++, _creditCard.getBank().getCode());
			pstmt.setInt(colIndex++, _creditCard.getCardIssuer().getDbIndex());
			pstmt.setInt(colIndex++, _creditCard.getClosingDayInMonth());
			pstmt.setLong(colIndex++, System.currentTimeMillis());

			if (pstmt.executeUpdate() == 1)
				return true;
			else
				return createCreditCard(_creditCard);
		} catch (SQLException e) {
			System.out.println("DB linking failed!");
			e.printStackTrace();
			return false;
		} finally {
			close(conn, pstmt, null);
		}
	}

	private boolean createCreditCard(CreditCard _creditCard) {
		Connection conn = getConn();
		PreparedStatement pstmt = null;
		try {
			// statement
			String qstr = "insert into " + TABLE_CREDIT_CARD + " (";
			qstr += "uid,title,cardNo,cardHolderUid,cardHolderName,bankCode,cardIssuerIndex,closingDayInMonth";
			qstr += ",oCreateDate, oUpdateDate";
			qstr += ")";
			qstr += " values(?,?,?,?,?,?,?,?,?,?)";
			pstmt = conn.prepareStatement(qstr);
			int colIndex = 1;
			pstmt.setString(colIndex++, _creditCard.getUid());
			pstmt.setString(colIndex++, _creditCard.getTitle());
			pstmt.setString(colIndex++, parseCardNoDbStr(_creditCard.getCardNo()));
			pstmt.setString(colIndex++, _creditCard.getCardHolderUid());
			pstmt.setString(colIndex++, _creditCard.getCardHolderName());
			pstmt.setString(colIndex++, _creditCard.getBank().getCode());
			pstmt.setInt(colIndex++, _creditCard.getCardIssuer().getDbIndex());
			pstmt.setInt(colIndex++, _creditCard.getClosingDayInMonth());
			pstmt.setLong(colIndex++, System.currentTimeMillis());
			pstmt.setLong(colIndex++, System.currentTimeMillis());
			pstmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			System.out.println("DB linking failed!");
			e.printStackTrace();
		} finally {
			close(conn, pstmt, null);
		}
		return false;
	}

	@Override
	public boolean deleteCreditCard(String _uid) {
		Connection conn = getConn();
		PreparedStatement pstmt = null;
		try {
			// statement
			String qstr = "delete from " + TABLE_CREDIT_CARD + " where uid='" + _uid + "'";
			pstmt = conn.prepareStatement(qstr);
			System.out.println("qstr: " + qstr);
			System.out.println("pstmt.executeUpdate(): " + pstmt.executeUpdate());
			return true;
		} catch (SQLException e) {
			System.out.println("DB linking failed!");
			e.printStackTrace();
		} finally {
			close(conn, pstmt, null);
		}
		return false;
	}

	private void packCreditCard(ResultSet _rs, CreditCard _cc) throws SQLException {
		_cc.setTitle(_rs.getString("title"));
		_cc.setClosingDayInMonth(_rs.getInt("closingDayInMonth"));
	}

	@Override
	public List<CreditCard> loadCreditCards() {
		List<CreditCard> resultList = new ArrayList<>();

		Connection conn = getConn();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			// statement
			String qstr = "select * from " + TABLE_CREDIT_CARD;
			pstmt = conn.prepareStatement(qstr);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				String uid = rs.getString("uid");
				String[] cardNo = parseCardNo(rs.getString("cardNo"));
				String cardHolderUid = rs.getString("cardHolderUid");
				String cardHolderName = rs.getString("cardHolderName");
				Bank bank = Bank.getInstance(rs.getString("bankCode"));
				CardIssuer cardIssuer = CardIssuer.getInstance(rs.getInt("cardIssuerIndex"));
				LocalDateTime oCreateTime = LocalDateTime.parse(rs.getString(COL_OBJECT_CREATE_TIME));
				LocalDateTime oUpdateTime = LocalDateTime.parse(rs.getString(COL_OBJECT_UPDATE_TIME));
				CreditCard cc = CreditCard.getInstance(uid, cardNo, cardHolderUid, cardHolderName, bank, cardIssuer,
						oCreateTime, oUpdateTime);
				packCreditCard(rs, cc);
				resultList.add(cc);
			}
			return resultList;
		} catch (SQLException e) {
			System.out.println("DB linking failed!");
			e.printStackTrace();
			return new ArrayList<>();
		} finally {
			close(conn, pstmt, rs);
		}
	}

}
