package hms.membership.bpu;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import hms_kernel.membership.Entity;
import hms_kernel.membership.GulooStamp;
import hms_kernel.membership.GulooStampCate;
import hms_kernel.membership.GulooStampCateConj;
import hms_kernel.membership.GulooStampEntityConj;
import legion.biz.Bpu;
import legion.util.DataFO;
import legion.util.TimeTraveler;

/**
 * 修改咕溜：日期、簡述、備註，以及成員、標籤。
 * 成員、標籤只處理差異(拿掉的刪除關聯、新加的建立關聯)；任何一步失敗都會以 TimeTraveler 還原。
 */
public class GulooStampBpuUpdate extends Bpu<Boolean> {

	/* base */
	private GulooStamp gs;

	/* data */
	private long stampDate;
	private String desp;
	private String remark;
	private List<Entity> entityList;
	private List<GulooStampCate> cateList;

	// -------------------------------------------------------------------------------
	@Override
	protected GulooStampBpuUpdate appendBase() {
		/* base */
		gs = (GulooStamp) args[0];

		/* data：編輯中的內容，初始為原本的值 */
		stampDate = gs.getStampDate();
		desp = gs.getDesp();
		remark = gs.getRemark();
		entityList = new ArrayList<>(gs.getEntityList());
		entityList.removeIf(Objects::isNull); // 關聯到已刪除成員的略過
		cateList = new ArrayList<>(gs.getCateList());
		cateList.removeIf(Objects::isNull);

		return this;
	}

	// -------------------------------------------------------------------------------
	// -----------------------------------appender------------------------------------
	public GulooStampBpuUpdate appendStampDate(long stampDate) {
		this.stampDate = stampDate;
		return this;
	}

	public GulooStampBpuUpdate appendDesp(String desp) {
		this.desp = desp;
		return this;
	}

	public GulooStampBpuUpdate appendRemark(String remark) {
		this.remark = remark;
		return this;
	}

	public GulooStampBpuUpdate appendEntityList(List<Entity> entityList) {
		this.entityList = entityList;
		return this;
	}

	public GulooStampBpuUpdate appendCateList(List<GulooStampCate> cateList) {
		this.cateList = cateList;
		return this;
	}

	/** 切換成員：已有就移除，沒有就加入(以 uid 比對) */
	public GulooStampBpuUpdate toggleEntity(Entity entity) {
		if (!entityList.removeIf(e -> e.getUid().equals(entity.getUid())))
			entityList.add(entity);
		return this;
	}

	/** 切換標籤：已有就移除，沒有就加入(以 uid 比對) */
	public GulooStampBpuUpdate toggleCate(GulooStampCate cate) {
		if (!cateList.removeIf(c -> c.getUid().equals(cate.getUid())))
			cateList.add(cate);
		return this;
	}

	// -------------------------------------------------------------------------------
	// ------------------------------------getter-------------------------------------
	/** 原本的咕溜 */
	public GulooStamp getGulooStamp() {
		return gs;
	}

	public long getStampDate() {
		return stampDate;
	}

	public String getDesp() {
		return desp;
	}

	public String getRemark() {
		return remark;
	}

	public List<Entity> getEntityList() {
		return entityList;
	}

	public List<GulooStampCate> getCateList() {
		return cateList;
	}

	// -------------------------------------------------------------------------------
	@Override
	public boolean validate(StringBuilder _msg) {
		return true;
	}

	/** 規則與新增(GulooStampBuilder1)一致 */
	@Override
	public boolean verify(StringBuilder _msg) {
		boolean v = true;

		if (stampDate <= 0) {
			_msg.append("StampDate NOT assigned.").append(System.lineSeparator());
			v = false;
		}

		if (DataFO.isEmptyString(desp)) {
			_msg.append("Description should NOT be empty.").append(System.lineSeparator());
			v = false;
		}

		if (entityList == null || entityList.isEmpty()) {
			_msg.append("EntityList should NOT be empty.").append(System.lineSeparator());
			v = false;
		}

		return v;
	}

	// -------------------------------------------------------------------------------
	/**
	 * 依序：更新日期/簡述/備註 → 成員關聯 → 標籤關聯。
	 * 每一步成功後都在 TimeTraveler 登記還原動作；任一步失敗就 travel()，把前面已完成的步驟全部還原。
	 */
	@Override
	protected Boolean buildProcess(TimeTraveler _tt) {
		TimeTraveler tt = new TimeTraveler();

		if (!updateStamp(tt) || !updateEntityConj(tt) || !updateCateConj(tt)) {
			tt.travel();
			return false;
		}

		//
		if (_tt != null)
			_tt.copySitesFrom(tt);

		return true;
	}

	/** 日期、簡述、備註 */
	protected boolean updateStamp(TimeTraveler tt) {
		long orgDate = gs.getStampDate();
		String orgDesp = gs.getDesp(), orgRemark = gs.getRemark();
		if (!gs.update(stampDate, desp, remark)) {
			log.error("GulooStamp.update return false. [{}]", gs.getUid());
			return false;
		}
		tt.addSite("revert GulooStamp.update", () -> gs.update(orgDate, orgDesp, orgRemark));
		return true;
	}

	/**
	 * 成員關聯只處理差異。
	 * 原本的關聯以「寫入當下」重新從 DB 載入，不用開始編輯時的快取，避免編輯期間別人改過而算錯差異。
	 */
	protected boolean updateEntityConj(TimeTraveler tt) {
		String gsUid = gs.getUid();
		Set<String> newEntityUids = entityList.stream().map(Entity::getUid)
				.collect(Collectors.toCollection(LinkedHashSet::new));
		Set<String> orgEntityUids = new LinkedHashSet<>();
		for (GulooStampEntityConj conj : gs.getEntityConjList(true)) {
			String entityUid = conj.getEntityUid();
			orgEntityUids.add(entityUid);
			if (newEntityUids.contains(entityUid))
				continue;
			if (!conj.delete()) {
				log.error("GulooStampEntityConj.delete return false. [{}][{}]", gsUid, entityUid);
				return false;
			}
			tt.addSite("revert GulooStampEntityConj.delete",
					() -> GulooStampEntityConj.create(gsUid, entityUid) != null);
		}
		for (String entityUid : newEntityUids) {
			if (orgEntityUids.contains(entityUid))
				continue;
			GulooStampEntityConj conj = GulooStampEntityConj.create(gsUid, entityUid);
			if (conj == null) {
				log.error("GulooStampEntityConj.create return null. [{}][{}]", gsUid, entityUid);
				return false;
			}
			tt.addSite("revert GulooStampEntityConj.create", () -> conj.delete());
		}
		return true;
	}

	/** 標籤關聯只處理差異；原本的關聯同樣在寫入當下重新載入 */
	protected boolean updateCateConj(TimeTraveler tt) {
		String gsUid = gs.getUid();
		Set<String> newCateUids = cateList.stream().map(GulooStampCate::getUid)
				.collect(Collectors.toCollection(LinkedHashSet::new));
		Set<String> orgCateUids = new LinkedHashSet<>();
		for (GulooStampCateConj conj : gs.getCateConjList(true)) {
			String cateUid = conj.getCateUid();
			orgCateUids.add(cateUid);
			if (newCateUids.contains(cateUid))
				continue;
			if (!conj.delete()) {
				log.error("GulooStampCateConj.delete return false. [{}][{}]", gsUid, cateUid);
				return false;
			}
			tt.addSite("revert GulooStampCateConj.delete", () -> GulooStampCateConj.create(gsUid, cateUid) != null);
		}
		for (String cateUid : newCateUids) {
			if (orgCateUids.contains(cateUid))
				continue;
			GulooStampCateConj conj = GulooStampCateConj.create(gsUid, cateUid);
			if (conj == null) {
				log.error("GulooStampCateConj.create return null. [{}][{}]", gsUid, cateUid);
				return false;
			}
			tt.addSite("revert GulooStampCateConj.create", () -> conj.delete());
		}
		return true;
	}

}
