package com.f1.ami.web.form.factory;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.field.BaseEditFieldPortlet;
import com.f1.ami.web.form.field.TimeRangeFieldEditFormPortlet;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.ami.web.form.queryfield.TimeRangeQueryField;
import com.f1.ami.web.style.AmiWebStyleType;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_FormTimeRangeField;

public class AmiWebFormTimeRangeFieldFactory extends AmiWebFormFieldFactory<TimeRangeQueryField> {

	@Override
	public String getType() {
		return QueryField.TYPE_ID_TIMERANGE;
	}
	@Override
	public String getEditorTypeId() {
		return QueryField.TYPE_ID_TIMERANGE;
	}
	@Override
	public String getUserLabel() {
		return QueryField.USER_LABEL_TIMERANGE;
	}
	@Override
	public String getIcon() {
		return "field-time.svg";
	}

	@Override
	public AmiWebStyleType getStyleType() {
		return AmiWebStyleTypeImpl_FormTimeRangeField.INSTANCE;
	}

	@Override
	public TimeRangeQueryField createQueryField(AmiWebQueryFormPortlet form) {
		return new TimeRangeQueryField(this, form);
	}

	@Override
	public BaseEditFieldPortlet<TimeRangeQueryField> createEditor(AmiWebQueryFormPortlet form, int x, int y) {
		return new TimeRangeFieldEditFormPortlet(this, form, x, y);
	}
	@Override
	public Class<TimeRangeQueryField> getClassType() {
		return TimeRangeQueryField.class;
	}
}
