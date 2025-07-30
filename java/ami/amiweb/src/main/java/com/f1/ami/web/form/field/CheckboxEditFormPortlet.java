package com.f1.ami.web.form.field;

import java.util.Map;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.factory.AmiWebFormCheckBoxFieldFactory;
import com.f1.ami.web.form.queryfield.CheckboxQueryField;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletField;

public class CheckboxEditFormPortlet extends BaseEditFieldPortlet<CheckboxQueryField> {
	public CheckboxEditFormPortlet(AmiWebFormCheckBoxFieldFactory amiWebFormCheckBoxFactory, AmiWebQueryFormPortlet form, int fieldX, int fieldY) {
		super(amiWebFormCheckBoxFactory, form, fieldX, fieldY);
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		super.onFieldValueChanged(portlet, field, attributes);
		Byte horizSettingsToggleValue = getHorizSettingsToggle().getValue();
		if (field == getHorizSettingsToggle()
				&& (((Byte) FIELD_ALIGN_LEFT_TOP).equals(horizSettingsToggleValue) || ((Byte) FIELD_ALIGN_RIGHT_BOTTOM).equals(horizSettingsToggleValue)
						|| ((Byte) FIELD_ALIGN_CENTER).equals(horizSettingsToggleValue) || ((Byte) FIELD_ALIGN_ADVANCED).equals(horizSettingsToggleValue))) {
			getWidthPxField().setValue(FormPortletField.DEFAULT_HEIGHT);
			if (this.queryField != null) {
				this.queryField.setWidthPx(FormPortletField.DEFAULT_HEIGHT);
			}
		}
	}

	@Override
	public void readFromField(CheckboxQueryField field) {
	}

	@Override
	public void writeToField(CheckboxQueryField field) {
	}

}
