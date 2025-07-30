/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

public class WebImage {

	public static final String PNG = "image/png";

	final private String name;
	final private int width;
	final private int height;
	final private byte[] data;
	final private String encoding;

	public WebImage(String name, int width, int height, byte[] data, String encoding) {
		super();
		this.name = name;
		this.width = width;
		this.height = height;
		this.data = data;
		this.encoding = encoding;
	}

	public String getName() {
		return name;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public byte[] getData() {
		return data;
	}

	public String getEncoding() {
		return encoding;
	}

}
