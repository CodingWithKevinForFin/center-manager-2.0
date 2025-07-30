package com.f1.suite.web.portal.style;

import com.f1.utils.OH;

public class PortletStyleManager_Tab implements Cloneable {

	private int defaultTabHeight = 30;
	private int defaultTabPaddingTop = 6;
	private int defaultTabPaddingBottom = 4;
	private int defaultTabSpacing = 6;
	private int defaultTabPaddingStart = 0;
	private int defaultTabLeftRounding = 6;
	private int defaultTabRightRounding = 6;
	private int defaultFontSize = 14;
	private int defaultSelBorderSize = 0;
	private String defaultBgColor = "#ffffff";
	private String defaultSelectedColor = "#e2e2e2";
	private String defaultUnselectedColor = "#a8a8a8";
	private String defaultUnselectedTextColor = "#000000";
	private String defaultSelectedTextColor = "#000000";
	private String defaultSelectedShadow = "none";
	private String defaultUnselectedShadow = "none";
	private boolean defaultHideTabWhenPoppedOut = true;
	private boolean defaultHasAddButton = false;
	private String defaultAddButtonColor = "#000000";
	private String defaultBorderColor = "#dddddd";
	private String defaultFontFamily = "arial";
	private String defaultSelBorderColor = "#dddddd";
	private boolean useDefaultStyling = false;

	public PortletStyleManager_Tab clone() {
		try {
			PortletStyleManager_Tab r = (PortletStyleManager_Tab) super.clone();
			return r;
		} catch (CloneNotSupportedException e) {
			throw OH.toRuntime(e);
		}
	}
	public PortletStyleManager_Tab() {
	}
	public int getDefaultTabHeight() {
		return defaultTabHeight;
	}
	public void setDefaultTabHeight(int defaultTabHeight) {
		this.defaultTabHeight = defaultTabHeight;
	}
	public int getDefaultTabPaddingTop() {
		return defaultTabPaddingTop;
	}
	public void setDefaultTabPaddingTop(int defaultTabPaddingTop) {
		this.defaultTabPaddingTop = defaultTabPaddingTop;
	}
	public int getDefaultTabPaddingBottom() {
		return defaultTabPaddingBottom;
	}
	public void setDefaultTabPaddingBottom(int defaultTabPaddingBottom) {
		this.defaultTabPaddingBottom = defaultTabPaddingBottom;
	}
	public String getDefaultBgColor() {
		return defaultBgColor;
	}
	public void setDefaultBgColor(String defaultBgColor) {
		this.defaultBgColor = defaultBgColor;
	}
	public String getDefaultSelectedColor() {
		return defaultSelectedColor;
	}
	public void setDefaultSelectedColor(String defaultSelectedColor) {
		this.defaultSelectedColor = defaultSelectedColor;
	}
	public String getDefaultUnselectedColor() {
		return defaultUnselectedColor;
	}
	public void setDefaultUnselectedColor(String defaultUnselectedColor) {
		this.defaultUnselectedColor = defaultUnselectedColor;
	}
	public String getDefaultUnselectedTextColor() {
		return defaultUnselectedTextColor;
	}
	public void setDefaultUnselectedTextColor(String defaultUnselectedTextColor) {
		this.defaultUnselectedTextColor = defaultUnselectedTextColor;
	}
	public String getDefaultSelectedTextColor() {
		return defaultSelectedTextColor;
	}
	public void setDefaultSelectedTextColor(String defaultselectedTextColor) {
		this.defaultSelectedTextColor = defaultselectedTextColor;
	}
	public int getDefaultFontSize() {
		return defaultFontSize;
	}
	public void setDefaultFontSize(int defaultFontSize) {
		this.defaultFontSize = defaultFontSize;
	}
	public int getDefaultTabSpacing() {
		return defaultTabSpacing;
	}
	public int getDefaultTabPaddingStart() {
		return defaultTabPaddingStart;
	}
	public void setDefaultTabSpacing(int defaultTabSpacing) {
		this.defaultTabSpacing = defaultTabSpacing;
	}
	public int getDefaultTabLeftRounding() {
		return this.defaultTabLeftRounding;
	}
	public int getDefaultTabRightRounding() {
		return this.defaultTabRightRounding;
	}
	public void setDefaultTabLeftRounding(int defaultTabLeftRounding) {
		this.defaultTabLeftRounding = defaultTabLeftRounding;
	}
	public void setDefaultTabRightRounding(int defaultTabRightRounding) {
		this.defaultTabRightRounding = defaultTabRightRounding;
	}
	public String getDefaultSelectedShadow() {
		return this.defaultSelectedShadow;
	}
	public String getDefaultUnselectedShadow() {
		return this.defaultUnselectedShadow;
	}
	public void setDefaultSelectedShadow(String defaultSelectedShadow) {
		this.defaultSelectedShadow = defaultSelectedShadow;
	}
	public void setDefaultUnselectedShadow(String defaultUnselectedShadow) {
		this.defaultUnselectedShadow = defaultUnselectedShadow;
	}

	public boolean getDefaultHideTabWhenPopedOut() {
		return this.defaultHideTabWhenPoppedOut;
	}

	public boolean getDefaultHasAddButton() {
		return this.defaultHasAddButton;
	}
	public void setDefaultHasAddButton(boolean hasAddButton) {
		this.defaultHasAddButton = hasAddButton;
	}

	public String getDefaultAddButtonColor() {
		return this.defaultAddButtonColor;
	}
	public void setDefaultAddButtonColor(String addButtonColor) {
		this.defaultAddButtonColor = addButtonColor;
	}
	public String getDefaultBorderColor() {
		return defaultBorderColor;
	}
	public void setDefaultBorderColor(String defaultBorderColor) {
		this.defaultBorderColor = defaultBorderColor;
	}
	public String getDefaultFontFamily() {
		return defaultFontFamily;
	}
	public void setDefaultFontFamily(String defaultFontFamily) {
		this.defaultFontFamily = defaultFontFamily;
	}
	public String getDefaultSelBorderColor() {
		return defaultSelBorderColor;
	}
	public void setDefaultSelBorderColor(String defaultSelBorderColor) {
		this.defaultSelBorderColor = defaultSelBorderColor;
	}
	public int getDefaultSelBorderSize() {
		return defaultSelBorderSize;
	}
	public void setDefaultSelBorderSize(int defaultSelBorderSize) {
		this.defaultSelBorderSize = defaultSelBorderSize;
	}
	public boolean isUseDefaultStyling() {
		return useDefaultStyling;
	}
	public void setUseDefaultStyling(boolean useDefaultStyling) {
		this.useDefaultStyling = useDefaultStyling;
	}
}
