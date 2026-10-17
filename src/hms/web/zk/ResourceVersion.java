package hms.web.zk;

import java.io.File;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.WebApps;

/**
 * 產生靜態資源(css/js)的版本號，附加在網址後面(?v=...)，讓檔案更新後瀏覽器一定會重新下載。
 *
 * <pre>
 * &lt;?xel-method prefix="hms" name="ver" class="hms.web.zk.ResourceVersion" signature="java.lang.String of(java.lang.String)"?&gt;
 * &lt;script src="/js/swipe.js?v=${hms:ver('/js/swipe.js')}" /&gt;
 * </pre>
 */
public class ResourceVersion {

	/** 取不到檔案修改時間時的備用值：應用程式啟動時間(每次重新部署都會改變)。 */
	private static final String START_TIME = String.valueOf(System.currentTimeMillis());

	/**
	 * @param _path 相對於 web 根目錄的路徑，例如 /js/swipe.js
	 * @return 檔案的最後修改時間；取不到時回傳應用程式啟動時間。
	 */
	public static String of(String _path) {
		WebApp webApp = WebApps.getCurrent();
		String realPath = webApp == null ? null : webApp.getRealPath(_path);
		if (realPath != null) {
			File file = new File(realPath);
			if (file.isFile())
				return String.valueOf(file.lastModified());
		}
		return START_TIME;
	}
}
