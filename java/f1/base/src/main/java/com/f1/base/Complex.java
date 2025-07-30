package com.f1.base;

public class Complex extends Number implements ToStringable, Comparable<Complex> {

	public static final Complex ZERO = new Complex(0, 0);
	final private double real, imaginary;

	public Complex(double real, double imaginary) {
		this.real = real;
		this.imaginary = imaginary;
	}
	public Complex(double real) {
		this.real = real;
		imaginary = 0;
	}

	public double real() {
		return real;
	}

	public double imaginary() {
		return imaginary;
	}

	public double modulus() {
		if (real != 0 || imaginary != 0) {
			return Math.sqrt(real * real + imaginary * imaginary);
		} else {
			return 0d;
		}
	}

	public double argument() {
		return Math.atan2(imaginary, real);
	}

	public Complex conjugate() {
		return new Complex(real, -imaginary);
	}

	public Complex add(Complex w) {
		return new Complex(real + w.real, imaginary + w.imaginary);
	}

	public Complex subtract(Complex w) {
		return new Complex(real - w.real, imaginary - w.imaginary);
	}

	public Complex multiply(Complex w) {
		return new Complex(real * w.real - imaginary * w.imaginary, real * w.imaginary + imaginary * w.real);
	}

	public Complex divide(Complex w) {
		double den = w.modulus();
		den *= den;//power of 2
		return new Complex((real * w.real() + imaginary * w.imaginary) / den, (imaginary * w.real() - real * w.imaginary) / den);
	}

	public Complex exponential() {
		double exp = Math.exp(real);
		return new Complex(exp * Math.cos(imaginary), exp * Math.sin(imaginary));
	}

	public Complex logarithm() {
		return new Complex(Math.log(this.modulus()), this.argument());
	}

	public Complex sqrt() {
		double r = Math.sqrt(this.modulus());
		double theta = this.argument() / 2;
		return new Complex(r * Math.cos(theta), r * Math.sin(theta));
	}

	// Real cosh function (used to compute complex trig functions)
	static private double cosh(double theta) {
		return (Math.exp(theta) + Math.exp(-theta)) / 2;
	}

	// Real sinh function (used to compute complex trig functions)
	static private double sinh(double theta) {
		return (Math.exp(theta) - Math.exp(-theta)) / 2;
	}

	public Complex sin() {
		return new Complex(cosh(imaginary) * Math.sin(real), sinh(imaginary) * Math.cos(real));
	}

	public Complex cos() {
		return new Complex(cosh(imaginary) * Math.cos(real), -sinh(imaginary) * Math.sin(real));
	}

	public Complex sinh() {
		return new Complex(sinh(real) * Math.cos(imaginary), cosh(real) * Math.sin(imaginary));
	}

	public Complex cosh() {
		return new Complex(cosh(real) * Math.cos(imaginary), sinh(real) * Math.sin(imaginary));
	}

	public Complex tan() {
		return (this.sin()).divide(this.cos());
	}

	public Complex negative() {
		return new Complex(-real, -imaginary);
	}

	public String toString() {
		if (imaginary == 0)
			return Double.toString(real);
		if (real == 0)
			return imaginary + "i";
		if (imaginary > 0)
			return real + "+" + imaginary + "i";
		if (imaginary < 0)
			return real + "-" + (-imaginary) + "i";
		return real + "+" + imaginary + "i";

	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		if (imaginary == 0)
			return sink.append(real);
		if (real == 0)
			return sink.append(imaginary).append('i');
		if (imaginary > 0)
			return sink.append(real).append('+').append(imaginary).append('i');
		if (imaginary < 0)
			return sink.append(real).append(imaginary).append('i');
		return sink.append(real).append('+').append(imaginary).append('i');
	}

	@Override
	public double doubleValue() {
		return real;
	}

	@Override
	public float floatValue() {
		return (float) real;
	}

	@Override
	public int intValue() {
		return (int) real;
	}

	@Override
	public long longValue() {
		return (long) real;
	}

	public int compareTo(Complex other) {
		int r = Double.compare(real, other.real);
		return r != 0 ? r : Double.compare(imaginary, other.imaginary);
	}

	@Override
	public boolean equals(Object other) {
		if (other == this)
			return true;
		if (other == null || other.getClass() != Complex.class)
			return false;
		Complex c = (Complex) other;
		return real == c.real && imaginary == c.imaginary;
	}

	@Override
	public int hashCode() {
		return hashCode(real) + 31 * hashCode(imaginary);
	}
	private static int hashCode(double value) {
		long bits = Double.doubleToLongBits(value);
		return (int) (bits ^ (bits >>> 32));
	}
	public Complex mod(Complex other) {
		return new Complex(this.real % other.real, this.imaginary % other.imaginary);
	}

}
