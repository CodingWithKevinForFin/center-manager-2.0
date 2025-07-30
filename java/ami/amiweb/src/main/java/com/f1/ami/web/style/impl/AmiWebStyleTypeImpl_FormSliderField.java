package com.f1.ami.web.style.impl;

import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.ami.web.style.AmiWebStyleType;

public class AmiWebStyleTypeImpl_FormSliderField extends AmiWebStyleType {

	private static final String TYPE = "formSliderField";
	public static final AmiWebStyleTypeImpl_FormSliderField INSTANCE = new AmiWebStyleTypeImpl_FormSliderField();

	public AmiWebStyleTypeImpl_FormSliderField() {
		super(TYPE, "Form Slider Field", AmiWebStyleTypeImpl_Field.TYPE_FIELD);
		addFieldFields(true);
		startGroup("Slider");
		addColorField(AmiWebStyleConsts.CODE_FLD_TRACK_CL, "Track Color");
		addColorField(AmiWebStyleConsts.CODE_FLD_GRIP_CL, "Grip Color");
	}

}
