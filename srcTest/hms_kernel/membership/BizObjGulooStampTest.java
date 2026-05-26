package hms_kernel.membership;

import java.time.LocalDate;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import hms_kernel.AbstractHmsKernelInitTest;
import hms_kernel.TestUtil;
import hms_kernel.data.membership.MembershipDataService;
import legion.DataServiceFactory;
import legion.util.DateUtil;

public class BizObjGulooStampTest extends AbstractHmsKernelInitTest {
	private static MembershipDataService dataService = DataServiceFactory.getInstance()
			.getService(MembershipDataService.class);

	private String targetUid;

	private Target target1, target2;

	@Before
	public void initMethod() {
		long l1 = DateUtil.toLong(LocalDate.now());
		long l2 = DateUtil.toLong(LocalDate.now().plusDays(1));
		target1 = new Target(l1, "desp1", "remark1");
		target2 = new Target(l2, "desp2", "remark2");
	}

	@Test
	public void testCRUD() throws Throwable {
		testCreateGulooStamp();
		testUpdateGulooStamp();
		testDeleteGulooStamp();
	}

	@Test
	@Ignore
	public void testCreateGulooStamp() throws Throwable {
		/* create */
		GulooStamp obj = GulooStamp.newInstance();
		PropertyUtils.copyProperties(obj, target1);
		assert obj.save();
		targetUid = obj.getUid();
		/* load */
		obj = dataService.loadGulooStamp(targetUid);
		TestUtil.assertObjEqual(target1, obj);
	}

	@Test
	@Ignore
	public void testUpdateGulooStamp() throws Throwable {
		GulooStamp obj = dataService.loadGulooStamp(targetUid);
		PropertyUtils.copyProperties(obj, target2);
		assert obj.save();
		/* load */
		obj = dataService.loadGulooStamp(targetUid);
		TestUtil.assertObjEqual(target2, obj);
	}

	@Test
	@Ignore
	public void testDeleteGulooStamp() {
		assert dataService.loadGulooStamp(targetUid).delete();
	}

	// -------------------------------------------------------------------------------
	public class Target {
		private long stampDate; // 日期
		private String desp; // 簡述
		private String remark; // 備註

		private Target(long stampDate, String desp, String remark) {
			this.stampDate = stampDate;
			this.desp = desp;
			this.remark = remark;
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


	}
}
