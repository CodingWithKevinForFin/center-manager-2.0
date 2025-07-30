package com.f1.ami.web.style.impl;

import com.f1.ami.web.style.AmiWebStyleType;

public class AmiWebStyleTypeImpl_FormTimeField extends AmiWebStyleType {

	private static final String TYPE = "formTimeField";
	public static final AmiWebStyleTypeImpl_FormTimeField INSTANCE = new AmiWebStyleTypeImpl_FormTimeField();

	public AmiWebStyleTypeImpl_FormTimeField() {
		super(TYPE, "Form Time Field", AmiWebStyleTypeImpl_Field.TYPE_FIELD);
		addFieldFields(true);
	}

}
