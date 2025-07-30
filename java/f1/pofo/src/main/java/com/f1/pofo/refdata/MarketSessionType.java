package com.f1.pofo.refdata;

import com.f1.base.VID;
import com.f1.base.ValuedEnum;

/**
 * 
 * type of a particular market session
 */
@VID("F1.RD.MS")
public enum MarketSessionType implements ValuedEnum<Byte> {
	PRE_MARKET((byte) 1), REGULAR((byte) 2), POST_MARKET((byte) 3);

	private byte value;

	private MarketSessionType(byte value) {
		this.value = value;
	}

	@Override
	public Byte getEnumValue() {
		return value;
	}

}
