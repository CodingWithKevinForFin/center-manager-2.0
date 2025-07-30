package com.f1.utils.casters;

import com.f1.utils.OH;
import com.f1.utils.SH;

public class Caster_Integer extends Caster_Number<Integer> {

	public static final Caster_Integer INSTANCE = new Caster_Integer(false);
	public static final Caster_Integer PRIMITIVE = new Caster_Integer(true);

	public Caster_Integer(boolean primitive) {
		super(primitive ? int.class : Integer.class);
	}
	@Override
	protected Integer getPrimitiveValue(Number n) {
		return n.intValue();
	}
	@Override
	protected Integer getParsedFromString(CharSequence cs, boolean throwExceptionOnError) {
		return OH.valueOf(SH.parseIntSafe(cs, throwExceptionOnError, false));
	}

}
