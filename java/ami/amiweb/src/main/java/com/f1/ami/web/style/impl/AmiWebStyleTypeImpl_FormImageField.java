package com.f1.ami.web.style.impl;

import com.f1.ami.web.style.AmiWebStyleType;

public class AmiWebStyleTypeImpl_FormImageField extends AmiWebStyleType {

	private static final String TYPE = "formImageField";
	public static final AmiWebStyleTypeImpl_FormImageField INSTANCE = new AmiWebStyleTypeImpl_FormImageField();

	public AmiWebStyleTypeImpl_FormImageField() {
		super(TYPE, "Form Image Field", AmiWebStyleTypeImpl_Field.TYPE_FIELD);
		addFieldFields(true);
	}

}
