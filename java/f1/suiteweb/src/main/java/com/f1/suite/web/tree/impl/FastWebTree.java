package com.f1.suite.web.tree.impl;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;

import com.f1.base.IntIterator;
import com.f1.base.IterableAndSize;
import com.f1.suite.web.JsFunction;
import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.WebMenuLink;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuDivider;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.PortletMetrics;
import com.f1.suite.web.portal.impl.FastTreePortlet;
import com.f1.suite.web.portal.impl.WebMenuListener;
import com.f1.suite.web.table.WebTreeListener;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.CopyPortlet;
import com.f1.suite.web.tree.WebTreeColumnContextMenuListener;
import com.f1.suite.web.tree.WebTreeColumnMenuFactory;
import com.f1.suite.web.tree.WebTreeContextMenuFactory;
import com.f1.suite.web.tree.WebTreeContextMenuListener;
import com.f1.suite.web.tree.WebTreeFilter;
import com.f1.suite.web.tree.WebTreeManager;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.WebTreeNodeFormatter;
import com.f1.suite.web.tree.WebTreeRowFormatter;
import com.f1.utils.CH;
import com.f1.utils.Formatter;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.assist.RootAssister;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.formatter.BasicTextFormatter;
import com.f1.utils.impl.ConstTextMatcher;
import com.f1.utils.structs.BasicIndexedList;
import com.f1.utils.structs.IndexedList;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.IntSet;

public class FastWebTree implements WebMenuListener, FastWebColumns {
	// a lot of things here are in common with FastWebTable, perhaps we can merge
	private static final int CHANGED_ALL = 1 << 0;
	private static final int CHANGED_CLIPZONE = 1 << 1;
	private static final int CHANGED_ROWS = 1 << 2;
	private static final int CHANGED_DATA = 1 << 3;
	private static final int CHANGED_ROW_HEIGHT = 1 << 4;
	private static final int CHANGED_ROWS_ALL = 1 << 5;
	private static final int CHANGED_ACTIVE_ROW = 1 << 6;
	//public static final int CHANGED_COLUMNS = 1 << 10;

	public static final byte SELECTION_MODE_STANDARD = 1;
	public static final byte SELECTION_MODE_TOGGLE = 2;
	public static final byte SELECTION_MODE_NONE = 3;
	public static final int TREE_COLUMNID = 0;
	private static final int MAX_CLIPBOARD_SIZE = 1024 * 1024;
	public static final int KEEP_SORT = 2;
	public static final int ADD = 4;
	public static final int ASCEND = 1;
	private int pinnedColumnsCount = 0;
	final private BasicWebTreeManager treeManager;
	private int changedRangeTop = -1;
	private int changedRangeBottom = -1;
	private IntKeyMap<WebTreeNode> updated = new IntKeyMap<WebTreeNode>();
	private String jsObjectName;
	private BitSet cached = new BitSet();
	private boolean isVisible = true;
	private int changes = 0;
	private JsFunction jsFunction;
	private int clipZoneTop;
	private int clipZoneBottom;

	private int lastSelected = -1;
	private int rowHeight = 18;
	private int headerRowHeight = 18;
	private List<WebTreeListener> listeners = new ArrayList<WebTreeListener>();
	private List<WebTreeContextMenuListener> menuListeners = new ArrayList<WebTreeContextMenuListener>();
	private boolean rootLevelVisible = true;
	private boolean autoExpandUntilMultipleNodes = true;
	private boolean autoCollapseChildren = true;
	private WebTreeContextMenuFactory contextMenuFactory;
	private WebTreeNodeFormatter formatter;
	private byte selectionMode = SELECTION_MODE_STANDARD;

	public static final int GROUPING_COLUMN_ID = 0;

	private IndexedList<Integer, FastWebTreeColumn> visibleColumns = new BasicIndexedList<Integer, FastWebTreeColumn>();
	private IndexedList<Integer, FastWebTreeColumn> hiddenColumns = new BasicIndexedList<Integer, FastWebTreeColumn>();
	private IntKeyMap<FastWebTreeColumn> columns = new IntKeyMap<FastWebTreeColumn>();
	private FastWebTreeColumn treeColumn = new FastWebTreeColumn(GROUPING_COLUMN_ID, new BasicWebTreeNodeFormatter(new BasicTextFormatter()), "", 200, "", null, true);
	final private Map<Integer, WebTreeFilteredInFilter> filteredIn = new HashMap<Integer, WebTreeFilteredInFilter>();
	private LinkedHashMap<Integer, Integer> toSort = new LinkedHashMap<Integer, Integer>();

	private WebTreeRowFormatter rowFormatter = null;
	private String search;
	private int visibleColumnsLimit = -1;
	private Integer selectedCol = null;
	private StringBuilder[] copyBuilder;

	public FastWebTree(BasicWebTreeManager treeManager, WebTreeNodeFormatter formatter) {
		this.treeColumn.setTree(this);
		this.treeManager = treeManager;
		((BasicWebTreeManager) this.treeManager).setWebTree(this);
		this.formatter = formatter;
		this.reset();
		this.copyBuilder = new StringBuilder[2];
	}

	public FastWebTree(Formatter formatter) {
		this(new BasicWebTreeManager(), new BasicWebTreeNodeFormatter(formatter));
	}

	public WebTreeManager getTreeManager() {
		return treeManager;
	}

	public void setFormatter(WebTreeNodeFormatter format) {
		if (format == null)
			throw new NullPointerException("format");
		this.formatter = format;
		changed(CHANGED_ALL);
	}
	public WebTreeNodeFormatter getFormatter() {
		return this.formatter;
	}

	public void onNodeRemoved(WebTreeNode node) {
		updated.remove(node.getUid());
		if (node.getChildrenCount() > 0)
			for (WebTreeNode i : node.getChildren())
				onNodeRemoved(i);
	}
	public void onStyleChanged(WebTreeNode node) {
		if (MH.areAnyBitsSet(changes, CHANGED_ALL | CHANGED_ROWS_ALL))
			return;
		if (getAbsolutePosition(node) != -1) {
			updated.put(node.getUid(), node);
			changed(CHANGED_DATA);
		}
	}
	public void onCheckedChanged(WebTreeNode node) {
		if (MH.areAnyBitsSet(changes, CHANGED_ALL | CHANGED_ROWS_ALL))
			return;
		if (getAbsolutePosition(node) != -1) {
			updated.put(node.getUid(), node);
			changed(CHANGED_DATA);
		}

	}

	public void onExpanded(WebTreeNode node) {
		if (MH.areAnyBitsSet(changes, CHANGED_ALL | CHANGED_ROWS_ALL))
			return;
		if (getAbsolutePosition(node) != -1)
			updated.put(node.getUid(), node);
		changed(CHANGED_DATA);
	}

	private void reset() {
		updated.clear();
		cached.clear();
		changedRangeTop = -1;
		changedRangeBottom = -1;
		//		this.moveToScrollPos = -1;
		changes = CHANGED_ALL;
	}

	protected void onNodesAddedToVisible(WebTreeNode[] nodes, int start, int end) {
		if (MH.areAnyBitsSet(changes, CHANGED_ALL | CHANGED_ROWS_ALL))
			return;

		boolean changed = false;
		for (int i = start; i < end; i++) {
			WebTreeNode n = nodes[i];
			int p = getAbsolutePosition(n);
			if (changedRangeTop == -1 || p < changedRangeTop) {
				changedRangeTop = p;
				changed = true;
			}
			if (changedRangeBottom == -1 || p > changedRangeBottom) {
				changedRangeBottom = p;
				changed = true;
			}
		}
		if (changed)
			changed(CHANGED_ROWS);
	}
	public void onAllNodesRemoved() {
		changed(CHANGED_ROWS_ALL);
	}

	protected void onRemovingNodesFromVisible(WebTreeNode[] nodes, int start, int end) {
		if (MH.areAnyBitsSet(changes, CHANGED_ALL | CHANGED_ROWS_ALL))
			return;
		if (updated.size() > 0) {
			for (WebTreeNode n : nodes)
				updated.remove(n.getUid());
		}
		boolean changed = false;
		for (int i = start; i < end; i++) {
			WebTreeNode n = nodes[i];
			int p = getAbsolutePosition(n);
			if (changedRangeTop == -1 || p < changedRangeTop) {
				changedRangeTop = p;
				changed = true;
			}
			if (changedRangeBottom == -1 || p > changedRangeBottom) {
				changedRangeBottom = p;
				changed = true;
			}
		}
		if (changed)
			changed(CHANGED_ROWS);
	}

	public void setJsObjectName(String name) {
		this.jsObjectName = name;
		this.jsFunction = new JsFunction(name);
	}

	public String getJsObjectName() {
		return jsObjectName;
	}

	public void setIsVisible(boolean isVisible) {
		if (this.isVisible == isVisible)
			return;
		this.isVisible = isVisible;
		reset();
		changed(CHANGED_ALL);
	}

	private void changed(int mask) {
		if (MH.allBits(changes, mask))
			return;
		changes |= mask;
		this.parentPortlet.onChange(this);
	}

	int lastSizeSent = -1;

	public StringBuilder createJs(StringBuilder js) {
		if (moveToScrollPos != -1) {
			int moveToScrollPos = MH.clip(this.moveToScrollPos, 0, treeManager.getVisibleNodesCount());
			WebTreeNode node = this.treeManager.getVisibleTreeNodeAt(moveToScrollPos);
			if (node != null) {
				node.setSelected(true);
			}
		}
		if (MH.anyBits(changes, CHANGED_ACTIVE_ROW | CHANGED_ALL)) {
			jsFunction.reset(js, "setActiveRowUid").addParam(this.treeManager.getActiveSelectedNodeUid()).end();
		}
		//Need to relook to use CHANGED_COLUMNS
		//if (MH.anyBits(changes, CHANGED_ALL | CHANGED_COLUMNS)) {
		if (MH.anyBits(changes, CHANGED_ALL)) {
			this.lastSizeSent = -1;
			cached.clear();
			createJsClearData(0, js);
			createJsInitColumns(js);
			createJsSetRowHeight(rowHeight, js);
			drawClipzone(js);
			//			createJsSetSize(getVisibleNodesCount(), js);
		} else if (MH.anyBits(changes, CHANGED_CLIPZONE)) {
			boolean needsUpdate = drawClipzoneUpdate(js);
			//			createJsSetSize(getVisibleNodesCount(), js);
			if (MH.anyBits(changes, CHANGED_DATA)) {
				for (WebTreeNode node : updated.values()) {
					int pos = getAbsolutePosition(node);
					if (pos >= 0 && cached.get(pos)) {
						if (OH.isBetween(pos, this.clipZoneTop, this.clipZoneBottom)) {
							createJsAddNode(node, js);
							needsUpdate = true;
						} else
							cached.clear(pos);
					}
				}
				//TODO: recalculate maxWidth??
				updated.clear();
			}
			if (needsUpdate)
				createJsUpdateCells(js);
		} else if (MH.anyBits(changes, CHANGED_ROWS_ALL)) {
			createJsClearData(0, js);
			cached.clear();
			drawClipzone(js);
			//			createJsSetSize(getVisibleNodesCount(), js);
		} else if (MH.anyBits(changes, CHANGED_ROWS)) {
			if (changedRangeTop < cached.size() && changedRangeTop >= 0)
				cached.clear(this.changedRangeTop, cached.size());
			createJsClearData(Math.max(0, this.changedRangeTop), js);
			drawClipzone(js);
		} else if (MH.anyBits(changes, CHANGED_DATA)) {
			for (WebTreeNode node : updated.values()) {
				int pos = getAbsolutePosition(node);
				if (pos >= 0 && cached.get(pos)) {
					if (OH.isBetween(pos, this.clipZoneTop, this.clipZoneBottom)) {
						createJsAddNode(node, js);
					} else
						cached.clear(pos);
				}
			}
			//TODO: recalculate maxWidth??
			updated.clear();
			createJsUpdateCells(js);
		}
		int visibleNodesCount = getVisibleNodesCount();
		if (this.lastSizeSent != visibleNodesCount) {
			createJsSetSize(visibleNodesCount, js);
			this.lastSizeSent = visibleNodesCount;
		}
		if (moveToScrollPos != -1) {
			moveToScrollPos = MH.clip(moveToScrollPos, 0, treeManager.getVisibleNodesCount());
			createJsMovedToScrollPos(moveToScrollPos, js);
		}
		moveToScrollPos = -1;
		this.changedRangeBottom = -1;
		this.changedRangeTop = -1;
		changes = 0;
		return js;
	}
	private void createJsMovedToScrollPos(int pos, StringBuilder js) {
		WebTreeNode node = this.treeManager.getVisibleTreeNodeAt(pos);
		if (node != null) {
			node.setSelected(true);
		}
		jsFunction.reset(js, "ensurePosVisible").addParam(pos).end();

	}

	private void createJsInitColumns(StringBuilder js) {
		jsFunction.reset(js, "initColumns");
		jsFunction.addParam(this.getPinnedColumnsCount());
		List<Map<Object, Object>> columns = new ArrayList<Map<Object, Object>>();
		for (int i = -1, l = visibleColumns.getSize(); i < l; i++) {
			FastWebTreeColumn column = i == -1 ? treeColumn : visibleColumns.getAt(i);
			Boolean ascending = CH.getOr(sortedColumnIds, column.getColumnId(), null);
			String sort = "";
			if (ascending != null)
				sort = String.valueOf((ascending ? FastWebTable.ASCEND : 0) + (getKeepSorting() ? FastWebTable.KEEP_SORT : 0));
			WebTreeFilteredInFilter filterSet = filteredIn.get(column.getColumnId());
			boolean isFiltered = filterSet != null;
			String filter = isFiltered ? filterSet.getSimpleValue() : null;
			columns.add(CH.m("name", column.getColumnName(), "cssClass", column.getColumnCssClass(), "sort", sort, "id", column.getColumnId(), "visible", true, "filter",
					isFiltered, "width", column.getWidth(), "headerStyle", column.getHeaderStyle(), "jsFormatterType", column.getJsFormatterType(), "filterText", filter, "hids",
					this.htmlIdSelectorForColumns.get(SH.toString(column.getColumnId()))));
		}
		jsFunction.addParam(RootAssister.INSTANCE.toJson(columns));
		jsFunction.end();

	}

	private int indentPx = 24;
	private int leftPaddingPx = 4;
	private int topPaddingPx = 4;
	private int fontSize = 10;

	private int maxWidth;

	private boolean drawClipzoneUpdate(StringBuilder js) {
		maxWidth = 1;
		boolean r = false;
		int clipVisible = this.clipZoneBottom - this.clipZoneTop + 1;
		for (int i = Math.max(this.clipZoneTop - clipVisible, 0), l = Math.min(clipZoneBottom + clipVisible, getVisibleNodesCount()); i < l; i++) {
			WebTreeNode node = getVisibleTreeNodeAt(i);
			if (!cached.get(i)) {
				cached.set(i);
				createJsAddNode(node, js);
				r = true;
			}
		}

		return r;
	}

	private void drawClipzone(StringBuilder js) {
		maxWidth = 1;
		int clipVisible = this.clipZoneBottom - this.clipZoneTop + 1;
		for (int i = Math.max(clipZoneTop - clipVisible, 0), l = Math.min(clipZoneBottom + clipVisible, getVisibleNodesCount()); i < l; i++) {
			WebTreeNode node = getVisibleTreeNodeAt(i);
			cached.set(i);
			createJsAddNode(node, js);
		}
		createJsUpdateCells(js);
	}

	public WebTreeNode getVisibleTreeNodeAt(int i) {
		if (!rootLevelVisible)
			i++;
		return this.treeManager.getVisibleTreeNodeAt(i);
	}

	public int getVisibleNodesCount() {
		int r = this.treeManager.getVisibleNodesCount();
		if (!rootLevelVisible && r > 0)
			r--;
		return r;
	}

	private void createJsSetRowHeight(int rowHeight, StringBuilder js) {
		/**
		 * 1. In a AmiWebTreePorltet(static tree) context, if this.visbileColumns is empty, it does not necessarily mean that the treeportlet has no column to render(because a
		 * treeColumn(group by column) does not count towards visibleColumns), in which case we should not set this.headerRowHeight to 0(setting it to 0 will cause js overflow). We
		 * should instead check whether this.columns is empty 2. In a AmiWebDmTreePortlet context, where we do want to set the headerHeightPx to 0, because this.columns is empty
		 */
		int headerHeightPx = this.headerRowHeight; // set headerHeight to 0 will cause js overflow if the UI does have at least one column to render
		int topPaddingPx = this.visibleColumns.getSize() > 0 ? 0 : 4;
		jsFunction.reset(js, "setMetrics").addParam(this.rowHeight).addParam(this.leftPaddingPx).addParam(topPaddingPx).addParam(headerHeightPx).end();
	}

	private void createJsUpdateCells(StringBuilder js) {
		jsFunction.reset(js, "updateCells").end();
	}

	private void createJsSetSize(int visibleNodesCount, StringBuilder js) {
		jsFunction.reset(js, "setSize").addParam(maxWidth).addParam(visibleNodesCount).end();
	}

	private void createJsClearData(int i, StringBuilder js) {
		jsFunction.reset(js, "clearData").addParam(i).end();
	}

	private StringBuilder tmpbuf = new StringBuilder();
	private StringBuilder tmpbuf2 = new StringBuilder();
	private WebTreeColumnMenuFactory columnMenuFactory;
	private List<WebTreeColumnContextMenuListener> columnMenuListeners = new ArrayList<WebTreeColumnContextMenuListener>();
	private int activeRowUid;
	private int moveToScrollPos = -1;
	private String selectedRowsText = "";
	private List<WebTreeNode> selectedRows = new ArrayList<WebTreeNode>();
	private boolean showExpandMenuItems = true;

	public void addColumnMenuListener(WebTreeColumnContextMenuListener listener) {
		this.columnMenuListeners.add(listener);
	}
	public void removeColumnMenuListener(WebTreeColumnContextMenuListener listener) {
		this.columnMenuListeners.remove(listener);
	}
	private void createJsAddNode(WebTreeNode childNode, StringBuilder js) {
		jsFunction.reset(js, "setData").addParam(childNode.getUid()).addParam(getAbsolutePosition(childNode));
		int before = js.length();
		tmpbuf.setLength(0);
		tmpbuf2.setLength(0);
		formatter.formatToHtml(childNode, tmpbuf, tmpbuf2);
		jsFunction.addParamQuoted(tmpbuf);
		jsFunction.addParamQuoted(tmpbuf2);
		tmpbuf.setLength(0);
		tmpbuf2.setLength(0);
		if (rowFormatter != null) {
			tmpbuf.setLength(0);
			rowFormatter.format(childNode, tmpbuf);
			jsFunction.addParamQuoted(tmpbuf);
			tmpbuf.setLength(0);
		} else {
			jsFunction.addParamQuoted("");
		}

		int depth = childNode.getDepth();
		if (!rootLevelVisible)
			depth--;
		depth *= indentPx;
		int widthPx = depth + calcTextLengthPx(js, before, js.length());
		if (maxWidth < widthPx)
			maxWidth = widthPx;

		jsFunction.addParam(depth).addParam(childNode.getIsExpanded()).addParamQuoted(childNode.getCssClass()).addParam(childNode.getIsExpandable())
				.addParam(childNode.getSelected()).addParamQuoted(childNode.getIcon()).addParamQuoted(childNode.getIconCssStyle()).addParam(childNode.getHasCheckbox())
				.addParam(childNode.getChecked()).addParam(childNode.getAllChildrenCheckedCount() > 0);
		for (FastWebTreeColumn col : this.visibleColumns.values()) {
			tmpbuf.setLength(0);
			tmpbuf2.setLength(0);
			col.getFormatter().formatToHtml(childNode, tmpbuf, tmpbuf2);
			jsFunction.addParamQuoted(tmpbuf);
			jsFunction.addParamQuoted(tmpbuf2);
			tmpbuf.setLength(0);
			tmpbuf2.setLength(0);
		}
		jsFunction.end();

	}
	private int calcTextLengthPx(StringBuilder js, int start, int end) {
		return (end - start) * fontSize / 2;
	}

	private int getAbsolutePosition(WebTreeNode childNode) {
		int r = childNode.getAbsolutePosition();
		if (!rootLevelVisible && r != -1)
			r--;//this will also translate the root node to a position of -1
		return r;
	}

	public static final String CALLBACK_CELL_CLICKED_MOVE_COLUMN = "moveColumn";

	public boolean processWebRequest(FastTreePortlet parent, Map<String, String> attributes, StringBuilder pendingJs) {
		final String callback = (String) attributes.get("type");
		if (CALLBACK_CELL_CLICKED_MOVE_COLUMN.equals(callback)) {
			final int oldPos = CH.getOrThrow(Caster_Integer.INSTANCE, attributes, "oldPos") + 1;
			final int newPos = CH.getOrThrow(Caster_Integer.INSTANCE, attributes, "newPos") + 1;
			FastWebTree t2 = parent.getTree();
			FastWebTreeColumn col = t2.getVisibleColumn(oldPos);
			int pcc = t2.getPinnedColumnsCount();
			int newPosForTree = newPos == 0 ? 0 : newPos - 1;
			t2.hideColumn(col.getColumnId());
			t2.showColumn(col.getColumnId(), newPosForTree);
			if (pcc > oldPos && pcc <= newPos)
				t2.setPinnedColumnsCount(pcc - 1);
			else if (pcc <= oldPos && pcc > newPos)
				t2.setPinnedColumnsCount(pcc + 1);
			else
				t2.setPinnedColumnsCount(pcc);
			t2.fireOnColumnsArranged();
		} else if ("expand".equals(callback)) {
			final int uid = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "treeNodeUid");
			final WebTreeNode node = treeManager.getTreeNodeNoThrow(uid);
			if (node != null) {//could be null if the row was removed real-time as the user clicks.
				if (node.getIsExpanded()) {
					if (autoCollapseChildren)
						WebTreeHelper.autoCollapseChildren(node, false);
					else
						node.setIsExpanded(false);
				} else {
					if (autoExpandUntilMultipleNodes)
						WebTreeHelper.autoExpandUntilMultipleNodes(node);
					else
						node.setIsExpanded(true);
				}
			}
		} else if ("copy".equals(callback)) {
			final int uid = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "uid");
			final int col = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "col");
			WebMenu copyMenu = new BasicWebMenu();
			WebTreeNode selected = getTreeManager().getTreeNodeNoThrow(uid);
			if (selected != null)
				this.treeManager.setActiveSelectedNode(selected);
			if (getVisibleColumnsCount() > 0) {
				this.selectedCol = col;
				List<WebTreeNode> sel = getTreeManager().getSelectedNodes();
				copyMenu.add(toCopyLink(sel.size() == 1 ? "Copy <U>R</U>ow" : "Copy <U>R</U>ows", "copy_row").setKeystroke('r'));
				copyMenu.add(toCopyLink(sel.size() == 1 ? "Copy <U>C</U>ell" : "Copy <U>C</U>olumn", "copy_cell").setKeystroke('c'));
			} else
				copyMenu.add(toCopyLink("<U>C</U>opy", "copy_row").setKeystroke('c'));
			copyMenu.add(new BasicWebMenuDivider());
			copyMenu.add(new BasicWebMenuLink("Copy <U>A</U>dvanced...", true, "copy_advanced").setKeystroke('a'));
			parent.getManager().showContextMenu(copyMenu, this);
		} else if ("showHeaderMenu".equals(callback)) {
			int col = CH.getOrThrow(Caster_Integer.INSTANCE, attributes, "col");
			FastWebTreeColumn column;
			BasicWebMenu menu = new BasicWebMenu();
			if (col == -2) {
				column = null;
			} else {
				column = getVisibleColumn(col);
				Set<Integer> sorted = getSortedColumnIds();
				boolean isPrimarySort = this.isPrimarySort(column.getColumnId());
				boolean keepSorting = getKeepSorting();
				menu.addChild(new BasicWebMenuLink("Sort Ascending", true, "__sort_3"));
				menu.addChild(new BasicWebMenuLink("Sort Descending", true, "__sort_2"));
				menu.addChild(new BasicWebMenuLink("Secondary Sort Ascending", !sorted.isEmpty() && keepSorting && !isPrimarySort, "__sort_5"));
				menu.addChild(new BasicWebMenuLink("Secondary Sort Descending", !sorted.isEmpty() && keepSorting && !isPrimarySort, "__sort_4"));
				menu.addChild(new BasicWebMenuLink("Sort Ascending Once", true, "__sort_1"));
				menu.addChild(new BasicWebMenuLink("Sort Descending Once", true, "__sort_0"));
				menu.addChild(new BasicWebMenuLink("Clear All Sorts", keepSorting, "__clearSort"));
				menu.addChild(new BasicWebMenuDivider());
				menu.addChild(new BasicWebMenuLink("Pin To This Column", true, "__pin"));
				menu.addChild(new BasicWebMenuLink("Clear Pinning", this.pinnedColumnsCount > 0, "__unpin"));
				menu.addChild(new BasicWebMenuDivider());

				//				boolean filtered = getFilteredInColumns().contains(column.getColumnId());
				//				menu.addChild(new BasicWebMenuLink("Filter...", true, "__filter"));
				//				menu.addChild(new BasicWebMenuLink("Clear Filter", filtered, "__clearfilter"));
				// Moved to AmiWebTreePortlet createColumnMenu
			}
			if (OH.ne(getColumnMenuFactory(), null)) {
				WebMenu menu2 = getColumnMenuFactory().createColumnMenu(this, column, menu);

				Map<String, Object> menuModel = PortletHelper.menuToJson(parent.getManager(), menu2);
				JsFunction jsf = new JsFunction(pendingJs, "t", "showContextMenu");
				jsf.addParamJson(menuModel);
				jsf.end();
			}
		} else if ("headerMenuitem".equals(callback)) {
			String menuAction = CH.getOrThrow(Caster_String.INSTANCE, attributes, "action");
			if (!SH.startsWith(menuAction, "__"))
				if (!menuAction.startsWith("__")) {
					WebMenuLink link = parent.getManager().getMenuManager().fireLinkForId(menuAction);
					menuAction = link == null ? null : link.getAction();
				}
			int col = Caster_Integer.INSTANCE.cast(CH.getOrThrow(attributes, "col"));
			if (col == -2 || col == 0) {
				if ("arrange".equals(menuAction)) {
					parent.getManager().showDialog("Arrange Columns", new ArrangeColumnsPortlet(parent.generateConfig(), this).setFormStyle(this.parentPortlet.getFormStyle()))
							.setStyle(this.parentPortlet.getDialogStyle());
					return true;
				}

			}
			FastWebTreeColumn column = getVisibleColumn(col);

			if (SH.startsWith(menuAction, "__")) {
				if (menuAction.equals("__help")) {
				} else if (menuAction.equals("__filter")) {
					int columnIndex = Caster_Integer.PRIMITIVE.cast(CH.getOrThrow(attributes, "col"));
					showFilterDialog(parent, columnIndex);
				} else if (menuAction.equals("__pin")) {
					setPinnedColumnsCount(getColumnPosition(column.getColumnId()) + 1);
				} else if (menuAction.equals("__unpin")) {
					setPinnedColumnsCount(0);
				} else if (menuAction.equals("__clearSort")) {
					FastWebTree t2 = parent.getTree();
					t2.clearSort();
				} else if (menuAction.equals("__hide")) {
					int cid = column.getColumnId();
					hideColumn(cid);
				} else if (menuAction.equals("__clearfilter")) {
					setFilteredIn(column.getColumnId(), null);
				} else if (menuAction.equals("__clearAllFilter")) {
					Set<Integer> toRemove = filteredIn.keySet();
					setFilteredIn(toRemove, null);
				} else if (menuAction.startsWith("__sort_")) {
					int sortType = SH.parseInt(SH.stripPrefix(menuAction, "__sort_", true));
					int cid = column.getColumnId();
					LinkedHashMap<Integer, Integer> toSort = getToSort();
					boolean ascend = (sortType & FastWebTree.ASCEND) == 1;
					boolean ks = (sortType & FastWebTree.KEEP_SORT) == 2;
					boolean add = (sortType & FastWebTree.ADD) == 4;
					int f = ascend ? MH.setBits(0, FastWebTree.ASCEND, true) : 0;
					int s = ks ? MH.setBits(0, FastWebTree.KEEP_SORT, true) : 0;
					int t = add ? MH.setBits(0, FastWebTree.ADD, true) : 0;
					if (!add)
						clearPendingSort();
					// save sort (when dm runs)
					toSort.put(cid, f | s | t);
					// sort now (user action)
					sortRows(cid, ascend, ks, add);
				} else if (menuAction.equals("__clearfilter")) {
					setFilteredIn(column.getColumnId(), null);
				}
			} else {
				List<WebTreeColumnContextMenuListener> listeners = getColumnMenuListeners();
				if (CH.isntEmpty(listeners)) {
					for (WebTreeColumnContextMenuListener listener : listeners)
						listener.onColumnContextMenu(this, column, menuAction);
				}
			}
		} else if ("columnWidth".equals(callback)) {
			int columnIndex = Caster_Integer.PRIMITIVE.cast(CH.getOrThrow(attributes, "columnIndex"));
			int width = Caster_Integer.PRIMITIVE.cast(CH.getOrThrow(attributes, "width"));
			if (columnIndex == this.treeColumn.getColumnId()) {
				this.treeColumn.setWidth(width);
			} else {
				getVisibleColumn(columnIndex).setWidth(width);
			}
			fireOnColumnsSized();
		} else if ("expandAll".equals(callback)) {
			treeManager.setAllExpanded(true);
		} else if ("search".equals(callback)) {
			//			boolean reverse = CH.getOrThrow(Caster_Boolean.PRIMITIVE, attributes, "reverse");
			final String expression = CH.getOrThrow(Caster_String.INSTANCE, attributes, "expression");
			//			WebTreeNode start = this.treeManager.getActiveSelectedNode();
			//			if (start == null)
			//				start = getTreeManager().getRoot();
			//			this.clearSelected();
			//			TextMatcher matcher = SH.m(expression);
			//			this.treeManager.setFilter(new SearchFilter(expression));
			//			WebTreeNode cur = start;
			//			for (;;) {
			//				if (reverse)
			//					cur = WebTreeHelper.getPrevious(cur);
			//				else
			//					cur = WebTreeHelper.getNext(cur);
			//				if (cur == null) {
			//					if (reverse)
			//						cur = WebTreeHelper.getLast(getTreeManager().getRoot());
			//					else
			//						cur = getTreeManager().getRoot();
			//				}
			//
			//				if (matches(matcher, cur)) {
			//					ensureVisible(cur);
			//					this.treeManager.setActiveSelectedNode(cur);
			//					break;
			//				}
			//				if (cur == start)
			//					break;
			//			}
			//			this.search = expression;
			setSearch(expression);
		} else if ("contractAll".equals(callback)) {
			if (rootLevelVisible)
				treeManager.setAllExpanded(false);
			else {
				WebTreeNode root = treeManager.getRoot();
				root.setIsExpanded(true);
				for (WebTreeNode child : root.getChildren())
					WebTreeHelper.setAllExpanded(child, false);
			}
		} else if ("clipzone".equals(callback)) {
			final int top = Caster_Integer.PRIMITIVE.cast(CH.getOrThrow(attributes, "top"));
			final int bottom = Caster_Integer.PRIMITIVE.cast(CH.getOrThrow(attributes, "bottom"));
			setClipZone(top, bottom);
		} else if ("checkbox".equals(callback)) {
			final int uid = CH.getOr(Caster_Integer.PRIMITIVE, attributes, "treeNodeUid", -1);
			final WebTreeNode node = treeManager.getTreeNodeNoThrow(uid);
			if (node != null && node.getHasCheckbox()) {
				WebTreeHelper.toggleCheckbox(node);
			}
		} else if (FastWebTable.CALLBACK_USER_SELECT.equals(callback)) {
			// this also handles arrow navigation on tree
			final int button = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "button");
			if (this.getVisibleNodesCount() > 0) {
				Integer col = CH.getOr(Caster_Integer.PRIMITIVE, attributes, "col", -1);
				// col is -1 when using arrow to navigate
				// col is not -1 when clicking on a tree row since we grab the col and row when cell clicked
				FastWebTreeColumn column = col == -1 ? this.getVisibleColumn(0) : this.getVisibleColumn(col);
				Integer activeRow = CH.getOr(Caster_Integer.PRIMITIVE, attributes, "activeRow", -1);
				Integer clickedRow = CH.getOr(Caster_Integer.PRIMITIVE, attributes, "clicked", -1);
				final WebTreeNode clickedNode = treeManager.getTreeNodeNoThrow(clickedRow);
				final WebTreeNode end = treeManager.getTreeNodeNoThrow(activeRow);
				this.treeManager.setActiveSelectedNode(end);
				this.activeRowUid = end != null ? end.getUid() : -1;
				if (column.isSelectable()) {
					String selectedRows = Caster_String.INSTANCE.cast(CH.getOrThrow(attributes, "selectedRows"));
					this.setSelectedRowsNoFire(selectedRows, true);
					if (button == 1 || button == 40 || button == 38) {
						for (WebTreeContextMenuListener ml : menuListeners)
							ml.onNodeClicked(this, clickedNode);
					} else if (button == 2)
						showContextMenu(end, pendingJs, parent);
				}
				if (column != null) {
					for (int i = 0, l = this.menuListeners.size(); i < l; i++)
						this.menuListeners.get(i).onCellMousedown(this, clickedNode, column);
				}
			}
		} else if ("selected_range".equals(callback)) {
			//Deprecated
			final String uid = CH.getOr(Caster_String.INSTANCE, attributes, "treeNodeUid", "");
			int t = uid.indexOf('-');
			int s = SH.parseInt(uid, 0, t, 10);
			int e = SH.parseInt(uid, t + 1, uid.length(), 10);
			final WebTreeNode start = treeManager.getTreeNodeNoThrow(s);
			final WebTreeNode end = treeManager.getTreeNodeNoThrow(e);
			this.treeManager.setActiveSelectedNode(end);
			if (end != null && start != null) {
				int startPos = start.getAbsolutePosition();
				int endPos = end.getAbsolutePosition();
				if (startPos > endPos) {
					t = startPos;
					startPos = endPos;
					endPos = t;
				}
				for (IntIterator i = this.treeManager.getSelected().iterator(); i.hasNext();) {
					WebTreeNode node = this.treeManager.getTreeNode(i.nextInt());
					if (node.getAbsolutePosition() < startPos || node.getAbsolutePosition() > endPos)
						node.setSelected(false);
				}
				for (int i = startPos; i <= endPos; i++)
					treeManager.getVisibleTreeNodeAt(i).setSelected(true);
			}
		} else if ("selected_clear".equals(callback)) {
			//Deprecated
			for (WebTreeNode i : this.getSelected())
				i.setSelected(false);
		} else if ("selected".equals(callback)) {
			//Deprecated
			final int uid = CH.getOr(Caster_Integer.PRIMITIVE, attributes, "treeNodeUid", -1);
			final boolean ctrl = CH.getOrThrow(Caster_Boolean.PRIMITIVE, attributes, "ctrl");
			final boolean shift = CH.getOrThrow(Caster_Boolean.PRIMITIVE, attributes, "shift");
			this.treeManager.setActiveSelectedNode(this.treeManager.getTreeNodeNoThrow(uid));
			int button = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "button");
			final WebTreeNode node = treeManager.getTreeNodeNoThrow(uid);
			if (node.getIsSelectable()) {
				if (button == 1) {
					if (shift) {
						WebTreeNode node2 = treeManager.getTreeNodeNoThrow(lastSelected);
						if (node2 != null && getAbsolutePosition(node2) != -1 && getAbsolutePosition(node) != -1) {
							int min = Math.min(getAbsolutePosition(node2), getAbsolutePosition(node));
							int max = Math.max(getAbsolutePosition(node2), getAbsolutePosition(node));
							for (int i = min; i <= max; i++)
								getVisibleTreeNodeAt(i).setSelected(true);
						} else {
							WebTreeHelper.search(treeManager.getRoot(), ConstTextMatcher.FALSE);
							node.setSelected(true);
						}
					} else if (ctrl) {
						node.setSelected(!node.getSelected());
					} else {
						switch (selectionMode) {
							case SELECTION_MODE_STANDARD:
								WebTreeHelper.search(treeManager.getRoot(), ConstTextMatcher.FALSE);
								node.setSelected(true);
								break;
							case SELECTION_MODE_TOGGLE:
								node.setSelected(!node.getSelected());
								break;
							case SELECTION_MODE_NONE:
								break;
						}
					}
				} else if (button == 2) {
					showContextMenu(node, pendingJs, parent);
				}
				this.lastSelected = uid;
			}
		} else if ("nameclicked".equals(callback)) {
			final int uid = CH.getOr(Caster_Integer.PRIMITIVE, attributes, "treeNodeUid", -1);
			final boolean ctrl = CH.getOrThrow(Caster_Boolean.PRIMITIVE, attributes, "ctrl");
			final boolean shift = CH.getOrThrow(Caster_Boolean.PRIMITIVE, attributes, "shift");
			final int button = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "button");
			final WebTreeNode node = treeManager.getTreeNodeNoThrow(uid);
			if (button == 1 || button == 40 || button == 38) {
				if (node != null) {
					if (node.getIsExpandable()) {
					} else if (node.getHasCheckbox()) {
						WebTreeHelper.toggleCheckbox(node);
					}
				} else if (selectionMode == SELECTION_MODE_TOGGLE)
					WebTreeHelper.search(treeManager.getRoot(), ConstTextMatcher.FALSE);
				if (button != 2 && !ctrl && !shift)
					for (WebTreeContextMenuListener ml : menuListeners)
						ml.onNodeClicked(this, node);
			} else if (button == 2) {
				showContextMenu(node, pendingJs, parent);
			}
		} else if ("menuitem".equals(callback)) {
			final WebMenuLink link = parent.getManager().getMenuManager().fireLinkForId(CH.getOrThrow(attributes, "action"));
			if (link == null)
				return true;
			String action = link.getAction();
			if (SH.startsWith(action, "___")) {
				List<WebTreeNode> selected = getSelected();
				if ("___expand".equals(action)) {
					for (WebTreeNode i : selected)
						WebTreeHelper.setAllExpanded(i, true);
				} else if ("___collapse".equals(action)) {
					for (WebTreeNode i : selected)
						WebTreeHelper.setAllExpanded(i, false);
				} else if ("___check".equals(action)) {
					for (WebTreeNode i : selected)
						WebTreeHelper.setAllChecked(i, true);
				} else if ("___uncheck".equals(action)) {
					for (WebTreeNode i : selected)
						WebTreeHelper.setAllChecked(i, false);
				}
			}
			for (WebTreeContextMenuListener ml : menuListeners)
				ml.onContextMenu(this, action);
		} else if (FastWebTable.CALLBACK_DBL_CLICK.equals(callback)) {
			final String action = CH.getOrThrow(attributes, "action");
			this.fireOnUserDblclick(action, attributes);
		} else if ("getColumnFilterOptions".equals(callback)) {
			final int pos = CH.getOrThrow(Caster_Integer.INSTANCE, attributes, "pos");
			String val = CH.getOrThrow(Caster_String.INSTANCE, attributes, "val");
			FastWebTreeColumn col = getVisibleColumn(pos);
			//			TreeMap<String, String> tree = getQuickFilterOptions(col, val);
			parent.onQuickFilterUserAction(col.getColumnId(), val, 20);
			//			json.addQuoted(tree);
			//			json.end();
			//			jsf.end();
		} else if ("columnFilter".equals(callback)) {
			final int pos = CH.getOrThrow(Caster_Integer.INSTANCE, attributes, "pos");
			String val = CH.getOrThrow(Caster_String.INSTANCE, attributes, "val");
			FastWebTreeColumn col = getVisibleColumn(pos);
			if (SH.is(val)) {
				WebTreeFilteredInFilter f = new WebTreeFilteredInFilter(col);
				f.setFilteredExpression(val);
				setFilteredIn(col.getColumnId(), f);
			} else
				setFilteredIn(col.getColumnId(), (WebTreeFilteredInFilter) null);
		} else
			return false;
		return true;
	}

	private TreeMap<String, String> getQuickFilterOptions(FastWebTreeColumn col, String startsWith) {
		TreeMap<String, String> r = new TreeMap<String, String>();
		StringBuilder sink = new StringBuilder();
		for (WebTreeNode i : treeManager.getRoot().getAllChildren()) {
			if (col == this.treeColumn || (i.getChildrenCount() == 0 && i.getFilteredChildrenCount() == 0)) {
				col.getFormatter().formatToText(i, sink);
				String val = SH.toStringAndClear(sink);
				if (SH.startsWithIgnoreCase(val, startsWith)) {
					r.put(val, val);
					if (r.size() > 20)
						break;
				}
			}
		}
		return r;
	}

	//-1 if not visible
	public int getVisibleColumnPosition(Integer columnId) {
		return this.visibleColumns.getPositionNoThrow(columnId);
	}

	public void setSelectedRowsNoFire(String selectedRowsText, boolean isUserSelect) {
		if (isUserSelect && OH.eq(selectedRowsText, this.selectedRowsText))
			return;
		// list of current selected rows
		IntSet selected = new IntSet();
		selected.addAll(this.treeManager.getSelected());
		this.selectedRowsText = selectedRowsText;
		//		this.clearSelected();
		buildSelectedRows(selectedRows, 1000);
		// for every selected node, check whether it was already selected.
		for (int i = 0; i < selectedRows.size(); i++) {
			WebTreeNode webTreeNode = selectedRows.get(i);
			// if this node is NOT in the previously selected list, then set it as selected.
			// if this node is already selected, then de-select it.
			if (!selected.remove(webTreeNode.getUid())) {
				webTreeNode.setSelected(true);
			}
		}
		// de-select everything from previous selections
		for (int i : selected) {
			WebTreeNode webTreeNode = this.treeManager.getTreeNode(i);
			if (webTreeNode != null)
				webTreeNode.setSelected(false);
		}
		//		for (WebTreeContextMenuListener menuListener : this.menuListeners)
		//			menuListener.onNodeSelectionChanged(this, this.treeManager.getRoot());
	}

	private void buildSelectedRows(List<WebTreeNode> selectedRowsSink, int max) {
		selectedRowsSink.clear();
		String[] parts = SH.split(',', selectedRowsText);
		int ranges[] = new int[parts.length * 2];
		int cnt = 0, i = 0;
		for (String s : parts) {
			int start, end;
			if (s.indexOf('-') == -1) {
				start = end = Integer.parseInt(s);
			} else {
				start = Integer.parseInt(SH.beforeFirst(s, '-'));
				end = Integer.parseInt(SH.afterFirst(s, '-'));
			}
			cnt += end - start + 1;
			if (cnt > max)
				return;
			ranges[i++] = start;
			ranges[i++] = end;
		}
		int rowsCount = this.getVisibleNodesCount();
		if (i == ranges.length) {//made it to the end
			for (i = 0; i < ranges.length; i += 2) {
				int loc = ranges[i], end = ranges[i + 1];
				while (loc <= end && loc < rowsCount)
					selectedRowsSink.add(this.getVisibleTreeNodeAt(loc++));
			}
		}

	}
	private BasicWebMenuLink toCopyLink(String title, String action) {
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
		
		List<WebTreeNode> nodes = getTreeManager().getSelectedNodes();
		this.copyBuilder[actionIdx] = new StringBuilder();
		int startCol, endCol;
		if (col == -1) {
			startCol = 0;
			endCol = getVisibleColumnsCount();
		} else {
			startCol = endCol = col;
		}

		outer: for (int y = 0; y < nodes.size(); y++) {
			if (y > 0)
				this.copyBuilder[actionIdx].append(SH.CHAR_RETURN).append(SH.CHAR_NEWLINE);
			WebTreeNode node = nodes.get(y);
			for (int i = startCol; i <= endCol; i++) {
				if (i > startCol)
					this.copyBuilder[actionIdx].append(SH.CHAR_TAB);
				FastWebTreeColumn c = getVisibleColumn(i);
				c.getFormatter().formatToText(node, this.copyBuilder[actionIdx]);
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
	private void showContextMenu(WebTreeNode node, StringBuilder pendingJs, FastTreePortlet parent) {
		List<WebTreeNode> sink = new ArrayList<WebTreeNode>();
		if (node != null && !node.getSelected()) {
			WebTreeHelper.search(treeManager.getRoot(), ConstTextMatcher.FALSE);
			node.setSelected(true);
			sink.add(node);
		} else
			sink.addAll(getSelected());
		WebMenu menu = null;
		if (this.contextMenuFactory != null) {
			menu = contextMenuFactory.createMenu(this, sink);
		}
		if (!sink.isEmpty()) {
			if (menu == null)
				menu = new BasicWebMenu();
			boolean hasExpand = false;
			boolean hasCheckbox = false;
			for (WebTreeNode i : sink) {
				if (i.getIsExpandable())
					hasExpand = true;
				if (i.getHasCheckbox())
					hasCheckbox = true;
				if (hasExpand && hasCheckbox)
					break;
			}
			if (this.showExpandMenuItems)
				if (hasExpand) {
					menu.add(new BasicWebMenuLink("Expand", true, "___expand"));
					menu.add(new BasicWebMenuLink("Collapse", true, "___collapse"));
				}
			//			if (hasCheckbox && this.showCheckUncheckOption) {
			//				menu.add(new BasicWebMenuLink("Check", true, "___check"));
			//				menu.add(new BasicWebMenuLink("Uncheck", true, "___uncheck"));
			//			}
		}
		if (menu != null) {
			Map<String, Object> menuModel = PortletHelper.menuToJson(parent.getManager(), menu);
			JsFunction jsf = new JsFunction(pendingJs, "t", "showContextMenu");
			jsf.addParamJson(menuModel);
			jsf.end();
		}

	}

	//	private StringBuilder buf = new StringBuilder();
	//
	//	private boolean matches(TextMatcher matcher, WebTreeNode cur) {
	//		buf.setLength(0);
	//		if (this.treeColumn.getFormatter() != null) {
	//			this.treeColumn.getFormatter().formatToText(this.treeColumn, cur, buf);
	//		} else if (this.formatter != null) {
	//			formatter.formatToText(null, cur, buf);
	//		} else {
	//			if (cur.getName() != null)
	//				buf.append(cur.getName());
	//		}
	//		for (FastWebTreeColumn i : this.visibleColumns.values()) {
	//			buf.append('\t');
	//			i.getFormatter().formatToText(i, cur, buf);
	//		}
	//		boolean r = matcher.matches(buf);
	//		buf.setLength(0);
	//		return r;
	//	}
	public void ensureVisible(WebTreeNode cur) {
		changed(CHANGED_DATA);
		WebTreeHelper.ensureVisible(cur);
		moveToScrollPos = cur.getAbsolutePosition();
	}
	//
	private void showFilterDialog(FastTreePortlet parent, int columnIndex) {
		FastWebTreeColumn column = getVisibleColumn(columnIndex);
		parent.getManager().showDialog("Filter", (new FastWebTreeFilterColumnPortlet2(parent.generateConfig(), parent, column)).setFormStyle(parent.getFormStyle()))
				.setStyle(parent.getDialogStyle());
	}

	private boolean getAllValues(WebTreeNode parent, StringBuilder tmp, FastWebTreeColumn column, HasherMap<CharSequence, Boolean> values) {
		tmp.setLength(0);
		WebTreeNodeFormatter f = column.getFormatter();
		f.formatToText(parent, tmp);
		if (!values.containsKey(tmp))
			values.put(tmp.toString(), Boolean.FALSE);
		if (values.size() > 25000)
			return false;
		for (WebTreeNode node : parent.getChildren())
			if (!getAllValues(node, tmp, column, values))
				return false;
		return true;
	}

	private boolean getKeepSorting() {
		return keepSorting;
	}

	private boolean keepSorting = false;

	final private LinkedHashMap<Integer, Boolean> sortedColumnIds = new LinkedHashMap<Integer, Boolean>();
	private FastTreePortlet parentPortlet;
	private String rowDelimiter = "\n";
	private String columnDelimiter = "|";
	private String inlineDelimiter = "=";
	private String inlineEnclosed = "\"";
	private String headerOptions = "hot";

	private List<WebTreeColumnContextMenuListener> getColumnMenuListeners() {
		return this.columnMenuListeners;
	}

	public List<WebTreeNode> getSelected() {
		List<WebTreeNode> r = new ArrayList<WebTreeNode>(this.treeManager.getSelected().size());
		for (IntIterator i = this.treeManager.getSelected().iterator(); i.hasNext();)
			r.add(treeManager.getTreeNode(i.nextInt()));
		return r;
	}
	public Iterable<WebTreeNode> getNodes() {
		return this.treeManager.getRoot().getAllChildren();
	}

	public void clearSelected() {
		for (IntIterator i = this.treeManager.getSelected().iterator(); i.hasNext();)
			treeManager.getTreeNode(i.nextInt()).setSelected(false);
		this.selectedRowsText = "";
	}

	public void setSelectedRowsText(String rowsText) {
		this.selectedRowsText = rowsText;
	}

	public String getSelectedRowsText() {
		return this.selectedRowsText;
	}

	public List<WebTreeNode> getChecked(boolean onlyLeafs) {
		List<WebTreeNode> r = new ArrayList<WebTreeNode>();
		WebTreeHelper.getChecked(treeManager.getRoot(), r, onlyLeafs);
		return r;
	}

	public void setClipZone(int top, int bottom) {
		if (top == this.clipZoneTop && bottom == this.clipZoneBottom)
			return;

		this.clipZoneTop = top;
		this.clipZoneBottom = bottom;

		changed(CHANGED_CLIPZONE);
	}
	public void setRowHeight(int rowHeight) {
		if (this.rowHeight == rowHeight)
			return;
		this.rowHeight = rowHeight;
		changed(CHANGED_ROW_HEIGHT | CHANGED_ALL);
	}
	public int getRowheight() {
		return this.rowHeight;
	}
	public void setHeaderRowHeight(int rowHeight) {
		if (this.headerRowHeight == rowHeight)
			return;
		this.headerRowHeight = rowHeight;
		changed(CHANGED_ROW_HEIGHT | CHANGED_ALL);
	}

	public void setTopPaddingPx(int topPaddingPx) {
		if (this.topPaddingPx == topPaddingPx)
			return;
		this.topPaddingPx = topPaddingPx;
		changed(CHANGED_ROW_HEIGHT | CHANGED_ALL);
	}
	public int getTopPaddingPx() {
		return this.topPaddingPx;
	}

	public void setLeftPaddingPx(int leftPaddingPx) {
		if (this.leftPaddingPx == leftPaddingPx)
			return;
		this.leftPaddingPx = leftPaddingPx;
		changed(CHANGED_ROW_HEIGHT | CHANGED_ALL);
	}
	public int getLeftPaddingPx() {
		return this.leftPaddingPx;
	}

	public WebTreeContextMenuFactory getContextMenuFactory() {
		return contextMenuFactory;
	}
	public WebTreeColumnMenuFactory getColumnMenuFactory() {
		return columnMenuFactory;
	}
	public void setColumnMenuFactory(WebTreeColumnMenuFactory factory) {
		this.columnMenuFactory = factory;
	}

	public void setContextMenuFactory(WebTreeContextMenuFactory contextMenuFactory) {
		this.contextMenuFactory = contextMenuFactory;
	}

	public void addMenuContextListener(WebTreeContextMenuListener listener) {
		menuListeners.add(listener);
	}

	public boolean removeMenuContextListener(WebTreeContextMenuListener listener) {
		return menuListeners.remove(listener);
	}

	public void setRootLevelVisible(boolean b) {
		if (this.rootLevelVisible == b)
			return;
		this.rootLevelVisible = b;
	}

	public void onSelectionChanged(WebTreeNode node) {
		onStyleChanged(node);
		for (WebTreeContextMenuListener ml : menuListeners)
			ml.onNodeSelectionChanged(this, node);
	}

	public boolean getAutoExpandUntilMultipleNodes() {
		return autoExpandUntilMultipleNodes;
	}

	public void setAutoExpandUntilMultipleNodes(boolean autoExpandSingleParents) {
		this.autoExpandUntilMultipleNodes = autoExpandSingleParents;
	}

	public boolean getAutoCollapseChildren() {
		return autoCollapseChildren;
	}

	public void setAutoCollapseChildren(boolean autoCollapseChildren) {
		this.autoCollapseChildren = autoCollapseChildren;
	}

	public FastWebTree setSelectionMode(byte selectionMode) {

		if (this.selectionMode == selectionMode)
			return this;
		this.selectionMode = selectionMode;
		return this;
	}

	public byte getSelectionMode() {
		return this.selectionMode;
	}
	public void flagStyleChanged() {
		changed(CHANGED_ALL);
	}

	//COLUMN STUFF

	public FastWebTreeColumn getColumn(int id) {
		if (this.treeColumn.getColumnId() == id)
			return this.treeColumn;
		return columns.get(id);
	}
	public int getColumnsCount() {
		return this.columns.size();
	}

	public void addColumnAt(boolean visible, FastWebTreeColumn column, int location) {
		column.setTree(this);
		final int columnId = column.getColumnId();
		if (getColumn(columnId) != null)
			throw new RuntimeException("Column already exists: " + column);
		columns.put(columnId, column);
		if (visible)
			visibleColumns.add(columnId, column, location);
		else
			hiddenColumns.add(columnId, column, location);
		if (visible && location < this.pinnedColumnsCount)
			this.pinnedColumnsCount++;
		changed(CHANGED_ALL);
	}

	public void hideColumn(int columnId) {
		if (columnId == this.treeColumn.getColumnId())
			throw new NoSuchElementException("Can not hide tree column");
		if (isColumnPinned(getColumn(columnId)))
			pinnedColumnsCount--;
		FastWebTreeColumn column = visibleColumns.removeNoThrow(columnId);
		if (column == null) {
			column = hiddenColumns.get(columnId);
			if (column == null)
				throw new NoSuchElementException("column not found: " + columnId);
			return;
		}
		hiddenColumns.add(column.getColumnId(), column);
		changed(CHANGED_ALL);
	}

	public FastWebTreeColumn getHiddenColumn(int columnLocation) {
		return hiddenColumns.getAt(columnLocation);
	}

	public FastWebTreeColumn getVisibleColumn(int columnLocation) {
		if (columnLocation == -2)
			return null;
		if (columnLocation == 0)
			return this.treeColumn;
		return visibleColumns.getAt(columnLocation - 1);
	}

	public int getVisibleColumnsCount() {
		return visibleColumns.getSize();
	}

	public int getHiddenColumnsCount() {
		return hiddenColumns.getSize();
	}

	public int getColumnPosition(int columnId) {
		if (columnId == this.treeColumn.getColumnId())
			return 0;
		int r = this.visibleColumns.getPositionNoThrow(columnId);
		return r == -1 ? -1 : r + 1;
	}

	public int getHiddenColumnPosition(int columnId) {
		return this.hiddenColumns.getPositionNoThrow(columnId);
	}

	public FastWebTreeColumn removeColumn(int columnId) {
		FastWebTreeColumn r = columns.remove(columnId);
		sortedColumnIds.remove(columnId);
		if (r == null)
			return null;
		if (isColumnPinned(r))
			this.pinnedColumnsCount--;

		if (visibleColumns.containsKey(columnId)) {
			visibleColumns.remove(columnId);
		} else
			hiddenColumns.remove(columnId);
		changed(CHANGED_ALL);
		return r;
	}

	private boolean isColumnPinned(FastWebTreeColumn r) {
		if (r == null)
			return false;
		int pos = getColumnPosition(r.getColumnId());
		return pos >= 0 && pos < this.pinnedColumnsCount;
	}

	public void showColumn(int columnId, int columnLocation) {
		FastWebTreeColumn column = hiddenColumns.removeNoThrow(columnId);
		if (column == null)
			column = visibleColumns.removeNoThrow(columnId);
		if (column == null)
			throw new NoSuchElementException("column not found: " + columnId);
		if (isColumnPinned(column))
			this.pinnedColumnsCount--;
		int newColLocationIfLeftMost = columnLocation == -1 ? 0 : columnLocation;
		visibleColumns.add(column.getColumnId(), column, newColLocationIfLeftMost);
		if (columnLocation < this.pinnedColumnsCount)
			this.pinnedColumnsCount++;
		changed(CHANGED_ALL);
	}

	public void showColumn(int columnId) {
		FastWebTreeColumn column = hiddenColumns.removeNoThrow(columnId);
		if (column == null) {
			column = visibleColumns.get(columnId);
			if (column == null)
				throw new NoSuchElementException("column not found: " + columnId);
			else
				return;//already shown
		}
		visibleColumns.add(column.getColumnId(), column);
		changed(CHANGED_ALL);
	}

	public int getNextColumnId() {
		for (int i = 1;; i++)
			if (!columns.containsKey(i))
				return i;
	}

	public IntIterator getColumnIds() {
		return this.columns.keyIterator();
	}
	public IterableAndSize<FastWebTreeColumn> getColumns() {
		return this.columns.values();
	}
	public FastWebTreeColumn getTreeColumn() {
		return this.treeColumn;
	}

	public void onColumnChanged(FastWebTreeColumn fastWebTreeColumn) {
		changed(CHANGED_ALL);
	}
	public Iterable<Map.Entry<Integer, Boolean>> getSortedColumns() {
		return this.sortedColumnIds.entrySet();
	}
	public Set<Integer> getSortedColumnIds() {
		return this.sortedColumnIds.keySet();
	}
	public boolean isSorting() {
		return sortedColumnIds.size() > 0;
	}
	public boolean isKeepSorting() {
		return isSorting();
	}

	public FastWebTreeComparator newComparator(Integer columnId, boolean ascending) {
		FastWebTreeColumn column = getColumn(columnId);
		sortedColumnIds.remove(column.getColumnId());
		sortedColumnIds.put(column.getColumnId(), ascending);
		FastWebTreeColumn[] columns = new FastWebTreeColumn[sortedColumnIds.size()];
		boolean[] ascendings = new boolean[sortedColumnIds.size()];
		int i = 0;
		for (Entry<Integer, Boolean> e : sortedColumnIds.entrySet()) {
			columns[i] = getColumn(e.getKey());
			ascendings[i++] = e.getValue().booleanValue();
		}
		// we don't necessarily need to return a new object each time
		return new FastWebTreeComparator(columns, ascendings);
	}

	public void sortRows(Integer columnId, boolean ascending, boolean keepSorting, boolean add) {
		if (add)
			keepSorting = getKeepSorting();
		if (!add)
			this.sortedColumnIds.clear();
		if (columnId == null) {
			treeManager.setComparator(null);
			this.setKeepSorting(false);
		} else {
			treeManager.setComparator(newComparator(columnId, ascending));
			if (!keepSorting)
				treeManager.setComparator(null);
			this.setKeepSorting(keepSorting);
		}
		changed(CHANGED_ALL);

	}

	public void setSearch(String expression) {
		if (OH.eq(expression, search))
			return;
		this.search = expression;
		//		HashMap<String, String> attributes = new HashMap<String, String>();
		//		attributes.put("expression", search);
		//		attributes.put("reverse", "false");
		//		attributes.put("type", "search");
		//		processWebRequest(parentPortlet, attributes, null);
		updateFilter();
		fireOnFilterChanging();
	}

	public String getSearch() {
		return this.search;
	}

	/**
	 * This method intends to remove multiple filters at once for Tree, should NOT call this method to modify multiple user filter values.
	 * 
	 * @param toRemove
	 *            Takes a set of column Ids (Integer) to remove
	 * @param filter
	 *            Set as null
	 * 
	 */
	public void setFilteredIn(Set<Integer> toRemove, WebTreeFilteredInFilter filter) {
		if (filter == null || filter.isEmpty()) {
			filteredIn.keySet().removeAll(toRemove);
			updateFilter();
		}
	}

	public void setFilteredIn(int columnId, WebTreeFilteredInFilter filter) {
		if (filter == null)
			filteredIn.remove(columnId);
		else
			filteredIn.put(columnId, filter);
		fireOnFilterChanging();
		updateFilter();
	}

	private void updateFilter() {
		//		final WebTreeFilteredInFilter treeFilter = this.filteredIn.get(TREE_COLUMNID);
		//		Set<Integer> filteredInColumns = this.getFilteredInColumns();
		final FastWebTreeSearchFilter searchFilter = SH.isnt(this.search) ? null : new FastWebTreeSearchFilter(this, SH.m(this.search));
		final FastWebTreeColumnFilter columnsFilter;
		if (filteredIn.size() == 0) {
			columnsFilter = null;
		}
		//		else if (!filteredIn.containsKey(TREE_COLUMNID)) {
		//			columnsFilter = new FastWebTreeColumnFilter(this, this.filteredIn);
		//		}
		//		else if (filteredIn.size() == 1) {
		//			//only contains TREE_COLUMNID rendering the columns filter blank 
		//			
		//		} 
		else {
			// 1 or more filter 
			//			for (Entry<Integer, WebTreeFilteredInFilter> e : filteredIn.entrySet()) {
			//				filters.add(e.getValue());
			//			}
			Map<Integer, WebTreeFilteredInFilter> t = new HashMap<Integer, WebTreeFilteredInFilter>(filteredIn);
			//			t.remove(TREE_COLUMNID);
			columnsFilter = new FastWebTreeColumnFilter(this, t);
		}
		// this is to follow table logic
		FastWebTreeColumnFilter oldCF = this.treeManager.getColumnsFilter();
		FastWebTreeSearchFilter oldSF = (FastWebTreeSearchFilter) this.treeManager.getSearchFilter();
		if (OH.ne(oldCF, columnsFilter) || OH.ne(oldSF, searchFilter)) {
			this.treeManager.setAndRunFilter(searchFilter, null, columnsFilter);
			changed(CHANGED_ALL);
		}
	}

	public WebTreeFilteredInFilter getFiltererdIn(int columnId) {
		return filteredIn.get(columnId);
	}
	//	public FastWebTreeColumnFilter getTreeColumnFilter() {
	//		return (FastWebTreeColumnFilter) this.treeManager.ge
	//	}

	public Set<Integer> getFilteredInColumns() {
		return filteredIn.keySet();
	}

	public WebTreeRowFormatter getRowFormatter() {
		return rowFormatter;
	}

	public void setRowFormatter(WebTreeRowFormatter rowFormatter) {
		this.rowFormatter = rowFormatter;
		changed(CHANGED_ALL);
	}

	public void setFilter(WebTreeFilter search, WebTreeFilter treeFilter, FastWebTreeColumnFilter columnsFilter) {
		if (search == null && treeFilter == null && columnsFilter == null && getSearchFilter() == null && getTreesFilter() == null && getColumnsFilter() == null)
			return;
		treeManager.setAndRunFilter(search, treeFilter, columnsFilter);
		changed(CHANGED_ROWS_ALL);
	}

	public void setComparator(Comparator<WebTreeNode> comparator) {
		if (comparator == null && getComparator() == null)
			return;
		treeManager.setComparator(comparator);
		changed(CHANGED_ROWS_ALL);
	}

	public WebTreeFilter getTreesFilter() {
		return treeManager.getTreesFilter();
	}
	public WebTreeFilter getSearchFilter() {
		return treeManager.getSearchFilter();
	}
	public WebTreeFilter getColumnsFilter() {
		return treeManager.getColumnsFilter();
	}

	public Comparator<WebTreeNode> getComparator() {
		return treeManager.getComparator();
	}

	public int getPinnedColumnsCount() {
		return pinnedColumnsCount;
	}
	public void setPinnedColumnsCount(int count) {
		if (count >= getVisibleColumnsCount())
			count = getVisibleColumnsCount();
		if (count == this.pinnedColumnsCount)
			return;
		this.pinnedColumnsCount = count;
		changed(CHANGED_ALL);
	}

	@Override
	public void onMenuItem(String id) {
		if ("copy_row".equals(id)) {
			StringBuilder sb = parentPortlet.getManager().getPendingJs();
			sb.append(PortletHelper.createJsCopyToClipboard(this.copyBuilder[0]));
		}
		else if ("copy_cell".equals(id)) {
			StringBuilder sb = parentPortlet.getManager().getPendingJs();
			sb.append(PortletHelper.createJsCopyToClipboard(this.copyBuilder[1]));
		}
		else if ("copy_advanced".equals(id)) {
			parentPortlet
					.getManager().showDialog("Copy",
							(new CopyPortlet(parentPortlet.generateConfig(), new CopyableTreeImpl(this), false)).setFormStyle(this.parentPortlet.getFormStyle()), 1100, 400)
					.setStyle(this.parentPortlet.getDialogStyle());
		}
	}
	@Override
	public void onMenuDismissed() {

	}

	public void setParentPortlet(FastTreePortlet fastTreePortlet) {
		this.parentPortlet = fastTreePortlet;
	}

	public List<WebTreeNode> getVisibleNodes() {
		List<WebTreeNode> visibleNodes = new ArrayList<WebTreeNode>();
		for (int i = 0; i < getVisibleNodesCount(); i++) {
			visibleNodes.add(getVisibleTreeNodeAt(i));
		}
		return visibleNodes;
	}

	public String getColumnDelimiter() {
		return columnDelimiter;
	}

	public void setColumnDelimiter(String columnDelimiter) {
		this.columnDelimiter = columnDelimiter;
	}

	public String getRowDelimiter() {
		return rowDelimiter;
	}

	public void setRowDelimiter(String rowDelimiter) {
		this.rowDelimiter = rowDelimiter;
	}

	public String getHeaderOptions() {
		return headerOptions;
	}

	public void setHeaderOptions(String headerOptions) {
		this.headerOptions = headerOptions;
	}

	public String getInlineDelimiter() {
		return inlineDelimiter;
	}

	public void setInlineDelimiter(String inlineDelimiter) {
		this.inlineDelimiter = inlineDelimiter;
	}

	public String getInlineEnclosed() {
		return inlineEnclosed;
	}

	public void setInlineEnclosed(String inlineEnclosed) {
		this.inlineEnclosed = inlineEnclosed;
	}

	@Override
	public void showColumn(String columnId, int location) {
		this.showColumn(SH.parseInt(columnId), location);
	}

	@Override
	public void hideColumn(String location) {
		this.hideColumn(SH.parseInt(location));
	}

	@Override
	public int getColumnPosition(Object columnId) {
		return this.getColumnPosition((int) ((Integer) columnId));
	}

	public void fireOnUserDblclick(String action, Map<String, String> properties) {
		for (int i = 0; i < menuListeners.size(); i++)
			menuListeners.get(i).onUserDblclick(this, action, properties);
	}

	@Override
	public void snapToColumn(Object columnId) {
	}

	@Override
	public FastWebColumn getFastWebColumn(Object columnId) {
		return this.columns.get(SH.parseInt((String) columnId));
	}

	@Override
	public void fireOnColumnsArranged() {
		for (int i = 0; i < this.listeners.size(); i++)
			this.listeners.get(i).onColumnsArranged(this);
	}
	@Override
	public void fireOnColumnsSized() {
		for (int i = 0; i < this.listeners.size(); i++)
			this.listeners.get(i).onColumnsSized(this);
	}
	@Override
	public void fireOnFilterChanging() {
		for (int i = 0; i < this.listeners.size(); i++)
			this.listeners.get(i).onFilterChanging(this);
	}

	public void autoSizeColumn(int columnPos, PortletMetrics metrics) {
		int nodeDepthWidthConst = 23;
		int nodeFontSize = 13;
		FastWebTreeColumn column = this.getColumn(columnPos);
		WebTreeNodeFormatter colFormatter = column.getFormatter();

		StringBuilder htmlSink = new StringBuilder();
		StringBuilder styleSink = new StringBuilder();
		List<WebTreeNode> visibleNodes = this.getVisibleNodes();

		int titleWidth = metrics.getHtmlWidth(column.getColumnName(), styleSink, nodeFontSize);
		int width = 0;

		width = Math.max(titleWidth, width);
		for (int i = 0; i < visibleNodes.size(); i++) {
			WebTreeNode node = visibleNodes.get(i);
			colFormatter.formatToHtml(node, htmlSink, styleSink);
			SH.clear(htmlSink);
			colFormatter.formatToText(node, htmlSink);
			int width2 = metrics.getWidth(htmlSink, styleSink, nodeFontSize);
			if (columnPos == 0)
				width2 += (node.getDepth() + 0) * nodeDepthWidthConst;
			width = Math.max(width, width2);
			SH.clear(htmlSink);
			SH.clear(styleSink);
		}

		width += 24;
		if (column.getWidth() != width) {
			column.setWidth(width);
			this.flagStyleChanged();
			fireOnColumnsSized();
		}
	}
	public void setShowCheckUncheckOption(boolean enabled) {
	}

	public void onRangeChanged(int min, int max) {
		if (!rootLevelVisible) {
			min--;
			max--;
		}
		boolean changed = false;
		if (min < this.changedRangeTop || this.changedRangeTop == -1) {
			this.changedRangeTop = min;
			changed = true;
		}
		if (max > this.changedRangeBottom || this.changedRangeBottom == -1) {
			this.changedRangeBottom = max;
			changed = true;
		}
		if (changed)
			changed(CHANGED_ROWS);
	}

	public StringBuilder getValueAsText(WebTreeNode node, int column, StringBuilder sb) {
		FastWebTreeColumn webColumn = getColumn(column);
		webColumn.getFormatter().formatToText(node, sb);
		return sb;
	}

	public Iterable<WebTreeNode> getFilteredRows() {
		return this.treeManager.getRoot().getAllFilteredChildren();
	}

	public WebTreeNode getNode(int i) {
		return this.treeManager.getNodes().get(i);
	}

	public int getNodesCount() {
		return this.treeManager.getNodes().size();
	}

	public void setFilteredIn(Integer columnId, Set<String> filterIn) {
		this.setFilteredIn(columnId, filterIn, true, false, false, null, false, null, false);
	}
	public void setFilteredIn(Integer columnId, Set<String> filterIn, boolean keep, boolean includeNull, boolean isPattern, String min, String max) {
		this.setFilteredIn(columnId, filterIn, keep, includeNull, isPattern, min, true, max, true);
	}
	// set and update
	public void setFilteredIn(Integer columnId, Set<String> filterIn, boolean keep, boolean includeNull, boolean isPattern, String min, boolean minInclusive, String max,
			boolean maxInclusive) {
		if (filterIn == null) // remove filter
			setFilteredIn(columnId, (WebTreeFilteredInFilter) null);
		else {
			WebTreeFilteredInFilter f = new WebTreeFilteredInFilter(getColumn(columnId));
			f.setValues(filterIn, isPattern).setMin(minInclusive, min).setMax(maxInclusive, max).setIncludeNull(includeNull).setKeep(keep);
			setFilteredIn(columnId, f);
		}

	}

	// just set, no update
	public void setFilteredInNoRun(Integer columnId, Set<String> filterIn, boolean keep, boolean includeNull, boolean isPattern, String min, boolean minInclusive, String max,
			boolean maxInclusive) {
		if (filterIn == null) // remove filter
			setFilteredIn(columnId, (WebTreeFilteredInFilter) null);
		else {
			WebTreeFilteredInFilter f = new WebTreeFilteredInFilter(getColumn(columnId));
			f.setValues(filterIn, isPattern).setMin(minInclusive, min).setMax(maxInclusive, max).setIncludeNull(includeNull).setKeep(keep);
			filteredIn.put(columnId, f);
			Map<Integer, WebTreeFilteredInFilter> t = new HashMap<Integer, WebTreeFilteredInFilter>(filteredIn);
			final FastWebTreeColumnFilter columnsFilter = new FastWebTreeColumnFilter(this, t);
			this.treeManager.setFilter(this.treeManager.getSearchFilter(), null, columnsFilter);
		}

	}

	public boolean hasSelected() {
		return this.treeManager.hasSelected();
	}
	@Override
	public int getVisibleColumnsLimit() {
		return this.visibleColumnsLimit;
	}
	@Override
	public void setVisibleColumnsLimit(int columnsLimit) {
		if (columnsLimit != this.visibleColumnsLimit)
			if (columnsLimit < -1)
				this.visibleColumnsLimit = -1;
			else
				this.visibleColumnsLimit = columnsLimit;
	}

	public void addListener(WebTreeListener listener) {
		this.listeners.add(listener);
	}
	public void removeListener(WebTreeListener listener) {
		this.listeners.remove(listener);
	}

	public void clearSort() {
		if (this.sortedColumnIds.isEmpty())
			return;
		this.sortedColumnIds.clear();
		this.toSort.clear();
		treeManager.setComparator(null);
		changed(CHANGED_ALL);

	}

	public void clearPendingSort() {
		this.toSort.clear();
	}

	public void flagActiveChanged() {
		changed(CHANGED_ACTIVE_ROW);
	}

	private Map<String, String> htmlIdSelectorForColumns = new HashMap<String, String>();

	public void setHtmlIdSelectorForColumn(String columnId, String his) {
		String htmlIdSelector = this.htmlIdSelectorForColumns.get(columnId);
		if (OH.eq(htmlIdSelector, his))
			return;
		this.htmlIdSelectorForColumns.put(columnId, his);
		//Used in createJsInitColumns 
		//changed(CHANGED_COLUMNS);
		changed(CHANGED_ALL);
	}

	public LinkedHashMap<Integer, Integer> getToSort() {
		return toSort;
	}

	public void setKeepSorting(boolean keepSorting) {
		this.keepSorting = keepSorting;
	}

	public boolean isPrimarySort(Integer colId) {
		if (!isSorting())
			return false;
		Set<Integer> sortedCols = getSortedColumnIds();
		for (Integer s : sortedCols) {
			if (OH.eq(s, colId))
				return true;
			break;
		}
		return false;
	}

	public boolean isShowExpandMenuItems() {
		return showExpandMenuItems;
	}

	public void setShowExpandMenuItems(boolean showExpandMenuItems) {
		this.showExpandMenuItems = showExpandMenuItems;
	}
}
