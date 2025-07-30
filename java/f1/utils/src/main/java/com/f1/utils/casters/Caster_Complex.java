package com.f1.utils.casters;

import com.f1.base.Complex;
import com.f1.utils.SH;

public class Caster_Complex extends Caster_Number<Complex> {
	public static final Caster_Complex INSTANCE = new Caster_Complex();

	public Caster_Complex() {
		super(Complex.class);
	}

	@Override
	protected Complex getPrimitiveValue(Number n) {
		return new Complex(n.doubleValue());
	}

	@Override
	protected Complex getParsedFromString(CharSequence cs, boolean throwExceptionOnError) {
		return SH.parseComplex(cs, 0, cs.length());
	}

}
