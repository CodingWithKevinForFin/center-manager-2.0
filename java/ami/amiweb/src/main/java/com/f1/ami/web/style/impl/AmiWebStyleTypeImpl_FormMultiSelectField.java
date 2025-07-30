package com.f1.ami.web.style.impl;

import com.f1.ami.web.style.AmiWebStyleType;

public class AmiWebStyleTypeImpl_FormMultiSelectField extends AmiWebStyleType {

	private static final String TYPE = "formMultiSelectField";
	public static final AmiWebStyleTypeImpl_FormMultiSelectField INSTANCE = new AmiWebStyleTypeImpl_FormMultiSelectField();

	public AmiWebStyleTypeImpl_FormMultiSelectField() {
		super(TYPE, "Form Multi Select Field", AmiWebStyleTypeImpl_Field.TYPE_FIELD);
		addFieldFields(true);
	}

}
