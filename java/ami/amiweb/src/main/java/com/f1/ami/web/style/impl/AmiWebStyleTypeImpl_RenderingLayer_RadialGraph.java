package com.f1.ami.web.style.impl;

import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.ami.web.style.AmiWebStyleType;

public class AmiWebStyleTypeImpl_RenderingLayer_RadialGraph extends AmiWebStyleType {

	public static final String TYPE_LAYER_RADIAL_GRAPH = "radialGraphLayer";
	public static final AmiWebStyleTypeImpl_RenderingLayer_RadialGraph INSTANCE = new AmiWebStyleTypeImpl_RenderingLayer_RadialGraph();

	public AmiWebStyleTypeImpl_RenderingLayer_RadialGraph() {
		super(TYPE_LAYER_RADIAL_GRAPH, "Radial Rendering Layer", AmiWebStyleTypeImpl_Panel.TYPE_PANEL);
		startGroup("Axis");
		addColorField(AmiWebStyleConsts.CODE_RD_LYR_SPOKES_CL, "Spokes Color");
		addRangeField(AmiWebStyleConsts.CODE_RD_LYR_SPOKES_SZ, "Spokes Thickness", 0, 4);
		addColorField(AmiWebStyleConsts.CODE_RD_LYR_CIRCLE_CL, "Circles Color");
		addRangeField(AmiWebStyleConsts.CODE_RD_LYR_CIRCLE_SZ, "Circles Thickness", 0, 4);
		addColorField(AmiWebStyleConsts.CODE_RD_LYR_BDR_CL, "Border Color");
		addRangeField(AmiWebStyleConsts.CODE_RD_LYR_BDR_SZ, "Border Thickness", 0, 4);
		addRangeField(AmiWebStyleConsts.CODE_RD_LYR_SPOKES_COUNT, "Spokes Count", 0, 90);
		addRangeField(AmiWebStyleConsts.CODE_RD_LYR_START_ANGLE, "Start Angle(degees)", 0, 360);
		addRangeField(AmiWebStyleConsts.CODE_RD_LYR_END_ANGLE, "End Angle(degees)", 0, 360);
		addRangeField(AmiWebStyleConsts.CODE_RD_LYR_CIRCLES_COUNT, "Circles Count", 0, 100);
		addRangeField(AmiWebStyleConsts.CODE_RD_LYR_INNER_PD_PX, "Inner Padding", 0, 150);
		addRangeField(AmiWebStyleConsts.CODE_RD_LYR_OUTER_PD_PX, "Outer Padding", 0, 200);

		startGroup("Position");
		addRangeField(AmiWebStyleConsts.CODE_RD_LYR_CENTER_X, "Center X Position(%)", 0, 100);
		addRangeField(AmiWebStyleConsts.CODE_RD_LYR_CENTER_Y, "Center Y Position(%)", 0, 100);

		startGroup("Label");
		addColorField(AmiWebStyleConsts.CODE_RD_LYR_LBL_CL, "Label Color");
		addFontSizeField(AmiWebStyleConsts.CODE_RD_LYR_LBL_SZ, "Label Size");
		addRangeField(AmiWebStyleConsts.CODE_RD_LYR_LBL_ANGLE, "Label Angle(degees)", 0, 360);
		lock();
	}

}
