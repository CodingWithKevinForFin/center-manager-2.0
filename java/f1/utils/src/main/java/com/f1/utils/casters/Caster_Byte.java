package com.f1.utils.casters;

import com.f1.utils.OH;
import com.f1.utils.SH;

public class Caster_Byte extends Caster_Number<Byte> {

	public static final Caster_Byte INSTANCE = new Caster_Byte(false);
	public static final Caster_Byte PRIMITIVE = new Caster_Byte(true);

	public Caster_Byte(boolean primitive) {
		super(primitive ? byte.class : Byte.class);
	}
	@Override
	protected Byte getPrimitiveValue(Number n) {
		return n.byteValue();
	}
	@Override
	protected Byte getParsedFromString(CharSequence cs, boolean throwExceptionOnError) {
		return OH.valueOf(SH.parseByteSafe(cs, throwExceptionOnError, false));
	}

}
