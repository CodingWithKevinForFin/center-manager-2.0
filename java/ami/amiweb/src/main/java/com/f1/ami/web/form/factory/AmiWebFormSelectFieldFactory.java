package com.f1.ami.web.form.factory;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.field.BaseEditFieldPortlet;
import com.f1.ami.web.form.field.SelectFieldEditFormPortlet;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.ami.web.form.queryfield.SelectQueryField;
import com.f1.ami.web.style.AmiWebStyleType;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_FormSelectField;

public class AmiWebFormSelectFieldFactory extends AmiWebFormFieldFactory<SelectQueryField> {

	@Override
	public String getType() {
		return QueryField.TYPE_ID_SELECT;
	}
	@Override
	public String getEditorTypeId() {
		return QueryField.TYPE_ID_SELECT;
	}
	@Override
	public String getUserLabel() {
		return QueryField.USER_LABEL_SELECT;
	}
	@Override
	public String getIcon() {
		return "field-select.svg";
	}

	@Override
	public SelectQueryField createQueryField(AmiWebQueryFormPortlet form) {
		return new SelectQueryField(this, form);
	}

	@Override
	public BaseEditFieldPortlet<SelectQueryField> createEditor(AmiWebQueryFormPortlet form, int x, int y) {
		return new SelectFieldEditFormPortlet(this, form, x, y);
	}
	@Override
	public Class<SelectQueryField> getClassType() {
		return SelectQueryField.class;
	}
	@Override
	public AmiWebStyleType getStyleType() {
		return AmiWebStyleTypeImpl_FormSelectField.INSTANCE;
	}
}
