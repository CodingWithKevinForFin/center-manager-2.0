package com.f1.ami.web.style.impl;

import com.f1.ami.web.style.AmiWebStyleType;

public class AmiWebStyleTypeImpl_FormTextField extends AmiWebStyleType {

	private static final String TYPE = "formTextField";
	public static final AmiWebStyleTypeImpl_FormTextField INSTANCE = new AmiWebStyleTypeImpl_FormTextField();

	public AmiWebStyleTypeImpl_FormTextField() {
		super(TYPE, "Form Text Field", AmiWebStyleTypeImpl_Field.TYPE_FIELD);
		addFieldFields(true);
	}

}
