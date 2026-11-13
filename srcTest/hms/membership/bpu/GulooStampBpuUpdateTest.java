package hms.membership.bpu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import hms_kernel.AbstractHmsKernelInitTest;
import hms_kernel.data.membership.MembershipDataService;
import hms_kernel.membership.Entity;
import hms_kernel.membership.GulooStamp;
import hms_kernel.membership.GulooStampCate;
import hms_kernel.membership.GulooStampCateConj;
import hms_kernel.membership.GulooStampEntityConj;
import hms_kernel.membership.dto.GulooStampCreateObj;
import hms_kernel.membership.type.EntityType;
import legion.DataServiceFactory;
import legion.biz.BpuFacade;
import legion.util.DateUtil;
import legion.util.TimeTraveler;

/**
 * GulooStampBpuUpdate：正常修改、TimeTraveler 還原(在不同步驟失敗)、以寫入當下的 DB 狀態計算差異。
 * 會在 hms_dev 建立 ZZTEST 開頭的測試資料，結束後刪除。
 */
public class GulooStampBpuUpdateTest extends AbstractHmsKernelInitTest {
	/** 在 @Before 取得：static 初始化時 @BeforeClass 的系統初始化還沒執行，會取不到服務 */
	private MembershipDataService dataService;

	private static final long DATE1 = DateUtil.toLong(LocalDate.of(2026, 1, 1));
	private static final long DATE2 = DateUtil.toLong(LocalDate.of(2026, 2, 2));

	private Entity e1, e2;
	private GulooStampCate c1, c2;
	private GulooStamp gs;

	// -------------------------------------------------------------------------------
	/** 原始狀態：DATE1 / desp1 / remark1，成員 e1，標籤 c1 */
	@Before
	public void createData() {
		dataService = DataServiceFactory.getInstance().getService(MembershipDataService.class);
		assertNotNull(dataService);
		String tag = "ZZTEST" + System.nanoTime();
		e1 = Entity.create(tag + "e1", EntityType.HUMAN, 0);
		e2 = Entity.create(tag + "e2", EntityType.HUMAN, 0);
		c1 = GulooStampCate.create(tag + "c1", "");
		c2 = GulooStampCate.create(tag + "c2", "");
		assertNotNull(e1);
		assertNotNull(e2);
		assertNotNull(c1);
		assertNotNull(c2);

		GulooStampCreateObj dto = new GulooStampCreateObj();
		dto.setStampDate(DATE1);
		dto.setDesp("desp1");
		dto.setRemark("remark1");
		gs = GulooStamp.create(dto);
		assertNotNull(gs);
		assertNotNull(GulooStampEntityConj.create(gs.getUid(), e1.getUid()));
		assertNotNull(GulooStampCateConj.create(gs.getUid(), c1.getUid()));
	}

	@After
	public void deleteData() {
		if (gs != null) {
			dataService.loadGulooStampEntityConjList(gs.getUid()).forEach(GulooStampEntityConj::delete);
			dataService.loadGulooStampCateConjList(gs.getUid()).forEach(GulooStampCateConj::delete);
			dataService.deleteGulooStamp(gs.getUid());
		}
		for (Entity e : Arrays.asList(e1, e2))
			if (e != null)
				dataService.deleteEntity(e.getUid());
		for (GulooStampCate c : Arrays.asList(c1, c2))
			if (c != null)
				dataService.deleteGulooStampCate(c.getUid());
	}

	// -------------------------------------------------------------------------------
	/** 改成：DATE2 / desp2 / remark2，成員 e2，標籤 c2 */
	private static void appendChanges(GulooStampBpuUpdate _bpu, Entity _e2, GulooStampCate _c2) {
		_bpu.appendStampDate(DATE2).appendDesp("desp2").appendRemark("remark2");
		_bpu.appendEntityList(new java.util.ArrayList<>(List.of(_e2)));
		_bpu.appendCateList(new java.util.ArrayList<>(List.of(_c2)));
	}

	private GulooStampBpuUpdate newBpu() {
		GulooStampBpuUpdate bpu = BpuFacade.getInstance().getBuilder(MbrBpuType.GulooStamp$Update,
				dataService.loadGulooStamp(gs.getUid()));
		assertNotNull(bpu);
		return bpu;
	}

	/** 由 DB 重新讀取，確認咕溜欄位與關聯 */
	private void assertDb(long _date, String _desp, String _remark, Set<String> _entityUids, Set<String> _cateUids) {
		GulooStamp db = dataService.loadGulooStamp(gs.getUid());
		assertEquals(_date, db.getStampDate());
		assertEquals(_desp, db.getDesp());
		assertEquals(_remark, db.getRemark());
		assertEquals(_entityUids, dataService.loadGulooStampEntityConjList(gs.getUid()).stream()
				.map(GulooStampEntityConj::getEntityUid).collect(Collectors.toSet()));
		assertEquals(_cateUids, dataService.loadGulooStampCateConjList(gs.getUid()).stream()
				.map(GulooStampCateConj::getCateUid).collect(Collectors.toSet()));
	}

	private void assertOriginal() {
		assertDb(DATE1, "desp1", "remark1", set(e1.getUid()), set(c1.getUid()));
	}

	private static Set<String> set(String... _uids) {
		return new HashSet<>(Arrays.asList(_uids));
	}

	// -------------------------------------------------------------------------------
	@Test
	public void testUpdateAll() {
		GulooStampBpuUpdate bpu = newBpu();
		appendChanges(bpu, e2, c2);
		assertTrue(bpu.verify(new StringBuilder()));
		assertEquals(Boolean.TRUE, bpu.build(new StringBuilder(), null));
		assertDb(DATE2, "desp2", "remark2", set(e2.getUid()), set(c2.getUid()));
	}

	/** 最後一步(標籤)已寫入後失敗：日期/簡述/備註、成員、標籤全部還原 */
	@Test
	public void testRollbackWhenLastStepFails() {
		GulooStampBpuUpdate bpu = new GulooStampBpuUpdate() {
			@Override
			protected boolean updateCateConj(TimeTraveler tt) {
				assertTrue(super.updateCateConj(tt)); // 標籤的變更確實寫入了
				return false; // 模擬之後發生錯誤
			}
		};
		bpu.init(new Object[] { dataService.loadGulooStamp(gs.getUid()) });
		appendChanges(bpu, e2, c2);

		assertFalse(Boolean.TRUE.equals(bpu.build(new StringBuilder(), null)));
		assertOriginal();
	}

	/** 中間一步(成員)失敗：已更新的日期/簡述/備註與成員還原，標籤這步不會執行 */
	@Test
	public void testRollbackWhenMiddleStepFails() {
		GulooStampBpuUpdate bpu = new GulooStampBpuUpdate() {
			@Override
			protected boolean updateEntityConj(TimeTraveler tt) {
				assertTrue(super.updateEntityConj(tt));
				return false;
			}

			@Override
			protected boolean updateCateConj(TimeTraveler tt) {
				throw new AssertionError("前一步失敗後不應執行標籤這步");
			}
		};
		bpu.init(new Object[] { dataService.loadGulooStamp(gs.getUid()) });
		appendChanges(bpu, e2, c2);

		assertFalse(Boolean.TRUE.equals(bpu.build(new StringBuilder(), null)));
		assertOriginal();
	}

	/** 開始編輯後別人加了成員 e2；寫入時以當下的 DB 計算差異，結果與 BPU 內容一致(只剩 e1) */
	@Test
	public void testDiffAgainstCurrentDb() {
		GulooStampBpuUpdate bpu = newBpu(); // appendBase 時載入(並快取)原本的關聯：只有 e1
		assertNotNull(GulooStampEntityConj.create(gs.getUid(), e2.getUid())); // 編輯期間的外部修改

		assertEquals(Boolean.TRUE, bpu.build(new StringBuilder(), null)); // BPU 內容未改：成員 e1、標籤 c1
		assertOriginal();
	}
}
