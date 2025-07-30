package com.f1.suite.web.portal.style;

import com.f1.utils.OH;

public class PortletStyleManager_Confirm implements Cloneable {

	private String defaultConfirmDialogBackgroundStyle = "_cn=confirm_dialog_text";
	private String defaultConfirmDialogTextStyle = null;
	private PortletStyleManager_Form buttonStyle = null;
	private int defaultIconWidth = 130;

	public String getDefaultConfirmDialogBackgroundStyle() {
		return this.defaultConfirmDialogBackgroundStyle;
	}

	public void setDefaultConfirmDialogBackgroundStyle(String defaultConfirmDialogBackgroundStyle) {
		this.defaultConfirmDialogBackgroundStyle = defaultConfirmDialogBackgroundStyle;
	}

	public String getDefaultConfirmDialogTextStyle() {
		return defaultConfirmDialogTextStyle;
	}

	public void setDefaultConfirmDialogTextStyle(String defaultConfirmDialogTextStyle) {
		this.defaultConfirmDialogTextStyle = defaultConfirmDialogTextStyle;
	}

	public PortletStyleManager_Form getButtonStyle() {
		return buttonStyle;
	}

	public void setButtonStyle(PortletStyleManager_Form buttonStyle) {
		this.buttonStyle = buttonStyle;
	}

	public PortletStyleManager_Confirm clone() {
		try {
			return (PortletStyleManager_Confirm) super.clone();
		} catch (CloneNotSupportedException e) {
			throw OH.toRuntime(e);
		}
	}

	public int getDefaultIconWidth() {
		return this.defaultIconWidth;
	}

	public void setDefaultIconWidth(int defaultIconWidth) {
		this.defaultIconWidth = defaultIconWidth;
	}

}
