/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.math;

import com.f1.base.Caster;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.casters.Caster_Double;

public class DoubleMath implements PrimitiveMath<Double> {
	public static final DoubleMath INSTANCE = new DoubleMath();

	@Override
	public Double add(Number left, Number right) {
		return valueOf(left.doubleValue() + right.doubleValue());
	}

	@Override
	public Double subtract(Number left, Number right) {
		return valueOf(left.doubleValue() - right.doubleValue());
	}

	@Override
	public Double multiply(Number left, Number right) {
		return valueOf(left.doubleValue() * right.doubleValue());
	}

	@Override
	public Double divide(Number left, Number right) {
		return valueOf(left.doubleValue() / right.doubleValue());
	}

	@Override
	public Class<Double> getReturnType() {
		return Double.class;
	}

	@Override
	public Double parseString(String text) {
		return valueOf(Double.parseDouble(text));
	}

	@Override
	public int compare(Number left, Number right) {
		double l = left.doubleValue(), r = right.doubleValue();
		return Double.compare(l, r);
	}

	@Override
	public Double cast(Number number) {
		return valueOf(number.doubleValue());
	}

	@Override
	public Double abs(Number t) {
		return valueOf(MH.abs(t.doubleValue()));
	}

	private static Double valueOf(double l) {
		return OH.valueOf(l);
	}

	@Override
	public Caster<Double> getCaster() {
		return Caster_Double.INSTANCE;
	}
	@Override
	public Number negate(Number object) {
		return -object.doubleValue();
	}
	@Override
	public Double mod(Number left, Number right) {
		return left.doubleValue() % right.doubleValue();
	}

}
