package com.f1.ami.web.realtimetree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.web.AmiWebAbstractPortletBuilder;
import com.f1.ami.web.AmiWebDomObject;
import com.f1.ami.web.AmiWebManagers;
import com.f1.ami.web.AmiWebObject;
import com.f1.ami.web.AmiWebObjectFields;
import com.f1.ami.web.AmiWebOverrideValue;
import com.f1.ami.web.AmiWebPanelSettingsPortlet;
import com.f1.ami.web.AmiWebRealtimeObjectListener;
import com.f1.ami.web.AmiWebRealtimeObjectManager;
import com.f1.ami.web.AmiWebRealtimePortlet;
import com.f1.ami.web.AmiWebRealtimeTableCustomFilter;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.ami.web.tree.AmiWebTreeColumn;
import com.f1.ami.web.tree.AmiWebTreeGroupBy;
import com.f1.ami.web.tree.AmiWebTreePortlet;
import com.f1.ami.web.tree.AmiWebTreeRecursiveBuilder;
import com.f1.base.CalcFrame;
import com.f1.base.IterableAndSize;
import com.f1.base.Table;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.BasicPortletManager;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.impl.FastWebTree;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Simple;
import com.f1.utils.concurrent.HasherSet;
import com.f1.utils.concurrent.IdentityHashSet;
import com.f1.utils.impl.FastArrayList;
import com.f1.utils.impl.IdentityHasher;
import com.f1.utils.sql.aggs.AggCalculator;
import com.f1.utils.sql.aggs.AggDeltaCalculator;
import com.f1.utils.structs.IntSet;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.stack.BasicCalcFrame;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class AmiWebRealtimeTreePortlet extends AmiWebTreePortlet implements AmiWebRealtimePortlet {

	private static final String DIALOG_BTN_RESET_TREE = "RESET_TREE";
	private static final String DIALOG_ON_ERROR = "ON_ERROR";
	private static final Logger log = LH.get();
	private AggCalculator[] allAggregates = new AggCalculator[0];
	private AggDeltaCalculator[] deltaAggregates = new AggDeltaCalculator[0];
	private AggCalculator[] snapshotAggregates = new AggCalculator[0];
	private AmiWebRealtimeTableCustomFilter filter;
	private boolean hasError = false;

	public static class Builder extends AmiWebAbstractPortletBuilder<AmiWebRealtimeTreePortlet> {
		public static final String ID = "amirealtimetree";

		public Builder() {
			super(AmiWebRealtimeTreePortlet.class);
		}
		@Override
		public AmiWebRealtimeTreePortlet buildPortlet(PortletConfig portletConfig) {
			AmiWebRealtimeTreePortlet r = new AmiWebRealtimeTreePortlet(portletConfig);
			return r;
		}
		@Override
		public String getPortletBuilderName() {
			return "Realtime Tree";
		}
		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	public AmiWebRealtimeTreePortlet(PortletConfig config) {
		super(config);
		this.treeManager.getRoot().setData(new AmiWebRealtimeTreeRow(this.treeManager.getRoot(), this, false, false));
	}

	@Override
	public void clearAmiData() {
		super.clearAmiData();
		this.leafs.clear();
		this.amiObjects.clear();
		this.treeManager.getRoot().setData(new AmiWebRealtimeTreeRow(this.treeManager.getRoot(), this, false, false));
		if (this.tmpUpdateObj != null)
			this.tmpUpdateObj.clear();
		if (this.recursiveBuilders != null)
			this.recursiveBuilders.clear();
		this.hasError = false;
	}

	@Override
	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = super.getConfiguration();
		HashSet<String> relative = new HashSet<String>();
		for (String s : this.getLowerRealtimeIds()) {
			s = AmiWebUtils.getRelativeRealtimeId(this.getAmiLayoutFullAlias(), s);
			relative.add(s);
		}
		r.put("rtSources", relative);
		return r;
	}

	@Override
	public void init(Map<String, Object> configuration, Map<String, String> origToNewIdMapping, StringBuilder sb) {
		Collection<String> types2 = (Collection) CH.getOr(Caster_Simple.OBJECT, configuration, "rtSources", null);
		HashSet<String> adn = new HashSet<String>();
		if (CH.isntEmpty(types2))
			for (String s : types2)
				adn.add(AmiWebUtils.getFullRealtimeId(this.getAmiLayoutFullAlias(), s));
		setDataTypes(adn);
		super.init(configuration, origToNewIdMapping, sb);
	}
	@Override
	public String getConfigMenuTitle() {
		return "Realtime Tree";
	}

	@Override
	public boolean isRealtime() {
		return true;
	}

	@Override
	protected void rebuildCalcs() {
		super.rebuildCalcs();
		this.allAggregates = AH.toArray(this.aggregateFactory.getAggregates(), AggCalculator.class);
		int deltas = 0;
		for (AggCalculator i : this.allAggregates)
			if (i instanceof AggDeltaCalculator)
				deltas++;
		this.deltaAggregates = new AggDeltaCalculator[deltas];
		this.snapshotAggregates = new AggCalculator[this.allAggregates.length - deltas];
		int di = 0, si = 0;
		for (AggCalculator i : this.allAggregates)
			if (i instanceof AggDeltaCalculator)
				this.deltaAggregates[di++] = (AggDeltaCalculator) i;
			else
				this.snapshotAggregates[si++] = i;
		getData(this.treeManager.getRoot()).onTreeCalcsChanged(this.allAggregates.length, this.deltaAggregates.length);
		this.currentNode = null;
	}

	@Override
	protected CalcFrame onCurrentNodeChanged(WebTreeNode node) {
		AmiWebRealtimeTreeRow rows = getData(node);
		this.tmpRows.clear();
		ReusableCalcFrameStack sf = getStackFrame();
		boolean gottenRows = false;
		for (AggCalculator i : this.allAggregates)
			i.reset();
		for (int n = 0; n < this.deltaAggregates.length; n++) {
			AggDeltaCalculator agg = this.deltaAggregates[n];
			if (rows.isDeltaAggCached(n)) {
				agg.setValue(rows.getCache(n));
			} else {
				agg.reset();
				if (!gottenRows) {
					rows.getRows(this.tmpRows, null);
					gottenRows = true;
				}
				agg.visitRows(sf, this.tmpRows);
				rows.setCache(n, agg.get(null));
				rows.setIsDeltaAggCached(n, true);
			}
		}
		if (rows.isSnapshotCached()) {
			for (int n = this.deltaAggregates.length; n < this.allAggregates.length; n++) {
				AggCalculator agg = this.allAggregates[n];
				agg.setValue(rows.getCache(n));
			}

		} else {
			for (int n = this.deltaAggregates.length; n < this.allAggregates.length; n++) {
				AggCalculator agg = this.allAggregates[n];
				agg.reset();
				if (!gottenRows) {
					rows.getRows(this.tmpRows, null);
					gottenRows = true;
				}
				agg.visitRows(sf, this.tmpRows);
				rows.setCache(n, agg.get(null));
			}
			rows.setIsSnapshotCached(true);

		}
		if (tmpRows.size() == 0) {
			AmiWebObject t = rows.getFirstRow();
			if (t != null) {
				return t;
			} else
				return emptyCalcFrame();
		} else
			return this.tmpRows.get(0);
	}

	@Override
	public Table getSelectableRows(AmiWebDmLink link, byte type) {
		switch (type) {
			case NONE:
				return AmiWebUtils.toTable(this.getClassTypes(), Collections.EMPTY_LIST);
			case ALL: {
				List<AmiWebObject> selected = new ArrayList<AmiWebObject>();
				getData(treeManager.getRoot()).getRows(selected, null);
				return AmiWebUtils.toTable(this.getClassTypes(), selected);
			}
			case SELECTED: {
				List<WebTreeNode> rawSelected = tree.getSelected();
				IntSet s = new IntSet();
				List<AmiWebObject> selected = new ArrayList<AmiWebObject>(rawSelected.size());
				for (WebTreeNode i : rawSelected) {
					getData(i).getRows(selected, s);
				}
				return AmiWebUtils.toTable(this.getClassTypes(), selected);
			}
		}
		return null;
	}

	@Override
	public boolean hasSelectedRows(AmiWebDmLink link) {
		List<WebTreeNode> rawSelected = tree.getSelected();
		for (WebTreeNode i : rawSelected) {
			AmiWebRealtimeTreeRow data = getData(i);
			if (data.leafCount() > 0)
				return true;
		}
		return false;
	}

	private List<AmiWebObject> tmpRows = new ArrayList<AmiWebObject>(4);

	public com.f1.base.CalcTypes getClassTypes() {
		return AmiWebUtils.getAvailableVariables(getService(), this);
	}

	@Override
	public com.f1.base.CalcTypes getLinkableVars() {
		return this.getClassTypes();
	}

	@Override
	public void processFilter(AmiWebDmLink link, Table table) {
		if (table == null) {
			this.filter = null;
		} else if (table.getSize() == 0) {
			this.filter = AmiWebRealtimeTableCustomFilter.HIDE_ALL;
		} else {
			this.filter = new AmiWebRealtimeTableCustomFilter(getService(), this.getAmiLayoutFullAlias());
			filter.reset(link, table, this.getClassTypes(), null);
		}
		clearAmiData();
		rebuildAmiData();
	}

	@Override
	public void onAmiEntitiesReset(AmiWebRealtimeObjectManager manager) {
		try {
			clearAmiData();
			rebuildAmiData();
		} catch (Exception e) {
			handleError("Error clearing Ami Entities", null, e);
		}
	}
	private AmiWebRealtimeTreeObject addLeaf(AmiWebObject entity) {
		if (amiObjects.containsKey(entity.getUniqueId()))
			throw new RuntimeException("invalid state");
		AmiWebRealtimeTreeObject treeObject = new AmiWebRealtimeTreeObject(entity, this.deltaAggregates.length);
		this.amiObjects.put(entity.getUniqueId(), treeObject);
		return treeObject;
	}
	private AmiWebRealtimeTreeObject removeLeaf(AmiWebObject entity) {
		if (!amiObjects.containsKey(entity.getUniqueId()))
			throw new RuntimeException("invalid state");
		return this.amiObjects.remove(entity.getUniqueId());
	}
	private AmiWebRealtimeTreeObject getLeaf(AmiWebObject entity) {
		if (!amiObjects.containsKey(entity.getUniqueId()))
			throw new RuntimeException("invalid state");
		return this.amiObjects.get(entity.getUniqueId());
	}

	public void handleError(String text, Object obj, Exception e) {
		LH.warning(log, obj != null ? text + obj : text, e);
		if (this.hasError == false) {
			this.hasError = true;
			BasicPortletManager pm = (BasicPortletManager) this.getManager();
			ConfirmDialogPortlet createAlertDialog = pm.createAlertDialog();
			createAlertDialog.setCallback(DIALOG_ON_ERROR);
			createAlertDialog.addButton(DIALOG_BTN_RESET_TREE, "Reset");
			createAlertDialog.addDialogListener(this);
			pm.showAlert(text, e, createAlertDialog);
		}
	}

	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		String callback = source.getCallback();
		if (DIALOG_ON_ERROR.equals(callback)) {
			if (DIALOG_BTN_RESET_TREE.equals(id)) {
				this.clearAmiData();
				this.rebuildAmiData();
			}
			return true;
		}
		return super.onButton(source, id);
	}

	@Override
	public void onAmiEntityAdded(AmiWebRealtimeObjectManager manager, AmiWebObject o) {
		try {
			if (shouldKeep(o))
				addAmiObject3(o);
		} catch (Exception e) {
			handleError("Error adding Ami Entity: " + o, null, e);
		}
	}

	@Override
	public void onAmiEntityRemoved(AmiWebRealtimeObjectManager manager, AmiWebObject o) {
		//		System.out.println("Removing " + o);
		try {
			if (shouldKeep(o))
				removeAmiObject3(o);
		} catch (Exception e) {
			handleError("Error removing Ami Entity: " + o, null, e);
		}
	}

	@Override
	public void onAmiEntityUpdated(AmiWebRealtimeObjectManager manager, AmiWebObjectFields fields, AmiWebObject o) {
		//		System.out.println("Updating " + o);
		try {
			boolean shouldKeepNew = shouldKeep(o);
			if (!shouldKeepNew) {
				onAmiEntityRemoved(manager, o);
				return;
			} else if (!this.amiObjects.containsKey(o.getUniqueId())) {
				onAmiEntityAdded(manager, o);
				return;
			}
			updateAmiObject3(fields, o);
		} catch (Exception e) {
			handleError("Error updating Ami Entity: " + o, null, e);
		}
	}

	private final Tuple2<Object, Object> tmpTupleFinder = new Tuple2<Object, Object>();

	private Object getValueForEntityForGroupby(CalcFrame entity, AmiWebTreeGroupBy gb, long uniqueId, ReusableCalcFrameStack sf) {
		return gb.isLeaf() ? uniqueId : gb.getValue(sf.reset(entity));
	}
	// Used for onEntityAdded
	private WebTreeNode findNodeForEntityAndGroupbyHelper(Object valueForGroupbyIndex, WebTreeNode groupbyRootNode, int groupbyIndex, boolean isRecursive) {
		if (groupbyRootNode == null)
			return null;

		WebTreeNode findNode = null;
		if (!isRecursive) {
			findNode = groupbyRootNode.getChildByKey(valueForGroupbyIndex);
		} else {
			tmpTupleFinder.setAB(OH.noNull(groupbyRootNode.getUid(), NULL_KEY), OH.noNull(valueForGroupbyIndex, NULL_KEY));
			findNode = this.recursiveBuilders.get(groupbyIndex).getValueByKey2(tmpTupleFinder);
			//			tmpTupleFinder.clear();
		}
		return findNode;
	}

	private void addAmiObject3(AmiWebObject entity) {
		// 1) Keep Track of leafs 1 to 1 entity to object
		// 2) Add the node to the tree 
		// 3) Add the object to the node
		// 4) Add
		AmiWebRealtimeTreeObject treeObj = this.addLeaf(entity);
		WebTreeNode leafNode = this.addAmiObjectTreeNode2(entity, entity.getUniqueId(), treeObj);

		// Moved this to the createNode because order of operations matter for filtering
		AmiWebRealtimeTreeRow leafTreeRow = ((AmiWebRealtimeTreeRow) leafNode.getData());
		leafTreeRow.addRow(treeObj, deltaAggregates, getStackFrame()); //TODO: this is for filters and aggregation
		this.getBasicWebTreeManager().checkFilter(leafNode);
		LinkedHashMap<Integer, Integer> toSort = this.tree.getToSort();
		for (Entry<Integer, Integer> e : toSort.entrySet()) {
			Integer mask = e.getValue();
			boolean ascend = (mask & FastWebTree.ASCEND) == FastWebTree.ASCEND;
			boolean keepSort = (mask & FastWebTree.KEEP_SORT) == FastWebTree.KEEP_SORT;
			boolean add = (mask & FastWebTree.ADD) == FastWebTree.ADD;
			// init -> store sort info -> load data -> sort -> load pref? -> sort again
			tree.sortRows(e.getKey(), ascend, keepSort, add);
		}
		// Walk up and update style as well as check filter
		this.updateNodeAndParents(leafNode);

		handleDownstreamForAdd(entity, leafNode);
	}

	private List<AmiWebTreeRecursiveBuilder<Object, WebTreeNode>> recursiveBuilders;
	private static final Object NULL_KEY = AmiWebTreeRecursiveBuilder.NULL_KEY;

	public WebTreeNode addAmiObjectTreeNode2(AmiWebObject o, long uniqueId, AmiWebRealtimeTreeObject treeObj) {
		this.currentNode = null;
		WebTreeNode node = treeManager.getRoot();
		int n = this.groupbyFormulas.getSize();
		WebTreeNode lastFound = null;
		WebTreeNode top = null;

		ReusableCalcFrameStack sf = getStackFrame();
		sf.reset(o);
		// Loop through all formulas
		for (int i = 0; i < n; i++) {
			AmiWebTreeGroupBy gb = this.groupbyFormulas.getAt(i);
			// If it's not a recursive groupby
			// Get the value for the groupby on this row and check if the node exists
			Object value = getValueForEntityForGroupby(o, gb, uniqueId, sf);
			boolean isRecursive = gb.getIsRecursive();

			WebTreeNode node2 = findNodeForEntityAndGroupbyHelper(value, node, i, isRecursive);

			if (node2 == null) {
				Object recursiveParentKey = !isRecursive ? null : gb.getParentGroupCalc().get(sf);
				// Create a new node set the group index with the previous node being the parent
				if (lastFound == null) {
					lastFound = node;
					top = node2 = createNode(i, value, node, null, isRecursive);
				} else
					node2 = createNode(i, value, node, node, isRecursive);

				if (isRecursive) {
					int uid = node.getUid();

					this.recursiveBuilders.get(i).add2(uid, value, recursiveParentKey, node2); // rCP should be renamed to grouping Parent Key
				}

			}
			node = node2;
		}

		WebTreeNode retLeafNode = node;
		if (lastFound != null) {
			lastFound.addChild(top, false);
		}

		onAddUpdateRecursiveNodes(o, uniqueId);

		this.tmpTupleFinder.clear();
		return retLeafNode;
	}
	public void onAddUpdateRecursiveNodes(AmiWebObject o, long uniqueId) {
		WebTreeNode node = treeManager.getRoot();
		int n = this.groupbyFormulas.getSize();
		Tuple2<Object, Object> tmpTuplekey2 = new Tuple2<Object, Object>();
		List<AmiWebTreeRecursiveBuilder<Object, WebTreeNode>.Node> buildSink2 = new ArrayList<AmiWebTreeRecursiveBuilder<Object, WebTreeNode>.Node>();
		ReusableCalcFrameStack sf = getStackFrame();
		for (int i = 0; i < n; i++) {
			AmiWebTreeGroupBy gb = this.groupbyFormulas.getAt(i);
			Object value = getValueForEntityForGroupby(o, gb, uniqueId, sf);
			boolean isRecursive = gb.getIsRecursive();
			WebTreeNode node2 = findNodeForEntityAndGroupbyHelper(value, node, i, isRecursive);
			if (isRecursive) {
				AmiWebTreeRecursiveBuilder<Object, WebTreeNode> recursiveBuilder = this.recursiveBuilders.get(i);
				buildSink2.clear();

				recursiveBuilder.buildKeyByGroupingParent(tmpTuplekey2, buildSink2); // val - group
				recursiveBuilder.buildKeyByNodeKey(tmpTuplekey2, buildSink2); // recParent - group

				for (AmiWebTreeRecursiveBuilder<Object, WebTreeNode>.Node rbn : buildSink2) {
					if (!rbn.needsUpdate())
						continue;
					WebTreeNode updateNode = rbn.value;
					tmpTuplekey2.setB(rbn.groupingParentKey);
					// Find the recursive parent if it exists
					WebTreeNode parentNode = recursiveBuilder.getValueByKey2(tmpTuplekey2);
					if (parentNode == null)
						continue;
					WebTreeNode oldParent = updateNode.getParent();
					if (oldParent != null && oldParent != parentNode && parentNode != updateNode) {
						boolean hasGroupAncestor = parentNode.hasGroupAncestor(updateNode.getKeyOrNull(), updateNode.getGroupRoot());
						if (!hasGroupAncestor) {
							oldParent.removeChild(updateNode);
							parentNode.addChild(updateNode);
							rbn.setNeedsUpdate(false);
						}
					}

				}

			}

			node = node2;
		}
	}

	private void removeAmiObject3(AmiWebObject o) {
		// 1) Remove the leaf 
		// 2) Remove the obj from the node
		// 2a) Check filter before removal
		// 3) Remove the node;
		AmiWebRealtimeTreeObject treeObj = this.removeLeaf(o);

		AmiWebRealtimeTreeRow leafRow = treeObj.getRow();

		leafRow.removeRow(treeObj, this.deltaAggregates);

		WebTreeNode leafNode = leafRow.getNode();
		WebTreeNode parent = leafNode.getParent();

		this.getBasicWebTreeManager().assertEmptyNodesToCheckFilter();
		while (leafNode != null && leafNode != this.treeManager.getRoot()) {
			this.getBasicWebTreeManager().addNodeToCheckFilter(leafNode);
			leafNode = leafNode.getParent();
		}

		WebTreeNode node = this.onRemoveTreeNodeHelperEmptyRow(leafRow, o); //R

		this.getBasicWebTreeManager().checkNodesToCheckFilter();

		// Walk up and update style as well as check filter
		this.updateNodeAndParents(parent);

		this.handleDownstreamForRemove(o, node);
	}

	// No children or filtered
	private boolean isNodeEmpty(WebTreeNode node) {
		return node.getChildrenAndFilteredCount() == 0;
	}
	// Either has no children or all nodes are filtered
	private boolean isNodeAllFiltered(WebTreeNode node) {
		return node.getChildrenCount() == 0;
	}
	// No filtered
	private boolean isNodeFilteredEmpty(WebTreeNode node) {
		return node.getFilteredChildrenCount() == 0;
	}

	private WebTreeNode onRemoveTreeNodeHelperEmptyRow(AmiWebRealtimeTreeRow row, CalcFrame obj) {
		WebTreeNode node = row.getNode();
		if (row.isEmpty()) {
			//If has leaf grouping this will happen first;
			this.onRemoveTreeNode(node, obj);
		}
		return node;
	}

	private void onRemoveTreeNode(WebTreeNode node, CalcFrame obj) {
		WebTreeNode parent = node.getParent();
		if (parent == null) // Simple check for null parent, maybe checking for root node is better
			return;

		ReusableCalcFrameStack sf = getStackFrame();
		sf.reset(obj);
		AmiWebRealtimeTreeRow row = getData(node);
		if (row.isEmpty()) {
			boolean isRecursive = node.isRecursive();
			if (!isRecursive) {
				if (isNodeEmpty(node)) {
					// If it has no children or filtered children hide the node
					// Remove and call onRemove on parent
					parent.removeChild(node);
					this.onRemoveTreeNode(parent, obj);
				} else if (isNodeAllFiltered(node)) {
					// If all children are filtered hide the node
					parent.setChildFilterered(node, true);
					this.onRemoveTreeNode(parent, obj);
				}
			} else {
				// TODO: count leaf nodes == 0 how do we do that? // Do I still need to take into account no data?
				int i = node.getGroupIndex();
				AmiWebTreeGroupBy gb = this.groupbyFormulas.getAt(i);
				Object parentKey = gb.getParentGroupCalc().get(sf);
				WebTreeNode groupingOrParent = node.getGroupRoot();

				// Remove all children add it to the group root
				List<WebTreeNode> l = CH.l(node.getChildren());
				for (int j = 0; j < l.size(); j++) {
					WebTreeNode child = l.get(j);
					if (isNodeEmpty(child)) {
						node.removeChild(child);
						groupingOrParent.addChild(child);
					} else if (isNodeAllFiltered(child)) {
						// If all children are filtered hide the node
						node.setChildFilterered(child, true);
					}
				}
				if (isNodeEmpty(parent)) {
					parent.removeChild(node);
				} else if (isNodeAllFiltered(parent)) {
					parent.setChildFilterered(node, true);
				}
				// Remove the builder and call onRemove on groupRoot
				AmiWebTreeRecursiveBuilder<Object, WebTreeNode> amiWebTreeRecursiveBuilder = this.recursiveBuilders.get(i);
				amiWebTreeRecursiveBuilder.remove2(groupingOrParent.getUid(), node.getKey(), parentKey);
				this.onRemoveTreeNode(groupingOrParent, obj);
			}
		}
	}

	private BasicCalcFrame tmpUpdateObj;

	private void updateAmiObject3(AmiWebObjectFields fields, AmiWebObject o) {
		tmpUpdateObj.clear();
		o.fill(tmpUpdateObj);
		for (int i = 0; i < fields.getChangesCount(); i++) {
			String changeField = fields.getChangeField(i);
			Object oldValue = fields.getOldValue(i);

			tmpUpdateObj.putValue(changeField, oldValue);
		}

		// A. Remove
		// 1) Get object and row // keep the row
		// 1a) Check Filter before removal
		// 2) Call remove on row because the data may have changed the keys
		// 3) Remove the node;
		// B. Add
		// 4) Add the node to the tree
		// 5) Add the object to the node
		// 5a) Update filter after

		AmiWebRealtimeTreeObject treeObj = this.getLeaf(o);
		AmiWebRealtimeTreeRow row = treeObj.getRow();

		row.removeRow(treeObj, this.deltaAggregates);

		this.getBasicWebTreeManager().assertEmptyNodesToCheckFilter();

		//New:
		/*
		 * 1) loop through all groupby's from 0 to n
		 * 2) Get the node for the old values,
		 * 
		 */

		WebTreeNode newNode = treeManager.getRoot();
		WebTreeNode oldNode = newNode;
		int n = this.groupbyFormulas.getSize();
		long uniqueId = o.getUniqueId();
		List<WebTreeNode> nodesToRemove = new ArrayList<WebTreeNode>();
		ReusableCalcFrameStack sf = getStackFrame();
		for (int i = 0; i < n; i++) {
			AmiWebTreeGroupBy gb = this.groupbyFormulas.getAt(i);
			//Old value
			Object oldValue = getValueForEntityForGroupby(tmpUpdateObj, gb, uniqueId, sf);
			Object newValue = getValueForEntityForGroupby(o, gb, uniqueId, sf);
			boolean isRecursive = gb.getIsRecursive();
			WebTreeNode oldNode2 = findNodeForEntityAndGroupbyHelper(oldValue, oldNode, i, isRecursive);
			WebTreeNode newNode2 = findNodeForEntityAndGroupbyHelper(newValue, newNode, i, isRecursive);
			if (newNode2 == null) {
				//New node doesn't exist need to be created 
				newNode2 = createNode(i, newValue, newNode, newNode, isRecursive);

				if (isRecursive) {
					Object recursiveParentKey = !isRecursive ? null : gb.getParentGroupCalc().get(sf.reset(o));
					int uid = newNode.getUid();

					this.recursiveBuilders.get(i).add2(uid, newValue, recursiveParentKey, newNode2); // rCP should be renamed to grouping Parent Key
				}
				// Update node selection
				if (newNode2.getIsExpandable() && oldNode2.getIsExpanded() == true && newNode2.getIsExpanded() == false) {
					newNode2.setIsExpanded(oldNode2.getIsExpanded());
				}
				if (oldNode2.getSelected() == true && newNode2.getSelected() == false) {
					newNode2.setSelected(oldNode2.getSelected());
				}
				this.onRemoveTreeNode(oldNode2, tmpUpdateObj);
			} else if (newNode2 != null && newNode2 != oldNode2) {
				//New node already exists, need to remove old node
				nodesToRemove.add(oldNode2);
			}

			newNode = newNode2;
			oldNode = oldNode2;

			this.getBasicWebTreeManager().addNodeToCheckFilter(oldNode);
			this.getBasicWebTreeManager().addNodeToCheckFilter(newNode);
		}
		for (int i = nodesToRemove.size() - 1; i >= 0; i--) {
			this.onRemoveTreeNode(nodesToRemove.get(i), tmpUpdateObj);
		}
		onAddUpdateRecursiveNodes(o, uniqueId);

		//Moved this to create node because order of operations matter for the filters
		AmiWebRealtimeTreeRow leafTreeRow = ((AmiWebRealtimeTreeRow) newNode.getData());
		leafTreeRow.addRow(treeObj, deltaAggregates, sf); //TODO: this is for filters and aggregation

		// Walk up and update style as well as check filter
		this.updateNodeAndParents(newNode);

		this.getBasicWebTreeManager().checkNodesToCheckFilter();

		handleDownstreamForUpdate(o, newNode, fields);
	}
	private void updateNodeAndParents(WebTreeNode updateNodeStyle) {
		// Walk up and update style as well as check filter
		WebTreeNode parentNode = null;
		while (updateNodeStyle != null && updateNodeStyle != treeManager.getRoot()) {
			parentNode = updateNodeStyle.getParent();
			tree.onStyleChanged(updateNodeStyle);
			treeManager.onNodeDataChanged(updateNodeStyle);
			if (parentNode != null) {
				if (updateNodeStyle.isFiltered() && !isNodeAllFiltered(updateNodeStyle))
					parentNode.setChildFilterered(updateNodeStyle, false);
				parentNode.ensureSorted(updateNodeStyle);
			}
			//			updateNodeStyle.checkFilter(); // This is a noop
			updateNodeStyle = parentNode;
		}

	}

	private boolean shouldKeep(AmiWebObject o) {
		boolean keep = super.shouldKeep(o);
		boolean filterKeep = (this.filter == null || this.filter.shouldKeep(o));

		boolean treeNodeKeep = true;
		if (this.amiObjects.containsKey(o.getUniqueId())) {
			//This is for updates: This will only check new values not old
			AmiWebRealtimeTreeObject leafObject = this.amiObjects.get(o.getUniqueId());
			WebTreeNode leafNode = leafObject.getRow().getNode();
			treeNodeKeep = leafNode.shouldKeep();
		}

		return keep && filterKeep && treeNodeKeep;
	}

	private WebTreeNode createNode(int i, Object value, WebTreeNode groupingParent, WebTreeNode parent, boolean isRecursive) {
		boolean isRollup = i != this.getFormulasCount() - 1;
		//Last groupby formula is the not rollup or the leaf
		WebTreeNode treeNode = treeManager.buildNode(null, false, isRollup, null);
		treeNode.setKey(value);
		treeNode.setGroupIndex(i);
		treeNode.setRecursive(isRecursive);
		if (isRecursive) {
			treeNode.setGroupRoot(groupingParent);
		}
		AmiWebRealtimeTreeRow row = new AmiWebRealtimeTreeRow(treeNode, this, !isRollup, isRecursive);
		treeNode.setData(row);
		treeNode.setHasCheckbox(true);
		treeNode.setCssClass("pointer");
		// If there is a bug filtering and handling updates it's because checkFilter is beng called before the treeObject gets added
		// Just make sure check filter gets called later
		if (parent != null) {
			parent.addChild(treeNode, false);
		}
		return treeNode;
	}
	// Takes old node changes key, and changes parent
	private WebTreeNode modifyNode(Object value, WebTreeNode node, WebTreeNode newParent) {
		WebTreeNode oldParent = node.getParent();
		boolean parentChanged = oldParent != newParent;
		boolean keyChanged = OH.ne(node.getKey(), value);
		if (parentChanged) {
			if (newParent != null)
				oldParent.removeChild(node, false);
			if (keyChanged)
				node.setKey(value);
			if (newParent != null)
				newParent.addChild(node, false);
		} else {
			if (keyChanged)
				node.setKey(value);
		}

		return parentChanged ? oldParent : null; // Only return Old parent if parents have changed
	}

	@Override
	public IterableAndSize<AmiWebObject> getAmiObjects() {
		List<WebTreeNode> rawSelected = tree.getSelected();
		IntSet s = new IntSet();
		if (rawSelected.isEmpty())
			rawSelected.add(tree.getTreeManager().getRoot());
		List<AmiWebObject> selected = new ArrayList<AmiWebObject>(rawSelected.size());
		for (WebTreeNode i : rawSelected) {
			getData(i).getRows(selected, s);
		}
		FastArrayList<AmiWebObject> r = new FastArrayList<AmiWebObject>(selected.size());
		r.addAll(selected);
		return r;

	}

	@Override
	public com.f1.base.CalcTypes getRealtimeObjectschema() {
		com.f1.utils.structs.table.stack.BasicCalcTypes r = new com.f1.utils.structs.table.stack.BasicCalcTypes();
		for (String type : this.dataTypes)
			r.putAll(this.getService().getWebManagers().getAmiObjectsByType(type).getRealtimeObjectsOutputSchema());
		return r;
	}

	@Override
	public com.f1.base.CalcTypes getRealtimeObjectsOutputSchema() {
		return getRealtimeObjectschema();
	}

	@Override
	public String getRealtimeId() {
		return this.getAri();
	}

	@Override
	public Set<String> getUpperRealtimeIds() {
		return AmiWebUtils.updateRealtimeIds(this.amiListeners, this.upperRealtimeIds);
	}

	final public Set<String> getLowerRealtimeIds() {
		return dataTypes;
	}

	private Set<String> dataTypes = new HashSet<String>();
	private AmiWebRealtimeObjectListener[] amiListeners = AmiWebRealtimeObjectListener.EMPTY_ARRAY;
	private HasherSet<AmiWebObject> downstreamRows = new HasherSet<AmiWebObject>(IdentityHasher.INSTANCE);
	private Set<String> upperRealtimeIds = new HashSet<String>();
	private AmiWebOverrideValue<Byte> downstreamRealtimeMode = new AmiWebOverrideValue<Byte>(DOWN_STREAM_MODE_SELECTED_OR_ALL);
	private byte downstreamRealtimeModeByte = downstreamRealtimeMode.get();
	private LongKeyMap<WebTreeNode> leafs = new LongKeyMap<WebTreeNode>();
	private LongKeyMap<AmiWebRealtimeTreeObject> amiObjects = new LongKeyMap<AmiWebRealtimeTreeObject>();
	private boolean buildingSnapshot;

	private boolean isDownstreamEnabled() {
		return this.downstreamRealtimeModeByte != DOWN_STREAM_MODE_OFF && this.amiListeners.length > 0;
	}
	@Override
	public boolean removeAmiListener(AmiWebRealtimeObjectListener listener) {
		int i = AH.indexOf(listener, this.amiListeners);
		if (i == -1)
			return false;
		this.amiListeners = AH.remove(this.amiListeners, i);
		if (!isDownstreamEnabled())
			downstreamRows.clear();
		this.getService().getWebManagers().onListenerRemoved(this, listener);
		return true;
	}

	@Override
	public boolean addAmiListener(AmiWebRealtimeObjectListener listener) {
		int i = AH.indexOf(listener, this.amiListeners);
		if (i != -1)
			return false;
		this.amiListeners = AH.append(this.amiListeners, listener);
		if (isDownstreamEnabled() && this.amiListeners.length == 1) {
			List<WebTreeNode> selected = this.tree.getSelected();
			if (!selected.isEmpty())
				getAmiObjects(selected, this.downstreamRows);
			else
				getAmiObjects(this.tree.getNodes(), this.downstreamRows);
		}
		this.getService().getWebManagers().onListenerAdded(this, listener);
		return true;
	}

	@Override
	public boolean hasAmiListeners() {
		return this.amiListeners.length > 0;
	}
	private void getAmiObjects(Iterable<WebTreeNode> nodes, Collection<AmiWebObject> sink) {
		for (WebTreeNode i : nodes)
			getData(i).getRows(sink, null);

	}

	@Override
	public AmiWebOverrideValue<Byte> getDownstreamRealtimeMode() {
		return this.downstreamRealtimeMode;
	}

	@Override
	public void setDownstreamRealtimeMode(byte mode) {
		if (downstreamRealtimeMode.set(mode, true))
			onDownstreamRealtimeModeChanged();
	}

	private void onDownstreamRealtimeModeChanged() {
		this.downstreamRealtimeModeByte = this.downstreamRealtimeMode.get();
		if (this.downstreamRealtimeModeByte == DOWN_STREAM_MODE_OFF) {
			this.downstreamRows.clear();
			this.fireOnAmiEntitiesClearedDownstream();
		} else {
			sendAmiRowsDownstream();
		}
	}

	@Override
	public void setDownstreamRealtimeModeOverride(byte mode) {
		if (downstreamRealtimeMode.setOverride(mode))
			onDownstreamRealtimeModeChanged();
	}

	public void setDataTypes(Set<String> selected) {
		if (selected.equals(dataTypes))
			return;
		Set<String> added = CH.comm(selected, dataTypes, true, false, false);
		Set<String> removed = CH.comm(selected, dataTypes, false, true, false);
		dataTypes.clear();
		dataTypes.addAll(selected);
		this.clearAmiData();
		AmiWebManagers webManagers = this.getService().getWebManagers();
		super.updatePanelTypesVar(this.getLowerRealtimeIds());
		for (String s : added)
			webManagers.getAmiObjectsByType(s).addAmiListener(this);
		for (String s : removed)
			webManagers.getAmiObjectsByType(s).removeAmiListener(this);
	}

	@Override
	public void rebuildAmiData() {
		try {
			buildingSnapshot = true;
			Comparator<WebTreeNode> c = this.treeManager.getComparator();
			this.treeManager.setComparator(null);
			AmiWebManagers webManagers = this.getService().getWebManagers();

			// Reset recursiveBuilders
			int formulasCount = this.getFormulasCount();
			this.tmpUpdateObj = new BasicCalcFrame(this.getClassTypes());
			this.recursiveBuilders = new ArrayList<AmiWebTreeRecursiveBuilder<Object, WebTreeNode>>(formulasCount);
			for (int i = 0; i < this.groupbyFormulas.getSize(); i++) {
				if (this.groupbyFormulas.getAt(i).getIsRecursive()) {
					this.recursiveBuilders.add(new AmiWebTreeRecursiveBuilder<Object, WebTreeNode>());
				} else
					this.recursiveBuilders.add(null);
			}

			if (!isHalted())
				for (AmiWebObject i : webManagers.getAmiObjects(this.dataTypes))
					if (shouldKeep(i))
						this.addAmiObject3(i);
			sendAmiRowsDownstream();
			//			this.treeManager.setComparator(c);
			buildingSnapshot = false;
		} catch (Exception e) {
			handleError("Error rebuilding visualization: ", null, e);
		}
	}

	private AmiWebRealtimeTreeRow getData(WebTreeNode i) {
		return (AmiWebRealtimeTreeRow) i.getData();
	}

	private boolean isHalted() {
		return false;
	}

	public int getAggColumnsCacheSize() {
		return this.allAggregates.length;
	}

	@Override
	public void onFilteredChanged(WebTreeNode child, boolean isFiltered) {
		getData(child).clearCache();
		super.onFilteredChanged(child, isFiltered);
	}

	@Override
	public void onWhereFormulaChanged() {
		//System.out.println("where formula changed");
		clearAmiData();
		rebuildAmiData();
	}

	@Override
	public void onNodeSelectionChanged(FastWebTree fastWebTree, WebTreeNode node) {
		super.onNodeSelectionChanged(fastWebTree, node);
		sendAmiRowsDownstream();
	}
	private void handleDownstreamForAdd(AmiWebObject entity, WebTreeNode row) {
		if (entity == null)
			LH.warning(log, logMe(), ": Missing entity for: ", row);
		else if (isDownstreamEnabled()) {
			if (!this.tree.hasSelected() || row.isSelected(true)) {
				downstreamRows.add(entity);
				this.fireOnAmiEntityAddedDownstream(entity);
			}
		}
	}
	protected void handleDownstreamForUpdate(AmiWebObject entity, WebTreeNode row, AmiWebObjectFields fields) {
		if (entity == null)
			LH.warning(log, logMe(), ": Missing entity for: ", row);
		else if (isDownstreamEnabled()) {
			if (this.downstreamRows.contains(entity))
				this.fireOnAmiEntityUpdatedDownstream(fields, entity);
		}
	}
	private void handleDownstreamForRemove(AmiWebObject entity, WebTreeNode row) {
		if (entity == null)
			LH.warning(log, logMe(), ": Missing entity for: ", row);
		else if (isDownstreamEnabled())
			if (this.downstreamRows.remove(entity))
				this.fireOnAmiEntityRemovedDownstream(entity);
	}

	private void sendAmiRowsDownstream() {
		if (isDownstreamEnabled()) {
			final List<AmiWebObject> add = new ArrayList<AmiWebObject>();
			final IdentityHashSet<AmiWebObject> selectedRows = new IdentityHashSet<AmiWebObject>();
			getAmiObjects(this.tree.getSelected(), selectedRows);
			if (selectedRows.isEmpty()) {
				this.downstreamRows.clear();
				getAmiObjects(this.tree.getNodes(), this.downstreamRows);
				this.fireOnAmiEntitiesClearedDownstream();
				return;
			}
			if (this.downstreamRows.size() - selectedRows.size() > 1000) {//deleting over 1000 records, lets just clear and rebuild
				this.downstreamRows.clear();
				this.downstreamRows.addAll(selectedRows);
				this.fireOnAmiEntitiesClearedDownstream();
			} else {
				final List<AmiWebObject> del = new ArrayList<AmiWebObject>();
				for (final AmiWebObject r : CH.comm(this.downstreamRows, selectedRows, true, false, false)) {
					this.downstreamRows.remove(r);
					del.add(r);
				}
				for (final AmiWebObject r : CH.comm(this.downstreamRows, selectedRows, false, true, false)) {
					this.downstreamRows.add(r);
					add.add(r);
				}
				if (!del.isEmpty()) {
					for (final AmiWebObject o : del)
						this.fireOnAmiEntityRemovedDownstream(o);
				}
				if (!add.isEmpty()) {
					for (final AmiWebObject o : add)
						this.fireOnAmiEntityAddedDownstream(o);
				}
			}
		}

	}

	@Override
	public AmiWebPanelSettingsPortlet showSettingsPortlet() {
		return new AmiWebRealtimeTreeEditSettingsPortlet(generateConfig(), this);
	}
	private void fireOnAmiEntitiesClearedDownstream() {
		for (final AmiWebRealtimeObjectListener i : this.amiListeners)
			try {
				i.onAmiEntitiesReset(this);
			} catch (Exception e) {
				LH.warning(log, "Error clearing Ami Entities for " + OH.getSimpleClassName(i), e);
			}
	}
	private void fireOnAmiEntityAddedDownstream(AmiWebObject entity) {
		for (final AmiWebRealtimeObjectListener i : this.amiListeners)
			try {
				i.onAmiEntityAdded(this, entity);
			} catch (Exception e) {
				LH.warning(log, "Error adding Ami Entity for " + OH.getSimpleClassName(i) + " for entity: " + SH.ddd(entity.toString(), 128), e);
			}
	}
	private void fireOnAmiEntityRemovedDownstream(AmiWebObject entity) {
		for (final AmiWebRealtimeObjectListener i : this.amiListeners)
			try {
				i.onAmiEntityRemoved(this, entity);
			} catch (Exception e) {
				LH.warning(log, "Error removing Ami Entity for " + OH.getSimpleClassName(i) + " for entity: " + SH.ddd(entity.toString(), 128), e);
			}
	}
	private void fireOnAmiEntityUpdatedDownstream(AmiWebObjectFields fields, AmiWebObject entity) {
		for (final AmiWebRealtimeObjectListener i : this.amiListeners)
			try {
				i.onAmiEntityUpdated(this, fields, entity);
			} catch (Exception e) {
				LH.warning(log, "Error updating Ami Entity for " + OH.getSimpleClassName(i) + " for entity: " + SH.ddd(entity.toString(), 128), e);
			}
	}

	@Override
	public void onAmiInitDone() {
		super.onAmiInitDone();
	}

	@Override
	public void onLowerAriChanged(AmiWebRealtimeObjectManager manager, String oldAri, String newAri) {
		CH.removeOrThrow(this.dataTypes, oldAri);
		CH.addOrThrow(this.dataTypes, newAri);
	}

	@Override
	protected void onAdnChanged(String old, String adn) {
		super.onAdnChanged(old, adn);
		if (old != null)
			for (AmiWebRealtimeObjectListener i : this.amiListeners) {
				try {
					i.onLowerAriChanged(this, AmiWebDomObject.ARI_TYPE_PANEL + ":" + old, AmiWebDomObject.ARI_TYPE_PANEL + ":" + adn);
				} catch (Exception e) {
					LH.warning(log, logMe(), " Error updating Ari Entity for ", OH.getSimpleClassName(i), " for : ", old + " ==> ", adn);
				}
			}
	}

	@Override
	public void onSchemaChanged(AmiWebRealtimeObjectManager manager, byte status, Map<String, Tuple2<Class, Class>> columns) {
		if (status != SCHEMA_DROPPED) {
			this.formulas.recompileAmiscript();
			for (AmiWebTreeGroupBy i : this.groupbyFormulas.values())
				i.getFormulas().recompileAmiscript();
			for (AmiWebTreeColumn i : this.columnsById.values())
				i.getFormulas().recompileAmiscript();
		}
	}

}
