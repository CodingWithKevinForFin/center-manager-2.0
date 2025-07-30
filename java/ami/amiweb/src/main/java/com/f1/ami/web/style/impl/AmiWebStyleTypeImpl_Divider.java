package com.f1.ami.web.style.impl;

import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.ami.web.style.AmiWebStyleType;

public class AmiWebStyleTypeImpl_Divider extends AmiWebStyleType {

	public static final String TYPE_DIVIDER = "div";
	public static final AmiWebStyleTypeImpl_Divider INSTANCE = new AmiWebStyleTypeImpl_Divider();

	public AmiWebStyleTypeImpl_Divider() {
		super(TYPE_DIVIDER, "Divider", null);
		startGroup("Style");
		addRangeField(AmiWebStyleConsts.CODE_DIV_SZ, "Thickness(px)", 0, 10);
		addColorField(AmiWebStyleConsts.CODE_DIV_CL, "Color");
		addColorField(AmiWebStyleConsts.CODE_DIV_HOVER_CL, "Hover Color");
		//		addTrueFalseToggleField(AmiWebStyleConsts.CODE_DIV_LOCK, "Lock Divider", "Locked", "Unlocked");
		//		addToggleField(AmiWebStyleConsts.CODE_DIV_ALIGN, "Align", Double.class, 0.0, "start", "Left/Top", .5, "ratio", "Ratio", 1.0, "end", "Right/Bottom");
		//		addToggleField(AmiWebStyleConsts.CODE_DIV_SNAP_SETTING, "Snap Setting", Byte.class, AmiWebDividerPortlet.SNAP_SETTING_NONE, "none", "None",
		//				AmiWebDividerPortlet.SNAP_SETTING_START, "start", "Left/Top", AmiWebDividerPortlet.SNAP_SETTING_END, "end", "Right/Bottom");
		//		addRangeField(AmiWebStyleConsts.CODE_DIV_SNAP_POS_PCT, "Unsnap Minimum(%)", 0, 100);
		addVisualizationFields();
		lock();
	}

}
