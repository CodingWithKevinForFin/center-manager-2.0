package com.f1.ami.web.form.factory;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.field.BaseEditFieldPortlet;
import com.f1.ami.web.form.field.TextAreaFieldEditFormPortlet;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.ami.web.form.queryfield.TextAreaQueryField;
import com.f1.ami.web.style.AmiWebStyleType;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_FormTextareaField;

public class AmiWebFormTextAreaFieldFactory extends AmiWebFormFieldFactory<TextAreaQueryField> {

	@Override
	public String getType() {
		return QueryField.TYPE_ID_TEXT_AREA;
	}
	@Override
	public String getEditorTypeId() {
		return QueryField.TYPE_ID_TEXT_AREA;
	}
	@Override
	public String getUserLabel() {
		return QueryField.USER_LABEL_TEXT_AREA;
	}
	@Override
	public String getIcon() {
		return "field-textarea.svg";
	}

	@Override
	public TextAreaQueryField createQueryField(AmiWebQueryFormPortlet form) {
		return new TextAreaQueryField(this, form);
	}

	@Override
	public BaseEditFieldPortlet<TextAreaQueryField> createEditor(AmiWebQueryFormPortlet form, int x, int y) {
		return new TextAreaFieldEditFormPortlet(this, form, x, y);
	}
	@Override
	public Class<TextAreaQueryField> getClassType() {
		return TextAreaQueryField.class;
	}
	public int getDefaultHeight() {
		return 200;
	}
	@Override
	public AmiWebStyleType getStyleType() {
		return AmiWebStyleTypeImpl_FormTextareaField.INSTANCE;
	}
}
