package com.f1.ami.web.form.field;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.factory.AmiWebFormFieldFactory;
import com.f1.ami.web.form.queryfield.PasswordQueryField;

public class PasswordFieldEditFormPortlet extends BaseEditFieldPortlet<PasswordQueryField> {

	public PasswordFieldEditFormPortlet(AmiWebFormFieldFactory<PasswordQueryField> factory, AmiWebQueryFormPortlet queryFormPortlet, int fieldX, int fieldY) {
		super(factory, queryFormPortlet, fieldX, fieldY);
	}

	@Override
	public void readFromField(PasswordQueryField queryField) {
	}

	@Override
	public void writeToField(PasswordQueryField queryField) {
	}

}
