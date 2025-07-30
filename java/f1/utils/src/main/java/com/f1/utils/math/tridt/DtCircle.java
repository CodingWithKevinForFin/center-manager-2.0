package com.f1.utils.math.tridt;

public class DtCircle {
	final private DtPoint center;
	final private double radius;

	public DtCircle(DtPoint center, double radius) {
		this.center = center;
		this.radius = radius;
	}

	public DtCircle(DtCircle circ) {
		this.center = circ.center;
		this.radius = circ.radius;
	}

	public DtPoint getCenter() {
		return this.center;
	}

	public double getRadius() {
		return this.radius;
	}

}
