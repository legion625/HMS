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

public class BizObjGulooStampCateTest extends AbstractHmsKernelInitTest {
	private static MembershipDataService dataService = DataServiceFactory.getInstance()
			.getService(MembershipDataService.class);

	private String targetUid;

	private Target target1, target2;

	@Before
	public void initMethod() {
		target1 = new Target("name1", "color1");
		target2 = new Target("name2", "color2");
	}

	@Test
	public void testCRUD() throws Throwable {
		testCreateGulooStampCate();
		testUpdateGulooStampCate();
		testDeleteGulooStampCate();
	}

	@Test
	@Ignore
	public void testCreateGulooStampCate() throws Throwable {
		/* create */
		GulooStampCate obj = GulooStampCate.newInstance();
		PropertyUtils.copyProperties(obj, target1);
		assert obj.save();
		targetUid = obj.getUid();
		/* load */
		obj = dataService.loadGulooStampCate(targetUid);
		TestUtil.assertObjEqual(target1, obj);
	}

	@Test
	@Ignore
	public void testUpdateGulooStampCate() throws Throwable {
		GulooStampCate obj = dataService.loadGulooStampCate(targetUid);
		PropertyUtils.copyProperties(obj, target2);
		assert obj.save();
		/* load */
		obj = dataService.loadGulooStampCate(targetUid);
		TestUtil.assertObjEqual(target2, obj);
	}

	@Test
	@Ignore
	public void testDeleteGulooStampCate() {
		assert dataService.loadGulooStampCate(targetUid).delete();
	}

	// -------------------------------------------------------------------------------
	public class Target {
		private String name;
		private String color;

		private Target(String name, String color) {
			this.name = name;
			this.color = color;
		}

		public String getName() {
			return name;
		}

		public String getColor() {
			return color;
		}

	}
}
