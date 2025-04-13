package hms_kernel;

import org.junit.Test;

import legion.LegionContext;
import legion.SystemInfoDefault;

public class HmsKernelTest extends AbstractHmsKernelInitTest{
	@Test
	public void test() {
		log.debug("SystemInfoDefault.getInstance().getVersion(): {}", SystemInfoDefault.getInstance().getVersion());
		log.debug("LegionContext.getInstance().getVersion(): {}", LegionContext.getInstance().getVersion());
		
	}

}
