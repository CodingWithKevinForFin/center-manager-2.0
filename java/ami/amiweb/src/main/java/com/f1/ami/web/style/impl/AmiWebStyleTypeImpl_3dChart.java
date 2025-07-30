package com.f1.ami.web.style.impl;

import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.ami.web.style.AmiWebStyleType;

public class AmiWebStyleTypeImpl_3dChart extends AmiWebStyleType {

	public static final String TYPE_3DCHART = "chart3d";
	public static final AmiWebStyleTypeImpl_3dChart INSTANCE = new AmiWebStyleTypeImpl_3dChart();

	public AmiWebStyleTypeImpl_3dChart() {
		super(TYPE_3DCHART, "3D Plot", AmiWebStyleTypeImpl_Panel.TYPE_PANEL);
		startGroup("General");
		addColorField(AmiWebStyleConsts.CODE_BG_CL, "Background Color");
		addColorField(AmiWebStyleConsts.CODE_SEL_CL, "Selection Color");
		addColorField(AmiWebStyleConsts.CODE_FONT_CL, "Label Color");//Font Color
		addColorField(AmiWebStyleConsts.CODE_CTRL_BTNS_CL, "Control Buttons Color");
		addTrueFalseToggleField(AmiWebStyleConsts.CODE_HIDE_CTRLS, "Control Buttons", "Show", "Hide");
		startGroup("Slider Colors");
		addColorField(AmiWebStyleConsts.CODE_SCR_X_CL, "X Color");
		addColorField(AmiWebStyleConsts.CODE_SCR_Y_CL, "Y Color");
		addColorField(AmiWebStyleConsts.CODE_SCR_Z_CL, "Z Color");
		addColorField(AmiWebStyleConsts.CODE_SCR_ZOOM_CL, "Zoom Color");
		addColorField(AmiWebStyleConsts.CODE_SCR_FOV_CL, "FOV Color");
		addColorField(AmiWebStyleConsts.CODE_SCR_X_POS_CL, "X Position Color");
		addColorField(AmiWebStyleConsts.CODE_SCR_Y_POS_CL, "Y Position Color");
		addColorsField(AmiWebStyleConsts.CODE_SERIES_CLS, "Partition Colors", 300, 50);
		addColorGradientField(AmiWebStyleConsts.CODE_GRADIENT, "Gradient Colors");
		addScrollbarFields();
		addVisualizationFields();
		lock();
	}

}
