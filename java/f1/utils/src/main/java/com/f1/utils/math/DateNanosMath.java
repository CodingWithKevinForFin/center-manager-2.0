/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.math;

import com.f1.base.Caster;
import com.f1.base.DateNanos;
import com.f1.utils.MH;
import com.f1.utils.casters.Caster_DateNanos;

public class DateNanosMath implements PrimitiveBitwiseMath<DateNanos> {
	public static final DateNanosMath INSTANCE = new DateNanosMath();

	@Override
	public DateNanos add(Number left, Number right) {
		return new DateNanos(left.longValue() + right.longValue());
	}
	@Override
	public DateNanos subtract(Number left, Number right) {
		return new DateNanos(left.longValue() - right.longValue());
	}
	@Override
	public DateNanos multiply(Number left, Number right) {
		return new DateNanos(left.longValue() * right.longValue());
	}
	@Override
	public DateNanos divide(Number left, Number right) {
		return new DateNanos(left.longValue() / right.longValue());
	}
	@Override
	public Class<DateNanos> getReturnType() {
		return DateNanos.class;
	}

	@Override
	public DateNanos parseString(String text) {
		return new DateNanos(Long.parseLong(text));
	}

	@Override
	public int compare(Number left, Number right) {
		long l = left.longValue(), r = right.longValue();
		return l < r ? -1 : l > r ? 1 : 0;
	}

	@Override
	public DateNanos cast(Number number) {
		return new DateNanos(number.longValue());
	}

	private static long asLong(Number number) {
		return number.longValue();
	}

	@Override
	public DateNanos shiftLeft(Number left, Number right) {
		return new DateNanos(asLong(left) << asLong(right));
	}

	@Override
	public DateNanos shiftRight(Number left, Number right) {
		return new DateNanos(asLong(left) >> asLong(right));
	}

	@Override
	public DateNanos shiftRightUnsigned(Number left, Number right) {
		return new DateNanos(asLong(left) >>> asLong(right));
	}

	@Override
	public DateNanos and(Number left, Number right) {
		return new DateNanos(asLong(left) & asLong(right));
	}

	@Override
	public DateNanos xor(Number left, Number right) {
		return new DateNanos(asLong(left) ^ asLong(right));
	}

	@Override
	public DateNanos or(Number left, Number right) {
		return new DateNanos(asLong(left) | asLong(right));
	}

	@Override
	public DateNanos mod(Number left, Number right) {
		return new DateNanos(asLong(left) % asLong(right));
	}
	@Override
	public DateNanos abs(Number t) {
		if (t.longValue() < 0)
			return new DateNanos(MH.abs(t.longValue()));
		return t instanceof DateNanos ? ((DateNanos) t) : new DateNanos(t.longValue());
	}
	@Override
	public Caster<DateNanos> getCaster() {
		return Caster_DateNanos.INSTANCE;
	}
	@Override
	public Number negate(Number object) {
		return new DateNanos(-object.longValue());
	}

}
