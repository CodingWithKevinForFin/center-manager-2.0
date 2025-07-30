package com.f1.ami.web.dm.portlets.vizwiz;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.web.AmiWebDesktopPortlet;
import com.f1.ami.web.AmiWebEditStylePortlet;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.ami.web.tree.AmiWebStaticTreePortlet;
import com.f1.ami.web.tree.AmiWebTreeEditColumnPortlet;
import com.f1.ami.web.tree.AmiWebTreeEditGroupingsPortlet;
import com.f1.ami.web.tree.AmiWebTreeGroupBy;
import com.f1.ami.web.tree.AmiWebTreePortlet;
import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.tree.WebTreeContextMenuListener;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.impl.ArrangeColumnsPortlet;
import com.f1.suite.web.tree.impl.FastWebColumn;
import com.f1.suite.web.tree.impl.FastWebTree;
import com.f1.suite.web.tree.impl.FastWebTreeColumn;
import com.f1.utils.SH;
import com.f1.utils.structs.BasicIndexedList;
import com.f1.utils.structs.IndexedList;

public class AmiWebVizwiz_TreeGrid extends AmiWebVizwiz<AmiWebStaticTreePortlet> implements WebTreeContextMenuListener {

	private TabPortlet creatorTabsPortlet;
	private DummyFastColumns fastColumns = new DummyFastColumns();
	private ArrangeColumnsPortlet arrangeColumns;
	private GridPortlet columnGrid;
	private HtmlPortlet blankPortlet;
	private AmiWebTreeEditColumnPortlet columnEditor;
	private AmiWebTreeEditGroupingsPortlet editSettingsPortlet;

	public AmiWebVizwiz_TreeGrid(AmiWebService service, String layoutAlias) {
		super(service, "Tree Grid");
		AmiWebDesktopPortlet desktop = service.getDesktop();
		AmiWebStaticTreePortlet tm = (AmiWebStaticTreePortlet) desktop.newPortlet(AmiWebStaticTreePortlet.Builder.ID, layoutAlias);
		setPreviewPortlet(tm);
		tm.getTree().addMenuContextListener(this);
		this.creatorTabsPortlet = new TabPortlet(generateConfig());
		this.creatorTabsPortlet.getTabPortletStyle().setBackgroundColor("#4c4c4c");
		this.columnGrid = new GridPortlet(generateConfig());
		this.columnGrid.addChild(blankPortlet = new HtmlPortlet(generateConfig(), "<BR><center>Click on a row or the whitespace covered by the column in the tree grid to edit."));
		this.blankPortlet.setCssStyle("_bg=#e2e2e2|style.fontSize=20px");
		this.creatorTabsPortlet.addChild("Columns", this.columnGrid);
		this.creatorTabsPortlet.addChild("Style", (new AmiWebEditStylePortlet(tm.getStylePeer(), generateConfig())).hideCloseButtons(true));
		this.creatorTabsPortlet.setIsCustomizable(false);
		addRefreshButton();
	}
	@Override
	public Portlet getCreatorPortlet() {
		return creatorTabsPortlet;
	}

	@Override
	public boolean initDm(AmiWebDm dm, Portlet initForm, String dmTableName) {
		if (this.arrangeColumns.getVisibleColumns().size() == 0) {
			this.getService().getPortletManager().showAlert("Must have at least one group by column");
			return false;
		}

		this.arrangeColumns.applyChanges();
		AmiWebStaticTreePortlet previewPortlet = getPreviewPortlet();
		previewPortlet.addUsedDatamodel(dm.getAmiLayoutFullAliasDotId(), dmTableName);
		StringBuilder errorSink = new StringBuilder();
		int col = 0;

		Set<String> names = new HashSet<String>();
		previewPortlet.addColumnFormula(-1, null, "count", "count(1)", null, null, null, null, AmiWebUtils.CUSTOM_COL_TYPE_NUMERIC, 0, col++, errorSink, null, "", false);
		names.add("count");
		AmiWebDmTableSchema schema2 = dm.getResponseOutSchema().getTable(dmTableName);
		for (DummyFastWebColumn i : this.fastColumns.getHidden().values()) {
			String var = (String) i.getColumnId();
			Class<?> type = schema2.getClassTypes().getType(var);
			String name = i.getColumnName();
			names.add(name = SH.getNextId(name, names));
			String agg = AmiUtils.isNumericType(type) ? "sum(" : "(";
			byte type2 = AmiWebUtils.guessColumnType(type, name);
			int prec = AmiWebUtils.guessColumnPrecision(type, name);
			if (previewPortlet.addColumnFormula(-1, null, name, agg + AmiUtils.escapeVarName(var) + ")", null, null, null, null, type2, prec, col++, errorSink, null, "",
					false) == null) {
			}
		}
		Set<String> groupByIds = new HashSet<String>();
		List<AmiWebTreeGroupBy> formulas = new ArrayList<AmiWebTreeGroupBy>();
		for (DummyFastWebColumn i : this.fastColumns.getVisible().values()) {
			String var = (String) i.getColumnId();
			String name = i.getColumnName();
			String id = AmiWebUtils.toPrettyVarName(name, "col");
			names.add(name = SH.getNextId(name, names));
			id = SH.getNextId(id, groupByIds, 2);
			groupByIds.add(id);
			AmiWebTreeGroupBy t2 = new AmiWebTreeGroupBy(id, previewPortlet);
			var = AmiUtils.escapeVarName(var);
			t2.setFormula(false, var, null, var, null, null, null, null, null, null, null);
			formulas.add(t2);
		}
		previewPortlet.setFormulas(formulas);
		previewPortlet.flagRebuildCalcs();
		previewPortlet.setUsedDatamodel(dm.getAmiLayoutFullAliasDotId(), dmTableName);
		previewPortlet.onDmDataChanged(dm);
		previewPortlet.getTree().setPinnedColumnsCount(1);
		if (errorSink.length() > 0)
			this.getService().getPortletManager().showAlert(errorSink.toString());
		this.creatorTabsPortlet.addChild(0, "Grouping", (editSettingsPortlet = new AmiWebTreeEditGroupingsPortlet(generateConfig(), previewPortlet)).hideButtonsForm(true), true);
		previewPortlet.onDmDataChanged(dm);
		previewPortlet.setAmiTitle(dmTableName, false);
		return true;
	}
	@Override
	public AmiWebStaticTreePortlet removePreviewPortlet() {
		this.getPreviewPortlet().getTree().removeMenuContextListener(this);
		return super.removePreviewPortlet();
	}
	@Override
	public Portlet getInitForm(AmiWebDm dm, String tableName) {
		Set<String> names = new HashSet<String>();
		AmiWebDmTableSchema schema2 = dm.getResponseOutSchema().getTable(tableName);
		this.fastColumns.clear();
		for (String var : schema2.getColumnNames()) {
			String name = AmiWebUtils.toPrettyName(var);
			names.add(name = SH.getNextId(name, names));
			Class<?> type = schema2.getClassTypes().getType(var);
			this.fastColumns.addColumn(var, name, !AmiUtils.isNumericType(type));
		}
		this.arrangeColumns = new ArrangeColumnsPortlet(generateConfig(), this.fastColumns);
		arrangeColumns.hideCloseButtons();
		arrangeColumns.setVisibleListTitle("Group by");
		arrangeColumns.setHiddenListTitle("Aggregate by");
		return arrangeColumns;
	}

	@Override
	public boolean preview() {
		return this.editSettingsPortlet.applySettings();
	}

	public class DummyFastColumns implements FastWebColumns {

		final private IndexedList<String, DummyFastWebColumn> visible = new BasicIndexedList<String, DummyFastWebColumn>();
		final private IndexedList<String, DummyFastWebColumn> hidden = new BasicIndexedList<String, DummyFastWebColumn>();

		public DummyFastColumns() {
		}
		public void clear() {
			visible.clear();
			hidden.clear();
		}
		public void addColumn(String id, String name, boolean visible) {
			if (this.visible.containsKey(id) || this.hidden.containsKey(id))
				throw new RuntimeException("key already exists");
			(visible ? this.visible : this.hidden).add(id, new DummyFastWebColumn(id, name));
		}
		@Override
		public int getVisibleColumnsCount() {
			return this.visible.getSize();
		}
		@Override
		public FastWebColumn getVisibleColumn(int i) {
			return this.visible.getAt(i);
		}
		@Override
		public int getHiddenColumnsCount() {
			return this.hidden.getSize();
		}
		@Override
		public FastWebColumn getHiddenColumn(int i) {
			return this.hidden.getAt(i);
		}
		@Override
		public void hideColumn(String columnId) {
			DummyFastWebColumn t = visible.remove(columnId);
			if (t == null)
				t = hidden.remove(columnId);
			if (t != null)
				hidden.add((String) t.getColumnId(), t);
		}
		@Override
		public void showColumn(String columnId, int location) {
			DummyFastWebColumn t = hidden.remove(columnId);
			if (t == null)
				t = visible.remove(columnId);
			if (t != null)
				visible.add((String) t.getColumnId(), t, location);
		}
		@Override
		public int getPinnedColumnsCount() {
			return 0;
		}
		@Override
		public void setPinnedColumnsCount(int count) {
		}
		public IndexedList<String, DummyFastWebColumn> getHidden() {
			return hidden;
		}
		public IndexedList<String, DummyFastWebColumn> getVisible() {
			return visible;
		}
		@Override
		public int getColumnPosition(Object columnId) {
			return 0;
		}
		@Override
		public void snapToColumn(Object columnId) {
		}
		@Override
		public FastWebColumn getFastWebColumn(Object columnId) {
			DummyFastWebColumn r = this.visible.getNoThrow((String) columnId);
			return r != null ? r : this.hidden.getNoThrow((String) columnId);
		}
		@Override
		public void fireOnColumnsArranged() {

		}
		@Override
		public void fireOnFilterChanging() {
		}
		@Override
		public void fireOnColumnsSized() {
		}
		@Override
		public int getVisibleColumnsLimit() {
			return -1;
		}
		@Override
		public void setVisibleColumnsLimit(int columnsLimit) {
		}
	}

	public class DummyFastWebColumn implements FastWebColumn {
		final private String id, name;

		public DummyFastWebColumn(String id, String name) {
			this.id = id;
			this.name = name;
		}
		@Override
		public Object getColumnId() {
			return id;
		}
		@Override
		public String getColumnName() {
			return name;
		}
		@Override
		public boolean getIsGrouping() {
			return false;
		}
	}

	@Override
	public void onContextMenu(FastWebTree tree, String action) {

	}
	@Override
	public void onNodeClicked(FastWebTree tree, WebTreeNode node) {

	}
	@Override
	public void onNodeSelectionChanged(FastWebTree fastWebTree, WebTreeNode node) {

	}
	@Override
	public void onCellMousedown(FastWebTree tree, WebTreeNode row, FastWebTreeColumn col) {
		AmiWebTreePortlet previewPortlet = getPreviewPortlet();
		// first we save the current column settings (table does the same)
		if (this.columnEditor != null) {
			if ((this.columnEditor.isChanged() && !this.columnEditor.onUpdate())) {
				// something changed AND update failed
				return;
			}
			// update success
			this.columnGrid.removeChildAt(0, 0);
			this.columnGrid.addChild(blankPortlet);
		}
		if (col.getIsGrouping()) {
			// we don't allow editing on the grouping column
			PortletHelper.ensureVisible(editSettingsPortlet);
			return;
		}
		// then we create a new column setting where user has clicked on
		this.columnEditor = (new AmiWebTreeEditColumnPortlet(previewPortlet, previewPortlet.getColumnFormatter(col.getColumnId().intValue()), 0, false)).hideHeader(true)
				.hideCloseButtons(true);
		// remove the old column editor
		Portlet old = this.columnGrid.removeChildAt(0, 0);
		if (old != this.blankPortlet) // TODO not sure why this is needed, blankPortlet is the only thing we will ever add
			old.close();
		// put in the new one
		this.columnGrid.addChild(this.columnEditor, 0, 0);
		this.getService().getPortletManager().onPortletAdded(this.columnEditor);
		this.columnEditor.getForm().addField(new FormPortletTitleField(col.getColumnName()), 0).setCssStyle("");
		PortletHelper.ensureVisible(this.columnEditor);
	}
	@Override
	public void onUserDblclick(FastWebColumns columns, String action, Map<String, String> properties) {
	}
}
