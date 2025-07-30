package com.f1.ami.web.style;

import java.util.Set;

import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.OneToOne;
import com.f1.utils.SH;

public class AmiWebStyleConsts {
	public static final int FONT_SIZE_MIN = 0;
	public static final int FONT_SIZE_MAX = 80;
	public static final byte TYPE_COLOR = 1;
	//	public static final byte TYPE_COLOR_ALPHA = 2;
	public static final byte TYPE_COLOR_ARRAY = 3;
	public static final byte TYPE_BOOLEAN = 4;
	public static final byte TYPE_ENUM = 6;
	public static final byte TYPE_FONT = 7;
	public static final byte TYPE_CSS_CLASS = 8;
	public static final byte TYPE_NUMBER = 9;
	public static final byte TYPE_COLOR_GRADIENT = 10;

	public static final OneToOne<String, String> CODES_AS_STRINGS = new OneToOne<String, String>();
	public static final OneToOne<String, Short> CODES = new OneToOne<String, Short>();

	public static void ADD(String key, short code) {
		OH.assertTrue(CODES.put(key, code));
		OH.assertTrue(CODES_AS_STRINGS.put(key, SH.toString(code)));
	}

	////////////COMPOSITE
	public static final String PROPERTY_NAME_DIV_CL = "divCl";//divider,charts
	public static final String PROPERTY_NAME_FONT_SZ = "fontSz";//Filter, Form,Table,Tabs,Tree
	public static final String PROPERTY_NAME_BG_CL = "bgCl";//chart, 3dchart, filter,form,desktop,Table,Tabs,Tree
	public static final String PROPERTY_NAME_SEL_CL = "selCl";//chart,3dchart,table,tabs
	public static final String PROPERTY_NAME_FONT_CL = "fontCl";//3dchart,filter,form,desktop,table,tree
	public static final String PROPERTY_NAME_FONT_STYLE = "fontSt"; //chart, radialchart

	//ALL
	public static final String PROPERTY_NAME_PD_BDR_CL = "pdBdrCl";
	public static final String PROPERTY_NAME_PD_BDR_SZ_PX = "pdBdrSzPx";
	public static final String PROPERTY_NAME_PD_BTM_PX = "pdBtmPx";
	public static final String PROPERTY_NAME_PD_CL = "pdCl";
	public static final String PROPERTY_NAME_PD_RAD_TP_LF_PX = "pdRadTpLfPx";
	public static final String PROPERTY_NAME_PD_RAD_TP_RT_PX = "pdRadTpRtPx";
	public static final String PROPERTY_NAME_PD_RAD_BTM_LF_PX = "pdRadBtmLfPx";
	public static final String PROPERTY_NAME_PD_RAD_BTM_RT_PX = "pdRadBtmRtPx";
	public static final String PROPERTY_NAME_PD_LF_PX = "pdLfPx";
	public static final String PROPERTY_NAME_PD_RT_PX = "pdRtPx";
	public static final String PROPERTY_NAME_PD_SHADOW_CL = "pdShadowCl";
	public static final String PROPERTY_NAME_PD_SHADOW_HZ_PX = "pdShadowHzPx";
	public static final String PROPERTY_NAME_PD_SHADOW_SZ_PX = "pdShadowSzPx";
	public static final String PROPERTY_NAME_PD_SHADOW_VT_PX = "pdShadowVtPx";
	public static final String PROPERTY_NAME_PD_TP_PX = "pdTpPx";
	public static final String PROPERTY_NAME_TITLE_PNL_ALIGN = "titlePnlAlign";
	public static final String PROPERTY_NAME_TITLE_PNL_FONT_CL = "titlePnlFontCl";
	public static final String PROPERTY_NAME_TITLE_PNL_FONT_SZ = "titlePnlFontSz";
	public static final String PROPERTY_NAME_TITLE_PNL_FONT_FAM = "titlePnlFontFm";

	//Table, Filter, Form, Surface, Tree
	public static final String PROPERTY_NAME_SCROLL_BTN_CL = "scrollBtnCl";
	public static final String PROPERTY_NAME_SCROLL_GRIP_CL = "scrollGripCl";
	public static final String PROPERTY_NAME_SCROLL_ICONS_CL = "scrollIconsCl";
	public static final String PROPERTY_NAME_SCROLL_TRACK_CL = "scrollTrackCl";
	public static final String PROPERTY_NAME_SCROLL_BDR_CL = "scrollBdrCl";
	public static final String PROPERTY_NAME_SCROLL_WD = "scrollWd";
	public static final String PROPERTY_NAME_SCROLL_BAR_RADIUS = "scrollBarRadius";
	public static final String PROPERTY_NAME_SCROLL_BAR_HIDE_ARROWS = "scrollBarHideArrows";
	public static final String PROPERTY_NAME_SCROLL_BAR_CORNER_CL = "scrollBarCornerCl";

	//TABLE & TREE
	public static final String PROPERTY_NAME_ROW_HT = "rowHt";
	public static final String PROPERTY_NAME_HEADER_HT = "headerHt";
	public static final String PROPERTY_NAME_HEADER_FONT_SZ = "headerFontSz";
	public static final String PROPERTY_NAME_CELL_BDR_CL = "cellBdrCl";
	public static final String PROPERTY_NAME_CELL_PAD_HT = "cellPadHt";
	public static final String PROPERTY_NAME_FILT_BG_CL = "filtBgCl";
	public static final String PROPERTY_NAME_FILT_FONT_CL = "filtFontCl";
	public static final String PROPERTY_NAME_GRAYBAR_CL = "graybarCl";
	public static final String PROPERTY_NAME_HEADER_BG_CL = "headerBgCl";
	public static final String PROPERTY_NAME_HEADER_DIV_HIDE = "headerDivHide";
	public static final String PROPERTY_NAME_HEADER_FONT_CL = "headerFontCl";
	public static final String PROPERTY_NAME_SEARCH_BG_CL = "searchBgCl";
	public static final String PROPERTY_NAME_SEARCH_BTNS_CL = "searchBtnsCl";
	public static final String PROPERTY_NAME_SEARCH_FLD_FONT_CL = "searchFldFontCl";
	public static final String PROPERTY_NAME_SEARCH_HIDE = "searchHide";
	public static final String PROPERTY_NAME_CELL_BTM_PX = "cellBtmPx";
	public static final String PROPERTY_NAME_CELL_RT_PX = "cellRtPx";
	public static final String PROPERTY_NAME_VT_ALIGN = "vtAlign";
	public static final String PROPERTY_NAME_FLASH_UP_CL = "flashUpCl";
	public static final String PROPERTY_NAME_FLASH_DN_CL = "flashDnCl";
	public static final String PROPERTY_NAME_FLASH_MILLIS = "FlashMillis";

	//FILTER & FORM
	public static final String PROPERTY_NAME_FLD_BG_CL = "fldBgCl";
	public static final String PROPERTY_NAME_FLD_FONT_CL = "fldFontCl";
	public static final String PROPERTY_NAME_FONT_FAM = "fontFam";
	public static final String PROPERTY_NAME_ITALIC = "italic";
	public static final String PROPERTY_NAME_UNDERLINE = "underline";
	public static final String PROPERTY_NAME_TXT_ALIGN = "txtAlign";
	public static final String PROPERTY_NAME_BOLD = "bold";
	public static final String PROPERTY_NAME_FLD_FONT_FAM = "fldFontFam";
	public static final String PROPERTY_NAME_FLD_FONT_SZ = "fldFontSz";

	//FORM
	public static final String PROPERTY_NAME_ROTATE = "rotate";
	public static final String PROPERTY_NAME_SHOW_BTM_BTNS = "showBtmBtns";
	public static final String PROPERTY_NAME_FLD_LBL_STATUS = "fldLblStatus";
	public static final String PROPERTY_NAME_FLD_LBL_SIDE = "fldLblSide";
	public static final String PROPERTY_NAME_FLD_LBL_ALIGN = "fldLblAlign";
	public static final String PROPERTY_NAME_FLD_BDR_CL = "fldBdrCl";
	public static final String PROPERTY_NAME_FLD_PRIM_CL = "fldPrimCl";
	public static final String PROPERTY_NAME_FLD_SEC_CL = "fldSecCl";
	public static final String PROPERTY_NAME_FLD_TRACK_CL = "fldTrackCl";
	public static final String PROPERTY_NAME_FLD_GRIP_CL = "fldGripCl";
	
	//Deprecated style consts,In 19015.dev, these are moved to `field->formfield-> fld css` section, any old stable layout(before 23183.stable) is using these deprecated properties
	//We need to make them backwards compatible so that eg. "fldCssCheck" in old stable is interpreted as "formCheckboxFieldFldCss" in new stable(after 23189.stable), etc.
	public static final String DEPRECATED_PROPERTY_NAME_FLD_CSS_CHECK = "fldCssCheck";
	public static final String DEPRECATED_PROPERTY_NAME_FLD_CSS_DATE = "fldCssDate";
	public static final String DEPRECATED_PROPERTY_NAME_FLD_CSS_DATE_RNG = "fldCssDateRng";
	public static final String DEPRECATED_PROPERTY_NAME_FLD_CSS_DATE_TIME = "fldCssDateTime";
	public static final String DEPRECATED_PROPERTY_NAME_FLD_CSS_DIV = "fldCssDiv";
	public static final String DEPRECATED_PROPERTY_NAME_FLD_CSS_BTN = "fldCssBtn";
	public static final String DEPRECATED_PROPERTY_NAME_FLD_CSS_IMG = "fldCssImg";
	public static final String DEPRECATED_PROPERTY_NAME_FLD_CSS_MULTI_SEL = "fldCssMultiSel";
	public static final String DEPRECATED_PROPERTY_NAME_FLD_CSS_RNG = "fldCssRng";
	public static final String DEPRECATED_PROPERTY_NAME_FLD_CSS_SEL = "fldCssSel";
	public static final String DEPRECATED_PROPERTY_NAME_FLD_CSS_SUB_RNG = "fldCssSubRng";
	public static final String DEPRECATED_PROPERTY_NAME_FLD_CSS_TXT_AREA = "fldCssTxtArea";
	public static final String DEPRECATED_PROPERTY_NAME_FLD_CSS_TXT = "fldCssTxt";
	public static final String DEPRECATED_PROPERTY_NAME_FLD_CSS_TIME = "fldCssTime";
	public static final String DEPRECATED_PROPERTY_NAME_FLD_CSS_TIME_RNG = "fldCssTimeRng";
	public static final String DEPRECATED_PROPERTY_NAME_FLD_CSS_UPLOAD = "fldCssUpload";
		
		
	public static final String PROPERTY_NAME_FLD_CSS = "fldCss";
	public static final String PROPERTY_NAME_FLD_CSS_HELP = "fldCssHelp";
	public static final String PROPERTY_NAME_FLD_CSS_LBL = "fldCssLbl";
	public static final String PROPERTY_NAME_LBL_PD = "lblPd";

	//MAPBOX
	public static final String PROPERTY_NAME_SEL_BDR_CL = "selBdrCl";

	//DIVIDER
	public static final String PROPERTY_NAME_DIV_SZ = "divSz";
	public static final String PROPERTY_NAME_DIV_ALIGN = "divAlign";
	public static final String PROPERTY_NAME_DIV_HOVER_CL = "divHoverCl";
	public static final String PROPERTY_NAME_DIV_SNAP_SETTING = "divSnapSetting";
	public static final String PROPERTY_NAME_DIV_SNAP_POS_PCT = "divSnapPosPct";
	public static final String PROPERTY_NAME_DIV_LOCK = "divLock";

	//CHART

	public static final String PROPERTY_NAME_SCR_CL = "scrCl";
	public static final String PROPERTY_NAME_SERIES_CLS = "seriesCls";
	public static final String PROPERTY_NAME_GRADIENT = "gradient";
	public static final String PROPERTY_NAME_DIV_THCK_H = "divThckH";
	public static final String PROPERTY_NAME_DIV_THCK_V = "divThckV";
	public static final String PROPERTY_NAME_SEL_BOX_CL = "selBoxCl";
	public static final String PROPERTY_NAME_SEL_BOX_BDR_CL = "selBoxBrdCl";
	public static final String PROPERTY_NAME_BTM = "btm";

	//TREEMAP
	public static final String PROPERTY_NAME_GROUP_BDR_CL = "groupBdrCl";
	public static final String PROPERTY_NAME_GROUP_FONT_CL = "groupFontCl";
	public static final String PROPERTY_NAME_GROUP_FONT_SZ = "groupFontSz";
	public static final String PROPERTY_NAME_GROUP_BDR_SZ = "groupBdrSz";
	public static final String PROPERTY_NAME_SEL_BDR_CL2 = "selBdrCl2";
	public static final String PROPERTY_NAME_SEL_BDR_CL1 = "selBdrCl1";
	public static final String PROPERTY_NAME_NODE_FONT_SZ = "nodeFontSz";
	public static final String PROPERTY_NAME_NODE_BDR_SZ = "nodeBdrSz";
	public static final String PROPERTY_NAME_GROUP_BG_CL = "groupBgCl";
	public static final String PROPERTY_NAME_NODE_FONT_CL = "nodeFontCl";
	public static final String PROPERTY_NAME_NODE_BDR_CL = "nodeBdrCl";
	public static final String PROPERTY_NAME_NODE_GRADIENT = "nodeGradient";

	//3D CHART
	public static final String PROPERTY_NAME_HIDE_CTRLS = "hideCtrls";
	public static final String PROPERTY_NAME_SCR_X_CL = "scrXCl";
	public static final String PROPERTY_NAME_SCR_Y_CL = "scrYCl";
	public static final String PROPERTY_NAME_SCR_Z_CL = "scrZCl";
	public static final String PROPERTY_NAME_SCR_ZOOM_CL = "scrZoomCl";
	public static final String PROPERTY_NAME_SCR_FOV_CL = "scrFOVCl";
	public static final String PROPERTY_NAME_SCR_X_POS_CL = "scrXPosCl";
	public static final String PROPERTY_NAME_SCR_Y_POS_CL = "scrYPosCl";
	public static final String PROPERTY_NAME_CTRL_BTNS_CL = "ctrlBtnsCl";

	//TABLE
	public static final String PROPERTY_NAME_SEARCH_FONT_CL = "searchFontCl";
	public static final String PROPERTY_NAME_SEARCH_FLD_CL = "searchFldCl";
	public static final String PROPERTY_NAME_SEARCH_FLD_BG_CL = "searchFldBgCl";
	public static final String PROPERTY_NAME_ACT_CL = "actCl";
	public static final String PROPERTY_NAME_SEARCH_BAR_DIV_CL = "searchBarDivCl";
	public static final String PROPERTY_NAME_SEARCH_FLD_BDR_CL = "searchFldBdrCl";
	public static final String PROPERTY_NAME_COLUMN_FILTER_HIDE = "columnFilterHide";
	public static final String PROPERTY_NAME_COLUMN_FILTER_HEIGHT = "columnFilterHeight";
	public static final String PROPERTY_NAME_COLUMN_FILTER_BG_CL = "columnFilterBgCl";
	public static final String PROPERTY_NAME_COLUMN_FILTER_FONT_CL = "columnFilterFontCl";
	public static final String PROPERTY_NAME_COLUMN_FILTER_FONT_SZ = "columnFilterFontSz";
	public static final String PROPERTY_NAME_COLUMN_FILTER_BDR_CL = "columnFilterBdrCl";

	//TREE
	public static final String PROPERTY_NAME_HEADER_BAR_HIDE = "headerBarHide";

	//TAB
	public static final String PROPERTY_NAME_UNDOCK = "undock";
	public static final String PROPERTY_NAME_HIDE_TAB_WHEN_POPPEDOUT = "htwpo";
	public static final String PROPERTY_NAME_UNSEL_TXT_CL = "unselTxtCl";
	public static final String PROPERTY_NAME_VT = "vt";
	public static final String PROPERTY_NAME_TABS_HIDE = "tabsHide";
	public static final String PROPERTY_NAME_ROUND_RT = "roundRt";
	public static final String PROPERTY_NAME_SPACING = "spacing";
	public static final String PROPERTY_NAME_SEL_TXT_CL = "selTxtCl";
	public static final String PROPERTY_NAME_PAD_TP = "padTp";
	public static final String PROPERTY_NAME_PAD_BTM = "padBtm";
	public static final String PROPERTY_NAME_PAD_START = "padStart";
	public static final String PROPERTY_NAME_ROUND_LF = "roundLf";
	public static final String PROPERTY_NAME_UNSEL_CL = "unselCl";
	public static final String PROPERTY_NAME_TAB_HT = "tabHt";
	public static final String PROPERTY_NAME_RT = "rt";
	public static final String PROPERTY_NAME_HASADDBUTTON = "hasAddBtn";
	public static final String PROPERTY_NAME_ADDBUTTON_CL = "addBtnCl";
	public static final String PROPERTY_NAME_HIDE_EXPORT_SS_MENUITEM = "hideExpSsMnItm";
	public static final String PROPERTY_NAME_HIDE_ARRANGE_TAB_MENUITEM = "hideArrTbMnItm";
	public static final String PROPERTY_NAME_TAB_BDR_CL = "tabBdrCl";
	public static final String PROPERTY_NAME_SEL_TAB_BDR_CL = "tabSelBdrCl";
	public static final String PROPERTY_NAME_SEL_TAB_BDR_SZ = "tabSelBdrSz";

	//DESKTOP
	public static final String PROPERTY_NAME_DESK_BG_CL = "deskBgCl";
	public static final String PROPERTY_NAME_USR_WIN_BTN_ICON_CL = "usrWinBtnIconCl";
	public static final String PROPERTY_NAME_BTN_ICON_CL = "btnIconCl";
	public static final String PROPERTY_NAME_USR_WIN_BTN_CL = "usrWinBtnCl";
	public static final String PROPERTY_NAME_USR_WIN_BTN_DOWN_CL = "usrWinBtnDownCl";
	public static final String PROPERTY_NAME_USR_WIN_TXT_CL = "usrWinTxtCl";
	public static final String PROPERTY_NAME_USR_WIN_FD_LBL_FN_CL = "usrWinFdLblFnCl";
	public static final String PROPERTY_NAME_USR_WIN_FD_BG_CL = "usrWinFdBgCl";
	public static final String PROPERTY_NAME_USR_WIN_FD_BDR_CL = "usrWinFdBdrCl";
	public static final String PROPERTY_NAME_USR_WIN_FD_VAL_FG_CL = "usrWinFdValFgCl";
	public static final String PROPERTY_NAME_POPOUTS = "popouts";
	public static final String PROPERTY_NAME_USR_WIN_CL = "usrWinCl";
	public static final String PROPERTY_NAME_USR_WIN_DOWN_CL = "usrWinDownCl";
	public static final String PROPERTY_NAME_WIN_BG_CL = "winBgCl";
	public static final String PROPERTY_NAME_USR_WIN_BTN_UP_CL = "usrWinBtnUpCl";
	public static final String PROPERTY_NAME_HELP_FONT_CL = "helpFontCl";
	public static final String PROPERTY_NAME_USR_WIN_UP_CL = "usrWinUpCl";
	public static final String PROPERTY_NAME_BTN_BG_CL = "btnBgCl";
	public static final String PROPERTY_NAME_WIN_FONT_CL = "winFontCl";
	public static final String PROPERTY_NAME_WIN_FONT_SZ = "winFontSz";
	public static final String PROPERTY_NAME_WIN_FONT_FAM = "winFontFam";
	public static final String PROPERTY_NAME_WIN_CL_TP_LF = "winClTpLf";
	public static final String PROPERTY_NAME_WIN_CL_BTM_RT = "winClBtmRt";
	public static final String PROPERTY_NAME_WIN_HEADER_PD = "winHeaderPd";
	public static final String PROPERTY_NAME_WIN_BDR_SIZE = "winBorderSz";
	public static final String PROPERTY_NAME_TITLE_BAR_BDR_CL = "titleBarBdrCl";
	public static final String PROPERTY_NAME_HELP_BG_CL = "helpBgCl";
	public static final String PROPERTY_NAME_BTN_BDR_CL = "btnBdrCl";
	public static final String PROPERTY_NAME_USR_WIN_BTN_FONT_CL = "usrWinBtnFontCl";
	public static final String PROPERTY_NAME_BTN_SHDW_CL = "btnShdwCl";
	public static final String PROPERTY_NAME_USR_WIN_HEADER_SZ = "usrWinHeaderSz";
	public static final String PROPERTY_NAME_USR_WIN_BDR_SZ = "usrWinBdrSz";
	public static final String PROPERTY_NAME_USR_WIN_FORM_BG_CL = "usrWinFormBgCl";
	public static final String PROPERTY_NAME_USR_WIN_FORM_BTN_PANEL_CL = "usrWinFormBtnPanelCl";
	public static final String PROPERTY_NAME_USR_WIN_INNER_BDR_SZ = "usrWinInnerBdrSz";
	public static final String PROPERTY_NAME_USR_WIN_OUTER_BDR_SZ = "usrWinOuterBdrSz";
	public static final String PROPERTY_NAME_WAIT_LINE_CL = "waitLineCl";
	public static final String PROPERTY_NAME_WAIT_BG_CL = "waitBgCl";
	public static final String PROPERTY_NAME_WAIT_FILL_CL = "waitFillCl";
	public static final String PROPERTY_NAME_MENU_DIV_CL = "menuDivCl";
	public static final String PROPERTY_NAME_MENU_FONT_CL = "menuFontCl";
	public static final String PROPERTY_NAME_MENU_BG_CL = "menuBgCl";
	public static final String PROPERTY_NAME_MENU_DISABLED_BG_CL = "menuDisBgCl";
	public static final String PROPERTY_NAME_MENU_DISABLED_FONT_CL = "menuDisFontCl";
	public static final String PROPERTY_NAME_MENU_BORDER_TOP_LEFT_CL = "menuBorderTpLfCl";
	public static final String PROPERTY_NAME_MENU_BORDER_BOTTOM_RIGHT_CL = "menuBorderBtmRtCl";
	public static final String PROPERTY_NAME_MENU_HOVER_BG_CL = "menuHoverBgCl";
	public static final String PROPERTY_NAME_MENU_HOVER_FONT_CL = "menuHoverFontCl";

	//RADIAL GRAPH
	public static final String PROPERTY_NAME_RD_LYR_CENTER_X = "rdLyrCenterX";
	public static final String PROPERTY_NAME_RD_LYR_END_ANGLE = "rdLyrEndAngle";
	public static final String PROPERTY_NAME_RD_LYR_CENTER_Y = "rdLyrCenterY";
	public static final String PROPERTY_NAME_RD_LYR_SPOKES_COUNT = "rdLyrSpokesCount";
	public static final String PROPERTY_NAME_RD_LYR_OUTER_PD_PX = "rdLyrOuterPdPx";
	public static final String PROPERTY_NAME_RD_LYR_LBL_ANGLE = "rdLyrLblAngle";
	public static final String PROPERTY_NAME_RD_LYR_CIRCLES_COUNT = "rdLyrCirclesCount";
	public static final String PROPERTY_NAME_RD_LYR_LBL_CL = "rdLyrLblCl";
	public static final String PROPERTY_NAME_RD_LYR_CIRCLE_CL = "rdLyrCircleCl";
	public static final String PROPERTY_NAME_RD_LYR_INNER_PD_PX = "rdLyrInnerPdPx";
	public static final String PROPERTY_NAME_RD_LYR_SPOKES_CL = "rdLyrSpokesCl";
	public static final String PROPERTY_NAME_RD_LYR_BDR_CL = "rdLyrBdrCl";
	public static final String PROPERTY_NAME_RD_LYR_LBL_SZ = "rdLyrLblSz";
	public static final String PROPERTY_NAME_RD_LYR_START_ANGLE = "rdLyrStartAngle";
	public static final String PROPERTY_NAME_RD_LYR_SPOKES_SZ = "rdLyrSpokesSz";
	public static final String PROPERTY_NAME_RD_LYR_CIRCLE_SZ = "rdLyrCircleSz";
	public static final String PROPERTY_NAME_RD_LYR_BDR_SZ = "rdLyrBdrSz";

	//ERROR
	public static final String PROPERTY_NAME_RD_LYR_FLIP_Y = "rdLyrFlipY";
	public static final String PROPERTY_NAME_RD_LYR_FLIP_X = "rdLyrFlipX";

	//LEGEND
	public static final String PROPERTY_NAME_LGD_MAX_HT = "lgdMaxHt";
	public static final String PROPERTY_NAME_LGD_FONT_FAM = "lgdFontFam";
	public static final String PROPERTY_NAME_LGD_BDR_CL = "lgdBdrCl";
	public static final String PROPERTY_NAME_LGD_BG_CL = "lgdBgCl";
	public static final String PROPERTY_NAME_LGD_NM_SZ = "lgdNmSz";
	public static final String PROPERTY_NAME_LGD_KEY_POS = "lgdKeyPos";
	public static final String PROPERTY_NAME_LGD_HZ_PD = "lgdHzPd";
	public static final String PROPERTY_NAME_LGD_NM_CL = "lgdNmCl";
	public static final String PROPERTY_NAME_LGD_VT_PD = "lgdVtPd";
	public static final String PROPERTY_NAME_LGD_MAX_WD = "lgdMaxWd";
	public static final String PROPERTY_NAME_LGD_LBL_SZ = "lgdLblSz";
	public static final String PROPERTY_NAME_LGD_NM_POS = "lgdNmPos";
	public static final String PROPERTY_NAME_LGD_CHECKBOX_CL = "lgdCbCl";
	public static final String PROPERTY_NAME_LGD_CHECKBOX_CHECK = "lgdCbCk";
	public static final String PROPERTY_NAME_LGD_CHECKBOX_BDR_CL = "lgdCbBdrCl";

	//AXIS
	public static final String PROPERTY_NAME_AX_NUM_FONT_SZ = "axNumFontSz";
	public static final String PROPERTY_NAME_AX_LBL_FONT_SZ = "axLblFontSz";
	public static final String PROPERTY_NAME_AX_MAJ_UNIT_SZ = "axMajUnitSz";
	public static final String PROPERTY_NAME_AX_LINE_CL = "axLineCl";
	public static final String PROPERTY_NAME_AX_NUM_FONT_CL = "axNumFontCl";
	public static final String PROPERTY_NAME_AX_TITLE_PD = "axTitlePd";
	public static final String PROPERTY_NAME_AX_TITLE_ROTATE = "axTitleRotate";
	public static final String PROPERTY_NAME_AX_TITLE_FONT_FAM = "axTitleFontFam";
	public static final String PROPERTY_NAME_AX_NUM_ROTATE = "axNumRotate";
	public static final String PROPERTY_NAME_AX_LBL_TICK_SZ = "axLblTickSz";
	public static final String PROPERTY_NAME_AX_END_PD = "axEndPd";
	public static final String PROPERTY_NAME_AX_MINOR_UNIT_SZ = "axMinorUnitSz";
	public static final String PROPERTY_NAME_AX_LBL_FONT_CL = "axLblFontCl";
	public static final String PROPERTY_NAME_AX_START_PD = "axStartPd";
	public static final String PROPERTY_NAME_AX_TITLE_SZ = "axTitleSz";
	public static final String PROPERTY_NAME_AX_TITLE_CL = "axTitleCl";
	public static final String PROPERTY_NAME_AX_LBL_FONT_FAM = "axLblFontFam";
	public static final String PROPERTY_NAME_AX_NUM_PD = "axNumPd";
	public static final String PROPERTY_NAME_AX_GROUP_PD = "axGroupPd";
	public static final String PROPERTY_NAME_AX_NUM_FONT_FAM = "axNumFontFam";
	public static final String PROPERTY_NAME_AX_LBL_ROTATE = "axLblRotate";
	public static final String PROPERTY_NAME_AX_LBL_PD = "axLblPd";

	//XY GRAPH
	public static final String PROPERTY_NAME_GR_LYR_H_MID_GRD_CL = "grLyrHMidGrdCl";
	public static final String PROPERTY_NAME_GR_LYR_V_MAJ_GRD_CL = "grLyrVMajGrdCl";
	public static final String PROPERTY_NAME_GR_LYR_BDR_CL = "grLyrBdrCl";
	public static final String PROPERTY_NAME_GR_LYR_V_MID_GRD_CL = "grLyrVMidGrdCl";
	public static final String PROPERTY_NAME_GR_LYR_V_GRD_CL = "grLyrVGrdCl";
	public static final String PROPERTY_NAME_GR_LYR_H_MAJ_GRD_CL = "grLyrHMajGrdCl";
	public static final String PROPERTY_NAME_GR_LYR_H_GRD_CL = "grLyrHGrdCl";
	public static final String PROPERTY_NAME_V_GRD_SZ = "vGrdSz";
	public static final String PROPERTY_NAME_H_GRD_SZ = "hGrdSz";
	public static final String PROPERTY_NAME_V_MID_GRD_SZ = "vMidGrdSz";
	public static final String PROPERTY_NAME_H_MID_GRD_SZ = "hMidGrdSz";
	public static final String PROPERTY_NAME_V_MAJ_GRD_SZ = "vMajGrdSz";
	public static final String PROPERTY_NAME_H_MAJ_GRD_SZ = "hMajGrdSz";

	//FILTER
	public static final String PROPERTY_NAME_TITLE_FONT_CL = "titleFontCl";
	public static final String PROPERTY_NAME_TITLE_FONT_SZ = "titleFontSz";
	public static final String PROPERTY_NAME_TITLE_FONT_FAM = "titleFontFam";
	public static final String PROPERTY_NAME_FLD_BORDER_WD = "fldBorderWd";
	public static final String PROPERTY_NAME_FLD_BORDER_RAD = "fldBorderRad";
	public static final String PROPERTY_NAME_FLD_FOCUS_BORDER_WD = "fldFocusBorderWd";
	public static final String PROPERTY_NAME_FLD_FOCUS_BORDER_CL = "fldFocusBorderCl";

	//ALERT DIALOG
	public static final String PROPERTY_NAME_DLG_WD = "dialogWd"; // dialog width
	public static final String PROPERTY_NAME_DLG_HI = "dialogHi"; // dialog height
	public static final String PROPERTY_NAME_DLG_FONT_SZ = "dialogFontSz";
	public static final String PROPERTY_NAME_DLG_FONT_FAM = "dialogFontFam";
	public static final String PROPERTY_NAME_DLG_FLD_FONT_CL = "dialogFldFontCl";
	public static final String PROPERTY_NAME_DLG_FLD_BG_CL = "dialogFldBgCl";
	public static final String PROPERTY_NAME_DLG_TITLE_FONT_FAM = "dialogTitleFontFam";
	public static final String PROPERTY_NAME_DLG_TITLE_FONT_SZ = "dialogTitleFontSz";
	public static final String PROPERTY_NAME_DLG_TITLE_ALG = "dialogTitleAlignment";
	public static final String PROPERTY_NAME_DLG_X_BUTTON_HI = "dialogXButtonHi";
	public static final String PROPERTY_NAME_DLG_X_BUTTON_WD = "dialogXButtonWd";
	public static final String PROPERTY_NAME_DLG_FORM_BUTTON_BG_CL = "dialogFormButtonBgCl";
	public static final String PROPERTY_NAME_DLG_FORM_BUTTON_PNL_BG_CL = "dialogFormButtonPnlBgCl";
	public static final String PROPERTY_NAME_DLG_FORM_BUTTON_FONT_CL = "dialogFormButtonFontCl";
	public static final String PROPERTY_NAME_DLG_FORM_BUTTON_FONT_FAM = "dialogFormButtonFontFam";

	// CALENDAR
	public static final String PROPERTY_NAME_CAL_BG_CL = "calendarBgCl";
	public static final String PROPERTY_NAME_CAL_BTN_BG_CL = "calendarButtonBgCl";
	public static final String PROPERTY_NAME_CAL_YR_FG_CL = "calendarYearFgCl";
	public static final String PROPERTY_NAME_CAL_SEL_YR_FG_CL = "calendarSelectedYearFgCl";
	public static final String PROPERTY_NAME_CAL_MTN_FG_CL = "calendarMonthFgCl";
	public static final String PROPERTY_NAME_CAL_SEL_MTN_FG_CL = "calendarSelectedMonthFgCl";
	public static final String PROPERTY_NAME_CAL_SEL_MTN_BG_CL = "calendarSelectedMonthBgCl";
	public static final String PROPERTY_NAME_CAL_WK_FG_CL = "calendarWeekFgCl";
	public static final String PROPERTY_NAME_CAL_WK_BG_CL = "calendarWeekBgCl";
	public static final String PROPERTY_NAME_CAL_WK_FD_CL = "calendarWeekFdCl";
	public static final String PROPERTY_NAME_CAL_DAY_FG_CL = "calendarDayFgCl";
	public static final String PROPERTY_NAME_CAL_X_DAY_FG_CL = "calendarXDayFgCl";
	public static final String PROPERTY_NAME_CAL_BTN_FG_CL = "calendarButtonFgCl";
	public static final String PROPERTY_NAME_CAL_HOV_BG_CL = "calendarHoverBgCl";

	public static final short CODE_DIV_CL = 1;
	public static final short CODE_FONT_SZ = 2;
	public static final short CODE_BG_CL = 3;
	public static final short CODE_SEL_CL = 4;
	public static final short CODE_FONT_CL = 5;

	//ALL
	public static final short CODE_PD_BDR_CL = 6;
	public static final short CODE_PD_BDR_SZ_PX = 7;
	public static final short CODE_PD_BTM_PX = 8;
	public static final short CODE_PD_CL = 9;
	public static final short CODE_PD_RAD_TP_LF_PX = 10;
	public static final short CODE_PD_RAD_TP_RT_PX = 11;
	public static final short CODE_PD_RAD_BTM_LF_PX = 12;
	public static final short CODE_PD_RAD_BTM_RT_PX = 13;
	public static final short CODE_PD_LF_PX = 14;
	public static final short CODE_PD_RT_PX = 15;
	public static final short CODE_PD_SHADOW_CL = 16;
	public static final short CODE_PD_SHADOW_HZ_PX = 17;
	public static final short CODE_PD_SHADOW_SZ_PX = 18;
	public static final short CODE_PD_SHADOW_VT_PX = 19;
	public static final short CODE_PD_TP_PX = 20;
	public static final short CODE_TITLE_PNL_ALIGN = 21;
	public static final short CODE_TITLE_PNL_FONT_CL = 22;
	public static final short CODE_TITLE_PNL_FONT_SZ = 23;
	public static final short CODE_TITLE_PNL_FONT_FAM = 260;
	public static final short CODE_FONT_STYLE = 265;

	//Table, Filter, Form, Surface, Tree
	public static final short CODE_SCROLL_BTN_CL = 24;
	public static final short CODE_SCROLL_GRIP_CL = 25;
	public static final short CODE_SCROLL_ICONS_CL = 26;
	public static final short CODE_SCROLL_TRACK_CL = 27;
	public static final short CODE_SCROLL_BDR_CL = 28;
	public static final short CODE_SCROLL_WD = 29;
	public static final short CODE_SCROLL_BAR_RADIUS = 306;
	public static final short CODE_SCROLL_BAR_HIDE_ARROWS = 307;
	public static final short CODE_SCROLL_BAR_CORNER_CL = 310;

	//TABLE & TREE
	public static final short CODE_ROW_HT = 30;
	public static final short CODE_HEADER_HT = 31;
	public static final short CODE_HEADER_FONT_SZ = 32;
	public static final short CODE_CELL_BDR_CL = 33;
	public static final short CODE_CELL_PAD_HT = 264;
	public static final short CODE_FILT_BG_CL = 34;
	public static final short CODE_FILT_FONT_CL = 35;
	public static final short CODE_GRAYBAR_CL = 36;
	public static final short CODE_HEADER_BG_CL = 37;
	public static final short CODE_HEADER_DIV_HIDE = 38;
	public static final short CODE_HEADER_FONT_CL = 39;
	public static final short CODE_SEARCH_BG_CL = 40;
	public static final short CODE_SEARCH_BTNS_CL = 41;
	public static final short CODE_SEARCH_FLD_FONT_CL = 42;
	public static final short CODE_SEARCH_HIDE = 43;
	public static final short CODE_CELL_BTM_PX = 44;
	public static final short CODE_CELL_RT_PX = 45;
	public static final short CODE_VT_ALIGN = 46;
	public static final short CODE_FLASH_UP_CL = 303;
	public static final short CODE_FLASH_DN_CL = 304;
	public static final short CODE_FLASH_MILLIS = 305;

	//FILTER & FORM
	public static final short CODE_FLD_BG_CL = 47;
	public static final short CODE_FLD_FONT_CL = 48;
	public static final short CODE_FONT_FAM = 49;
	public static final short CODE_ITALIC = 50;
	public static final short CODE_UNDERLINE = 51;
	public static final short CODE_TXT_ALIGN = 52;
	public static final short CODE_BOLD = 53;

	//FORM
	public static final short CODE_ROTATE = 54;
	public static final short CODE_SHOW_BTM_BTNS = 55;
	public static final short CODE_FLD_LBL_STATUS = 56;
	public static final short CODE_FLD_LBL_SIDE = 57;
	public static final short CODE_FLD_LBL_ALIGN = 58;
	//	public static final short CODE_FLD_PRIM_CL = 59;
	//	public static final short CODE_FLD_SEC_CL = 60;
	//	public static final short CODE_FLD_CSS_CHECK = 61;
	//	public static final short CODE_FLD_CSS_DATE = 62;
	//	public static final short CODE_FLD_CSS_DATE_RNG = 63;
	//	public static final short CODE_FLD_CSS_DATE_TIME = 64;
	//	public static final short CODE_FLD_CSS_DIV = 65;
	//	public static final short CODE_FLD_CSS_BTN = 66;
	//	public static final short CODE_FLD_CSS_IMG = 67;
	//	public static final short CODE_FLD_CSS_MULTI_SEL = 68;
	//	public static final short CODE_FLD_CSS_RNG = 69;
	//	public static final short CODE_FLD_CSS_SEL = 70;
	//	public static final short CODE_FLD_CSS_SUB_RNG = 71;
	//	public static final short CODE_FLD_CSS_TXT_AREA = 72;
	//	public static final short CODE_FLD_CSS_TXT = 73;
	//	public static final short CODE_FLD_CSS_TIME = 74;
	//	public static final short CODE_FLD_CSS_TIME_RNG = 75;
	//	public static final short CODE_FLD_CSS_UPLOAD = 76;
	public static final short CODE_FLD_TRACK_CL = 59;
	public static final short CODE_FLD_GRIP_CL = 60;
	public static final short CODE_FLD_CSS = 61;
	public static final short CODE_FLD_CSS_HELP = 62;
	public static final short CODE_FLD_CSS_LBL = 63;
	public static final short CODE_LBL_PD = 77;
	public static final short CODE_FLD_FONT_FAM = 251;
	public static final short CODE_FLD_FONT_SZ = 252;

	//ALERT DIALOG
	public static final short CODE_DLG_WD = 266;
	public static final short CODE_DLG_HI = 267;
	public static final short CODE_DLG_FONT_SZ = 268;
	public static final short CODE_DLG_FONT_FAM = 269;
	public static final short CODE_DLG_FLD_FONT_CL = 270;
	public static final short CODE_DLG_FLD_BG_CL = 271;
	public static final short CODE_DLG_TITLE_FONT_CL = 273;
	public static final short CODE_DLG_TITLE_FONT_FAM = 274;
	public static final short CODE_DLG_TITLE_FONT_SZ = 275;
	public static final short CODE_DLG_TITLE_FONT_ALG = 276;

	public static final short CODE_DLG_X_BUTTON_HI = 278;
	public static final short CODE_DLG_X_BUTTON_WD = 279;
	public static final short CODE_DLG_FORM_BUTTON_BG_CL = 284;
	public static final short CODE_DLG_FORM_BUTTON_PNL_BG_CL = 318;
	public static final short CODE_DLG_FORM_BUTTON_FONT_CL = 285;
	public static final short CODE_DLG_FORM_BUTTON_FONT_FAM = 286;

	//MAPBOX
	public static final short CODE_SEL_BDR_CL = 78;

	//DIVIDER
	public static final short CODE_DIV_SZ = 79;
	public static final short CODE_DIV_ALIGN = 80;
	public static final short CODE_DIV_HOVER_CL = 81;
	public static final short CODE_DIV_SNAP_SETTING = 82;
	public static final short CODE_DIV_SNAP_POS_PCT = 83;
	public static final short CODE_DIV_LOCK = 84;

	//CHART
	public static final short CODE_SCR_CL = 86;
	public static final short CODE_SERIES_CLS = 87;
	public static final short CODE_GRADIENT = 247;
	public static final short CODE_DIV_THCK_H = 88;
	public static final short CODE_DIV_THCK_V = 89;
	public static final short CODE_SEL_BOX_CL = 90;
	public static final short CODE_SEL_BOX_BDR_CL = 91;
	public static final short CODE_BTM = 92;

	//TREEMAP
	public static final short CODE_GROUP_BDR_CL = 93;
	public static final short CODE_GROUP_FONT_CL = 94;
	public static final short CODE_GROUP_FONT_SZ = 95;
	public static final short CODE_GROUP_BDR_SZ = 96;
	public static final short CODE_SEL_BDR_CL2 = 97;
	public static final short CODE_SEL_BDR_CL1 = 98;
	public static final short CODE_NODE_FONT_SZ = 99;
	public static final short CODE_NODE_BDR_SZ = 100;
	public static final short CODE_GROUP_BG_CL = 101;
	public static final short CODE_NODE_FONT_CL = 102;
	public static final short CODE_NODE_BDR_CL = 103;
	public static final short CODE_NODE_GRADIENT = 246;

	//3D CHART
	public static final short CODE_HIDE_CTRLS = 104;
	public static final short CODE_SCR_X_CL = 105;
	public static final short CODE_SCR_Y_CL = 106;
	public static final short CODE_SCR_Z_CL = 107;
	public static final short CODE_SCR_ZOOM_CL = 108;
	public static final short CODE_SCR_FOV_CL = 109;
	public static final short CODE_SCR_X_POS_CL = 110;
	public static final short CODE_SCR_Y_POS_CL = 111;
	public static final short CODE_CTRL_BTNS_CL = 112;

	//TABLE
	public static final short CODE_SEARCH_FONT_CL = 113;
	public static final short CODE_SEARCH_FLD_CL = 114;
	public static final short CODE_SEARCH_FLD_BG_CL = 115;
	public static final short CODE_ACT_CL = 116;
	public static final short CODE_SEARCH_BAR_DIV_CL = 117;
	public static final short CODE_SEARCH_FLD_BDR_CL = 118;
	public static final short CODE_COLUMN_FILTER_HIDE = 254;
	public static final short CODE_COLUMN_FILTER_HT = 255;
	public static final short CODE_COLUMN_FILTER_BG_CL = 256;
	public static final short CODE_COLUMN_FILTER_FONT_CL = 257;
	public static final short CODE_COLUMN_FILTER_BDR_CL = 258;
	public static final short CODE_COLUMN_FILTER_FONT_SZ = 259;

	//TREE
	public static final short CODE_HEADER_BAR_HIDE = 119;

	//TAB
	public static final short CODE_UNDOCK = 120;
	public static final short CODE_HIDE_TAB_WHEN_POPPEDOUT = 121;
	public static final short CODE_UNSEL_TXT_CL = 122;
	public static final short CODE_VT = 123;
	public static final short CODE_TABS_HIDE = 124;
	public static final short CODE_ROUND_RT = 125;
	public static final short CODE_SPACING = 126;
	public static final short CODE_SEL_TXT_CL = 127;
	public static final short CODE_PAD_TP = 128;
	public static final short CODE_PAD_BTM = 129;
	public static final short CODE_PAD_START = 130;
	public static final short CODE_ROUND_LF = 131;
	public static final short CODE_UNSEL_CL = 132;
	public static final short CODE_TAB_HT = 133;
	public static final short CODE_RT = 134;
	public static final short CODE_HASADDBUTTON = 248;
	public static final short CODE_ADDBUTTON_CL = 249;
	public static final short CODE_HIDE_EXPORT_SS_MENUITEM = 250;
	public static final short CODE_TAB_BDR_CL = 253;
	public static final short CODE_SEL_TAB_BDR_CL = 308;
	public static final short CODE_SEL_TAB_BDR_SZ = 309;
	public static final short CODE_HIDE_ARRANGE_TAB_MENUITEM = 302;

	//DESKTOP
	public static final short CODE_DESK_BG_CL = 135;
	public static final short CODE_USR_WIN_BTN_ICON_CL = 136;
	public static final short CODE_BTN_ICON_CL = 137;
	public static final short CODE_USR_WIN_BTN_CL = 138;
	public static final short CODE_USR_WIN_BTN_DOWN_CL = 139;
	public static final short CODE_USR_WIN_TXT_CL = 140;
	public static final short CODE_USR_WIN_FD_LBL_FN_CL = 311;
	public static final short CODE_USR_WIN_FD_BG_CL = 312;
	public static final short CODE_USR_WIN_FD_BRD_CL = 315;
	public static final short CODE_USR_WIN_FD_VAL_FG_CL = 313;
	public static final short CODE_POPOUTS = 141;
	public static final short CODE_USR_WIN_CL = 142;
	public static final short CODE_USR_WIN_DOWN_CL = 143;
	public static final short CODE_WIN_BG_CL = 144;
	public static final short CODE_USR_WIN_BTN_UP_CL = 145;
	public static final short CODE_HELP_FONT_CL = 146;
	public static final short CODE_USR_WIN_UP_CL = 147;
	public static final short CODE_BTN_BG_CL = 148;
	public static final short CODE_WIN_FONT_CL = 149;
	public static final short CODE_WIN_FONT_SZ = 150;
	public static final short CODE_WIN_FONT_FAM = 151;
	public static final short CODE_WIN_CL_TP_LF = 152;
	public static final short CODE_WIN_CL_BTM_RT = 153;
	public static final short CODE_WIN_HEADER_PD = 154;
	public static final short CODE_WIN_BDR_SIZE = 155;
	public static final short CODE_TITLE_BAR_BDR_CL = 156;
	public static final short CODE_HELP_BG_CL = 157;
	public static final short CODE_BTN_BDR_CL = 158;
	public static final short CODE_USR_WIN_BTN_FONT_CL = 314;
	public static final short CODE_BTN_SHDW_CL = 159;
	public static final short CODE_USR_WIN_HEADER_SZ = 160;
	public static final short CODE_USR_WIN_BDR_SZ = 161;
	public static final short CODE_USR_WIN_FORM_BG_CL = 162;
	public static final short CODE_USR_WIN_FORM_BTN_PANEL_CL = 163;
	public static final short CODE_USR_WIN_INNER_BDR_SZ = 164;
	public static final short CODE_USR_WIN_OUTER_BDR_SZ = 165;
	public static final short CODE_WAIT_LINE_CL = 166;
	public static final short CODE_WAIT_BG_CL = 167;
	public static final short CODE_WAIT_FILL_CL = 168;
	public static final short CODE_MENU_DIV_CL = 169;
	public static final short CODE_MENU_FONT_CL = 170;
	public static final short CODE_MENU_BG_CL = 171;
	public static final short CODE_MENU_DISABLED_BG_CL = 172;
	public static final short CODE_MENU_DISABLED_FONT_CL = 173;
	public static final short CODE_MENU_BORDER_TP_LF_CL = 244;
	public static final short CODE_MENU_BORDER_BTM_RT_CL = 245;
	public static final short CODE_MENU_HOVER_BG_CL = 316;
	public static final short CODE_MENU_HOVER_FONT_CL = 317;

	//RADIAL GRAPH
	public static final short CODE_RD_LYR_CENTER_X = 174;
	public static final short CODE_RD_LYR_END_ANGLE = 175;
	public static final short CODE_RD_LYR_CENTER_Y = 176;
	public static final short CODE_RD_LYR_SPOKES_COUNT = 177;
	public static final short CODE_RD_LYR_OUTER_PD_PX = 178;
	public static final short CODE_RD_LYR_LBL_ANGLE = 179;
	public static final short CODE_RD_LYR_CIRCLES_COUNT = 180;
	public static final short CODE_RD_LYR_LBL_CL = 181;
	public static final short CODE_RD_LYR_CIRCLE_CL = 182;
	public static final short CODE_RD_LYR_INNER_PD_PX = 183;
	public static final short CODE_RD_LYR_SPOKES_CL = 184;
	public static final short CODE_RD_LYR_BDR_CL = 185;
	public static final short CODE_RD_LYR_LBL_SZ = 186;
	public static final short CODE_RD_LYR_START_ANGLE = 187;
	public static final short CODE_RD_LYR_SPOKES_SZ = 188;
	public static final short CODE_RD_LYR_CIRCLE_SZ = 189;
	public static final short CODE_RD_LYR_BDR_SZ = 190;

	//ERROR
	public static final short CODE_RD_LYR_FLIP_Y = 191;
	public static final short CODE_RD_LYR_FLIP_X = 192;

	//LEGEND
	public static final short CODE_LGD_MAX_HT = 193;
	public static final short CODE_LGD_FONT_FAM = 194;
	public static final short CODE_LGD_BDR_CL = 195;
	public static final short CODE_LGD_BG_CL = 196;
	public static final short CODE_LGD_NM_SZ = 197;
	public static final short CODE_LGD_KEY_POS = 198;
	public static final short CODE_LGD_HZ_PD = 199;
	public static final short CODE_LGD_NM_CL = 200;
	public static final short CODE_LGD_VT_PD = 201;
	public static final short CODE_LGD_MAX_WD = 202;
	public static final short CODE_LGD_LBL_SZ = 203;
	public static final short CODE_LGD_NM_POS = 204;
	public static final short CODE_LGD_CHECKBOX_CL = 261;
	public static final short CODE_LGD_CHECKBOX_CHECK_CL = 262;
	public static final short CODE_LGD_CHECKBOX_BDR_CL = 263;

	//AXIS
	public static final short CODE_AX_NUM_FONT_SZ = 205;
	public static final short CODE_AX_LBL_FONT_SZ = 206;
	public static final short CODE_AX_MAJ_UNIT_SZ = 207;
	public static final short CODE_AX_LINE_CL = 208;
	public static final short CODE_AX_NUM_FONT_CL = 209;
	public static final short CODE_AX_TITLE_PD = 210;
	public static final short CODE_AX_TITLE_ROTATE = 211;
	public static final short CODE_AX_TITLE_FONT_FAM = 212;
	public static final short CODE_AX_NUM_ROTATE = 213;
	public static final short CODE_AX_LBL_TICK_SZ = 214;
	public static final short CODE_AX_END_PD = 215;
	public static final short CODE_AX_MINOR_UNIT_SZ = 216;
	public static final short CODE_AX_LBL_FONT_CL = 217;
	public static final short CODE_AX_START_PD = 218;
	public static final short CODE_AX_TITLE_SZ = 219;
	public static final short CODE_AX_TITLE_CL = 220;
	public static final short CODE_AX_LBL_FONT_FAM = 221;
	public static final short CODE_AX_NUM_PD = 222;
	public static final short CODE_AX_GROUP_PD = 223;
	public static final short CODE_AX_NUM_FONT_FAM = 224;
	public static final short CODE_AX_LBL_ROTATE = 225;
	public static final short CODE_AX_LBL_PD = 226;

	//XY GRAPH
	public static final short CODE_GR_LYR_H_MID_GRD_CL = 227;
	public static final short CODE_GR_LYR_V_MAJ_GRD_CL = 228;
	public static final short CODE_GR_LYR_BDR_CL = 229;
	public static final short CODE_GR_LYR_V_MID_GRD_CL = 230;
	public static final short CODE_GR_LYR_V_GRD_CL = 231;
	public static final short CODE_GR_LYR_H_MAJ_GRD_CL = 232;
	public static final short CODE_GR_LYR_H_GRD_CL = 233;
	public static final short CODE_V_GRD_SZ = 234;
	public static final short CODE_H_GRD_SZ = 235;
	public static final short CODE_V_MID_GRD_SZ = 236;
	public static final short CODE_H_MID_GRD_SZ = 237;
	public static final short CODE_V_MAJ_GRD_SZ = 238;
	public static final short CODE_H_MAJ_GRD_SZ = 239;;
	//FILTER
	public static final short CODE_TITLE_FONT_CL = 240;
	public static final short CODE_TITLE_FONT_SZ = 241;
	public static final short CODE_TITLE_FONT_FAM = 242;
	public static final short CODE_FLD_BDR_CL = 243;
	public static final short CODE_FLD_BDR_RAD = 287;
	public static final short CODE_FLD_BDR_WD = 288;
	public static final short CODE_FLD_FCS_BDR_CL = 319;
	public static final short CODE_FLD_FCS_BDR_WD = 320;
	//CALENDAR
	public static final short CODE_CAL_BG_CL = 289;
	public static final short CODE_CAL_BTN_BG_CL = 290;

	public static final short CODE_CAL_YR_FG_CL = 291;
	public static final short CODE_CAL_SEL_YR_FG_CL = 292;
	public static final short CODE_CAL_MTN_FG_CL = 293;
	public static final short CODE_CAL_SEL_MTN_FG_CL = 294;
	public static final short CODE_CAL_SEL_MTN_BG_CL = 295;
	public static final short CODE_CAL_WK_FG_CL = 296;
	public static final short CODE_CAL_WK_BG_CL = 297;
	public static final short CODE_CAL_WK_FD_CL = 64;
	public static final short CODE_CAL_DAY_FG_CL = 298;
	public static final short CODE_CAL_X_DAY_FG_CL = 299;
	public static final short CODE_CAL_BTN_FG_CL = 300;
	public static final short CODE_CAL_HOV_BG_CL = 301;

	// Code used up to 321
	public static final short MISSING_CODE = -1;

	static {
		ADD(PROPERTY_NAME_DIV_CL, CODE_DIV_CL);
		ADD(PROPERTY_NAME_FONT_SZ, CODE_FONT_SZ);
		ADD(PROPERTY_NAME_BG_CL, CODE_BG_CL);
		ADD(PROPERTY_NAME_SEL_CL, CODE_SEL_CL);
		ADD(PROPERTY_NAME_FONT_CL, CODE_FONT_CL);
		ADD(PROPERTY_NAME_PD_BDR_CL, CODE_PD_BDR_CL);
		ADD(PROPERTY_NAME_PD_BDR_SZ_PX, CODE_PD_BDR_SZ_PX);
		ADD(PROPERTY_NAME_PD_BTM_PX, CODE_PD_BTM_PX);
		ADD(PROPERTY_NAME_PD_CL, CODE_PD_CL);
		ADD(PROPERTY_NAME_PD_RAD_TP_LF_PX, CODE_PD_RAD_TP_LF_PX);
		ADD(PROPERTY_NAME_PD_RAD_TP_RT_PX, CODE_PD_RAD_TP_RT_PX);
		ADD(PROPERTY_NAME_PD_RAD_BTM_LF_PX, CODE_PD_RAD_BTM_LF_PX);
		ADD(PROPERTY_NAME_PD_RAD_BTM_RT_PX, CODE_PD_RAD_BTM_RT_PX);
		ADD(PROPERTY_NAME_PD_LF_PX, CODE_PD_LF_PX);
		ADD(PROPERTY_NAME_PD_RT_PX, CODE_PD_RT_PX);
		ADD(PROPERTY_NAME_PD_SHADOW_CL, CODE_PD_SHADOW_CL);
		ADD(PROPERTY_NAME_PD_SHADOW_HZ_PX, CODE_PD_SHADOW_HZ_PX);
		ADD(PROPERTY_NAME_PD_SHADOW_SZ_PX, CODE_PD_SHADOW_SZ_PX);
		ADD(PROPERTY_NAME_PD_SHADOW_VT_PX, CODE_PD_SHADOW_VT_PX);
		ADD(PROPERTY_NAME_PD_TP_PX, CODE_PD_TP_PX);
		ADD(PROPERTY_NAME_TITLE_PNL_ALIGN, CODE_TITLE_PNL_ALIGN);
		ADD(PROPERTY_NAME_TITLE_PNL_FONT_CL, CODE_TITLE_PNL_FONT_CL);
		ADD(PROPERTY_NAME_TITLE_PNL_FONT_SZ, CODE_TITLE_PNL_FONT_SZ);
		ADD(PROPERTY_NAME_TITLE_PNL_FONT_FAM, CODE_TITLE_PNL_FONT_FAM);
		ADD(PROPERTY_NAME_SCROLL_BTN_CL, CODE_SCROLL_BTN_CL);
		ADD(PROPERTY_NAME_SCROLL_GRIP_CL, CODE_SCROLL_GRIP_CL);
		ADD(PROPERTY_NAME_SCROLL_ICONS_CL, CODE_SCROLL_ICONS_CL);
		ADD(PROPERTY_NAME_SCROLL_TRACK_CL, CODE_SCROLL_TRACK_CL);
		ADD(PROPERTY_NAME_SCROLL_BDR_CL, CODE_SCROLL_BDR_CL);
		ADD(PROPERTY_NAME_SCROLL_WD, CODE_SCROLL_WD);
		ADD(PROPERTY_NAME_ROW_HT, CODE_ROW_HT);
		ADD(PROPERTY_NAME_HEADER_HT, CODE_HEADER_HT);
		ADD(PROPERTY_NAME_HEADER_FONT_SZ, CODE_HEADER_FONT_SZ);
		ADD(PROPERTY_NAME_CELL_BDR_CL, CODE_CELL_BDR_CL);
		ADD(PROPERTY_NAME_CELL_PAD_HT, CODE_CELL_PAD_HT);
		ADD(PROPERTY_NAME_FILT_BG_CL, CODE_FILT_BG_CL);
		ADD(PROPERTY_NAME_FILT_FONT_CL, CODE_FILT_FONT_CL);
		ADD(PROPERTY_NAME_GRAYBAR_CL, CODE_GRAYBAR_CL);
		ADD(PROPERTY_NAME_HEADER_BG_CL, CODE_HEADER_BG_CL);
		ADD(PROPERTY_NAME_HEADER_DIV_HIDE, CODE_HEADER_DIV_HIDE);
		ADD(PROPERTY_NAME_HEADER_FONT_CL, CODE_HEADER_FONT_CL);
		ADD(PROPERTY_NAME_SEARCH_BG_CL, CODE_SEARCH_BG_CL);
		ADD(PROPERTY_NAME_SEARCH_BTNS_CL, CODE_SEARCH_BTNS_CL);
		ADD(PROPERTY_NAME_SEARCH_FLD_FONT_CL, CODE_SEARCH_FLD_FONT_CL);
		ADD(PROPERTY_NAME_SEARCH_HIDE, CODE_SEARCH_HIDE);
		ADD(PROPERTY_NAME_CELL_BTM_PX, CODE_CELL_BTM_PX);
		ADD(PROPERTY_NAME_CELL_RT_PX, CODE_CELL_RT_PX);
		ADD(PROPERTY_NAME_VT_ALIGN, CODE_VT_ALIGN);
		ADD(PROPERTY_NAME_FLASH_UP_CL, CODE_FLASH_UP_CL);
		ADD(PROPERTY_NAME_FLASH_DN_CL, CODE_FLASH_DN_CL);
		ADD(PROPERTY_NAME_FLASH_MILLIS, CODE_FLASH_MILLIS);
		ADD(PROPERTY_NAME_FLD_BG_CL, CODE_FLD_BG_CL);
		ADD(PROPERTY_NAME_FLD_FONT_CL, CODE_FLD_FONT_CL);
		ADD(PROPERTY_NAME_FONT_FAM, CODE_FONT_FAM);
		ADD(PROPERTY_NAME_ITALIC, CODE_ITALIC);
		ADD(PROPERTY_NAME_UNDERLINE, CODE_UNDERLINE);
		ADD(PROPERTY_NAME_TXT_ALIGN, CODE_TXT_ALIGN);
		ADD(PROPERTY_NAME_BOLD, CODE_BOLD);
		ADD(PROPERTY_NAME_ROTATE, CODE_ROTATE);
		ADD(PROPERTY_NAME_SHOW_BTM_BTNS, CODE_SHOW_BTM_BTNS);
		ADD(PROPERTY_NAME_FLD_LBL_STATUS, CODE_FLD_LBL_STATUS);
		ADD(PROPERTY_NAME_FLD_LBL_SIDE, CODE_FLD_LBL_SIDE);
		ADD(PROPERTY_NAME_FLD_LBL_ALIGN, CODE_FLD_LBL_ALIGN);
		ADD(PROPERTY_NAME_FLD_TRACK_CL, CODE_FLD_TRACK_CL);
		ADD(PROPERTY_NAME_FLD_GRIP_CL, CODE_FLD_GRIP_CL);
		//		ADD(PROPERTY_NAME_FLD_CSS_CHECK, CODE_FLD_CSS_CHECK);
		//		ADD(PROPERTY_NAME_FLD_CSS_DATE, CODE_FLD_CSS_DATE);
		//		ADD(PROPERTY_NAME_FLD_CSS_DATE_RNG, CODE_FLD_CSS_DATE_RNG);
		//		ADD(PROPERTY_NAME_FLD_CSS_DATE_TIME, CODE_FLD_CSS_DATE_TIME);
		//		ADD(PROPERTY_NAME_FLD_CSS_DIV, CODE_FLD_CSS_DIV);
		//		ADD(PROPERTY_NAME_FLD_CSS_BTN, CODE_FLD_CSS_BTN);
		//		ADD(PROPERTY_NAME_FLD_CSS_IMG, CODE_FLD_CSS_IMG);
		//		ADD(PROPERTY_NAME_FLD_CSS_MULTI_SEL, CODE_FLD_CSS_MULTI_SEL);
		//		ADD(PROPERTY_NAME_FLD_CSS_RNG, CODE_FLD_CSS_RNG);
		//		ADD(PROPERTY_NAME_FLD_CSS_SEL, CODE_FLD_CSS_SEL);
		//		ADD(PROPERTY_NAME_FLD_CSS_SUB_RNG, CODE_FLD_CSS_SUB_RNG);
		//		ADD(PROPERTY_NAME_FLD_CSS_TXT_AREA, CODE_FLD_CSS_TXT_AREA);
		//		ADD(PROPERTY_NAME_FLD_CSS_TXT, CODE_FLD_CSS_TXT);
		//		ADD(PROPERTY_NAME_FLD_CSS_TIME, CODE_FLD_CSS_TIME);
		//		ADD(PROPERTY_NAME_FLD_CSS_TIME_RNG, CODE_FLD_CSS_TIME_RNG);
		//		ADD(PROPERTY_NAME_FLD_CSS_UPLOAD, CODE_FLD_CSS_UPLOAD);
		ADD(PROPERTY_NAME_FLD_CSS, CODE_FLD_CSS);
		ADD(PROPERTY_NAME_FLD_CSS_HELP, CODE_FLD_CSS_HELP);
		ADD(PROPERTY_NAME_FLD_CSS_LBL, CODE_FLD_CSS_LBL);
		ADD(PROPERTY_NAME_LBL_PD, CODE_LBL_PD);
		ADD(PROPERTY_NAME_SEL_BDR_CL, CODE_SEL_BDR_CL);
		ADD(PROPERTY_NAME_DIV_SZ, CODE_DIV_SZ);
		ADD(PROPERTY_NAME_DIV_ALIGN, CODE_DIV_ALIGN);
		ADD(PROPERTY_NAME_DIV_HOVER_CL, CODE_DIV_HOVER_CL);
		ADD(PROPERTY_NAME_DIV_SNAP_SETTING, CODE_DIV_SNAP_SETTING);
		ADD(PROPERTY_NAME_DIV_SNAP_POS_PCT, CODE_DIV_SNAP_POS_PCT);
		ADD(PROPERTY_NAME_DIV_LOCK, CODE_DIV_LOCK);
		ADD(PROPERTY_NAME_SCR_CL, CODE_SCR_CL);
		ADD(PROPERTY_NAME_SERIES_CLS, CODE_SERIES_CLS);
		ADD(PROPERTY_NAME_GRADIENT, CODE_GRADIENT);
		ADD(PROPERTY_NAME_DIV_THCK_H, CODE_DIV_THCK_H);
		ADD(PROPERTY_NAME_DIV_THCK_V, CODE_DIV_THCK_V);
		ADD(PROPERTY_NAME_SEL_BOX_CL, CODE_SEL_BOX_CL);
		ADD(PROPERTY_NAME_SEL_BOX_BDR_CL, CODE_SEL_BOX_BDR_CL);
		ADD(PROPERTY_NAME_BTM, CODE_BTM);
		ADD(PROPERTY_NAME_GROUP_BDR_CL, CODE_GROUP_BDR_CL);
		ADD(PROPERTY_NAME_GROUP_FONT_CL, CODE_GROUP_FONT_CL);
		ADD(PROPERTY_NAME_GROUP_FONT_SZ, CODE_GROUP_FONT_SZ);
		ADD(PROPERTY_NAME_GROUP_BDR_SZ, CODE_GROUP_BDR_SZ);
		ADD(PROPERTY_NAME_SEL_BDR_CL2, CODE_SEL_BDR_CL2);
		ADD(PROPERTY_NAME_SEL_BDR_CL1, CODE_SEL_BDR_CL1);
		ADD(PROPERTY_NAME_NODE_FONT_SZ, CODE_NODE_FONT_SZ);
		ADD(PROPERTY_NAME_NODE_BDR_SZ, CODE_NODE_BDR_SZ);
		ADD(PROPERTY_NAME_GROUP_BG_CL, CODE_GROUP_BG_CL);
		ADD(PROPERTY_NAME_NODE_FONT_CL, CODE_NODE_FONT_CL);
		ADD(PROPERTY_NAME_NODE_BDR_CL, CODE_NODE_BDR_CL);
		ADD(PROPERTY_NAME_NODE_GRADIENT, CODE_NODE_GRADIENT);
		ADD(PROPERTY_NAME_HIDE_CTRLS, CODE_HIDE_CTRLS);
		ADD(PROPERTY_NAME_SCR_X_CL, CODE_SCR_X_CL);
		ADD(PROPERTY_NAME_SCR_Y_CL, CODE_SCR_Y_CL);
		ADD(PROPERTY_NAME_SCR_Z_CL, CODE_SCR_Z_CL);
		ADD(PROPERTY_NAME_SCR_ZOOM_CL, CODE_SCR_ZOOM_CL);
		ADD(PROPERTY_NAME_SCR_FOV_CL, CODE_SCR_FOV_CL);
		ADD(PROPERTY_NAME_SCR_X_POS_CL, CODE_SCR_X_POS_CL);
		ADD(PROPERTY_NAME_SCR_Y_POS_CL, CODE_SCR_Y_POS_CL);
		ADD(PROPERTY_NAME_CTRL_BTNS_CL, CODE_CTRL_BTNS_CL);
		ADD(PROPERTY_NAME_SEARCH_FONT_CL, CODE_SEARCH_FONT_CL);
		ADD(PROPERTY_NAME_SEARCH_FLD_CL, CODE_SEARCH_FLD_CL);
		ADD(PROPERTY_NAME_SEARCH_FLD_BG_CL, CODE_SEARCH_FLD_BG_CL);
		ADD(PROPERTY_NAME_ACT_CL, CODE_ACT_CL);
		ADD(PROPERTY_NAME_SEARCH_BAR_DIV_CL, CODE_SEARCH_BAR_DIV_CL);
		ADD(PROPERTY_NAME_SEARCH_FLD_BDR_CL, CODE_SEARCH_FLD_BDR_CL);
		ADD(PROPERTY_NAME_HEADER_BAR_HIDE, CODE_HEADER_BAR_HIDE);
		ADD(PROPERTY_NAME_COLUMN_FILTER_HIDE, CODE_COLUMN_FILTER_HIDE);
		ADD(PROPERTY_NAME_COLUMN_FILTER_HEIGHT, CODE_COLUMN_FILTER_HT);
		ADD(PROPERTY_NAME_COLUMN_FILTER_BG_CL, CODE_COLUMN_FILTER_BG_CL);
		ADD(PROPERTY_NAME_COLUMN_FILTER_FONT_CL, CODE_COLUMN_FILTER_FONT_CL);
		ADD(PROPERTY_NAME_COLUMN_FILTER_FONT_SZ, CODE_COLUMN_FILTER_FONT_SZ);
		ADD(PROPERTY_NAME_COLUMN_FILTER_BDR_CL, CODE_COLUMN_FILTER_BDR_CL);
		ADD(PROPERTY_NAME_UNDOCK, CODE_UNDOCK);
		ADD(PROPERTY_NAME_HIDE_TAB_WHEN_POPPEDOUT, CODE_HIDE_TAB_WHEN_POPPEDOUT);
		ADD(PROPERTY_NAME_UNSEL_TXT_CL, CODE_UNSEL_TXT_CL);
		ADD(PROPERTY_NAME_VT, CODE_VT);
		ADD(PROPERTY_NAME_HASADDBUTTON, CODE_HASADDBUTTON);
		ADD(PROPERTY_NAME_HIDE_EXPORT_SS_MENUITEM, CODE_HIDE_EXPORT_SS_MENUITEM);
		ADD(PROPERTY_NAME_HIDE_ARRANGE_TAB_MENUITEM, CODE_HIDE_ARRANGE_TAB_MENUITEM);
		ADD(PROPERTY_NAME_TAB_BDR_CL, CODE_TAB_BDR_CL);
		ADD(PROPERTY_NAME_SEL_TAB_BDR_CL, CODE_SEL_TAB_BDR_CL);
		ADD(PROPERTY_NAME_SEL_TAB_BDR_SZ, CODE_SEL_TAB_BDR_SZ);
		ADD(PROPERTY_NAME_ADDBUTTON_CL, CODE_ADDBUTTON_CL);
		ADD(PROPERTY_NAME_TABS_HIDE, CODE_TABS_HIDE);
		ADD(PROPERTY_NAME_ROUND_RT, CODE_ROUND_RT);
		ADD(PROPERTY_NAME_SPACING, CODE_SPACING);
		ADD(PROPERTY_NAME_SEL_TXT_CL, CODE_SEL_TXT_CL);
		ADD(PROPERTY_NAME_PAD_TP, CODE_PAD_TP);
		ADD(PROPERTY_NAME_PAD_BTM, CODE_PAD_BTM);
		ADD(PROPERTY_NAME_PAD_START, CODE_PAD_START);
		ADD(PROPERTY_NAME_ROUND_LF, CODE_ROUND_LF);
		ADD(PROPERTY_NAME_UNSEL_CL, CODE_UNSEL_CL);
		ADD(PROPERTY_NAME_TAB_HT, CODE_TAB_HT);
		ADD(PROPERTY_NAME_RT, CODE_RT);
		ADD(PROPERTY_NAME_DESK_BG_CL, CODE_DESK_BG_CL);
		ADD(PROPERTY_NAME_USR_WIN_BTN_ICON_CL, CODE_USR_WIN_BTN_ICON_CL);
		ADD(PROPERTY_NAME_BTN_ICON_CL, CODE_BTN_ICON_CL);
		ADD(PROPERTY_NAME_USR_WIN_BTN_CL, CODE_USR_WIN_BTN_CL);
		ADD(PROPERTY_NAME_USR_WIN_BTN_DOWN_CL, CODE_USR_WIN_BTN_DOWN_CL);
		ADD(PROPERTY_NAME_USR_WIN_TXT_CL, CODE_USR_WIN_TXT_CL);
		ADD(PROPERTY_NAME_USR_WIN_FD_BG_CL, CODE_USR_WIN_FD_BG_CL);
		ADD(PROPERTY_NAME_USR_WIN_FD_BDR_CL, CODE_USR_WIN_FD_BRD_CL);
		ADD(PROPERTY_NAME_USR_WIN_FD_LBL_FN_CL, CODE_USR_WIN_FD_LBL_FN_CL);
		ADD(PROPERTY_NAME_USR_WIN_FD_VAL_FG_CL, CODE_USR_WIN_FD_VAL_FG_CL);
		ADD(PROPERTY_NAME_POPOUTS, CODE_POPOUTS);
		ADD(PROPERTY_NAME_USR_WIN_CL, CODE_USR_WIN_CL);
		ADD(PROPERTY_NAME_USR_WIN_DOWN_CL, CODE_USR_WIN_DOWN_CL);
		ADD(PROPERTY_NAME_WIN_BG_CL, CODE_WIN_BG_CL);
		ADD(PROPERTY_NAME_USR_WIN_BTN_UP_CL, CODE_USR_WIN_BTN_UP_CL);
		ADD(PROPERTY_NAME_HELP_FONT_CL, CODE_HELP_FONT_CL);
		ADD(PROPERTY_NAME_USR_WIN_UP_CL, CODE_USR_WIN_UP_CL);
		ADD(PROPERTY_NAME_BTN_BG_CL, CODE_BTN_BG_CL);
		ADD(PROPERTY_NAME_WIN_FONT_CL, CODE_WIN_FONT_CL);
		ADD(PROPERTY_NAME_WIN_FONT_SZ, CODE_WIN_FONT_SZ);
		ADD(PROPERTY_NAME_WIN_FONT_FAM, CODE_WIN_FONT_FAM);
		ADD(PROPERTY_NAME_WIN_CL_TP_LF, CODE_WIN_CL_TP_LF);
		ADD(PROPERTY_NAME_WIN_CL_BTM_RT, CODE_WIN_CL_BTM_RT);
		ADD(PROPERTY_NAME_WIN_HEADER_PD, CODE_WIN_HEADER_PD);
		ADD(PROPERTY_NAME_WIN_BDR_SIZE, CODE_WIN_BDR_SIZE);
		ADD(PROPERTY_NAME_TITLE_BAR_BDR_CL, CODE_TITLE_BAR_BDR_CL);
		ADD(PROPERTY_NAME_HELP_BG_CL, CODE_HELP_BG_CL);
		ADD(PROPERTY_NAME_BTN_BDR_CL, CODE_BTN_BDR_CL);
		ADD(PROPERTY_NAME_USR_WIN_BTN_FONT_CL, CODE_USR_WIN_BTN_FONT_CL);
		ADD(PROPERTY_NAME_BTN_SHDW_CL, CODE_BTN_SHDW_CL);
		ADD(PROPERTY_NAME_USR_WIN_HEADER_SZ, CODE_USR_WIN_HEADER_SZ);
		ADD(PROPERTY_NAME_USR_WIN_BDR_SZ, CODE_USR_WIN_BDR_SZ);
		ADD(PROPERTY_NAME_USR_WIN_FORM_BG_CL, CODE_USR_WIN_FORM_BG_CL);
		ADD(PROPERTY_NAME_USR_WIN_FORM_BTN_PANEL_CL, CODE_USR_WIN_FORM_BTN_PANEL_CL);
		ADD(PROPERTY_NAME_USR_WIN_INNER_BDR_SZ, CODE_USR_WIN_INNER_BDR_SZ);
		ADD(PROPERTY_NAME_USR_WIN_OUTER_BDR_SZ, CODE_USR_WIN_OUTER_BDR_SZ);
		ADD(PROPERTY_NAME_WAIT_LINE_CL, CODE_WAIT_LINE_CL);
		ADD(PROPERTY_NAME_WAIT_BG_CL, CODE_WAIT_BG_CL);
		ADD(PROPERTY_NAME_WAIT_FILL_CL, CODE_WAIT_FILL_CL);
		ADD(PROPERTY_NAME_MENU_DIV_CL, CODE_MENU_DIV_CL);
		ADD(PROPERTY_NAME_MENU_FONT_CL, CODE_MENU_FONT_CL);
		ADD(PROPERTY_NAME_MENU_BG_CL, CODE_MENU_BG_CL);
		ADD(PROPERTY_NAME_MENU_DISABLED_BG_CL, CODE_MENU_DISABLED_BG_CL);
		ADD(PROPERTY_NAME_MENU_DISABLED_FONT_CL, CODE_MENU_DISABLED_FONT_CL);
		ADD(PROPERTY_NAME_MENU_BORDER_TOP_LEFT_CL, CODE_MENU_BORDER_TP_LF_CL);
		ADD(PROPERTY_NAME_MENU_BORDER_BOTTOM_RIGHT_CL, CODE_MENU_BORDER_BTM_RT_CL);
		ADD(PROPERTY_NAME_MENU_HOVER_BG_CL, CODE_MENU_HOVER_BG_CL);
		ADD(PROPERTY_NAME_MENU_HOVER_FONT_CL, CODE_MENU_HOVER_FONT_CL);
		ADD(PROPERTY_NAME_RD_LYR_CENTER_X, CODE_RD_LYR_CENTER_X);
		ADD(PROPERTY_NAME_RD_LYR_END_ANGLE, CODE_RD_LYR_END_ANGLE);
		ADD(PROPERTY_NAME_RD_LYR_CENTER_Y, CODE_RD_LYR_CENTER_Y);
		ADD(PROPERTY_NAME_RD_LYR_SPOKES_COUNT, CODE_RD_LYR_SPOKES_COUNT);
		ADD(PROPERTY_NAME_RD_LYR_OUTER_PD_PX, CODE_RD_LYR_OUTER_PD_PX);
		ADD(PROPERTY_NAME_RD_LYR_LBL_ANGLE, CODE_RD_LYR_LBL_ANGLE);
		ADD(PROPERTY_NAME_RD_LYR_CIRCLES_COUNT, CODE_RD_LYR_CIRCLES_COUNT);
		ADD(PROPERTY_NAME_RD_LYR_LBL_CL, CODE_RD_LYR_LBL_CL);
		ADD(PROPERTY_NAME_RD_LYR_CIRCLE_CL, CODE_RD_LYR_CIRCLE_CL);
		ADD(PROPERTY_NAME_RD_LYR_INNER_PD_PX, CODE_RD_LYR_INNER_PD_PX);
		ADD(PROPERTY_NAME_RD_LYR_SPOKES_CL, CODE_RD_LYR_SPOKES_CL);
		ADD(PROPERTY_NAME_RD_LYR_BDR_CL, CODE_RD_LYR_BDR_CL);
		ADD(PROPERTY_NAME_RD_LYR_LBL_SZ, CODE_RD_LYR_LBL_SZ);
		ADD(PROPERTY_NAME_RD_LYR_START_ANGLE, CODE_RD_LYR_START_ANGLE);
		ADD(PROPERTY_NAME_RD_LYR_SPOKES_SZ, CODE_RD_LYR_SPOKES_SZ);
		ADD(PROPERTY_NAME_RD_LYR_CIRCLE_SZ, CODE_RD_LYR_CIRCLE_SZ);
		ADD(PROPERTY_NAME_RD_LYR_BDR_SZ, CODE_RD_LYR_BDR_SZ);
		ADD(PROPERTY_NAME_RD_LYR_FLIP_Y, CODE_RD_LYR_FLIP_Y);
		ADD(PROPERTY_NAME_RD_LYR_FLIP_X, CODE_RD_LYR_FLIP_X);
		ADD(PROPERTY_NAME_LGD_MAX_HT, CODE_LGD_MAX_HT);
		ADD(PROPERTY_NAME_LGD_FONT_FAM, CODE_LGD_FONT_FAM);
		ADD(PROPERTY_NAME_LGD_BDR_CL, CODE_LGD_BDR_CL);
		ADD(PROPERTY_NAME_LGD_BG_CL, CODE_LGD_BG_CL);
		ADD(PROPERTY_NAME_LGD_NM_SZ, CODE_LGD_NM_SZ);
		ADD(PROPERTY_NAME_LGD_KEY_POS, CODE_LGD_KEY_POS);
		ADD(PROPERTY_NAME_LGD_HZ_PD, CODE_LGD_HZ_PD);
		ADD(PROPERTY_NAME_LGD_NM_CL, CODE_LGD_NM_CL);
		ADD(PROPERTY_NAME_LGD_VT_PD, CODE_LGD_VT_PD);
		ADD(PROPERTY_NAME_LGD_MAX_WD, CODE_LGD_MAX_WD);
		ADD(PROPERTY_NAME_LGD_LBL_SZ, CODE_LGD_LBL_SZ);
		ADD(PROPERTY_NAME_LGD_NM_POS, CODE_LGD_NM_POS);
		ADD(PROPERTY_NAME_LGD_CHECKBOX_CL, CODE_LGD_CHECKBOX_CL);
		ADD(PROPERTY_NAME_LGD_CHECKBOX_CHECK, CODE_LGD_CHECKBOX_CHECK_CL);
		ADD(PROPERTY_NAME_LGD_CHECKBOX_BDR_CL, CODE_LGD_CHECKBOX_BDR_CL);
		ADD(PROPERTY_NAME_AX_NUM_FONT_SZ, CODE_AX_NUM_FONT_SZ);
		ADD(PROPERTY_NAME_AX_LBL_FONT_SZ, CODE_AX_LBL_FONT_SZ);
		ADD(PROPERTY_NAME_AX_MAJ_UNIT_SZ, CODE_AX_MAJ_UNIT_SZ);
		ADD(PROPERTY_NAME_AX_LINE_CL, CODE_AX_LINE_CL);
		ADD(PROPERTY_NAME_AX_NUM_FONT_CL, CODE_AX_NUM_FONT_CL);
		ADD(PROPERTY_NAME_AX_TITLE_PD, CODE_AX_TITLE_PD);
		ADD(PROPERTY_NAME_AX_TITLE_ROTATE, CODE_AX_TITLE_ROTATE);
		ADD(PROPERTY_NAME_AX_TITLE_FONT_FAM, CODE_AX_TITLE_FONT_FAM);
		ADD(PROPERTY_NAME_AX_NUM_ROTATE, CODE_AX_NUM_ROTATE);
		ADD(PROPERTY_NAME_AX_LBL_TICK_SZ, CODE_AX_LBL_TICK_SZ);
		ADD(PROPERTY_NAME_AX_END_PD, CODE_AX_END_PD);
		ADD(PROPERTY_NAME_AX_MINOR_UNIT_SZ, CODE_AX_MINOR_UNIT_SZ);
		ADD(PROPERTY_NAME_AX_LBL_FONT_CL, CODE_AX_LBL_FONT_CL);
		ADD(PROPERTY_NAME_AX_START_PD, CODE_AX_START_PD);
		ADD(PROPERTY_NAME_AX_TITLE_SZ, CODE_AX_TITLE_SZ);
		ADD(PROPERTY_NAME_AX_TITLE_CL, CODE_AX_TITLE_CL);
		ADD(PROPERTY_NAME_AX_LBL_FONT_FAM, CODE_AX_LBL_FONT_FAM);
		ADD(PROPERTY_NAME_AX_NUM_PD, CODE_AX_NUM_PD);
		ADD(PROPERTY_NAME_AX_GROUP_PD, CODE_AX_GROUP_PD);
		ADD(PROPERTY_NAME_AX_NUM_FONT_FAM, CODE_AX_NUM_FONT_FAM);
		ADD(PROPERTY_NAME_AX_LBL_ROTATE, CODE_AX_LBL_ROTATE);
		ADD(PROPERTY_NAME_AX_LBL_PD, CODE_AX_LBL_PD);
		ADD(PROPERTY_NAME_GR_LYR_H_MID_GRD_CL, CODE_GR_LYR_H_MID_GRD_CL);
		ADD(PROPERTY_NAME_GR_LYR_V_MAJ_GRD_CL, CODE_GR_LYR_V_MAJ_GRD_CL);
		ADD(PROPERTY_NAME_GR_LYR_BDR_CL, CODE_GR_LYR_BDR_CL);
		ADD(PROPERTY_NAME_GR_LYR_V_MID_GRD_CL, CODE_GR_LYR_V_MID_GRD_CL);
		ADD(PROPERTY_NAME_GR_LYR_V_GRD_CL, CODE_GR_LYR_V_GRD_CL);
		ADD(PROPERTY_NAME_GR_LYR_H_MAJ_GRD_CL, CODE_GR_LYR_H_MAJ_GRD_CL);
		ADD(PROPERTY_NAME_GR_LYR_H_GRD_CL, CODE_GR_LYR_H_GRD_CL);
		ADD(PROPERTY_NAME_V_GRD_SZ, CODE_V_GRD_SZ);
		ADD(PROPERTY_NAME_H_GRD_SZ, CODE_H_GRD_SZ);
		ADD(PROPERTY_NAME_V_MID_GRD_SZ, CODE_V_MID_GRD_SZ);
		ADD(PROPERTY_NAME_H_MID_GRD_SZ, CODE_H_MID_GRD_SZ);
		ADD(PROPERTY_NAME_V_MAJ_GRD_SZ, CODE_V_MAJ_GRD_SZ);
		ADD(PROPERTY_NAME_H_MAJ_GRD_SZ, CODE_H_MAJ_GRD_SZ);
		ADD(PROPERTY_NAME_TITLE_FONT_CL, CODE_TITLE_FONT_CL);
		ADD(PROPERTY_NAME_TITLE_FONT_SZ, CODE_TITLE_FONT_SZ);
		ADD(PROPERTY_NAME_TITLE_FONT_FAM, CODE_TITLE_FONT_FAM);
		ADD(PROPERTY_NAME_FLD_BDR_CL, CODE_FLD_BDR_CL);
		ADD(PROPERTY_NAME_FLD_FOCUS_BORDER_CL, CODE_FLD_FCS_BDR_CL);
		ADD(PROPERTY_NAME_FLD_FOCUS_BORDER_WD, CODE_FLD_FCS_BDR_WD);
		ADD(PROPERTY_NAME_FLD_FONT_FAM, CODE_FLD_FONT_FAM);
		ADD(PROPERTY_NAME_FLD_FONT_SZ, CODE_FLD_FONT_SZ);
		ADD(PROPERTY_NAME_FONT_STYLE, CODE_FONT_STYLE);
		ADD(PROPERTY_NAME_DLG_WD, CODE_DLG_WD);
		ADD(PROPERTY_NAME_DLG_HI, CODE_DLG_HI);
		ADD(PROPERTY_NAME_DLG_FLD_BG_CL, CODE_DLG_FLD_BG_CL);
		ADD(PROPERTY_NAME_DLG_FLD_FONT_CL, CODE_DLG_FLD_FONT_CL);
		ADD(PROPERTY_NAME_DLG_FONT_FAM, CODE_DLG_FONT_FAM);
		ADD(PROPERTY_NAME_DLG_FONT_SZ, CODE_DLG_FONT_SZ);
		ADD(PROPERTY_NAME_DLG_TITLE_FONT_FAM, CODE_DLG_TITLE_FONT_FAM);
		ADD(PROPERTY_NAME_DLG_TITLE_FONT_SZ, CODE_DLG_TITLE_FONT_SZ);
		ADD(PROPERTY_NAME_DLG_TITLE_ALG, CODE_DLG_TITLE_FONT_ALG);
		ADD(PROPERTY_NAME_DLG_X_BUTTON_HI, CODE_DLG_X_BUTTON_HI);
		ADD(PROPERTY_NAME_DLG_X_BUTTON_WD, CODE_DLG_X_BUTTON_WD);
		ADD(PROPERTY_NAME_DLG_FORM_BUTTON_BG_CL, CODE_DLG_FORM_BUTTON_BG_CL);
		ADD(PROPERTY_NAME_DLG_FORM_BUTTON_PNL_BG_CL, CODE_DLG_FORM_BUTTON_PNL_BG_CL);
		ADD(PROPERTY_NAME_DLG_FORM_BUTTON_FONT_CL, CODE_DLG_FORM_BUTTON_FONT_CL);
		ADD(PROPERTY_NAME_DLG_FORM_BUTTON_FONT_FAM, CODE_DLG_FORM_BUTTON_FONT_FAM);
		ADD(PROPERTY_NAME_FLD_BORDER_RAD, CODE_FLD_BDR_RAD);
		ADD(PROPERTY_NAME_FLD_BORDER_WD, CODE_FLD_BDR_WD);
		ADD(PROPERTY_NAME_CAL_BG_CL, CODE_CAL_BG_CL);
		ADD(PROPERTY_NAME_CAL_BTN_BG_CL, CODE_CAL_BTN_BG_CL);
		ADD(PROPERTY_NAME_CAL_YR_FG_CL, CODE_CAL_YR_FG_CL);
		ADD(PROPERTY_NAME_CAL_SEL_YR_FG_CL, CODE_CAL_SEL_YR_FG_CL);
		ADD(PROPERTY_NAME_CAL_MTN_FG_CL, CODE_CAL_MTN_FG_CL);
		ADD(PROPERTY_NAME_CAL_SEL_MTN_FG_CL, CODE_CAL_SEL_MTN_FG_CL);
		ADD(PROPERTY_NAME_CAL_SEL_MTN_BG_CL, CODE_CAL_SEL_MTN_BG_CL);
		ADD(PROPERTY_NAME_CAL_WK_FG_CL, CODE_CAL_WK_FG_CL);
		ADD(PROPERTY_NAME_CAL_WK_BG_CL, CODE_CAL_WK_BG_CL);
		ADD(PROPERTY_NAME_CAL_WK_FD_CL, CODE_CAL_WK_FD_CL);
		ADD(PROPERTY_NAME_CAL_DAY_FG_CL, CODE_CAL_DAY_FG_CL);
		ADD(PROPERTY_NAME_CAL_X_DAY_FG_CL, CODE_CAL_X_DAY_FG_CL);
		ADD(PROPERTY_NAME_CAL_BTN_FG_CL, CODE_CAL_BTN_FG_CL);
		ADD(PROPERTY_NAME_CAL_HOV_BG_CL, CODE_CAL_HOV_BG_CL);
		ADD(PROPERTY_NAME_SCROLL_BAR_RADIUS, CODE_SCROLL_BAR_RADIUS);
		ADD(PROPERTY_NAME_SCROLL_BAR_HIDE_ARROWS, CODE_SCROLL_BAR_HIDE_ARROWS);
		ADD(PROPERTY_NAME_SCROLL_BAR_CORNER_CL, CODE_SCROLL_BAR_CORNER_CL);
	}

	public static short GET(String key) {
		Short r = CODES.getValue(key);
		return r == null ? MISSING_CODE : r.shortValue();
	}
	public static String GET(short code) {
		return CODES.getKey(code);
	}

	public static final Set<? extends Short> KEYS_PADDING = CH.s(CODE_PD_LF_PX, CODE_PD_RT_PX, CODE_PD_TP_PX, CODE_PD_BTM_PX, CODE_PD_CL, CODE_PD_SHADOW_HZ_PX,
			CODE_PD_SHADOW_VT_PX, CODE_PD_SHADOW_SZ_PX, CODE_PD_SHADOW_CL, CODE_PD_BDR_SZ_PX, CODE_PD_BDR_CL, CODE_PD_RAD_TP_LF_PX, CODE_PD_RAD_TP_RT_PX, CODE_PD_RAD_BTM_LF_PX,
			CODE_PD_RAD_BTM_RT_PX);
	public static final Set<? extends Short> KEYS_SCROLLBAR = CH.s(CODE_SCROLL_GRIP_CL, CODE_SCROLL_TRACK_CL, CODE_SCROLL_BTN_CL, CODE_SCROLL_ICONS_CL, CODE_SCROLL_WD,
			CODE_SCROLL_BDR_CL);
	public static final Set<? extends Short> KEYS_TITLE_PNL = CH.s(CODE_TITLE_PNL_FONT_SZ, CODE_TITLE_PNL_ALIGN, CODE_TITLE_PNL_FONT_CL, CODE_TITLE_PNL_FONT_FAM);
	public static final Set<? extends Short> KEYS_COMMON = CH.s(CODE_BG_CL, CODE_BOLD, CODE_CELL_BDR_CL, CODE_FILT_BG_CL, CODE_FILT_FONT_CL, CODE_FLD_BG_CL, CODE_FLD_FONT_CL,
			CODE_FONT_CL, CODE_FONT_FAM, CODE_FONT_SZ, CODE_GRAYBAR_CL, CODE_HEADER_BG_CL, CODE_HEADER_DIV_HIDE, CODE_HEADER_FONT_CL, CODE_ITALIC, CODE_PD_BDR_CL,
			CODE_PD_BDR_SZ_PX, CODE_PD_BTM_PX, CODE_PD_CL, CODE_PD_RAD_TP_LF_PX, CODE_PD_RAD_TP_RT_PX, CODE_PD_RAD_BTM_LF_PX, CODE_PD_RAD_BTM_RT_PX, CODE_PD_LF_PX, CODE_PD_RT_PX,
			CODE_PD_SHADOW_CL, CODE_PD_SHADOW_HZ_PX, CODE_PD_SHADOW_SZ_PX, CODE_PD_SHADOW_VT_PX, CODE_PD_TP_PX, CODE_SCROLL_BTN_CL, CODE_SCROLL_GRIP_CL, CODE_SCROLL_ICONS_CL,
			CODE_SCROLL_TRACK_CL, CODE_SCROLL_BDR_CL, CODE_SCROLL_WD, CODE_SEARCH_BG_CL, CODE_SEARCH_BTNS_CL, CODE_SEARCH_FLD_FONT_CL, CODE_SEARCH_HIDE, CODE_SEL_CL,
			CODE_TITLE_PNL_ALIGN, CODE_TITLE_PNL_FONT_CL, CODE_TITLE_PNL_FONT_SZ, CODE_TXT_ALIGN, CODE_UNDERLINE, CODE_TITLE_PNL_FONT_FAM, CODE_FONT_STYLE);
	
	public static final Set<String> DEPRECATED_FIELD_CSS_STYLE = CH.s(DEPRECATED_PROPERTY_NAME_FLD_CSS_CHECK, DEPRECATED_PROPERTY_NAME_FLD_CSS_DATE, DEPRECATED_PROPERTY_NAME_FLD_CSS_DATE_RNG, 
			DEPRECATED_PROPERTY_NAME_FLD_CSS_DATE_TIME, DEPRECATED_PROPERTY_NAME_FLD_CSS_DIV, DEPRECATED_PROPERTY_NAME_FLD_CSS_BTN, DEPRECATED_PROPERTY_NAME_FLD_CSS_IMG, 
			DEPRECATED_PROPERTY_NAME_FLD_CSS_MULTI_SEL, DEPRECATED_PROPERTY_NAME_FLD_CSS_RNG, DEPRECATED_PROPERTY_NAME_FLD_CSS_SEL, DEPRECATED_PROPERTY_NAME_FLD_CSS_SUB_RNG, 
			DEPRECATED_PROPERTY_NAME_FLD_CSS_TXT_AREA, DEPRECATED_PROPERTY_NAME_FLD_CSS_TXT, DEPRECATED_PROPERTY_NAME_FLD_CSS_TIME, DEPRECATED_PROPERTY_NAME_FLD_CSS_TIME_RNG, 
			DEPRECATED_PROPERTY_NAME_FLD_CSS_UPLOAD);
}
