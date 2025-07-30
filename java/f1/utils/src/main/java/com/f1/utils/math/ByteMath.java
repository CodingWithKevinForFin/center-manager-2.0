/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.math;

import com.f1.base.Caster;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.casters.Caster_Byte;

public class ByteMath implements PrimitiveBitwiseMath<Byte> {

	@Override
	public Byte add(Number left, Number right) {
		return valueOf(left.byteValue() + right.byteValue());
	}

	@Override
	public Byte subtract(Number left, Number right) {
		return valueOf(left.byteValue() - right.byteValue());
	}

	@Override
	public Byte multiply(Number left, Number right) {
		return valueOf(left.byteValue() * right.byteValue());
	}

	@Override
	public Byte divide(Number left, Number right) {
		byte rValue = right.byteValue();
		if (rValue == 0)
			return null;
		return valueOf(left.byteValue() / rValue);
	}

	@Override
	public Class<Byte> getReturnType() {
		return Byte.class;
	}

	@Override
	public Byte parseString(String text) {
		return valueOf(Byte.parseByte(text));
	}

	@Override
	public int compare(Number left, Number right) {
		long l = left.byteValue(), r = right.byteValue();
		return l < r ? -1 : l > r ? 1 : 0;
	}

	@Override
	public Byte cast(Number number) {
		return valueOf(number.byteValue());
	}

	@Override
	public Byte shiftLeft(Number left, Number right) {
		return valueOf(cast2(left) << cast2(right));
	}

	@Override
	public Byte shiftRight(Number left, Number right) {
		return valueOf(cast2(left) >> cast2(right));
	}

	@Override
	public Byte shiftRightUnsigned(Number left, Number right) {
		return valueOf(cast2(left) >>> cast2(right));
	}

	@Override
	public Byte and(Number left, Number right) {
		return valueOf(cast2(left) & cast2(right));
	}

	@Override
	public Byte xor(Number left, Number right) {
		return valueOf(cast2(left) ^ cast2(right));
	}

	@Override
	public Byte or(Number left, Number right) {
		return valueOf(cast2(left) | cast2(right));
	}

	@Override
	public Byte mod(Number left, Number right) {
		return valueOf(cast2(left) % cast2(right));
	}

	@Override
	public Byte abs(Number t) {
		return valueOf(MH.abs(t.byteValue()));
	}

	private static Byte valueOf(int l) {
		return OH.valueOf((byte) l);
	}

	private static byte cast2(Number left) {
		return left.byteValue();
	}

	@Override
	public Caster<Byte> getCaster() {
		return Caster_Byte.INSTANCE;
	}
	@Override
	public Number negate(Number object) {
		return -object.byteValue();
	}

}
