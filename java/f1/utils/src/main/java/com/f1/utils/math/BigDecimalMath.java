package com.f1.utils.math;

import java.math.BigDecimal;

import com.f1.base.Caster;
import com.f1.utils.casters.Caster_BigDecimal;

public class BigDecimalMath implements PrimitiveMath<BigDecimal> {
	private static final int DEFAULT_SCALE_FOR_DIV_OVERFLOW = 128;
	public static final BigDecimalMath INSTANCE = new BigDecimalMath();

	@Override
	public BigDecimal add(Number left, Number right) {
		return cast(left).add(cast(right));
	}
	@Override
	public BigDecimal subtract(Number left, Number right) {
		return cast(left).subtract(cast(right));
	}
	@Override
	public BigDecimal multiply(Number left, Number right) {
		return cast(left).multiply(cast(right));
	}
	@Override
	public BigDecimal divide(Number left, Number right) {
		BigDecimal cast = cast(left);
		BigDecimal cast2 = cast(right);
		try {
			return cast.divide(cast2);
		} catch (java.lang.ArithmeticException e) {
			int scale = Math.max(DEFAULT_SCALE_FOR_DIV_OVERFLOW, Math.max(cast.scale(), cast2.scale()));
			return cast.divide(cast2, scale, BigDecimal.ROUND_HALF_EVEN);
		}
	}
	@Override
	public Class<BigDecimal> getReturnType() {
		return BigDecimal.class;
	}
	@Override
	public Caster<BigDecimal> getCaster() {
		return Caster_BigDecimal.INSTANCE;
	}
	@Override
	public BigDecimal parseString(String text) {
		return new BigDecimal(text);
	}
	@Override
	public int compare(Number left, Number right) {
		return cast(left).compareTo(cast(right));
	}
	@Override
	public BigDecimal cast(Number number) {
		if (number instanceof BigDecimal)
			return (BigDecimal) number;
		return Caster_BigDecimal.INSTANCE.castInner(number,true);
	}
	@Override
	public BigDecimal abs(Number t) {
		return cast(t).abs();
	}
	@Override
	public Number negate(Number object) {
		return cast(object).negate();
	}
	@Override
	public BigDecimal mod(Number left, Number right) {
		return cast(left).remainder(cast(right));
	}
	
}
