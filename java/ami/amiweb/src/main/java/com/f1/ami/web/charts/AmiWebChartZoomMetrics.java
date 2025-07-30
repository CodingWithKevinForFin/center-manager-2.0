package com.f1.ami.web.charts;

import java.util.logging.Logger;

import com.f1.utils.LH;

public class AmiWebChartZoomMetrics {

	private static final Logger log = LH.get();

	final protected int width;
	final protected double posZoomX;
	final protected double posOffsetX;
	final protected int height;
	final protected double posZoomY;
	final protected double posOffsetY;

	public AmiWebChartZoomMetrics(int width, double posZoomX, double posOffsetX, int height, double posZoomY, double posOffsetY) {
		this.width = width;
		this.posZoomX = posZoomX;
		this.posOffsetX = posOffsetX;
		this.height = height;
		this.posZoomY = posZoomY;
		this.posOffsetY = posOffsetY;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public double getPosOffsetX() {
		return posOffsetX;
	}

	public double getPosZoomX() {
		return posZoomX;
	}
	public double getPosOffsetY() {
		return posOffsetY;
	}

	public double getPosZoomY() {
		return posZoomY;
	}

	public double scaleY(double y) {
		return (y * this.posZoomY + this.posOffsetY);
	}

	public double scaleX(double x) {
		return (x * this.posZoomX + this.posOffsetX);
	}
	public double unscaleX(double x) {
		return (x - this.posOffsetX) / this.posZoomX;
	}
	public double unscaleY(double y) {
		return (y - this.posOffsetY) / this.posZoomY;
	}
	public double scaleH(double h) {
		return (h * this.posZoomY);
	}

	public double scaleW(double w) {
		return (w * this.posZoomX);
	}
	public double scaleHDouble(double h) {
		return (h * this.posZoomY);
	}

	public double unscaleH(double h) {
		return (h / this.posZoomY);
	}

	public double unscaleW(double w) {
		return (w / this.posZoomX);
	}

}
