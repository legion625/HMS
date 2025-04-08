package hms.account.bpu;

import java.util.List;

import hms_kernel.account.Consumption;
import hms_kernel.account.Payment;
import legion.biz.Bpu;
import legion.util.TimeTraveler;

public class CnspBpuDel extends Bpu<Boolean> {

	/* base */
	private Consumption cnsp;

	/* data */
	// none

	@Override
	protected CnspBpuDel appendBase() {
		/* base */
		cnsp = (Consumption) args[0];

		/* data */
		// none

		return this;
	}

	@Override
	public boolean validate(StringBuilder _msg) {
		return true;
	}

	@Override
	public boolean verify(StringBuilder _msg) {
		return true;
	}

	// -------------------------------------------------------------------------------
	public Consumption getCnsp() {
		return cnsp;
	}

	// -------------------------------------------------------------------------------
	@Override
	protected Boolean buildProcess(TimeTraveler _tt) {
		/* delete Consumption */
		if (!cnsp.delete()) {
			log.error("cnsp.delete return false. [{}][{}]", getCnsp().getUid(), getCnsp().getInfo());
			return false;
		}

		/* delete Payment list */
		List<Payment> pmList = getCnsp().getPaymentList(true);
		for (Payment pm : pmList) {
			if (!pm.delete()) {
				log.error("payment.delete return false. [{}][{}]", pm.getUid(), pm.getInfo());
				return false;
			}
		}

		return true;
	}

}
