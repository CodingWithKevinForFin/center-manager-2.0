package com.f1.ami.web.form.queryfield;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.factory.AmiWebFormTextAreaFieldFactory;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;

public class TextAreaQueryField extends QueryField<FormPortletTextAreaField> {

	public TextAreaQueryField(AmiWebFormTextAreaFieldFactory factory, AmiWebQueryFormPortlet form) {
		super(factory, form, new FormPortletTextAreaField("").setValue(""));
	}

}