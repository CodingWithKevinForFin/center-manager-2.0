package com.f1.ami.web;

import java.util.Map;

import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuFactory;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.portal.impl.form.FormPortletToggleButtonsField;
import com.f1.suite.web.portal.style.PortletStyleManager_Form;
import com.f1.utils.OH;

public class AmiWebDeveloperSettingsPortlet extends GridPortlet implements FormPortletContextMenuFactory, FormPortletContextMenuListener, FormPortletListener {

	public static final String AMI_TEXT = "default";
	public static final String AMI_VIM = "vi";
	public static final String EXPORT_LEGIBLE = "legible";
	public static final String EXPORT_COMPACT = "compact";
	public static final String EXPORT_EXPANDED = "expanded";
	private FormPortlet form;
	private final FormPortletSelectField<String> infoField;
	private final FormPortletSelectField<String> warningField;
	private final FormPortletSelectField<String> aceEditorKeyboardField;
	private final FormPortletSelectField<String> logoutSettingField;
	private final FormPortletToggleButtonsField<String> expandHeaderField;
	private final FormPortletToggleButtonsField<String> showTabStyleEditorField;
	private final FormPortletToggleButtonsField<String> showDividerSettingEditorField;
	private final FormPortletToggleButtonsField<String> showAutosavePromptField;
	final private FormPortletButton okayButton;
	final private FormPortletButton cancelButton;
	final private AmiWebDesktopPortlet desktop;
	final private FormPortletSelectField<String> exportSettingField;
	final private AmiWebVarsManager vars;
	private FormPortletTitleField layoutSettingTitleField;
	private FormPortletTitleField editorSettingTitleField;
	private FormPortletTitleField headerSettingTitleField;
	private FormPortletTitleField editorShowTitleField;
	private FormPortletTitleField autosaveShowTitleField;
	private PortletStyleManager_Form formStyle;
	private FormPortletTitleField debugSettingTitleField;
	private AmiWebService service;

	public AmiWebDeveloperSettingsPortlet(PortletConfig config) {
		super(config);
		this.service = AmiWebUtils.getService(getManager());
		this.desktop = service.getDesktop();
		this.vars = this.desktop.getService().getVarsManager();
		this.formStyle = getManager().getStyleManager().getFormStyle();
		form = addChild(new FormPortlet(generateConfig()), 0, 0);
		debugSettingTitleField = form.addField(new FormPortletTitleField("Debug Settings"));
		form.addField(infoField = new FormPortletSelectField<String>(String.class, "Capture Info <img src='rsc/portlet_icon_debug.gif'>"));
		infoField.addOption(AmiWebConsts.DEBUG_ONLY, "When in layout editor mode");
		infoField.addOption(AmiWebConsts.ALWAYS, "Always");
		infoField.addOption(AmiWebConsts.NEVER, "Never");
		infoField.setValue(this.desktop.getDebugInfoSetting());
		form.addField(warningField = new FormPortletSelectField<String>(String.class, "Capture Warnings <img src='rsc/portlet_icon_warning.gif'>"));
		warningField.addOption(AmiWebConsts.DEBUG_ONLY, "When in layout editor mode");
		warningField.addOption(AmiWebConsts.ALWAYS, "Always");
		warningField.addOption(AmiWebConsts.NEVER, "Never");
		warningField.setValue(this.desktop.getDebugWarningSetting());
		form.addField(logoutSettingField = new FormPortletSelectField<String>(String.class, "On Automated Logout"));
		logoutSettingField.addOption("Debug", "Debug (Don't actually logout, just notify you)");
		logoutSettingField.addOption("Logout", "Logout (Actually logout, changes are lost)");
		logoutSettingField.addOption("Ignore", "Ignore (Don't logout, don't notify you)");
		this.logoutSettingField.setValueNoThrow(this.vars.getSetting(AmiWebConsts.USER_SETTING_LOGOUT));

		layoutSettingTitleField = form.addField(new FormPortletTitleField("Layout Settings"));
		form.addField(exportSettingField = new FormPortletSelectField<String>(String.class, "Export Config:"));
		exportSettingField.addOption(AmiWebDeveloperSettingsPortlet.EXPORT_COMPACT, "Compact");
		exportSettingField.addOption(AmiWebDeveloperSettingsPortlet.EXPORT_LEGIBLE, "Legible");
		exportSettingField.addOption(AmiWebDeveloperSettingsPortlet.EXPORT_EXPANDED, "Expanded");
		String exportMode = this.vars.getSetting(AmiWebConsts.USER_SETTING_EXPORT);
		if ("readable".equals(exportMode))//backwards compatibility
			exportMode = EXPORT_EXPANDED;
		exportSettingField.setValue(OH.noNull(exportMode, AmiWebDeveloperSettingsPortlet.EXPORT_COMPACT));

		editorSettingTitleField = form.addField(new FormPortletTitleField("Editor Settings"));
		form.addField(aceEditorKeyboardField = new FormPortletSelectField<String>(String.class, "Editor Keyboard:"));
		aceEditorKeyboardField.addOption(AmiWebDeveloperSettingsPortlet.AMI_TEXT, "Default");
		aceEditorKeyboardField.addOption(AmiWebDeveloperSettingsPortlet.AMI_VIM, "Vi Mode");
		aceEditorKeyboardField.setValue(OH.noNull(this.vars.getSetting(AmiWebConsts.USER_SETTING_AMI_EDITOR_KEYBOARD), AmiWebDeveloperSettingsPortlet.AMI_TEXT));

		headerSettingTitleField = form.addField(new FormPortletTitleField("Header Settings"));
		expandHeaderField = new FormPortletToggleButtonsField<String>(String.class, "Headers:");
		expandHeaderField.addOption(AmiWebConsts.DEVELOPER_HEADERS_EXPAND, "Expanded");
		expandHeaderField.addOption(AmiWebConsts.DEVELOPER_HEADERS_COLLAPSE, "Collapsed");
		expandHeaderField.setMode(FormPortletToggleButtonsField.TOGGLE_MODE_SELECT);

		expandHeaderField.setValueNoThrow(OH.noNull(this.vars.getSetting(AmiWebConsts.USER_SETTING_DEVELOPER_HEADERS), AmiWebConsts.DEVELOPER_HEADERS_EXPAND));
		form.addField(expandHeaderField);

		editorShowTitleField = this.form.addField(new FormPortletTitleField("Show style/setting on creation"));
		this.showTabStyleEditorField = this.form.addField(new FormPortletToggleButtonsField<String>(String.class, "Tab Style:"));
		this.showTabStyleEditorField.addOption(AmiWebConsts.STYLE_EDITOR_SHOW, "Show");
		this.showTabStyleEditorField.addOption(AmiWebConsts.STYLE_EDITOR_HIDE, "Hide");
		this.showTabStyleEditorField.setValue(OH.noNull(this.vars.getSetting(AmiWebConsts.USER_SETTING_SHOW_STYLE_EDITOR_TABS), AmiWebConsts.STYLE_EDITOR_SHOW));
		this.showDividerSettingEditorField = this.form.addField(new FormPortletToggleButtonsField<String>(String.class, "Divider Setting:"));
		this.showDividerSettingEditorField.addOption(AmiWebConsts.STYLE_EDITOR_SHOW, "Show");
		this.showDividerSettingEditorField.addOption(AmiWebConsts.STYLE_EDITOR_HIDE, "Hide");
		this.showDividerSettingEditorField.setValue(OH.noNull(this.vars.getSetting(AmiWebConsts.USER_SETTING_SHOW_SETTING_DIVIDER), AmiWebConsts.STYLE_EDITOR_SHOW));

		autosaveShowTitleField = this.form.addField(new FormPortletTitleField("Show autosave prompt"));
		this.showAutosavePromptField = new FormPortletToggleButtonsField<String>(String.class, "Autosave Prompt:");
		this.showAutosavePromptField.addOption(AmiWebConsts.DEVELOPER_AUTOSAVE_SHOW, "Show");
		this.showAutosavePromptField.addOption(AmiWebConsts.DEVELOPER_AUTOSAVE_HIDE, "Hide");
		this.showAutosavePromptField.setMode(FormPortletToggleButtonsField.TOGGLE_MODE_SELECT);
		this.showAutosavePromptField.setValueNoThrow(OH.noNull(this.vars.getSetting(AmiWebConsts.USER_SETTING_SHOW_AUTOSAVE_PROMPT), AmiWebConsts.DEVELOPER_AUTOSAVE_SHOW));
		form.addField(showAutosavePromptField);

		this.infoField.setDisabled(this.vars.isReadonlySetting(AmiWebConsts.USER_SETTING_DEBUG_INFO));
		this.warningField.setDisabled(this.vars.isReadonlySetting(AmiWebConsts.USER_SETTING_DEBUG_WARNING));
		this.expandHeaderField.setDisabled(this.vars.isReadonlySetting(AmiWebConsts.USER_SETTING_DEVELOPER_HEADERS));
		this.showTabStyleEditorField.setDisabled(this.vars.isReadonlySetting(AmiWebConsts.USER_SETTING_SHOW_STYLE_EDITOR_TABS));
		this.showDividerSettingEditorField.setDisabled(this.vars.isReadonlySetting(AmiWebConsts.USER_SETTING_SHOW_SETTING_DIVIDER));
		this.aceEditorKeyboardField.setDisabled(this.vars.isReadonlySetting(AmiWebConsts.USER_SETTING_AMI_EDITOR_KEYBOARD));
		this.exportSettingField.setDisabled(this.vars.isReadonlySetting(AmiWebConsts.USER_SETTING_EXPORT));
		this.logoutSettingField.setDisabled(this.vars.isReadonlySetting(AmiWebConsts.USER_SETTING_LOGOUT));
		this.showAutosavePromptField.setDisabled(this.vars.isReadonlySetting(AmiWebConsts.USER_SETTING_SHOW_AUTOSAVE_PROMPT));

		form.setMenuFactory(this);
		form.addMenuListener(this);
		form.addFormPortletListener(this);
		okayButton = form.addButton(new FormPortletButton("OK"));
		cancelButton = form.addButton(new FormPortletButton("Cancel"));
		prepareStyle();
	}
	private void prepareStyle() {
		if (formStyle.isUseDefaultStyling())
			return;
		form.getFormPortletStyle().setCssStyle(desktop.getService().getUserFormStyleManager().getFormStyle());
		form.getFormPortletStyle().setButtonPanelStyle(desktop.getService().getUserFormStyleManager().getFormButtonPanelStyle());
		debugSettingTitleField.setStyle("_fg=" + formStyle.getDefaultFormTitleColor() + "|_fm=bold," + formStyle.getDefaultFormFontFam());
		this.infoField.setLabelCssStyle("_fg=" + formStyle.getDefaultFormTitleColor() + "|_fm=bold," + formStyle.getDefaultFormFontFam());
		this.infoField.setBgColor(formStyle.getDefaultFormBgColor());
		this.infoField.setBorderColor(formStyle.getFormBorderColor());
		this.infoField.setFontColor(formStyle.getDefaultFormFontColor());
		this.infoField.setFieldFontFamily(formStyle.getDefaultFormFontFam());

		this.warningField.setLabelCssStyle("_fg=" + formStyle.getDefaultFormTitleColor() + "|_fm=bold," + formStyle.getDefaultFormFontFam());
		this.warningField.setBgColor(formStyle.getDefaultFormBgColor());
		this.warningField.setBorderColor(formStyle.getFormBorderColor());
		this.warningField.setFontColor(formStyle.getDefaultFormFontColor());
		this.warningField.setFieldFontFamily(formStyle.getDefaultFormFontFam());

		this.logoutSettingField.setLabelCssStyle("_fg=" + formStyle.getDefaultFormTitleColor() + "|_fm=bold," + formStyle.getDefaultFormFontFam());
		this.logoutSettingField.setBgColor(formStyle.getDefaultFormBgColor());
		this.logoutSettingField.setBorderColor(formStyle.getFormBorderColor());
		this.logoutSettingField.setFontColor(formStyle.getDefaultFormFontColor());
		this.logoutSettingField.setFieldFontFamily(formStyle.getDefaultFormFontFam());
		this.layoutSettingTitleField.setStyle("_fg=" + formStyle.getDefaultFormTitleColor() + "|_fm=bold," + formStyle.getDefaultFormFontFam());

		this.exportSettingField.setLabelCssStyle("_fg=" + formStyle.getDefaultFormTitleColor() + "|_fm=bold," + formStyle.getDefaultFormFontFam());
		this.exportSettingField.setBgColor(formStyle.getDefaultFormBgColor());
		this.exportSettingField.setBorderColor(formStyle.getFormBorderColor());
		this.exportSettingField.setFontColor(formStyle.getDefaultFormFontColor());
		this.exportSettingField.setFieldFontFamily(formStyle.getDefaultFormFontFam());

		this.editorSettingTitleField.setStyle("_fg=" + formStyle.getDefaultFormTitleColor() + "|_fm=bold," + formStyle.getDefaultFormFontFam());

		this.aceEditorKeyboardField.setLabelCssStyle("_fg=" + formStyle.getDefaultFormTitleColor() + "|_fm=bold," + formStyle.getDefaultFormFontFam());
		this.aceEditorKeyboardField.setBgColor(formStyle.getDefaultFormBgColor());
		this.aceEditorKeyboardField.setBorderColor(formStyle.getFormBorderColor());
		this.aceEditorKeyboardField.setFontColor(formStyle.getDefaultFormFontColor());
		this.aceEditorKeyboardField.setFieldFontFamily(formStyle.getDefaultFormFontFam());

		this.headerSettingTitleField.setStyle("_fg=" + formStyle.getDefaultFormTitleColor() + "|_fm=bold," + formStyle.getDefaultFormFontFam());

		this.expandHeaderField.setLabelCssStyle("_fg=" + formStyle.getDefaultFormTitleColor() + "|_fm=bold," + formStyle.getDefaultFormFontFam());
		this.expandHeaderField.setBgColor(formStyle.getDefaultFormBgColor());
		this.expandHeaderField.setBorderColor(formStyle.getFormBorderColor());
		this.expandHeaderField.setFontColor(formStyle.getDefaultFormFontColor());
		this.expandHeaderField.setFieldFontFamily(formStyle.getDefaultFormFontFam());

		this.editorShowTitleField.setStyle("_fg=" + formStyle.getDefaultFormTitleColor() + "|_fm=bold," + formStyle.getDefaultFormFontFam());

		this.showTabStyleEditorField.setLabelCssStyle("_fg=" + formStyle.getDefaultFormTitleColor() + "|_fm=bold," + formStyle.getDefaultFormFontFam());
		this.showTabStyleEditorField.setBgColor(formStyle.getDefaultFormBgColor());
		this.showTabStyleEditorField.setBorderColor(formStyle.getFormBorderColor());
		this.showTabStyleEditorField.setFontColor(formStyle.getDefaultFormFontColor());
		this.showTabStyleEditorField.setFieldFontFamily(formStyle.getDefaultFormFontFam());
		this.showDividerSettingEditorField.setLabelCssStyle("_fg=" + formStyle.getDefaultFormTitleColor() + "|_fm=bold," + formStyle.getDefaultFormFontFam());
		this.showDividerSettingEditorField.setBgColor(formStyle.getDefaultFormBgColor());
		this.showDividerSettingEditorField.setBorderColor(formStyle.getFormBorderColor());
		this.showDividerSettingEditorField.setFontColor(formStyle.getDefaultFormFontColor());
		this.showDividerSettingEditorField.setFieldFontFamily(formStyle.getDefaultFormFontFam());

		this.autosaveShowTitleField.setStyle("_fg=" + formStyle.getDefaultFormTitleColor() + "|_fm=bold," + formStyle.getDefaultFormFontFam());

		this.showAutosavePromptField.setLabelCssStyle("_fg=" + formStyle.getDefaultFormTitleColor() + "|_fm=bold," + formStyle.getDefaultFormFontFam());
		this.showAutosavePromptField.setBgColor(formStyle.getDefaultFormBgColor());
		this.showAutosavePromptField.setBorderColor(formStyle.getFormBorderColor());
		this.showAutosavePromptField.setFontColor(formStyle.getDefaultFormFontColor());
		this.showAutosavePromptField.setFieldFontFamily(formStyle.getDefaultFormFontFam());

	}
	@Override
	public WebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition) {
		return null;
	}

	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {

	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.cancelButton)
			close();
		else if (button == this.okayButton) {
			this.desktop.setDebugInfoSetting(this.infoField.getValue());
			this.desktop.setDebugWarningSetting(this.warningField.getValue());
			this.vars.putSetting(AmiWebConsts.USER_SETTING_DEBUG_INFO, this.infoField.getValue());
			this.vars.putSetting(AmiWebConsts.USER_SETTING_DEBUG_WARNING, this.warningField.getValue());
			this.vars.putSetting(AmiWebConsts.USER_SETTING_DEVELOPER_HEADERS, this.expandHeaderField.getValue());
			this.vars.putSetting(AmiWebConsts.USER_SETTING_SHOW_STYLE_EDITOR_TABS, this.showTabStyleEditorField.getValue());
			this.vars.putSetting(AmiWebConsts.USER_SETTING_SHOW_SETTING_DIVIDER, this.showDividerSettingEditorField.getValue());
			this.vars.putSetting(AmiWebConsts.USER_SETTING_AMI_EDITOR_KEYBOARD, this.aceEditorKeyboardField.getValue());
			this.vars.putSetting(AmiWebConsts.USER_SETTING_EXPORT, this.exportSettingField.getValue());
			this.vars.putSetting(AmiWebConsts.USER_SETTING_LOGOUT, this.logoutSettingField.getValue());
			this.vars.putSetting(AmiWebConsts.USER_SETTING_SHOW_AUTOSAVE_PROMPT, this.showAutosavePromptField.getValue());
			this.close();
		}

	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {

	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {

	}

}
