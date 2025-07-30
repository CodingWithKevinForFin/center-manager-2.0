package com.f1.ami.web.form.field;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.factory.AmiWebFormTimeRangeFieldFactory;
import com.f1.ami.web.form.queryfield.TimeRangeQueryField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;

public class TimeRangeFieldEditFormPortlet extends BaseEditFieldPortlet<TimeRangeQueryField> implements FormPortletListener {

	public TimeRangeFieldEditFormPortlet(AmiWebFormTimeRangeFieldFactory factory, AmiWebQueryFormPortlet queryFormPortlet, int fieldX, int fieldY) {
		super(factory, queryFormPortlet, fieldX, fieldY);
	}

	@Override
	public void readFromField(TimeRangeQueryField field) {
	}

	@Override
	public void writeToField(TimeRangeQueryField queryField) {
	}

}
