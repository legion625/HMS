package hms.membership.bpu;

import java.util.List;

import hms_kernel.membership.Entity;
import hms_kernel.membership.GulooStamp;
import hms_kernel.membership.GulooStampCate;
import hms_kernel.membership.GulooStampCateConj;
import hms_kernel.membership.GulooStampEntityConj;
import legion.util.TimeTraveler;

public class GulooStampBuilder1 extends GulooStampBuilder {
	/* base */
	// none

	/* data */
	private List<Entity> entityList;
	private List<GulooStampCate> cateList;

	// -------------------------------------------------------------------------------
	@Override
	protected GulooStampBuilder1 appendBase() {
		/* base */
		// none

		/* data */
		// none

		return this;
	}

	// -------------------------------------------------------------------------------
	@Override
	public GulooStampBuilder1 appendStampDate(long stampDate) {
		return (GulooStampBuilder1) super.appendStampDate(stampDate);
	}

	@Override
	public GulooStampBuilder appendDesp(String desp) {
		return super.appendDesp(desp);

	}

	@Override
	public GulooStampBuilder appendRemark(String remark) {
		return super.appendRemark(remark);
	}

	public GulooStampBuilder appendEntityList(List<Entity> entityList) {
		this.entityList = entityList;
		return this;
	}

	public GulooStampBuilder appendCateList(List<GulooStampCate> cateList) {
		this.cateList = cateList;
		return this;
	}

	// -------------------------------------------------------------------------------
	public List<Entity> getEntityList() {
		return entityList;
	}

	public List<GulooStampCate> getCateList() {
		return cateList;
	}

	// -------------------------------------------------------------------------------
	@Override
	public boolean verify(StringBuilder _msg) {
		boolean v = super.verify(_msg);

		// entityList
		if (getEntityList() == null || getEntityList().size() <= 0) {
			_msg.append("EntityList should NOT be empty.").append(System.lineSeparator());
			v = false;
		}

		return v;
	}

	// -------------------------------------------------------------------------------
	@Override
	protected GulooStamp buildProcess(TimeTraveler _tt) {
		TimeTraveler tt = new TimeTraveler();

		//
		GulooStamp gs = buildGulooStamp(tt);
		if (gs == null) {
			tt.travel();
			log.error("buildGulooStamp return null.");
			return null;
		} // copy sites inside

		// 指定entity
		for (Entity entity : getEntityList()) {
			GulooStampEntityConj gsec = GulooStampEntityConj.create(gs.getUid(), entity.getUid());
			if (gsec == null) {
				tt.travel();
				log.error("GulooStampEntityConj.create return null.");
				return null;
			}
			tt.addSite("revert GulooStampEntityConj.create", () -> gsec.delete());
		}

		// 指定cate
		if (getCateList() != null) {
			for (GulooStampCate cate : getCateList()) {
				GulooStampCateConj gscc = GulooStampCateConj.create(gs.getUid(), cate.getUid());
				if (gscc == null) {
					tt.travel();
					log.error("GulooStampCateConj.create return null.");
					return null;
				}
				tt.addSite("revert GulooStampCateConj.create", () -> gscc.delete());
			}
		}

		//
		if (_tt != null)
			_tt.copySitesFrom(tt);

		return gs;
	}

	// -------------------------------------------------------------------------------

//	

}
