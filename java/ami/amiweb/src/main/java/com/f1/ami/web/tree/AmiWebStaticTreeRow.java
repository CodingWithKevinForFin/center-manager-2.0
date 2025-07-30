package com.f1.ami.web.tree;

import java.util.ArrayList;
import java.util.List;

import com.f1.base.Row;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.structs.IntSet;

public class AmiWebStaticTreeRow {
	final private AmiWebStaticTreePortlet tree;
	final private List<Row> rows;
	private Object[] cache;
	private boolean isCached;
	private WebTreeNode node;
	private boolean isRecursive;

	public AmiWebStaticTreeRow(WebTreeNode node, AmiWebStaticTreePortlet tree, boolean isLeaf, boolean isRecursive) {
		this.node = node;
		this.tree = tree;
		this.isRecursive = isRecursive;
		this.cache = new Object[this.tree.getAggColumnsCacheSize()];
		this.rows = isLeaf ? new ArrayList<Row>() : null;

	}

	public void getRows(List<Row> sink, IntSet uniqueNodesSink) {
		if (uniqueNodesSink != null && !uniqueNodesSink.add(this.node.getUid()))
			return;
		//		if (rows != null) {
		//			sink.addAll(rows);
		//		} else {
		//			for (int i = 0, l = node.getChildrenCount(); i < l; i++) {
		//				AmiWebStaticTreeRow row = (AmiWebStaticTreeRow) node.getChildAt(i).getData();
		//				row.getRows(sink, uniqueNodesSink);
		//			}
		//		}

		if (!isRecursive) {
			if (rows != null) {
				sink.addAll(rows);
			} else {
				for (int i = 0, l = node.getChildrenCount(); i < l; i++) {
					AmiWebStaticTreeRow row = (AmiWebStaticTreeRow) node.getChildAt(i).getData();
					row.getRows(sink, uniqueNodesSink);
				}
			}
		} else {
			if (rows != null)
				sink.addAll(rows);
			if (!this.node.isLeaf()) {
				List<WebTreeNode> notleafs = new ArrayList<WebTreeNode>();
				for (int i = 0, l = node.getChildrenCount(); i < l; i++) {
					WebTreeNode childAt = node.getChildAt(i);
					if (childAt.isLeaf()) {
						AmiWebStaticTreeRow row = (AmiWebStaticTreeRow) childAt.getData();
						row.getRows(sink, uniqueNodesSink);
					} else
						notleafs.add(childAt);
				}
				for (int i = 0, l = notleafs.size(); i < l; i++) {
					AmiWebStaticTreeRow row = (AmiWebStaticTreeRow) notleafs.get(i).getData();
					row.getRows(sink, uniqueNodesSink);
				}
			}

		}
	}

	public void clearCache() {
		//		if (!this.isCached)
		//			return;
		this.isCached = false;
		this.tree.getTree().getTreeManager().onNodeDataChanged(this.node);
		WebTreeNode parent = this.node.getParent();
		if (parent != null) {
			AmiWebStaticTreeRow t = (AmiWebStaticTreeRow) parent.getData();
			if (t != null)
				t.clearCache();
		}
	}

	public int size() {
		if (rows != null) {
			return this.rows.size();
		} else {
			int r = 0;
			for (int i = 0, l = node.getChildrenCount(); i < l; i++) {
				AmiWebStaticTreeRow row = (AmiWebStaticTreeRow) node.getChildAt(i).getData();
				r += row.size();
			}
			return r;
		}
	}
	public boolean isCached() {
		return isCached;
	}
	public void setIsCached() {
		this.isCached = true;
	}
	public Object getCache(int i) {
		return cache[i];
	}
	public void setCache(int i, Object value) {
		this.cache[i] = value;
	}
	public void addRow(Row row) {
		this.rows.add(row);
		clearCache();
	}
	public void removeRow(Row row) {
		int i = CH.indexOfIdentity(this.rows, row);
		this.rows.remove(i);
		if (this.rows.isEmpty()) {
			WebTreeNode parent = this.node.getParent();
			parent.removeChild(this.node);
			while (parent.getChildrenCount() == 0 && !this.node.isRecursive()) {
				WebTreeNode parent2 = parent.getParent();
				if (parent2 == null)
					break;
				parent2.removeChild(parent);
				parent = parent2;
			}
		}
		clearCache();

	}
	public void onTreeCalcsChanged() {
		this.cache = new Object[this.tree.getAggColumnsCacheSize()];
		this.isCached = false;
	}
	public boolean isEmpty() {
		if (rows != null)
			return rows.size() == 0;
		for (int i = 0, l = node.getChildrenCount(); i < l; i++) {
			AmiWebStaticTreeRow row = (AmiWebStaticTreeRow) node.getChildAt(i).getData();
			if (!row.isEmpty())
				return false;
		}
		return true;
	}
	public Row getFirstRow() {
		if (rows != null) {
			return rows.size() == 0 ? null : rows.get(0);
		} else if (!isRecursive) {
			for (int i = 0, l = node.getChildrenCount(); i < l; i++) {
				AmiWebStaticTreeRow row = (AmiWebStaticTreeRow) node.getChildAt(i).getData();
				Row r = row.getFirstRow();
				if (r != null)
					return r;
			}
		} else {
			List<WebTreeNode> notleafs = new ArrayList<WebTreeNode>();
			for (int i = 0, l = node.getChildrenCount(); i < l; i++) {
				WebTreeNode childAt = node.getChildAt(i);
				if (childAt.isLeaf()) {
					AmiWebStaticTreeRow row = (AmiWebStaticTreeRow) childAt.getData();
					Row r = row.getFirstRow();
					if (r != null)
						return r;
				} else
					notleafs.add(childAt);
			}
			for (int i = 0, l = notleafs.size(); i < l; i++) {
				AmiWebStaticTreeRow row = (AmiWebStaticTreeRow) notleafs.get(i).getData();
				Row r = row.getFirstRow();
				if (r != null)
					return r;
			}
		}
		for (WebTreeNode i : node.getFilteredChildren()) {
			AmiWebStaticTreeRow row = (AmiWebStaticTreeRow) i.getData();
			Row r = row.getFirstRow();
			if (r != null)
				return r;
		}
		return null;
	}

	public void updateRow(Row orig, Row row2) {
		int i = CH.indexOfIdentity(this.rows, orig);
		this.rows.set(i, row2);
		if (OH.ne(orig, row2))
			clearCache();
	}

	public int getRowCount() {
		return rows == null ? 0 : this.rows.size();
	}

	public Row getRowAt(int i) {
		return this.rows.get(i);
	}

}
