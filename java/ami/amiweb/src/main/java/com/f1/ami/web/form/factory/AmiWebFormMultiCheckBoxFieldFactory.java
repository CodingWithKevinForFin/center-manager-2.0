package com.f1.ami.web.form.factory;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.field.BaseEditFieldPortlet;
import com.f1.ami.web.form.field.MultiCheckboxFieldEditFormPortlet;
import com.f1.ami.web.form.queryfield.MultiCheckboxQueryField;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.ami.web.style.AmiWebStyleType;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_FormMultiCheckboxField;

public class AmiWebFormMultiCheckBoxFieldFactory extends AmiWebFormFieldFactory<MultiCheckboxQueryField> {

	@Override
	public String getType() {
		return QueryField.TYPE_ID_MULTI_CHECKBOX;
	}

	@Override
	public String getEditorTypeId() {
		return QueryField.TYPE_ID_MULTI_CHECKBOX;
	}

	@Override
	public String getUserLabel() {
		return QueryField.USER_LABEL_MULTI_CHECKBOX;
	}

	@Override
	public String getIcon() {
		return "field-multicheckbox.svg";
	}

	@Override
	public MultiCheckboxQueryField createQueryField(AmiWebQueryFormPortlet form) {
		return new MultiCheckboxQueryField(this, form);
	}

	@Override
	public BaseEditFieldPortlet<MultiCheckboxQueryField> createEditor(AmiWebQueryFormPortlet form, int x, int y) {
		return new MultiCheckboxFieldEditFormPortlet(this, form, x, y);
	}

	@Override
	public Class<MultiCheckboxQueryField> getClassType() {
		return MultiCheckboxQueryField.class;
	}

	@Override
	public AmiWebStyleType getStyleType() {
		return AmiWebStyleTypeImpl_FormMultiCheckboxField.INSTANCE;
	}
}
