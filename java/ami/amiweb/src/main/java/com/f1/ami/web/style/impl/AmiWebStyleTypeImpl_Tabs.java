package com.f1.ami.web.style.impl;

import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.ami.web.style.AmiWebStyleType;

public class AmiWebStyleTypeImpl_Tabs extends AmiWebStyleType {

	public static final String TYPE_TABS = "tabs";
	public static final AmiWebStyleTypeImpl_Tabs INSTANCE = new AmiWebStyleTypeImpl_Tabs();

	public AmiWebStyleTypeImpl_Tabs() {
		super(TYPE_TABS, "Tabs", null);
		startGroup("General");
		addTrueFalseToggleField(AmiWebStyleConsts.CODE_UNDOCK, "Allow Undocking", "Yes", "No");
		addTrueFalseToggleField(AmiWebStyleConsts.CODE_HIDE_TAB_WHEN_POPPEDOUT, "Hide Tab When Popped Out", "Yes", "No");
		addTrueFalseToggleField(AmiWebStyleConsts.CODE_HASADDBUTTON, "Show Add Button", "Yes", "No");
		addTrueFalseToggleField(AmiWebStyleConsts.CODE_HIDE_EXPORT_SS_MENUITEM, "Disable Export Spreadsheet", "Yes", "No");
		addTrueFalseToggleField(AmiWebStyleConsts.CODE_HIDE_ARRANGE_TAB_MENUITEM, "Disable Rearrangement", "Yes", "No");
		addColorField(AmiWebStyleConsts.CODE_SEL_CL, "Selected Color");
		addColorField(AmiWebStyleConsts.CODE_SEL_TXT_CL, "Selected Text Color");
		addColorField(AmiWebStyleConsts.CODE_UNSEL_CL, "Unselected Color");
		addColorField(AmiWebStyleConsts.CODE_UNSEL_TXT_CL, "Unselected Text Color");
		addColorField(AmiWebStyleConsts.CODE_ADDBUTTON_CL, "Add Button Color");
		addColorField(AmiWebStyleConsts.CODE_TAB_BDR_CL, "Border Color");
		addColorField(AmiWebStyleConsts.CODE_SEL_TAB_BDR_CL, "Selected Bottom Border Color");
		addRangeField(AmiWebStyleConsts.CODE_SEL_TAB_BDR_SZ, "Selected Bottom Border Size", 0, 5);

		startGroup("Font");
		addFontField(AmiWebStyleConsts.CODE_FONT_FAM, "Font-Family");
		addFontSizeField(AmiWebStyleConsts.CODE_FONT_SZ, "Font Size");

		startGroup("Position");
		addTrueFalseToggleField(AmiWebStyleConsts.CODE_TABS_HIDE, "Hide Tabs", "Hide", "Show");
		addFalseTrueToggleField(AmiWebStyleConsts.CODE_VT, "IsVertical", "Horizontal", "Vertical");
		addFalseTrueToggleField(AmiWebStyleConsts.CODE_RT, "IsRightAligned", "Left", "Right");
		addFalseTrueToggleField(AmiWebStyleConsts.CODE_BTM, "IsBottomAligned", "Top", "Bottom");

		startGroup("Spacing");
		addRangeField(AmiWebStyleConsts.CODE_TAB_HT, "Tab height", 10, 40);
		addRangeField(AmiWebStyleConsts.CODE_PAD_TP, "Upper Padding", 0, 20);
		addRangeField(AmiWebStyleConsts.CODE_PAD_BTM, "Lower Padding", 0, 20);
		addRangeField(AmiWebStyleConsts.CODE_PAD_START, "Start Padding", 0, 20);
		addRangeField(AmiWebStyleConsts.CODE_SPACING, "Spacing", 0, 20);
		addRangeField(AmiWebStyleConsts.CODE_ROUND_LF, "Left Rounding", 0, 20);
		addRangeField(AmiWebStyleConsts.CODE_ROUND_RT, "Right Rounding", 0, 20);

		addVisualizationFields();
		lock();
	}

}
