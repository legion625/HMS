package hms_kernel.membership;

import java.time.LocalDate;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import hms_kernel.AbstractHmsKernelInitTest;
import hms_kernel.TestUtil;
import hms_kernel.data.membership.MembershipDataService;
import hms_kernel.membership.BizObjEntityTest.Target;
import hms_kernel.membership.type.EntityType;
import legion.DataServiceFactory;
import legion.util.DateUtil;

public class BizObjGulooStampCateConjTest extends AbstractHmsKernelInitTest {
	private static MembershipDataService dataService = DataServiceFactory.getInstance()
			.getService(MembershipDataService.class);

	private String targetUid;

	private Target target1, target2;

	@Before
	public void initMethod() {
		target1 = new Target("stampUid1", "cateUid1");
		target2 = new Target("stampUid2", "cateUid2");
	}

	@Test
	public void testCRUD() throws Throwable {
		testCreateGulooStampCateConj();
		testUpdateGulooStampCateConj();
		testDeleteGulooStampCateConj();
	}

	@Test
	@Ignore
	public void testCreateGulooStampCateConj() throws Throwable {
		/* create */
		GulooStampCateConj obj = GulooStampCateConj.newInstance(target1.stampUid, target1.cateUid);
		PropertyUtils.copyProperties(obj, target1);
		assert obj.save();
		targetUid = obj.getUid();
		/* load */
		obj = dataService.loadGulooStampCateConj(targetUid);
		TestUtil.assertObjEqual(target1, obj);
	}

	@Test
	@Ignore
	public void testUpdateGulooStampCateConj() throws Throwable {
		GulooStampCateConj obj = dataService.loadGulooStampCateConj(targetUid);
		PropertyUtils.copyProperties(obj, target2);
		assert obj.save();
		/* load */
		obj = dataService.loadGulooStampCateConj(targetUid);
		TestUtil.assertObjEqual(target2, obj);
	}

	@Test
	@Ignore
	public void testDeleteGulooStampCateConj() {
		assert dataService.loadGulooStampCateConj(targetUid).delete();
	}

	// -------------------------------------------------------------------------------
	public class Target {
		private String stampUid;
		private String cateUid;

		private Target(String stampUid, String cateUid) {
			this.stampUid = stampUid;
			this.cateUid = cateUid;
		}

		public String getStampUid() {
			return stampUid;
		}

		public String getCateUid() {
			return cateUid;
		}

	}
}
