package com.f1.ami.web.form.field;

import java.util.LinkedHashMap;
import java.util.Map;

import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.AmiWebQueryFormPortletUtils;
import com.f1.ami.web.form.factory.AmiWebFormSelectFieldFactory;
import com.f1.ami.web.form.queryfield.AbstractDmQueryField;
import com.f1.ami.web.form.queryfield.SelectQueryField;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.utils.CH;
import com.f1.utils.SH;

public class SelectFieldEditFormPortlet extends BaseEditFieldPortlet<SelectQueryField> {

	private FormPortletTextAreaField valuesField;
	private FormPortletSelectField<String> datasources;
	private FormPortletSelectField<String> datasourceValueColumn;
	private FormPortletSelectField<String> datasourceIdColumn;
	private FormPortletSelectField<String> displayValueSortOptionsField;
	private AmiWebService service;
	private final FormPortletTitleField keyValueHelpField = new FormPortletTitleField(
			"In the text area below, enter the \"key-display value\" pairs to appear in the select field. Keys are the underlying data values stored by the field. Display values are strings displayed by the field corresponding to each key. Keys must be unique, but display values need not be. Key-display value pairs should be entered in the following format: key1=dispval1, key2=dispval2, ... NOTE: Whitespace characters in keys are NOT trimmed, escape characters are supported.");

	public SelectFieldEditFormPortlet(AmiWebFormSelectFieldFactory factory, AmiWebQueryFormPortlet queryFormPortlet, int fieldX, int fieldY) {
		super(factory, queryFormPortlet, fieldX, fieldY);
		FormPortlet settingsForm = getSettingsForm();
		FormPortletTitleField titleField = settingsForm.addField(new FormPortletTitleField("Select Field Options"));
		this.service = AmiWebUtils.getService(getManager());
		this.datasources = settingsForm.addField(new FormPortletSelectField<String>(String.class, "Datamodel:"));
		this.datasources.addOption(null, "<custom>");
		this.datasourceValueColumn = (new FormPortletSelectField<String>(String.class, "Display Values:"));
		this.datasourceIdColumn = (new FormPortletSelectField<String>(String.class, "Keys:"));

		String layoutAlias = queryFormPortlet.getAmiLayoutFullAlias();
		for (AmiWebDm dm : service.getDmManager().getDmsSorted(layoutAlias)) {
			for (String tbName : dm.getResponseOutSchema().getTableNamesSorted()) {
				datasources.addOption(dm.getAmiLayoutFullAliasDotId() + "!" + tbName, dm.getAmiLayoutFullAliasDotId() + ":" + tbName);
			}
		}
		datasources.sortOptionsByName();
		this.displayValueSortOptionsField = settingsForm.addField(new FormPortletSelectField<String>(String.class, "Display Value Sort:"));
		this.displayValueSortOptionsField.addOption(AbstractDmQueryField.DISPLAY_VAL_SORT_NONE, "No Sort");
		this.displayValueSortOptionsField.addOption(AbstractDmQueryField.DISPLAY_VAL_SORT_ASC, "Ascending");
		this.displayValueSortOptionsField.addOption(AbstractDmQueryField.DISPLAY_VAL_SORT_DESC, "Descending");
		settingsForm.addField(this.keyValueHelpField);
		this.keyValueHelpField.setCssStyle("style.text-transform=none");
		this.keyValueHelpField.setHeightPx(50);
		this.valuesField = settingsForm.addField(new FormPortletTextAreaField("Key-Value Pairs:"));
		valuesField.setHeightPx(200);
		valuesField.setWidthPct(0.80);

		titleField.setLeftPosPx(BaseEditFieldPortlet.COL1_HORIZONTAL_POS_PX).setWidthPx(220);
		titleField.setTopPosPx(380).setHeightPx(25);
		this.datasources.setLeftPosPx(BaseEditFieldPortlet.COL1_HORIZONTAL_POS_PX).setWidthPx(220);
		this.datasources.setTopPosPx(420).setHeightPx(25);
		this.displayValueSortOptionsField.setLeftPosPx(BaseEditFieldPortlet.COL1_HORIZONTAL_POS_PX).setWidthPx(220);
		this.displayValueSortOptionsField.setTopPosPx(450).setHeightPx(25);
		this.keyValueHelpField.setLeftPosPx(BaseEditFieldPortlet.COL1_HORIZONTAL_POS_PX).setRightPosPx(30);
		this.keyValueHelpField.setTopPosPx(480).setHeightPx(25);
		this.valuesField.setLeftPosPx(BaseEditFieldPortlet.COL1_HORIZONTAL_POS_PX);
		this.valuesField.setTopPosPx(540);
	}
	@Override
	public void readFromField(SelectQueryField field) {
		displayValueSortOptionsField.setValue(field.getDisplaySortOption(false));
		if (field.getDmName() == null) {
			String values = AmiWebQueryFormPortletUtils.getValuesFieldFromMap(field.getCustomOptions());
			valuesField.setValue(values);
			datasources.setValue(null);
			refreshDatasourcesOption();
		} else {
			if (datasources.setValueNoThrow(field.getDmName() + "!" + field.getDmTableName())) {
				refreshDatasourcesOption();
				datasourceIdColumn.setValueNoThrow(field.getColumnId());
				datasourceValueColumn.setValueNoThrow(field.getValueId());
			}
		}
	}

	@Override
	public boolean submit() {
		if (!AmiWebQueryFormPortletUtils.getMapFromValuesField(new LinkedHashMap(), this.valuesField)) {
			getManager().showAlert("Error in key-value syntax");
			return false;
		}
		return super.submit();
	}

	@Override
	public boolean verifyForSubmit() {
		LinkedHashMap<String, String> values = new LinkedHashMap<String, String>();
		if (!AmiWebQueryFormPortletUtils.getMapFromValuesField(values, this.valuesField)) {
			getManager().showAlert("Error in key-value syntax");
			return false;
		}
		return super.verifyForSubmit();
	}
	@Override
	public void writeToField(SelectQueryField queryField) {
		queryField.setDisplaySortOption(this.displayValueSortOptionsField.getValue(), false);
		if (this.datasources.getValue() != null) {
			String dsDatamodel = datasources.getValue();
			String idColumn = datasourceIdColumn.getValue();
			String valueColumn = datasourceValueColumn.getValue();

			String dmName = dsDatamodel == null ? null : SH.beforeFirst(dsDatamodel, '!');
			String dmTableName = dsDatamodel == null ? null : SH.afterFirst(dsDatamodel, '!');

			queryField.setDmName(dmName);
			queryField.setDmTableName(dmTableName);
			queryField.setColumnId(idColumn);
			queryField.setValueId(valueColumn);
			if (dsDatamodel != null)
				queryField.bindToDatamodel();
		} else {
			LinkedHashMap<String, String> values = new LinkedHashMap<String, String>();
			if (!AmiWebQueryFormPortletUtils.getMapFromValuesField(values, this.valuesField))
				getManager().showAlert("Error in key-value syntax");
			else
				queryField.setCustomOptions(values);
			queryField.setDmName(null);
			queryField.setDmTableName(null);
		}
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		super.onFieldValueChanged(portlet, field, attributes);
		if (field == this.datasources) {
			refreshDatasourcesOption();
		}
	}
	private void refreshDatasourcesOption() {
		FormPortlet settingsForm = getSettingsForm();
		settingsForm.removeFieldNoThrow(this.datasourceIdColumn);
		settingsForm.removeFieldNoThrow(this.datasourceValueColumn);
		settingsForm.removeFieldNoThrow(this.valuesField);
		boolean noDatasource = this.datasources.getValue() == null;
		if (noDatasource) {
			settingsForm.addField(this.valuesField);
		} else {
			settingsForm.addField(this.datasourceIdColumn);
			settingsForm.addField(this.datasourceValueColumn);
			this.datasourceIdColumn.setLeftPosPx(BaseEditFieldPortlet.COL2_HORIZONTAL_POS_PX).setWidthPx(220);
			this.datasourceIdColumn.setTopPosPx(455).setHeightPx(25);
			this.datasourceValueColumn.setLeftPosPx(BaseEditFieldPortlet.COL2_HORIZONTAL_POS_PX).setWidthPx(220);
			this.datasourceValueColumn.setTopPosPx(487).setHeightPx(25);

			this.datasourceIdColumn.clearOptions();
			this.datasourceValueColumn.clearOptions();
			String value = this.datasources.getValue();
			AmiWebDmTableSchema schema = service.getDmManager().getDmByAliasDotName(SH.beforeFirst(value, '!')).getResponseOutSchema().getTable(SH.afterFirst(value, '!'));
			for (String i : CH.sort(schema.getColumnNames())) {
				this.datasourceIdColumn.addOption(i, i);
				this.datasourceValueColumn.addOption(i, i);
			}
		}
		this.keyValueHelpField.setVisible(noDatasource);
	}
}
