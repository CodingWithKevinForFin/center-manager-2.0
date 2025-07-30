package com.f1.ami.web.form.factory;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.field.BaseEditFieldPortlet;
import com.f1.ami.web.form.field.PasswordFieldEditFormPortlet;
import com.f1.ami.web.form.queryfield.PasswordQueryField;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.ami.web.style.AmiWebStyleType;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_FormPasswordField;

public class AmiWebFormPasswordFieldFactory extends AmiWebFormFieldFactory<PasswordQueryField> {

	@Override
	public String getType() {
		return QueryField.TYPE_ID_PASSWORD;
	}

	@Override
	public String getEditorTypeId() {
		return QueryField.TYPE_ID_PASSWORD;
	}

	@Override
	public String getUserLabel() {
		return QueryField.USER_LABEL_PASSWORD;
	}

	@Override
	public String getIcon() {
		return "field-password.svg";
	}

	@Override
	public PasswordQueryField createQueryField(AmiWebQueryFormPortlet form) {
		return new PasswordQueryField(this, form);
	}

	@Override
	public BaseEditFieldPortlet<PasswordQueryField> createEditor(AmiWebQueryFormPortlet form, int x, int y) {
		return new PasswordFieldEditFormPortlet(this, form, x, y);
	}

	@Override
	public Class<PasswordQueryField> getClassType() {
		return PasswordQueryField.class;
	}

	@Override
	public AmiWebStyleType getStyleType() {
		return AmiWebStyleTypeImpl_FormPasswordField.INSTANCE;
	}
}
