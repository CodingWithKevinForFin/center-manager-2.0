package com.f1.ami.web.style.impl;

import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.ami.web.style.AmiWebStyleType;

public class AmiWebStyleTypeImpl_RenderingLayer_Graph extends AmiWebStyleType {

	public static final String TYPE_LAYER_GRAPH = "graphLayer";
	public static final AmiWebStyleTypeImpl_RenderingLayer_Graph INSTANCE = new AmiWebStyleTypeImpl_RenderingLayer_Graph();

	public AmiWebStyleTypeImpl_RenderingLayer_Graph() {
		super(TYPE_LAYER_GRAPH, "Graph Rendering Layer", null);
		startGroup("General");
		addColorField(AmiWebStyleConsts.CODE_GR_LYR_BDR_CL, "Border Color");

		startGroup("Horizontal Grid Lines");
		addColorField(AmiWebStyleConsts.CODE_GR_LYR_V_GRD_CL, "Grouping Color");
		addRangeField(AmiWebStyleConsts.CODE_V_GRD_SZ, "Grouping Thickness", 0, 4);
		addColorField(AmiWebStyleConsts.CODE_GR_LYR_V_MAJ_GRD_CL, "Major Unit Color");
		addRangeField(AmiWebStyleConsts.CODE_V_MAJ_GRD_SZ, "Major Unit Thickness", 0, 4);
		addColorField(AmiWebStyleConsts.CODE_GR_LYR_V_MID_GRD_CL, "Grouping Separator Color");
		addRangeField(AmiWebStyleConsts.CODE_V_MID_GRD_SZ, "Grouping Seperator Thickness", 0, 4);

		startGroup("Vertical Grid Lines");
		addColorField(AmiWebStyleConsts.CODE_GR_LYR_H_GRD_CL, "Grouping Color");
		addRangeField(AmiWebStyleConsts.CODE_H_GRD_SZ, "Grouping Thickness", 0, 4);
		addColorField(AmiWebStyleConsts.CODE_GR_LYR_H_MAJ_GRD_CL, "Major Unit Color");
		addRangeField(AmiWebStyleConsts.CODE_H_MAJ_GRD_SZ, "Major Unit Thickness", 0, 4);
		addColorField(AmiWebStyleConsts.CODE_GR_LYR_H_MID_GRD_CL, "Grouping Separator Color");
		addRangeField(AmiWebStyleConsts.CODE_H_MID_GRD_SZ, "Grouping Seperator Thickness", 0, 4);
		lock();
	}

}
