package com.f1.suite.web.portal.impl.form;

import com.f1.suite.web.portal.style.PortletStyleManager_Form;
import com.f1.utils.OH;

public class FormPortletStyle {
	private PortletStyleManager_Form styleManager;
	private int labelsWidth;
	private int fieldSpacing;
	private int buttonHeight;
	private int buttonPaddingB;
	private int buttonPaddingT;
	private String buttonPanelStyle;
	private String buttonsStyle;
	private int buttonsSpacing;
	private String labelsStyle;
	private int widthStretchPadding;
	private String scrollGripColor;
	private String scrollTrackColor;
	private String scrollButtonColor;
	private String scrollIconsColor;
	private String scrollBorderColor;
	private Integer scrollBarWidth = 15;
	private int labelPadding = 0;
	private boolean labelStatus = true;
	private String cssStyle;
	private int htmlRotate;

	private FormPortlet form;
	private boolean showBottomButtons = true;
	private int scrollBarRadius;
	private Boolean hideArrows = false;
	private String cornerColor;

	public FormPortletStyle(FormPortlet form, PortletStyleManager_Form styleManager) {
		this.form = form;
		this.styleManager = styleManager;
		this.setStyleManager(this.styleManager);
	}
	public PortletStyleManager_Form getStyleManager() {
		return this.styleManager;
	}
	public FormPortlet setStyleManager(PortletStyleManager_Form styleManager) {
		if (styleManager.isUseDefaultStyling()) {
			this.buttonsStyle = styleManager.getDefaultFormButtonsStyle();
			this.buttonPanelStyle = styleManager.getDefaultFormButtonPanelStyle();
			this.cssStyle = styleManager.getDefaultFormStyle();
		} else {
			this.buttonsStyle = styleManager.getFormButtonsStyle();
			this.buttonPanelStyle = styleManager.getFormButtonPanelStyle();
			this.cssStyle = styleManager.getFormStyle();
		}
		this.buttonHeight = styleManager.getDefaultFormButtonsHeight();
		this.buttonPaddingT = styleManager.getDefaultFormButtonsPaddingTop();
		this.buttonPaddingB = styleManager.getDefaultFormButtonsPaddingBottom();
		this.buttonsSpacing = styleManager.getDefaultFormButtonsSpacing();
		this.labelsWidth = styleManager.getDefaultFormLabelsWidth();
		this.labelsStyle = styleManager.getDefaultFormLabelsStyle();
		this.fieldSpacing = styleManager.getDefaultFormFieldSpacing();
		this.setWidthStretchPadding(styleManager.getDefaultFormWidthStretchPadding());
		this.scrollGripColor = styleManager.getDefaultFormScrollGripColor();
		this.scrollTrackColor = styleManager.getDefaultFormScrollTrackColor();
		this.scrollButtonColor = styleManager.getDefaultFormScrollButtonColor();
		this.scrollIconsColor = styleManager.getDefaultFormScrollIconsColor();
		this.scrollBarWidth = styleManager.getDefaultFormScrollBarWidth();
		return form;
	}

	public String getCssStyle() {
		return this.cssStyle;
	}
	public FormPortlet setCssStyle(String cssStyle) {
		if (OH.eq(this.cssStyle, cssStyle))
			return form;
		this.cssStyle = cssStyle;
		form.flagStyleChanged();
		return form;
	}
	public int getHtmlRotate() {
		return htmlRotate;
	}

	public FormPortlet setHtmlRotate(int htmlRotate) {
		if (OH.eq(this.htmlRotate, htmlRotate))
			return this.form;
		if (htmlRotate % 90 != 0)
			throw new IllegalArgumentException("Must be multiple of 90 degrees not: " + htmlRotate);
		this.htmlRotate = htmlRotate;
		form.flagStyleChanged();
		return this.form;
	}
	public int getLabelsWidth() {
		return labelsWidth;
	}

	public FormPortlet setLabelsWidth(int labelSize) {
		if (this.labelsWidth == labelSize)
			return form;
		this.labelsWidth = labelSize;
		form.flagStyleChanged();
		return form;
	}
	public int getButtonHeight() {
		return buttonHeight;
	}

	public FormPortlet setButtonHeight(int buttonHeight) {
		if (this.buttonHeight == buttonHeight)
			return form;
		this.buttonHeight = buttonHeight;
		form.flagStyleChanged();
		return form;
	}
	public FormPortlet setButtonPaddingB(int buttonPaddingB) {
		if (this.buttonPaddingB == buttonPaddingB)
			return form;
		this.buttonPaddingB = buttonPaddingB;
		form.flagStyleChanged();
		return form;
	}

	public int getButtonPaddingB() {
		return buttonPaddingB;
	}
	public int getButtonPaddingT() {
		return buttonPaddingT;
	}

	public FormPortlet setButtonPaddingT(int buttonPaddingT) {
		if (this.buttonPaddingT == buttonPaddingT)
			return form;
		this.buttonPaddingT = buttonPaddingT;
		form.flagStyleChanged();
		return form;
	}

	public String getButtonPanelStyle() {
		return buttonPanelStyle;
	}

	public FormPortlet setButtonPanelStyle(String buttonPanelStyle) {
		if (OH.eq(this.buttonPanelStyle, buttonPanelStyle))
			return form;
		this.buttonPanelStyle = buttonPanelStyle;
		form.flagStyleChanged();
		return form;
	}
	public String getButtonsStyle() {
		return buttonsStyle;
	}

	public FormPortlet setButtonsStyle(String buttonsStyle) {
		if (OH.eq(this.buttonsStyle, buttonsStyle))
			return form;
		this.buttonsStyle = buttonsStyle;
		form.flagStyleChanged();
		return form;
	}

	public int getButtonsSpacing() {
		return buttonsSpacing;
	}

	public FormPortlet setButtonsSpacing(int buttonsSpacing) {
		if (this.buttonsSpacing == buttonsSpacing)
			return form;
		this.buttonsSpacing = buttonsSpacing;
		form.flagStyleChanged();
		return form;
	}
	public String getLabelsStyle() {
		return labelsStyle;
	}

	public FormPortlet setLabelsStyle(String labelsStyle) {
		if (OH.eq(this.labelsStyle, labelsStyle))
			return form;
		this.labelsStyle = labelsStyle;
		form.flagStyleChanged();
		return form;
	}

	public int getFieldSpacing() {
		return fieldSpacing;
	}
	public void setFieldSpacing(int fieldSpacing) {
		this.fieldSpacing = fieldSpacing;
	}

	public int getWidthStretchPadding() {
		return widthStretchPadding;
	}

	public FormPortlet setWidthStretchPadding(int widthStretchPadding) {
		if (OH.eq(this.widthStretchPadding, widthStretchPadding))
			return form;
		this.widthStretchPadding = widthStretchPadding;
		return form;
	}
	public String getScrollGripColor() {
		return scrollGripColor;
	}

	public void setScrollGripColor(String scrollGripColor) {
		if (this.scrollGripColor == scrollGripColor)
			return;
		this.scrollGripColor = scrollGripColor;
		form.flagScrollStyleChanged();
	}

	public String getScrollTrackColor() {
		return scrollTrackColor;
	}

	public void setScrollTrackColor(String scrollTrackColor) {
		if (this.scrollTrackColor == scrollTrackColor)
			return;
		this.scrollTrackColor = scrollTrackColor;
		form.flagScrollStyleChanged();
	}

	public String getScrollButtonColor() {
		return scrollButtonColor;
	}

	public void setScrollButtonColor(String scrollButtonColor) {
		if (this.scrollButtonColor == scrollButtonColor)
			return;
		this.scrollButtonColor = scrollButtonColor;
		form.flagScrollStyleChanged();
	}

	public String getScrollIconsColor() {
		return scrollIconsColor;
	}

	public void setScrollIconsColor(String scrollIconsColor) {
		if (this.scrollIconsColor == scrollIconsColor)
			return;
		this.scrollIconsColor = scrollIconsColor;
		form.flagScrollStyleChanged();
	}
	public String getScrollBorderColor() {
		return scrollBorderColor;
	}
	public void setScrollBorderColor(String scrollBorderColor) {
		if (this.scrollBorderColor == scrollBorderColor)
			return;
		this.scrollBorderColor = scrollBorderColor;
		form.flagScrollStyleChanged();
	}
	public Integer getScrollBarWidth() {
		return scrollBarWidth;
	}

	public void setScrollBarWidth(Integer scrollBarWidth) {
		if (this.scrollBarWidth == scrollBarWidth)
			return;
		this.scrollBarWidth = scrollBarWidth;
		form.flagScrollStyleChanged();
	}

	public void setScrollBarRadius(int scrollBarRadius) {
		if (this.scrollBarRadius == scrollBarRadius)
			return;
		this.scrollBarRadius = scrollBarRadius;
		form.flagScrollStyleChanged();
	}

	public Integer getScrollBarRadius() {
		return scrollBarRadius;
	}

	public boolean getShowBottomButtons() {
		return showBottomButtons;
	}

	public void setShowBottomButtons(boolean showBottomButtons) {
		if (this.showBottomButtons == showBottomButtons)
			return;
		this.showBottomButtons = showBottomButtons;
		form.flagButtonsChanged();
	}
	public int getLabelPadding() {
		return labelPadding;
	}
	public void setLabelPadding(int labelPadding) {
		if (this.labelPadding == labelPadding)
			return;
		this.labelPadding = labelPadding;
		form.flagStyleChanged();
	}
	public boolean getLabelStatus() {
		return labelStatus;
	}
	public void setLabelStatus(boolean status) {
		this.labelStatus = status;
	}
	public void setScrollBarHideArrows(Boolean hide) {
		if (this.hideArrows == hide)
			return;
		this.hideArrows = hide;
		form.flagScrollStyleChanged();
	}

	public void setScrollBarCornerColor(String color) {
		if (this.cornerColor == color)
			return;
		this.cornerColor = color;
		form.flagScrollStyleChanged();
	}

	public String getScrollBarCornerColor() {
		return this.cornerColor;
	}
	public Boolean getScrollBarHideArrows() {
		return hideArrows;
	}

}
