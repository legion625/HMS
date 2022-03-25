import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import hms_kernel.account.AccountService;
import hms_kernel.account.Consumption;
import hms_kernel.account.ConsumptionSearchParam;

public class KernelTest {

	@Test
	public void test() {
//		fail("Not yet implemented");
		List<Consumption> list = AccountService.getInstance().searchConsumptions(new ConsumptionSearchParam(), false);
		System.out.println("list.size(): " + list.size());
	}

}
