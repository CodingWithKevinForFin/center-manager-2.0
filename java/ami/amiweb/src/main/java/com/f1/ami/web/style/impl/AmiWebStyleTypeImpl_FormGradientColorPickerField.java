package com.f1.ami.web.style.impl;

import com.f1.ami.web.style.AmiWebStyleType;

public class AmiWebStyleTypeImpl_FormGradientColorPickerField extends AmiWebStyleType {

	private static final String TYPE = "formGradientColorPickerField";
	public static final AmiWebStyleTypeImpl_FormGradientColorPickerField INSTANCE = new AmiWebStyleTypeImpl_FormGradientColorPickerField();

	public AmiWebStyleTypeImpl_FormGradientColorPickerField() {
		super(TYPE, "Form Gradient Color Picker Field", AmiWebStyleTypeImpl_Field.TYPE_FIELD);
		addFieldFields(true);
	}

}
