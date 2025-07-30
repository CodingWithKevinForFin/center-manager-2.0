package com.f1.pofo.oms;

import com.f1.base.VID;
import com.f1.base.ValuedEnum;
import com.f1.base.ValuedEnumCache;

/**
 * fix tag 54
 * 
 */
@VID("F1.FX.SD")
public enum Side implements ValuedEnum<Character> {
	/** 1 */
	BUY('1'),

	/** 2 */
	SELL('2'),

	/** 3 */
	BUY_MINUS('3'),

	/** 5 */
	SHORT_SELL('5'),

	/** 6 */
	SHORT_SELL_EXEMPT('6');

	private static ValuedEnumCache<Character, Side> cache;
	private char side;

	Side(char side) {
		this.side = side;
	}

	public static Side get(char c) {
		return cache.getValueByPrimitive(c);
	}

	public static Side get(char c, Side dflt) {
		return cache.getValueByPrimitive(c, dflt);
	}

	@Override
	public Character getEnumValue() {
		return side;
	}

	static {
		cache = ValuedEnumCache.getCache(Side.class);
	}

}
