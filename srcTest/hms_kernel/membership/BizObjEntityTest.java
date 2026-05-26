package hms_kernel.membership;

import java.time.LocalDate;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import hms_kernel.AbstractHmsKernelInitTest;
import hms_kernel.TestUtil;
import hms_kernel.data.membership.MembershipDataService;
import hms_kernel.membership.type.EntityType;
import legion.DataServiceFactory;
import legion.util.DateUtil;

public class BizObjEntityTest extends AbstractHmsKernelInitTest {
	private static MembershipDataService dataService = DataServiceFactory.getInstance()
			.getService(MembershipDataService.class);

	private String targetUid;

	private Target target1, target2;

	@Before
	public void initMethod() {
		long l1 = DateUtil.toLong(LocalDate.now());
		long l2 = DateUtil.toLong(LocalDate.now().plusDays(1));
		target1 = new Target("alias1", EntityType.HUMAN, l1);
		target2 = new Target("alias2", EntityType.VEHICLE, l2);
	}

	@Test
	public void testCRUD() throws Throwable {
		testCreateEntity();
		testUpdateEntity();
		testDeleteEntity();
	}

	@Test
	@Ignore
	public void testCreateEntity() throws Throwable {
		/* create */
		Entity obj = Entity.newInstance();
		PropertyUtils.copyProperties(obj, target1);
		assert obj.save();
		targetUid = obj.getUid();
		/* load */
		obj = dataService.loadEntity(targetUid);
		TestUtil.assertObjEqual(target1, obj);
	}

	@Test
	@Ignore
	public void testUpdateEntity() throws Throwable {
		Entity obj = dataService.loadEntity(targetUid);
		PropertyUtils.copyProperties(obj, target2);
		assert obj.save();
		/* load */
		obj = dataService.loadEntity(targetUid);
		TestUtil.assertObjEqual(target2, obj);
	}

	@Test
	@Ignore
	public void testDeleteEntity() {
		assert dataService.loadEntity(targetUid).delete();
	}

	// -------------------------------------------------------------------------------
	public class Target {
		private String alias; // 簡稱、暱稱
		private EntityType type;
		private long birthDate;

		private Target(String alias, EntityType type, long birthDate) {
			this.alias = alias;
			this.type = type;
			this.birthDate = birthDate;
		}

		public String getAlias() {
			return alias;
		}

		public EntityType getType() {
			return type;
		}

		public long getBirthDate() {
			return birthDate;
		}

	}
}
