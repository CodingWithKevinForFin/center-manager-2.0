package com.f1.utils.casters;

import com.f1.utils.OH;
import com.f1.utils.SH;

public class Caster_Long extends Caster_Number<Long> {

	public static final Caster_Long INSTANCE = new Caster_Long(false);
	public static final Caster_Long PRIMITIVE = new Caster_Long(true);

	public Caster_Long(boolean primitive) {
		super(primitive ? long.class : Long.class);
	}
	@Override
	protected Long getPrimitiveValue(Number n) {
		return n.longValue();
	}
	@Override
	protected Long getParsedFromString(CharSequence cs, boolean throwExceptionOnError) {
		return OH.valueOf(SH.parseLongSafe(cs, throwExceptionOnError));
	}

}
