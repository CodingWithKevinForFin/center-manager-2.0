/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.math;

import com.f1.base.Caster;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.casters.Caster_Integer;

public class IntMath implements PrimitiveBitwiseMath<Integer> {
	public static final IntMath INSTANCE = new IntMath();

	@Override
	public Integer add(Number left, Number right) {
		return valueOf(left.intValue() + right.intValue());
	}

	@Override
	public Integer subtract(Number left, Number right) {
		return valueOf(left.intValue() - right.intValue());
	}

	@Override
	public Integer multiply(Number left, Number right) {
		return valueOf(left.intValue() * right.intValue());
	}

	@Override
	public Integer divide(Number left, Number right) {
		int rValue = right.intValue();
		if (rValue == 0)
			return null;
		return valueOf(left.intValue() / rValue);
	}

	@Override
	public Class<Integer> getReturnType() {
		return Integer.class;
	}

	@Override
	public Integer parseString(String text) {
		return valueOf(Integer.parseInt(text));
	}

	@Override
	public int compare(Number left, Number right) {
		long l = left.intValue(), r = right.intValue();
		return l < r ? -1 : l > r ? 1 : 0;
	}

	@Override
	public Integer cast(Number number) {
		return valueOf(number.intValue());
	}

	@Override
	public Integer shiftLeft(Number left, Number right) {
		return valueOf(cast2(left) << cast2(right));
	}

	@Override
	public Integer shiftRight(Number left, Number right) {
		return valueOf(cast2(left) >> cast2(right));
	}

	@Override
	public Integer shiftRightUnsigned(Number left, Number right) {
		return valueOf(cast2(left) >>> cast2(right));
	}

	@Override
	public Integer and(Number left, Number right) {
		return valueOf(cast2(left) & cast2(right));
	}

	@Override
	public Integer xor(Number left, Number right) {
		return valueOf(cast2(left) ^ cast2(right));
	}

	@Override
	public Integer or(Number left, Number right) {
		return valueOf(cast2(left) | cast2(right));
	}

	@Override
	public Integer mod(Number left, Number right) {
		return valueOf(cast2(left) % cast2(right));
	}

	@Override
	public Integer abs(Number t) {
		return MH.abs(t.intValue());
	}

	private static Integer valueOf(int l) {
		return OH.valueOf(l);
	}

	private static int cast2(Number left) {
		return left.intValue();
	}

	@Override
	public Caster<Integer> getCaster() {
		return Caster_Integer.INSTANCE;
	}
	@Override
	public Number negate(Number object) {
		return -object.intValue();
	}

}
