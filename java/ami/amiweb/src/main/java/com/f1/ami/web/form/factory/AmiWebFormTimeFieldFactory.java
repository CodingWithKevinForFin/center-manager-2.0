package com.f1.ami.web.form.factory;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.field.BaseEditFieldPortlet;
import com.f1.ami.web.form.field.TimeFieldEditFormPortlet;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.ami.web.form.queryfield.TimeQueryField;
import com.f1.ami.web.style.AmiWebStyleType;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_FormTimeField;

public class AmiWebFormTimeFieldFactory extends AmiWebFormFieldFactory<TimeQueryField> {

	@Override
	public String getType() {
		return QueryField.TYPE_ID_TIME;
	}
	@Override
	public String getEditorTypeId() {
		return QueryField.TYPE_ID_TIME;
	}
	@Override
	public String getUserLabel() {
		return QueryField.USER_LABEL_TIME;
	}
	@Override
	public String getIcon() {
		return "field-time.svg";
	}
	@Override
	public AmiWebStyleType getStyleType() {
		return AmiWebStyleTypeImpl_FormTimeField.INSTANCE;
	}

	@Override
	public TimeQueryField createQueryField(AmiWebQueryFormPortlet form) {
		return new TimeQueryField(this, form);
	}

	@Override
	public BaseEditFieldPortlet<TimeQueryField> createEditor(AmiWebQueryFormPortlet form, int x, int y) {
		return new TimeFieldEditFormPortlet(this, form, x, y);
	}
	@Override
	public Class<TimeQueryField> getClassType() {
		return TimeQueryField.class;
	}
}
