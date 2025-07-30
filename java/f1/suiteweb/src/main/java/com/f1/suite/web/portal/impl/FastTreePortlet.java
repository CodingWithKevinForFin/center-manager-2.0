package com.f1.suite.web.portal.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.f1.suite.web.JsFunction;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletSchema;
import com.f1.suite.web.portal.style.PortletStyleManager_Dialog;
import com.f1.suite.web.portal.style.PortletStyleManager_Form;
import com.f1.suite.web.tree.WebTreeManager;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.impl.BasicTreeQuickFilterAutocompleteManager;
import com.f1.suite.web.tree.impl.FastWebTree;
import com.f1.suite.web.tree.impl.TreeQuickFilterAutocompleteManager;
import com.f1.utils.Formatter;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.json.JsonBuilder;

public class FastTreePortlet extends AbstractPortlet {
	public static final PortletSchema<FastTreePortlet> SCHEMA = new BasicPortletSchema<FastTreePortlet>("Tree", "TreePortlet", FastTreePortlet.class, false, true);

	public static final String OPTION_BACKGROUND_STYLE = "backgroundStyle";
	public static final String OPTION_GRAY_BAR_STYLE = "grayBarStyle";
	public static final String OPTION_FONT_STYLE = "fontStyle";
	public static final String OPTION_CELL_BORDER_STYLE = "cellBorderStyle";
	public static final String OPTION_CELL_BOTTOM_DIVIDER = "cellBottomDivider";
	public static final String OPTION_CELL_RIGHT_DIVIDER = "cellRightDivider";
	public static final String OPTION_VERTICAL_ALIGN = "verticalAlign";

	public static final String OPTION_HEADER_BAR_HIDDEN = "headerBarHidden";
	public static final String OPTION_COLUMN_HEADER_BG_COLOR = "headerBgStyle";
	public static final String OPTION_COLUMN_HEADER_FONT_COLOR = "headerFontStyle";

	public static final String OPTION_SEARCH_BAR_BG_STYLE = "searchBgStyle";
	public static final String OPTION_SEARCH_BAR_FIELD_BG_STYLE = "searchFieldBgStyle";
	public static final String OPTION_SEARCH_BAR_FIELD_FG_STYLE = "searchFieldFgStyle";
	public static final String OPTION_SEARCH_BUTTONS_COLOR = "searchButtonsColor";
	public static final String OPTION_SEARCH_BAR_HIDDEN = "searchBarHidden";
	public static final String OPTION_SEARCH_FIELD_BORDER_COLOR = "searchFieldBorderColor";

	public static final String OPTION_FILTERED_COLUMN_BG = "filteredBg";
	public static final String OPTION_FILTERED_FONT_COLOR = "filteredFont";

	public static final String OPTION_SCROLL_BAR_WIDTH = "scrollBarWidth";
	public static final String OPTION_GRIP_COLOR = "gripColor";
	public static final String OPTION_TRACK_COLOR = "trackColor";
	public static final String OPTION_SCROLL_BUTTON_COLOR = "scrollButtonColor";
	public static final String OPTION_SCROLL_ICONS_COLOR = "scrollIconsColor";
	public static final String OPTION_SCROLL_BORDER_COLOR = "scrollBorderColor";
	public static final String OPTION_SCROLL_BAR_RADIUS = "scrollBarRadius";
	public static final String OPTION_SCROLL_BAR_HIDE_ARROWS = "scrollBarHideArrows";
	public static final String OPTION_SCROLL_BAR_CORNER_COLOR = "scrollBarCornerColor";
	public static final String OPTION_HEADER_DIVIDER_HIDDEN = "headerDividerHidden";
	public static final String OPTION_TREAT_NAME_CLICK_AS_SELECT = "treatNameClickAsSelect";
	public static final String OPTION_HIDE_CHECKBOXES = "hideCheckBoxes";
	public static final String OPTION_HEADER_FONT_SIZE = "headerFontSize";
	public static final String OPTION_HEADER_ROW_HEIGHT = "headerRowHeight";
	public static final String OPTION_ROW_HEIGHT = "rowHeight";
	public static final String OPTION_FONT_SIZE = "fontSize";
	public static final String OPTION_FONT_FAMILY = "fontFamily";
	public static final String OPTION_FLASH_UP_COLOR = "flashUpColor";
	public static final String OPTION_FLASH_DN_COLOR = "flashDnColor";
	public static final String OPTION_FLASH_MS = "flashMs";

	final private FastWebTree tree;
	private PortletStyleManager_Dialog dialogStyle;
	private PortletStyleManager_Form formStyle;
	private int maxShowValuesForFilterDialog = TableFilterPortlet.MAX_VALUES_COUNT;
	private String displayTimeFormatted;

	public FastTreePortlet(PortletConfig portletConfig) {
		super(portletConfig);
		this.tree = new FastWebTree(portletConfig.getPortletManager().getTextFormatter());
		this.tree.setJsObjectName("t");
		this.tree.setParentPortlet(this);

	}
	public FastWebTree getTree() {
		return tree;
	}
	public void onChange(FastWebTree tree) {
		flagPendingAjax();
	}
	public String getDisplayTimeFormatted() {
		return displayTimeFormatted;
	}
	public void setDisplayTimeFormatted(String displayTimeFormatted) {
		this.displayTimeFormatted = displayTimeFormatted;
		flagPendingAjax();
	}

	@Override
	public void initJs() {
		super.initJs();
		flagPendingAjax();
		this.tree.setIsVisible(false);
		if (getVisible()) {
			optionsChanged = true;
			this.tree.setIsVisible(true);
		}
	}

	@Override
	public void drainJavascript() {
		super.drainJavascript();
		if (getVisible()) {
			if (optionsChanged) {
				optionsChanged = false;
				JsFunction func = callJsFunction("setOptions");
				JsonBuilder optionsJson = func.startJson();
				optionsJson.addQuoted(options);
				optionsJson.close();
				func.end();
				callJsFunction("setSearch").addParamQuoted(SH.noNull(tree.getSearch())).end();
			}
			callJsFunction("setTitle").addParamQuotedHtml(getTreeTitle()).end();
			StringBuilder pendingJs = getManager().getPendingJs();
			int start = pendingJs.length();
			pendingJs.append("{var t=");
			callJsFunction("getTree").end();
			int n = pendingJs.length();
			tree.createJs(getManager().getPendingJs());
			if (n == pendingJs.length())
				pendingJs.setLength(start);
			else
				pendingJs.append("}");

		}

	}

	@Override
	public void handleCallback(String callback, Map<String, String> attributes) {
		StringBuilder pendingJs = getManager().getPendingJs();
		int start = pendingJs.length();
		pendingJs.append("{var t=");
		callJsFunction("getTree").end();
		int n = pendingJs.length();
		if (!tree.processWebRequest(this, attributes, pendingJs))
			super.handleCallback(callback, attributes);
		if (n == pendingJs.length())
			pendingJs.setLength(start);
		else
			pendingJs.append("}");
	}

	@Override
	public PortletSchema<? extends FastTreePortlet> getPortletSchema() {
		return SCHEMA;
	}
	public WebTreeNode createNode(String name, WebTreeNode parent, boolean expanded) {
		return getTree().getTreeManager().createNode(name, parent, expanded);
	}
	public WebTreeNode createNode(String name, WebTreeNode parent, boolean expanded, Object data) {
		return getTree().getTreeManager().createNode(name, parent, expanded, data);
	}

	public WebTreeManager getTreeManager() {
		return getTree().getTreeManager();
	}

	private Map<String, Object> options = new HashMap<String, Object>();
	private boolean optionsChanged;

	public Object addOption(String key, Object value) {
		Object r = options.put(key, value);
		if (OH.eq(r, value))
			return r;
		flagOptionsChanged();
		return r;
	}
	public Object removeOption(String key) {
		Object r = options.remove(key);
		if (r == null) {
			return null;
		}
		flagOptionsChanged();
		return r;
	}
	public void clearOptions() {
		if (options.isEmpty()) {
			return;
		}
		this.options.clear();
		flagOptionsChanged();
	}
	public void flagOptionsChanged() {
		this.optionsChanged = true;
		this.tree.flagStyleChanged();
		if (getVisible())
			flagPendingAjax();
	}
	public Object getOption(String option) {
		return options.get(option);
	}
	public Set<String> getOptions() {
		return options.keySet();
	}
	public PortletStyleManager_Dialog getDialogStyle() {
		return dialogStyle;
	}
	public FastTreePortlet setDialogStyle(PortletStyleManager_Dialog dialogStyle) {
		this.dialogStyle = dialogStyle;
		return this;
	}
	public PortletStyleManager_Form getFormStyle() {
		return formStyle;
	}
	public FastTreePortlet setFormStyle(PortletStyleManager_Form formStyle) {
		this.formStyle = formStyle;
		return this;
	}

	public void clear() {
		this.getTreeManager().clear();
		this.getTree().clearSelected();

	}
	public void callSetAutocomplete(int columnId, Map<String, String> r) {
		int pos = tree.getColumnPosition(columnId);
		if (pos != -1) {
			JsFunction jsf = this.callJsFunction("setQuickFilterAutocomplete");
			jsf.addParam(pos);
			JsonBuilder json = jsf.startJson();
			json.addQuoted(r);
			json.end();
			jsf.end();
		}
	}

	private TreeQuickFilterAutocompleteManager quickfilterAutocompleteManager = new BasicTreeQuickFilterAutocompleteManager();

	public TreeQuickFilterAutocompleteManager getQuickfilterAutocompleteManager() {
		return quickfilterAutocompleteManager;
	}

	public void setQuickfilterAutocompleteManager(TreeQuickFilterAutocompleteManager quickfilterAutocompleteManager) {
		this.quickfilterAutocompleteManager = quickfilterAutocompleteManager;
	}

	public void onQuickFilterUserAction(Integer columnId, String val, int i) {
		if (this.quickfilterAutocompleteManager != null)
			this.quickfilterAutocompleteManager.onQuickFilterUserAction(this, columnId, val, i);
	}
	public int getMaxShowValuesForFilterDialog() {
		return this.maxShowValuesForFilterDialog;
	}
	public void setMaxShowValuesForFilterDialog(int maxShowValuesForFilterDialog) {
		this.maxShowValuesForFilterDialog = maxShowValuesForFilterDialog;
	}
	public WebTreeNode getRoot() {
		return this.getTreeManager().getRoot();
	}

	private String getTreeTitle() {
		int rows = this.tree.getNodesCount() - 1;
		LocaleFormatter formatter = getManager().getLocaleFormatter();
		Formatter nf = formatter.getNumberFormatter(0);
		return nf.format(rows) + " rows " + (SH.isnt(this.displayTimeFormatted) ? "" : " | Last Runtime: " + this.displayTimeFormatted);
	}

	public void autoSizeColumn(int columnId) {
		this.getTree().autoSizeColumn(columnId, this.getManager().getPortletMetrics());
	}

}
