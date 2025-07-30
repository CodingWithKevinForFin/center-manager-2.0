/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.portal.impl;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.base.TableList;
import com.f1.base.TableListenable;
import com.f1.suite.web.JsFunction;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.BasicPortletDownload;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.PortletMetrics;
import com.f1.suite.web.portal.PortletSchema;
import com.f1.suite.web.portal.style.PortletStyleManager_Dialog;
import com.f1.suite.web.portal.style.PortletStyleManager_Form;
import com.f1.suite.web.portal.style.PortletStyleManager_Menu;
import com.f1.suite.web.table.WebCellFormatter;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.WebTableTooltipFactory;
import com.f1.suite.web.table.fast.BasicQuickFilterAutocompleteManager;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.fast.QuickFilterAutocompleteManager;
import com.f1.suite.web.table.impl.CopyPortlet;
import com.f1.suite.web.table.impl.NumberWebCellFormatter;
import com.f1.suite.web.table.impl.SpreadSheetBuilder;
import com.f1.suite.web.table.impl.WebTableCallbackProcessor;
import com.f1.suite.web.table.impl.WebTableFilteredInFilter;
import com.f1.suite.web.tree.impl.CopyableTableImpl;
import com.f1.utils.CH;
import com.f1.utils.Formatter;
import com.f1.utils.IntArrayList;
import com.f1.utils.LH;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.WebPoint;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_Simple;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.formatter.BasicNumberFormatter;
import com.f1.utils.json.JsonBuilder;
import com.f1.utils.structs.MapInMap;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.Tuple3;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;

public class FastTablePortlet extends AbstractPortlet implements WebMenuListener {

	private static final Logger log = LH.get();;
	public static final String OPTION_ROW_HEIGHT = "rowHeight";
	public static final String OPTION_USE_GREY_BARS = "useGreybars";
	public static final String OPTION_SELECTED_CSS_CLASS = "cellSelectedStyle";
	public static final String OPTION_ACTIVE_CSS_CLASS = "cellActiveStyle";
	public static final String OPTION_BACKGROUND_STYLE = "backgroundStyle";
	public static final String OPTION_STYLE = "tableStyle";
	public static final String OPTION_FONT_SIZE = "fontSize";
	public static final String OPTION_FONT_FAMILY = "fontFamily";
	public static final String OPTION_SELECTED_BG = "cellSelectedBg";
	public static final String OPTION_ACTIVE_BG = "cellActiveBg";
	public static final String OPTION_MENU_BAR_COLOR = "menuBarBg";
	public static final String OPTION_MENU_FONT_COLOR = "menuFontColor";
	public static final String OPTION_GREY_BAR_COLOR = "greyBarColor";
	public static final String OPTION_SEARCH_BAR_COLOR = "searchBarColor";
	public static final String OPTION_SEARCH_FIELD_BORDER_COLOR = "searchFieldBorderColor";
	public static final String OPTION_FLASH_UP_COLOR = "flashUpColor";
	public static final String OPTION_FLASH_DN_COLOR = "flashDnColor";
	public static final String OPTION_FLASH_MS = "flashMs";
	public static final String OPTION_SEARCH_BAR_FONT_COLOR = "searchBarFontColor";
	public static final String OPTION_SEARCH_BUTTONS_COLOR = "searchButtonsColor";
	public static final String OPTION_DEFAULT_FONT_COLOR = "defaultFontColor";
	public static final String OPTION_FILTERED_COLUMN_BG_COLOR = "filteredColumnBgColor";
	public static final String OPTION_FILTERED_COLUMN_FONT_COLOR = "filteredColumnFontColor";
	public static final String OPTION_TITLE_BAR_COLOR = "titleBarColor";
	public static final String OPTION_TITLE_BAR_FONT_COLOR = "titleBarFontColor";
	public static final String OPTION_MENU_BAR_HIDDEN = "menuBarHidden";
	public static final String OPTION_QUICK_COLUMN_FILTER_HIDDEN = "quickColumnFilterHidden";
	public static final String OPTION_QUICK_COLUMN_FILTER_HEIGHT = "quickColumnFilterHeight";
	public static final String OPTION_QUICK_COLUMN_FILTER_BG_CL = "quickColumnFilterBgCl";
	public static final String OPTION_QUICK_COLUMN_FILTER_FONT_CL = "quickColumnFilterFontCl";
	public static final String OPTION_QUICK_COLUMN_FILTER_FONT_SZ = "quickColumnFilterFontSz";
	public static final String OPTION_QUICK_COLUMN_FILTER_BDR_CL = "quickColumnFilterBdrCl";
	public static final String OPTION_CELL_BORDER_COLOR = "cellBorderColor";
	public static final String OPTION_SEARCH_BAR_DIV_COLOR = "searchBarDivColor";
	public static final String OPTION_CELL_BOTTOM_DIVIDER = "cellBottomDivider";
	public static final String OPTION_CELL_RIGHT_DIVIDER = "cellRightDivider";
	public static final String OPTION_CELL_PADDING_HORIZONTAL = "cellPadHt";
	public static final String OPTION_VERTICAL_ALIGN = "verticalAlign";

	public static final PortletSchema<FastTablePortlet> SCHEMA = new BasicPortletSchema<FastTablePortlet>("FastTable", "FastTablePortlet", FastTablePortlet.class, false, true);
	public static final String OPTION_GRIP_COLOR = "gripColor";
	public static final String OPTION_TRACK_COLOR = "trackColor";
	public static final String OPTION_TRACK_BUTTON_COLOR = "trackButtonColor";
	public static final String OPTION_SCROLL_ICONS_COLOR = "scrollIconsColor";
	public static final String OPTION_SCROLL_BORDER_COLOR = "scrollBorderColor";
	public static final String OPTION_SCROLL_BAR_RADIUS = "scrollBarRadius";
	public static final String OPTION_SCROLL_BAR_HIDE_ARROWS = "scrollBarHideArrows";
	public static final String OPTION_SCROLL_BAR_CORNER_COLOR = "scrollBarCornerColor";
	public static final String OPTION_TITLE_DIVIDER_HIDDEN = "hideHeaderDivider";
	public static final String OPTION_SCROLL_BAR_WIDTH = "scrollBarWidth";
	public static final String OPTION_HEADER_FONT_SIZE = "headerFontSize";
	public static final String OPTION_HEADER_ROW_HEIGHT = "headerRowHeight";
	private static final int MAX_CLIPBOARD_SIZE = 1024 * 1024;
	private static final int MIN_AUTO_WIDTH = 30;
	private static final int MAX_AUTO_WIDTH = 1000;
	public static final int DEFAULT_SCROLLBAR_WIDTH = 15;
	private FastWebTable table;
	private PortletStyleManager_Dialog dialogStyle;
	private PortletStyleManager_Form formStyle;
	private PortletStyleManager_Menu menuStyle;

	protected TableList rows;
	private int rowsCount;
	//	public final BasicPortletSocket hiddenFields;
	private boolean optionsChanged;
	private String displayTimeFormatted;
	private WebTableTooltipFactory tooltipFactory;
	private Integer selectedCol = null ;
	private StringBuilder[] copyBuilder;

	public FastTablePortlet(PortletConfig config, FastWebTable table) {
		super(config);
		//		this.hiddenFields = addSocket(true, "Show Hidden Fields", "Link to tree and show hidden fields", false, CH.s(ShowTableFieldsInterPortletMessage.class), null);
		if (table != null)
			setTable(table);
		this.formStyle = getManager().getStyleManager().getFormStyle();
		this.dialogStyle = getManager().getStyleManager().getDialogStyle();
		this.copyBuilder = new StringBuilder[2];
	}
	public FastTablePortlet(PortletConfig config, TableListenable table, String title) {
		this(config, new FastWebTable(new BasicSmartTable(table), config.getPortletManager().getTextFormatter()));
		setTableTitle(title);
		setTitle(title);
	}

	public void setTable(FastWebTable table) {
		if (this.table != null)
			throw new IllegalStateException();
		this.table = table;
		this.table.setParentPortlet(this);
		this.table.setWebTableId("t");

		rows = (TableList) getTable().getTable().getRows();
	}

	public FastWebTable getTable() {
		return table;
	}

	public void onChange(WebTable table) {
		flagPendingAjax();
	}

	@Override
	public void initJs() {
		super.initJs();
		flagPendingAjax();
		this.table.setIsVisible(false);
		if (getVisible())
			this.table.setIsVisible(true);
		this.quickFilterAutocomplete = null;
		this.quickFilterColumn = null;
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
			}
			if (MH.allBits(table.getPendingChangesMask(), FastWebTable.CHANGED_ALL) || MH.allBits(table.getPendingChangesMask(), FastWebTable.CHANGED_ALL_ROWS)) {
				callJsFunction("setSearch").addParamQuoted(SH.noNull(table.getSearch())).end();
			}
			StringBuilder js = getManager().getPendingJs();
			int mark = js.length();
			js.append("{var t=");
			callJsFunction(js, "getFastTable").end();
			if (table.createJs(js))
				getManager().getPendingJs().append("}");
			else
				js.setLength(mark);
			table.fireOnSelectedChanged();
			if (rowsCount != table.getRowsCount()) {
				rowsCount = table.getRowsCount();
				boolean hasFilter = table.getExternalFilter() != null || table.getTable().getExternalFilterIndexColumnId() != null;
				callJsFunction("setTitle").addParamQuotedHtml(getTableTitle()).addParam(hasFilter).end();
			}
			if (flagNeedClearEditRows) {
				this.flagNeedClearEditRows = false;
				this.callJsFunction("editRowsComplete").end();
			}
			if (flagNeedEditRows) {
				this.flagNeedEditRows = false;
				this.callJsFunction_editRows();
			}
			if (this.quickFilterAutocomplete != null) {
				int colPos = getTable().getColumnPosition(this.quickFilterColumn);
				if (colPos != -1) {
					JsFunction jsf = this.callJsFunction("setAutocomplete");
					jsf.addParam(colPos);
					JsonBuilder json = jsf.startJson();
					json.addQuoted(new TreeMap<String, String>(this.quickFilterAutocomplete));
					json.end();
					jsf.end();
				}
				this.quickFilterAutocomplete = null;
				this.quickFilterColumn = null;
			}
		}

	}
	public void setTableTitle(String tableTitle) {
		SmartTable t = table.getTable();
		if (OH.eq(t.getTitle(), tableTitle))
			return;
		t.setTitle(tableTitle);
		flagPendingAjax();
		rowsCount = -1;//force refresh... hacky
	}

	private String getTableTitle() {
		LocaleFormatter formatter = getManager().getLocaleFormatter();
		Formatter nf = formatter.getNumberFormatter(0);
		return nf.format(rowsCount) + " " + formatText(table.getTable().getTitle()) + (SH.isnt(this.displayTimeFormatted) ? "" : " | Last Runtime: " + this.displayTimeFormatted);
	}

	@Override
	public void setVisible(boolean isVisible) {
		if (isVisible != getVisible()) {
			editAborted();
		}
		super.setVisible(isVisible);
		this.optionsChanged = true;
		this.table.setIsVisible(isVisible);
		this.rowsCount = -1;
	}

	public boolean editAborted() {
		if (this.isEditing()) {
			this.editListener.onTableEditAbort(this);
			if (this.table.isPausedSort())
				this.table.pauseSort(false);
			this.editColumns = null;
			this.editListener = null;
			this.editOrigValues = null;
			this.userEditValues.clear();
			this.flagNeedClearEditRows = true;
			this.flagPendingAjax();
			return true;
		}
		return false;
	}
	@Override
	public void handleCallback(String callback, Map<String, String> attributes) {
		if ("hover".equals(callback)) {
			int x = CH.getOr(Caster_Integer.PRIMITIVE, attributes, "x", -1);
			int y = CH.getOr(Caster_Integer.PRIMITIVE, attributes, "y", -1);
			if (x < 0 || x >= table.getVisibleColumnsCount() || y < 0 || y >= table.getRowsCount()) {
				callJsFunction("clearHover").end();
				return;
			}
			WebColumn column = table.getVisibleColumn(x);
			String tooltip = "";
			try {
				if (this.tooltipFactory != null) {
					boolean tooltipSetForColumn = tooltipFactory.isColumnTooltipSet(column);
					if (!tooltipSetForColumn) {//don't bother creating tooltip if the tooltip is not set for the column
						callJsFunction("clearHover").end();
						return;
					} else
						tooltip = tooltipFactory.createTooltip(column, table.getRow(y));
				}
				if (SH.is(tooltip))
					callJsFunction("setHover").addParam(x).addParam(y).addParamQuoted(tooltip).addParam(1).addParam(1).end();
				else
					callJsFunction("clearHover").end();
			} catch (RuntimeException e) {
				LH.warning(log, "Error processing hover over at ", x, ",", y, ": ", e);
			}
		} else if ("rows_edit".equals(callback)) {
			final boolean isSubmit = CH.getOrNoThrow(Caster_Boolean.PRIMITIVE, attributes, "submit", false);
			List<Map<String, Object>> sob = (List<Map<String, Object>>) ObjectToJsonConverter.INSTANCE_CLEAN.stringToObject(attributes.get("cells"));
			onUserEditSubmit(sob, isSubmit);
		} else if ("rows_edit_cancel".equals(callback)) {
			editAborted();
		} else if ("download".equals(callback)) {
			getManager().showContextMenu(addDownloadMenuItems(new BasicWebMenu()), this);
		} else {
			StringBuilder js = getManager().getPendingJs();
			int mark = js.length();
			js.append("{var t=");
			callJsFunction(js, "getFastTable").end();
			int mark2 = js.length();
			if (!WebTableCallbackProcessor.processWebRequest(attributes, this, js)) {
				js.setLength(mark);
				super.handleCallback(callback, attributes);
			} else {
				if (js.length() == mark2)
					js.setLength(mark);
				else
					js.append("}");
			}
		}
	}
	public BasicWebMenu addDownloadMenuItems(BasicWebMenu sink) {
		sink.add(new BasicWebMenuLink("Download as Text", true, "download_pipe"));
		sink.add(new BasicWebMenuLink("Download as Spread Sheet", true, "download_ss"));
		sink.add(new BasicWebMenuLink("Download as Csv", true, "download_csv"));
		// show following menu if at least one row is selected
		List<Row> selRows = getTable().getSelectedRows();
		if (!selRows.isEmpty()) {
			sink.add(new BasicWebMenuLink(selRows.size() == 1 ? "Download Selected Row as Text" : "Download Selected Rows as Text", true, "download_selected_pipe"));
			sink.add(new BasicWebMenuLink(selRows.size() == 1 ? "Download Selected Row as Spread Sheet" : "Download Selected Rows as Spread Sheet", true, "download_selected_ss"));
			sink.add(new BasicWebMenuLink(selRows.size() == 1 ? "Download Selected Row as Csv" : "Download Selected Rows as Csv", true, "download_selected_csv"));
		}
		sink.setStyle(getMenuStyle());
		return sink;
	}
	public void doDownload(boolean onlySelected) {
		StringBuilder text = new StringBuilder();
		final char delim = '|';
		FastWebTable t = getTable();
		WebColumn[] columns = new WebColumn[getTable().getVisibleColumnsCount()];
		for (int i = 0; i < getTable().getVisibleColumnsCount(); i++) {
			columns[i] = t.getVisibleColumn(i);
			if (i > 0)
				text.append(delim);
			text.append(getManager().getTextFormatter().format(columns[i].getColumnName()));
		}
		text.append("\r\n");
		List<Row> rows = onlySelected ? t.getSelectedRows() : t.getTable().getRows();
		for (int j = 0, s = rows.size(); j < s; j++) {
			Row row = rows.get(j);
			for (int i = 0; i < columns.length; i++) {
				if (i > 0)
					text.append(delim);
				columns[i].getCellFormatter().formatCellToText(columns[i].getData(row), text);
			}
			text.append("\r\n");
		}
		String title = this.getDownloadNameRealized();
		getManager().pushPendingDownload(new BasicPortletDownload(title + ".txt", text.toString().getBytes()));
	}
	public void doCsvDownload(boolean onlySelected) {
		StringBuilder text = new StringBuilder();
		FastWebTable t = getTable();
		WebColumn[] columns = new WebColumn[getTable().getVisibleColumnsCount()];
		for (int i = 0; i < getTable().getVisibleColumnsCount(); i++) { // column headers
			columns[i] = t.getVisibleColumn(i);
			if (i > 0)
				text.append(',');
			escapeCsv(getManager().getTextFormatter().format(columns[i].getColumnName()), text);
		}
		text.append("\r\n");
		List<Row> rows = onlySelected ? t.getSelectedRows() : t.getTable().getRows();
		for (int j = 0, s = rows.size(); j < s; j++) { // row data
			Row row = rows.get(j);
			for (int i = 0; i < columns.length; i++) {
				if (i > 0)
					text.append(',');
				escapeCsv(columns[i].getCellFormatter().formatCellToText(columns[i].getData(row)), text);
			}
			text.append("\r\n");
		}
		String title = this.getDownloadNameRealized();
		getManager().pushPendingDownload(new BasicPortletDownload(title + ".csv", text.toString().getBytes()));
	}

	private void escapeCsv(String text, StringBuilder sink) {
		if (text == null)
			return;
		if (text.indexOf(',') != -1 || text.indexOf('"') != -1 || text.indexOf('\n') != -1) {
			SH.replaceAll(text, '"', "\"\"", sink.append('"')).append('"');
		} else
			sink.append(text);
	}
	@Override
	public PortletSchema<? extends FastTablePortlet> getPortletSchema() {
		return SCHEMA;
	}

	public void removeRow(Row row) {
		getTable().getTable().removeRow(row);
	}
	public Row addRow(Row row) {
		rows.add(row);
		return row;
	}
	public Row addRow(Object... row) {
		return rows.addRow(row);
	}
	public void clearRows() {
		getTable().clear();
		if (this.isEditing()) {
			this.flagNeedClearEditRows = true;
			this.flagPendingAjax();
		}
	}
	@Override
	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = super.getConfiguration();
		List<Map<String, Object>> visibleColumns = new ArrayList<Map<String, Object>>();
		FastWebTable table = getTable();
		for (int i = 0, l = table.getVisibleColumnsCount(); i < l; i++) {
			Map<String, Object> m = new HashMap<String, Object>();
			WebColumn column = table.getVisibleColumn(i);
			m.put("id", column.getColumnId());
			m.put("width", column.getWidth());
			m.put("location", i);
			visibleColumns.add(m);
		}
		List<Map<String, Object>> hiddenColumns = new ArrayList<Map<String, Object>>();
		for (int i = 0, l = table.getHiddenColumnsCount(); i < l; i++) {
			WebColumn column = table.getHiddenColumn(i);
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("id", column.getColumnId());
			m.put("width", column.getWidth());
			hiddenColumns.add(m);
		}
		r.put("visibleColumns", visibleColumns);
		r.put("hiddenColumns", hiddenColumns);
		r.put("pinCnt", this.table.getPinnedColumnsCount());

		if (table.isKeepSorting()) {
			final List<Map<String, String>> sortingColumns = new ArrayList<Map<String, String>>();
			for (Entry<String, Boolean> i : table.getSortedColumns()) {
				Map<String, String> m = new HashMap<String, String>();
				m.put("id", i.getKey());
				m.put("order", i.getValue() ? "asc" : "dsc");
				sortingColumns.add(m);
			}
			r.put("sorting", sortingColumns);
		}
		final Map<String, Map<String, Object>> filtersMap = new HashMap<String, Map<String, Object>>();
		for (String filteredInColumn : table.getFilteredInColumns()) {
			WebTableFilteredInFilter f = table.getFiltererdIn(filteredInColumn);
			if (f != null) {
				Map<String, Object> m = CH.m("v", new HashSet<String>(f.getValues()), "k", f.getKeep(), "p", f.getIsPattern(), "n", f.getIncludeNull(), "i", f.getMin(), "x",
						f.getMax());
				filtersMap.put(filteredInColumn, m);
			}
		}
		r.put("filters", filtersMap);
		return r;
	}

	public static class LocationComparator implements Comparator<Map<String, Object>> {

		@Override
		public int compare(Map<String, Object> o1, Map<String, Object> o2) {
			int location1 = CH.getOrThrow(Caster_Integer.INSTANCE, o1, "location");
			int location2 = CH.getOrThrow(Caster_Integer.INSTANCE, o2, "location");
			return OH.compare(location1, location2);
		}

	}

	@Override
	public void init(Map<String, Object> configuration, Map<String, String> origToNewIdMapping, StringBuilder sb) {
		super.init(configuration, origToNewIdMapping, sb);
		List<Map<String, Object>> visibleColumns = (List<Map<String, Object>>) CH.getOrThrow(Caster_Simple.OBJECT, configuration, "visibleColumns");
		List<Map<String, Object>> hiddenColumns = (List<Map<String, Object>>) CH.getOrThrow(Caster_Simple.OBJECT, configuration, "hiddenColumns");
		init(configuration, sb, visibleColumns, hiddenColumns);
	}

	public void init(Map<String, Object> configuration, StringBuilder sb, List<Map<String, Object>> visibleColumns, List<Map<String, Object>> hiddenColumns) {
		FastWebTable table = getTable();
		for (Map<String, Object> column : hiddenColumns) {
			String id = CH.getOrThrow(Caster_String.INSTANCE, column, "id");
			int width = CH.getOrThrow(Caster_Integer.INSTANCE, column, "width");
			WebColumn col = table.getColumnNoThrow(id);
			if (col != null) {
				col.setWidth(width);
				table.hideColumn(id);
			} else {
				if (!"!params".equals(id))
					sb.append("column not found: " + id);
			}
		}

		Collections.sort(visibleColumns, new LocationComparator());
		int location = 0;
		for (Map<String, Object> column : visibleColumns) {
			String id = CH.getOrThrow(Caster_String.INSTANCE, column, "id");
			int width = CH.getOrThrow(Caster_Integer.INSTANCE, column, "width");
			WebColumn col = table.getColumnNoThrow(id);
			if (col != null) {
				col.setWidth(width);
				table.showColumn(id, location++);
			} else {
				if (!"!params".equals(id))
					sb.append("column not found: " + id);
			}
		}

		final Map<String, Object> filtersMap = (Map<String, Object>) configuration.get("filters");
		if (filtersMap != null) {
			for (Entry<String, Object> e : filtersMap.entrySet()) {
				Object val = e.getValue();
				if (val instanceof List) {
					//LEGACY
					table.setFilteredIn(e.getKey(), new HashSet<String>((List) val));
				} else {
					Map<String, Object> m = (Map<String, Object>) val;
					List<String> filterValues = CH.l((Collection<String>) CH.getOrThrow(Caster_Simple.OBJECT, m, "v"));
					boolean keep = CH.getOrThrow(Caster_Boolean.INSTANCE, m, "k");
					boolean pattern = CH.getOr(Caster_Boolean.INSTANCE, m, "p", Boolean.FALSE);
					boolean includeNulls = CH.getOrThrow(Caster_Boolean.INSTANCE, m, "n");
					String min = CH.getOrThrow(Caster_String.INSTANCE, m, "i");
					String max = CH.getOrThrow(Caster_String.INSTANCE, m, "x");
					table.setFilteredIn(e.getKey(), new HashSet<String>(filterValues), keep, includeNulls, pattern, min, max);
				}
			}
		}
		List<Map<String, String>> sorting = (List<Map<String, String>>) CH.getOr(Caster_Simple.OBJECT, configuration, "sorting", null);
		if (CH.isntEmpty(sorting)) {
			boolean first = true;
			for (Map<String, String> m : sorting) {
				String id = CH.getOrThrow(Caster_String.INSTANCE, m, "id");
				String order = CH.getOrThrow(Caster_String.INSTANCE, m, "order");
				WebColumn column = table.getColumnNoThrow(id);
				if (column != null) {
					table.sortRows(column.getColumnId(), "asc".equals(order), true, !first);
					first = false;
				}
			}
		}
		this.table.setPinnedColumnsCount(CH.getOr(Caster_Integer.INSTANCE, configuration, "pinCnt", 0));

	}
	@Override
	public int getSuggestedWidth(PortletMetrics pm) {
		int w = 15;
		for (int i = 0; i < getTable().getVisibleColumnsCount(); i++)
			w += getTable().getVisibleColumn(i).getWidth();
		if (w < 200)
			w = 200;
		return w;
	}

	@Override
	public int getSuggestedHeight(PortletMetrics pm) {
		int h = 30;
		h += getTable().getRowsCount() * 20;
		if (h < 150)
			h = 150;
		return h;
	}

	private Map<String, Object> options = new HashMap<String, Object>();
	private int fontSize = 13;
	private int headerFontSize = 13;
	private String fontFamily;
	private MapInMap<Integer, String, String> editOrigValues;
	private FastTableEditListener editListener;
	private HashMap<String, WebColumnEditConfig> editColumns;

	public Object addOption(String key, Object value) {
		Object r = options.put(key, value);
		if (OH.eq(r, value))
			return r;
		this.optionsChanged = true;
		flagPendingAjax();
		return r;
	}
	public Object removeOption(String key) {
		Object r = options.remove(key);
		if (r == null) {
			return null;
		}
		this.optionsChanged = true;
		flagPendingAjax();
		return r;
	}
	public void clearOptions() {
		if (options.isEmpty()) {
			return;
		}
		this.options.clear();
		this.optionsChanged = true;
		flagPendingAjax();
	}
	public Object getOption(String option) {
		return options.get(option);
	}
	public String getOption(String option, String dflt) {
		return OH.noNull(CH.getOr(Caster_String.INSTANCE, options, option, null), dflt);
	}
	public Set<String> getOptions() {
		return options.keySet();
	}
	@Override
	public void onMenuItem(String id) {
		if ("copy_row".equals(id)) {
			StringBuilder sb = this.getManager().getPendingJs();
			sb.append(PortletHelper.createJsCopyToClipboard(this.copyBuilder[0]));
		}
		else if ("copy_cell".equals(id)) {
			StringBuilder sb = this.getManager().getPendingJs();
			sb.append(PortletHelper.createJsCopyToClipboard(this.copyBuilder[1]));
		}
		else if ("copy_advanced".equals(id)) {
			CopyPortlet fP = (new CopyPortlet(generateConfig(), new CopyableTableImpl(table), false)).setFormStyle(getFormStyle());
			getManager().showDialog("Copy", fP, 1100, 400).setStyle(getDialogStyle());
		} else if ("download_pipe".equals(id)) {
			doDownload(false);
		} else if ("download_selected_pipe".equals(id)) {
			doDownload(true);
		} else if ("download_csv".equals(id)) {
			doCsvDownload(false);
		} else if ("download_selected_csv".equals(id)) {
			doCsvDownload(true);
		} else {
			String title = this.getDownloadNameRealized();
			String ssfo = this.getManager().getUserConfigStore().getSettingString("spreadSheetFormatOption");
			boolean formatSheet = ssfo == null || OH.eq("always", ssfo);
			if ("download_ss".equals(id)) {
				SpreadSheetBuilder sb = new SpreadSheetBuilder();
				sb.addSheet(this, title, false, formatSheet);
				getManager().pushPendingDownload(new BasicPortletDownload(title + ".xlsx", sb.build()));
			} else if ("download_selected_ss".equals(id)) {
				SpreadSheetBuilder sb = new SpreadSheetBuilder();
				sb.addSheet(this, title, true, formatSheet);
				getManager().pushPendingDownload(new BasicPortletDownload(title + ".xlsx", sb.build()));
			} else if ("download_csv".equals(id)) {

			}
		}
	}

	private String downloadTitle;

	public String getDownloadNameRealized() {
		return (downloadTitle != null) ? downloadTitle : this.getTable().getTable().getTitle();
	}

	public String getDownloadName() {
		return this.downloadTitle;
	}
	public void setDownloadName(String downloadTitle) {
		this.downloadTitle = downloadTitle;
	}
	@Override
	public void onMenuDismissed() {
	}
	
	public BasicWebMenuLink toCopyLink(String title, String action) {
		int actionIdx;
		int col;
		if ("copy_row".equals(action)) {
			actionIdx = 0;
			col = -1;
		}
		else {
			actionIdx = 1;
			col = this.selectedCol;
		}
		this.copyBuilder[actionIdx] = new StringBuilder();
		List<Row> selRows = table.getSelectedRows();
		WebColumn[] columns = new WebColumn[getTable().getVisibleColumnsCount()];
		for (int i = 0; i < getTable().getVisibleColumnsCount(); i++) {
			columns[i] = getTable().getVisibleColumn(i);
		}
		int startCol, endCol;
		if (col == -1) {
			startCol = 0;
			endCol = getTable().getVisibleColumnsCount();
		} else {
			startCol = col;
			endCol = col + 1;
		}

		outer: for (int y = 0; y < selRows.size(); y++) {
			if (y > 0)
				this.copyBuilder[actionIdx].append(SH.CHAR_RETURN).append(SH.CHAR_NEWLINE);
			Row row = selRows.get(y);
			for (int i = startCol; i < endCol; i++) {
				if (i > startCol)
					this.copyBuilder[actionIdx].append(SH.CHAR_TAB);
				WebColumn webColumn = columns[i];
				webColumn.getCellFormatter().formatCellToText(webColumn.getData(row), this.copyBuilder[actionIdx]);
				if (this.copyBuilder[actionIdx].length() > MAX_CLIPBOARD_SIZE) {
					this.copyBuilder[actionIdx] = null;
					break outer;
				}
			}
		}

		if (this.copyBuilder[actionIdx] == null)
			return new BasicWebMenuLink(title + " (Too large for clipboard)", false, "");
		else
			return new BasicWebMenuLink(title, true, action);
	}
	public void autoSizeColumn(WebColumn webColumn) {
		if (webColumn.isFixedWidth())
			return;
		if (!"spark_line".equals(webColumn.getJsFormatterType()) && !"html".equals(webColumn.getJsFormatterType()) && !"checkbox".equals(webColumn.getJsFormatterType()))
			this.table.autoSizeColumn(webColumn, this.getManager().getPortletMetrics(), this.fontSize, MIN_AUTO_WIDTH, MAX_AUTO_WIDTH, this.headerFontSize);
	}
	public void autoSizeAllColumns() {
		for (int i = 0; i < table.getVisibleColumnsCount(); i++) {
			this.autoSizeColumn(table.getVisibleColumn(i));
		}
	}
	public boolean autoFitVisibleColumns() {
		int width = getWidth();
		if (hasVerticalScrollBar()) {
			int srollbarWidth = CH.getOr(Caster_Integer.INSTANCE, options, OPTION_SCROLL_BAR_WIDTH, 0);
			width -= srollbarWidth;
		}
		return this.table.autoFitVisibleColumns(width);
	}
	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	public void setHeaderFontSize(int headerFontSize) {
		this.headerFontSize = headerFontSize;
	}

	public int getFontSize() {
		return this.fontSize;
	}

	public void setFontFamily(String fontFamily) {
		this.fontFamily = fontFamily;
	}

	public String getFontFamily() {
		return this.fontFamily;
	}
	public PortletStyleManager_Dialog getDialogStyle() {
		return dialogStyle;
	}
	public FastTablePortlet setDialogStyle(PortletStyleManager_Dialog dialogStyle) {
		this.dialogStyle = dialogStyle;
		return this;
	}
	public PortletStyleManager_Form getFormStyle() {
		return formStyle;
	}
	public FastTablePortlet setFormStyle(PortletStyleManager_Form formStyle) {
		this.formStyle = formStyle;
		return this;
	}

	MapInMap<Integer, Integer, String> userEditValues = new MapInMap<Integer, Integer, String>();
	private int maxShowValuesForFilterDialog = TableFilterPortlet.MAX_VALUES_COUNT;

	private void onUserEditSubmit(List<Map<String, Object>> list, boolean isSubmit) {
		if (isEditing()) {
			for (Map<String, Object> m : list) {
				int x = CH.getOrThrow(Caster_Integer.INSTANCE, m, "x");
				int y = CH.getOrThrow(Caster_Integer.INSTANCE, m, "y");
				String v = CH.getOrThrow(Caster_String.INSTANCE, m, "v");
				userEditValues.putMulti(x, y, v);
				this.editListener.onEditCell(x, y, v);
			}
			if (isSubmit) {
				StringBuilder errorSink = new StringBuilder();
				Tuple2 origAndEditValuesTables = createOnEditTables(errorSink);
				Table origTable = (Table) origAndEditValuesTables.getA();
				Table editedTable = (Table) origAndEditValuesTables.getB();
				this.editListener.onTableEditComplete(origTable, editedTable, this, errorSink);
			}
		}
	}
	public Tuple2 createOnEditTables(StringBuilder errorSink) {
		if (this.editOrigValues == null)
			return null;
		Map<Integer, Map<String, String>> values = new HashMap<Integer, Map<String, String>>();
		Table bt = new BasicTable();
		Table origTable = new BasicTable();
		for (Entry<String, WebColumnEditConfig> i : this.editColumns.entrySet()) {
			WebColumnEditConfig c = i.getValue();
			if (c.getEditType() != WebColumnEditConfig.EDIT_DISABLED) {
				bt.addColumn(String.class, c.getEditId());
				origTable.addColumn(String.class, c.getEditId());
			}
		}
		for (Tuple3<Integer, String, String> i : this.editOrigValues.entrySetMulti()) {
			WebColumnEditConfig ec = this.editColumns.get(i.getB());
			int x = this.getTable().getColumnPosition(ec.getColumnId());
			int y = i.getA();
			String v = i.getC();
			if (ec.getEditType() != WebColumnEditConfig.EDIT_READONLY && x >= 0) {
				if (userEditValues.containsKey(x, y))
					v = userEditValues.getMulti(x, y);
			}
			if (ec.getEditType() == WebColumnEditConfig.EDIT_NUMERIC) {
				WebColumn col = this.getTable().getColumn(ec.getColumnId());
				WebCellFormatter cellFormatter = col.getCellFormatter();
				if (cellFormatter instanceof NumberWebCellFormatter) {
					Formatter formatter = ((NumberWebCellFormatter) cellFormatter).getFormatter();
					if (formatter instanceof BasicNumberFormatter) {
						BasicNumberFormatter bnf = (BasicNumberFormatter) formatter;
						NumberFormat nf = bnf.getNumberFormat();
						try {
							ParsePosition pp = new ParsePosition(0);
							Number n = nf.parse(v, pp);
							int parseStopIndex = pp.getIndex();
							if (parseStopIndex == v.length()) // parsing success
								v = n != null ? n.toString() : null;
							else
								throw new ParseException(v.toString(), parseStopIndex);
						} catch (ParseException e) {
							errorSink.append("Invalid format for numeric column type: <b>" + v + "</b> at position <b>" + e.getErrorOffset() + "</b>");
							LH.warning(log, "Invalid format for numeric column type: ", v, " at position ", e.getErrorOffset(), e);
							return new Tuple2<Table, Table>();
						} catch (Exception e) {
							errorSink.append("Something went wrong editing the table with value " + v);
							LH.warning(log, "Exception editing numeric column type with value ", v, e);
							return new Tuple2<Table, Table>();
						}
					}
				}
			}
			Map<String, String> row = values.get(y);
			if (row == null)
				values.put(y, row = new HashMap<String, String>());
			row.put(ec.getColumnId(), v);
		}
		for (Entry<Integer, Map<String, String>> i : values.entrySet()) {
			int y = i.getKey();
			Map<String, String> origVals = this.editOrigValues.get(y);
			Map<String, String> userVals = i.getValue();
			boolean hasChange = false;
			for (Entry<String, String> v : userVals.entrySet()) {
				String origVal = origVals.get(v.getKey());
				if (OH.ne(origVal, v.getValue())) {
					hasChange = true;
					break;
				}
			}
			if (hasChange) {
				final Row row = bt.newEmptyRow();
				final Row origRow = origTable.newEmptyRow();
				for (Entry<String, String> origKeyVal : origVals.entrySet()) {
					final String key = origKeyVal.getKey();
					final String val = userVals.containsKey(key) ? userVals.get(key) : origKeyVal.getValue();
					final String key2 = this.editColumns.get(key).getEditId();
					row.put(key2, val);
					origRow.put(key2, origKeyVal.getValue());
				}
				bt.getRows().add(row);
				origTable.getRows().add(origRow);
			}
		}
		Tuple2<Table, Table> tables = new Tuple2<Table, Table>();
		tables.setAB(origTable, bt);
		return tables;
	}
	public void startEdit(List<Row> rows, Map<String, ? extends WebColumnEditConfig> columns, FastTableEditListener listener) {
		if (this.isEditing())
			return;
		if (rows.size() == 0) {
			getManager().showAlert("Please select a row for editing first");
			return;
		}
		List<WebColumn> cols = new ArrayList<WebColumn>();
		List<WebColumnEditConfig> ccols = new ArrayList<WebColumnEditConfig>();
		IntArrayList pos = new IntArrayList();

		boolean hasEditColumn = false;
		for (int i = 0, l = this.getTable().getVisibleColumnsCount(); i < l; i++) {
			WebColumn col = getTable().getVisibleColumn(i);
			WebColumnEditConfig cc = columns.get(col.getColumnId());
			if (cc != null && cc.getEditType() != WebColumnEditConfig.EDIT_DISABLED) {
				if (cc.getEditType() != WebColumnEditConfig.EDIT_READONLY)
					hasEditColumn = true;
				cols.add(col);
				ccols.add(cc);
				pos.add(i);
			}
		}
		if (!hasEditColumn) {
			getManager().showAlert("There are no Editable columns visible");
			return;
		}
		for (int i = 0, l = this.getTable().getHiddenColumnsCount(); i < l; i++) {
			WebColumn col = getTable().getHiddenColumn(i);
			WebColumnEditConfig cc = columns.get(col.getColumnId());
			if (cc != null && cc.getEditType() != WebColumnEditConfig.EDIT_DISABLED) {
				cols.add(col);
				ccols.add(cc);
				pos.add(-1 - i);
			}
		}

		MapInMap<Integer, String, String> editValues = new MapInMap<Integer, String, String>();
		for (Row row : rows) {
			int y = row.getLocation();
			for (int i = 0; i < pos.size(); i++) {
				WebColumn col = cols.get(i);
				WebColumnEditConfig cc = ccols.get(i);
				String v = col.getCellFormatter().formatCellToText(col.getData(row));
				editValues.putMulti(y, cc.getColumnId(), v);

			}
		}

		/*
		JsFunction jsf = this.callJsFunction("editRows");
		JsonBuilder jb = jsf.startJson();
		jb.startList();
		MapInMap<Integer, String, String> editValues = new MapInMap<Integer, String, String>();
		for (Row row : rows) {
			int y = row.getLocation();
			for (int i = 0; i < pos.size(); i++) {
				int x = pos.get(i);
				WebColumn col = cols.get(i);
				WebColumnEditConfig cc = ccols.get(i);
				String v = col.getCellFormatter().formatCellToText(col.getData(row));
				editValues.putMulti(y, cc.getColumnId(), v);
				if (x < 0 || cc.getEditType() == WebColumnEditConfig.EDIT_READONLY)
					continue;
				jb.startMap();
				jb.addKeyValue("x", x);
				jb.addKeyValue("y", y);
				jb.addKeyValueQuoted("v", v);
				jb.addKeyValue("t", cc.getEditType());
		
				boolean hasEditOptions = false;
				if (cc.getEditType() == WebColumnEditConfig.EDIT_SELECT)
					hasEditOptions = true;
				else if (cc.getEditType() == WebColumnEditConfig.EDIT_COMBOBOX)
					hasEditOptions = true;
		
				if (hasEditOptions) {
					Object returnObject = listener.getEditOptions(cc, row);
					String optionsString = returnObject == null ? "" : SH.toString(returnObject);
					List<String> editSelectOptions = SH.splitToList(",", optionsString);
		
					if (editSelectOptions != null) {
						int maxLength = OH.min(500, editSelectOptions.size());
						jb.addKey("o").addQuoted(editSelectOptions.subList(0, maxLength));
					} else
						jb.addKey("o").addQuoted(new ArrayList<String>());
		
				}
				jb.endMap();
			}
		}
		jb.endList();
		jsf.end();
		*/
		this.editOrigValues = editValues;
		if (this.isEditing()) {
			if (!this.table.isPausedSort() && this.table.isKeepSorting())
				this.table.pauseSort(true);
			this.editListener = listener;
			this.editColumns = new HashMap<String, WebColumnEditConfig>(columns);
			this.userEditValues.clear();
			this.flagNeedEditRows = true;
			this.flagPendingAjax();
		}
	}

	private boolean flagNeedEditRows = false;
	private boolean flagNeedClearEditRows = false;
	private String quickFilterColumn = null;
	private TreeMap<String, String> quickFilterAutocomplete;

	private void callJsFunction_editRows() {
		if (!isEditing())
			return;
		JsFunction jsf = this.callJsFunction("editRows");
		JsonBuilder jb = jsf.startJson();
		jb.startList();
		int rc = this.table.getRowsCount();
		for (Tuple3<Integer, String, String> i : this.editOrigValues.entrySetMulti()) {
			int y = i.getA();
			String columnId = i.getB();
			String v = i.getC();
			WebColumnEditConfig cc = this.editColumns.get(columnId);
			int x = this.getTable().getColumnPosition(cc.getColumnId());
			if (x < 0 || cc.getEditType() == WebColumnEditConfig.EDIT_READONLY)
				continue;
			if (y >= rc)
				continue;
			jb.startMap();
			jb.addKeyValue("x", x);
			jb.addKeyValue("y", y);
			jb.addKeyValueQuoted("v", v);
			jb.addKeyValue("t", cc.getEditType());
			boolean hasEditOptions = false;
			if (cc.getEditType() == WebColumnEditConfig.EDIT_SELECT || cc.getEditType() == WebColumnEditConfig.EDIT_COMBOBOX
					|| cc.getEditType() == WebColumnEditConfig.EDIT_DATERANGE_FIELD || cc.getEditType() == WebColumnEditConfig.EDIT_DATE_FIELD)
				hasEditOptions = true;
			if (hasEditOptions) {
				Object returnObject = this.editListener.getEditOptions(cc, this.table.getRow(y));
				String optionsString = returnObject == null ? "" : SH.toString(returnObject);
				List<String> editSelectOptions = SH.splitToList(",", optionsString);

				if (editSelectOptions != null) {
					int maxLength = OH.min(500, editSelectOptions.size());
					jb.addKey("o").addQuoted(editSelectOptions.subList(0, maxLength));
				} else
					jb.addKey("o").addQuoted(new ArrayList<String>());
				// date range edit field
				if (cc.getEditType() == WebColumnEditConfig.EDIT_DATERANGE_FIELD || cc.getEditType() == WebColumnEditConfig.EDIT_DATE_FIELD) {
					// TODO add same options for right calendar for date range field
					jb.addKeyValue("dfd", cc.getDisableFutureDays());
					jb.addKeyValue("lnd", (long) (SH.is(cc.getEnableLastNDays()) ? Caster_Integer.PRIMITIVE.cast(cc.getEnableLastNDays()) : 0));
				}
			}
			jb.endMap();

		}
		jb.endList();
		jsf.end();
	}
	public boolean isEditing() {
		return this.editOrigValues != null;
	}
	public void finishEdit() {
		if (!isEditing())
			return;
		if (this.table.isPausedSort())
			this.table.pauseSort(false);
		callJsFunction("editRowsComplete").end();
		this.editListener = null;
		this.editColumns = null;
		this.editOrigValues = null;
		this.userEditValues.clear();
	}
	public String getDisplayTimeFormatted() {
		return displayTimeFormatted;
	}
	public void setDisplayTimeFormatted(String displayTimeFormatted) {
		this.displayTimeFormatted = displayTimeFormatted;
		flagPendingAjax();
		rowsCount = -1;
	}
	public int getMaxShowValuesForFilterDialog() {
		return this.maxShowValuesForFilterDialog;
	}
	public void setMaxShowValuesForFilterDialog(int maxShowValuesForFilterDialog) {
		this.maxShowValuesForFilterDialog = maxShowValuesForFilterDialog;
	}
	public void callSetAutocomplete(String columnId, Map<String, String> values) {
		if (values == null) {
			this.quickFilterColumn = columnId;
			this.quickFilterAutocomplete = null;
		} else {
			this.quickFilterColumn = columnId;
			this.quickFilterAutocomplete = new TreeMap<String, String>(values);
			this.flagPendingAjax();
		}
	}

	private QuickFilterAutocompleteManager quickfilterAutocompleteManager = new BasicQuickFilterAutocompleteManager();

	public QuickFilterAutocompleteManager getQuickfilterAutocompleteManager() {
		return quickfilterAutocompleteManager;
	}

	public void setQuickfilterAutocompleteManager(QuickFilterAutocompleteManager quickfilterAutocompleteManager) {
		this.quickfilterAutocompleteManager = quickfilterAutocompleteManager;
	}

	public void onQuickFilterUserAction(String columnId, String val, int i) {
		if (this.quickfilterAutocompleteManager != null)
			this.quickfilterAutocompleteManager.onQuickFilterUserAction(this, columnId, val, i);
	}
	public PortletStyleManager_Menu getMenuStyle() {
		return menuStyle;
	}
	public void setMenuStyle(PortletStyleManager_Menu menuStyle) {
		this.menuStyle = menuStyle;
	}

	public void setTooltipFactory(final WebTableTooltipFactory tooltipFactory) {
		this.tooltipFactory = tooltipFactory;
	}

	public WebTableTooltipFactory getTooltipFactory() {
		return this.tooltipFactory;
	}
	public boolean hasVerticalScrollBar() {
		int visibleRowsCount = Math.min(this.table.getClipZoneBottom() - this.table.getClipZoneTop() + 1, this.table.getRowsCount());
		return visibleRowsCount < this.table.getRowsCount();
	}

	public boolean hasHorizontalScrollBar() {
		// the # of columns that AMI is displaying
		int displayColumnCount = this.table.getVisibleColumnsCount();
		// the # of columns the user sees (restricted by window size)
		int visibleColumnCount = (this.table.getClipZoneRight() - this.table.getClipZoneLeft()) + 1;
		if (visibleColumnCount < displayColumnCount)
			return true;
		int curWidth = 0;
		int width = this.getWidth();
		if (hasVerticalScrollBar()) {
			// vertical scroll bar reduces available space of the last column
			int srollbarWidth = CH.getOr(Caster_Integer.INSTANCE, options, OPTION_SCROLL_BAR_WIDTH, 0);
			width -= srollbarWidth;
		}
		for (int i = 0; i < visibleColumnCount; i++) {
			WebColumn col = this.table.getVisibleColumn(i);
			curWidth += col.getWidth();
			if (curWidth > width)
				return true;
		}
		return false;
	}
	public void setScrollPosition(WebPoint pos) {
		this.getTable().setScrollPosition(pos, hasVerticalScrollBar(), hasHorizontalScrollBar());
	}
	public WebPoint getScrollPosition() {
		return this.getTable().getScrollPosition();
	}
	public void setSelectedColumn(int col) {
		this.selectedCol = col;
	}
}
