package com.f1.ami.web;

import java.util.Map;

import com.f1.ami.web.AmiWebWhereClause.WhereClause;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuFactory;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.portal.impl.form.FormPortletToggleButtonsField;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;

public class AmiWebAbstractTableSettingsPortlet extends AmiWebPanelSettingsPortlet implements FormPortletContextMenuListener, FormPortletContextMenuFactory {

	private final AmiWebAbstractTablePortlet portlet;
	private final FormPortletToggleButtonsField<Boolean> showCommandMenuItemsField = new FormPortletToggleButtonsField<Boolean>(Boolean.class, "");
	private final FormPortletToggleButtonsField<Boolean> scrollToBottomOnAppend = new FormPortletToggleButtonsField<Boolean>(Boolean.class, "");
	protected final FormPortletToggleButtonsField<Byte> editEnabledField = new FormPortletToggleButtonsField<Byte>(Byte.class, "");
	protected final FormPortletToggleButtonsField<Boolean> editDoubleClickField = new FormPortletToggleButtonsField<Boolean>(Boolean.class, "Double Click To Edit:");
	protected final FormPortletTextField editMenuItemTitleField = new FormPortletTextField("Edit Menu Option Title:");
	protected final FormPortletTextField editCommandIdField = new FormPortletTextField("Callback Command ID (Deprecated):");
	protected final FormPortletTextField editAppNameField = new FormPortletTextField("Callback App name (Deprecated):");
	private final FormPortletTextField downloadNameField = new FormPortletTextField("Download Name:");
	private FormPortletTextField whereField;
	private FormPortletTextField whereRuntimeField;
	private FormPortletTextField rowBackgroundColorField;
	private FormPortletTextField rowTextColorField;
	private FormPortletTextField visibleColumnsLimitField;

	public AmiWebAbstractTableSettingsPortlet(PortletConfig config, AmiWebAbstractTablePortlet portlet) {
		super(config, portlet);
		this.portlet = portlet;
		this.whereField = new FormPortletTextField("Default (on login):").setHasButton(true).setWidth(FormPortletField.WIDTH_STRETCH).setMaxChars(4096);
		this.whereRuntimeField = new FormPortletTextField("Current (not saved):").setHasButton(true).setWidth(FormPortletField.WIDTH_STRETCH).setMaxChars(4096);
		this.visibleColumnsLimitField = new FormPortletTextField("");
		this.visibleColumnsLimitField.setWidthPx(100);
		if (portlet.getTable().getVisibleColumnsLimit() != -1)
			this.visibleColumnsLimitField.setValue(Caster_String.INSTANCE.cast(portlet.getTable().getVisibleColumnsLimit()));
		FormPortlet settingsForm = getSettingsForm();
		settingsForm.addField(new FormPortletTitleField("Row Styling"));
		String rowTextColor = this.portlet.getRowTextColor().getFormula(false);
		String rowBackgroundColor = this.portlet.getRowBackgroundColor().getFormula(false);
		this.whereField.setValue(this.portlet.getDefaultWhereFilter());
		this.whereRuntimeField.setValue(this.portlet.getCurrentRuntimeFilter());
		this.rowTextColorField = settingsForm.addField(new FormPortletTextField("Text").setValue(rowTextColor));
		this.rowTextColorField.setHasButton(true).setWidth(FormPortletField.WIDTH_STRETCH);
		this.rowBackgroundColorField = settingsForm.addField(new FormPortletTextField("Background").setValue(rowBackgroundColor));
		this.rowBackgroundColorField.setHasButton(true).setWidth(FormPortletField.WIDTH_STRETCH);
		settingsForm.addField(new FormPortletTitleField("WHERE FILTER"));
		settingsForm.addField(this.whereField);
		settingsForm.addField(this.whereRuntimeField);
		settingsForm.addField(new FormPortletTitleField("Visible Columns Limit"));
		settingsForm.addField(this.visibleColumnsLimitField);
		settingsForm.addField(new FormPortletTitleField("Display commands in context menus."));
		settingsForm.addField(this.showCommandMenuItemsField);
		settingsForm.addField(new FormPortletTitleField("Scroll to Bottom when rows are appended"));
		settingsForm.addField(this.scrollToBottomOnAppend);
		settingsForm.addField(new FormPortletTitleField("In Table Editing"));
		settingsForm.addField(this.editEnabledField);
		settingsForm.addField(this.editDoubleClickField);
		settingsForm.addField(this.editMenuItemTitleField);
		settingsForm.addField(this.editAppNameField);
		settingsForm.addField(this.editCommandIdField);
		//		settingsForm.addFormPortletListener(this);
		settingsForm.setMenuFactory(this);
		settingsForm.addMenuListener(this);
		this.showCommandMenuItemsField.addOption(true, "Show");
		this.showCommandMenuItemsField.addOption(false, "Hide");
		this.showCommandMenuItemsField.setValue(this.portlet.getShowCommandMenuItems());
		this.scrollToBottomOnAppend.addOption(true, "Enabled");
		this.scrollToBottomOnAppend.addOption(false, "Disabled");
		this.scrollToBottomOnAppend.setValue(this.portlet.getScrollToBottomOnAppend());
		this.editEnabledField.addOption(AmiWebAbstractTablePortlet.EDIT_SINGLE, "Only Single Row");
		this.editEnabledField.addOption(AmiWebAbstractTablePortlet.EDIT_MULTI, "Multiple Rows");
		this.editEnabledField.addOption(AmiWebAbstractTablePortlet.EDIT_OFF, "Disable Editing");
		this.editEnabledField.setValue(this.portlet.getEditMode());
		this.editDoubleClickField.addOption(true, "Enabled");
		this.editDoubleClickField.addOption(false, "Disabled");
		this.editDoubleClickField.setValue(this.portlet.getEditViaDoubleClick());
		this.editMenuItemTitleField.setValue(this.portlet.getEditContextMenuTitle());
		this.editAppNameField.setValue(this.portlet.getEditAppName());
		this.editCommandIdField.setValue(this.portlet.getEditCommandId());
		settingsForm.addFieldAfter(this.getTitleField(), this.downloadNameField);
		this.downloadNameField.setValue(portlet.getAmiDownloadName());
		updateEditFields();
		settingsForm.addField(new FormPortletTitleField(""));
	}
	@Override
	protected void initForms() {
		super.initForms();
	}

	protected boolean verifyChanges() {
		StringBuilder errorSink = new StringBuilder();
		try {
			Caster_Integer.PRIMITIVE.cast(this.visibleColumnsLimitField.getValue());
		} catch (Exception e) {
			errorSink.append("Invalid value for the Visible Columns Limit field");
		}
		if (errorSink.length() > 0) {
			getManager().showAlert(errorSink.toString());
			return false;
		}
		WhereClause whereFm = this.portlet.compileWhereFilter(this.whereField.getValue(), errorSink);
		if (whereFm != null && whereFm.getReturnType() != Boolean.class)
			errorSink.append("Where Filter must return type of Boolean, not: " + portlet.getScriptManager().forType(whereFm.getReturnType()));
		WhereClause whereRuntimeFm = this.portlet.compileWhereFilter(this.whereRuntimeField.getValue(), errorSink);
		if (whereRuntimeFm != null && whereRuntimeFm.getReturnType() != Boolean.class)
			errorSink.append("Where Runtime Filter must return type of Boolean, not: " + portlet.getScriptManager().forType(whereFm.getReturnType()));

		if (errorSink.length() > 0) {
			getManager().showAlert(errorSink.toString());
			return false;
		}
		return super.verifyChanges();
	};

	@Override
	protected void submitChanges() {
		StringBuilder errorSink = new StringBuilder();
		WhereClause whereFm = this.portlet.compileWhereFilter(this.whereField.getValue(), errorSink);
		if (whereFm != null && whereFm.getReturnType() != Boolean.class)
			errorSink.append("Where Filter must return type of Boolean, not: " + portlet.getScriptManager().forType(whereFm.getReturnType()));
		WhereClause whereRuntimeFm = this.portlet.compileWhereFilter(this.whereRuntimeField.getValue(), errorSink);
		if (whereRuntimeFm != null && whereRuntimeFm.getReturnType() != Boolean.class) {
			errorSink.append("Where Runtime Filter must return type of Boolean, not: " + portlet.getScriptManager().forType(whereFm.getReturnType()));
		}

		if (errorSink.length() > 0) {
			getManager().showAlert(errorSink.toString());
			return;
		}
		this.portlet.setShowCommandMenuItems(this.showCommandMenuItemsField.getValue());
		this.portlet.setScrollToBottomOnAppend(this.scrollToBottomOnAppend.getValue());
		this.portlet.setEditMode(this.editEnabledField.getValue());
		this.portlet.setEditContextMenuTitle(this.editMenuItemTitleField.getValue());
		this.portlet.setEditCommandId(this.editCommandIdField.getValue());
		this.portlet.setEditAppName(this.editAppNameField.getValue());
		this.portlet.setEditViaDoubleClick(this.editDoubleClickField.getValue());
		this.portlet.setRowBackgroundColor(this.rowBackgroundColorField.getValue(), false);
		this.portlet.setRowTextColor(this.rowTextColorField.getValue(), false);
		this.portlet.setCurrentRuntimeFilter(this.whereField.getValue(), false);
		this.portlet.setCurrentRuntimeFilter(this.whereRuntimeField.getValue(), true);
		String downloadName = SH.trim(this.downloadNameField.getValue());
		if (SH.isnt(downloadName))
			downloadName = null;
		this.portlet.setAmiDownloadName(downloadName);

		int lim = Caster_Integer.INSTANCE.cast(this.visibleColumnsLimitField.getValue()) == null ? -1 : Caster_Integer.PRIMITIVE.cast(this.visibleColumnsLimitField.getValue());
		this.portlet.getTable().setVisibleColumnsLimit(lim);
		super.submitChanges();
	}

	protected String replaceTitlesWithVarsInFormula(String value) {
		return value;
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field == this.editEnabledField) {
			updateEditFields();
		} else
			super.onFieldValueChanged(portlet, field, attributes);
	}

	protected void updateEditFields() {
		boolean disabled = this.editEnabledField.getValue() == AmiWebAbstractTablePortlet.EDIT_OFF;
		this.editDoubleClickField.setDisabled(disabled);
		this.editMenuItemTitleField.setDisabled(disabled);
		this.editAppNameField.setDisabled(disabled);
		this.editCommandIdField.setDisabled(disabled);
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}
	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
		AmiWebMenuUtils.processContextMenuAction(getPortlet().getService(), action, node);
	}
	protected boolean isFormulaField(FormPortletField node) {
		return node == this.rowBackgroundColorField || node == this.rowTextColorField || node == this.whereField || node == this.whereRuntimeField;
	}
	protected boolean isColorFormulaField(FormPortletField node) {
		return node == this.rowBackgroundColorField || node == this.rowTextColorField;
	}
	@Override
	public WebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition) {
		BasicWebMenu r = new BasicWebMenu();
		AmiWebMenuUtils.createVariablesMenu(r, false, this.getPortlet());
		AmiWebMenuUtils.createColorsMenu(r, this.getPortlet().getStylePeer());
		return r;
	}

}
