package com.f1.ami.web.style.impl;

import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.ami.web.style.AmiWebStyleType;

public class AmiWebStyleTypeImpl_Panel extends AmiWebStyleType {

	public static final String TYPE_PANEL = "panel";
	public static final AmiWebStyleTypeImpl_Panel INSTANCE = new AmiWebStyleTypeImpl_Panel();

	public AmiWebStyleTypeImpl_Panel() {
		super(TYPE_PANEL, "Panel", null);
		startGroup("General");
		addColorField(AmiWebStyleConsts.CODE_BG_CL, "Background Color");
		addFalseTrueToggleField(AmiWebStyleConsts.CODE_BOLD, "Is Bold", "Normal", "Bold").setTrueStyle("style.fontWeight=900");
		addColorField(AmiWebStyleConsts.CODE_CELL_BDR_CL, "Cell Border Color");
		addColorField(AmiWebStyleConsts.CODE_FILT_BG_CL, "Filter Header Background Color");
		addColorField(AmiWebStyleConsts.CODE_FILT_FONT_CL, "Filter Header Font Color");
		addColorField(AmiWebStyleConsts.CODE_FLD_BG_CL, "Field Background Color");
		addColorField(AmiWebStyleConsts.CODE_FLD_FONT_CL, "Field Font Color");
		addColorField(AmiWebStyleConsts.CODE_FONT_CL, "Font Color");
		addFontField(AmiWebStyleConsts.CODE_FONT_FAM, "Font Family");
		addFontSizeField(AmiWebStyleConsts.CODE_FONT_SZ, "Font Size");
		addColorField(AmiWebStyleConsts.CODE_GRAYBAR_CL, "Graybar Color");
		addColorField(AmiWebStyleConsts.CODE_HEADER_BG_CL, "Header Background Color");
		addTrueFalseToggleField(AmiWebStyleConsts.CODE_HEADER_DIV_HIDE, "Hide Header Bar", "Hide", "Show");
		addColorField(AmiWebStyleConsts.CODE_HEADER_FONT_CL, "Header Font Color");
		addFalseTrueToggleField(AmiWebStyleConsts.CODE_ITALIC, "Is Italic", "Normal", "Italic").setTrueStyle("style.fontStyle=italic");
		addColorField(AmiWebStyleConsts.CODE_SEARCH_BG_CL, "Search Background Color");
		addColorField(AmiWebStyleConsts.CODE_SEARCH_BTNS_CL, "Search Buttons Color");
		addColorField(AmiWebStyleConsts.CODE_SEARCH_FLD_FONT_CL, "Search Field Font Color");
		addTrueFalseToggleField(AmiWebStyleConsts.CODE_SEARCH_HIDE, "Hide Search Bar", "Hide", "Show");
		addColorField(AmiWebStyleConsts.CODE_SEL_CL, "Selected Color");
		addToggleField(AmiWebStyleConsts.CODE_TXT_ALIGN, "Title Alignment", String.class, "left", "Left", "center", "Center", "right", "Right");
		addFalseTrueToggleField(AmiWebStyleConsts.CODE_UNDERLINE, "Is Underline", "Normal", "Underline").setTrueStyle("style.textDecoration=underline");

		addScrollbarFields();
		addVisualizationFields();
	}

}
