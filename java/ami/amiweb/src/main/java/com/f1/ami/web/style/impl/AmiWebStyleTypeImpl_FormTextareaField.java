package com.f1.ami.web.style.impl;

import com.f1.ami.web.style.AmiWebStyleType;

public class AmiWebStyleTypeImpl_FormTextareaField extends AmiWebStyleType {

	private static final String TYPE = "formTextareaField";
	public static final AmiWebStyleTypeImpl_FormTextareaField INSTANCE = new AmiWebStyleTypeImpl_FormTextareaField();

	public AmiWebStyleTypeImpl_FormTextareaField() {
		super(TYPE, "Form Textarea Field", AmiWebStyleTypeImpl_Field.TYPE_FIELD);
		addFieldFields(true);
	}

}
