package hms_kernel.data.membership;

import java.sql.ResultSet;
import java.util.List;

import hms_kernel.membership.Entity;
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
	

	List<Entity> loadEntityList(){
		return loadObjectList(TB_ENTITY, this::parseEntity);
	}

}
