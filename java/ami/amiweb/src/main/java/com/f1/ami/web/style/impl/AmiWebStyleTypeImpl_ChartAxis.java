package com.f1.ami.web.style.impl;

import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.ami.web.style.AmiWebStyleType;

public class AmiWebStyleTypeImpl_ChartAxis extends AmiWebStyleType {

	public static final String TYPE_CHART_AXIS = "chartAxis";
	public static final AmiWebStyleTypeImpl_ChartAxis INSTANCE = new AmiWebStyleTypeImpl_ChartAxis();

	public AmiWebStyleTypeImpl_ChartAxis() {
		super(TYPE_CHART_AXIS, "Chart Axis", null);
		startGroup("Title");
		addFontSizeField(AmiWebStyleConsts.CODE_AX_TITLE_SZ, "Font Size");
		addFontField(AmiWebStyleConsts.CODE_AX_TITLE_FONT_FAM, "Font");
		addColorField(AmiWebStyleConsts.CODE_AX_TITLE_CL, "Font Color");
		addRangeField(AmiWebStyleConsts.CODE_AX_TITLE_PD, "Padding(px)", 0, 100);
		addRangeField(AmiWebStyleConsts.CODE_AX_TITLE_ROTATE, "Rotate", -90, 90);

		startGroup("Numbers");
		addFontSizeField(AmiWebStyleConsts.CODE_AX_NUM_FONT_SZ, "Font Size");
		addFontField(AmiWebStyleConsts.CODE_AX_NUM_FONT_FAM, "Font");
		addColorField(AmiWebStyleConsts.CODE_AX_NUM_FONT_CL, "Font Color");
		addRangeField(AmiWebStyleConsts.CODE_AX_NUM_PD, "Padding(px)", 0, 100);
		addRangeField(AmiWebStyleConsts.CODE_AX_NUM_ROTATE, "Rotate", -90, 90);
		addRangeField(AmiWebStyleConsts.CODE_AX_MAJ_UNIT_SZ, "Major Tick Size(px)", 0, 40);
		addRangeField(AmiWebStyleConsts.CODE_AX_MINOR_UNIT_SZ, "Minor Tick Size(px)", 0, 40);

		startGroup("Partition Label");
		addFontSizeField(AmiWebStyleConsts.CODE_AX_LBL_FONT_SZ, "Font Size");
		addFontField(AmiWebStyleConsts.CODE_AX_LBL_FONT_FAM, "Font Family");
		addColorField(AmiWebStyleConsts.CODE_AX_LBL_FONT_CL, "Font Color");
		addRangeField(AmiWebStyleConsts.CODE_AX_LBL_PD, "Padding(px)", 0, 100);
		addRangeField(AmiWebStyleConsts.CODE_AX_LBL_ROTATE, "Rotate", -90, 90);
		addRangeField(AmiWebStyleConsts.CODE_AX_LBL_TICK_SZ, "Tick Size(px)", 0, 40);

		startGroup("Padding");
		addRangeField(AmiWebStyleConsts.CODE_AX_START_PD, "Start Padding (px)", 0, 200);
		addRangeField(AmiWebStyleConsts.CODE_AX_END_PD, "End Padding (px)", 0, 200);
		addRangeField(AmiWebStyleConsts.CODE_AX_GROUP_PD, "Group Padding (px)", 0, 200);

		startGroup("Style");
		addColorField(AmiWebStyleConsts.CODE_AX_LINE_CL, "Line Color");
		lock();
	}

}
