package com.f1.ami.plugins.mapbox;

import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.ami.web.style.AmiWebStyleType;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_Panel;

public class AmiWebStyleTypeImpl_Mapbox extends AmiWebStyleType {

	public static final String TYPE_MAPBOX = "mapbox";

	public AmiWebStyleTypeImpl_Mapbox() {
		super(TYPE_MAPBOX, "Map", AmiWebStyleTypeImpl_Panel.TYPE_PANEL);
		startGroup("Selection Box");
		addColorField(AmiWebStyleConsts.CODE_SEL_CL, "Color", false);
		addColorField(AmiWebStyleConsts.CODE_SEL_BDR_CL, "Border Color", false);
		addVisualizationFields();
		lock();
	}

}
