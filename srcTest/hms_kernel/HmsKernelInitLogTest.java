package hms_kernel;

import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;

public class HmsKernelInitLogTest {
	protected static Logger log = LoggerFactory.getLogger(TestLogMark.class);
	private static volatile boolean logFlag = false;

	@BeforeClass
	public static void setupBeforeClass() throws Exception {
		// 啟始系統設定
		if (!logFlag) {
			synchronized (HmsKernelInitLogTest.class) {
				if (!logFlag) {
					initLog();
					logFlag = true;
				}
			}
		}
		assertNotNull("log null", log);
	}

	static void initLog() throws Exception {
		// 啟始logback
		String logfile = "srcTest\\hms_kernel\\conf\\logback-conf.xml";
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		JoranConfigurator configurator = new JoranConfigurator();
		configurator.setContext(lc);
		// to clear any previous configuration
		lc.reset();
		configurator.doConfigure(logfile);

		log = LoggerFactory.getLogger(TestLogMark.class);
	}
}
