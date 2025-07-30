package com.f1.ami.web.form.factory;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.field.BaseEditFieldPortlet;
import com.f1.ami.web.form.field.ColorGradientPickerEditFormPortlet;
import com.f1.ami.web.form.queryfield.ColorGradientPickerQueryField;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.ami.web.style.AmiWebStyleType;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_FormGradientColorPickerField;

public class AmiWebFormColorGradientPickerFieldFactory extends AmiWebFormFieldFactory<ColorGradientPickerQueryField> {

	@Override
	public String getType() {
		return QueryField.TYPE_ID_COLOR_GRADIENT_PICKER;
	}

	@Override
	public String getEditorTypeId() {
		return QueryField.TYPE_ID_COLOR_GRADIENT_PICKER;
	}

	@Override
	public String getUserLabel() {
		return QueryField.USER_LABEL_COLOR_GRADIENT_PICKER;
	}

	@Override
	public String getIcon() {
		return "field-gradientpicker.svg";
	}

	@Override
	public ColorGradientPickerQueryField createQueryField(AmiWebQueryFormPortlet form) {
		return new ColorGradientPickerQueryField(this, form);
	}

	@Override
	public BaseEditFieldPortlet<ColorGradientPickerQueryField> createEditor(AmiWebQueryFormPortlet form, int x, int y) {
		return new ColorGradientPickerEditFormPortlet(this, form, x, y);
	}

	@Override
	public Class<ColorGradientPickerQueryField> getClassType() {
		return ColorGradientPickerQueryField.class;
	}

	@Override
	public AmiWebStyleType getStyleType() {
		return AmiWebStyleTypeImpl_FormGradientColorPickerField.INSTANCE;
	}
}
