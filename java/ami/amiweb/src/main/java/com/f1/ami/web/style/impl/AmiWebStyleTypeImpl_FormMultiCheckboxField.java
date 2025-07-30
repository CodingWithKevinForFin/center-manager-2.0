package com.f1.ami.web.style.impl;

import com.f1.ami.web.style.AmiWebStyleType;

public class AmiWebStyleTypeImpl_FormMultiCheckboxField extends AmiWebStyleType {

	private static final String TYPE = "formMultiCheckboxField";
	public static final AmiWebStyleTypeImpl_FormMultiCheckboxField INSTANCE = new AmiWebStyleTypeImpl_FormMultiCheckboxField();

	public AmiWebStyleTypeImpl_FormMultiCheckboxField() {
		super(TYPE, "Form Multi Checkbox Field", AmiWebStyleTypeImpl_Field.TYPE_FIELD);
		addFieldFields(true);
	}

}
