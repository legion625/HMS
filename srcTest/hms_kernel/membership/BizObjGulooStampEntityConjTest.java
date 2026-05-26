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

public class BizObjGulooStampEntityConjTest extends AbstractHmsKernelInitTest {
	private static MembershipDataService dataService = DataServiceFactory.getInstance()
			.getService(MembershipDataService.class);

	private String targetUid;

	private Target target1, target2;

	@Before
	public void initMethod() {
		target1 = new Target("stampUid1", "entityUid1");
		target2 = new Target("stampUid2", "entityUid2");
	}

	@Test
	public void testCRUD() throws Throwable {
		testCreateGulooStampEntityConj();
		testUpdateGulooStampEntityConj();
		testDeleteGulooStampEntityConj();
	}

	@Test
	@Ignore
	public void testCreateGulooStampEntityConj() throws Throwable {
		/* create */
		GulooStampEntityConj obj = GulooStampEntityConj.newInstance(target1.stampUid, target1.entityUid);
		PropertyUtils.copyProperties(obj, target1);
		assert obj.save();
		targetUid = obj.getUid();
		/* load */
		obj = dataService.loadGulooStampEntityConj(targetUid);
		TestUtil.assertObjEqual(target1, obj);
	}

	@Test
	@Ignore
	public void testUpdateGulooStampEntityConj() throws Throwable {
		GulooStampEntityConj obj = dataService.loadGulooStampEntityConj(targetUid);
		PropertyUtils.copyProperties(obj, target2);
		assert obj.save();
		/* load */
		obj = dataService.loadGulooStampEntityConj(targetUid);
		TestUtil.assertObjEqual(target2, obj);
	}

	@Test
	@Ignore
	public void testDeleteGulooStampEntityConj() {
		assert dataService.loadGulooStampEntityConj(targetUid).delete();
	}

	// -------------------------------------------------------------------------------
	public class Target {
		private String stampUid;
		private String entityUid;

		private Target(String stampUid, String entityUid) {
			this.stampUid = stampUid;
			this.entityUid = entityUid;
		}

		public String getStampUid() {
			return stampUid;
		}

		public String getEntityUid() {
			return entityUid;
		}

	}
}
