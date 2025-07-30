package com.f1.ami.web.style.impl;

import com.f1.ami.web.AmiWebScrollPortlet;
import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.ami.web.style.AmiWebStyleType;

public class AmiWebStyleTypeImpl_Scroll extends AmiWebStyleType {
	public static final String TYPE = AmiWebScrollPortlet.AMISCROLLPANE_ID;

	public static final AmiWebStyleTypeImpl_Scroll INSTANCE = new AmiWebStyleTypeImpl_Scroll();

	public AmiWebStyleTypeImpl_Scroll() {
		super(TYPE, "ScrollPane", AmiWebStyleTypeImpl_Panel.TYPE_PANEL);
		startGroup("General");
		addColorField(AmiWebStyleConsts.CODE_BG_CL, "Background Color", false);
		addScrollbarFields();
		addVisualizationFields();
		lock();

	}
}
