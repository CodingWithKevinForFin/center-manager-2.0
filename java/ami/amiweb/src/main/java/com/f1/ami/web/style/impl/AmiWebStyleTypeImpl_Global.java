package com.f1.ami.web.style.impl;

import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.ami.web.style.AmiWebStyleType;

public class AmiWebStyleTypeImpl_Global extends AmiWebStyleType {

	public static final String TYPE_GLOBAL = "global";
	public static final AmiWebStyleTypeImpl_Global INSTANCE = new AmiWebStyleTypeImpl_Global();

	public AmiWebStyleTypeImpl_Global() {
		super(TYPE_GLOBAL, "Dashboard", null);
		startGroup("General");
		addColorField(AmiWebStyleConsts.CODE_DESK_BG_CL, "Background Color");

		startGroup("Title");
		addColorField(AmiWebStyleConsts.CODE_BG_CL, "Background Color");
		addColorField(AmiWebStyleConsts.CODE_FONT_CL, "Font Color");
		addColorField(AmiWebStyleConsts.CODE_TITLE_BAR_BDR_CL, "Border Color");

		startGroup("Windows");
		addColorField(AmiWebStyleConsts.CODE_WIN_BG_CL, "Background Color");
		addColorField(AmiWebStyleConsts.CODE_WIN_FONT_CL, "Font Color");
		addColorField(AmiWebStyleConsts.CODE_WIN_CL_TP_LF, "Top/Left Border Color");
		addColorField(AmiWebStyleConsts.CODE_WIN_CL_BTM_RT, "Bottom/Right Border Color");
		addFontField(AmiWebStyleConsts.CODE_WIN_FONT_FAM, "Font");
		addFontSizeField(AmiWebStyleConsts.CODE_WIN_FONT_SZ, "Font Size");
		addRangeField(AmiWebStyleConsts.CODE_WIN_HEADER_PD, "Header Padding", 1, 10);
		addRangeField(AmiWebStyleConsts.CODE_WIN_BDR_SIZE, "Border Size", 2, 10);
		addTrueFalseToggleField(AmiWebStyleConsts.CODE_POPOUTS, "Popouts", "Enabled", "Disabled");

		startGroup("Window Buttons");
		addColorField(AmiWebStyleConsts.CODE_BTN_BG_CL, "Background Color");
		addColorField(AmiWebStyleConsts.CODE_BTN_ICON_CL, "Icon Color");
		addColorField(AmiWebStyleConsts.CODE_BTN_BDR_CL, "Border Color");
		addColorField(AmiWebStyleConsts.CODE_BTN_SHDW_CL, "Shadow Color");

		startGroup("Column Help");
		addColorField(AmiWebStyleConsts.CODE_HELP_BG_CL, "Background Color");
		addColorField(AmiWebStyleConsts.CODE_HELP_FONT_CL, "Font Color");

		startGroup("Wait Animation");
		addColorField(AmiWebStyleConsts.CODE_WAIT_LINE_CL, "Line Color");
		addColorField(AmiWebStyleConsts.CODE_WAIT_BG_CL, "Background Color");
		addColorField(AmiWebStyleConsts.CODE_WAIT_FILL_CL, "Fill Color");

		// User window settings
		startGroup("User Windows");
		addColorField(AmiWebStyleConsts.CODE_USR_WIN_CL, "Window Color");
		addColorField(AmiWebStyleConsts.CODE_USR_WIN_UP_CL, "Window Top Color");
		addColorField(AmiWebStyleConsts.CODE_USR_WIN_DOWN_CL, "Window Bottom Color");
		addColorField(AmiWebStyleConsts.CODE_USR_WIN_TXT_CL, "Title Font Color");
		addFontField(AmiWebStyleConsts.CODE_DLG_TITLE_FONT_FAM, "Title Font Family");
		addRangeField(AmiWebStyleConsts.CODE_DLG_TITLE_FONT_SZ, "Title Font Size", 1, 30);
		addToggleField(AmiWebStyleConsts.CODE_DLG_TITLE_FONT_ALG, "Title Alignment", String.class, "left", "Left", "Left", "center", "Center", "Center", "right", "Right", "Right");
		addColorField(AmiWebStyleConsts.CODE_USR_WIN_FD_BG_CL, "Field Background Color");
		addColorField(AmiWebStyleConsts.CODE_USR_WIN_BTN_FONT_CL, "Field Button Font Color");
		addColorField(AmiWebStyleConsts.CODE_USR_WIN_FD_LBL_FN_CL, "Field Label Font Color");
		addColorField(AmiWebStyleConsts.CODE_USR_WIN_FD_VAL_FG_CL, "Field Value Font Color");
		addColorField(AmiWebStyleConsts.CODE_USR_WIN_FD_BRD_CL, "Field Border Color");
		addColorField(AmiWebStyleConsts.CODE_USR_WIN_BTN_CL, "Button Color");
		addColorField(AmiWebStyleConsts.CODE_USR_WIN_BTN_UP_CL, "X Button Top Color");
		addColorField(AmiWebStyleConsts.CODE_USR_WIN_BTN_DOWN_CL, "X Button Bottom Color");
		addColorField(AmiWebStyleConsts.CODE_USR_WIN_BTN_ICON_CL, "X Button Icon Color");
		addRangeField(AmiWebStyleConsts.CODE_DLG_X_BUTTON_WD, "X Button Width", 30, 120);
		addRangeField(AmiWebStyleConsts.CODE_DLG_X_BUTTON_HI, "X Button Height", 12, 26);
		addRangeField(AmiWebStyleConsts.CODE_USR_WIN_HEADER_SZ, "Header Size", 1, 40);
		addRangeField(AmiWebStyleConsts.CODE_USR_WIN_BDR_SZ, "Border Size", 1, 20);
		addColorField(AmiWebStyleConsts.CODE_USR_WIN_FORM_BG_CL, "Form Background Color");
		addColorField(AmiWebStyleConsts.CODE_USR_WIN_FORM_BTN_PANEL_CL, "Form Button Panel Color");
		addRangeField(AmiWebStyleConsts.CODE_USR_WIN_INNER_BDR_SZ, "Inner Border Size", 0, 2);
		addRangeField(AmiWebStyleConsts.CODE_USR_WIN_OUTER_BDR_SZ, "Outer Border Size", 0, 2);

		startGroup("Menu");
		addColorField(AmiWebStyleConsts.CODE_MENU_FONT_CL, "Font Color");
		addColorField(AmiWebStyleConsts.CODE_MENU_BG_CL, "Background Color");
		addColorField(AmiWebStyleConsts.CODE_MENU_DIV_CL, "Divider Color");
		addColorField(AmiWebStyleConsts.CODE_MENU_DISABLED_BG_CL, "Disabled Background Color");
		addColorField(AmiWebStyleConsts.CODE_MENU_DISABLED_FONT_CL, "Disabled Font Color");
		addColorField(AmiWebStyleConsts.CODE_MENU_BORDER_TP_LF_CL, "Border Top Left Color");
		addColorField(AmiWebStyleConsts.CODE_MENU_BORDER_BTM_RT_CL, "Border Bottom Right Color");
		addColorField(AmiWebStyleConsts.CODE_MENU_HOVER_BG_CL, "Hover Background Color");
		addColorField(AmiWebStyleConsts.CODE_MENU_HOVER_FONT_CL, "Hover Font Color");

		// alert dialog styles
		startGroup("Alert Dialog Size");
		addRangeField(AmiWebStyleConsts.CODE_DLG_WD, "Dialog Width", 360, 720);
		addRangeField(AmiWebStyleConsts.CODE_DLG_HI, "Dialog Height", 170, 680);
		addFontSizeField(AmiWebStyleConsts.CODE_DLG_FONT_SZ, "Body Font Size", 50);

		startGroup("Alert Dialog Text");
		addFalseTrueToggleField(AmiWebStyleConsts.CODE_BOLD, "Is Bold", "Normal", "Bold").setTrueStyle("style.fontWeight=900");
		addFalseTrueToggleField(AmiWebStyleConsts.CODE_UNDERLINE, "Is Underline", "Normal", "Underline").setTrueStyle("style.textDecoration=underline");
		addFalseTrueToggleField(AmiWebStyleConsts.CODE_ITALIC, "Is Italic", "Normal", "Italic").setTrueStyle("style.fontStyle=italic");
		addToggleField(AmiWebStyleConsts.CODE_TXT_ALIGN, "Body Alignment", String.class, "left", "Left", "Left", "center", "Center", "Center", "right", "Right", "Right");
		addFontField(AmiWebStyleConsts.CODE_DLG_FONT_FAM, "Body Font Family");
		addFontField(AmiWebStyleConsts.CODE_DLG_FORM_BUTTON_FONT_FAM, "Button Font Family");

		startGroup("Alert Dialog Colors");
		addColorField(AmiWebStyleConsts.CODE_DLG_FLD_BG_CL, "Body Background Color");
		addColorField(AmiWebStyleConsts.CODE_DLG_FORM_BUTTON_PNL_BG_CL, "Form Panel Background Color");
		addColorField(AmiWebStyleConsts.CODE_DLG_FLD_FONT_CL, "Body Font Color");
		addColorField(AmiWebStyleConsts.CODE_DLG_FORM_BUTTON_BG_CL, "Button Background Color");
		addColorField(AmiWebStyleConsts.CODE_DLG_FORM_BUTTON_FONT_CL, "Button Font Color");
		lock();
	}
}
