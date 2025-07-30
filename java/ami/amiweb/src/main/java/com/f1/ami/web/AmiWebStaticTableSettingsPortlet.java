package com.f1.ami.web;

import java.util.Set;

import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.ami.web.dm.portlets.AmiWebDmChooseDmTablePorlet;
import com.f1.ami.web.dm.portlets.AmiWebDmChooseDmTablePorlet.ChooseDmListener;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButtonField;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuFactory;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.portal.impl.form.FormPortletToggleButtonsField;
import com.f1.utils.CH;
import com.f1.utils.SH;

class AmiWebStaticTableSettingsPortlet extends AmiWebAbstractTableSettingsPortlet implements FormPortletContextMenuListener, FormPortletContextMenuFactory, ChooseDmListener {

	private FormPortletButtonField dmButton;
	private FormPortletTextField corIdField;
	private AmiWebDatasourceTablePortlet table;
	private FormPortlet settingsFormPortlet;
	private FormPortlet buttonsFormPortlet;
	private FormPortletToggleButtonsField<Boolean> enableSummaryToggle;
	private FormPortletToggleButtonsField<Boolean> editRerunDatamodel;
	private FormPortletToggleButtonsField<Boolean> editUpdatesInplace;
	private FormPortletToggleButtonsField<Boolean> displayLastRuntimeField;
	private FormPortletToggleButtonsField<String> dynamicColumnsField;
	private FormPortletToggleButtonsField<Boolean> clearOnDataStale;
	private final AmiWebDm origDm;
	private final String origTableName;

	public AmiWebStaticTableSettingsPortlet(PortletConfig config, AmiWebDatasourceTablePortlet target) {
		super(config, target);
		FormPortlet settingsForm = getSettingsForm();
		editRerunDatamodel = new FormPortletToggleButtonsField<Boolean>(Boolean.class, "Run dm after Edit:");
		editRerunDatamodel.addOption(true, "Enabled");
		editRerunDatamodel.addOption(false, "Disabled");
		editRerunDatamodel.setValue(this.table.getEditRerunDatamodel());
		settingsForm.addFieldAfter(super.editEnabledField, this.editRerunDatamodel);
		editUpdatesInplace = new FormPortletToggleButtonsField<Boolean>(Boolean.class, "Edit table in place:");
		editUpdatesInplace.addOption(true, "Enabled");
		editUpdatesInplace.addOption(false, "Disabled");
		editUpdatesInplace.setValue(this.table.getEditUpdatesInPlace());
		settingsForm.addFieldAfter(super.editEnabledField, this.editUpdatesInplace);
		updateEditFields();
		this.origDm = this.table.getDatamodel();
		this.origTableName = getFirstDmTableName();
	}
	@Override
	protected void initForms() {
		AmiWebAbstractPortlet target = getPortlet();
		this.table = (AmiWebDatasourceTablePortlet) target;
		this.settingsFormPortlet = getSettingsForm();
		this.buttonsFormPortlet = new FormPortlet(generateConfig());
		settingsFormPortlet.addField(new FormPortletTitleField("Underlying Data Model"));
		dmButton = settingsFormPortlet.addField(new FormPortletButtonField("")).setHeight(35);

		String corId = table.getCorrelationId(false);
		this.settingsFormPortlet.addField(new FormPortletTitleField("Enter Column for Correlation Id"));
		this.corIdField = this.settingsFormPortlet.addField(new FormPortletTextField("").setValue(corId));

		corIdField.setWidth(295);
		corIdField.setHasButton(true);

		settingsFormPortlet.addField(new FormPortletTitleField("'Summarize' option in context menu"));
		this.enableSummaryToggle = settingsFormPortlet.addField(new FormPortletToggleButtonsField<Boolean>(Boolean.class, ""));
		this.enableSummaryToggle.addOption(true, "Enabled");
		this.enableSummaryToggle.addOption(false, "Disabled");
		this.enableSummaryToggle.setValue(table.isRollupEnabled());

		settingsFormPortlet.addField(new FormPortletTitleField("Display Last Runtime"));
		this.displayLastRuntimeField = settingsFormPortlet.addField(new FormPortletToggleButtonsField<Boolean>(Boolean.class, ""));
		this.displayLastRuntimeField.addOption(true, "Enabled");
		this.displayLastRuntimeField.addOption(false, "Disabled");
		this.displayLastRuntimeField.setValue(table.getDisplayLastRuntime());

		settingsFormPortlet.addField(new FormPortletTitleField("Auto-Update Columns to Match datamodel"));
		this.dynamicColumnsField = settingsFormPortlet.addField(new FormPortletToggleButtonsField<String>(String.class, ""));
		this.dynamicColumnsField.addOption(AmiWebDatasourceTablePortlet.DYNAMIC_ENABLED_REARRANGE, "Enabled with Rearrange");
		this.dynamicColumnsField.addOption(AmiWebDatasourceTablePortlet.DYNAMIC_ENABLED, "Enabled");
		this.dynamicColumnsField.addOption(AmiWebDatasourceTablePortlet.DYNAMIC_OFF, "Disabled");
		this.dynamicColumnsField.setValue(table.getIsDynamicColumns());

		this.clearOnDataStale = new FormPortletToggleButtonsField<Boolean>(Boolean.class, "Clear on data stale");
		this.clearOnDataStale.setHelp(
				"On: clears the data and shows hourglass when the underlying data model is running.<br>Off: keeps old (stale) data on screen while data model is running.");
		this.clearOnDataStale.addOption(true, "On");
		this.clearOnDataStale.addOption(false, "Off");
		this.clearOnDataStale.setValue(this.table.isClearOnDataStale());
		this.settingsFormPortlet.addField(this.clearOnDataStale);

		this.buttonsFormPortlet.addButton(getSubmitButton());
		this.buttonsFormPortlet.addButton(getCancelButton());

		buttonsFormPortlet.addFormPortletListener(this);
		this.addChild(this.settingsFormPortlet, 0, 0);
		this.addChild(this.buttonsFormPortlet, 0, 1);
		this.setRowSize(1, 35);
		this.setSuggestedSize(500, 500);
		updateDatamodelButton();
	}
	private void updateDatamodelButton() {
		Set<String> names = table.getUsedDmAliasDotNames();
		String name = CH.first(names);
		AmiWebDm dm = table.getService().getDmManager().getDmByAliasDotName(name);
		if (dm != null) {
			String table = getFirstDmTableName();
			String dmLabel = dm.getAmiLayoutFullAliasDotId();
			dmButton.setValue(dmLabel + " : " + table);
		} else {
			dmButton.setValue("&lt;No datamodel&gt;");
		}
	}
	private String getFirstDmTableName() {
		return CH.first(table.getUsedDmTables(CH.first(table.getUsedDmAliasDotNames())));
	}

	protected boolean verifyChanges() {
		if (this.editEnabledField.getValue() != AmiWebAbstractTablePortlet.EDIT_OFF) {
			if (this.editRerunDatamodel.getValue() && this.editUpdatesInplace.getValue()) {
				getManager().showAlert("Can not have both 'Run dm after Edit' and 'Edit table in place' enabled");
				return false;
			}
		}
		return super.verifyChanges();
	};
	@Override
	protected void submitChanges() {
		this.table.setCorrelationId(corIdField.getValue(), false);
		this.table.setRollupEnabled(enableSummaryToggle.getValue());
		this.table.setDisplayLastRuntime(this.displayLastRuntimeField.getValue());
		this.table.setEditRerunDatamodel(this.editRerunDatamodel.getValue());
		this.table.setEditUpdatesInPlace(this.editUpdatesInplace.getValue());
		this.table.setIsDynamicColumns(this.dynamicColumnsField.getValue());
		AmiWebDm datamodel = this.table.getDatamodel();
		if (this.origDm != datamodel || (SH.is(this.origTableName) && !this.origTableName.equals(getFirstDmTableName()))) {
			this.table.onDmDataChanged(datamodel);
		}
		this.table.setClearOnDataStale(this.clearOnDataStale.getValue());
		super.submitChanges();
	}
	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
		if (node == this.dmButton) {
			String dmName = null;
			if (table.getDatamodel() != null)
				dmName = table.getDatamodel().getDmName();
			AmiWebDmChooseDmTablePorlet t = new AmiWebDmChooseDmTablePorlet(generateConfig(), dmName, this, false, this.table.getAmiLayoutFullAlias());
			getManager().showDialog("Select Datamodel", t);
		} else
			super.onContextMenu(portlet, action, node);
	}

	@Override
	protected boolean isFormulaField(FormPortletField node) {
		return super.isFormulaField(node) || node == this.corIdField;
	}
	@Override
	public void onDmSelected(AmiWebDmTableSchema selectedDmTable) {
		this.table.setUsedDatamodel(selectedDmTable.getDm().getAmiLayoutFullAliasDotId(), selectedDmTable.getName());
		updateDatamodelButton();
	}

	@Override
	protected void updateEditFields() {
		if (this.editRerunDatamodel != null) {
			boolean disabled = this.editEnabledField.getValue() == AmiWebAbstractTablePortlet.EDIT_OFF;
			this.editRerunDatamodel.setDisabled(disabled);
			this.editUpdatesInplace.setDisabled(disabled);
		}
		super.updateEditFields();
	};

}
