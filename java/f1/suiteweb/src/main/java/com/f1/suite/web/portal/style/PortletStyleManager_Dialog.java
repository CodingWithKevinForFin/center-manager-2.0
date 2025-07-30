package com.f1.suite.web.portal.style;

import java.util.HashMap;
import java.util.Map;

import com.f1.suite.web.portal.impl.DesktopPortlet;
import com.f1.utils.OH;

public class PortletStyleManager_Dialog implements Cloneable {
	private boolean hasCloseButton = true;
	private final int transformPivot = 70;
	private final String defaultAlertTitleFontStyle = "_fs=13px|style.textTransform=upperCase|_fg=#ffffff|style.display=block|style.height=100%";
	private int defaultDialogBorderSize = 7;
	private int defaultDialogHeaderSize = 30;
	private PortletStyleManager_Form userButtonStyle = new PortletStyleManager_Form();
	private PortletStyleManager_Form alertButtonStyle = new PortletStyleManager_Form();
	private PortletStyleManager_Form defaultButtonStyle = new PortletStyleManager_Form();
	private String defaultDialogCssClassPrefix = null;
	private boolean useDefaultStyling = false;
	// ALERT STYLING
	// default
	private int defaultDialogWidth, defaultdialogHeight;
	private final String defaultAlertTitleAlignment = "left";
	private final String defaultAlertBodyAlign = "center";
	private final int defaultAlertTitleFontSize = 13;
	private final int defaultAlertBodyFontSize = 13;
	private final int defaultAlertXButtonWidth = 32;
	private boolean defaultAlertBold = false, defaultAlertUnderline = false, defaultAlertItalic = false;

	// alert size
	private int dialogWidth = defaultDialogWidth, dialogHeight = defaultdialogHeight;
	// alert title
	private String alertTitleFontColor;
	private String alertTitleFontStyle = defaultAlertTitleFontStyle;
	private String alertTitleFontFamily = "arial";
	private String alertTitleAlignment = defaultAlertTitleAlignment;
	private int alertTitleFontSize = defaultAlertTitleFontSize;
	private int XButtonWidth = defaultAlertXButtonWidth; // x button on the far right of title
	// alert body
	private int alertBodyFontSize = defaultAlertBodyFontSize;
	private String alertBodyFontFam;
	private String alertBodyFontColor;
	private String alertBodyAlign = defaultAlertBodyAlign;
	private String alertBodyBackgroundColor;
	private boolean alertBold = defaultAlertBold, alertUnderline = defaultAlertUnderline, alertItalic = defaultAlertItalic;
	// alert buttons
	private String buttonFontFam;
	private String defaultDialogBackgroundStyle = "_bg=#e2e2e2";
	private String defaultDialogTextStyle = "style.margin=20px|_fm=center|_fs=16|style.overflow=auto|style.userSelect=text";
	private int defaultIconWidth = 130;

	private Map<String, Object> defaultDialogOptions = new HashMap<String, Object>();
	private Map<String, Object> alertDialogOptions = new HashMap<String, Object>(); // this is for the desktop portlet codes
	private int dialogBorderSize = defaultDialogBorderSize;
	private int dialogHeaderSize = defaultDialogHeaderSize;

	public PortletStyleManager_Dialog clone() {
		try {
			PortletStyleManager_Dialog r = (PortletStyleManager_Dialog) super.clone();
			r.defaultDialogOptions = new HashMap<String, Object>(r.defaultDialogOptions);
			return r;
		} catch (CloneNotSupportedException e) {
			throw OH.toRuntime(e);
		}
	}
	public PortletStyleManager_Dialog() {
		defaultDialogOptions.put(DesktopPortlet.OPTION_WINDOW_FONTSTYLE, "_fs=11px|style.textTransform=upperCase");

	}

	public String buildTextCss(boolean forInput) {
		// add left/right padding so italicized, right-aligned big text doesn't get clipped
		StringBuilder defaultStyle = new StringBuilder("style.overflow=auto|style.paddingTop=20px|style.userSelect=text|style.padding=20px 10px 0px 10px|");
		StringBuilder fontSizeStyle = new StringBuilder();
		if (forInput && getAlertBodyFontSize() > 12) {
			fontSizeStyle.append("_fs=").append((int) (12 + getAlertBodyFontSize() % 10));
		} else {
			fontSizeStyle.append("_fs=").append(getAlertBodyFontSize());
		}

		StringBuilder additionalStyles = new StringBuilder();
		additionalStyles.append("|_fm=").append(getAlertBodyFontFam()).append(",").append(getAlertBodyAlign()).append((isAlertBold() == true ? ",bold" : ""))
				.append((isAlertUnderline() == true ? ",underline" : "")).append((isAlertItalic() == true ? ",italic" : ""));
		StringBuilder base = new StringBuilder();
		base.append(fontSizeStyle).append("|_fg=").append(getAlertBodyFontColor()).append("|_bg=").append(getAlertBodyBackgroundColor()).append(additionalStyles);
		if (forInput)
			return base.toString();
		return defaultStyle.append(base).toString();
	}

	public String getDefaultDialogBackgroundStyle() {
		return this.defaultDialogBackgroundStyle;
	}

	public void setDefaultDialogBackgroundStyle(String defaultDialogBackgroundStyle) {
		this.defaultDialogBackgroundStyle = defaultDialogBackgroundStyle;
	}

	public String getDefaultDialogTextStyle() {
		return defaultDialogTextStyle;
	}

	public void setDefaultDialogTextStyle(String defaultDialogTextStyle) {
		this.defaultDialogTextStyle = defaultDialogTextStyle;
	}

	public int getDefaultIconWidth() {
		return this.defaultIconWidth;
	}

	public void setDefaultIconWidth(int defaultIconWidth) {
		this.defaultIconWidth = defaultIconWidth;
	}

	public String getDefaultDialogCssClassPrefix() {
		return this.defaultDialogCssClassPrefix;
	}

	public void setDefaultDialogCssClassPrefix(String defaultDialogCssClassPrefix) {
		this.defaultDialogCssClassPrefix = defaultDialogCssClassPrefix;
	}

	public int getDefaultDialogBorderSize() {
		return defaultDialogBorderSize;
	}

	public void setDefaultDialogBorderSize(int defaultDialogBorderSize) {
		this.defaultDialogBorderSize = defaultDialogBorderSize;
	}

	public int getDefaultDialogHeaderSize() {
		return defaultDialogHeaderSize;
	}

	public void setDefaultDialogHeaderSize(int defaultDialogHeaderSize) {
		this.defaultDialogHeaderSize = defaultDialogHeaderSize;
	}

	public Map<String, Object> getDefaultDialogOptions() {
		return defaultDialogOptions;
	}

	public Map<String, Object> getAlertDialogOptions() {
		return alertDialogOptions;
	}

	public void setDefaultDialogOptions(Map<String, Object> defaultDialogOptions) {
		this.defaultDialogOptions = defaultDialogOptions;
	}

	public Map<String, Object> getStyleOptions() {
		return useDefaultStyling ? defaultDialogOptions : alertDialogOptions;
	}

	// for title
	public void buildCustomCssStyle() {
		StringBuilder fontFormats = new StringBuilder();
		StringBuilder transform = new StringBuilder();
		int finalT = transformPivot;
		// the bigger the font size, the smaller the downward vertical shift
		finalT -= (this.alertTitleFontSize);
		transform.append("|style.transform=translateY(").append(finalT).append("%)");
		fontFormats.append("|style.position=relative");
		if (hasCloseButton && this.alertTitleAlignment.contentEquals("right")) {
			int gap = 7 + this.XButtonWidth;
			fontFormats.append("|style.margin=0px ").append(gap).append("px 0px 0px");
		}
		StringBuilder sb = new StringBuilder();
		sb.append("_fs=").append(this.alertTitleFontSize).append("|style.textTransform=upperCase|style.height=100%").append("|_fg=").append(this.alertTitleFontColor)
				.append("|_fm=").append(this.alertTitleFontFamily).append(",").append(this.alertTitleAlignment).append(fontFormats).append(transform);
		this.alertTitleFontStyle = sb.toString();
		this.alertDialogOptions.put(DesktopPortlet.OPTION_WINDOW_FONTSTYLE, this.alertTitleFontStyle);
	}

	public void setAlertBackgroundColor(String color) {
		this.alertDialogOptions.put(DesktopPortlet.OPTION_COLOR_WINDOW, color);
	}

	public void setAlertTitleFontColor(String color) {
		this.alertTitleFontColor = color;
	}

	public void setAlertTitleAlignment(String alertTitleAlignment) {
		this.alertTitleAlignment = alertTitleAlignment;
	}

	public void setAlertTitleFontSize(int size) {
		this.alertTitleFontSize = size;
	}
	public void setAlertTitleFontFamily(String ff) {
		this.alertTitleFontFamily = ff;
	}

	public void setAlertXButtonHeight(int alertXButtonHeight) {
		this.alertDialogOptions.put(DesktopPortlet.OPTION_WINDOW_BUTTON_HEIGHT, alertXButtonHeight);
	}
	public void setAlertXButtonWidth(int alertXButtonWidth) {
		this.XButtonWidth = alertXButtonWidth;
		if (this.alertTitleAlignment.contentEquals("right")) {
			//	update title margin
		}
		this.alertDialogOptions.put(DesktopPortlet.OPTION_WINDOW_BUTTON_WIDTH, alertXButtonWidth);
	}

	public void setAlertXButtonIconColor(String alertXButtonIconColor) {
		this.alertDialogOptions.put(DesktopPortlet.OPTION_COLOR_WINDOW_BUTTON_ICON, alertXButtonIconColor);
	}

	//CODE_DLG_X_BUTTON_BG_CL
	public void setAlertXButtonBgColor(String alertXButtonBgColor) {
		this.alertDialogOptions.put(DesktopPortlet.OPTION_COLOR_WINDOW_BUTTON, alertXButtonBgColor);
	}

	public void setAlertXButtonBorderColor(String alertXButtonBorderColor) {
		this.alertDialogOptions.put(DesktopPortlet.OPTION_COLOR_WINDOW_BUTTON_UP, alertXButtonBorderColor);
	}

	public void setAlertXButtonShadowColor(String alertXButtonShadowColor) {
		this.alertDialogOptions.put(DesktopPortlet.OPTION_COLOR_WINDOW_BUTTON_DOWN, alertXButtonShadowColor);
	}
	public int getDialogWidth() {
		return dialogWidth;
	}
	public void setDialogWidth(int dialogWidth) {
		if (dialogWidth < 360)
			return;
		this.dialogWidth = dialogWidth;
	}
	public int getDialogHeight() {
		return dialogHeight;
	}
	public void setDialogHeight(int dialogHeight) {
		if (dialogHeight < 170)
			return;
		this.dialogHeight = dialogHeight;
	}
	public int getAlertBodyFontSize() {
		return alertBodyFontSize;
	}
	public void setAlertBodyFontSize(int alertBodyFontSize) {
		this.alertBodyFontSize = alertBodyFontSize;
	}
	public String getAlertBodyFontFam() {
		return alertBodyFontFam;
	}
	public void setAlertBodyFontFam(String alertBodyFontFam) {
		this.alertBodyFontFam = alertBodyFontFam;
	}
	public String getAlertBodyFontColor() {
		return alertBodyFontColor;
	}
	public void setAlertBodyFontColor(String alertBodyFontColor) {
		this.alertBodyFontColor = alertBodyFontColor;
	}
	public String getAlertBodyAlign() {
		return alertBodyAlign;
	}
	public void setAlertBodyAlign(String alertBodyFontAlign) {
		this.alertBodyAlign = alertBodyFontAlign;
	}
	public String getAlertBodyBackgroundColor() {
		return alertBodyBackgroundColor;
	}
	public void setAlertBodyBackgroundColor(String alertBodyBackgroundColor) {
		this.alertBodyBackgroundColor = alertBodyBackgroundColor;
	}
	public boolean isAlertBold() {
		return alertBold;
	}
	public void setAlertBold(boolean alertBold) {
		this.alertBold = alertBold;
	}
	public boolean isAlertUnderline() {
		return alertUnderline;
	}
	public void setAlertUnderline(boolean alertUnderline) {
		this.alertUnderline = alertUnderline;
	}
	public boolean isAlertItalic() {
		return alertItalic;
	}
	public void setAlertItalic(boolean alertItalic) {
		this.alertItalic = alertItalic;
	}
	public String getButtonFontFam() {
		return buttonFontFam;
	}
	public void setButtonFontFam(String buttonFontFam) {
		this.buttonFontFam = buttonFontFam;
	}

	public PortletStyleManager_Form getUserButtonStyle() {
		return userButtonStyle;
	}

	public void setUserButtonStyle(PortletStyleManager_Form buttonStyle) {
		this.userButtonStyle = buttonStyle;
	}

	public boolean isUseDefaultStyling() {
		return useDefaultStyling;
	}
	public void setUseDefaultStyling(boolean useDefaultStyling) {
		this.useDefaultStyling = useDefaultStyling;
	}
	public PortletStyleManager_Form getDefaultButtonStyle() {
		return defaultButtonStyle;
	}
	public void setDefaultButtonStyle(PortletStyleManager_Form defaultButtonStyle) {
		this.defaultButtonStyle = defaultButtonStyle;
	}
	public int getDialogBorderSize() {
		return this.dialogBorderSize;
	}

	public void setDialogBorderSize(int newSize) {
		this.dialogBorderSize = newSize;
	}
	public int getDialogHeaderSize() {
		return this.dialogHeaderSize;
	}

	public void setDialogHeaderSize(int newSize) {
		this.dialogHeaderSize = newSize;
	}
	public void setHasCloseButton(boolean hasCloseButton) {
		this.hasCloseButton = hasCloseButton;
	}
	public PortletStyleManager_Form getAlertButtonStyle() {
		return alertButtonStyle;
	}
	public void setAlertButtonStyle(PortletStyleManager_Form alertButtonStyle) {
		this.alertButtonStyle = alertButtonStyle;
	}

}
