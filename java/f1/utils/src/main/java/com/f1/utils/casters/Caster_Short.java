package com.f1.utils.casters;

import com.f1.utils.OH;
import com.f1.utils.SH;

public class Caster_Short extends Caster_Number<Short> {

	public static final Caster_Short INSTANCE = new Caster_Short(false);
	public static final Caster_Short PRIMITIVE = new Caster_Short(true);

	public Caster_Short(boolean primitive) {
		super(primitive ? short.class : Short.class);
	}
	@Override
	protected Short getPrimitiveValue(Number n) {
		return n.shortValue();
	}
	@Override
	protected Short getParsedFromString(CharSequence cs, boolean throwExceptionOnError) {
		return OH.valueOf(SH.parseShortSafe(cs, throwExceptionOnError, false));
	}

}
