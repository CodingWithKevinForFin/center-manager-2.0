package com.f1.ami.web.style.impl;

import com.f1.ami.web.style.AmiWebStyleType;

public class AmiWebStyleTypeImpl_FormSelectField extends AmiWebStyleType {

	private static final String TYPE = "formSelectField";
	public static final AmiWebStyleTypeImpl_FormSelectField INSTANCE = new AmiWebStyleTypeImpl_FormSelectField();

	public AmiWebStyleTypeImpl_FormSelectField() {
		super(TYPE, "Form Select Field", AmiWebStyleTypeImpl_Field.TYPE_FIELD);
		addFieldFields(true);
	}

}
