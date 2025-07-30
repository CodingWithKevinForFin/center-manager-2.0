package com.f1.ami.web.style.impl;

import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.ami.web.style.AmiWebStyleType;

public class AmiWebStyleTypeImpl_Form extends AmiWebStyleType {

	public static final String TYPE_FORM = "form";

	public static final AmiWebStyleTypeImpl_Form INSTANCE = new AmiWebStyleTypeImpl_Form();

	public AmiWebStyleTypeImpl_Form() {
		super(TYPE_FORM, "HTML/Canvas", AmiWebStyleTypeImpl_Panel.TYPE_PANEL);
		startGroup("Style");
		// Fields has this already
		//		addColorField(AmiWebStyleConsts.CODE_FONT_CL, "Font Color");
		addColorField(AmiWebStyleConsts.CODE_BG_CL, "Background Color");
		addToggleField(AmiWebStyleConsts.CODE_ROTATE, "Rotate HTML", Integer.class, 0, "0", "0\u00B0", 90, "90", "90\u00B0", 180, "180", "180\u00B0")
				.addOption(270, "270", "270\u00B0").setMinButtonWidth(50);
		addTrueFalseToggleField(AmiWebStyleConsts.CODE_SHOW_BTM_BTNS, "Show Buttons Panel", "Show", "Hide");
		//		startGroup("Field");
		//		addColorField(AmiWebStyleConsts.CODE_FLD_BG_CL, "Background Color", false);
		//		addColorField(AmiWebStyleConsts.CODE_FLD_FONT_CL, "Font Color", false);
		//		addFontField(AmiWebStyleConsts.CODE_FLD_FONT_FAM, "Font Family");
		//		addFontSizeField(AmiWebStyleConsts.CODE_FLD_FONT_SZ, "Font Size");
		//		addColorField(AmiWebStyleConsts.CODE_FLD_BDR_CL, "Border Color", false);
		//		addRangeField(AmiWebStyleConsts.CODE_FLD_BDR_WD, "Border Width", 0, 10);
		//		addRangeField(AmiWebStyleConsts.CODE_FLD_BDR_RAD, "Border Radius", 0, 10);
		//		startGroup("Calendar");
		//		addColorField(AmiWebStyleConsts.CODE_CAL_BG_CL, "Background Color", false);
		//		addColorField(AmiWebStyleConsts.CODE_CAL_BTN_BG_CL, "Button Background Color", false);
		//		addColorField(AmiWebStyleConsts.CODE_CAL_BTN_FG_CL, "Button Font Color", false);
		//		addColorField(AmiWebStyleConsts.CODE_CAL_YR_FG_CL, "Year Font Color", false);
		//		addColorField(AmiWebStyleConsts.CODE_CAL_SEL_YR_FG_CL, "Selected Year Font Color", false);
		//		addColorField(AmiWebStyleConsts.CODE_CAL_MTN_FG_CL, "Month Font Color", false);
		//		addColorField(AmiWebStyleConsts.CODE_CAL_SEL_MTN_FG_CL, "Selected Month Font Color", false);
		//		addColorField(AmiWebStyleConsts.CODE_CAL_SEL_MTN_BG_CL, "Selected Month Background Color", false);
		//		addColorField(AmiWebStyleConsts.CODE_CAL_WK_FG_CL, "Week Font Color", false);
		//		addColorField(AmiWebStyleConsts.CODE_CAL_WK_BG_CL, "Week Background Color", false);
		//		addColorField(AmiWebStyleConsts.CODE_CAL_WK_FD_CL, "Week Fade Color", false);
		//		addColorField(AmiWebStyleConsts.CODE_CAL_DAY_FG_CL, "Day Font Color", false);
		//		addColorField(AmiWebStyleConsts.CODE_CAL_X_DAY_FG_CL, "Crossed Out Day Font Color", false);
		//		addColorField(AmiWebStyleConsts.CODE_CAL_HOV_BG_CL, "Hover Over Background Color", false);
		//		startGroup("Slider");
		//		addColorField(AmiWebStyleConsts.CODE_FLD_TRACK_CL, "Track Color", false);
		//		addColorField(AmiWebStyleConsts.CODE_FLD_GRIP_CL, "Grip Color", false);
		//		startGroup("Label");
		//		addRangeField(AmiWebStyleConsts.CODE_LBL_PD, "Label Padding", 0, 30);
		//		addFontSizeField(AmiWebStyleConsts.CODE_FONT_SZ, "Font Size");
		//		addToggleField(AmiWebStyleConsts.CODE_TXT_ALIGN, "Text Alignment", String.class, "left", "left", "Left", "center", "center", "Center", "right", "right", "Right");
		//		addFontField(AmiWebStyleConsts.CODE_FONT_FAM, "Font Family");
		//		addFalseTrueToggleField(AmiWebStyleConsts.CODE_BOLD, "Is Bold", "Normal", "Bold").setTrueStyle("style.fontWeight=900");
		//		addFalseTrueToggleField(AmiWebStyleConsts.CODE_ITALIC, "Is Italic", "Normal", "Italic").setTrueStyle("style.fontStyle=italic");
		//		addFalseTrueToggleField(AmiWebStyleConsts.CODE_UNDERLINE, "Is Underline", "Normal", "Underline").setTrueStyle("style.textDecoration=underline");
		//		addTrueFalseToggleField(AmiWebStyleConsts.CODE_FLD_LBL_STATUS, "Show Label", "Show", "Hide");
		//		addToggleField(AmiWebStyleConsts.CODE_FLD_LBL_SIDE, "Side", Byte.class, FormPortletField.LABEL_SIDE_LEFT, "left", "Left", FormPortletField.LABEL_SIDE_RIGHT, "right",
		//				"Right", FormPortletField.LABEL_SIDE_TOP, "top", "Top").addOption(FormPortletField.LABEL_SIDE_BOTTOM, "bottom", "Bottom").setMinButtonWidth(30);
		//		addToggleField(AmiWebStyleConsts.CODE_FLD_LBL_ALIGN, "Alignment", Byte.class, FormPortletField.LABEL_SIDE_ALIGN_START, "start", "Start",
		//				FormPortletField.LABEL_SIDE_ALIGN_CENTER, "center", "Center", FormPortletField.LABEL_SIDE_ALIGN_END, "end", "End");
		//		startGroup("Css");
		//		addCssSelectField(AmiWebStyleConsts.CODE_FLD_CSS_HELP, "Help CSS Class");
		//		addCssSelectField(AmiWebStyleConsts.CODE_FLD_CSS, "Field CSS Class");
		//		addCssSelectField(AmiWebStyleConsts.CODE_FLD_CSS_LBL, "Label CSS Class");
		//		addCssSelectField(AmiWebStyleConsts.CODE_FLD_CSS_DATE, "Date");
		//		addCssSelectField(AmiWebStyleConsts.CODE_FLD_CSS_DATE_RNG, "Daterange");
		//		addCssSelectField(AmiWebStyleConsts.CODE_FLD_CSS_DATE_TIME, "Datetime");
		//		addCssSelectField(AmiWebStyleConsts.CODE_FLD_CSS_DIV, "Div");
		//		addCssSelectField(AmiWebStyleConsts.CODE_FLD_CSS_BTN, "Button");
		//		addCssSelectField(AmiWebStyleConsts.CODE_FLD_CSS_IMG, "Image");
		//		addCssSelectField(AmiWebStyleConsts.CODE_FLD_CSS_MULTI_SEL, "Multiselect");
		//		addCssSelectField(AmiWebStyleConsts.CODE_FLD_CSS_RNG, "Range");
		//		addCssSelectField(AmiWebStyleConsts.CODE_FLD_CSS_SEL, "Select");
		//		addCssSelectField(AmiWebStyleConsts.CODE_FLD_CSS_SUB_RNG, "SubRange");
		//		addCssSelectField(AmiWebStyleConsts.CODE_FLD_CSS_TXT_AREA, "Textarea");
		//		addCssSelectField(AmiWebStyleConsts.CODE_FLD_CSS_TXT, "Text");
		//		addCssSelectField(AmiWebStyleConsts.CODE_FLD_CSS_TIME, "Time");
		//		addCssSelectField(AmiWebStyleConsts.CODE_FLD_CSS_TIME_RNG, "Timerange");
		//		addCssSelectField(AmiWebStyleConsts.CODE_FLD_CSS_UPLOAD, "Upload");

		addScrollbarFields();
		addVisualizationFields();
		lock();
	}

}
