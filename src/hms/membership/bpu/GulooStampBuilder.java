package hms.membership.bpu;

import hms_kernel.membership.GulooStamp;
import hms_kernel.membership.dto.GulooStampCreateObj;
import legion.biz.Bpu;
import legion.util.DataFO;
import legion.util.TimeTraveler;

public abstract class GulooStampBuilder extends Bpu<GulooStamp> {
	/* base */
	private long stampDate; // 日期
	private String desp; // 簡述
	private String remark; // 備註

	/* data */
	// none

	// -------------------------------------------------------------------------------
	// -----------------------------------appender------------------------------------
	protected GulooStampBuilder appendStampDate(long stampDate) {
		this.stampDate = stampDate;
		return this;
	}

	protected GulooStampBuilder appendDesp(String desp) {
		this.desp = desp;
		return this;
	}

	protected GulooStampBuilder appendRemark(String remark) {
		this.remark = remark;
		return this;
	}

	// -------------------------------------------------------------------------------
	// ------------------------------------getter-------------------------------------
	public long getStampDate() {
		return stampDate;
	}

	public String getDesp() {
		return desp;
	}

	public String getRemark() {
		return remark;
	}

	// -------------------------------------------------------------------------------
	private GulooStampCreateObj packGulooStampCreateObj() {
		GulooStampCreateObj dto = new GulooStampCreateObj();
		dto.setStampDate(getStampDate());
		dto.setDesp(getDesp());
		dto.setRemark(getRemark());
		return dto;
	}

	// -------------------------------------------------------------------------------
	@Override
	public boolean validate(StringBuilder _msg) {
		return true;
	}

	@Override
	public boolean verify(StringBuilder _msg) {
		boolean v = true;

		// stampDate
		if (getStampDate() <= 0) {
			_msg.append("StampDate NOT assigned.").append(System.lineSeparator());
			v = false;
		}

		// desp
		if (DataFO.isEmptyString(getDesp())) {
			_msg.append("Description should NOT be empty.").append(System.lineSeparator());
			v = false;
		}

		return v;
	}

	// -------------------------------------------------------------------------------
	protected GulooStamp buildGulooStamp(TimeTraveler _tt) {
		TimeTraveler tt = new TimeTraveler();

		GulooStamp gs = GulooStamp.create(packGulooStampCreateObj());
		if (gs == null) {
			tt.travel();
			log.error("GulooStamp.create return null.");
			return null;
		}
		tt.addSite("revert GulooStamp.create", () -> gs.delete());
		log.info("GulooStamp.create [{}][{}][{}]", gs.getUid(), gs.getStampDateStr(), gs.getDesp());

		//
		if (_tt != null)
			_tt.copySitesFrom(tt);

		return gs;
	}

}
