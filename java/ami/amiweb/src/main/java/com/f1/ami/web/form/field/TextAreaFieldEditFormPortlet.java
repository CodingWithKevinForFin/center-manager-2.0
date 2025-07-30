package com.f1.ami.web.form.field;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.factory.AmiWebFormTextAreaFieldFactory;
import com.f1.ami.web.form.queryfield.TextAreaQueryField;

public class TextAreaFieldEditFormPortlet extends BaseEditFieldPortlet<TextAreaQueryField> {

	public TextAreaFieldEditFormPortlet(AmiWebFormTextAreaFieldFactory factory, AmiWebQueryFormPortlet queryFormPortlet, int fieldX, int fieldY) {
		super(factory, queryFormPortlet, fieldX, fieldY);
	}

	@Override
	public void readFromField(TextAreaQueryField field) {
	}

	@Override
	public void writeToField(TextAreaQueryField queryField) {
	}

}
