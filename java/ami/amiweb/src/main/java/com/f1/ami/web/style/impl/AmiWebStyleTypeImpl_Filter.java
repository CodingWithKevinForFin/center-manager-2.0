package com.f1.ami.web.style.impl;

import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.ami.web.style.AmiWebStyleType;

public class AmiWebStyleTypeImpl_Filter extends AmiWebStyleType {

	public static final String TYPE_FILTER = "filter";
	public static final AmiWebStyleTypeImpl_Filter INSTANCE = new AmiWebStyleTypeImpl_Filter();

	public AmiWebStyleTypeImpl_Filter() {
		super(TYPE_FILTER, "Filter", AmiWebStyleTypeImpl_Panel.TYPE_PANEL);
		startGroup("Body");
		addColorField(AmiWebStyleConsts.CODE_BG_CL, "Background Color");
		addColorField(AmiWebStyleConsts.CODE_FONT_CL, "Font Color");
		addFontSizeField(AmiWebStyleConsts.CODE_FONT_SZ, "Font Size");
		addFontField(AmiWebStyleConsts.CODE_FONT_FAM, "Font Family");
		addColorField(AmiWebStyleConsts.CODE_FLD_FONT_CL, "Input Font Color");
		addColorField(AmiWebStyleConsts.CODE_FLD_BG_CL, "Input Background Color");
		addColorField(AmiWebStyleConsts.CODE_FLD_BDR_CL, "Input Border Color");

		startGroup("Title");
		addToggleField(AmiWebStyleConsts.CODE_TXT_ALIGN, "Alignment", String.class, "left", "left", "Left", "center", "center", "Center", "right", "right", "Right");
		addColorField(AmiWebStyleConsts.CODE_TITLE_FONT_CL, "Font Color");
		addFontSizeField(AmiWebStyleConsts.CODE_TITLE_FONT_SZ, "Font Size");
		addFontField(AmiWebStyleConsts.CODE_TITLE_FONT_FAM, "Font Family");

		addFalseTrueToggleField(AmiWebStyleConsts.CODE_BOLD, "Is Bold", "Normal", "Bold").setTrueStyle("style.fontWeight=900");
		addFalseTrueToggleField(AmiWebStyleConsts.CODE_ITALIC, "Is Italic", "Normal", "Italic").setTrueStyle("style.fontStyle=italic");
		addFalseTrueToggleField(AmiWebStyleConsts.CODE_UNDERLINE, "Is Normal", "Normal", "Underline").setTrueStyle("style.textDecoration=underline");
		addScrollbarFields();
		addVisualizationFields();
		lock();

	}

}
