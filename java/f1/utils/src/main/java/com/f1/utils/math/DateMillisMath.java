/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.math;

import com.f1.base.Caster;
import com.f1.base.DateMillis;
import com.f1.utils.MH;
import com.f1.utils.casters.Caster_DateMillis;

public class DateMillisMath implements PrimitiveBitwiseMath<DateMillis> {
	public static final DateMillisMath INSTANCE = new DateMillisMath();

	@Override
	public DateMillis add(Number left, Number right) {
		return new DateMillis(left.longValue() + right.longValue());
	}
	@Override
	public DateMillis subtract(Number left, Number right) {
		return new DateMillis(left.longValue() - right.longValue());
	}
	@Override
	public DateMillis multiply(Number left, Number right) {
		return new DateMillis(left.longValue() * right.longValue());
	}
	@Override
	public DateMillis divide(Number left, Number right) {
		return new DateMillis(left.longValue() / right.longValue());
	}
	@Override
	public Class<DateMillis> getReturnType() {
		return DateMillis.class;
	}

	@Override
	public DateMillis parseString(String text) {
		return new DateMillis(Long.parseLong(text));
	}

	@Override
	public int compare(Number left, Number right) {
		long l = left.longValue(), r = right.longValue();
		return l < r ? -1 : l > r ? 1 : 0;
	}

	@Override
	public DateMillis cast(Number number) {
		return new DateMillis(number.longValue());
	}

	private static long asLong(Number number) {
		return number.longValue();
	}

	@Override
	public DateMillis shiftLeft(Number left, Number right) {
		return new DateMillis(asLong(left) << asLong(right));
	}

	@Override
	public DateMillis shiftRight(Number left, Number right) {
		return new DateMillis(asLong(left) >> asLong(right));
	}

	@Override
	public DateMillis shiftRightUnsigned(Number left, Number right) {
		return new DateMillis(asLong(left) >>> asLong(right));
	}

	@Override
	public DateMillis and(Number left, Number right) {
		return new DateMillis(asLong(left) & asLong(right));
	}

	@Override
	public DateMillis xor(Number left, Number right) {
		return new DateMillis(asLong(left) ^ asLong(right));
	}

	@Override
	public DateMillis or(Number left, Number right) {
		return new DateMillis(asLong(left) | asLong(right));
	}

	@Override
	public DateMillis mod(Number left, Number right) {
		return new DateMillis(asLong(left) % asLong(right));
	}
	@Override
	public DateMillis abs(Number t) {
		if (t.longValue() < 0)
			return new DateMillis(MH.abs(t.longValue()));
		return t instanceof DateMillis ? ((DateMillis) t) : new DateMillis(t.longValue());
	}
	@Override
	public Caster<DateMillis> getCaster() {
		return Caster_DateMillis.INSTANCE;
	}
	@Override
	public Number negate(Number object) {
		return new DateMillis(-object.longValue());
	}

}
