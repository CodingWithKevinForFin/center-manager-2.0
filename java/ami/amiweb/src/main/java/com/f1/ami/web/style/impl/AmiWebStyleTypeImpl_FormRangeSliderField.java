package com.f1.ami.web.style.impl;

import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.ami.web.style.AmiWebStyleType;

public class AmiWebStyleTypeImpl_FormRangeSliderField extends AmiWebStyleType {

	private static final String TYPE = "formRangeSliderField";
	public static final AmiWebStyleTypeImpl_FormRangeSliderField INSTANCE = new AmiWebStyleTypeImpl_FormRangeSliderField();

	public AmiWebStyleTypeImpl_FormRangeSliderField() {
		super(TYPE, "Form Range Slider Field", AmiWebStyleTypeImpl_Field.TYPE_FIELD);
		addFieldFields(true);
		startGroup("Slider");
		addColorField(AmiWebStyleConsts.CODE_FLD_TRACK_CL, "Track Color");
		addColorField(AmiWebStyleConsts.CODE_FLD_GRIP_CL, "Grip Color");
	}

}
