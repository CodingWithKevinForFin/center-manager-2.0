package com.f1.utils.casters;

import com.f1.utils.OH;
import com.f1.utils.SH;

public class Caster_Float extends Caster_Number<Float> {

	public static final Caster_Float INSTANCE = new Caster_Float(false);
	public static final Caster_Float PRIMITIVE = new Caster_Float(true);

	public Caster_Float(boolean primitive) {
		super(primitive ? float.class : Float.class);
	}
	@Override
	protected Float getPrimitiveValue(Number n) {
		return n.floatValue();
	}
	@Override
	protected Float getParsedFromString(CharSequence cs, boolean throwExceptionOnError) {
		return OH.valueOf(SH.parseFloatSafe(cs, throwExceptionOnError));
	}

}
