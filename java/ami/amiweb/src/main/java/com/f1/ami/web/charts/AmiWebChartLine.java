package com.f1.ami.web.charts;

import java.awt.Color;
import java.util.Arrays;

public class AmiWebChartLine {

	private double x[] = new double[100];
	private double y[] = new double[100];
	private int length = 0;
	private int capacity = 100;
	private int[] lineSize = new int[100];
	private Color[] color = new Color[100];
	private int[] dashSize = new int[100];
	private byte lineType;

	public void reset(byte lineType) {
		this.length = 0;
		this.lineType = lineType;
	}

	public void add(double x, double y, int lineSize, Color color, int dashSize) {
		if (length == capacity) {
			capacity *= 2;
			this.x = Arrays.copyOf(this.x, capacity);
			this.y = Arrays.copyOf(this.y, capacity);
			this.lineSize = Arrays.copyOf(this.lineSize, capacity);
			this.color = Arrays.copyOf(this.color, capacity);
			this.dashSize = Arrays.copyOf(this.dashSize, capacity);
		}
		this.x[length] = x;
		this.y[length] = y;
		this.lineSize[length] = lineSize;
		this.color[length] = color;
		this.dashSize[length] = dashSize;
		length++;
	}

	public double[] getX() {
		return x;
	}

	public void setX(double[] x) {
		this.x = x;
	}

	public double[] getY() {
		return y;
	}

	public void setY(double[] y) {
		this.y = y;
	}

	public int getLength() {
		return length;
	}

	public int getCapacity() {
		return capacity;
	}

	public int[] getLineSize() {
		return lineSize;
	}

	public Color[] getColor() {
		return color;
	}

	public int[] getDashSize() {
		return dashSize;
	}

	public byte getLineType() {
		return lineType;
	}

}
