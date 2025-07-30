package com.f1.ami.web.form.factory;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.field.BaseEditFieldPortlet;
import com.f1.ami.web.form.field.TextFieldEditFormPortlet;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.ami.web.form.queryfield.TextQueryField;
import com.f1.ami.web.style.AmiWebStyleType;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_FormTextField;

public class AmiWebFormTextFieldFactory extends AmiWebFormFieldFactory<TextQueryField> {

	@Override
	public String getType() {
		return QueryField.TYPE_ID_TEXT;
	}
	@Override
	public String getEditorTypeId() {
		return QueryField.TYPE_ID_TEXT;
	}
	@Override
	public String getUserLabel() {
		return QueryField.USER_LABEL_TEXT;
	}
	@Override
	public String getIcon() {
		return "field-text.svg";
	}

	@Override
	public TextQueryField createQueryField(AmiWebQueryFormPortlet form) {
		return new TextQueryField(this, form);
	}

	@Override
	public BaseEditFieldPortlet<TextQueryField> createEditor(AmiWebQueryFormPortlet form, int x, int y) {
		return new TextFieldEditFormPortlet(this, form, x, y);
	}
	@Override
	public Class<TextQueryField> getClassType() {
		return TextQueryField.class;
	}
	@Override
	public AmiWebStyleType getStyleType() {
		return AmiWebStyleTypeImpl_FormTextField.INSTANCE;
	}
}
