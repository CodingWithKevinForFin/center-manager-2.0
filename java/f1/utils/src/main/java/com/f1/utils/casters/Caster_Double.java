package com.f1.utils.casters;

import com.f1.utils.OH;
import com.f1.utils.SH;

public class Caster_Double extends Caster_Number<Double> {

	public static final Caster_Double INSTANCE = new Caster_Double(false);
	public static final Caster_Double PRIMITIVE = new Caster_Double(true);

	public Caster_Double(boolean primitive) {
		super(primitive ? double.class : Double.class);
	}

	@Override
	protected Double getPrimitiveValue(Number n) {
		return n.doubleValue();
	}

	@Override
	protected Double getParsedFromString(CharSequence cs, boolean throwExceptionOnError) {
		return OH.valueOf(SH.parseDoubleSafe(cs, throwExceptionOnError));
	}

}
