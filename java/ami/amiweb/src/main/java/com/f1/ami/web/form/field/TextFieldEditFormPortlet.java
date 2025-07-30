package com.f1.ami.web.form.field;

import java.util.LinkedHashMap;
import java.util.Map;

import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.AmiWebQueryFormPortletUtils;
import com.f1.ami.web.form.factory.AmiWebFormTextFieldFactory;
import com.f1.ami.web.form.queryfield.AbstractDmQueryField;
import com.f1.ami.web.form.queryfield.TextQueryField;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletCheckboxField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.utils.CH;
import com.f1.utils.SH;

public class TextFieldEditFormPortlet extends BaseEditFieldPortlet<TextQueryField> {
	private FormPortletTextAreaField valuesField;
	private FormPortletSelectField<String> datasources;
	private FormPortletSelectField<String> datasourceValueColumn;
	private FormPortletSelectField<String> displayValueSortOptionsField;
	private final FormPortletTitleField keyValueHelpField = new FormPortletTitleField(
			"In the text area below, enter the \"values\" list to appear in the text field. Values should be entered in the following format: dispval1,dispval2, ... NOTE: Whitespace characters in values are NOT trimmed, escape characters are supported.");
	private final AmiWebService service;
	private FormPortletCheckboxField showOptionsImmediatelyField;
	private FormPortletCheckboxField performSubstringMatchingField;
	private FormPortletTextField autocompleteDelimField;

	public TextFieldEditFormPortlet(AmiWebFormTextFieldFactory factory, AmiWebQueryFormPortlet queryFormPortlet, int fieldX, int fieldY) {
		super(factory, queryFormPortlet, fieldX, fieldY);
		this.service = AmiWebUtils.getService(getManager());
		FormPortlet settingsForm = getSettingsForm();
		FormPortletTitleField titleField = settingsForm.addField(new FormPortletTitleField("Text Field Options"));
		this.datasources = settingsForm.addField(new FormPortletSelectField<String>(String.class, "Datamodel:"));
		this.datasources.addOption(null, "<custom>");
		this.datasourceValueColumn = settingsForm.addField(new FormPortletSelectField<String>(String.class, "Display Values:"));

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
		titleField.setLeftPosPx(BaseEditFieldPortlet.COL1_HORIZONTAL_POS_PX).setWidthPx(220);
		titleField.setTopPosPx(380).setHeightPx(25);
		this.datasources.setLeftPosPx(BaseEditFieldPortlet.COL1_HORIZONTAL_POS_PX).setWidthPx(220);
		this.datasources.setTopPosPx(400).setHeightPx(25);
		this.datasourceValueColumn.setLeftPosPx(BaseEditFieldPortlet.COL1_HORIZONTAL_POS_PX).setWidthPx(220);
		this.datasourceValueColumn.setTopPosPx(430).setHeightPx(25);
		this.displayValueSortOptionsField.setLeftPosPx(BaseEditFieldPortlet.COL1_HORIZONTAL_POS_PX).setWidthPx(220);
		this.displayValueSortOptionsField.setTopPosPx(460).setHeightPx(25);
		this.showOptionsImmediatelyField = settingsForm.addField(new FormPortletCheckboxField("Show Options Immediately"));
		this.showOptionsImmediatelyField.setLabelSide(FormPortletField.LABEL_SIDE_TOP);
		this.showOptionsImmediatelyField.setLabelCssStyle("style.display=inline|style.whiteSpace=nowrap");
		this.showOptionsImmediatelyField.setLeftPosPx(BaseEditFieldPortlet.COL1_HORIZONTAL_POS_PX);
		this.showOptionsImmediatelyField.setTopPosPx(520);
		this.showOptionsImmediatelyField.setValueNoFire(false);
		this.performSubstringMatchingField = settingsForm.addField(new FormPortletCheckboxField("Perform Substring Matching"));
		this.performSubstringMatchingField.setLabelSide(FormPortletField.LABEL_SIDE_TOP);
		this.performSubstringMatchingField.setLabelCssStyle("style.display=inline|style.whiteSpace=nowrap");
		this.performSubstringMatchingField.setLeftPosPx(BaseEditFieldPortlet.COL1_HORIZONTAL_POS_PX);
		this.performSubstringMatchingField.setTopPosPx(580);
		this.performSubstringMatchingField.setValueNoFire(false);
		this.autocompleteDelimField = settingsForm.addField(new FormPortletTextField("Autocomplete Delimiter:"));
		this.autocompleteDelimField.setLeftTopWidthHeightPx(BaseEditFieldPortlet.COL1_HORIZONTAL_POS_PX, this.performSubstringMatchingField.getTopPosPx() + 30, 60, 20);
		this.autocompleteDelimField.setHelp("autocomplete menu resets after each occurence of the delimiter.");
		this.autocompleteDelimField.setCssStyle("style.textAlign=center|style.fontWeight=bold");

		this.valuesField = settingsForm.addField(new FormPortletTextAreaField("Values List:"));
		this.valuesField.setHeightPx(200);
		this.valuesField.setWidthPct(0.80);

		this.keyValueHelpField.setLeftPosPx(BaseEditFieldPortlet.COL1_HORIZONTAL_POS_PX).setRightPosPx(30);
		this.keyValueHelpField.setTopPosPx(640).setHeightPx(25);
		this.valuesField.setLeftPosPx(BaseEditFieldPortlet.COL1_HORIZONTAL_POS_PX);
		this.valuesField.setTopPosPx(680);

		refreshDatasourcesOption();
	}

	@Override
	public void readFromField(TextQueryField field) {
		autocompleteDelimField.setValue(field.getAutocompleteDelimeter(false));
		showOptionsImmediatelyField.setValue(field.isShowOptionsImmediately(false));
		displayValueSortOptionsField.setValue(field.getDisplaySortOption(false));
		performSubstringMatchingField.setValue(field.getPerformSubstringMatching(false));
		if (field.getDmName() == null) {
			String values = AmiWebQueryFormPortletUtils.getValuesFieldFromMap(field.getCustomOptions());
			valuesField.setValue(values);
			datasources.setValue(null);
			refreshDatasourcesOption();

		} else {
			String key = field.getDmName() + "!" + field.getDmTableName();
			if (datasources.setValueNoThrow(key)) {
				refreshDatasourcesOption();
				datasourceValueColumn.setValueNoThrow(field.getValueId());
			}
		}

	}

	@Override
	public void writeToField(TextQueryField queryField) {
		queryField.setAutocompleteDelimiter(autocompleteDelimField.getValue(), false);
		queryField.setShowOptionsImmediately(showOptionsImmediatelyField.getValue(), false);
		queryField.setDisplaySortOption(displayValueSortOptionsField.getValue(), false);
		queryField.setPerformSubstringMatching(performSubstringMatchingField.getValue(), false);
		if (this.datasources.getValue() != null) {
			String dsDatamodel = datasources.getValue();
			String valueColumn = datasourceValueColumn.getValue();

			String dmName = dsDatamodel == null ? null : SH.beforeFirst(dsDatamodel, '!');
			String dmTableName = dsDatamodel == null ? null : SH.afterFirst(dsDatamodel, '!');

			queryField.setDmName(dmName);
			queryField.setDmTableName(dmTableName);
			queryField.setColumnId(null);
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
		this.datasourceValueColumn.clearOptions();

		boolean noDatasource = this.datasources.getValue() == null;
		if (noDatasource) {
		} else {
			this.datasourceValueColumn.clearOptions();

			String value = this.datasources.getValue();
			AmiWebDmTableSchema schema = service.getDmManager().getDmByAliasDotName(SH.beforeFirst(value, '!')).getResponseOutSchema().getTable(SH.afterFirst(value, '!'));
			for (String i : CH.sort(schema.getColumnNames())) {
				this.datasourceValueColumn.addOption(i, i);
			}
		}

		this.datasourceValueColumn.setVisible(!noDatasource);
		this.keyValueHelpField.setVisible(noDatasource);
		this.valuesField.setVisible(noDatasource);
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
		if (!AmiWebQueryFormPortletUtils.getMapFromValuesField(new LinkedHashMap(), this.valuesField)) {
			getManager().showAlert("Error in syntax");
			return false;
		}
		if (SH.is(this.autocompleteDelimField.getValue()) && this.autocompleteDelimField.getValue().length() > TextQueryField.AUTOCOMPLETE_DELIM_MAXLEN) {
			getManager().showAlert("Autocomplete Delimiter cannot have more than 5 characters");
			return false;
		}
		return super.verifyForSubmit();
	}

}
