/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.math;

import com.f1.base.Caster;
import com.f1.utils.BasicFixPoint;
import com.f1.utils.FixPoint;
import com.f1.utils.FixPointHelper;
import com.f1.utils.casters.Caster_FixPoint;

public class FixPointMath implements PrimitiveMath<FixPoint> {

	private static final int defaultPrecision = 4;

	@Override
	public FixPoint add(Number left, Number right) {
		if (left instanceof FixPoint && right instanceof FixPoint)
			return FixPointHelper.add((FixPoint) left, (FixPoint) right);
		else if (isDecimal(left) || isDecimal(right))
			return BasicFixPoint.nuw(getPrecision(left, right), left.doubleValue() + right.doubleValue());
		else
			return BasicFixPoint.nuw(getPrecision(left, right), left.longValue() + right.longValue());
	}

	@Override
	public FixPoint subtract(Number left, Number right) {
		if (left instanceof FixPoint && right instanceof FixPoint)
			return FixPointHelper.subtract((FixPoint) left, (FixPoint) right);
		else if (isDecimal(left) || isDecimal(right))
			return BasicFixPoint.nuw(getPrecision(left, right), left.doubleValue() + right.doubleValue());
		else
			return BasicFixPoint.nuw(getPrecision(left, right), left.longValue() + right.longValue());
	}

	@Override
	public FixPoint multiply(Number left, Number right) {
		if (left instanceof FixPoint && right instanceof FixPoint)
			return FixPointHelper.multiply((FixPoint) left, (FixPoint) right);
		else if (isDecimal(left) || isDecimal(right))
			return BasicFixPoint.nuw(getPrecision(left, right), left.doubleValue() + right.doubleValue());
		else
			return BasicFixPoint.nuw(getPrecision(left, right), left.longValue() + right.longValue());
	}

	@Override
	public FixPoint divide(Number left, Number right) {
		if (left instanceof FixPoint && right instanceof FixPoint)
			return FixPointHelper.divide((FixPoint) left, (FixPoint) right);
		else if (isDecimal(left) || isDecimal(right))
			return BasicFixPoint.nuw(getPrecision(left, right), left.doubleValue() + right.doubleValue());
		else
			return BasicFixPoint.nuw(getPrecision(left, right), left.longValue() + right.longValue());
	}

	@Override
	public Class<FixPoint> getReturnType() {
		return FixPoint.class;
	}

	@Override
	public FixPoint parseString(String text) {
		return BasicFixPoint.nuw(text);
	}

	@Override
	public int compare(Number left, Number right) {
		if (left instanceof FixPoint && right instanceof FixPoint)
			return ((FixPoint) left).compareTo((FixPoint) right);
		if (isDecimal(left) || isDecimal(right)) {
			double l = left.doubleValue();
			double r = right.doubleValue();
			return Double.compare(l, r);
		}
		long l = left.longValue();
		long r = right.longValue();
		if (l == +0.0 && r == -0.0 || l == -0.0 && r == +0.0)
			return 0;
		return l < r ? -1 : l > r ? 1 : 0;
	}

	@Override
	public FixPoint cast(Number number) {
		if (number instanceof FixPoint)
			return (FixPoint) number;
		else if (number instanceof Float || number instanceof Double)
			return BasicFixPoint.nuw(defaultPrecision, number.doubleValue());
		else
			return BasicFixPoint.nuw(defaultPrecision, number.longValue());
	}

	private static boolean isDecimal(Number n) {
		return n instanceof Float || n instanceof Double || n instanceof FixPoint;
	}

	private int getPrecision(Number left, Number right) {
		if (left instanceof FixPoint)
			return ((FixPoint) left).getPrecision();
		else if (right instanceof FixPoint)
			return ((FixPoint) right).getPrecision();
		else
			return defaultPrecision;
	}

	@Override
	public FixPoint abs(Number t) {
		return cast(t).abs();
	}

	@Override
	public Caster<FixPoint> getCaster() {
		return Caster_FixPoint.INSTANCE;
	}
	@Override
	public Number negate(Number object) {
		return FixPointHelper.subtract(BasicFixPoint.ZERO, cast(object));
	}

	@Override
	public FixPoint mod(Number left, Number right) {
		return BasicFixPoint.nuw(defaultPrecision, left.doubleValue() % right.doubleValue());
	}

}
