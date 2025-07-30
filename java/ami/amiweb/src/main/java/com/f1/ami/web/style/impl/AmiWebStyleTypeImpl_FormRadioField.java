package com.f1.ami.web.style.impl;

import com.f1.ami.web.style.AmiWebStyleType;

public class AmiWebStyleTypeImpl_FormRadioField extends AmiWebStyleType {

	private static final String TYPE = "formRadioField";
	public static final AmiWebStyleTypeImpl_FormRadioField INSTANCE = new AmiWebStyleTypeImpl_FormRadioField();

	public AmiWebStyleTypeImpl_FormRadioField() {
		super(TYPE, "Form Radio Field", AmiWebStyleTypeImpl_Field.TYPE_FIELD);
		addFieldFields(true);
	}

}
