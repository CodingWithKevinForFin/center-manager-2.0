package com.f1.fix.oms.schema;

import com.f1.base.VID;
import com.f1.base.ValuedEnum;
import com.f1.base.ValuedEnumCache;

/**
 * 
 * the status of an oms order
 * 
 */
@VID("F1.OM.OS")
public enum OmsOrderStatus implements ValuedEnum<Byte> {
	/** 0 */
	PENDING_PAUSE(0),

	/** 1 */
	PAUSED(1),

	/** 2 */
	PENDING_CXL(2),

	/** 3 */
	PENDING_RPL(3),

	/** 4 */
	FILLED(4),

	/** 5 */
	CANCELLED(5),

	/** 6 */
	PARTIAL(6),

	/** 7 */
	REPLACED(7),

	/** 8 */
	ACKED(8),

	/** 9 */
	REJECTED(9),

	/** 10 */
	PENDING_ACK(10),

	/** 11 */
	UNINITIALIZED(11),

	/** 255 */
	MAX_VALUE(255);

	final private static ValuedEnumCache<Byte, OmsOrderStatus> cache;
	private byte position;
	private int mask;

	public int getMask() {
		return mask;
	}

	static {
		cache = ValuedEnumCache.getCache(OmsOrderStatus.class);
	}

	public static OmsOrderStatus get(int status) {
		return cache.getValueByPrimitive(status);
	}

	public static OmsOrderStatus get(int status, OmsOrderStatus default_) {
		return cache.getValueByPrimitive(status, default_);
	}

	private OmsOrderStatus(int position) {
		this.position = (byte) position;
		this.mask = (1 << position);
	}

	@Override
	public Byte getEnumValue() {
		return position;
	}

}
