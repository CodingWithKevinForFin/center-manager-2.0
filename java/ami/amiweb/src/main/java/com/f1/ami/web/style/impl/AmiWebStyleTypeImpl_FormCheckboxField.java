package com.f1.ami.web.style.impl;

import com.f1.ami.web.style.AmiWebStyleType;

public class AmiWebStyleTypeImpl_FormCheckboxField extends AmiWebStyleType {

	private static final String TYPE = "formCheckboxField";
	public static final AmiWebStyleTypeImpl_FormCheckboxField INSTANCE = new AmiWebStyleTypeImpl_FormCheckboxField();

	public AmiWebStyleTypeImpl_FormCheckboxField() {
		super(TYPE, "Form Checkbox Field", AmiWebStyleTypeImpl_Field.TYPE_FIELD);
		addFieldFields(true);
	}

}
