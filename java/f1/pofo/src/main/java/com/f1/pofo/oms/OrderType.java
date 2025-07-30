package com.f1.pofo.oms;

import com.f1.base.VID;
import com.f1.base.ValuedEnum;

/**
 * 
 * Fix tag 40
 * 
 */
@VID("F1.FX.OT")
public enum OrderType implements ValuedEnum<Character> {
	/** 2 */
	LIMIT('2'),

	/** 1 */
	MARKET('1'),

	/** 3 */
	STOP('3'),

	/** 4 */
	STOP_LIMIT('4'),

	/** P */
	PEGGED('P');

	public final char type;

	private OrderType(char type) {
		this.type = type;
	}

	public static OrderType getOrdType(char type) {
		switch (type) {
			case '2':
				return LIMIT;
			case '1':
				return MARKET;
			case '3':
				return STOP;
			case '4':
				return STOP_LIMIT;
			case '5':
				return MARKET;
			case 'B':
				return LIMIT;
			case 'P':
				return PEGGED;
		}
		return null;
	}

	public static char getOrdType(OrderType type, TimeInForce TIF) {
		if (TIF != null) {
			switch (TIF) {
				case ON_CLOSE:
					switch (type) {
						case LIMIT:
							return 'B';
						case MARKET:
							return '5';
					}
					break;
			}
		}
		return type.getEnumValue();
	}

	@Override
	public Character getEnumValue() {
		return type;
	}

}
