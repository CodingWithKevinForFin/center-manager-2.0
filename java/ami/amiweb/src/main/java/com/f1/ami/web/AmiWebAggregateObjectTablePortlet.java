package com.f1.ami.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.base.Column;
import com.f1.base.IterableAndSize;
import com.f1.base.Row;
import com.f1.base.TableListenable;
import com.f1.base.TableListener;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuDivider;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.FastTablePortletListener;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebContextMenuFactory;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.WebTableColumnContextMenuFactory;
import com.f1.suite.web.table.WebTableColumnContextMenuListener;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.BasicWebColumn;
import com.f1.suite.web.table.impl.WebTableFilteredInFilter;
import com.f1.utils.CH;
import com.f1.utils.IntArrayList;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Simple;
import com.f1.utils.concurrent.HasherSet;
import com.f1.utils.sql.aggs.AggCalculator;
import com.f1.utils.sql.aggs.AggregateFactory;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.SmartTable;
import com.f1.utils.structs.table.derived.AggregateGroupByColumn;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedColumn;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.DerivedTable;
import com.f1.utils.structs.table.stack.EmptyCalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class AmiWebAggregateObjectTablePortlet extends AmiWebObjectTablePortlet
		implements WebContextMenuFactory, WebTableColumnContextMenuFactory, WebTableColumnContextMenuListener, ConfirmDialogListener, FastTablePortletListener, TableListener {
	private static final Logger log = LH.get();

	private AmiWebAggregator aggregator;
	private AggregateFactory methodFactory;

	private Set<String> groupByColumnIds = new HashSet<String>();

	private boolean addCustomColumnIsGroupBy;

	public AmiWebAggregateObjectTablePortlet(PortletConfig config) {
		super(config);
		this.getTable().addListener(this);
		getTable().addVisibleColumn(new BasicWebColumn(getTable(), "#0", "Count", getService().getFormatterManager().getIntegerWebCellFormatter(), new String[] { "@0" }));
		this.methodFactory = this.getScriptManager().createAggregateFactory();
	}
	protected DerivedTable initTable(DerivedTable derivedTable) {
		derivedTable = new DerivedTable(EmptyCalcFrameStack.INSTANCE);
		derivedTable.setTitle("Ami Objects");
		SmartTable st = new BasicSmartTable(derivedTable);
		FastWebTable table = new FastWebTable(st, getManager().getTextFormatter());
		this.tablePortlet.setTable(table);
		this.aggregator = new AmiWebAggregator(this.getService(), this.tablePortlet, (TableListenable) getTable().getTable(), this.getAmiLayoutFullAlias(),
				this.getService().createStackFrame(this));
		this.formulas.setAggregateTable(this.aggregator.getAggregateTable());
		//		st.addTableListener(this);
		this.aggregator.addAggregateColumn("count(1)");
		return derivedTable;
	}
	protected String[] getTableIds() {
		return new String[] { "#0" };
	}
	protected Class[] getTableTypes() {
		return new Class[] { Integer.class };
	}

	@Override
	protected void removeAmiObject(AmiWebObject entity) {
		if (isHalted())
			return;
		this.aggregator.removeAmiObject(entity);
	}

	@Override
	protected void updateAmiObject(AmiWebObject entity, AmiWebObjectFields changes) {
		if (!meetsWhereFilter(entity))
			aggregator.removeAmiObject((AmiWebObject) entity);
		else
			aggregator.addAmiObject((AmiWebObject) entity, changes);
	}

	@Override
	protected void addAmiObject(AmiWebObject entity) {
		if (isHalted())
			return;
		if (!meetsWhereFilter(entity))
			return;
		aggregator.addAmiObject((AmiWebObject) entity, null);
	}

	public static class Builder extends AmiWebAbstractPortletBuilder<AmiWebAggregateObjectTablePortlet> {

		public static final String OLD_ID = "VortexWebAmiAggregateObject2TablePortlet";
		public static final String ID = "Amirealtimeaggtable";

		public Builder() {
			super(AmiWebAggregateObjectTablePortlet.class);
		}

		@Override
		public AmiWebAggregateObjectTablePortlet buildPortlet(PortletConfig portletConfig) {
			AmiWebAggregateObjectTablePortlet r = new AmiWebAggregateObjectTablePortlet(portletConfig);
			return r;
		}

		@Override
		public String getPortletBuilderName() {
			return "Realtime Aggregate Table";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	private void showAddGroupingColumnDialog() {
		AmiWebAddObjectColumnFormPortlet addAmiObjectPortlet = super.newAddAmiObjectColumnFormPortlet(generateConfig(), this, 0, null);
		addAmiObjectPortlet.setIsAggregate(false);
		//		AmiCenterGetAmiSchemaRequest req = nw(AmiCenterGetAmiSchemaRequest.class);
		//		getService().sendRequestToBackend(getPortletId(), req);
		getManager().showDialog(addAmiObjectPortlet.getTitle(), addAmiObjectPortlet);
	}
	@Override
	public AmiWebAddObjectColumnFormPortlet newAddAmiObjectColumnFormPortlet(PortletConfig config, AmiWebAbstractTablePortlet portlet, int columnPosition, AmiWebCustomColumn col) {
		AmiWebAddObjectColumnFormPortlet r = super.newAddAmiObjectColumnFormPortlet(config, portlet, columnPosition, col);
		if (col == null || !groupByColumnIds.contains(col.getColumnId()))
			r.setIsAggregate(true);
		return r;
	}

	@Override
	public void debug(StringBuilder sb) {
		super.debug(sb);
		sb.append("Group by ids: ");
		SH.join(", ", groupByColumnIds, sb);
		sb.append(SH.NEWLINE);
		getAggregator().debug(sb);
	}

	public AmiWebAggregator getAggregator() {
		return aggregator;
	}

	@Override
	public DerivedCellCalculator toFormula(AmiWebFormula formula, StringBuilder sb, String description, Set<String> usedConstVarsSink) {
		if (SH.isnt(formula.getFormula(true)))
			return null;
		try {
			if (addCustomColumnIsGroupBy) {
				return super.toFormula(formula, sb, description, usedConstVarsSink);
			} else {
				return getScriptManager().toAggCalc(formula.getFormula(true), this.getFormulaVarTypes(formula), aggregator.getAggregateTable(), this, usedConstVarsSink);
			}
		} catch (ExpressionParserException e) {
			LH.info(log, "bad fomula: " + formula, e);
			sb.append(description).append(" formula is invalid: <BR><B>").append(e.getMessage());
			return null;

		}
	}
	@Override
	public String addDerivedColumn(DerivedCellCalculator formula, com.f1.base.CalcTypes varTypes) {
		if (addCustomColumnIsGroupBy) {
			AggregateGroupByColumn col = getAggregator().addGroupBy(formula, varTypes);
			return (String) col.getId();
		} else {
			DerivedColumn col = getAggregator().addAggregateColumn(null, formula, varTypes);
			return (String) col.getId();
		}
	}
	protected boolean columnHasDependencyOnCurrentTime(Object columnId) {
		return false;
	}

	public boolean addCustomColumnGroupBy(AmiWebCustomColumn col, StringBuilder errorSink, int columnLocation, AmiWebCustomColumn replacing, com.f1.base.CalcTypes varTypes,
			boolean populateValues) {
		try {
			addCustomColumnIsGroupBy = true;
			return addCustomColumn(col, errorSink, columnLocation, replacing, varTypes, populateValues);
		} finally {
			addCustomColumnIsGroupBy = false;
		}
	}

	@Override
	public boolean addCustomColumn(AmiWebCustomColumn col, StringBuilder errorSink, int columnLocation, AmiWebCustomColumn replacing, com.f1.base.CalcTypes varTypes,
			boolean populateValues) {
		boolean enable = getAggregator().setNotifySink(false);
		boolean r = false;
		try {
			r = super.addCustomColumn(col, errorSink, columnLocation, replacing, varTypes, populateValues);
			if (r && addCustomColumnIsGroupBy)
				this.groupByColumnIds.add(col.getColumnId());
			return r;
		} finally {
			if (enable)
				getAggregator().setNotifySink(true);
			if (!r & !isHalted())//TODO:HACK
				for (AmiWebObject i : this.getService().getWebManagers().getAmiObjects(this.getLowerRealtimeIds()))
					addAmiObject(i);
		}
	}

	@Override
	protected void onWebColumnAdded(BasicWebColumn webColumn) {
		if (addCustomColumnIsGroupBy)
			webColumn.setIsGrouping(true);
	}

	public boolean isAggregate() {
		return true;
	}

	@Override
	public void init(Map<String, Object> configuration, Map<String, String> origToNewIdMapping, StringBuilder sb) {
		this.aggregateOnVisibleOnly = CH.getOr(Caster_Boolean.INSTANCE, configuration, "aggVisOnly", Boolean.FALSE);
		super.init(configuration, origToNewIdMapping, sb);
		updateEnabledAggregates();
	}

	@Override
	protected void initSpecialColumns(Map<String, Object> configuration, List<Map<String, Object>> visibleColumns) {
		super.initSpecialColumns(configuration, visibleColumns);
		List<Map<String, Object>> customCols = (List<Map<String, Object>>) CH.getOr(Caster_Simple.OBJECT, configuration, "amiGroupCols", null);
		Map<String, String> vtypes = (Map<String, String>) CH.getOr(Caster_Simple.OBJECT, configuration, "varTypes", null);
		final com.f1.utils.structs.table.stack.BasicCalcTypes varTypes = new com.f1.utils.structs.table.stack.BasicCalcTypes(vtypes.size());
		for (Entry<String, String> e : vtypes.entrySet())
			varTypes.putType(e.getKey(), AmiWebUtils.saveCodeToType(getService(), e.getValue()));
		varTypes.putAll(getSpecialVariables());
		StringBuilder sb = new StringBuilder();
		for (Map<String, Object> m : customCols) {
			try {
				addCustomColumnIsGroupBy = true;
				AmiWebCustomColumn col = new AmiWebCustomColumn(this, m, false);
				addCustomColumn(col, sb, -1, null, varTypes, false);
				if (m.containsKey("location"))
					visibleColumns.add(m);
			} catch (Exception e) {
				LH.warning(log, "Error with group by column", m, e);
			} finally {
				addCustomColumnIsGroupBy = false;
			}
		}
	}

	@Override
	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = super.getConfiguration();
		List<Map<String, String>> customCols = (List<Map<String, String>>) CH.getOr(Caster_Simple.OBJECT, r, "amiCols", null);
		List<Map<String, String>> customCols2 = new ArrayList<Map<String, String>>();
		List<Map<String, String>> groupingCols = new ArrayList<Map<String, String>>();
		for (Map<String, String> e : customCols)
			if (this.groupByColumnIds.contains(e.get("id")))
				groupingCols.add(e);
			else
				customCols2.add(e);
		r.put("amiCols", customCols2);
		r.put("aggVisOnly", aggregateOnVisibleOnly);
		r.put("amiGroupCols", groupingCols);
		return r;
	}

	@Override
	protected void removeUnusedVariableColumns() {
		super.removeUnusedVariableColumns();
		getAggregator().removeUnusedVariableColumns();
	}

	public void populateConfigMenu(WebMenu headMenu) {
		headMenu.add(new BasicWebMenuLink("Add Grouping Column...", true, "addgrouping"));
		super.populateConfigMenu(headMenu);
	}
	public void setGroupByColumn(AmiWebCustomColumn customColumn) {
		this.groupByColumnIds.add(customColumn.getColumnId());
	}

	@Override
	public void clearRows() {
		super.clearRows();
		aggregator.clear();
	}

	@Override
	public void removeCustomColumnById(String colid) {
		this.groupByColumnIds.remove(colid);
		//		AmiWebCustomColumn cc = getCustomDisplayColumn(colid);
		//		Object[] ids = getTable().getColumn(cc.getColumnId()).getTableColumns();
		//		if (AH.isntEmpty(ids)) {
		boolean t = getAggregator().setNotifySink(false);
		try {
			super.removeCustomColumnById(colid);
			//				for (Object id : ids)
			//					getAggregator().removeAggregateColumn((String) id);
		} catch (Exception e) {
			LH.warning(log, "uncaught exception: ", e);
		} finally {
			if (!isHalted())
				for (AmiWebObject i : this.getService().getPrimaryWebManager().getAmiObjects(this.getLowerRealtimeIds()))
					addAmiObject(i);
			if (t)
				getAggregator().setNotifySink(true);
		}
		//		} else
		//			super.removeCustomColumnById(colid);
	}

	public Set<String> getGroupByColumnIds() {
		return groupByColumnIds;
	}

	@Override
	public boolean canEditColumn(AmiWebCustomColumn col) {
		if (this.groupByColumnIds.contains(col.getColumnId())) {
			String colid = getTable().getColumn(col.getColumnId()).getTableColumns()[0];
			if (aggregator.getAggregateTable().getDependentColumnsCount(colid) > 0) {
				getManager().showAlert("Can not edit this column because there are other columns that depend on it");
				return false;
			}
		}
		return super.canEditColumn(col);
	}
	@Override
	public String getSpecialVariableTitleFor(String name) {
		return aggregator.getUnderlyingColumnTitleFor(name);
	}

	@Override
	public com.f1.base.CalcTypes getSpecialVariables() {
		return this.aggregator.getSpecialVariables();
	}

	@Override
	public WebMenu createColumnMenu(WebTable table, WebColumn column, WebMenu defaultMenu) {
		super.createColumnMenu(table, column, defaultMenu);
		if (inEditMode()) {
			if (this.groupByColumnIds.contains(column.getColumnId())) {
				((BasicWebMenu) defaultMenu).removeChildByAction("delete");
				defaultMenu.add(new BasicWebMenuLink("Delete Grouping Column...", true, "delete").setCssStyle("className=ami_edit_menu"));
			}
			defaultMenu.add(new BasicWebMenuLink("Add Grouping Column...", true, "addgrouping").setCssStyle("className=ami_edit_menu"));
		}
		return defaultMenu;
	}
	@Override
	public WebMenu createColumnMenu(WebTable table, WebMenu defaultMenu) {
		super.createColumnMenu(table, defaultMenu);
		if (inEditMode()) {
			defaultMenu.add(new BasicWebMenuDivider());
			defaultMenu.add(new BasicWebMenuLink("Add Grouping Column...", true, "addgrouping").setCssStyle("className=ami_edit_menu"));
		}
		return defaultMenu;
	}
	@Override
	public void onColumnContextMenu(WebTable table, WebColumn column, String action) {
		if ("addgrouping".equals(action)) {
			showAddGroupingColumnDialog();
		} else
			super.onColumnContextMenu(table, column, action);
	}

	@Override
	public void onContextMenu(WebTable table, String action) {
		if ("addgrouping".equals(action)) {
			showAddGroupingColumnDialog();
		} else
			super.onContextMenu(table, action);
	}

	@Override
	public AmiWebPanelSettingsPortlet showSettingsPortlet() {
		return new AmiWebAggregateTableSettingsPortlet(generateConfig(), (AmiWebObjectTablePortlet) this);
	}

	@Override
	public String getColumnTitleFor(String i) {
		return getSpecialVariableTitleFor(i);
	}

	@Override
	public void setDataTypes(Set<String> selected) {
		if (!selected.containsAll(getLowerRealtimeIds())) {
			getAggregator().clear();
			super.setDataTypes(selected);
			if (!isHalted())
				for (AmiWebObject i : this.getService().getWebManagers().getAmiObjects(this.getLowerRealtimeIds()))
					addAmiObject(i);
		} else
			super.setDataTypes(selected);
	}

	//	public void setAddCustomColumnsIsGroupBy(boolean b) {
	//		this.addCustomColumnIsGroupBy = b;
	//	}

	//	@Override
	//	public String replaceVarsWithTitlesInFormula(String varprefix, String formula) {
	//		if (SH.isnt(formula))
	//			return null;
	//		Node node = getService().getScriptManager().getParser(this.getAmiLayoutFullAlias()).getExpressionParser().parse(formula);
	//		replaceVarsWithTitlesInFormula(varprefix, node);
	//		return node.toString();
	//	}
	//	private void replaceVarsWithTitlesInFormula(String varprefix, Node node) {
	//		if (node instanceof VariableNode) {
	//			VariableNode varnode = (VariableNode) node;
	//			if (varnode.varname.startsWith(varprefix)) {
	//				String varname = SH.stripPrefix(varnode.varname, varprefix, true);
	//				WebColumn col = getTable().getColumn(varname);
	//				if (col == null)
	//					throw new ExpressionParserException(node.getPosition(), "Unknown top level variable: " + varnode.varname);
	//				varname = AmiWebUtils.toValidVarname(varprefix + col.getColumnName());
	//				varnode.varname = varname;
	//			}
	//		} else if (node instanceof OperationNode) {
	//			replaceVarsWithTitlesInFormula(varprefix, ((OperationNode) node).left);
	//			replaceVarsWithTitlesInFormula(varprefix, ((OperationNode) node).right);
	//		} else if (node instanceof MethodNode) {
	//			MethodNode mn = (MethodNode) node;
	//			if (OH.ne(mn.methodName, "sum") && OH.ne(mn.methodName, "min") && OH.ne(mn.methodName, "max") && OH.ne(mn.methodName, "count")) {
	//				for (Node param : mn.params)
	//					replaceVarsWithTitlesInFormula(varprefix, param);
	//			}
	//		} else if (node instanceof ExpressionNode) {
	//			replaceVarsWithTitlesInFormula(varprefix, ((ExpressionNode) node).value);
	//		}
	//	}

	//	@Override
	//	public String replaceTitlesWithVarsInFormula(String varprefix, String formula) {
	//		if (addCustomColumnIsGroupBy || SH.isnt(formula))
	//			return formula;
	//		Node node = getService().getScriptManager().getParser(this.getAmiLayoutFullAlias()).getExpressionParser().parse(formula);
	//		replaceTitlesWithVarsInFormula(varprefix, node);
	//		return node.toString();
	//	}
	//	private void replaceTitlesWithVarsInFormula(String varprefix, Node node) {
	//		if (node instanceof VariableNode) {
	//			VariableNode varnode = (VariableNode) node;
	//			String varname = varnode.varname;
	//			//			String varname = AmiWebUtils.fromValidVarname(node.getPosition(), varnode.varname);
	//			if (varname.startsWith(varprefix)) {
	//				varname = SH.stripPrefix(varname, varprefix, true);
	//				WebColumn col = findColumnByTitle(varname);
	//				if (col == null)
	//					throw new ExpressionParserException(node.getPosition(), "Unknown top level variable: " + varname);
	//
	//				varname = col.getColumnId();
	//				varnode.varname = varprefix + varname;
	//			}
	//		} else if (node instanceof OperationNode) {
	//			replaceTitlesWithVarsInFormula(varprefix, ((OperationNode) node).left);
	//			replaceTitlesWithVarsInFormula(varprefix, ((OperationNode) node).right);
	//		} else if (node instanceof MethodNode) {
	//			MethodNode mn = (MethodNode) node;
	//			if (OH.ne(mn.methodName, "sum") && OH.ne(mn.methodName, "min") && OH.ne(mn.methodName, "max") && OH.ne(mn.methodName, "count")) {
	//				for (Node param : mn.params)
	//					replaceTitlesWithVarsInFormula(varprefix, param);
	//			}
	//		} else if (node instanceof ExpressionNode) {
	//			replaceTitlesWithVarsInFormula(varprefix, ((ExpressionNode) node).value);
	//		}
	//	}
	@Override
	public String getConfigMenuTitle() {
		return "Realtime Aggregate Table";
	}

	@Override
	public com.f1.base.CalcTypes getLinkableVars() {
		com.f1.utils.structs.table.stack.BasicCalcTypes r = new com.f1.utils.structs.table.stack.BasicCalcTypes();
		for (String column : getTable().getColumnIds()) {
			WebColumn col = getTable().getColumn(column);
			String[] columns = col.getTableColumns();
			r.putType(col.getColumnName(), getTable().getTable().getColumn(columns[0]).getType());
		}
		return r;
	}

	@Override
	protected List<Row> getSelectedRowsForCommand(WebTable table) {
		return getValuesForLink(table.getSelectedRows()).getRows();
	}
	protected com.f1.base.CalcTypes getColumnTypesForCommand(WebTable table) {
		return getValuesForLink(Collections.EMPTY_LIST).getColumnTypesMapping();
	}

	private Set<Object> selectedRowsBeforeHidden = new HasherSet<Object>();
	private Object activeRowBeforeHidden;

	private boolean aggregateOnVisibleOnly = false;

	public void stopProcessingAMiData(boolean isHidden) {
		if (isHidden) {
			selectedRowsBeforeHidden.clear();
			activeRowBeforeHidden = null;
			List<Row> selected = getTable().getSelectedRows();
			if (selected.size() > 0) {
				Row ar = getTable().getActiveRow();
				int[] gbcol = getGroupByColLocations();
				if (ar != null)
					activeRowBeforeHidden = getCompositeKey(gbcol, ar);
				for (Row row : selected)
					if (row != ar)
						selectedRowsBeforeHidden.add(getCompositeKey(gbcol, row));
			}
			clearAmiData();
		} else {
			rebuildAmiData();

			if (selectedRowsBeforeHidden.size() > 0) {
				int[] gbcol = getGroupByColLocations();
				IntArrayList uids = new IntArrayList(selectedRowsBeforeHidden.size());
				for (Row row : getTable().getRows()) {
					Object key = getCompositeKey(gbcol, row);
					if (OH.eq(activeRowBeforeHidden, key))
						uids.add(0, row.getLocation());
					else if (selectedRowsBeforeHidden.contains(key))
						uids.add(row.getLocation());
				}
				getTable().setSelectedRows(uids.toIntArray());
				selectedRowsBeforeHidden.clear();
			}
		}
	}
	protected void clearCachedDataDueToHidden() {
		selectedRowsBeforeHidden.clear();
		this.activeRowBeforeHidden = null;
	}
	private int[] getGroupByColLocations() {
		Set<String> ids = getGroupByColumnIds();
		if (ids.size() == 0)
			return OH.EMPTY_INT_ARRAY;
		int[] r = new int[ids.size()];
		int pos = 0;
		for (String id : ids) {
			r[pos++] = getTable().getColumn(id).getTableColumnLocations()[0];
		}

		return r;
	}

	private Object getCompositeKey(int groupByColLocations[], Row row) {
		if (groupByColLocations.length == 0)
			return null;
		if (groupByColLocations.length == 1)
			return row.getAt(groupByColLocations[0]);
		final ArrayList<Object> r = new ArrayList<Object>(groupByColLocations.length);
		for (int pos : groupByColLocations)
			r.add(row.getAt(pos));
		return r;
	}
	public boolean isAggregateOnVisibleColumnsOnly() {
		return aggregateOnVisibleOnly;
	}

	public void setAggregateOnVisibleColumnsOnly(boolean enabled) {
		if (this.aggregateOnVisibleOnly == enabled)
			return;
		this.aggregateOnVisibleOnly = enabled;
		this.updateEnabledAggregates();
		if (this.aggregateOnVisibleOnly)
			this.activeGroupByColumnIds.clear();
	}

	private Set<String> activeGroupByColumnIds = new HashSet<String>();

	@Override
	public void onFastTablePortletChanged(int changes) {
		if (aggregateOnVisibleOnly && MH.anyBits(changes, FastWebTable.CHANGED_COLUMNS)) {
			FastWebTable t = this.getTable();
			Set<String> groupBys = new HashSet<String>();
			for (int i = 0, l = t.getVisibleColumnsCount(); i < l; i++) {
				String colId = t.getVisibleColumn(i).getColumnId();
				if (this.groupByColumnIds.contains(colId)) {
					groupBys.add(colId);
				}
			}
			if (!groupBys.equals(activeGroupByColumnIds)) {
				updateEnabledAggregates();
				this.activeGroupByColumnIds.clear();
				this.activeGroupByColumnIds.addAll(groupBys);
			}
		}
	}
	private void updateEnabledAggregates() {
		boolean hasCleared = false;
		if (!aggregateOnVisibleOnly) {
			for (Entry<Object, AggregateGroupByColumn> i : getAggregator().getAggregateTable().getGroupbyColumns().entrySet())
				if (!i.getValue().getEnabled()) {
					if (!hasCleared) {
						this.clearAmiData();
						hasCleared = true;
					}
					i.getValue().setEnabled(true);
				}
		} else {
			Set<String> underlyingCols = new HashSet<String>();
			FastWebTable t = this.getTable();
			for (int i = 0, l = t.getVisibleColumnsCount(); i < l; i++) {
				String colId = t.getVisibleColumn(i).getColumnId();
				if (this.groupByColumnIds.contains(colId)) {
					WebColumn col = this.getTable().getColumn(colId);
					for (Object c : col.getTableColumns())
						underlyingCols.add((String) c);
				}
			}
			Set<String> aggFilters = CH.comm(getTable().getFilteredInColumns(), this.groupByColumnIds, false, false, true);
			if (aggFilters.size() > 0)
				for (int i = 0, l = t.getHiddenColumnsCount(); i < l; i++) {
					String colId = t.getHiddenColumn(i).getColumnId();
					if (aggFilters.contains(colId))
						this.getTable().setFilteredIn((String) colId, (WebTableFilteredInFilter) null);
				}
			for (Entry<Object, AggregateGroupByColumn> i : getAggregator().getAggregateTable().getGroupbyColumns().entrySet()) {
				boolean needsEnabled = underlyingCols.contains(i.getKey());
				if (i.getValue().getEnabled() != needsEnabled) {
					if (!hasCleared) {
						this.clearAmiData();
						hasCleared = true;
					}
					i.getValue().setEnabled(needsEnabled);
				}
			}
		}
		if (hasCleared)
			this.rebuildAmiData();
	}
	protected AmiWebObject getAmiWebObject(Row row) {
		return (AmiWebObject) row.getAt(0);
	}

	@Override
	public com.f1.base.CalcTypes getRealtimeObjectsOutputSchema() {
		com.f1.utils.structs.table.stack.BasicCalcTypes r = new com.f1.utils.structs.table.stack.BasicCalcTypes();
		for (String s : this.getTable().getColumnIds()) {
			AmiWebCustomColumn c = this.getCustomDisplayColumn(s);
			if (c != null)
				r.putType(c.getTitle(false), c.getDataType());
			else {
				BasicWebColumn col = (BasicWebColumn) this.getTable().getFastWebColumn(s);
				Column col2 = this.getTable().getTable().getColumn(col.getTableColumns()[0]);
				r.putType(col.getColumnName(), col2.getType());
			}
		}
		return r;
	}

	private AmiWebObjectFieldsImpl tmpChanges = new AmiWebObjectFieldsImpl();

	@Override
	public void onCell(Row row, int cell, Object oldValue, Object newValue) {
		tmpChanges.clear();
		AmiWebObject_AggregateWrapper at = (AmiWebObject_AggregateWrapper) row.getAt(dataLocation);
		String name = at.getName(cell);
		tmpChanges.addChange(name, oldValue);
		handleDownstreamForUpdate(at, row, tmpChanges);
	}
	@Override
	protected void rebuildAmiData() {
		IterableAndSize<AmiWebObject> values = getService().getWebManagers().getAmiObjects(this.getLowerRealtimeIds());
		boolean ks = this.getTable().isKeepSorting();
		long now1 = System.currentTimeMillis();
		if (ks)
			getTable().pauseSort(true);
		this.getTable().removeListener(this);
		this.aggregator.startBulkAdd();
		for (AmiWebObject obj : values)
			addAmiObject(obj);
		this.aggregator.finishBulkAdd();
		this.getTable().addListener(this);
		if (ks)
			getTable().pauseSort(false);
		if (log.isLoggable(Level.FINE)) {
			long now2 = System.currentTimeMillis();
			LH.fine(log, "User ", getService().getUserName(), ": rebuilt ", this.getAri(), ": ", values.size(), " row(s) in ", (now2 - now1), " millis");
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public String createTooltip(WebColumn col, Row row) {
		if (col == null)
			return null;
		final AmiWebCustomColumn customCol = getCustomDisplayColumn(col.getColumnId());
		if (customCol == null)
			return null;
		final AmiWebFormula tooltip = customCol.getTooltipFormula();
		if (tooltip == null)
			return null;
		//Resolve tooltip with aggregations
		try {
			ReusableCalcFrameStack sf = getStackFrame();
			com.f1.base.CalcTypes variables = this.aggregator.getInnerTable().getColumnTypesMapping();
			//Recompute the formula as an aggregate calculator
			DerivedCellCalculator derivedCalc = this.getScriptManager().toAggCalc(tooltip.getFormula(false), variables, this.methodFactory, this, null);
			if (derivedCalc.isConst())
				return SH.toString(derivedCalc.get(null));
			//Resolve all aggregations
			List<AggCalculator> aggCalcs = new ArrayList<AggCalculator>();
			DerivedHelper.find(derivedCalc, AggCalculator.class, aggCalcs);
			if (!aggCalcs.isEmpty()) {
				//Collect all relevant rows
				final Iterable<Row> it = this.aggregator.getUnderlyingRows(row);
				List<Row> rows = CH.l(it);
				for (final AggCalculator aggCalc : aggCalcs)
					aggCalc.visitRows(sf, rows);
			}
			//Get derived row with actual column ids (instead of @1...)
			return SH.toString(derivedCalc.get(sf.reset(DerivedHelper.toFrame((Map) row.get("#params")))));
		} catch (Exception e) {
			LH.severe(log, "Failed to create tooltip for: " + col.getColumnName() + ". Exception: " + e.toString());
			return null;
		}
	}
}
