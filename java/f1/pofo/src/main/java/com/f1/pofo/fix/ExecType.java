package com.f1.pofo.fix;

import com.f1.base.VID;
import com.f1.base.ValuedEnum;
import com.f1.base.ValuedEnumCache;

/**
 * fix tag 150
 */
@VID("F1.FX.ET")
public enum ExecType implements ValuedEnum<Character> {
	/** Fix code A */
	PENDING_NEW('A'),

	/** Fix code 8 */
	REJECTED('8'),

	/** Fix code 0 */
	ACKNOWLEDGED('0'),

	/** Fix code 6 */
	PENDING_CANCEL('6'),

	/** Fix code 4 */
	CANCELLED('4'),

	/** Fix code 1 */
	PARTIAL('1'),

	/** Fix code 2 */
	FILLED('2'),

	/** Fix code E */
	PENDING_REPLACE('E'),

	/** Fix code 5 */
	REPLACED('5'),

	/** Fix code 3 */
	DONE_FOR_DAY('3'),

	RESTATED('D');

	private static ValuedEnumCache<Character, ExecType> cache;
	private char execType;

	private ExecType(char execType) {
		this.execType = execType;
	}

	static {
		cache = ValuedEnumCache.getCache(ExecType.class);
	}

	public static ExecType get(char c) {
		return cache.getValueByPrimitive(c);
	}

	public static ExecType get(char c, ExecType dflt) {
		return cache.getValueByPrimitive(c, dflt);
	}

	@Override
	public Character getEnumValue() {
		return execType;
	}

}
