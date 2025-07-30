package com.f1.ami.web.form.field;

import java.util.Map;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.factory.AmiWebFormFieldFactory;
import com.f1.ami.web.form.queryfield.ColorPickerQueryField;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletCheckboxField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletToggleButtonsField;

public class ColorPickerEditFormPortlet extends BaseEditFieldPortlet<ColorPickerQueryField> {

	final private FormPortletCheckboxField alphaEnabled;

	public ColorPickerEditFormPortlet(AmiWebFormFieldFactory<ColorPickerQueryField> factory, AmiWebQueryFormPortlet queryFormPortlet, int fieldX, int fieldY) {
		super(factory, queryFormPortlet, fieldX, fieldY);
		this.alphaEnabled = getSettingsForm().addField(new FormPortletCheckboxField("Enable Opacity (Alpha): ", false));
		alphaEnabled.setLeftTopWidthHeightPx(BaseEditFieldPortlet.COL1_HORIZONTAL_POS_PX, 300, 220, 16);
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (getHorizSettingsToggle() == field) {
			FormPortletToggleButtonsField<Byte> tbf = (FormPortletToggleButtonsField<Byte>) field;
			if (tbf.getValue() == BaseEditFieldPortlet.FIELD_ALIGN_ADVANCED)
				alphaEnabled.setLeftTopWidthHeightPx(BaseEditFieldPortlet.COL1_HORIZONTAL_POS_PX, 400, 220, 16);
			else
				alphaEnabled.setLeftTopWidthHeightPx(BaseEditFieldPortlet.COL1_HORIZONTAL_POS_PX, 300, 220, 16);
		}
		super.onFieldValueChanged(portlet, field, attributes);
		if (this.queryField != null) {
			if (field == this.alphaEnabled) {
				this.queryField.setAlaphaEnabled(this.alphaEnabled.getBooleanValue(), false);
			}
		}
	}

	@Override
	public void readFromField(ColorPickerQueryField queryField) {
		alphaEnabled.setValue(queryField.getAlaphaEnabled(false));
	}

	@Override
	public void writeToField(ColorPickerQueryField queryField) {
		queryField.setAlaphaEnabled(alphaEnabled.getBooleanValue(), false);
	}

}
