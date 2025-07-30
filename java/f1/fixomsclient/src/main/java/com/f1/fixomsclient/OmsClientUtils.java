package com.f1.fixomsclient;

import com.f1.pofo.fix.OrdStatus;
import com.f1.utils.MH;

public class OmsClientUtils {

	/**
	 * Gets the most important bit from an {@link OrdStatus} mask.
	 * 
	 * @param statuses
	 *            a bit set of all Ord Statuses.
	 * @param dflt
	 * @return the most important (from a user perspective) bit out of all the bits set
	 */
	static public OrdStatus getMostImportantOrdStatus(int statuses, OrdStatus dflt) {
		return OrdStatus.get(MH.indexOfLastBitSet(statuses), dflt);
	}

	/**
	 * @param status
	 *            status to test
	 * @return true if cancelled, filled or rejected
	 */
	public static boolean isCompleted(int status) {
		return OrdStatus.CANCELLED.isSet(status) || OrdStatus.FILLED.isSet(status) || OrdStatus.REJECTED.isSet(status);
	}

	/**
	 * @param status
	 *            status to test
	 * @return true if not pending, not cancelled, not filled or not rejected
	 */
	public static boolean canCancel(int status) {
		if (isCompleted(status))
			return false;

		if (OrdStatus.PENDING_CXL.isSet(status) || OrdStatus.PENDING_RPL.isSet(status))
			return false;

		return true;
	}
}
