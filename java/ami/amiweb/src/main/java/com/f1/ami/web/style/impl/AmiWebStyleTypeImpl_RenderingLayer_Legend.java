package com.f1.ami.web.style.impl;

import com.f1.ami.web.charts.AmiWebChartRenderingLayer_Legend;
import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.ami.web.style.AmiWebStyleType;

public class AmiWebStyleTypeImpl_RenderingLayer_Legend extends AmiWebStyleType {

	public static final String TYPE_LAYER_LEGEND = "legend";
	public static final AmiWebStyleTypeImpl_RenderingLayer_Legend INSTANCE = new AmiWebStyleTypeImpl_RenderingLayer_Legend();

	public AmiWebStyleTypeImpl_RenderingLayer_Legend() {
		super(TYPE_LAYER_LEGEND, "Legend", null);
		startGroup("Title");
		AmiWebStyleOptionChoices lgdNmPosFd = addSelectField(AmiWebStyleConsts.CODE_LGD_NM_POS, "Position", Byte.class);
		addFontSizeField(AmiWebStyleConsts.CODE_LGD_NM_SZ, "Font Size");
		lgdNmPosFd.addOption(AmiWebChartRenderingLayer_Legend.KEY_TOP_LEFT, "top_left", "Top Left");
		lgdNmPosFd.addOption(AmiWebChartRenderingLayer_Legend.KEY_TOP_RIGHT, "top_right", "Top Right");
		lgdNmPosFd.addOption(AmiWebChartRenderingLayer_Legend.KEY_TOP, "top", "Top");
		lgdNmPosFd.addOption(AmiWebChartRenderingLayer_Legend.KEY_HIDDEN, "hidden", "Hidden");

		startGroup("Position");
		AmiWebStyleOptionChoices lgdKeyPosFd = addSelectField(AmiWebStyleConsts.CODE_LGD_KEY_POS, "Align", Byte.class);
		lgdKeyPosFd.addOption(AmiWebChartRenderingLayer_Legend.KEY_TOP_LEFT, "top_left", "Top Left");
		lgdKeyPosFd.addOption(AmiWebChartRenderingLayer_Legend.KEY_TOP_RIGHT, "top_right", "Top Right");
		lgdKeyPosFd.addOption(AmiWebChartRenderingLayer_Legend.KEY_BOTTOM_LEFT, "bottom_left", "Bottom Left");
		lgdKeyPosFd.addOption(AmiWebChartRenderingLayer_Legend.KEY_BOTTOM_RIGHT, "bottom_right", "Bottom Right");
		lgdKeyPosFd.addOption(AmiWebChartRenderingLayer_Legend.KEY_TOP, "top", "Top");
		lgdKeyPosFd.addOption(AmiWebChartRenderingLayer_Legend.KEY_BOTTOM, "bottom", "Bottom");
		lgdKeyPosFd.addOption(AmiWebChartRenderingLayer_Legend.KEY_LEFT, "left", "Left");
		lgdKeyPosFd.addOption(AmiWebChartRenderingLayer_Legend.KEY_RIGHT, "right", "Right");
		lgdKeyPosFd.addOption(AmiWebChartRenderingLayer_Legend.KEY_CENTER, "center", "Center");
		lgdKeyPosFd.addOption(AmiWebChartRenderingLayer_Legend.KEY_HIDDEN, "hidden", "Hidden");

		addRangeField(AmiWebStyleConsts.CODE_LGD_MAX_WD, "Width", 50, 200);
		addRangeField(AmiWebStyleConsts.CODE_LGD_MAX_HT, "Height", 50, 600);
		addRangeField(AmiWebStyleConsts.CODE_LGD_HZ_PD, "Horizontal Padding", 0, 500);
		addRangeField(AmiWebStyleConsts.CODE_LGD_VT_PD, "Vertical Padding", 0, 500);

		startGroup("General");
		addFontField(AmiWebStyleConsts.CODE_LGD_FONT_FAM, "Font");
		addColorField(AmiWebStyleConsts.CODE_LGD_NM_CL, "Title Color");
		addFontSizeField(AmiWebStyleConsts.CODE_LGD_LBL_SZ, "Body Font Size");
		addColorField(AmiWebStyleConsts.CODE_LGD_BDR_CL, "Border Color");
		addColorField(AmiWebStyleConsts.CODE_LGD_BG_CL, "Background Color");
		addColorField(AmiWebStyleConsts.CODE_LGD_CHECKBOX_CL, "Checkbox Color");
		addColorField(AmiWebStyleConsts.CODE_LGD_CHECKBOX_CHECK_CL, "Check Color");
		addColorField(AmiWebStyleConsts.CODE_LGD_CHECKBOX_BDR_CL, "Checkbox Border");
		lock();

	}

}
