package com.f1.utils.math;

public interface Fraction<N extends Number> {

	public N getNumerator();

	public N getDenominator();

	public N getValue();

	public Fraction<N> add(Fraction<N> f);

	public Fraction<N> subtract(Fraction<N> f);

	public Fraction<N> divide(Fraction<N> f);

	public Fraction<N> multiple(Fraction<N> f);

}
