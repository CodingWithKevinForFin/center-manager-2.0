/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.math;

import com.f1.base.Caster;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.casters.Caster_Long;

public class LongMath implements PrimitiveBitwiseMath<Long> {
	public static final LongMath INSTANCE = new LongMath();

	@Override
	public Long add(Number left, Number right) {
		return valueOf(left.longValue() + right.longValue());
	}

	@Override
	public Long subtract(Number left, Number right) {
		return valueOf(left.longValue() - right.longValue());
	}

	@Override
	public Long multiply(Number left, Number right) {
		return valueOf(left.longValue() * right.longValue());
	}

	@Override
	public Long divide(Number left, Number right) {
		long rValue = right.longValue();
		if (rValue == 0L)
			return null;
		return valueOf(left.longValue() / rValue);
	}

	@Override
	public Class<Long> getReturnType() {
		return Long.class;
	}

	@Override
	public Long parseString(String text) {
		return valueOf(Long.parseLong(text));
	}

	@Override
	public int compare(Number left, Number right) {
		long l = left.longValue(), r = right.longValue();
		return l < r ? -1 : l > r ? 1 : 0;
	}

	@Override
	public Long cast(Number number) {
		return valueOf(number.longValue());
	}

	@Override
	public Long shiftLeft(Number left, Number right) {
		return valueOf(cast2(left) << cast2(right));
	}

	@Override
	public Long shiftRight(Number left, Number right) {
		return valueOf(cast2(left) >> cast2(right));
	}

	@Override
	public Long shiftRightUnsigned(Number left, Number right) {
		return valueOf(cast2(left) >>> cast2(right));
	}

	@Override
	public Long and(Number left, Number right) {
		return valueOf(cast2(left) & cast2(right));
	}

	@Override
	public Long xor(Number left, Number right) {
		return valueOf(cast2(left) ^ cast2(right));
	}

	@Override
	public Long or(Number left, Number right) {
		return valueOf(cast2(left) | cast2(right));
	}

	@Override
	public Long mod(Number left, Number right) {
		return valueOf(cast2(left) % cast2(right));
	}

	@Override
	public Long abs(Number t) {
		return valueOf(MH.abs(t.longValue()));
	}

	private static Long valueOf(long l) {
		return OH.valueOf(l);
	}

	private static long cast2(Number left) {
		return left.longValue();
	}

	@Override
	public Caster<Long> getCaster() {
		return Caster_Long.INSTANCE;
	}
	@Override
	public Number negate(Number object) {
		return -object.longValue();
	}
}
