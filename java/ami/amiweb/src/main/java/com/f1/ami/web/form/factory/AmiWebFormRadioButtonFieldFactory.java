package com.f1.ami.web.form.factory;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.field.BaseEditFieldPortlet;
import com.f1.ami.web.form.field.RadioButtonFieldEditFormPortlet;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.ami.web.form.queryfield.RadioButtonQueryField;
import com.f1.ami.web.style.AmiWebStyleType;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_FormRadioField;

public class AmiWebFormRadioButtonFieldFactory extends AmiWebFormFieldFactory<RadioButtonQueryField> {

	@Override
	public String getType() {
		return QueryField.TYPE_ID_RADIO;
	}

	@Override
	public String getEditorTypeId() {
		return QueryField.TYPE_ID_RADIO;
	}

	@Override
	public String getUserLabel() {
		return QueryField.USER_LABEL_RADIO;
	}

	@Override
	public String getIcon() {
		return "field-radio.svg";
	}

	@Override
	public RadioButtonQueryField createQueryField(AmiWebQueryFormPortlet form) {
		return new RadioButtonQueryField(this, form);
	}

	@Override
	public BaseEditFieldPortlet<RadioButtonQueryField> createEditor(AmiWebQueryFormPortlet form, int x, int y) {
		return new RadioButtonFieldEditFormPortlet(this, form, x, y);
	}

	@Override
	public Class<RadioButtonQueryField> getClassType() {
		return RadioButtonQueryField.class;
	}

	@Override
	public AmiWebStyleType getStyleType() {
		return AmiWebStyleTypeImpl_FormRadioField.INSTANCE;
	}
}
