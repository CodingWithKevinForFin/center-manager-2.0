package com.f1.ami.web.form.factory;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.field.BaseEditFieldPortlet;
import com.f1.ami.web.form.field.DateTimeFieldEditFormPortlet;
import com.f1.ami.web.form.queryfield.DateTimeQueryField;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.ami.web.style.AmiWebStyleType;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_FormDateTimeField;

public class AmiWebFormDateTimeFieldFactory extends AmiWebFormFieldFactory<DateTimeQueryField> {

	@Override
	public String getType() {
		return QueryField.TYPE_ID_DATETIME;
	}
	@Override
	public String getEditorTypeId() {
		return QueryField.TYPE_ID_DATETIME;
	}
	@Override
	public String getUserLabel() {
		return QueryField.USER_LABEL_DATETIME;
	}
	@Override
	public String getIcon() {
		return "field-datetime.svg";
	}
	@Override
	public AmiWebStyleType getStyleType() {
		return AmiWebStyleTypeImpl_FormDateTimeField.INSTANCE;
	}

	@Override
	public DateTimeQueryField createQueryField(AmiWebQueryFormPortlet form) {
		return new DateTimeQueryField(this, form);
	}
	@Override
	public BaseEditFieldPortlet<DateTimeQueryField> createEditor(AmiWebQueryFormPortlet form, int x, int y) {
		return new DateTimeFieldEditFormPortlet(this, form, x, y);
	}
	@Override
	public Class<DateTimeQueryField> getClassType() {
		return DateTimeQueryField.class;
	}

}
