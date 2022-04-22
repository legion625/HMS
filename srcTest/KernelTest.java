import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import hms_kernel.account.AccountService;
import hms_kernel.account.Consumption;
import hms_kernel.account.ConsumptionSearchParam;
import hms_kernel.data.account.AccountDataService;
import legion.kernel.LegionObject;

public class KernelTest {

	@Test
	public void test() {
//		fail("Not yet implemented");
		List<Consumption> list = AccountService.getInstance().searchConsumptions(new ConsumptionSearchParam(), false);
		System.out.println("list.size(): " + list.size());
		
		Consumption c1 = list.get(0);
//		Object c2 = list.get(1);
		System.out.println(c1.getClass());
		System.out.println(c1.getClass().getDeclaringClass());
//		System.out.println(c2.getClass());
		
//		System.out.println(c1.getClass().isInstance(c2));
//		System.out.println(c2.getClass().isInstance(c1));
		
		List<Consumption> list2 = AccountService.getInstance().searchConsumptions(new ConsumptionSearchParam(), false);
		Object c3 = list2.get(0);
		System.out.println("c1.getUid(): "+c1.getUid());
		System.out.println("c3.getUid(): "+((LegionObject) c3).getUid());
		System.out.println(c1.equals(c3));
		System.out.println(c1.getClass()==c3.getClass());
//		System.out.println((c1 instanceof c2.getClass()));
	}
	

}
