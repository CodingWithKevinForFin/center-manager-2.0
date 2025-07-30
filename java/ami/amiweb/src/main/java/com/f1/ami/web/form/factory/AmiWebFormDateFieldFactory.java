package com.f1.ami.web.form.factory;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.field.BaseEditFieldPortlet;
import com.f1.ami.web.form.field.DateFieldEditFormPortlet;
import com.f1.ami.web.form.queryfield.DateQueryField;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.ami.web.style.AmiWebStyleType;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_FormDateField;

public class AmiWebFormDateFieldFactory extends AmiWebFormFieldFactory<DateQueryField> {

	@Override
	public String getType() {
		return QueryField.TYPE_ID_DATE;
	}
	@Override
	public String getEditorTypeId() {
		return QueryField.TYPE_ID_DATE;
	}
	@Override
	public String getUserLabel() {
		return QueryField.USER_LABEL_DATE;
	}
	@Override
	public String getIcon() {
		return "field-date.svg";
	}
	@Override
	public AmiWebStyleType getStyleType() {
		return AmiWebStyleTypeImpl_FormDateField.INSTANCE;
	}

	@Override
	public DateQueryField createQueryField(AmiWebQueryFormPortlet form) {
		return new DateQueryField(this, form);
	}
	@Override
	public BaseEditFieldPortlet<DateQueryField> createEditor(AmiWebQueryFormPortlet form, int x, int y) {
		return new DateFieldEditFormPortlet(this, form, x, y);
	}
	@Override
	public Class<DateQueryField> getClassType() {
		return DateQueryField.class;
	}

}
