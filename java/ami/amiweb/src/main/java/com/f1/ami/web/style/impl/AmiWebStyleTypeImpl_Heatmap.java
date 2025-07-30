package com.f1.ami.web.style.impl;

import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.ami.web.style.AmiWebStyleType;

public class AmiWebStyleTypeImpl_Heatmap extends AmiWebStyleType {

	public static final String TYPE_HEATMAP = "heatmap";
	public static final AmiWebStyleTypeImpl_Heatmap INSTANCE = new AmiWebStyleTypeImpl_Heatmap();

	public AmiWebStyleTypeImpl_Heatmap() {
		super(TYPE_HEATMAP, "Heat Map", AmiWebStyleTypeImpl_Panel.TYPE_PANEL);

		startGroup("Font Family");
		addFontField(AmiWebStyleConsts.CODE_FONT_FAM, "Font Family");

		startGroup("Grouping");
		addFontSizeField(AmiWebStyleConsts.CODE_GROUP_FONT_SZ, "Font Size");
		addColorField(AmiWebStyleConsts.CODE_GROUP_FONT_CL, "Font Color");
		addRangeField(AmiWebStyleConsts.CODE_GROUP_BDR_SZ, "Border Size", 1, 10);
		addColorField(AmiWebStyleConsts.CODE_GROUP_BDR_CL, "Border Color");
		addColorField(AmiWebStyleConsts.CODE_GROUP_BG_CL, "Background Color");

		startGroup("Nodes");
		addFontSizeField(AmiWebStyleConsts.CODE_NODE_FONT_SZ, "Font Size");
		addColorField(AmiWebStyleConsts.CODE_NODE_FONT_CL, "Font Color");
		addRangeField(AmiWebStyleConsts.CODE_NODE_BDR_SZ, "Border Size", 1, 10);
		addColorField(AmiWebStyleConsts.CODE_NODE_BDR_CL, "Border Color");
		addColorGradientField(AmiWebStyleConsts.CODE_NODE_GRADIENT, "Color Gradient");
		addToggleField(AmiWebStyleConsts.CODE_TXT_ALIGN, "Alignment", String.class, "left", "left", "Left", "center", "center", "Center", "right", "right", "Right");
		addToggleField(AmiWebStyleConsts.CODE_VT_ALIGN, "Vertical Alignment", String.class, "top", "top", "Top", "center", "center", "Center", "bottom", "bottom", "Bottom");

		startGroup("Select");
		addColorField(AmiWebStyleConsts.CODE_SEL_BDR_CL1, "Border Color 1");
		addColorField(AmiWebStyleConsts.CODE_SEL_BDR_CL2, "Border Color 2");
		addVisualizationFields();
		lock();
	}

}
