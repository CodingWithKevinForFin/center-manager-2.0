package com.f1.ami.web.style.impl;

import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.ami.web.style.AmiWebStyleType;

public class AmiWebStyleTypeImpl_Table extends AmiWebStyleType {

	public static final String TYPE_TABLE = "table";
	public static final AmiWebStyleTypeImpl_Table INSTANCE = new AmiWebStyleTypeImpl_Table();

	public AmiWebStyleTypeImpl_Table() {
		super(TYPE_TABLE, "Table", AmiWebStyleTypeImpl_Panel.TYPE_PANEL);
		startGroup("Cells");
		addColorField(AmiWebStyleConsts.CODE_BG_CL, "Background Color");
		addColorField(AmiWebStyleConsts.CODE_GRAYBAR_CL, "Gray Bar Color");
		addRangeField(AmiWebStyleConsts.CODE_ROW_HT, "Row Height", 8, 350);
		addFontField(AmiWebStyleConsts.CODE_FONT_FAM, "Font Family");
		addFontSizeField(AmiWebStyleConsts.CODE_FONT_SZ, "Font Size");
		addColorField(AmiWebStyleConsts.CODE_FONT_CL, "Font Color");
		addRangeField(AmiWebStyleConsts.CODE_CELL_BTM_PX, "Bottom Border(px)", 0, 16);
		addRangeField(AmiWebStyleConsts.CODE_CELL_RT_PX, "Right Border(px)", 0, 16);
		addColorField(AmiWebStyleConsts.CODE_CELL_BDR_CL, "Border Color");
		addRangeField(AmiWebStyleConsts.CODE_CELL_PAD_HT, "Cell Horizontal Padding(px)", 0, 32);
		addToggleField(AmiWebStyleConsts.CODE_VT_ALIGN, "Vertical Align", String.class, "flex-start", "top", "Top", "center", "center", "Center", "flex-end", "bottom", "Bottom");
		startGroup("Cell Flash");
		addColorField(AmiWebStyleConsts.CODE_FLASH_UP_CL, "Flash Up Color");
		addColorField(AmiWebStyleConsts.CODE_FLASH_DN_CL, "Flash Down Color");
		addRangeField(AmiWebStyleConsts.CODE_FLASH_MILLIS, "Flash Duration (millis)", 0, 1000);

		startGroup("Column Header");
		addColorField(AmiWebStyleConsts.CODE_HEADER_BG_CL, "Background Color");
		addColorField(AmiWebStyleConsts.CODE_HEADER_FONT_CL, "Font Color");
		addRangeField(AmiWebStyleConsts.CODE_HEADER_HT, "Height", 0, 350);
		addFontSizeField(AmiWebStyleConsts.CODE_HEADER_FONT_SZ, "Font Size");
		addTrueFalseToggleField(AmiWebStyleConsts.CODE_HEADER_DIV_HIDE, "Hide Divider", "Hide", "Show");

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
		addColorField(AmiWebStyleConsts.CODE_SEARCH_BAR_DIV_CL, "Divider Color");
		addColorField(AmiWebStyleConsts.CODE_SEARCH_BG_CL, "Background Color");

		addColorField(AmiWebStyleConsts.CODE_SEARCH_FONT_CL, "Font Color");
		addColorField(AmiWebStyleConsts.CODE_SEARCH_FLD_CL, "Search Field Color");
		addColorField(AmiWebStyleConsts.CODE_SEARCH_FLD_BDR_CL, "Search Field Border Color");
		addColorField(AmiWebStyleConsts.CODE_SEARCH_FLD_FONT_CL, "Search Field Font Color");
		addColorField(AmiWebStyleConsts.CODE_SEARCH_BTNS_CL, "Button Color");

		startGroup("Filtered Header");
		addColorField(AmiWebStyleConsts.CODE_FILT_BG_CL, "Background Color");
		addColorField(AmiWebStyleConsts.CODE_FILT_FONT_CL, "Font Color");

		addScrollbarFields();
		addVisualizationFields();
		lock();
	}

}
