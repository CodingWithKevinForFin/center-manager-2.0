package com.f1.pofo.oms;

import com.f1.base.VID;
import com.f1.base.ValuedEnum;
import com.f1.base.ValuedEnumCache;

/**
 * 
 * Enumerates the various types of oms actions. Please note the specialized {@link #SNAPSHOT} action which is the result of a snapshot request.
 */
@VID("F1.OM.OA")
public enum OmsAction implements ValuedEnum<Integer> {
	NEW_ORDER_RCVD(0),

	/** 1 */
	REPLACE_ORDER(1),

	/** 2 */
	CANCEL_ORDER(2),

	/** 3 */
	CHILD_REPLACE_SUCCEEDED(3),

	/** 4 */
	CHILD_CANCEL_SUCCEEDED(4),

	/** 5 */
	CHILD_PARTIALLY_FILLED(5),

	/** 6 */
	CHILD_FULLY_FILLED(6),

	/** 7 */
	CHILD_UNSOLICITED_CANCEL(7),

	/** 8 */
	CHILD_ORDER_ACKNOWLEDGED(8),

	/** 9 */
	CHILD_FORCE_CANCELLED(9),

	/** 10 */
	CHILD_REJECTED(10),

	/** 11 */
	CHILD_REPLACE_REJECTED(11),

	/** 12 */
	CHILD_CANCEL_REJECTED(12),

	/** 13 */
	CHILD_EXEC_BUST(13),

	/** 14 */
	CHILD_EXEC_CORRECT(14),

	/** 15 */
	FORCE_CANCEL_ORDER(15),

	/** 16 */
	ATTACH_EXECUTION(16),

	/** 17 */
	PAUSE_ORDER(17),

	/** 18 */
	ORDER_CANCELLED(18),

	/** 19 */
	ORDER_REPLACED(19),

	/** 20 */
	ACKNOWLEDGE_ORDER(20),

	/** 21 */
	REJECT_ORDER(21),

	/** 22 */
	NEW_CHILD_ORDER(22),

	/** 23 */
	REPLACE_CHILD_ORDER(23),

	/** 24 */
	UNSOLICITED_CANCEL(24),

	/** 25 */
	FILL_RECEIVED(25),

	/** 26 */
	ORDER_ACKED(26),

	/** 27 */
	REPLACE_REJECTED(27),

	/** 28 */
	CANCEL_CHILD_ORDER(28),

	/** 29 */
	SNAPSHOT(29),

	/** 30 */
	CUSTOM_DATA_UPDATED(30),

	/** 31 */
	CANCEL_ALL_CHILD_ORDERS(31),

	/** 32 */
	ALL_CHILDREN_CANCELLED(32),

	/** 33 */
	DONE_FOR_DAY(33),

	/** 34 */
	ATTACH_BUST_EXECUTION(34),

	/** 35 */
	ORDER_RESTATED(35),

	/** 36 */
	ATTACH_CORRECT_EXECUTION(36),

	/** 37 */
	CHILD_EXEC_STATUS(37),

	/** 9999 */
	MAX_VALUE(9998);

	private static ValuedEnumCache<Integer, OmsAction> cache;
	private int val;

	private OmsAction(int value) {
		this.val = value;
	}

	@Override
	public Integer getEnumValue() {
		return val;
	}

	static {
		cache = ValuedEnumCache.getCache(OmsAction.class);
	}

	public static OmsAction get(int key) {
		return cache.getValue(key);
	}

	public static OmsAction get(int key, OmsAction dflt) {
		return cache.getValue(key, dflt);
	}

}
