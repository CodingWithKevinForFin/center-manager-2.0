package com.f1.ami.web.style.impl;

import com.f1.ami.web.style.AmiWebStyleType;

public class AmiWebStyleTypeImpl_FormPasswordField extends AmiWebStyleType {

	private static final String TYPE = "formPasswordField";
	public static final AmiWebStyleTypeImpl_FormPasswordField INSTANCE = new AmiWebStyleTypeImpl_FormPasswordField();

	public AmiWebStyleTypeImpl_FormPasswordField() {
		super(TYPE, "Form Password Field", AmiWebStyleTypeImpl_Field.TYPE_FIELD);
		addFieldFields(true);
	}

}
