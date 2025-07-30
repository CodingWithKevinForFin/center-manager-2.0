package com.f1.ami.web.realtimetree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.f1.ami.web.AmiWebObject;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.utils.AH;
import com.f1.utils.sql.aggs.AggDeltaCalculator;
import com.f1.utils.structs.IntSet;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.LongKeyMap.Node;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

//one per row in the tree
public class AmiWebRealtimeTreeRow {
	public static final Object NOT_CACHED = new Object();
	private LongKeyMap<AmiWebRealtimeTreeObject> rows;
	final private WebTreeNode node;
	private Object[] cache;
	private boolean isDeltaAggCached[];
	private boolean isSnapshotAggCached;
	private boolean isRecursive;

	public AmiWebRealtimeTreeRow(WebTreeNode node, AmiWebRealtimeTreePortlet tree, boolean isLeaf, boolean isRecursive) {
		this.node = node;
		this.cache = new Object[tree.getAggColumnsCacheSize()];
		this.isDeltaAggCached = new boolean[this.cache.length];
		this.rows = isLeaf ? new LongKeyMap<AmiWebRealtimeTreeObject>() : null;
		this.isRecursive = isRecursive;
	}

	public WebTreeNode getNode() {
		return this.node;
	}

	public AmiWebObject getFirstRow() {
		//##Static:
		// 1) If rows not null , return first row otherwise null
		// 2) (normal) Walk down the tree until you find the first data from a node
		// 2) (recursive) Walk down the tree, visit the leaf nodes, get the first row, otherwise then walk down the nodes that are not leafs
		// 3) Walk down the filtered children. 

		//##RT:
		// 1) If rows not null, return first row 
		// 2) Walk down the tree until you find the first data from a node
		// 3) Walk down the filtered children

		if (rows != null) {
			if (rows.size() == 0)
				return null;
			for (Node<AmiWebRealtimeTreeObject> i : rows)
				return i.getValue().getObject();
		} else {
			if (!isRecursive) {
				for (int i = 0, l = node.getChildrenCount(); i < l; i++) {
					AmiWebRealtimeTreeRow row = (AmiWebRealtimeTreeRow) node.getChildAt(i).getData();
					AmiWebObject r = row.getFirstRow();
					if (r != null)
						return r;
				}
			} else {
				List<WebTreeNode> notleafs = new ArrayList<WebTreeNode>();
				for (int i = 0, l = node.getChildrenCount(); i < l; i++) {
					WebTreeNode childAt = node.getChildAt(i);
					if (childAt.isLeaf()) {
						AmiWebRealtimeTreeRow row = (AmiWebRealtimeTreeRow) node.getChildAt(i).getData();
						AmiWebObject r = row.getFirstRow();
						if (r != null)
							return r;
					} else
						notleafs.add(childAt);
				}
				for (int i = 0, l = notleafs.size(); i < l; i++) {
					AmiWebRealtimeTreeRow row = (AmiWebRealtimeTreeRow) node.getChildAt(i).getData();
					AmiWebObject r = row.getFirstRow();
					if (r != null)
						return r;
				}
			}

			for (WebTreeNode i : node.getFilteredChildren()) {
				AmiWebRealtimeTreeRow row = (AmiWebRealtimeTreeRow) i.getData();
				AmiWebObject r = row.getFirstRow();
				if (r != null)
					return r;
			}
		}

		return null;
	}
	public void getRows(Collection<AmiWebObject> sink, IntSet uniqueNodesSink) {
		if (uniqueNodesSink != null && !uniqueNodesSink.add(this.node.getUid()))
			return;
		if (!isRecursive)
			if (rows != null) {
				for (Node<AmiWebRealtimeTreeObject> i : rows)
					sink.add(i.getValue().getObject());
			} else {
				for (int i = 0, l = node.getChildrenCount(); i < l; i++) {
					AmiWebRealtimeTreeRow row = (AmiWebRealtimeTreeRow) node.getChildAt(i).getData();
					row.getRows(sink, uniqueNodesSink);
				}
			}
		else {
			if (rows != null)
				for (Node<AmiWebRealtimeTreeObject> i : rows)
					sink.add(i.getValue().getObject());
			if (!this.node.isLeaf()) {
				List<WebTreeNode> notleafs = new ArrayList<WebTreeNode>();
				for (int i = 0, l = node.getChildrenCount(); i < l; i++) {
					WebTreeNode childAt = node.getChildAt(i);
					if (childAt.isLeaf()) {
						AmiWebRealtimeTreeRow row = (AmiWebRealtimeTreeRow) childAt.getData();
						row.getRows(sink, uniqueNodesSink);
					} else
						notleafs.add(childAt);
				}
				for (int i = 0, l = notleafs.size(); i < l; i++) {
					AmiWebRealtimeTreeRow row = (AmiWebRealtimeTreeRow) notleafs.get(i).getData();
					row.getRows(sink, uniqueNodesSink);
				}
			}
		}
	}

	public void onParentSet(AmiWebRealtimeTreeRow treeRow, AggDeltaCalculator[] deltaAggregates) {
		for (int i = 0; i < deltaAggregates.length; i++) {
			for (AmiWebRealtimeTreeRow row = treeRow; row != null; row = row.getParent()) {
				if (this.isDeltaAggCached(i)) {
					Object aggValue = deltaAggregates[i].applyDelta(row.getCache(i), null, getCache(i));
					row.setCache(i, aggValue);
				} else
					row.setIsDeltaAggCached(i, false);
			}
		}
		for (AmiWebRealtimeTreeRow row = treeRow; row != null; row = row.getParent())
			row.setIsSnapshotCached(false);
	}

	public void onParentUnset(AmiWebRealtimeTreeRow treeRow, AggDeltaCalculator[] deltaAggregates) {
		for (int i = 0; i < deltaAggregates.length; i++) {
			for (AmiWebRealtimeTreeRow row = treeRow; row != null; row = row.getParent()) {
				if (this.isDeltaAggCached(i)) {
					Object aggValue = deltaAggregates[i].applyDelta(row.getCache(i), getCache(i), null);
					row.setCache(i, aggValue);
				} else
					row.setIsDeltaAggCached(i, false);
			}
		}
		for (AmiWebRealtimeTreeRow row = treeRow; row != null; row = row.getParent())
			row.setIsSnapshotCached(false);
	}

	public int leafCount() {
		if (rows != null) {
			return this.rows.size();
		} else {
			int r = 0;
			for (int i = 0, l = node.getChildrenCount(); i < l; i++) {
				AmiWebRealtimeTreeRow row = (AmiWebRealtimeTreeRow) node.getChildAt(i).getData();
				r += row.leafCount();
			}
			return r;
		}
	}
	public boolean isDeltaAggCached(int i) {
		return isDeltaAggCached[i];
	}
	public void setIsDeltaAggCached(int i, boolean b) {
		this.isDeltaAggCached[i] = b;
	}
	public Object getCache(int i) {
		return cache[i];
	}
	public void setCache(int i, Object value) {
		if (value == AggDeltaCalculator.NOT_AGGEGATED)
			this.isDeltaAggCached[i] = false;
		this.cache[i] = value;
		//		node.getTreeManager().onNodeDataChanged(node);
	}
	public void clearCache() {
		AH.fill(this.isDeltaAggCached, false);
		this.isSnapshotAggCached = false;
		if (getParent() != null)
			getParent().clearCache();

	}

	public void addRow(AmiWebRealtimeTreeObject row, AggDeltaCalculator[] deltaAggregates, ReusableCalcFrameStack sf) {
		this.rows.putOrThrow(row.getObject().getUniqueId(), row);
		row.onAdd(this, deltaAggregates, sf);
	}

	public AmiWebRealtimeTreeObject removeRow(AmiWebRealtimeTreeObject treeObject, AggDeltaCalculator[] deltaAggregates) {
		AmiWebRealtimeTreeObject existing = this.rows.removeOrThrow(treeObject.getObject().getUniqueId());
		existing.onRemove(this, deltaAggregates);
		// Moved to the realtime tree portlet
		return existing;
	}

	// OLD
	//	public boolean isEmpty() {
	//		//Static: check if the children nodes isEmpty as well
	//		return rows != null ? this.rows.isEmpty() : this.node.getChildrenCount() == 0;
	//	}

	// As long as rows is null or is empty
	public boolean isEmpty() {
		return rows != null ? this.rows.isEmpty() : true;
		//TODO:handle filtered
		//		if (this.node == null)
		//			throw new RuntimeException("Invalid State");
		//		if (!isRecursive) {
		//			//Scenario 0: The Leaf
		//			if (this.rows != null)
		//				return this.rows.isEmpty();
		//			//Scenario 1: Show Leaf (Parent of Leaf) 
		//			//Scenario 2: All other nodes
		//			else
		//				// This isn't quite perfect either with filtering
		//				return this.node.getChildrenCount() == 0;
		//			//				return (this.node.getFilteredChildrenCount() + this.node.getChildrenCount()) == 0;
		//		} else {
		//			if (this.rows != null)
		//				return this.rows.isEmpty();
		//			//Scenario 3: Show Leaf (Parent of Leaf) 
		//			//Scenario 4: All other nodes
		//			else
		//				return !this.node.hasChildrenLeafs();
		//		}
	}

	public void onTreeCalcsChanged(int columnCacheSize, int deltaColumnCacheSize) {
		this.cache = new Object[columnCacheSize];
		this.isDeltaAggCached = new boolean[columnCacheSize];
		// Loops through children and clears the caches
		if (rows != null) {
			for (AmiWebRealtimeTreeObject i : this.rows.values())
				i.onTreeCalcsChanged(deltaColumnCacheSize);
		} else {
			for (int i = 0, l = node.getChildrenCount(); i < l; i++) {
				AmiWebRealtimeTreeRow row = (AmiWebRealtimeTreeRow) node.getChildAt(i).getData();
				row.onTreeCalcsChanged(columnCacheSize, deltaColumnCacheSize);
			}
		}
		AH.fill(this.isDeltaAggCached, false);
		isSnapshotAggCached = false;
	}
	public AmiWebRealtimeTreeObject getRow(long uniqueId) {
		return this.rows.get(uniqueId);
	}
	public AmiWebRealtimeTreeRow getParent() {
		WebTreeNode p = node.getParent();
		return p == null ? null : (AmiWebRealtimeTreeRow) p.getData();
	}
	public void setIsSnapshotCached(boolean b) {
		this.isSnapshotAggCached = false;
	}
	public boolean isSnapshotCached() {
		return this.isSnapshotAggCached;
	}

}
