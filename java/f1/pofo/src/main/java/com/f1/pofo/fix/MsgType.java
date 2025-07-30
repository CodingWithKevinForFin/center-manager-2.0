package com.f1.pofo.fix;

import com.f1.base.VID;
import com.f1.base.ValuedEnum;
import com.f1.base.ValuedEnumCache;

/**
 * type of fix message. Fix tag 35
 * 
 */
@VID("F1.FX.MT")
public enum MsgType implements ValuedEnum<Character> {

	/** Fix code D */
	NEW_ORDER_SINGLE('D'),

	/** Fix code F */
	CANCEL_REQUEST('F'),

	/** Fix code G */
	REPLACE_REQUEST('G'),

	/** Fix code 8 */
	EXECUTION_REPORT('8'),

	/** Fix code 9 */
	CANCEL_REJECT('9');

	private static ValuedEnumCache<Character, MsgType> cache;
	private Character val;

	MsgType(char val) {
		this.val = val;
	}

	/**
	 * @return fix tag value (35)
	 */
	public static int getTagValue() {
		return 35;
	}

	@Override
	public Character getEnumValue() {
		return val;
	}

	static {
		cache = ValuedEnumCache.getCache(MsgType.class);
	}

	public static MsgType get(char msgTypeStr) {
		return cache.getValueByPrimitive(msgTypeStr);
	}

	public static MsgType get(char msgTypeStr, MsgType dflt) {
		return cache.getValueByPrimitive(msgTypeStr, dflt);
	}
}
