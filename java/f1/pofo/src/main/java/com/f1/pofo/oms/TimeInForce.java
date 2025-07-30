package com.f1.pofo.oms;

import com.f1.base.VID;
import com.f1.base.ValuedEnum;
import com.f1.base.ValuedEnumCache;

/**
 * fix tag 59
 */
@VID("F1.FX.TF")
public enum TimeInForce implements ValuedEnum<Character> {
	DAY('0'),

	/** 3 */
	IOC('3'),

	/** 7 */
	ON_CLOSE('7'),

	/** 2 */
	ON_OPEN('2'),

	/** 1 */
	GTC('1'),

	/** 6 */
	GTD('6'),

	/** 4 */
	FILL_OR_KILL('4');

	final private static ValuedEnumCache<Character, TimeInForce> cache;
	private char tif;

	private TimeInForce(char tif) {
		this.tif = tif;
	}

	static {
		cache = ValuedEnumCache.getCache(TimeInForce.class);
	}

	public static TimeInForce get(char tif) {
		return cache.getValueByPrimitive(tif);
	}

	public static TimeInForce get(char tif, TimeInForce dflt) {
		return cache.getValueByPrimitive(tif, dflt);
	}

	public static TimeInForce getTIF(String ordType, String timeInForce) {
		char tif = timeInForce.charAt(0);
		TimeInForce ret = get(tif);
		if (ordType != null) {
			char type = ordType.charAt(0);
			switch (type) {
				case '5':
				case 'B':
					ret = ON_CLOSE;
			}
		}
		return ret;
	}

	public static Character getTIF(OrderType ordType, TimeInForce tif) {

		if (tif == null)
			return null;
		switch (tif) {
			case ON_CLOSE:
				return '0';
		}
		return tif.getEnumValue();
	}

	@Override
	public Character getEnumValue() {
		return tif;
	}
}
