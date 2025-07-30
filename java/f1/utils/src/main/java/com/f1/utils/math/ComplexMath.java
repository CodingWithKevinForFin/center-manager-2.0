package com.f1.utils.math;

import com.f1.base.Caster;
import com.f1.base.Complex;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Complex;

public class ComplexMath implements PrimitiveMath<Complex> {

	@Override
	public Complex add(Number left, Number right) {
		return cast(left).add(cast(right));
	}

	@Override
	public Complex subtract(Number left, Number right) {
		return cast(left).subtract(cast(right));
	}

	@Override
	public Complex multiply(Number left, Number right) {
		return cast(left).multiply(cast(right));
	}

	@Override
	public Complex divide(Number left, Number right) {
		return cast(left).divide(cast(right));
	}

	@Override
	public Class<Complex> getReturnType() {
		return Complex.class;
	}

	@Override
	public Caster<Complex> getCaster() {
		return Caster_Complex.INSTANCE;
	}

	@Override
	public Complex parseString(String text) {
		return text == null ? null : SH.parseComplex(text, 0, text.length());
	}

	@Override
	public int compare(Number left, Number right) {
		return cast(left).compareTo(cast(right));
	}

	@Override
	public Complex cast(Number number) {
		if (number instanceof Complex)
			return (Complex) number;
		return new Complex(number.doubleValue());
	}

	@Override
	public Complex abs(Number t) {
		return new Complex(cast(t).modulus());
	}
	@Override
	public Number negate(Number object) {
		return cast(object).negative();
	}

	@Override
	public Complex mod(Number left, Number right) {
		return cast(left).mod(cast(right));
	}

}
