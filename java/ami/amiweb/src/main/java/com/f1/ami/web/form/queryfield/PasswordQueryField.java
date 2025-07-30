package com.f1.ami.web.form.queryfield;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.factory.AmiWebFormFieldFactory;
import com.f1.base.Password;
import com.f1.suite.web.portal.impl.form.FormPortletPasswordField;

public class PasswordQueryField extends QueryField<FormPortletPasswordField> {

	public PasswordQueryField(AmiWebFormFieldFactory<?> factory, AmiWebQueryFormPortlet form) {
		super(factory, form, new FormPortletPasswordField(""));
		getField().setDefaultValue(new Password(""));
	}

}
