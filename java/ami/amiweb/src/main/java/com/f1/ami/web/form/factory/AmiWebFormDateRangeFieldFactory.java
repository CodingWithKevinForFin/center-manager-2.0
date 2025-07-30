package com.f1.ami.web.form.factory;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.field.BaseEditFieldPortlet;
import com.f1.ami.web.form.field.DateRangeFieldEditFormPortlet;
import com.f1.ami.web.form.queryfield.DateRangeQueryField;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.ami.web.style.AmiWebStyleType;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_FormDateRangeField;

public class AmiWebFormDateRangeFieldFactory extends AmiWebFormFieldFactory<DateRangeQueryField> {

	@Override
	public String getType() {
		return QueryField.TYPE_ID_DATERANGE;
	}
	@Override
	public String getEditorTypeId() {
		return QueryField.TYPE_ID_DATERANGE;
	}
	@Override
	public String getUserLabel() {
		return QueryField.USER_LABEL_DATERANGE;
	}
	@Override
	public String getIcon() {
		return "field-date.svg";
	}
	@Override
	public AmiWebStyleType getStyleType() {
		return AmiWebStyleTypeImpl_FormDateRangeField.INSTANCE;
	}

	@Override
	public DateRangeQueryField createQueryField(AmiWebQueryFormPortlet form) {
		return new DateRangeQueryField(this, form);
	}
	@Override
	public BaseEditFieldPortlet<DateRangeQueryField> createEditor(AmiWebQueryFormPortlet form, int x, int y) {
		return new DateRangeFieldEditFormPortlet(this, form, x, y);
	}
	@Override
	public Class<DateRangeQueryField> getClassType() {
		return DateRangeQueryField.class;
	}

}
