/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.table.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.suite.web.JsFunction;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.WebMenuLink;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuDivider;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.portal.impl.TableFilterPortlet;
import com.f1.suite.web.portal.impl.WebMenuListener;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebContextMenuFactory;
import com.f1.suite.web.table.WebContextMenuListener;
import com.f1.suite.web.table.WebTableColumnContextMenuFactory;
import com.f1.suite.web.table.WebTableColumnContextMenuListener;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.tree.impl.ArrangeColumnsPortlet;
import com.f1.utils.BundledTextFormatter;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_Long;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.json.JsonBuilder;
import com.f1.utils.structs.table.BasicTable;

public class WebTableCallbackProcessor {

	static public boolean processWebRequest(Map<String, String> properties, FastTablePortlet t, StringBuilder js) {
		FastWebTable table = t.getTable();
		String type = (String) properties.get("type");
		final PortletManager manager = t.getManager();
		if ("tableScroll".equals(type)) {
			int left = Caster_Integer.PRIMITIVE.cast(CH.getOrThrow(properties, "left"));
			int top = Caster_Integer.PRIMITIVE.cast(CH.getOrThrow(properties, "top"));
			int height = CH.getOr(Caster_Integer.INSTANCE, properties, "height", 0);
			long contentHeight = CH.getOr(Caster_Long.PRIMITIVE, properties, "contentHeight", 0L);
			long contentWidth = CH.getOr(Caster_Long.PRIMITIVE, properties, "contentWidth", 0L);
			int userSeqnum = Caster_Integer.INSTANCE.cast(CH.getOrThrow(properties, "userSeqnum"));
			table.setUserScrollSeqnum(userSeqnum);
			table.setTableLeft(left);
			table.setTableTop(top);
			table.setTableHeight(height);
			table.setContentWidth(contentWidth);
			table.setContentHeight(contentHeight);
		} else if ("tableSizeChanged".equals(type)) {
			int left = Caster_Integer.PRIMITIVE.cast(CH.getOrThrow(properties, "left"));
			int top = Caster_Integer.PRIMITIVE.cast(CH.getOrThrow(properties, "top"));
			int height = CH.getOr(Caster_Integer.PRIMITIVE, properties, "height", 0);
			long contentHeight = CH.getOr(Caster_Long.PRIMITIVE, properties, "contentHeight", 0L);
			long contentWidth = CH.getOr(Caster_Long.PRIMITIVE, properties, "contentWidth", 0L);
			table.setTableLeft(left);
			table.setTableTop(top);
			table.setTableHeight(height);
			table.setContentWidth(contentWidth);
			table.setContentHeight(contentHeight);
			table.fireOnScroll();
		} else if ("ArrowLeft".equals(type)) {
			int left = Caster_Integer.PRIMITIVE.cast(CH.getOrThrow(properties, "left"));
			t.getTable().scrollLeftColumn(left);
		} else if ("ArrowRight".equals(type)) {
			int left = Caster_Integer.PRIMITIVE.cast(CH.getOrThrow(properties, "left"));
			t.getTable().scrollRightColumn(left);
		} else if ("make_visible".equals(type)) {
			final int col = Caster_Integer.PRIMITIVE.cast(CH.getOrThrow(properties, "c"));
			final int rowPos = Caster_Integer.PRIMITIVE.cast(CH.getOrThrow(properties, "rp"));
			final boolean snapCol = CH.getOr(Caster_Boolean.INSTANCE, properties, "snapCol", Boolean.FALSE);
			final boolean snapRow = CH.getOr(Caster_Boolean.INSTANCE, properties, "snapRow", Boolean.FALSE);
			if (snapCol) {
				WebColumn column = t.getTable().getVisibleColumn(col);
				t.getTable().snapToColumn(column.getColumnId());
			}
			if (snapRow) {
				t.getTable().snapToRow(rowPos);
			}

		} else if (FastWebTable.CALLBACK_CLIPZONE.equals(type)) {
			int top = CH.getOr(Caster_Integer.PRIMITIVE, properties, "top", -1);
			int bottom = CH.getOr(Caster_Integer.PRIMITIVE, properties, "bottom", -1);
			int left = CH.getOr(Caster_Integer.PRIMITIVE, properties, "left", -1);
			int right = CH.getOr(Caster_Integer.PRIMITIVE, properties, "right", -1);
			int leftPin = CH.getOr(Caster_Integer.PRIMITIVE, properties, "leftPin", -1);
			int rightPin = CH.getOr(Caster_Integer.PRIMITIVE, properties, "rightPin", -1);
			table.setClipZone(top, bottom, left, right, leftPin, rightPin);
		} else if (FastWebTable.CALLBACK_CELL_CLICKED.equals(type)) {
			final int columnIndex = Caster_Integer.PRIMITIVE.cast(CH.getOrThrow(properties, "columnIndex"));
			final int rowIndex = Caster_Integer.PRIMITIVE.cast(CH.getOrThrow(properties, "rowIndex"));
			final boolean clickable = CH.getOr(Caster_Boolean.INSTANCE, properties, "clickable", Boolean.FALSE);
			final boolean oneClick = CH.getOr(Caster_Boolean.INSTANCE, properties, "oneClick", Boolean.FALSE);
			if (table.getTable().getSize() <= rowIndex)
				return true;
			Row row = table.getRow(rowIndex);
			WebColumn col = table.getVisibleColumn(columnIndex);
			List<WebContextMenuListener> listeners = table.getMenuListeners();
			for (WebContextMenuListener listener : listeners)
				listener.onCellMousedown(table, row, col);
			if (clickable || oneClick)
				for (WebContextMenuListener listener : listeners)
					listener.onCellClicked(table, row, col);
		} else if (FastWebTable.CALLBACK_COLUMN_WIDTH.equals(type)) {
			int columnIndex = Caster_Integer.PRIMITIVE.cast(CH.getOrThrow(properties, "columnIndex"));
			int width = Caster_Integer.PRIMITIVE.cast(CH.getOrThrow(properties, "width"));
			table.getVisibleColumn(columnIndex).setWidth(width);
			table.fireOnColumnsSized();
		} else if (FastWebTable.CALLBACK_PAGE.equals(type)) {
			int page = Caster_Integer.PRIMITIVE.cast(CH.getOrThrow(properties, "page"));
			table.setCurrentPage(page);
		} else if (FastWebTable.CALLBACK_USER_NAVIGATE.equals(type)) {
			int activeRow = CH.getOr(Caster_Integer.PRIMITIVE, properties, "activeRow", -1);
			String action = CH.getOrThrow(Caster_String.INSTANCE, properties, "action");
			if ("up".equals(action))
				table.userNavigate(activeRow, -1);
			else if ("dn".equals(action))
				table.userNavigate(activeRow, 1);
			else if ("text".equals(action) || "retext".equals(action)) {
				String text = CH.getOr(Caster_String.INSTANCE, properties, "text", null);
				if (SH.is(text))
					table.userNavigate(activeRow, text, "retext".equals(action));
			}
		} else if (FastWebTable.CALLBACK_USER_SELECT.equals(type)) {
			if (table.getRowsCount() > 0) {
				Integer activeRow = CH.getOr(Caster_Integer.PRIMITIVE, properties, "activeRow", -1);
				String selectedRows = Caster_String.INSTANCE.cast(CH.getOrThrow(properties, "selectedRows"));
				int userSeqnum = Caster_Integer.INSTANCE.cast(CH.getOrThrow(properties, "userSeqnum"));
				table.setActiveRowNoFire(activeRow);
				table.setSelectedRowsNoFire(selectedRows);
				table.setUserSelectSeqnum(userSeqnum);
			}
		} else if (FastWebTable.CALLBACK_COLUMNS.equals(type)) {
			String[] columns = SH.split(',', Caster_String.INSTANCE.cast(CH.getOrThrow(properties, "columns")));
			while (table.getVisibleColumnsCount() > 0)
				table.hideColumn(table.getVisibleColumn(0).getColumnId());
			for (String column : columns)
				table.showColumn(column);
			table.fireOnColumnsArranged();
		} else if (FastWebTable.CALLBACK_EXPORT.equals(type)) {
			int colsCount = table.getVisibleColumnsCount();
			String[] colNames = new String[colsCount];
			Class[] colTypes = new Class[colsCount];
			for (int i = 0; i < colsCount; i++) {
				colNames[i] = table.formatText(table.getVisibleColumn(i).getColumnName());
				colTypes[i] = String.class;
			}
			Table t2 = new BasicTable(colTypes, colNames);
			StringBuilder sb = new StringBuilder();
			for (int y = 0, l = table.getRowsCount(); y < l; y++) {
				Object[] row = new Object[colsCount];
				for (int i = 0; i < colsCount; i++) {
					row[i] = table.getValueAsText(table.getRow(y), i, sb).toString();
					sb.setLength(0);
				}
				t2.getRows().addRow(row);
			}
		} else if (FastWebTable.CALLBACK_SEARCH.equals(type)) {
			String expression = Caster_String.INSTANCE.cast(CH.getOrThrow(properties, "expression"));
			table.setSearch(expression);
		} else if (FastWebTable.CALLBACK_FILTER.equals(type)) {
			Set<String> values = CH.s(SH.splitWithEscape(',', '\\', Caster_String.INSTANCE.cast(CH.getOrThrow(properties, "values"))));
			int columnIndex = Caster_Integer.PRIMITIVE.cast(CH.getOrThrow(properties, "columnIndex"));
			WebColumn column = table.getVisibleColumn(columnIndex);
			table.setFilteredIn(column.getColumnId(), values);
		} else if ("openFilter".equals(type)) {
			showFilterDialog(properties, t, js);
		} else if (FastWebTable.CALLBACK_SHOWHEADERMENU.equals(type)) {
			int col = CH.getOrThrow(Caster_Integer.INSTANCE, properties, "col");
			WebColumn column;
			BasicWebMenu menu = new BasicWebMenu();
			if (col == -2) {
				column = null;
				t.addDownloadMenuItems(menu);
				menu.addChild(new BasicWebMenuLink("Arrange Columns...", true, "__arrange"));
				menu.addChild(new BasicWebMenuLink("Auto-Size All Columns", true, "__autosizeall"));
				menu.addChild(new BasicWebMenuLink("Auto-Fit All Columns", true, "__autofitall"));
			} else {
				column = table.getVisibleColumn(col);
				boolean isPrimarySort = table.isPrimarySort(column.getColumnId());
				Set<String> sorted = table.getSortedColumnIds();
				boolean keepSorting = table.getTable().getKeepSorting();
				menu.addChild(new BasicWebMenuLink("Sort Ascending", true, "__sort_3"));
				menu.addChild(new BasicWebMenuLink("Sort Descending", true, "__sort_2"));
				menu.addChild(new BasicWebMenuLink("Secondary Sort Ascending", !sorted.isEmpty() && keepSorting && !isPrimarySort, "__sort_5"));//add=true,ks=false,ascend=true, mask=2^2+2^0=5
				menu.addChild(new BasicWebMenuLink("Secondary Sort Descending", !sorted.isEmpty() && keepSorting && !isPrimarySort, "__sort_4"));//add=true,ks=false,ascend=false, mask=2^2=4
				menu.addChild(new BasicWebMenuLink("Sort Ascending Once", true, "__sort_1"));
				menu.addChild(new BasicWebMenuLink("Sort Descending Once", true, "__sort_0"));
				menu.addChild(new BasicWebMenuLink("Clear All Sorts", table.isKeepSorting(), "__clearSort"));
				menu.addChild(new BasicWebMenuDivider());
				menu.addChild(new BasicWebMenuLink("Pin To This Column", true, "__pin"));
				menu.addChild(new BasicWebMenuLink("Clear Pinning", table.getPinnedColumnsCount() > 0, "__unpin"));
				menu.addChild(new BasicWebMenuDivider());
				boolean filtered = table.getFilteredInColumns().contains(column.getColumnId());
				boolean hasFilter = !table.getFilteredInColumns().isEmpty();
				menu.addChild(new BasicWebMenuLink("Auto-Size All Columns", true, "__autosizeall"));
				menu.addChild(new BasicWebMenuLink("Auto-Size This Column", true, "__autosize"));
				menu.addChild(new BasicWebMenuLink("Auto-Fit All Columns", true, "__autofitall"));
				menu.addChild(new BasicWebMenuLink("Filter...", true, "__filter"));
				menu.addChild(new BasicWebMenuLink("Clear Filter", filtered, "__clearfilter"));
				menu.addChild(new BasicWebMenuLink("Clear All Filters", hasFilter, "__clearAllFilter"));
				menu.addChild(new BasicWebMenuLink("Arrange Columns...", true, "__arrange"));
				menu.addChild(new BasicWebMenuLink("Hide This Column", true, "__hide"));
			}

			WebTableColumnContextMenuFactory factory = table.getColumnMenuFactory();
			WebMenu menu2;
			if (factory != null) {
				if (column == null)
					menu2 = factory.createColumnMenu(table, menu);
				else
					menu2 = factory.createColumnMenu(table, column, menu);
			} else
				menu2 = menu;

			Map<String, Object> menuModel = PortletHelper.menuToJson(manager, menu2);
			JsFunction jsf = new JsFunction(js, table.getJsTableName(), "showContextMenu");
			jsf.addParamJson(menuModel);
			jsf.end();
		} else if (FastWebTable.CALLBACK_SHOWMENU.equals(type)) {
			WebContextMenuFactory menuFactory = table.getMenuFactory();
			if (menuFactory != null) {
				WebMenu menu = menuFactory.createMenu(table);
				if (menu != null && menu.getChildren().size() > 0) {
					Map<String, Object> menuModel = PortletHelper.menuToJson(manager, menu);
					JsFunction jsf = new JsFunction(js, table.getJsTableName(), "showContextMenu");
					jsf.addParamJson(menuModel);
					jsf.end();
				}
			}
		} else if (FastWebTable.CALLBACK_HEADERMENUITEM.equals(type)) {
			String menuAction = CH.getOrThrow(properties, "action");
			if (!menuAction.startsWith("__")) {
				WebMenuLink link = manager.getMenuManager().fireLinkForId(menuAction);
				menuAction = link == null ? null : link.getAction();
			}
			int col = Caster_Integer.INSTANCE.cast(CH.getOrThrow(properties, "col"));
			if (col == -2) {
				if (SH.startsWith(menuAction, "__")) {
					if (menuAction.startsWith("__arrange")) {
						t.getManager().showDialog("Arrange Columns", (new ArrangeColumnsPortlet(t.generateConfig(), t.getTable())).setFormStyle(t.getFormStyle()))
								.setStyle(t.getDialogStyle());
					} else if ("__autosizeall".equals(menuAction)) {
						t.autoSizeAllColumns();
					} else if ("__autofitall".equals(menuAction)) {
						t.autoFitVisibleColumns();
					}
				}
				if (SH.startsWith(menuAction, "download"))
					t.onMenuItem(menuAction);
				List<WebTableColumnContextMenuListener> listeners = table.getColumnMenuListeners();
				if (CH.isntEmpty(listeners)) {
					for (WebTableColumnContextMenuListener listener : listeners)
						listener.onColumnContextMenu(table, null, menuAction);
				}
			} else {
				WebColumn column = table.getVisibleColumn(col);
				if (SH.startsWith(menuAction, "__")) {
					if (menuAction.equals("__help")) {
						showHelp(properties, table, js);
					} else if (menuAction.equals("__filter")) {
						showFilterDialog(properties, t, js);
					} else if (menuAction.equals("__clearSort")) {
						String cid = column.getColumnId();
						// clear the triangle icon on column header
						table.sortRows(cid, true, false, false);
						// clear sortedColumnIds
						table.clearSort();
					} else if (menuAction.equals("__hide")) {
						String cid = column.getColumnId();
						table.hideColumn(cid);
						table.fireOnColumnsArranged();
					} else if (menuAction.equals("__clearfilter")) {
						table.setFilteredIn(column.getColumnId(), (Set<String>) null);
					} else if (menuAction.equals("__clearAllFilter")) {
						Set<String> toRemove = table.getFilteredInColumns();
						table.clearFilters(toRemove);
					} else if (menuAction.startsWith("__sort_")) {
						int sortType = SH.parseInt(SH.stripPrefix(menuAction, "__sort_", true));
						String cid = column.getColumnId();
						table.sortRows(cid, (sortType & FastWebTable.ASCEND) == FastWebTable.ASCEND, (sortType & FastWebTable.KEEP_SORT) == FastWebTable.KEEP_SORT,
								(sortType & FastWebTable.ADD) == FastWebTable.ADD);
					} else if (menuAction.startsWith("__arrange")) {
						t.getManager().showDialog("Arrange Columns", (new ArrangeColumnsPortlet(t.generateConfig(), t.getTable(), column)).setFormStyle(t.getFormStyle()))
								.setStyle(t.getDialogStyle());
					} else if (menuAction.equals("__pin")) {
						table.setPinnedColumnsCount(table.getColumnPosition(column.getColumnId()) + 1);
					} else if (menuAction.equals("__unpin")) {
						table.setPinnedColumnsCount(0);
					} else if ("__autosizeall".equals(menuAction)) {
						t.autoSizeAllColumns();
					} else if ("__autosize".equals(menuAction)) {
						t.autoSizeColumn(column);
					} else if ("__draggingEnd".equals(menuAction)) { // important: this gets called only on certain column formatting type (ie. spark-charts) 
						table.updateColumn(column);
					} else if ("__autofitall".equals(menuAction)) {
						t.autoFitVisibleColumns();
					}
				} else {
					List<WebTableColumnContextMenuListener> listeners = table.getColumnMenuListeners();
					if (CH.isntEmpty(listeners)) {
						for (WebTableColumnContextMenuListener listener : listeners)
							listener.onColumnContextMenu(table, column, menuAction);
					}
				}
			}
		} else if (FastWebTable.CALLBACK_MENUITEM.equals(type)) {
			final WebMenuLink menuAction = manager.getMenuManager().fireLinkForId(CH.getOrThrow(properties, "action"));
			if (menuAction != null) {
				List<WebContextMenuListener> listeners = table.getMenuListeners();
				if (CH.isntEmpty(listeners)) {
					for (WebContextMenuListener listener : listeners)
						listener.onContextMenu(table, menuAction.getAction());
				}
			}
		} else if (FastWebTable.CALLBACK_ROWS_COPIED.equals(type)) {
			BasicWebMenu copyMenu = new BasicWebMenu();
			int col = Integer.parseInt(properties.get("col"));
			List<Row> selRows = t.getTable().getSelectedRows();
			if (!selRows.isEmpty()) {
				t.setSelectedColumn(col);
				copyMenu.add(t.toCopyLink(selRows.size() == 1 ? "Copy <U>R</U>ow" : "Copy <U>R</U>ows", "copy_row").setKeystroke('r'));
				copyMenu.add(t.toCopyLink(selRows.size() == 1 ? "Copy <U>C</U>ell" : "Copy <U>C</U>olumn", "copy_cell").setKeystroke('c'));
				copyMenu.add(new BasicWebMenuDivider());
			}
			copyMenu.add(new BasicWebMenuLink("Copy <U>A</U>dvanced...", true, "copy_advanced").setKeystroke('a'));
			t.getManager().showContextMenu(copyMenu, (WebMenuListener) t);

		} else if (FastWebTable.CALLBACK_DBL_CLICK.equals(type)) {
			final String action = CH.getOrThrow(properties, "action");
			table.fireOnUserDblclick(action, properties);
		} else if (FastWebTable.CALLBACK_MOVE_COLUMN.equals(type)) {
			final int oldPos = CH.getOrThrow(Caster_Integer.INSTANCE, properties, "oldPos");
			final int newPos = CH.getOrThrow(Caster_Integer.INSTANCE, properties, "newPos");
			FastWebTable t2 = t.getTable();
			int pcc = t2.getPinnedColumnsCount();
			WebColumn col = t2.getVisibleColumn(oldPos);
			t2.hideColumn(col.getColumnId());
			t2.showColumn(col.getColumnId(), newPos);
			if (pcc > oldPos && pcc <= newPos)
				t2.setPinnedColumnsCount(pcc - 1);
			else if (pcc <= oldPos && pcc > newPos)
				t2.setPinnedColumnsCount(pcc + 1);
			else
				t2.setPinnedColumnsCount(pcc);
			t2.fireOnColumnsArranged();
		} else if (FastWebTable.CALLBACK_COLUMN_FILTER.equals(type)) {
			final int pos = CH.getOrThrow(Caster_Integer.INSTANCE, properties, "pos");
			String val = CH.getOrThrow(Caster_String.INSTANCE, properties, "val");
			WebColumn col = t.getTable().getVisibleColumn(pos);
			t.getTable().setFilteredExpression(col.getColumnId(), val);
		} else if (FastWebTable.CALLBACK_GET_COLUMN_FILTER_OPTIONS.equals(type)) {
			final int pos = CH.getOrThrow(Caster_Integer.INSTANCE, properties, "pos");
			String val = CH.getOrThrow(Caster_String.INSTANCE, properties, "val");
			WebColumn col = t.getTable().getVisibleColumn(pos);
			t.onQuickFilterUserAction(col.getColumnId(), val, 20);
		} else
			return false;
		return true;
	}
	private static void showFilterDialog(Map<String, String> properties, FastTablePortlet ftp, StringBuilder js) {
		FastWebTable table = ftp.getTable();
		int columnIndex = Caster_Integer.PRIMITIVE.cast(CH.getOrThrow(properties, "col"));
		WebColumn column = table.getVisibleColumn(columnIndex);
		TableFilterPortlet tfp = new TableFilterPortlet(ftp.generateConfig(), ftp, column);
		ftp.getManager().showDialog("Filter - " + column.getColumnName(), tfp.setFormStyle(ftp.getFormStyle())).setStyle(ftp.getDialogStyle());
		return;
	}

	private static void showHelp(Map<String, String> properties, FastWebTable table, StringBuilder js) {
		JsFunction jsf = new JsFunction(js, table.getJsTableName(), "showHelp");
		JsonBuilder json = jsf.startJson();
		String[] menuFunctions = new String[] { "Sort Ascending Once", "Sort Descending Once", "Sort Ascending", "Sort Descending", "Sub-Sort Ascending", "Sub-Sort Descending",
				"Arrange Columns", "Adjust Filter", "Clear Filter", "Clear All Filters", "Filter" };
		json.startList();
		{
			json.startMap();
			json.addKeyValueQuoted("title", table.formatText("Column Menu options"));
			json.addKey("items");
			json.startList();
			for (String menu : menuFunctions) {
				json.startMap();
				json.addKeyValueQuoted("title", table.formatText(menu));
				json.addKeyValueQuoted("help", table.formatText(menu + BundledTextFormatter.HELP_SUFFIX));
				json.endMap();
			}
			json.endList();
			json.endMap();
		}
		{
			json.startMap();
			json.addKeyValueQuoted("title", table.formatText("Visible Columns"));
			json.addKey("items");
			json.startList();
			for (int i = 0; i < table.getVisibleColumnsCount(); i++) {
				WebColumn col = table.getVisibleColumn(i);
				json.startMap();
				json.addKeyValueQuoted("title", table.formatText(col.getColumnName()));
				json.addKeyValueQuoted("help", table.formatText(col.getColumnName() + BundledTextFormatter.HELP_SUFFIX));
				json.endMap();
			}
			json.endList();
			json.endMap();
		}
		{
			json.startMap();
			json.addKeyValueQuoted("title", table.formatText("Hidden Columns"));
			json.addKey("items");
			json.startList();
			for (int i = 0; i < table.getHiddenColumnsCount(); i++) {
				WebColumn col = table.getHiddenColumn(i);
				json.startMap();
				json.addKeyValueQuoted("title", table.formatText(col.getColumnName()));
				json.addKeyValueQuoted("help", table.formatText(col.getColumnName() + BundledTextFormatter.HELP_SUFFIX));
				json.endMap();
			}
			json.endList();
			json.endMap();
		}
		json.endList();
		json.close();
		jsf.end();
	}
}
