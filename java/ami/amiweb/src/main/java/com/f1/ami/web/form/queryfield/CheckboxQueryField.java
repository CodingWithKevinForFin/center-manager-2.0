package com.f1.ami.web.form.queryfield;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.factory.AmiWebFormCheckBoxFieldFactory;
import com.f1.suite.web.portal.impl.form.FormPortletCheckboxField;

public class CheckboxQueryField extends QueryField<FormPortletCheckboxField> {

	public CheckboxQueryField(AmiWebFormCheckBoxFieldFactory factory, AmiWebQueryFormPortlet form) {
		super(factory, form, new FormPortletCheckboxField(""));
		getField().setValueNoFire(Boolean.FALSE);
		getField().setDefaultValue(Boolean.FALSE);
	}
}