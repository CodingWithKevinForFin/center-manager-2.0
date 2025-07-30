package com.f1.suite.web.portal.style;

public class PortletStyleManager_Menu implements Cloneable {

	public static final String defaultBgColor = "#F0F0F0";
	public static final String defaultFontColor = "#000000";
	public static final String defaultDividerColor = "#898989";
	public static final String defaultDisabledBgColor = "#F0F0F0";
	public static final String defaultDisabledFontDColor = "#000000";
	public static final String defaultBorderTopLeftColor = "#808080";
	public static final String defaultBorderBottomRightColor = "#808080";

	private String bgColor;
	private String fontColor;
	private String dividerColor;
	private String disabledBgColor;
	private String disabledFontColor;
	private String borderTopLeftColor;
	private String borderBottomRightColor;
	private String hoverBgColor;
	private String hoverFontColor;
	private boolean useDefaultStyling;

	public String getBgColor() {
		return bgColor;
	}

	public void setBgColor(String color) {
		this.bgColor = color;
	}

	public String getFontColor() {
		return this.fontColor;
	}

	public void setFontColor(String color) {
		this.fontColor = color;
	}

	public String getDividerColor() {
		return this.dividerColor;
	}

	public void setDividerColor(String color) {
		this.dividerColor = color;
	}

	public String getDisabledBgColor() {
		return disabledBgColor;
	}

	public void setDisabledBgColor(String hoverBgColor) {
		this.disabledBgColor = hoverBgColor;
	}

	public String getDisabledFontColor() {
		return disabledFontColor;
	}

	public void setDisabledFontColor(String hoverFontColor) {
		this.disabledFontColor = hoverFontColor;
	}

	public String getBorderTopLeftColor() {
		return borderTopLeftColor;
	}

	public void setBorderTopLeftColor(String borderTopLeftColor) {
		this.borderTopLeftColor = borderTopLeftColor;
	}

	public String getBorderBottomRightColor() {
		return borderBottomRightColor;
	}

	public void setBorderBottomRightColor(String borderBottomRightColor) {
		this.borderBottomRightColor = borderBottomRightColor;
	}
	public void setHoverBgColor(String hoverBgColor) {
		this.hoverBgColor = hoverBgColor;
	}
	public String getHoverBgColor() {
		return this.hoverBgColor;
	}
	public void setHoverFontColor(String hoverFontColor) {
		this.hoverFontColor = hoverFontColor;
	}
	public String getHoverFontColor() {
		return this.hoverFontColor;
	}

	public boolean isUseDefaultStyling() {
		return useDefaultStyling;
	}

	public void setUseDefaultStyling(boolean useDefaultStyling) {
		this.useDefaultStyling = useDefaultStyling;
	}
}
