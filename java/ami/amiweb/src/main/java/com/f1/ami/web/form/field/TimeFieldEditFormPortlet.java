package com.f1.ami.web.form.field;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.factory.AmiWebFormTimeFieldFactory;
import com.f1.ami.web.form.queryfield.TimeQueryField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;

public class TimeFieldEditFormPortlet extends BaseEditFieldPortlet<TimeQueryField> implements FormPortletListener {

	public TimeFieldEditFormPortlet(AmiWebFormTimeFieldFactory factory, AmiWebQueryFormPortlet queryFormPortlet, int fieldX, int fieldY) {
		super(factory, queryFormPortlet, fieldX, fieldY);
	}

	@Override
	public void readFromField(TimeQueryField field) {
	}

	@Override
	public void writeToField(TimeQueryField queryField) {
	}

}
