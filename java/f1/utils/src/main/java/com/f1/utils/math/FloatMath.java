/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.math;

import com.f1.base.Caster;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.casters.Caster_Float;

public class FloatMath implements PrimitiveMath<Float> {

	public static final FloatMath INSTANCE = new FloatMath();

	@Override
	public Float add(Number left, Number right) {
		return valueOf(left.floatValue() + right.floatValue());
	}

	@Override
	public Float subtract(Number left, Number right) {
		return valueOf(left.floatValue() - right.floatValue());
	}

	@Override
	public Float multiply(Number left, Number right) {
		return valueOf(left.floatValue() * right.floatValue());
	}

	@Override
	public Float divide(Number left, Number right) {
		return valueOf(left.floatValue() / right.floatValue());
	}

	@Override
	public Class<Float> getReturnType() {
		return Float.class;
	}

	@Override
	public Float parseString(String text) {
		return valueOf(Float.parseFloat(text));
	}

	@Override
	public int compare(Number left, Number right) {
		float l = left.floatValue(), r = right.floatValue();
		return Float.compare(l, r);
	}

	@Override
	public Float cast(Number number) {
		return valueOf(number.floatValue());
	}

	@Override
	public Float abs(Number t) {
		return valueOf(MH.abs(t.floatValue()));
	}
	private static Float valueOf(float l) {
		return OH.valueOf(l);
	}

	@Override
	public Caster<Float> getCaster() {
		return Caster_Float.INSTANCE;
	}
	@Override
	public Number negate(Number object) {
		return -object.floatValue();
	}

	@Override
	public Float mod(Number left, Number right) {
		return left.floatValue() % right.floatValue();
	}

}
