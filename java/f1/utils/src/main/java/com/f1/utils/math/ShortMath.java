/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.math;

import com.f1.base.Caster;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.casters.Caster_Short;

public class ShortMath implements PrimitiveBitwiseMath<Short> {

	@Override
	public Short add(Number left, Number right) {
		return valueOf((left.shortValue() + right.shortValue()));
	}

	@Override
	public Short subtract(Number left, Number right) {
		return valueOf((left.shortValue() - right.shortValue()));
	}

	@Override
	public Short multiply(Number left, Number right) {
		return valueOf((left.shortValue() * right.shortValue()));
	}

	@Override
	public Short divide(Number left, Number right) {
		short rValue = right.shortValue();
		if (rValue == 0)
			return null;
		return valueOf(left.shortValue() / rValue);
	}

	@Override
	public Class<Short> getReturnType() {
		return Short.class;
	}

	@Override
	public Short parseString(String text) {
		return valueOf(Short.parseShort(text));
	}

	@Override
	public int compare(Number left, Number right) {
		long l = left.shortValue(), r = right.shortValue();
		return l < r ? -1 : l > r ? 1 : 0;
	}

	@Override
	public Short cast(Number number) {
		return valueOf(number.shortValue());
	}

	@Override
	public Short shiftLeft(Number left, Number right) {
		return valueOf(cast2(left) << cast2(right));
	}

	@Override
	public Short shiftRight(Number left, Number right) {
		return valueOf(cast2(left) >> cast2(right));
	}

	@Override
	public Short shiftRightUnsigned(Number left, Number right) {
		return valueOf(cast2(left) >>> cast2(right));
	}

	@Override
	public Short and(Number left, Number right) {
		return valueOf(cast2(left) & cast2(right));
	}

	@Override
	public Short xor(Number left, Number right) {
		return valueOf(cast2(left) ^ cast2(right));
	}

	@Override
	public Short or(Number left, Number right) {
		return valueOf(cast2(left) | cast2(right));
	}

	@Override
	public Short mod(Number left, Number right) {
		return valueOf(cast2(left) % cast2(right));
	}

	@Override
	public Short abs(Number t) {
		return MH.abs(t.shortValue());
	}

	private static Short valueOf(int l) {
		return OH.valueOf((short) l);
	}

	private static short cast2(Number left) {
		return left.shortValue();
	}

	@Override
	public Caster<Short> getCaster() {
		return Caster_Short.INSTANCE;
	}

	@Override
	public Number negate(Number object) {
		return -object.shortValue();
	}

}
