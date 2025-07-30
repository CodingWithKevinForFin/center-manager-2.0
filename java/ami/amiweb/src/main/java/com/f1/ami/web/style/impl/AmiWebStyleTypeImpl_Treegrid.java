package com.f1.ami.web.style.impl;

import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.ami.web.style.AmiWebStyleType;

public class AmiWebStyleTypeImpl_Treegrid extends AmiWebStyleType {

	public static final String TYPE_TREEGRID = "treegrid";
	public static final AmiWebStyleTypeImpl_Treegrid INSTANCE = new AmiWebStyleTypeImpl_Treegrid();

	public AmiWebStyleTypeImpl_Treegrid() {
		super(TYPE_TREEGRID, "Tree Grid", AmiWebStyleTypeImpl_Panel.TYPE_PANEL);
		startGroup("Body");
		addColorField(AmiWebStyleConsts.CODE_BG_CL, "Background Color");
		addColorField(AmiWebStyleConsts.CODE_GRAYBAR_CL, "Gray Bar Color");
		addRangeField(AmiWebStyleConsts.CODE_ROW_HT, "Row Height", 8, 350);
		addFontField(AmiWebStyleConsts.CODE_FONT_FAM, "Font Family");
		addFontSizeField(AmiWebStyleConsts.CODE_FONT_SZ, "Font Size");
		addColorField(AmiWebStyleConsts.CODE_FONT_CL, "Font Color");
		addRangeField(AmiWebStyleConsts.CODE_CELL_BTM_PX, "Cell Bottom Border(px)", 0, 16);
		addRangeField(AmiWebStyleConsts.CODE_CELL_RT_PX, "Cell Right Border(px)", 0, 16);
		addColorField(AmiWebStyleConsts.CODE_CELL_BDR_CL, "Cell Border Color");
		addRangeField(AmiWebStyleConsts.CODE_CELL_PAD_HT, "Cell Horizontal Padding(px)", 0, 32);
		addToggleField(AmiWebStyleConsts.CODE_VT_ALIGN, "Vertical Align", String.class, "flex-start", "top", "Top", "center", "center", "Center", "flex-end", "bottom", "Bottom");

		startGroup("Cell Flash");
		addColorField(AmiWebStyleConsts.CODE_FLASH_UP_CL, "Flash Up Color", false);
		addColorField(AmiWebStyleConsts.CODE_FLASH_DN_CL, "Flash Down Color", false);
		addRangeField(AmiWebStyleConsts.CODE_FLASH_MILLIS, "Flash Duration (millis)", 0, 1000);

		startGroup("Header");
		addTrueFalseToggleField(AmiWebStyleConsts.CODE_HEADER_BAR_HIDE, "Hide Header Bar", "Hide", "Show");
		addTrueFalseToggleField(AmiWebStyleConsts.CODE_HEADER_DIV_HIDE, "Hide Divider Bar", "Hide", "Show");
		addColorField(AmiWebStyleConsts.CODE_HEADER_BG_CL, "Background Color");
		addColorField(AmiWebStyleConsts.CODE_HEADER_FONT_CL, "Font Color");
		addRangeField(AmiWebStyleConsts.CODE_HEADER_HT, "Header Height", 8, 350);
		addFontSizeField(AmiWebStyleConsts.CODE_HEADER_FONT_SZ, "Font Size");

		startGroup("Column Filter");
		addTrueFalseToggleField(AmiWebStyleConsts.CODE_COLUMN_FILTER_HIDE, "Hide Column Filters", "Hide", "Show");
		addRangeField(AmiWebStyleConsts.CODE_COLUMN_FILTER_HT, "Height", 8, 350);
		addColorField(AmiWebStyleConsts.CODE_COLUMN_FILTER_BG_CL, "Background Color");
		addColorField(AmiWebStyleConsts.CODE_COLUMN_FILTER_FONT_CL, "Font Color");
		addFontSizeField(AmiWebStyleConsts.CODE_COLUMN_FILTER_FONT_SZ, "Font Size");
		addColorField(AmiWebStyleConsts.CODE_COLUMN_FILTER_BDR_CL, "Border Color");

		startGroup("Selection");
		addColorField(AmiWebStyleConsts.CODE_SEL_CL, "Selected Color");
		addColorField(AmiWebStyleConsts.CODE_ACT_CL, "Active Color");

		startGroup("Search Bar");
		addTrueFalseToggleField(AmiWebStyleConsts.CODE_SEARCH_HIDE, "Hide", "Hide", "Show");
		addColorField(AmiWebStyleConsts.CODE_SEARCH_BG_CL, "Background Color");
		addColorField(AmiWebStyleConsts.CODE_SEARCH_FLD_BG_CL, "Search Field Background Color");
		addColorField(AmiWebStyleConsts.CODE_SEARCH_FLD_FONT_CL, "Search Field Font Color");
		addColorField(AmiWebStyleConsts.CODE_SEARCH_FLD_BDR_CL, "Search Field Border Color");
		addColorField(AmiWebStyleConsts.CODE_SEARCH_BTNS_CL, "Button Color");

		startGroup("Filtered Header");
		addColorField(AmiWebStyleConsts.CODE_FILT_BG_CL, "Background Color");
		addColorField(AmiWebStyleConsts.CODE_FILT_FONT_CL, "Font Color");

		addScrollbarFields();
		addVisualizationFields();
		lock();
	}

}
