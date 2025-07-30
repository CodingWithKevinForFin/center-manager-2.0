/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.math;

import java.util.Comparator;

import com.f1.base.Caster;

public interface PrimitiveMath<N extends Number> extends Comparator<Number> {

	N add(Number left, Number right);

	N subtract(Number left, Number right);

	N multiply(Number left, Number right);

	N divide(Number left, Number right);

	Class<N> getReturnType();

	Caster<N> getCaster();

	N parseString(String text);

	@Override
	int compare(Number left, Number right);

	N cast(Number number);

	N abs(Number t);

	Number negate(Number object);

	N mod(Number left, Number right);
}
