package com.f1.suite.web.portal.impl.visual;

public class TreemapDepthStyle {

	private int borderSize = -1;
	private int textSize = -1;
	private String borderColor;
	private String defaultText;
	private String defaultBgColor;
	final private int depth;

	public TreemapDepthStyle(int depth) {
		this.depth = depth;
	}

	public int getBorderSize() {
		return borderSize;
	}

	public void setBorderSize(int borderSize) {
		this.borderSize = borderSize;
	}

	public int getTextSize() {
		return textSize;
	}

	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}

	public String getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(String borderColor) {
		this.borderColor = borderColor;
	}

	public String getDefaultTextColor() {
		return defaultText;
	}

	public void setDefaultTextColor(String defaultTextColor) {
		this.defaultText = defaultTextColor;
	}

	public String getDefaultBgColor() {
		return defaultBgColor;
	}

	public void setDefaultBgColor(String defaultBfColor) {
		this.defaultBgColor = defaultBfColor;
	}

	public int getDepth() {
		return this.depth;
	}
}
