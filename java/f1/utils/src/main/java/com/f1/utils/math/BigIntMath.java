package com.f1.utils.math;

import java.math.BigInteger;

import com.f1.base.Caster;
import com.f1.utils.casters.Caster_BigInteger;

public class BigIntMath implements PrimitiveMath<BigInteger> {

	@Override
	public BigInteger add(Number left, Number right) {

		return cast(left).add(cast(right));

	}

	@Override
	public BigInteger subtract(Number left, Number right) {

		return cast(left).subtract(cast(right));

	}

	@Override
	public BigInteger multiply(Number left, Number right) {

		return cast(left).multiply(cast(right));

	}

	@Override
	public BigInteger divide(Number left, Number right) {

		return cast(left).divide(cast(right));

	}

	@Override
	public Class<BigInteger> getReturnType() {

		return BigInteger.class;

	}

	@Override
	public BigInteger parseString(String text) {

		return new BigInteger(text);

	}

	@Override
	public int compare(Number left, Number right) {

		return cast(left).compareTo(cast(right));

	}

	@Override
	public BigInteger cast(Number number) {
		if (number instanceof BigInteger)
			return (BigInteger) number;
		return Caster_BigInteger.INSTANCE.castInner(number, true);
	}

	@Override
	public BigInteger abs(Number t) {
		return cast(t).abs();
	}

	@Override
	public Caster<BigInteger> getCaster() {
		return Caster_BigInteger.INSTANCE;
	}
	@Override
	public Number negate(Number object) {
		return cast(object).negate();
	}

	@Override
	public BigInteger mod(Number left, Number right) {
		return cast(left).mod(cast(right));
	}

}
