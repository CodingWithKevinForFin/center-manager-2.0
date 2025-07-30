package com.f1.ami.web.style.impl;

import com.f1.ami.web.style.AmiWebStyleType;

public class AmiWebStyleTypeImpl_Field extends AmiWebStyleType {

	public static final String TYPE_FIELD = "field";
	public static final AmiWebStyleTypeImpl_Field INSTANCE = new AmiWebStyleTypeImpl_Field();

	public AmiWebStyleTypeImpl_Field() {
		super(TYPE_FIELD, "Field", null);
		addFieldFields(true);
	}

}
