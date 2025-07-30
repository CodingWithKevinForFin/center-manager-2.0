package com.f1.suite.web.portal.impl;

import com.f1.suite.web.portal.style.PortletStyleManager_Tab;
import com.f1.utils.OH;

public class TabPortletStyle {
	public static final int NO_FLOAT = -1;
	public String backgroundColor;
	public String selectColor;
	public String unselectColor;
	public String selectTextColor;
	public String unselectTextColor;
	public String menuArrowColor;
	public int tabHeight;
	public int tabPaddingBottom;
	public int tabPaddingTop;
	public int tabSpacing;
	public int tabPaddingStart;
	public int leftRounding;
	public int rightRounding;
	public int fontSize;
	public int initialPadding = 0;
	public int menuArrowSize = 5;
	public int tabFloatSize = NO_FLOAT;
	public String selectShadow = "none";
	public String unselectShadow = "none";
	public String addButtonColor = "none";

	public boolean isVertical;
	public boolean isOnRight;
	public boolean isOnBottom;
	public boolean hasExtraButtonAlways = false;
	public boolean hasMenuAlways = false;
	public boolean isHidden = false;
	public boolean isCustomizable = true;
	private boolean hideTabWhenPoppedOut = false;
	private boolean showTabsOverride = false;
	public String addButtonText;
	public boolean hasAddButton;
	public String borderColor;
	public String fontFamily;

	private PortletStyleManager_Tab styleManager;
	private TabPortlet tabs;
	private String ddButtonColor;
	public String selBorderColor;
	public Integer selBorderSize;
	public boolean hideArrangeTabs = true;

	private void flagTabsChanged() {
		this.tabs.flagTabsChanged();
	}
	private void layoutChildren() {
		this.tabs.fireLayoutChildren();
	}
	private boolean getVisible() {
		return tabs.getVisible();
	}
	public TabPortletStyle(TabPortlet tabs, PortletStyleManager_Tab tabStyle) {
		this.tabs = tabs;
		this.styleManager = tabStyle;
		this.applyStyle(tabStyle);
	}
	public PortletStyleManager_Tab getStyleManager() {
		return this.styleManager;
	}
	public TabPortlet applyStyle(PortletStyleManager_Tab tabStyle) {
		this.styleManager = tabStyle;
		this.tabHeight = tabStyle.getDefaultTabHeight();
		this.leftRounding = tabStyle.getDefaultTabLeftRounding();
		this.tabPaddingBottom = tabStyle.getDefaultTabPaddingBottom();
		this.tabPaddingTop = tabStyle.getDefaultTabPaddingTop();
		this.rightRounding = tabStyle.getDefaultTabRightRounding();
		this.tabSpacing = tabStyle.getDefaultTabSpacing();
		this.tabPaddingStart = tabStyle.getDefaultTabPaddingStart();
		this.hideTabWhenPoppedOut = tabStyle.getDefaultHideTabWhenPopedOut();
		this.hasAddButton = tabStyle.getDefaultHasAddButton();
		this.addButtonColor = tabStyle.getDefaultAddButtonColor();
		this.borderColor = tabStyle.getDefaultBorderColor();
		this.selBorderColor = tabStyle.getDefaultSelBorderColor();
		this.fontFamily = tabStyle.getDefaultFontFamily();
		this.setFontSize(this.styleManager.getDefaultFontSize());
		flagTabsChanged();
		return this.tabs;
	}

	public void setTabHeight(Integer height) {
		if (height == null || this.tabHeight == height)
			return;
		this.tabHeight = height;
		if (getVisible()) {
			layoutChildren();
		}
		flagTabsChanged();
	}

	public int getTabHeight() {
		return this.tabHeight;
	}

	public void setUnselectTextColor(String unselectTextColor) {
		if (unselectTextColor == null)
			this.unselectTextColor = null;
		else
			this.unselectTextColor = unselectTextColor;
		flagTabsChanged();
	}
	public void setIsHidden(Boolean hidden) {
		if (hidden == null || this.isHidden == hidden)
			return;
		this.isHidden = hidden;
		layoutChildren();
		flagTabsChanged();
	}
	public boolean getIsHidden() {
		return isHidden;
	}
	public int getTabPaddingBottom() {
		return tabPaddingBottom;
	}
	public void setTabPaddingBottom(Integer tabPaddingBottom) {
		if (tabPaddingBottom == null || this.tabPaddingBottom == tabPaddingBottom)
			return;
		this.tabPaddingBottom = tabPaddingBottom;
		if (getVisible())
			layoutChildren();
		flagTabsChanged();
	}
	public int getTabPaddingTop() {
		return tabPaddingTop;
	}

	public void setTabPaddingTop(Integer tabPaddingTop) {
		if (tabPaddingTop == null || this.tabPaddingTop == tabPaddingTop)
			return;
		this.tabPaddingTop = tabPaddingTop;
		if (getVisible())
			layoutChildren();
		flagTabsChanged();
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(Integer fontSize) {
		if (fontSize == null || this.fontSize == fontSize)
			return;
		this.fontSize = fontSize;
		flagTabsChanged();
	}

	public int getInitialPadding() {
		return initialPadding;
	}

	public void setInitialPadding(Integer initialPadding) {
		if (initialPadding == null || this.initialPadding == initialPadding)
			return;
		if (getIsOnRight())
			this.initialPadding = -1 * initialPadding;
		else
			this.initialPadding = initialPadding;
		flagTabsChanged();
	}
	public boolean getIsVertical() {
		return isVertical;
	}

	public void setIsVertical(Boolean isVertical) {
		if (isVertical == null || this.isVertical == isVertical)
			return;
		this.isVertical = isVertical;
		if (getVisible())
			layoutChildren();
		flagTabsChanged();
	}

	public boolean getIsOnRight() {
		return isOnRight;
	}

	public void setIsOnRight(Boolean isOnRight) {
		if (isOnRight == null || this.isOnRight == isOnRight)
			return;
		this.isOnRight = isOnRight;
		flagTabsChanged();
	}

	public boolean getIsOnBottom() {
		return isOnBottom;
	}

	public void setIsOnBottom(Boolean isOnBottom) {
		if (isOnBottom == null || this.isOnBottom == isOnBottom)
			return;
		this.isOnBottom = isOnBottom;
		flagTabsChanged();
	}

	public int getLeftRounding() {
		return leftRounding;
	}

	public void setLeftRounding(Integer leftRounding) {
		if (leftRounding == null || this.leftRounding == leftRounding)
			return;
		this.leftRounding = leftRounding;
		flagTabsChanged();
	}

	public int getRightRounding() {
		return rightRounding;
	}

	public void setRightRounding(Integer rightRounding) {
		if (rightRounding == null || this.rightRounding == rightRounding)
			return;
		this.rightRounding = rightRounding;
		flagTabsChanged();
	}

	public int getTabSpacing() {
		return tabSpacing;
	}

	public void setTabSpacing(Integer tabSpacing) {
		if (tabSpacing == null || this.tabSpacing == tabSpacing)
			return;
		this.tabSpacing = tabSpacing;
		flagTabsChanged();
	}

	public int getTabPaddingStart() {
		return this.tabPaddingStart;
	}
	public void setTabPaddingStart(Integer tabPaddingStart) {
		if (tabPaddingStart == null || this.tabPaddingStart == tabPaddingStart)
			return;
		this.tabPaddingStart = tabPaddingStart;
		flagTabsChanged();
	}
	public String getMenuArrowColor() {
		return menuArrowColor;
	}

	public TabPortlet setMenuArrowColor(String menuArrowColor) {
		if (OH.eq(this.menuArrowColor, menuArrowColor))
			return this.tabs;
		this.menuArrowColor = menuArrowColor;
		flagTabsChanged();
		return this.tabs;
	}

	public int getMenuArrowSize() {
		return menuArrowSize;
	}

	public TabPortlet setMenuArrowSize(int menuArrowSize) {
		if (this.menuArrowSize == menuArrowSize)
			return this.tabs;
		this.menuArrowSize = menuArrowSize;
		flagTabsChanged();
		return this.tabs;
	}
	public String getUnselectShadow() {
		return unselectShadow;
	}

	public void setUnselectShadow(String unselectShadow) {
		if (OH.eq(this.unselectShadow, unselectShadow))
			return;
		this.unselectShadow = unselectShadow;
		flagTabsChanged();
	}
	public String getSelectShadow() {
		return selectShadow;
	}

	public void setSelectShadow(String selectShadow) {
		if (OH.eq(this.selectShadow, selectShadow))
			return;
		this.selectShadow = selectShadow;
		flagTabsChanged();
	}

	public void setTabFloatSize(int tabFloatSize) {
		if (this.tabFloatSize == tabFloatSize)
			return;
		this.tabFloatSize = tabFloatSize;
		if (getVisible()) {
			layoutChildren();
		}
		flagTabsChanged();
	}
	public int getTabFloatSize() {
		return tabFloatSize;
	}
	public void setBackgroundColor(String color) {
		if (color == null)
			this.backgroundColor = null;
		else
			this.backgroundColor = color;
		flagTabsChanged();
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}
	public void setSelectedColor(String color) {
		if (color == null)
			this.selectColor = null;
		else
			this.selectColor = color;
		flagTabsChanged();
	}

	public String getSelectedColor() {
		return selectColor;
	}
	public String getSelectTextColor() {
		return selectTextColor;
	}

	public void setSelectTextColor(String selectTextColor) {
		if (selectTextColor == null)
			this.selectTextColor = null;
		else
			this.selectTextColor = selectTextColor;
		flagTabsChanged();
	}

	public String getUnselectTextColor() {
		return unselectTextColor;
	}
	public void setUnselectedColor(String color) {
		if (color == null)
			this.unselectColor = null;
		else
			this.unselectColor = color;
		flagTabsChanged();
	}
	public String getUnselectedColor() {
		return unselectColor;
	}
	public boolean isCustomizable() {
		return isCustomizable;
	}
	public void setIsCustomizable(boolean isCustomizable) {
		if (this.isCustomizable == isCustomizable)
			return;
		this.isCustomizable = isCustomizable;
		flagTabsChanged();
	}

	public void setHasExtraButtonAlways(boolean t) {
		if (t == this.hasExtraButtonAlways)
			return;
		this.hasExtraButtonAlways = t;
		flagTabsChanged();
	}
	public void setHasMenuAlways(boolean t) {
		if (t == this.hasMenuAlways)
			return;
		this.hasMenuAlways = t;
		flagTabsChanged();
	}

	public boolean getHasMenuAlways() {
		return hasMenuAlways;
	}
	public boolean getHasExtraButtonAlwasy() {
		return hasExtraButtonAlways;
	}
	public String getAddButtonText() {
		return addButtonText;
	}

	public void setAddButtonText(String addButtonText) {
		if (OH.eq(this.addButtonText, addButtonText))
			return;
		this.addButtonText = addButtonText;
		flagTabsChanged();
	}

	public boolean hasExtraButton() {
		return isCustomizable || hasExtraButtonAlways;
	}

	public boolean hasMenu() {
		return isCustomizable || hasMenuAlways;
	}
	public boolean getHideTabWhenPoppedOut() {
		return hideTabWhenPoppedOut;
	}
	public void setHideTabWhenPoppedOut(Boolean hideTabWhenPoppedOut) {
		if (hideTabWhenPoppedOut == null || this.hideTabWhenPoppedOut == hideTabWhenPoppedOut)
			return;
		this.hideTabWhenPoppedOut = hideTabWhenPoppedOut;

		this.tabs.redockTabs();
		flagTabsChanged();
	}
	public boolean isShowTabsOverride() {
		return showTabsOverride;
	}
	public void setShowTabsOverride(boolean showTabsOverride) {
		this.showTabsOverride = showTabsOverride;
		flagTabsChanged();
	}
	public void setHideArrangeTabs(boolean hideArrangeTabs) {
		this.hideArrangeTabs = hideArrangeTabs;
		flagTabsChanged();
	}
	public boolean getHideArrangeTabs() {
		return this.hideArrangeTabs;
	}
	public void setHasAddButton(boolean hasAddButton) {
		this.hasAddButton = hasAddButton;
		flagTabsChanged();
	}
	public boolean getHasAddButton() {
		return this.hasAddButton;
	}
	public void setAddButtonColor(String color) {
		this.addButtonColor = color;
		flagTabsChanged();
	}
	public String getAddButtonColor() {
		return this.addButtonColor;
	}
	public String getBorderColor() {
		return borderColor;
	}
	public void setBorderColor(String borderColor) {
		if (borderColor == null)
			this.borderColor = null;
		else
			this.borderColor = borderColor;
		flagTabsChanged();
	}
	public void setSelBorderColor(String selBorderColor) {
		if (selBorderColor == null)
			this.selBorderColor = null;
		else
			this.selBorderColor = selBorderColor;
		flagTabsChanged();
	}
	public String getSelBorderColor() {
		return selBorderColor;
	}
	public String getFontFamily() {
		return fontFamily;
	}
	public void setFontFamily(String fontFamily) {
		this.fontFamily = fontFamily;
		flagTabsChanged();
	}
	public void setSelBorderSize(Integer selBorderSize) {
		this.selBorderSize = selBorderSize;
		flagTabsChanged();
	}

	public Integer getSelBorderSize() {
		return this.selBorderSize;
	}

}
