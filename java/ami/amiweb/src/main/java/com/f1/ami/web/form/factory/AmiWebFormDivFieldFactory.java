package com.f1.ami.web.form.factory;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.field.BaseEditFieldPortlet;
import com.f1.ami.web.form.field.DivFieldEditFormPortlet;
import com.f1.ami.web.form.queryfield.DivQueryField;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.ami.web.style.AmiWebStyleType;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_FormDivField;

public class AmiWebFormDivFieldFactory extends AmiWebFormFieldFactory<DivQueryField> {

	@Override
	public String getType() {
		return QueryField.TYPE_ID_DIV;
	}
	@Override
	public String getEditorTypeId() {
		return QueryField.TYPE_ID_DIV;
	}
	@Override
	public String getUserLabel() {
		return QueryField.USER_LABEL_DIV;
	}
	@Override
	public String getIcon() {
		return "field-div.svg";
	}

	@Override
	public DivQueryField createQueryField(AmiWebQueryFormPortlet form) {
		return new DivQueryField(this, form);
	}
	@Override
	public BaseEditFieldPortlet<DivQueryField> createEditor(AmiWebQueryFormPortlet form, int x, int y) {
		return new DivFieldEditFormPortlet(this, form, x, y);
	}

	@Override
	public Class<DivQueryField> getClassType() {
		return DivQueryField.class;
	}
	@Override
	public int getDefaultWidth() {
		return 200;
	}
	@Override
	public int getDefaultHeight() {
		return 200;
	}

	@Override
	public AmiWebStyleType getStyleType() {
		return AmiWebStyleTypeImpl_FormDivField.INSTANCE;
	}
}
