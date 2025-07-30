package com.f1.pofo.fix;

import com.f1.base.VID;
import com.f1.base.ValuedEnum;
import com.f1.base.ValuedEnumCache;

/**
 * the status of an order (tag 39)
 * 
 */
@VID("F1.FX.OS")
public enum OrdStatus implements ValuedEnum<Byte> {
	/** fix code 6. Bit 2 (4) */
	PENDING_CXL('6', (byte) 2),

	/** fix code E. Bit 3 (8) */
	PENDING_RPL('E', (byte) 3),

	/** fix code 2. Bit 4 (16) */
	FILLED('2', (byte) 4),

	/** fix code 4. Bit 5 (32) */
	CANCELLED('4', (byte) 5),

	/** fix code 1. Bit 6 (64) */
	PARTIAL('1', (byte) 6),

	/** fix code 5. Bit 7 (128) */
	REPLACED('5', (byte) 7),

	/** fix code 0. Bit 8 (256) */
	ACKED('0', (byte) 8),

	/** fix code 8. Bit 9 (512) */
	REJECTED('8', (byte) 9),

	/** fix code A. Bit 10 (1024) */
	PENDING_ACK('A', (byte) 10),

	/** code X. */
	MAX_VALUE('X', (byte) 255);

	final private static ValuedEnumCache<Byte, OrdStatus> cache;
	private char fixordstatus;
	private byte bitposition;
	public final int mask;

	private OrdStatus(char fixordstatus, byte bitposition) {
		this.fixordstatus = fixordstatus;
		this.bitposition = bitposition;
		this.mask = 1 << bitposition;
	}

	public int getIntMask() {
		return mask;
	}

	public char getFixOrdStatus() {
		return fixordstatus;
	}

	public static int getMask(char fixordstatus) {
		switch (fixordstatus) {
			case 'A':
				return PENDING_ACK.mask;
			case '0':
				return ACKED.mask;
			case '1':
				return PARTIAL.mask;
			case '2':
				return FILLED.mask;
			case '6':
				return PENDING_CXL.mask;
			case '4':
				return CANCELLED.mask;
			case 'E':
				return PENDING_RPL.mask;
			case '5':
				return REPLACED.mask;
			case '8':
				return REJECTED.mask;
		}
		return -1;
	}

	/**
	 * @param status
	 *            status to test
	 * @return true if cancelled, filled or rejected
	 */
	public static boolean isCompleted(int status) {
		return CANCELLED.isSet(status) || FILLED.isSet(status) || REJECTED.isSet(status);
	}

	public static OrdStatus get(int ordStatus) {
		return cache.getValueByPrimitive(ordStatus);
	}

	public static OrdStatus get(int ordStatus, OrdStatus dflt) {
		return cache.getValueByPrimitive(ordStatus, dflt);
	}

	static {
		cache = ValuedEnumCache.getCache(OrdStatus.class);
	}

	/**
	 * return the bit position
	 */
	@Override
	public Byte getEnumValue() {
		return bitposition;
	}

	public boolean isSet(int bits) {
		return (bits & mask) == mask;
	}

}
