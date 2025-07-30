package com.f1.ami.web.form.factory;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.field.BaseEditFieldPortlet;
import com.f1.ami.web.form.field.FormButtonEditFormPortlet;
import com.f1.ami.web.form.queryfield.FormButtonQueryField;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.ami.web.style.AmiWebStyleType;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_FormButtonField;

public class AmiWebFormButtonFieldFactory extends AmiWebFormFieldFactory<FormButtonQueryField> {

	@Override
	public String getType() {
		return QueryField.TYPE_ID_BUTTON;
	}

	@Override
	public FormButtonQueryField createQueryField(AmiWebQueryFormPortlet form) {
		return new FormButtonQueryField(this, form);
	}
	@Override
	public String getEditorTypeId() {
		return QueryField.TYPE_ID_BUTTON;
	}
	@Override
	public String getUserLabel() {
		return QueryField.USER_LABEL_BUTTON;
	}

	@Override
	public String getIcon() {
		return "field-button.svg";
	}

	@Override
	public BaseEditFieldPortlet<FormButtonQueryField> createEditor(AmiWebQueryFormPortlet portlet, int fieldX, int fieldY) {
		return new FormButtonEditFormPortlet(this, portlet, fieldX, fieldY);
	}

	@Override
	public Class<FormButtonQueryField> getClassType() {
		return FormButtonQueryField.class;
	}

	@Override
	public AmiWebStyleType getStyleType() {
		return AmiWebStyleTypeImpl_FormButtonField.INSTANCE;
	}
}
