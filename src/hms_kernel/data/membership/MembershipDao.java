package hms_kernel.data.membership;

import java.sql.ResultSet;
import java.util.List;

import hms_kernel.membership.Entity;
import hms_kernel.membership.GulooStamp;
import hms_kernel.membership.GulooStampCate;
import hms_kernel.membership.GulooStampCateConj;
import hms_kernel.membership.GulooStampEntityConj;
import hms_kernel.membership.type.EntityType;
import legion.data.service.PgDao;
import legion.util.LogUtil;

public class MembershipDao extends PgDao {

	protected MembershipDao(String source) {
		super(source);
	}

	// -------------------------------------------------------------------------------
	// ------------------------------------Entity-------------------------------------
	private final static String TB_ENTITY = "mbr_entity";
	private final static String COL_ETT_ALIAS = "alias";
	private final static String COL_ETT_TYPE_IDX = "type_idx";
	private final static String COL_ETT_BIRTH_DATE = "birth_date";

	boolean saveEntity(Entity _ett) {
		DbColumn<Entity>[] cols = new DbColumn[] { //
				DbColumn.of(COL_ETT_ALIAS, ColType.STRING, Entity::getAlias), //
				DbColumn.of(COL_ETT_TYPE_IDX, ColType.INT, Entity::getTypeIdx), //
				DbColumn.of(COL_ETT_BIRTH_DATE, ColType.LONG, Entity::getBirthDate), //
		};
		return saveObject(TB_ENTITY, cols, _ett);
	}

	boolean deleteEntity(String _uid) {
		return deleteObject(TB_ENTITY, _uid);
	}

	private Entity parseEntity(ResultSet _rs) {
		try {
			Entity ett = Entity.getInstance(parseUid(_rs), parseObjectCreateTime(_rs), parseObjectUpdateTime(_rs));
			/* pack attributes */
			ett.setAlias(_rs.getString(COL_ETT_ALIAS));
			ett.setType(EntityType.get(_rs.getInt(COL_ETT_TYPE_IDX)));
			ett.setBirthDate(_rs.getLong(COL_ETT_BIRTH_DATE));

			return ett;
		} catch (Throwable e) {
			LogUtil.log(e);
			return null;
		}
	}

	Entity loadEntity(String _uid) {
		return loadObject(TB_ENTITY, _uid, this::parseEntity);
	}

	List<Entity> loadEntityList() {
		return loadObjectList(TB_ENTITY, this::parseEntity);
	}

	// -------------------------------------------------------------------------------
	// ----------------------------------GulooStamp-----------------------------------
	private final static String TB_GS = "mbr_guloo_stamp";
	private final static String COL_GS$_stamp_date = "stamp_date";
	private final static String COL_GS$_desp = "desp";
	private final static String COL_GS$_remark = "remark";

	boolean saveGulooStamp(GulooStamp _gs) {
		DbColumn<GulooStamp>[] cols = new DbColumn[] { //
				DbColumn.of(COL_GS$_stamp_date, ColType.LONG, GulooStamp::getStampDate), //
				DbColumn.of(COL_GS$_desp, ColType.STRING, GulooStamp::getDesp), //
				DbColumn.of(COL_GS$_remark, ColType.STRING, GulooStamp::getRemark), //
		};
		return saveObject(TB_GS, cols, _gs);
	}

	boolean deleteGulooStamp(String _uid) {
		return deleteObject(TB_GS, _uid);
	}

	private GulooStamp parseGulooStamp(ResultSet _rs) {
		try {
			GulooStamp gs = GulooStamp.getInstance(parseUid(_rs), parseObjectCreateTime(_rs),
					parseObjectUpdateTime(_rs));
			/* pack attributes */
			gs.setStampDate(_rs.getLong(COL_GS$_stamp_date));
			gs.setDesp(_rs.getString(COL_GS$_desp));
			gs.setRemark(_rs.getString(COL_GS$_remark));
			return gs;
		} catch (Throwable e) {
			LogUtil.log(e);
			return null;
		}
	}

	GulooStamp loadGulooStamp(String _uid) {
		return loadObject(TB_GS, _uid, this::parseGulooStamp);
	}

	List<GulooStamp> loadGulooStampList() {
		return loadObjectList(TB_GS, this::parseGulooStamp);
	}

	// -------------------------------------------------------------------------------
	// -----------------------------GulooStampEntityConj------------------------------
	private final static String TB_GSEC = "mbr_guloo_stamp_entity_conj";
	private final static String COL_GSEC$_stamp_uid = "stamp_uid";
	private final static String COL_GSEC$_entity_uid = "entity_uid";

	boolean saveGulooStampEntityConj(GulooStampEntityConj _gsEntityConj) {
		DbColumn<GulooStampEntityConj>[] cols = new DbColumn[] { //
				DbColumn.of(COL_GSEC$_stamp_uid, ColType.STRING, GulooStampEntityConj::getStampUid), //
				DbColumn.of(COL_GSEC$_entity_uid, ColType.STRING, GulooStampEntityConj::getEntityUid), //
		};
		return saveObject(TB_GSEC, cols, _gsEntityConj);
	}

	boolean deleteGulooStampEntityConj(String _uid) {
		return deleteObject(TB_GSEC, _uid);
	}

	private GulooStampEntityConj parseGulooStampEntityConj(ResultSet _rs) {
		try {
			String stamUid = _rs.getString(COL_GSEC$_stamp_uid);
			String entityUid = _rs.getString(COL_GSEC$_entity_uid);
			GulooStampEntityConj gsec = GulooStampEntityConj.getInstance(parseUid(_rs), stamUid, entityUid,
					parseObjectCreateTime(_rs), parseObjectUpdateTime(_rs));
			/* pack attributes */
			// none
			return gsec;
		} catch (Throwable e) {
			LogUtil.log(e);
			return null;
		}
	}

	GulooStampEntityConj loadGulooStampEntityConj(String _uid) {
		return loadObject(TB_GSEC, _uid, this::parseGulooStampEntityConj);
	}

	List<GulooStampEntityConj> loadGulooStampEntityConjList(String _gsUid) {
		return loadObjectList(TB_GSEC, COL_GSEC$_stamp_uid, _gsUid, this::parseGulooStampEntityConj);
	}

	List<GulooStampEntityConj> loadGulooStampEntityConjListByEntity(String _entityUid) {
		return loadObjectList(TB_GSEC, COL_GSEC$_entity_uid, _entityUid, this::parseGulooStampEntityConj);
	}

	// -------------------------------------------------------------------------------
	// --------------------------------GulooStampCate---------------------------------
	private final static String TB_GSC = "mbr_guloo_stamp_cate";
	private final static String COL_GSC$_name = "name";
	private final static String COL_GSC$_color = "color";

	boolean saveGulooStampCate(GulooStampCate _gsc) {
		DbColumn<GulooStampCate>[] cols = new DbColumn[] { //
				DbColumn.of(COL_GSC$_name, ColType.STRING, GulooStampCate::getName), //
				DbColumn.of(COL_GSC$_color, ColType.STRING, GulooStampCate::getColor), //
		};
		return saveObject(TB_GSC, cols, _gsc);
	}

	boolean deleteGulooStampCate(String _uid) {
		return deleteObject(TB_GSC, _uid);
	}

	private GulooStampCate parseGulooStampCate(ResultSet _rs) {
		try {
			GulooStampCate gsc = GulooStampCate.getInstance(parseUid(_rs), parseObjectCreateTime(_rs),
					parseObjectUpdateTime(_rs));
			/* pack attributes */
			gsc.setName(_rs.getString(COL_GSC$_name));
			gsc.setColor(_rs.getString(COL_GSC$_color));
			return gsc;
		} catch (Throwable e) {
			LogUtil.log(e);
			return null;
		}
	}

	GulooStampCate loadGulooStampCate(String _uid) {
		return loadObject(TB_GSC, _uid, this::parseGulooStampCate);
	}

	List<GulooStampCate> loadGulooStampCateList() {
		return loadObjectList(TB_GSC, this::parseGulooStampCate);
	}
	
	// -------------------------------------------------------------------------------
	// ------------------------------GulooStampCateConj-------------------------------
	private final static String TB_GSCC = "mbr_guloo_stamp_cate_conj";
	private final static String COL_GSCC$_stamp_uid = "stamp_uid";
	private final static String COL_GSCC$_cate_uid = "cate_uid";

	
	boolean saveGulooStampCateConj(GulooStampCateConj _gscc) {
		DbColumn<GulooStampCateConj>[] cols = new DbColumn[] { //
				DbColumn.of(COL_GSCC$_stamp_uid, ColType.STRING, GulooStampCateConj::getStampUid), //
				DbColumn.of(COL_GSCC$_cate_uid, ColType.STRING, GulooStampCateConj::getCateUid), //
		};
		return saveObject(TB_GSCC, cols, _gscc);
	}
	
	boolean deleteGulooStampCateConj(String _uid) {
		return deleteObject(TB_GSCC, _uid);
	}
	
	private GulooStampCateConj parseGulooStampCateConj(ResultSet _rs) {
		try {
			String stamUid = _rs.getString(COL_GSCC$_stamp_uid);
			String cateUid = _rs.getString(COL_GSCC$_cate_uid);
			GulooStampCateConj gsec = GulooStampCateConj.getInstance(parseUid(_rs), stamUid, cateUid,
					parseObjectCreateTime(_rs), parseObjectUpdateTime(_rs));
			/* pack attributes */
			// none
			return gsec;
		} catch (Throwable e) {
			LogUtil.log(e);
			return null;
		}
	}
	
	GulooStampCateConj loadGulooStampCateConj(String _uid) {
		return loadObject(TB_GSCC, _uid, this::parseGulooStampCateConj);
	}
	List<GulooStampCateConj> loadGulooStampCateConjList(String _gsUid){
		return loadObjectList(TB_GSCC, COL_GSCC$_stamp_uid, _gsUid, this::parseGulooStampCateConj);
	}
	List<GulooStampCateConj> loadGulooStampCateConjListByCate(String _cateUid){
		return loadObjectList(TB_GSCC, COL_GSCC$_cate_uid, _cateUid, this::parseGulooStampCateConj);
	}

}
