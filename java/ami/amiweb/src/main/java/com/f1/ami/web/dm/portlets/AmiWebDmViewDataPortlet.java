package com.f1.ami.web.dm.portlets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.ami.web.AmiWebConsts;
import com.f1.ami.web.AmiWebDmPortlet;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.ami.web.dm.AmiWebDmTablesetSchema;
import com.f1.base.Column;
import com.f1.base.Table;
import com.f1.base.TableListenable;
import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.portal.impl.FastTreePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.MultiDividerPortlet;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.table.WebCellFormatter;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.WebTableColumnContextMenuFactory;
import com.f1.suite.web.table.impl.BasicWebColumn;
import com.f1.suite.web.tree.WebTreeContextMenuListener;
import com.f1.suite.web.tree.WebTreeManager;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.impl.FastWebTree;
import com.f1.suite.web.tree.impl.FastWebTreeColumn;
import com.f1.utils.concurrent.IdentityHashSet;
import com.f1.utils.sql.Tableset;
import com.f1.utils.structs.table.BasicTable;

public class AmiWebDmViewDataPortlet extends GridPortlet implements WebTableColumnContextMenuFactory, FormPortletListener, WebTreeContextMenuListener {

	//New
	final public static byte RESPONSEDATA_BEFORE_FILTER = 1;
	final public static byte RESPONSEDATA_AFTER_FILTER = 0;
	final public static byte RESPONSEDATA_DEFAULT = 0;
	final private MultiDividerPortlet resultsMultiDivider;
	final private FastTreePortlet schemaTree;
	final private WebTreeManager treeMgr;
	final private WebTreeNode treeRoot;
	final private AmiWebService service;
	final private List<FastTablePortlet> tables = new ArrayList<FastTablePortlet>();
	private FormPortlet form;

	public AmiWebDmViewDataPortlet(PortletConfig config, List<AmiWebDm> datamodels, AmiWebDmPortlet portlet, byte responseRelationToFilter) {
		this(config);
		for (int i = 0; i < datamodels.size(); i++) {
			addDmToMultiDividerPortlet(datamodels.get(i), portlet, datamodels.size() > 1, responseRelationToFilter);
		}
	}
	public AmiWebDmViewDataPortlet(PortletConfig config) {
		super(config);
		this.service = AmiWebUtils.getService(config.getPortletManager());
		this.form = new FormPortlet(generateConfig());
		form.addButton(new FormPortletButton("Close"));
		form.addFormPortletListener(this);
		this.schemaTree = new FastTreePortlet(generateConfig());
		this.schemaTree.addOption(FastTreePortlet.OPTION_SEARCH_BUTTONS_COLOR, "#007608");
		this.schemaTree.addOption(FastTreePortlet.OPTION_GRIP_COLOR, "_bg=#ffffff");
		this.schemaTree.addOption(FastTreePortlet.OPTION_SCROLL_BUTTON_COLOR, "_bg=#ffffff");
		this.schemaTree.addOption(FastTreePortlet.OPTION_SCROLL_ICONS_COLOR, "#007608");
		this.schemaTree.addOption(FastTreePortlet.OPTION_SEARCH_BAR_HIDDEN, true);
		this.schemaTree.addOption(FastTreePortlet.OPTION_HEADER_DIVIDER_HIDDEN, true);
		this.schemaTree.addOption(FastTreePortlet.OPTION_HEADER_BAR_HIDDEN, true);
		this.schemaTree.addOption(FastTreePortlet.OPTION_CELL_RIGHT_DIVIDER, 0);
		this.schemaTree.addOption(FastTreePortlet.OPTION_CELL_BOTTOM_DIVIDER, 0);
		this.schemaTree.addOption(FastTreePortlet.OPTION_BACKGROUND_STYLE, "_bg=#eeeeee");
		this.schemaTree.addOption(FastTreePortlet.OPTION_GRAY_BAR_STYLE, "_bg=#eeeeee");
		this.treeMgr = schemaTree.getTreeManager();
		this.schemaTree.getTree().addMenuContextListener(this);
		this.schemaTree.getTree().setRootLevelVisible(false);
		this.treeRoot = treeMgr.getRoot();
		this.treeRoot.setIconCssStyle("_bgi=url(" + AmiWebConsts.ICON_FILES + ")");
		this.treeRoot.setName("Datamodels");
		this.resultsMultiDivider = new MultiDividerPortlet(generateConfig(), true);
		DividerPortlet viewDataDivider = new DividerPortlet(generateConfig(), true);
		viewDataDivider.setThickness(1);
		viewDataDivider.addChild(schemaTree);
		viewDataDivider.addChild(resultsMultiDivider);
		this.addChild(viewDataDivider, 0, 0, 1, 1);
		viewDataDivider.setExpandBias(0, 1);
		this.addChild(form, 0, 1, 1, 1);
		this.setRowSize(1, 50);
		viewDataDivider.setOffsetFromTopPx(250);
	}
	public void addDmToMultiDividerPortlet(AmiWebDm dm, AmiWebDmPortlet portlet, boolean includeDmName, byte responseRelationToFilter) {
		TabPortlet tab = new TabPortlet(generateConfig());
		tab.setIsCustomizable(false);

		AmiWebDmTablesetSchema resultsSchema = dm.getResponseOutSchema();
		Tableset results;
		if (responseRelationToFilter == RESPONSEDATA_DEFAULT)
			results = dm.getResponseTableset();
		else
			results = dm.getResponseTablesetBeforeFilter();

		WebTreeNode datamodel = treeMgr.createNode(dm.getDmName(), treeMgr.getRoot(), true).setIconCssStyle("_bgi=url(" + AmiWebConsts.ICON_FOLDER + ")");

		for (String name : resultsSchema.getTableNamesSorted()) {

			WebTreeNode node = treeMgr.createNode(name, datamodel, false).setIconCssStyle("_bgi=url(" + AmiWebConsts.ICON_DOT + ")");
			Table table = results.getTableNoThrow(name);
			AmiWebDmTableSchema schema = resultsSchema.getTable(name);
			table = schema.mapToSchema(table, false);
			FastTablePortlet tablePortlet = new FastTablePortlet(generateConfig(), (TableListenable) new BasicTable(table), includeDmName ? (dm.getDmName() + "." + name) : name);
			tablePortlet.addOption(FastTablePortlet.OPTION_HEADER_ROW_HEIGHT, 32);
			node.setData(tablePortlet);
			AmiWebUtils.applyEndUserTableStyle(tablePortlet);
			tablePortlet.getTable().setColumnMenuFactory(this);
			int i = 0;
			Set<String> sink = new HashSet<String>();
			if (portlet != null && portlet.getUsedDmTables(dm.getAmiLayoutFullAliasDotId()).contains(name)) {
				tablePortlet.addOption(FastTablePortlet.OPTION_TITLE_BAR_COLOR, "#008888");
				portlet.getUsedDmVariables(dm.getAmiLayoutFullAliasDotId(), name, sink);
			}

			for (String column : schema.getColumnNames()) {
				Class<?> type = schema.getClassTypes().getType(column);
				boolean col = table.getColumnIds().contains(column);
				if (col == false)
					continue;
				String stype = service.getMethodFactory().forType(type);
				BasicWebColumn c = tablePortlet.getTable().addColumnAt(true, (String) column + "<BR><I>" + stype, column, this.service.getFormatterManager().getFormatter(type),
						i++);
				WebTreeNode node2 = treeMgr.createNode(column + " (" + schema.getType(column) + ")", node, true).setIconCssStyle("_bgi=url(" + AmiWebConsts.ICON_DOT + ")");
				if (sink.contains(column)) {
					node2.setCssClass("view_used_data");
					c.setHeaderStyle("_bg=#008888");
					c.setCssColumn("view_used_data");
				}
				node2.setData(c);
			}
			tablePortlet.autoSizeAllColumns();
			this.resultsMultiDivider.addChild(tablePortlet);
			this.tables.add(tablePortlet);
			getManager().onPortletAdded(tablePortlet);
		}

	}

	public void clear() {
		this.treeMgr.clear();
		this.tables.clear();
		WebTreeNode t = treeMgr.getRoot();
		this.resultsMultiDivider.removeAllChildren();
	}

	public FastTablePortlet addTable(Table table) {
		return addTable(table, null);
	}
	public FastTablePortlet addTable(Table table, List<WebCellFormatter> formatters) {
		WebTreeNode node = treeMgr.createNode(table.getTitle(), treeMgr.getRoot(), false).setIconCssStyle("_bgi=url(" + AmiWebConsts.ICON_DOT + ")");
		FastTablePortlet tablePortlet = new FastTablePortlet(generateConfig(), (TableListenable) new BasicTable(table), table.getTitle());
		tablePortlet.addOption(FastTablePortlet.OPTION_HEADER_ROW_HEIGHT, 32);
		node.setData(tablePortlet);
		AmiWebUtils.applyEndUserTableStyle(tablePortlet);
		tablePortlet.getTable().setColumnMenuFactory(this);
		int i = 0;
		Set<String> sink = new HashSet<String>();

		for (Column column : table.getColumns()) {
			WebCellFormatter formatter = formatters == null ? null : formatters.get(column.getLocation());
			Class<?> type = column.getType();
			String id = (String) column.getId();
			String stype = service.getMethodFactory().forType(type);
			BasicWebColumn c = tablePortlet.getTable().addColumnAt(true, id + "<BR><I>" + stype, id,
					formatter != null ? formatter : this.service.getFormatterManager().getFormatter(type), i++);
			WebTreeNode node2 = treeMgr.createNode(id + " (" + service.getMethodFactory().forType(column.getType()) + ")", node, true)
					.setIconCssStyle("_bgi=url(" + AmiWebConsts.ICON_DOT + ")");
			if (sink.contains(column)) {
				node2.setCssClass("view_used_data");
				c.setHeaderStyle("_bg=#008888");
				c.setCssColumn("view_used_data");
			}
			node2.setData(c);
		}
		tablePortlet.autoSizeAllColumns();
		this.resultsMultiDivider.addChild(tablePortlet);
		this.tables.add(tablePortlet);
		getManager().onPortletAdded(tablePortlet);
		return tablePortlet;
	}

	@Override
	public WebMenu createColumnMenu(WebTable table, WebColumn column, WebMenu defaultMenu) {
		//		Class type = table.getTable().getColumn(column.getTableColumns()[0]).getType();
		//		defaultMenu.add(new BasicWebMenuDivider());
		//		StringBuilder sb = new StringBuilder();
		//		sb.append("<span style='display:block;max-width:250px;white-space:normal;word-wrap:break-word;padding-top:4px;padding-bottom:4px;'>(type is ")
		//				.append(AmiUtils.toTypeName(service.getMethodFactory(), type)).append(")</span>");
		//		BasicWebMenuLink link = new BasicWebMenuLink(sb.toString(), false, null);
		//		link.setCssStyle("className=ami_help_menu|_bg=#ffffcc|_fg=#000000|_fs=13");
		//		defaultMenu.add(link);
		return defaultMenu;
	}

	@Override
	public WebMenu createColumnMenu(WebTable table, WebMenu defaultMenu) {
		return defaultMenu;
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		this.close();
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}
	@Override
	public void onNodeClicked(FastWebTree fastWebTree, WebTreeNode node) {
		List<WebTreeNode> sel = fastWebTree.getSelected();
		if (sel.isEmpty()) {
			int pos = 0;
			this.resultsMultiDivider.removeAllChildren();
			for (WebTreeNode tableNode : fastWebTree.getTreeManager().getRoot().getChildren()) {
				FastTablePortlet table = (FastTablePortlet) tableNode.getData();
				if (table == null)
					return;
				this.resultsMultiDivider.addChild(table);
				for (WebTreeNode columnNode : tableNode.getChildren()) {
					BasicWebColumn col = (BasicWebColumn) columnNode.getData();
					table.getTable().hideColumn(col.getColumnId());
				}
				for (WebTreeNode columnNode : tableNode.getChildren()) {
					BasicWebColumn col = (BasicWebColumn) columnNode.getData();
					table.getTable().showColumn(col.getColumnId());
				}
			}
			return;
		}
		Set<FastTablePortlet> toShow = new IdentityHashSet<FastTablePortlet>();
		for (WebTreeNode i : sel) {
			if (i.getData() instanceof BasicWebColumn)
				toShow.add((FastTablePortlet) i.getParent().getData());
			else if (i.getData() instanceof FastTablePortlet)
				toShow.add((FastTablePortlet) i.getData());
		}
		for (FastTablePortlet t : this.tables)
			if (t.getParent() != null && !toShow.contains(t))
				this.resultsMultiDivider.removeChild(t.getPortletId());
		if (node != null) {
			if (node.getData() instanceof BasicWebColumn) {
				BasicWebColumn column = (BasicWebColumn) node.getData();
				WebTreeNode parent = node.getParent();
				FastTablePortlet table = (FastTablePortlet) parent.getData();
				if (table.getParent() == null)
					this.resultsMultiDivider.addChild(table);
				int selected = 0;
				for (WebTreeNode i : parent.getAllChildren()) {
					if (i.getSelected())
						selected++;
				}
				if (selected == 0) {
					if (parent.getSelected()) {
						for (WebTreeNode i : parent.getAllChildren()) {
							BasicWebColumn col = (BasicWebColumn) i.getData();
							table.getTable().hideColumn(col.getColumnId());
						}
						for (WebTreeNode i : parent.getAllChildren()) {
							BasicWebColumn col = (BasicWebColumn) i.getData();
							table.getTable().showColumn(col.getColumnId());
						}
					} else {
						this.resultsMultiDivider.removeChild(table.getPortletId());
					}
				} else {
					for (WebTreeNode i : parent.getAllChildren()) {
						BasicWebColumn col = (BasicWebColumn) i.getData();
						if (i.getSelected())
							table.getTable().showColumn(col.getColumnId());
						else
							table.getTable().hideColumn(col.getColumnId());
					}
				}
			} else if (node.getData() instanceof FastTablePortlet) {
				FastTablePortlet table = (FastTablePortlet) node.getData();
				if (table.getParent() == null) {
					this.resultsMultiDivider.addChild(table);
					for (WebTreeNode i : node.getAllChildren()) {
						BasicWebColumn col = (BasicWebColumn) i.getData();
						table.getTable().hideColumn(col.getColumnId());
					}
					for (WebTreeNode i : node.getAllChildren()) {
						BasicWebColumn col = (BasicWebColumn) i.getData();
						table.getTable().showColumn(col.getColumnId());
					}
				}
				int selected = 0;
				for (WebTreeNode i : node.getAllChildren()) {
					if (i.getSelected())
						selected++;
				}
				if (selected == 0) {
					for (WebTreeNode i : node.getAllChildren()) {
						BasicWebColumn col = (BasicWebColumn) i.getData();
						table.getTable().hideColumn(col.getColumnId());
					}
					for (WebTreeNode i : node.getAllChildren()) {
						BasicWebColumn col = (BasicWebColumn) i.getData();
						table.getTable().showColumn(col.getColumnId());
					}
				} else {
					for (WebTreeNode i : node.getAllChildren()) {
						BasicWebColumn col = (BasicWebColumn) i.getData();
						if (i.getSelected())
							table.getTable().showColumn(col.getColumnId());
						else
							table.getTable().hideColumn(col.getColumnId());
					}
				}
			}
		}
	}
	@Override
	public void onNodeSelectionChanged(FastWebTree fastWebTree, WebTreeNode node) {

	}

	@Override
	public void onCellMousedown(FastWebTree tree, WebTreeNode start, FastWebTreeColumn col) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onContextMenu(FastWebTree tree, String action) {
		// TODO Auto-generated method stub

	}
	public void hideButtons() {
		this.setRowSize(1, 0);
		this.form.clearButtons();
	}
	@Override
	public void onUserDblclick(FastWebColumns columns, String action, Map<String, String> properties) {
	}

}
