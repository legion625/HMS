package hms.web.zk;

import java.util.function.Consumer;

import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;

public class HmsMessageBox {
	public static void info(String _msg) {
		Messagebox.show(_msg, "HMS", Messagebox.OK, Messagebox.INFORMATION);
	}

	public static void exclamation(String _msg) {
		Messagebox.show(_msg, "HMS", Messagebox.OK, Messagebox.EXCLAMATION);
	}
	
	public static void exclamation(String _msg, Runnable _r) {
		Messagebox.show(_msg, "HMS", Messagebox.OK, Messagebox.EXCLAMATION, e -> {
			if ((int) e.getData() == Messagebox.OK)
//				_csmAction.accept(null);
				_r.run();
		});
	}

	public static void error(String _msg) {
		Messagebox.show(_msg, "HMS", Messagebox.OK, Messagebox.ERROR);
	}

	public static void confirm(String _msg, Runnable _r) {
		Messagebox.show(_msg, "HMS", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, e -> {
			if ((int) e.getData() == Messagebox.OK)
				_r.run();
		});
	}
}
