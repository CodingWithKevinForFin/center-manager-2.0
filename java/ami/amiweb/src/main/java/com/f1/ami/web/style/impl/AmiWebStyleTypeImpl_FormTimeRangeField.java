package com.f1.ami.web.style.impl;

import com.f1.ami.web.style.AmiWebStyleType;

public class AmiWebStyleTypeImpl_FormTimeRangeField extends AmiWebStyleType {

	private static final String TYPE = "formTimeRangeField";
	public static final AmiWebStyleTypeImpl_FormTimeRangeField INSTANCE = new AmiWebStyleTypeImpl_FormTimeRangeField();

	public AmiWebStyleTypeImpl_FormTimeRangeField() {
		super(TYPE, "Form Time Range Field", AmiWebStyleTypeImpl_Field.TYPE_FIELD);
		addFieldFields(true);
	}

}
