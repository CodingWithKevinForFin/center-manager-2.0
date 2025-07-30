package com.f1.ami.web.style.impl;

import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.ami.web.style.AmiWebStyleType;

public class AmiWebStyleTypeImpl_FormDateTimeField extends AmiWebStyleType {

	private static final String TYPE = "formDateTimeField";
	public static final AmiWebStyleTypeImpl_FormDateTimeField INSTANCE = new AmiWebStyleTypeImpl_FormDateTimeField();

	public AmiWebStyleTypeImpl_FormDateTimeField() {
		super(TYPE, "Form Date Time Field", AmiWebStyleTypeImpl_Field.TYPE_FIELD);
		addFieldFields(true);
		startGroup("Calendar");
		addColorField(AmiWebStyleConsts.CODE_CAL_BG_CL, "Background Color");
		addColorField(AmiWebStyleConsts.CODE_CAL_BTN_BG_CL, "Button Background Color");
		addColorField(AmiWebStyleConsts.CODE_CAL_BTN_FG_CL, "Button Font Color");
		addColorField(AmiWebStyleConsts.CODE_CAL_YR_FG_CL, "Year Font Color");
		addColorField(AmiWebStyleConsts.CODE_CAL_SEL_YR_FG_CL, "Selected Year Font Color");
		addColorField(AmiWebStyleConsts.CODE_CAL_MTN_FG_CL, "Month Font Color");
		addColorField(AmiWebStyleConsts.CODE_CAL_SEL_MTN_FG_CL, "Selected Month Font Color");
		addColorField(AmiWebStyleConsts.CODE_CAL_SEL_MTN_BG_CL, "Selected Month Background Color");
		addColorField(AmiWebStyleConsts.CODE_CAL_WK_FG_CL, "Week Font Color");
		addColorField(AmiWebStyleConsts.CODE_CAL_WK_BG_CL, "Week Background Color");
		addColorField(AmiWebStyleConsts.CODE_CAL_WK_FD_CL, "Week Fade Color");
		addColorField(AmiWebStyleConsts.CODE_CAL_DAY_FG_CL, "Day Font Color");
		addColorField(AmiWebStyleConsts.CODE_CAL_X_DAY_FG_CL, "Crossed Out Day Font Color");
		addColorField(AmiWebStyleConsts.CODE_CAL_HOV_BG_CL, "Hover Over Background Color");
	}

}
