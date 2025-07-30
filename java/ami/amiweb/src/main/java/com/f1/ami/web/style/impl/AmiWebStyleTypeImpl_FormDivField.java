package com.f1.ami.web.style.impl;

import com.f1.ami.web.style.AmiWebStyleType;

public class AmiWebStyleTypeImpl_FormDivField extends AmiWebStyleType {

	private static final String TYPE = "formDivField";
	public static final AmiWebStyleTypeImpl_FormDivField INSTANCE = new AmiWebStyleTypeImpl_FormDivField();

	public AmiWebStyleTypeImpl_FormDivField() {
		super(TYPE, "Form Div Field", AmiWebStyleTypeImpl_Field.TYPE_FIELD);
		addFieldFields(true);
	}

}
