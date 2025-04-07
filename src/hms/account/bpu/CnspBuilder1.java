package hms.account.bpu;

import java.time.LocalDate;

import hms_kernel.account.Consumption;
import hms_kernel.account.DirectionEnum;
import hms_kernel.account.Payment;
import hms_kernel.account.PaymentTypeEnum;
import hms_kernel.account.TypeEnum;
import legion.biz.Bpu;
import legion.util.TimeTraveler;

public class CnspBuilder1 extends CnspBuilder {

	/* base */
	// none

	/* data */
	// none

	// -------------------------------------------------------------------------------
	@Override
	protected CnspBuilder1 appendBase() {
		/* base */
		// none

		/* data */
		// none

		return this;
	}

	// -------------------------------------------------------------------------------
	@Override
	public CnspBuilder1 appendType(TypeEnum type) {
		return (CnspBuilder1) super.appendType(type);
	}

	@Override
	public CnspBuilder1 appendDirection(DirectionEnum direction) {
		return (CnspBuilder1) super.appendDirection(direction);
	}

	@Override
	public CnspBuilder1 appendAmount(int amount) {
		return (CnspBuilder1) super.appendAmount(amount);
	}

	@Override
	public CnspBuilder1 appendDescription(String description) {
		return (CnspBuilder1) super.appendDescription(description);
	}

	@Override
	public CnspBuilder1 appendPaymentType(PaymentTypeEnum paymentType) {
		return (CnspBuilder1) super.appendPaymentType(paymentType);
	}

	@Override
	public CnspBuilder1 appendDate(LocalDate date) {
		return (CnspBuilder1) super.appendDate(date);
	}

	// -------------------------------------------------------------------------------
	@Override
	protected Consumption buildProcess(TimeTraveler _tt) {
		TimeTraveler tt = new TimeTraveler();

		//
		Consumption cnsp = buildCnsp(tt);
		if (cnsp == null) {
			tt.travel();
			log.error("buildCnsp return null.");
			return null;
		} // copy sites inside

		// 若付款方式為「現金」，直接增加一筆付款。
		if (PaymentTypeEnum.CASH == cnsp.getPaymentType()) {
			Payment pm = Payment.create(cnsp.getUid(), cnsp.getDate(), cnsp.getAmount());
			if (pm == null) {
				tt.travel();
				log.error("Payment.create return null.");
				return null;
			}
			tt.addSite("revert Payment.create", () -> pm.delete());
			log.info("Payment.create [{}][{}]", pm.getUid(), pm.getInfo());
		}

		//
		if (_tt != null)
			_tt.copySitesFrom(tt);

		return cnsp;
	}

}
