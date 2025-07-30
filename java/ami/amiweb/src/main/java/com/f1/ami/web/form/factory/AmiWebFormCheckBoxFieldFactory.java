package com.f1.ami.web.form.factory;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.field.BaseEditFieldPortlet;
import com.f1.ami.web.form.field.CheckboxEditFormPortlet;
import com.f1.ami.web.form.queryfield.CheckboxQueryField;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.ami.web.style.AmiWebStyleType;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_FormCheckboxField;

public class AmiWebFormCheckBoxFieldFactory extends AmiWebFormFieldFactory<CheckboxQueryField> {

	@Override
	public String getType() {
		return QueryField.TYPE_ID_CHECKBOX;
	}
	@Override
	public String getEditorTypeId() {
		return QueryField.TYPE_ID_CHECKBOX;
	}
	@Override
	public String getUserLabel() {
		return QueryField.USER_LABEL_CHECKBOX;
	}

	@Override
	public CheckboxQueryField createQueryField(AmiWebQueryFormPortlet form) {
		return new CheckboxQueryField(this, form);
	}
	@Override
	public String getIcon() {
		return "field-checkbox.svg";
	}
	@Override
	public BaseEditFieldPortlet<CheckboxQueryField> createEditor(AmiWebQueryFormPortlet form, int x, int y) {
		return new CheckboxEditFormPortlet(this, form, x, y);
	}
	@Override
	public Class<CheckboxQueryField> getClassType() {
		return CheckboxQueryField.class;
	}

	@Override
	public int getDefaultWidth() {
		return 20;
	}
	@Override
	public int getDefaultHeight() {
		return 20;
	}
	@Override
	public AmiWebStyleType getStyleType() {
		return AmiWebStyleTypeImpl_FormCheckboxField.INSTANCE;
	}

}
