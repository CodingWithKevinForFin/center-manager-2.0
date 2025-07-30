package com.f1.ami.web.style.impl;

import com.f1.ami.web.style.AmiWebStyleType;

public class AmiWebStyleTypeImpl_FormUploadField extends AmiWebStyleType {

	private static final String TYPE = "formUploadField";
	public static final AmiWebStyleTypeImpl_FormUploadField INSTANCE = new AmiWebStyleTypeImpl_FormUploadField();

	public AmiWebStyleTypeImpl_FormUploadField() {
		super(TYPE, "Form Upload Field", AmiWebStyleTypeImpl_Field.TYPE_FIELD);
		addFieldFields(true);
	}

}
