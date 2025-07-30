package com.f1.ami.web.form.factory;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.field.BaseEditFieldPortlet;
import com.f1.ami.web.form.field.ColorPickerEditFormPortlet;
import com.f1.ami.web.form.queryfield.ColorPickerQueryField;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.ami.web.style.AmiWebStyleType;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_FormColorPickerField;

public class AmiWebFormColorPickerFieldFactory extends AmiWebFormFieldFactory<ColorPickerQueryField> {

	@Override
	public String getType() {
		return QueryField.TYPE_ID_COLOR_PICKER;
	}

	@Override
	public String getEditorTypeId() {
		return QueryField.TYPE_ID_COLOR_PICKER;
	}

	@Override
	public String getUserLabel() {
		return QueryField.USER_LABEL_COLOR_PICKER;
	}

	@Override
	public String getIcon() {
		return "field-colorpicker.svg";
	}

	@Override
	public ColorPickerQueryField createQueryField(AmiWebQueryFormPortlet form) {
		return new ColorPickerQueryField(this, form);
	}

	@Override
	public BaseEditFieldPortlet<ColorPickerQueryField> createEditor(AmiWebQueryFormPortlet form, int x, int y) {
		return new ColorPickerEditFormPortlet(this, form, x, y);
	}

	@Override
	public Class<ColorPickerQueryField> getClassType() {
		return ColorPickerQueryField.class;
	}

	@Override
	public AmiWebStyleType getStyleType() {
		return AmiWebStyleTypeImpl_FormColorPickerField.INSTANCE;
	}
}
