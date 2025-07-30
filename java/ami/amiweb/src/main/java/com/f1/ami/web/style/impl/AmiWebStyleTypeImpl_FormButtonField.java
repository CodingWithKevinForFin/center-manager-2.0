package com.f1.ami.web.style.impl;

import com.f1.ami.web.style.AmiWebStyleType;

public class AmiWebStyleTypeImpl_FormButtonField extends AmiWebStyleType {

	private static final String TYPE = "formButtonField";
	public static final AmiWebStyleTypeImpl_FormButtonField INSTANCE = new AmiWebStyleTypeImpl_FormButtonField();

	public AmiWebStyleTypeImpl_FormButtonField() {
		super(TYPE, "Form Button Field", AmiWebStyleTypeImpl_Field.TYPE_FIELD);
		addFieldFields(true);
	}

}
