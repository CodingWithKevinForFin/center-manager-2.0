package com.f1.suite.web.portal.style;

import com.f1.suite.web.portal.impl.DesktopPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButtonField;
import com.f1.suite.web.portal.impl.form.FormPortletColorField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletNumericRangeField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;

public class PortletStyleManager {

	private PortletStyleManager_Form formStyle;
	private PortletStyleManager_Dialog dialogStyle;
	private PortletStyleManager_Tab tabStyle;
	private PortletStyleManager_Menu menuStyle;

	public PortletStyleManager() {
		this.formStyle = new PortletStyleManager_Form();
		this.dialogStyle = new PortletStyleManager_Dialog();
		this.menuStyle = new PortletStyleManager_Menu();
		this.tabStyle = new PortletStyleManager_Tab();
		initFormDefaultStyles(this.formStyle);
		initDialogFormDefaultStyles(this.dialogStyle);
	}

	private void initDialogFormDefaultStyles(PortletStyleManager_Dialog dialogStyle2) {
		dialogStyle2.getDefaultDialogOptions().put(DesktopPortlet.OPTION_WINDOW_BORDER_INNER_SIZE, "0");
		dialogStyle2.setUserButtonStyle(this.formStyle.clone());
	}

	private void initFormDefaultStyles(PortletStyleManager_Form formStyle2) {
		// TODO Auto-generated method stub
		formStyle2.putDefaultFormFieldStyle(FormPortletTitleField.JSNAME, "_fs=11px|style.textTransform=upperCase|_fg=#000000|style.padding=4px 0px 0px 0px|style.display=block");
		formStyle2.putDefaultFormFieldStyle(FormPortletTextField.JSNAME, "style.border=1px solid white|_fs=11px");
		formStyle2.putDefaultFormFieldStyle(FormPortletSelectField.JSNAME, "style.border=1px solid white|_fs=11px");
		formStyle2.putDefaultFormFieldStyle(FormPortletTextAreaField.JSNAME, "style.border=1px solid white|_fs=11px|style.resize=none");
		formStyle2.putDefaultFormFieldStyle(FormPortletColorField.JSNAME, "style.border=1px solid white|_fs=11px");
		formStyle2.putDefaultFormFieldWidth("AmiCodeField", FormPortletField.HEIGHT_STRETCH);
		formStyle2.putDefaultFormFieldWidth("AmiCodeField", FormPortletField.WIDTH_STRETCH);
		formStyle2.putDefaultFormFieldStyle(FormPortletButtonField.JSNAME,
				"style.background=linear-gradient(#13B124,#007608)|_fg=#FFFFFF|style.border=1px|_cn=none|style.borderRadius=8px");
		formStyle2.putDefaultFormFieldOption(FormPortletNumericRangeField.JSNAME, FormPortletNumericRangeField.OPTION_SCROLL_GRIP_COLOR, "#007608");
		formStyle2.putDefaultFormFieldOption(FormPortletNumericRangeField.JSNAME, FormPortletNumericRangeField.OPTION_SCROLL_TRACK_LEFT_COLOR, "#007608");
	}

	public PortletStyleManager_Form getFormStyle() {
		return formStyle;
	}

	public void setFormStyle(PortletStyleManager_Form formStyle) {
		this.formStyle = formStyle;
	}

	public PortletStyleManager_Dialog getDialogStyle() {
		return dialogStyle;
	}

	public void setDialogStyle(PortletStyleManager_Dialog dialogStyle) {
		this.dialogStyle = dialogStyle;
	}

	public PortletStyleManager_Tab getTabStyle() {
		return tabStyle;
	}

	public void setTabStyle(PortletStyleManager_Tab tabStyle) {
		this.tabStyle = tabStyle;
	}

	public PortletStyleManager_Menu getMenuStyle() {
		return menuStyle;
	}

	public void setMenuStyle(PortletStyleManager_Menu style) {
		this.menuStyle = style;
	}

	public void setUseDefaultStyling(boolean useDefaultStyling) {
		dialogStyle.setUseDefaultStyling(useDefaultStyling);
		dialogStyle.getDefaultButtonStyle().setUseDefaultStyling(useDefaultStyling);
		dialogStyle.getUserButtonStyle().setUseDefaultStyling(useDefaultStyling);
		formStyle.setUseDefaultStyling(useDefaultStyling);
		tabStyle.setUseDefaultStyling(useDefaultStyling);
		menuStyle.setUseDefaultStyling(useDefaultStyling);
	}

}
