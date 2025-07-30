package com.f1.ami.web.tree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.web.AmiWebAbstractPortletBuilder;
import com.f1.ami.web.AmiWebDmPortlet;
import com.f1.ami.web.AmiWebDmPortletBuilder;
import com.f1.ami.web.AmiWebPanelSettingsPortlet;
import com.f1.ami.web.AmiWebUsedDmSingleton;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmError;
import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.ami.web.dm.AmiWebDmsImpl;
import com.f1.base.CalcFrame;
import com.f1.base.CalcTypes;
import com.f1.base.Caster;
import com.f1.base.Column;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.base.TableList;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.TreeStateCopier;
import com.f1.suite.web.portal.impl.TreeStateCopierIdGetter;
import com.f1.suite.web.tree.WebTreeFilter;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.impl.FastWebTree;
import com.f1.suite.web.tree.impl.FastWebTreeColumnFilter;
import com.f1.utils.CH;
import com.f1.utils.CasterManager;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.concurrent.HasherSet;
import com.f1.utils.concurrent.LinkedHasherMap;
import com.f1.utils.impl.ArrayHasher;
import com.f1.utils.sql.aggs.AggCalculator;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.IntSet;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.columnar.ColumnarTable;
import com.f1.utils.structs.table.columnar.ReadonlyTable;
import com.f1.utils.structs.table.stack.EmptyCalcTypes;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class AmiWebStaticTreePortlet extends AmiWebTreePortlet implements AmiWebDmPortlet, TreeStateCopierIdGetter {
	private static final Logger log = LH.get();

	private AmiWebUsedDmSingleton dmSingleton;
	private Table table;
	private boolean clearOnDataStale = true;
	private List<Row> tmpRows = new ArrayList<Row>(4);
	private List<TreeRecursiveBuilder<Object, WebTreeNode>> recursiveBuilders;
	private static final Object NULL_KEY = new Object();

	public static class Builder extends AmiWebAbstractPortletBuilder<AmiWebStaticTreePortlet> implements AmiWebDmPortletBuilder<AmiWebStaticTreePortlet> {
		public static final String ID = "amitree";

		public Builder() {
			super(AmiWebStaticTreePortlet.class);
		}
		@Override
		public AmiWebStaticTreePortlet buildPortlet(PortletConfig portletConfig) {
			AmiWebStaticTreePortlet r = new AmiWebStaticTreePortlet(portletConfig);
			return r;
		}
		@Override
		public String getPortletBuilderName() {
			return "Tree";
		}
		@Override
		public String getPortletBuilderId() {
			return ID;
		}
		@Override
		public List<String> extractUsedDmAndTables(Map<String, Object> portletConfig) {
			return AmiWebUsedDmSingleton.extractUsedDmAndTables(portletConfig);
		}

		@Override
		public void replaceUsedDmAndTable(Map<String, Object> portletConfig, int position, String name) {
			AmiWebUsedDmSingleton.replaceUsedDmAndTable(portletConfig, position, name);
		}

	}

	public AmiWebStaticTreePortlet(PortletConfig config) {
		super(config);
		this.dmSingleton = new AmiWebUsedDmSingleton(this.getService().getDmManager(), this);
		this.treeManager.getRoot().setData(new AmiWebStaticTreeRow(this.treeManager.getRoot(), this, false, false));
	}

	@Override
	public void clearAmiData() {
		super.clearAmiData();
		this.table = null;
		this.treeManager.getRoot().setData(new AmiWebStaticTreeRow(this.treeManager.getRoot(), this, false, false));
	}
	@Override
	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = super.getConfiguration();
		this.dmSingleton.getConfiguration(getAmiLayoutFullAlias(), r);
		if (this.getUserDefinedVariables().isVarsEmpty()) {//backwards compatibility to load trees prior to having vartypes stored separately
			AmiWebDmTableSchema dm = getDm();
			CalcTypes classTypes = dm.getClassTypes();
			for (String i : classTypes.getVarKeys()) {
				Class<?> type = classTypes.getType(i);
				if (type != null)
					putUserDefinedVariable(i, type);
				else
					LH.warning(log, "For '", this.toString() + "', underlying dm '", getDm().toString(), "' has bad var entry: " + i);
			}
		}
		if (this.clearOnDataStale == false) //Only put in configuration if false
			r.put("cods", this.clearOnDataStale);
		return r;
	}
	@Override
	public void init(Map<String, Object> configuration, Map<String, String> origToNewIdMapping, StringBuilder sb) {
		this.dmSingleton.init(getAmiLayoutFullAlias(), configuration);
		this.clearOnDataStale = CH.getOrNoThrow(Caster_Boolean.INSTANCE, configuration, "cods", true); // If null or not in configuration true
		super.init(configuration, origToNewIdMapping, sb);
	}

	//	@Override
	//	public void onInitDone() {
	//		super.onInitDone();
	//		rebuildAmiData();
	//	}

	@Override
	public boolean isRealtime() {
		return false;
	}

	@Override
	public String getConfigMenuTitle() {
		return "Tree";
	}

	@Override
	protected void rebuildCalcs() {
		super.rebuildCalcs();
		clearRowCaches(treeManager.getRoot());
		this.currentNode = null;
	}

	@Override
	protected CalcFrame onCurrentNodeChanged(WebTreeNode node) {
		AmiWebStaticTreeRow rows = getData(node);
		if (rows == null)
			return emptyCalcFrame();
		int n = 0;
		this.tmpRows.clear();
		if (rows.isCached()) {
			for (AggCalculator i : this.aggregateFactory.getAggregates()) {
				i.setValue(rows.getCache(n++));
			}
		} else {
			ReusableCalcFrameStack sf = getStackFrame();
			rows.getRows(this.tmpRows, null);
			for (AggCalculator i : this.aggregateFactory.getAggregates()) {
				i.reset();
				i.visitRows(sf, this.tmpRows);
				rows.setCache(n++, i.get(null));
			}
			rows.setIsCached();
		}
		if (tmpRows.size() == 0) {
			Row t = rows.getFirstRow();
			if (t != null) {
				return t;
			} else
				return emptyCalcFrame();
		} else
			return this.tmpRows.get(0);
	}

	public AmiWebDmTableSchema getDm() {
		return this.dmSingleton.getDmTableSchema();
	}

	private boolean displayLastRuntime = true;

	@Override
	public void onDmDataChanged(AmiWebDm datamodel) {
		this.showWaitingSplash(false);
		if (datamodel instanceof AmiWebDmsImpl) {
			if (this.displayLastRuntime) {
				this.getFastTreePortlet().setDisplayTimeFormatted(
						AmiWebUtils.getService(getManager()).getFormatterManager().getDatetimeSecsFormatter().format(((AmiWebDmsImpl) datamodel).getLastQueryEndTimeMillis()));
			} else {
				this.getFastTreePortlet().setDisplayTimeFormatted(null);
			}
		}
		rebuildAmiData();
	}

	@Override
	protected void rebuildAmiData() {
		// 4 scenarios:
		// 1. no old/new table: no op
		// 2. has old table, no new table: clear old data
		// 3. no old table, has new table: add it, build filter
		// 4. has both old/new table: delta and build filter
		Table nuwTable = mapFromVars(this.dmSingleton.getTable());
		TreeStateCopier tsc = null;
		if (nuwTable instanceof ReadonlyTable)
			nuwTable = new ColumnarTable(nuwTable);
		List<Row> toAdd = new ArrayList<Row>();
		if (containsRecursive()) {
			tsc = new TreeStateCopier(this.getFastTreePortlet(), this);
			if (this.table != null)
				this.clearAmiData();
			if (nuwTable != null) {
				for (Row i : nuwTable.getRows())
					if (shouldKeep(i))
						toAdd.add(i);
				this.table = nuwTable;
			}
		} else if (nuwTable != null && this.table != null) {
			// delta based if we have the old and new table
			IdentityHashMap<Row, AmiWebStaticTreeRow> rowsToEntries = new IdentityHashMap<Row, AmiWebStaticTreeRow>();
			buildRowsToEntitiesMapping(this.getTree().getTreeManager().getRoot(), rowsToEntries);
			List<Row> toRem = new ArrayList<Row>();
			Set<Object> deps = new HashSet<Object>();
			for (AmiWebTreeGroupBy i : this.groupbyFormulas.values())
				i.getDependencies(deps);
			List<Object> deps2 = new ArrayList<Object>(deps);
			Map<Object[], List<Row>> old = mapRows(deps2, this.table);
			Map<Object[], List<Row>> nuw = mapRows(deps2, nuwTable);
			HasherSet<Object[]> both = new HasherSet<Object[]>(ArrayHasher.INSTANCE);
			both.addAll(old.keySet());
			both.addAll(nuw.keySet());
			for (Object[] key : both) {
				List<Row> oldRow = old.get(key);
				List<Row> nuwRow = nuw.get(key);
				if (oldRow == null) {
					toAdd.addAll(nuwRow);
				} else if (nuwRow == null) {
					toRem.addAll(oldRow);
				} else {
					int minSize = Math.min(oldRow.size(), nuwRow.size());
					for (int i = 0; i < minSize; i++) {
						AmiWebStaticTreeRow t = rowsToEntries.get(oldRow.get(i));
						if (t != null)
							t.updateRow(oldRow.get(i), nuwRow.get(i));
						else
							toAdd.add(nuwRow.get(i));
					}
					for (int i = minSize; i < oldRow.size(); i++)
						toRem.add(oldRow.get(i));
					for (int i = minSize; i < nuwRow.size(); i++)
						toAdd.add(nuwRow.get(i));
				}
			}
			this.table = nuwTable;
			for (Row row : toRem) {
				AmiWebStaticTreeRow t = rowsToEntries.get(row);
				if (t != null)
					t.removeRow(row);
			}
		} else if (this.table != null) {
			this.clearAmiData();
		} else if (nuwTable != null) {
			for (Row i : nuwTable.getRows())
				if (shouldKeep(i))
					toAdd.add(i);
			this.table = nuwTable;
		}

		if (table != null) {
			ReusableCalcFrameStack sf = getStackFrame();
			// only 3 4 will reach here
			WebTreeNode root = treeManager.getRoot();
			// Reset recursiveBuilders
			int formulasCount = this.getFormulasCount();
			this.recursiveBuilders = new ArrayList<AmiWebStaticTreePortlet.TreeRecursiveBuilder<Object, WebTreeNode>>(formulasCount);
			for (int i = 0; i < this.groupbyFormulas.getSize(); i++) {
				if (this.groupbyFormulas.getAt(i).getIsRecursive()) {
					this.recursiveBuilders.add(new TreeRecursiveBuilder<Object, WebTreeNode>());
				} else
					this.recursiveBuilders.add(null);
			}
			// End Reset recursiveBuilders
			root.setData(new AmiWebStaticTreeRow(root, this, false, false));
			resetCurrentNode();
			//Loop through all rows
			for (Row row : toAdd)
				addAmiObject(row, sf);
			// need to build filter from new data
			WebTreeFilter searchFilter = tree.getSearchFilter();
			WebTreeFilter treesFilter = tree.getTreesFilter();
			FastWebTreeColumnFilter columnsFilter = (FastWebTreeColumnFilter) tree.getColumnsFilter();
			tree.setFilter(searchFilter, treesFilter, columnsFilter);
			LinkedHashMap<Integer, Integer> toSort = tree.getToSort();
			for (Entry<Integer, Integer> e : toSort.entrySet()) {
				Integer mask = e.getValue();
				boolean ascend = (mask & FastWebTree.ASCEND) == FastWebTree.ASCEND;
				boolean keepSort = (mask & FastWebTree.KEEP_SORT) == FastWebTree.KEEP_SORT;
				boolean add = (mask & FastWebTree.ADD) == FastWebTree.ADD;
				// init -> store sort info -> load data -> sort -> load pref? -> sort again
				tree.sortRows(e.getKey(), ascend, keepSort, add);
			}
		}
		if (tsc != null) {
			tsc.reapplyState();
		}
	}
	private void buildRowsToEntitiesMapping(WebTreeNode root, IdentityHashMap<Row, AmiWebStaticTreeRow> rowsToEntries) {
		AmiWebStaticTreeRow data = getData(root);
		for (int i = 0; i < data.getRowCount(); i++)
			rowsToEntries.put(data.getRowAt(i), data);
		for (WebTreeNode i : root.getChildren())
			buildRowsToEntitiesMapping(i, rowsToEntries);
		for (WebTreeNode i : root.getFilteredChildren())
			buildRowsToEntitiesMapping(i, rowsToEntries);
	}

	private Map<Object[], List<Row>> mapRows(List<Object> deps, Table table2) {
		int keySize = deps.size();
		int pos[] = new int[keySize];
		for (int n = 0; n < keySize; n++) {
			Column col = table2.getColumn((String) deps.get(n));
			pos[n] = col == null ? -1 : col.getLocation();
		}
		LinkedHasherMap<Object[], List<Row>> r = new LinkedHasherMap<Object[], List<Row>>(ArrayHasher.INSTANCE);
		for (Row row : table2.getRows()) {
			if (!shouldKeep(row))
				continue;
			Object[] key = new Object[keySize];
			for (int i = 0; i < keySize; i++) {
				int p = pos[i];
				if (p != -1)
					key[i] = row.getAt(p);
			}
			LinkedHasherMap.Node<Object[], List<Row>> v = r.getOrCreateEntry(key);
			List<Row> l = v.getValue();
			if (l == null)
				v.setValue(l = new ArrayList<Row>(2));
			l.add(row);
		}
		return r;
	}

	private void addAmiObject(Row o, ReusableCalcFrameStack sf) {
		this.currentNode = null;
		WebTreeNode node = treeManager.getRoot();
		int n = this.groupbyFormulas.getSize();
		WebTreeNode lastFound = null;
		WebTreeNode top = null;

		Object lastKey = null;
		// Loop through all formulas
		sf.reset(o);
		Tuple2<Object, Object> tmpTuplekey = new Tuple2<Object, Object>();
		for (int i = 0; i < n; i++) {
			//			AmiWebTreeGroupBy gb = this.groupbyFormulas.get(i);
			AmiWebTreeGroupBy gb = this.groupbyFormulas.getAt(i);
			// If it's not a recursive groupby
			//Get the value for the groupby on this row and check if the node exists
			Object value = gb.isLeaf() ? o.getUid() : gb.getValue(sf);
			boolean isRecursive = gb.getIsRecursive();

			WebTreeNode node2 = null;
			// Find the node
			if (!isRecursive)
				node2 = node.getChildByKey(value);
			else {
				tmpTuplekey.setAB(OH.noNull(lastKey, NULL_KEY), OH.noNull(value, NULL_KEY));
				node2 = this.recursiveBuilders.get(i).getValueByKey(tmpTuplekey);
			}

			if (node2 == null) {
				Object recursiveParentKey = !isRecursive ? null : gb.getParentGroupCalc().get(sf.reset(o));
				// Create a new node set the group index with the previous node being the parent
				if (lastFound == null) {
					lastFound = node;
					top = node2 = createNode(i, value, node, null, isRecursive);
				} else
					node2 = createNode(i, value, node, node, isRecursive);

				if (isRecursive)
					this.recursiveBuilders.get(i).add(OH.noNull(value, NULL_KEY), OH.noNull(lastKey, NULL_KEY), OH.noNull(recursiveParentKey, NULL_KEY), node2);

			}
			lastKey = value;
			node = node2;
		}

		AmiWebStaticTreeRow data = getData(node);
		if (lastFound != null) {
			lastFound.addChild(top);
		}
		data.addRow(o);
		// Recursive groupings
		node = treeManager.getRoot();
		lastKey = null;
		tmpTuplekey.setAB(null, null);

		List<TreeRecursiveBuilder<Object, WebTreeNode>.Node> buildSink = new ArrayList<AmiWebStaticTreePortlet.TreeRecursiveBuilder<Object, WebTreeNode>.Node>();
		for (int i = 0; i < n; i++) {
			AmiWebTreeGroupBy gb = this.groupbyFormulas.getAt(i);
			sf.reset(o);
			Object value = gb.isLeaf() ? o.getUid() : gb.getValue(sf);
			boolean isRecursive = gb.getIsRecursive();
			WebTreeNode node2 = null;
			if (!isRecursive)
				node2 = node.getChildByKey(value);
			else {
				TreeRecursiveBuilder<Object, WebTreeNode> recursiveBuilder = this.recursiveBuilders.get(i);
				tmpTuplekey.setAB(OH.noNull(lastKey, NULL_KEY), OH.noNull(value, NULL_KEY));
				node2 = recursiveBuilder.getValueByKey(tmpTuplekey);

				buildSink.clear();

				// update nodes that have have this node as a parent or is this node
				recursiveBuilder.buildKeyByGrouping(tmpTuplekey, buildSink); // val - group
				recursiveBuilder.buildKeyByRecursiveParent(tmpTuplekey, buildSink); // recParent - group

				for (TreeRecursiveBuilder<Object, WebTreeNode>.Node rbn : buildSink) {
					WebTreeNode updateNode = rbn.value;
					tmpTuplekey.setB(rbn.recursiveParentKey);
					// Find the recursive parent if it exists
					WebTreeNode parentNode = recursiveBuilder.getValueByKey(tmpTuplekey);
					if (parentNode == null)
						continue;
					WebTreeNode oldParent = updateNode.getParent();
					if (oldParent != null && oldParent != parentNode && parentNode != updateNode) {
						boolean hasGroupAncestor = parentNode.hasGroupAncestor(updateNode.getKeyOrNull(), updateNode.getGroupRoot());
						if (!hasGroupAncestor) {
							oldParent.removeChild(updateNode);
							parentNode.addChild(updateNode);
							rbn.needsUpdate = false;
						}
					}

				}

			}

			lastKey = value;
			node = node2;
		}

	}

	private WebTreeNode createNode(int i, Object value, WebTreeNode groupingParent, WebTreeNode parent, boolean isRecursive) {
		//	private WebTreeNode createNode(int i, Object value, WebTreeNode parent) {
		boolean isRollup = i != this.getFormulasCount() - 1;
		WebTreeNode r = treeManager.buildNode(null, false, isRollup, null);
		r.setKey(value);
		r.setGroupIndex(i);
		r.setRecursive(isRecursive);
		if (isRecursive) {
			r.setGroupRoot(groupingParent);
		}
		r.setData(new AmiWebStaticTreeRow(r, this, !isRollup, isRecursive));
		//		r.setData(new AmiWebStaticTreeRow(r, this, !isRollup, false));
		r.setHasCheckbox(true);
		r.setCssClass("pointer");
		if (parent != null)
			parent.addChild(r);
		return r;
	}
	private AmiWebStaticTreeRow getData(WebTreeNode i) {
		return (AmiWebStaticTreeRow) i.getData();
	}
	protected void clearRowCaches(WebTreeNode node) {
		AmiWebStaticTreeRow data = getData(node);
		data.onTreeCalcsChanged();
		for (WebTreeNode i : node.getChildrenAndFiltered())
			clearRowCaches(i);
	}
	//	private boolean clearEmptyCells(WebTreeNode node) {
	//		AmiWebStaticTreeRow data = getData(node);
	//		if (data.isEmpty() && node != treeManager.getRoot())
	//			return true;
	//		List<WebTreeNode> toRemove = null;
	//		for (WebTreeNode i : node.getChildren()) {
	//			if (clearEmptyCells(i)) {
	//				if (toRemove == null)
	//					toRemove = new ArrayList<WebTreeNode>(4);
	//				toRemove.add(i);
	//			}
	//		}
	//		if (toRemove != null)
	//			for (WebTreeNode i : toRemove)
	//				node.removeChild(i);
	//		return false;
	//	}

	//	private void clearGroup(WebTreeNode node) {
	//		node.clearGroupChildren();
	//		node.setGroupIndex(WebTreeNode.DEFAULT_GROUP_INDEX);
	//		for (WebTreeNode i : node.getChildren())
	//			clearGroup(i);
	//	}
	//	private void walkCurrentNodeChildren(WebTreeNode node, int groupIndex) {
	//		for (WebTreeNode child : node.getChildren()) {
	//			int childGroupIndex = child.getGroupIndex();
	//			if (childGroupIndex != groupIndex)
	//				continue;
	//			AmiWebStaticTreeRow rows = getData(child);
	//			if (rows == null || rows.size() == 0)
	//				continue;
	//			rows.getRows(this.tmpRows);
	//			walkCurrentNodeChildren(child, groupIndex);
	//		}
	//	}

	@Override
	public Table getSelectableRows(AmiWebDmLink link, byte type) {
		if (this.table == null)
			return null;
		switch (type) {
			case NONE:
				return new BasicTable(table.getColumns());
			case ALL: {
				Table t = new BasicTable(table.getColumns());
				List<Row> selected = new ArrayList<Row>();
				getData(treeManager.getRoot()).getRows(selected, null);
				for (Row n : selected)
					t.getRows().addRow(n.getValuesCloned());
				return t;
			}
			case SELECTED: {
				List<WebTreeNode> rawSelected = tree.getSelected();
				List<Row> selected = new ArrayList<Row>(rawSelected.size());
				IntSet s = new IntSet();
				for (WebTreeNode i : rawSelected)
					if (i.getData() != null)
						getData(i).getRows(selected, s);
				Table t = new BasicTable(table.getColumns());
				for (Row n : selected)
					t.getRows().addRow(n.getValuesCloned());
				return t;
			}
		}
		return null;
	}
	private Table mapFromVars(Table in) {
		if (in == null)
			return null;
		boolean needsMapping = false;
		com.f1.base.CalcTypes fromTypes = in.getColumnTypesMapping();
		com.f1.base.CalcTypes toTypes = this.getUserDefinedVariables();
		for (String e : toTypes.getVarKeys()) {
			Class<?> to = toTypes.getType(e);
			Class<?> from = fromTypes.getType(e);
			if (to != from) {
				needsMapping = true;
				break;
			}
		}
		if (!needsMapping)
			return in;
		int mappingsCount = 0;
		CalcTypes vars = AmiWebUtils.getAvailableVariables(this.getService(), this);
		int colsCount = vars.getVarsCount();
		final int[] srcPos = new int[colsCount];
		final int[] tgtPos = new int[colsCount];
		final String[] outNames = new String[colsCount];
		final Class<?>[] outTypes = new Class[colsCount];
		final Caster[] casters = new Caster[colsCount];
		int pos = 0;
		for (String i : vars.getVarKeys()) {
			Class<?> type = vars.getType(i);
			outNames[pos] = i;
			outTypes[pos] = type;
			Column src = in.getColumnsMap().get(i);
			if (src != null) {
				tgtPos[mappingsCount] = pos;
				srcPos[mappingsCount] = src.getLocation();
				casters[mappingsCount] = CasterManager.getCaster(type);
				mappingsCount++;
			}
			pos++;
		}
		Table out = new ColumnarTable(outTypes, outNames);
		TableList outRows = out.getRows();
		for (Row row : in.getRows()) {
			Row row2 = out.newEmptyRow();
			for (int i = 0; i < mappingsCount; i++)
				row2.putAt(tgtPos[i], casters[i].cast(row.getAt(srcPos[i])));
			outRows.add(row2);
		}

		return out;
	}

	@Override
	public com.f1.base.CalcTypes getClassTypes() {
		AmiWebDmTableSchema dm = this.getDm();
		return dm == null ? EmptyCalcTypes.INSTANCE : dm.getClassTypes();
	}
	@Override
	public boolean hasVisiblePortletForDm(AmiWebDm datamodel) {
		return getVisible();
	}
	@Override
	public Set<String> getUsedDmVariables(String aliasDotName, String dmTable, Set<String> r) {
		if (this.dmSingleton.matches(aliasDotName, dmTable)) {
			this.getTreePortletFormatter().getDependencies((Set) r);
			int fc = this.getFormulasCount();
			for (int i = 0; i < fc; i++) {
				AmiWebTreeGroupBy formula = this.getFormula(i);
				formula.getDependencies((Set) r);
			}
			for (AmiWebTreeColumn i : this.columnsByAmiId.values())
				i.getFormatter().getDependencies((Set) r);
		}
		return r;
	}
	@Override
	public void onDmRunningQuery(AmiWebDm datamodel, boolean isRequery) {
		if (!isRequery) {
			showWaitingSplash(true);
			if (this.clearOnDataStale)
				clearAmiData();
		}
	}

	@Override
	public void onDmError(AmiWebDm datamodel, AmiWebDmError error) {
		clearAmiData();
		showWaitingSplash(false);
	}

	@Override
	public void onDmDataBeforeFilterChanged(AmiWebDm datamodel) {
	}

	public void addUsedDatamodel(String dmAliasDotName, String value) {
		this.dmSingleton.addUsedDatamodel(dmAliasDotName, value);

	}
	final public void setUsedDatamodel(String dmName, String tableName) {
		this.dmSingleton.setUsedDm(dmName, tableName);
		for (AmiWebDmLink i : getDmLinksFromThisPortlet())
			i.setSourceDm(dmName, tableName);
		for (AmiWebDmLink i : getDmLinksToThisPortlet())
			i.setTargetDm(dmName);
	}

	@Override
	public Set<String> getUsedDmTables(String aliasDotNames) {
		return this.dmSingleton.getUsedDmTables(aliasDotNames);
	};

	@Override
	public Set<String> getUsedDmAliasDotNames() {
		return this.dmSingleton.getUsedDmAliasDotNames();
	}
	@Override
	public void onDmNameChanged(String oldAliasDotName, AmiWebDm dm) {
		this.dmSingleton.onDmNameChanged(oldAliasDotName, dm);
	};
	public boolean isClearOnDataStale() {
		return clearOnDataStale;
	}
	public void setClearOnDataStale(boolean clearOnDataStale) {
		this.clearOnDataStale = clearOnDataStale;
	}
	@Override
	public boolean hasSelectedRows(AmiWebDmLink link) {
		List<WebTreeNode> rawSelected = tree.getSelected();
		for (WebTreeNode i : rawSelected)
			if (i.getData() != null)
				if (getData(i).size() > 0)
					return true;
		return false;
	}

	@Override
	public void onFilteredChanged(WebTreeNode child, boolean isFiltered) {
		super.onFilteredChanged(child, isFiltered);
		for (WebTreeNode parent = child.getParent(); parent != null; parent = parent.getParent()) {
			AmiWebStaticTreeRow d = getData(parent);
			if (!d.isCached())
				break;
			d.clearCache();
		}

	}

	@Override
	public void onWhereFormulaChanged() {
		this.clearAmiData();
		//		AmiWebDm dm = getService().getDmManager().getDmByAliasDotName(this.dmSingleton.getDmAliasDotName());
		//		if (dm != null)
		//dm doesn't get used
		onDmDataChanged(null);
	}

	private boolean containsRecursive() {
		// Ideally we should replace this with a property
		boolean containsRecursive = false;
		for (int i = 0; i < this.groupbyFormulas.getSize(); i++) {
			if (this.groupbyFormulas.getAt(i).getIsRecursive()) {
				containsRecursive = true;
				break;
			}
		}
		return containsRecursive;
	}

	@Override
	public Object getId(WebTreeNode node) {
		int depth = node.getDepth();
		if (depth == 0)
			return null;
		if (containsRecursive())
			return this.groupbyFormulas.getAt(0).getFormatter().getValue(node);
		return this.groupbyFormulas.getAt(depth - 1).getFormatter().getValue(node);
		//		AmiWebStaticTreeRow row = (AmiWebStaticTreeRow) node.getData();
		//		int n = this.groupbyFormulas.getSize();
		//		List<Object> key = new ArrayList<Object>(n);
		//		for (int i = 0; i < n; i++) {
		//			AmiWebTreeGroupBy gb = this.groupbyFormulas.getAt(i);
		//			if (row.getFirstRow() == null)
		//				continue;
		//			Object o = gb.getFormatter().getValue(node);
		//			key.add(o);
		//		}
		//		return key;
	}

	public class TreeRecursiveBuilder<K, V> {
		private BasicMultiMap.List<Tuple2<K, K>, Node> entriesByKey = new BasicMultiMap.List<Tuple2<K, K>, Node>();
		private BasicMultiMap.List<Tuple2<K, K>, Node> entriesByRecursiveParent = new BasicMultiMap.List<Tuple2<K, K>, Node>();

		public class Node {
			final K key;
			final K groupingParentKey;
			final K recursiveParentKey;
			final V value;
			List<Node> children = null;
			Node parent;
			protected boolean needsUpdate;

			public Node(K key, K groupingParentKey, K recursiveParentKey, V value, boolean needsUpdate) {
				this.key = key;
				this.groupingParentKey = groupingParentKey;
				this.recursiveParentKey = recursiveParentKey;
				this.value = value;
				this.needsUpdate = true;

			}
			public void setChildren(List<TreeRecursiveBuilder<K, V>.Node> list) {
				this.children = list;
				if (this.children != null)
					for (Node c : children)
						c.parent = this;
			}
		}

		public void add(K key, K groupingParent, K recursiveParent, V value) {
			Tuple2<K, K> groupingToKeyTuple = new Tuple2<K, K>(groupingParent, key);
			Tuple2<K, K> groupingToRecursiveParentTuple = new Tuple2<K, K>(groupingParent, recursiveParent);
			Node val = new Node(key, groupingParent, recursiveParent, value, true);
			entriesByKey.putMulti(groupingToKeyTuple, val);
			entriesByRecursiveParent.putMulti(groupingToRecursiveParentTuple, val);
		}

		public V getValueByKey(Tuple2<K, K> key) {
			TreeRecursiveBuilder<K, V>.Node node = entriesByKey.getMulti(key);
			return node == null ? null : node.value;
		}

		public List<Node> buildKeyByGrouping(Tuple2<K, K> key, List<Node> sink) {
			if (!entriesByKey.containsKey(key))
				return sink;
			List<TreeRecursiveBuilder<K, V>.Node> allNodesWithKey = entriesByKey.get(key);
			for (TreeRecursiveBuilder<K, V>.Node node : allNodesWithKey) {
				if (node.needsUpdate)
					sink.add(node);

			}

			return sink;
		}
		public List<Node> buildKeyByRecursiveParent(Tuple2<K, K> key, List<Node> sink) {
			if (!entriesByRecursiveParent.containsKey(key))
				return sink;
			List<TreeRecursiveBuilder<K, V>.Node> allNodesWithKey = entriesByRecursiveParent.get(key);
			for (TreeRecursiveBuilder<K, V>.Node node : allNodesWithKey) {
				if (node.needsUpdate)
					sink.add(node);
			}

			return sink;
		}

	}

	@Override
	public AmiWebPanelSettingsPortlet showSettingsPortlet() {
		return new AmiWebStaticTreeEditSettingsPortlet(generateConfig(), this);
	}

	@Override
	public void onAmiInitDone() {
		super.onAmiInitDone();
	}

}
