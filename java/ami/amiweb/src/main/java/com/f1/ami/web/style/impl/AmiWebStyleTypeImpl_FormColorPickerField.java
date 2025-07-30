package com.f1.ami.web.style.impl;

import com.f1.ami.web.style.AmiWebStyleType;

public class AmiWebStyleTypeImpl_FormColorPickerField extends AmiWebStyleType {

	private static final String TYPE = "formColorPickerField";
	public static final AmiWebStyleTypeImpl_FormColorPickerField INSTANCE = new AmiWebStyleTypeImpl_FormColorPickerField();

	public AmiWebStyleTypeImpl_FormColorPickerField() {
		super(TYPE, "Form Color Picker Field", AmiWebStyleTypeImpl_Field.TYPE_FIELD);
		addFieldFields(true);
	}

}
