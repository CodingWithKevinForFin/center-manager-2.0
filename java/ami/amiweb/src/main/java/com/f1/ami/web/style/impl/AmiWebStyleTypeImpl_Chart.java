package com.f1.ami.web.style.impl;

import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.ami.web.style.AmiWebStyleType;

public class AmiWebStyleTypeImpl_Chart extends AmiWebStyleType {

	public static final String TYPE_CHART = "chart";
	public static final AmiWebStyleTypeImpl_Chart INSTANCE = new AmiWebStyleTypeImpl_Chart();

	public AmiWebStyleTypeImpl_Chart() {
		super(TYPE_CHART, "Chart", AmiWebStyleTypeImpl_Panel.TYPE_PANEL);
		startGroup("Values");
		addFontField(AmiWebStyleConsts.CODE_FONT_FAM, "Font");
		AmiWebStyleOptionChoices fontStyleOptions = addSelectField(AmiWebStyleConsts.CODE_FONT_STYLE, "Font Style", String.class);
		fontStyleOptions.addOption("normal", "normal", "Normal");
		fontStyleOptions.addOption("bold", "bold", "Bold");
		fontStyleOptions.addOption("italic", "italic", "Italic");
		fontStyleOptions.addOption("underline", "underline", "Underline");
		fontStyleOptions.addOption("bold_italic", "bold_italic", "Bold & Italic");
		fontStyleOptions.addOption("bold_underline", "bold_underline", "Bold & Underline");
		fontStyleOptions.addOption("italic_underline", "italic_underline", "Italic & Underline");
		fontStyleOptions.addOption("bold_italic_underline", "bold_italic_underline", "Bold, Italic & Underline");

		addColorField(AmiWebStyleConsts.CODE_BG_CL, "Background Color");
		addColorField(AmiWebStyleConsts.CODE_SEL_CL, "Selected Color");
		addColorField(AmiWebStyleConsts.CODE_SEL_BOX_CL, "Selection Box Color");
		addColorField(AmiWebStyleConsts.CODE_SEL_BOX_BDR_CL, "Selection Box Border Color");
		addColorField(AmiWebStyleConsts.CODE_DIV_CL, "Divider Color");
		addRangeField(AmiWebStyleConsts.CODE_DIV_THCK_H, "Horizontal Divider Size", 0, 10);
		addRangeField(AmiWebStyleConsts.CODE_DIV_THCK_V, "Vertical Divider Size", 0, 10);
		addColorField(AmiWebStyleConsts.CODE_SCR_CL, "Options Slider Fields Color");
		addColorsField(AmiWebStyleConsts.CODE_SERIES_CLS, "Partition Colors", 300, 50);
		addColorGradientField(AmiWebStyleConsts.CODE_GRADIENT, "Gradient Colors");
		addVisualizationFields();
		lock();
	}

}
