/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.tree.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import com.f1.base.IntIterator;
import com.f1.suite.web.tree.WebTreeFilter;
import com.f1.suite.web.tree.WebTreeManager;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.WebTreeNode.Inner;
import com.f1.suite.web.tree.WebTreeNodeFormatter;
import com.f1.suite.web.tree.WebTreeNodeListener;
import com.f1.utils.AH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.impl.FastArrayList;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.IntSet;
import com.f1.utils.structs.SkipList;
import com.f1.utils.structs.SparseList;

public class BasicWebTreeManager implements WebTreeManager {

	public static class InnerComparator implements Comparator<Inner> {

		private Comparator<WebTreeNode> inner;

		public InnerComparator(Comparator<WebTreeNode> c) {
			this.inner = c;
		}

		@Override
		public int compare(Inner o1, Inner o2) {
			return this.inner.compare(o1.getNode(), o2.getNode());
		}

	}

	public static final Comparator<WebTreeNode> DEFAULT_COMPARATOR = new Comparator<WebTreeNode>() {
		@Override
		public int compare(WebTreeNode o1, WebTreeNode o2) {
			return SH.COMPARATOR_CASEINSENSITIVE.compare(o1.getName(), o2.getName());
		}
	};

	public static final Comparator<WebTreeNode> POSITION_SORTER = new Comparator<WebTreeNode>() {

		@Override
		public int compare(WebTreeNode o1, WebTreeNode o2) {
			return OH.compare(o1.getAbsolutePosition(), o2.getAbsolutePosition());
		}
	};

	private IntKeyMap<WebTreeNode> treeNodes = new IntKeyMap<WebTreeNode>();
	private WebTreeNode root;
	private int nextUid = ROOT_UID + 1;
	private WebTreeNodeListener listeners[] = null;
	private SparseList<WebTreeNode> nodes = (SparseList) new SkipList<WebTreeNode>(1000);
	private Comparator<WebTreeNode> comparator;
	private Comparator<WebTreeNode.Inner> innerComparator;
	private WebTreeFilter treeFilter;
	private WebTreeFilter searchFilter;
	private FastWebTreeColumnFilter columnsFilter;
	final private IntSet selected = new IntSet();

	private int activeSelectedNodeUid = -1;

	private FastWebTree fastWebTree;

	public BasicWebTreeManager() {
		clear();
		this.comparator = DEFAULT_COMPARATOR;
		this.innerComparator = toInner(this.comparator);
	}
	public WebTreeNodeFormatter getFormatter() {
		return this.fastWebTree.getFormatter();
	}
	private static Comparator<Inner> toInner(Comparator<WebTreeNode> c) {
		if (c == null)
			return null;
		return new InnerComparator(c);
	}
	protected int generateUid() {
		return nextUid++;
	}

	@Override
	public WebTreeNode getRoot() {
		return root;
	}

	@Override
	public WebTreeNode getTreeNode(int uid) {
		final WebTreeNode r = treeNodes.get(uid);
		if (r == null)
			throw new NoSuchElementException("uid: " + SH.toString(uid));
		return r;
	}
	@Override
	public WebTreeNode getTreeNodeNoThrow(int uid) {
		return treeNodes.get(uid);
	}

	@Override
	public WebTreeNode createNode(String name, WebTreeNode parent, boolean expanded) {
		return createNode(name, parent, expanded, null);
	}

	@Override
	public WebTreeNode buildNode(String name, boolean expanded, boolean isRollup, Object data) {
		WebTreeNode r = new WebTreeNode(this, generateUid(), name, expanded);
		r.setData(data);
		return r;
	}

	@Override
	public void addNode(WebTreeNode parent, WebTreeNode r) {
		r.setParent(parent);//this way the parent won't know about this child
		//		if (
		//			parent.addFilteredChild(r);
		//		} else {
		parent.addChild(r);
		//		}
	}
	@Override
	public WebTreeNode createNode(String name, WebTreeNode parent, boolean expanded, Object data) {
		if (parent == null)
			throw new NullPointerException("parent null for child node: " + name);
		WebTreeNode r = buildNode(name, expanded, false, data);
		addNode(parent, r);
		return r;
	}

	@Override
	public void addListener(WebTreeNodeListener listener) {
		if (this.listeners == null)
			this.listeners = new WebTreeNodeListener[] { listener };
		else
			this.listeners = AH.append(this.listeners, listener);
	}
	@Override
	public void removeListener(WebTreeNodeListener listener) {
		this.listeners = AH.remove(this.listeners, AH.indexOf(listener, this.listeners));
		if (this.listeners.length == 0)
			this.listeners = null;
	}

	@Override
	public void onNodeDataChanged(WebTreeNode node) {
		//		node.checkFilter(); // This is a noop
		this.fastWebTree.onStyleChanged(node);
		if (this.listeners != null)
			for (WebTreeNodeListener i : this.listeners)
				i.onStyleChanged(node);
		//		if (!node.isFiltered()) {
		//			if (comparator != null) {
		//				if (node.getParent() != null)
		//					node.getParent().ensureSorted(node);
		//			}
		//	}

	}

	public void onChildFilteredChanged(WebTreeNode child, boolean isFiltered) {
		if (this.listeners != null)
			for (WebTreeNodeListener i : this.listeners)
				i.onFilteredChanged(child, isFiltered);
		tmpNodesBuf.clear(1024);
		tmpNodesBuf.add(child);
		this.fastWebTree.onNodesAddedToVisible(tmpNodesBuf.getElements(), 0, tmpNodesBuf.size());
	}

	public void onExpandedChanged(WebTreeNode node) {
		if (node == root || (node.getParent().getAbsolutePosition() != -1 && node.getParent().getIsExpanded() && node.getChildrenCount() > 0)) {
			// Fix for root nodes throwing error when expanding
			final int top = this.root == node ? node.getAbsolutePosition() + 1 : node.getAbsolutePosition() + 1;
			if (node.getIsExpanded()) {
				tmpNodesBuf.clear(1024);
				node.sort();
				addChildren(node, tmpNodesBuf);
				WebTreeNode[] elements = tmpNodesBuf.getElements();
				this.nodes.addAll(top, elements, 0, tmpNodesBuf.size());
				this.fastWebTree.onNodesAddedToVisible(elements, 0, tmpNodesBuf.size());
				tmpNodesBuf.clear(1024);
			} else {

				for (int i = 0; i < node.getChildrenCount(); i++)
					deselectRecusively(node.getChildAt(i));
				WebTreeNode lastNode = node;
				while ((lastNode.getIsExpanded() || node == lastNode) && lastNode.getChildrenCount() > 0)
					lastNode = lastNode.getChildAt(lastNode.getChildrenCount() - 1);
				int bottom = lastNode.getAbsolutePosition();
				if (bottom == -1)
					throw new IllegalStateException();
				WebTreeNode[] sink = new WebTreeNode[bottom + 1 - top];
				this.nodes.getAll(top, bottom + 1, sink, 0);
				this.fastWebTree.onRemovingNodesFromVisible(sink, 0, sink.length);
				this.nodes.removeAll(top, bottom + 1);
				//				toRemove.clear();
			}
		}
		this.fastWebTree.onExpanded(node);
		if (this.listeners != null)
			for (WebTreeNodeListener i : this.listeners)
				i.onExpanded(node);
	}

	private void deselectRecusively(WebTreeNode node) {
		node.setSelected(false);
		for (int i = 0; i < node.getChildrenCount(); i++)
			deselectRecusively(node.getChildAt(i));
	}
	public void onChildRemoved(WebTreeNode node) {
		this.onChildRemoved(node, true);
	}
	public void onChildRemoved(WebTreeNode node, boolean deselectRecursively) {
		if (deselectRecursively)
			deselectRecusively(node);
		this.fastWebTree.onNodeRemoved(node);
		if (this.listeners != null)
			for (WebTreeNodeListener i : this.listeners)
				i.onNodeRemoved(node);
		if (treeNodes.remove(node.getUid()) == null)
			throw new IllegalStateException("node not found: " + node.getUid());
		int top = node.getAbsolutePosition();
		if (top != -1) {
			WebTreeNode lastNode = node;//node.getBottomVisibleChildNode();
			while (lastNode.getIsExpanded() && lastNode.getChildrenCount() > 0)
				lastNode = lastNode.getChildAt(lastNode.getChildrenCount() - 1);
			int bottom = lastNode.getAbsolutePosition();
			if (bottom == -1)
				throw new IllegalStateException();
			WebTreeNode[] sink = new WebTreeNode[bottom + 1 - top];
			this.nodes.getAll(top, bottom + 1, sink, 0);
			this.fastWebTree.onRemovingNodesFromVisible(sink, 0, sink.length);
			this.nodes.removeAll(top, bottom + 1);
		}
	}

	final FastArrayList<WebTreeNode> tmpNodesBuf = new FastArrayList<WebTreeNode>(new WebTreeNode[1024], 0);

	public void onChildAdded(WebTreeNode node) {
		if (this.listeners != null)
			for (WebTreeNodeListener i : this.listeners)
				i.onNodeAdded(node);
		//TODO:check for existing CH.putOrThrow(treeNodes, node.getUid(), node);
		if (treeNodes.put(node.getUid(), node) != null)
			throw new IllegalStateException("already exists: " + node);
		//TODO: why check for isExpanded old?
		if (node != root & node.getParent().getAbsolutePosition() != -1 && node.getParent().getIsExpanded()) {
			tmpNodesBuf.clear(1024);
			tmpNodesBuf.add(node);
			addChildren(node, tmpNodesBuf);
			int top;
			if (node.getPosition() == 0)
				top = node.getParent().getAbsolutePosition() + 1;
			else {
				WebTreeNode lastNode = node.getParent().getChildAt(node.getPosition() - 1);//WebTreeNode lastNode = node.getParent().getBottomVisibleChildNode();
				while (lastNode.getIsExpanded() && lastNode.getChildrenCount() > 0)
					lastNode = lastNode.getChildAt(lastNode.getChildrenCount() - 1);
				top = lastNode.getAbsolutePosition() + 1;
			}
			this.nodes.addAll(top, tmpNodesBuf.getElements(), 0, tmpNodesBuf.size());
			this.fastWebTree.onNodesAddedToVisible(tmpNodesBuf.getElements(), 0, tmpNodesBuf.size());
			tmpNodesBuf.clear(1024);
		}
	}

	public void verify() {
	}
	private int verify(WebTreeNode node, int position) {
		if (node.getAbsolutePosition() != position) {
			OH.assertEq(node.getAbsolutePosition(), position);
		}
		if (position != -1) {
			if (node.getIsExpanded()) {
				for (int i = 0; i < node.getChildrenCount(); i++) {
					position++;
					WebTreeNode child = node.getChildAt(i);
					OH.assertEq(child.getPosition(), i);
					position = verify(child, position);
				}
			} else {
				for (int i = 0; i < node.getChildrenCount(); i++) {
					WebTreeNode child = node.getChildAt(i);
					OH.assertEq(child.getPosition(), i);
					verify(child, -1);
				}
			}
			return position;
		} else {
			for (int i = 0; i < node.getChildrenCount(); i++) {
				WebTreeNode child = node.getChildAt(i);
				OH.assertEq(child.getPosition(), i);
				verify(child, -1);
			}
			return -1;
		}
	}

	private void addChildren(WebTreeNode node, FastArrayList<WebTreeNode> sink) {
		if (node.getIsExpanded()) {
			for (int i = 0, l = node.getChildrenCount(); i < l; i++) {
				WebTreeNode child = node.getChildAt(i);
				sink.add(child);
				addChildren(child, sink);
			}
		}
	}

	public void debug() {
		StringBuilder t = new StringBuilder();
		debug(t);
		System.out.println(t);
	}
	public void debug(StringBuilder out) {
		debug(root, out);
	}
	public void debug(WebTreeNode node, StringBuilder out) {
		SH.repeat(' ', node.getDepth() * 2, out);
		out.append(node.getIsExpanded() ? "+ " : "- ");
		out.append(node.getName());
		out.append(" (position=").append(node.getPosition()).append(", absPosition=").append(node.getAbsolutePosition()).append(") ");
		out.append(SH.NEWLINE);
		for (int i = 0, l = node.getChildrenCount(); i < l; i++) {
			debug(node.getChildAt(i), out);
		}
	}

	@Override
	public void removeNode(WebTreeNode child) {
		//		if (filter != null && filtered.remove(child.getUid()) != null)
		//			return;
		child.getParent().removeChild(child);
	}

	protected WebTreeNode getVisibleTreeNodeAt(int location) {
		return this.nodes.get(location);
	}

	protected int getVisibleNodesCount() {
		return this.nodes.size();
	}

	@Override
	public void setAllExpanded(boolean isExpanded) {
		WebTreeHelper.setAllExpanded(root, isExpanded);

	}

	public void setSelectedExpanded(boolean isExpanded, boolean isRecursive) {
		List<WebTreeNode> selectedNodes = getSelectedNodes();
		if (isExpanded) {
			for (WebTreeNode i : selectedNodes) {
				if (!isRecursive)
					i.setIsExpanded(true);
				else
					WebTreeHelper.setAllExpanded(i, true);
			}
		} else {
			for (WebTreeNode i : selectedNodes) {
				if (!isRecursive)
					i.setIsExpanded(false);
				else
					WebTreeHelper.setAllExpanded(i, false);
			}
		}

	}

	@Override
	public void clear() {
		if (nodes.size() > 0) {
			this.fastWebTree.onAllNodesRemoved();//nodes.toArray(new WebTreeNode[nodes.size()]), 0, nodes.size());
			this.nodes.clear();
		}
		this.treeNodes.clear();
		this.selected.clear();
		this.root = new WebTreeNode(this, ROOT_UID, "", true);
		this.treeNodes.put(root.getUid(), root);
		this.nodes.add(root);
		this.activeSelectedNodeUid = -1;
		if (this.fastWebTree != null)
			this.fastWebTree.setSelectedRowsText("");
	}

	public void onSelectionChanged(WebTreeNode node) {
		if (node.getSelected())
			selected.add(node.getUid());
		else {
			selected.remove(node.getUid());
			if (node.getUid() == this.activeSelectedNodeUid)
				this.activeSelectedNodeUid = -1;
		}
		this.fastWebTree.onSelectionChanged(node); //Need to fire after for correct state of selectedRows
	}
	public void onCheckedChanged(WebTreeNode node) {
		this.fastWebTree.onCheckedChanged(node);
		if (!node.isFiltered())
			if (this.listeners != null)
				for (WebTreeNodeListener i : this.listeners)
					i.onCheckedChanged(node);

	}

	@Override
	public Comparator<WebTreeNode> getComparator() {
		return comparator;
	}
	public Comparator<Inner> getInnerComparator() {
		return this.innerComparator;
	}

	@Override
	public void setComparator(Comparator<WebTreeNode> comparator) {
		if (comparator == null && this.comparator == null)
			return;
		this.comparator = comparator;
		this.innerComparator = toInner(comparator);
		if (this.comparator != null) {
			root.onComparatorChanged();
			root.sort();
			this.nodes.clear();
			rebuildPositions(root, 0);
		}

	}

	private int rebuildPositions(WebTreeNode node, int absPos) {
		if (absPos != -1) {
			nodes.add(node);
			absPos++;
		}
		if (absPos == -1 || !node.getIsExpanded()) {
			for (int i = 0; i < node.getChildrenCount(); i++)
				rebuildPositions(node.getChildAt(i), -1);
		} else {
			for (int i = 0; i < node.getChildrenCount(); i++) {
				absPos = rebuildPositions(node.getChildAt(i), absPos);
			}
		}
		return absPos;
	}
	protected void setAndRunFilter(WebTreeFilter searchFilter, WebTreeFilter treeFilter, FastWebTreeColumnFilter columnsFilter) {
		setFilter(searchFilter, treeFilter, columnsFilter);
		runFilter();
	}

	protected void runFilter() {
		this.checkFilter(root);
		root.sort();
		this.nodes.clear();
		rebuildPositions(root, 0);
	}

	protected void setFilter(WebTreeFilter searchFilter, WebTreeFilter treeFilter, FastWebTreeColumnFilter columnsFilter) {
		this.searchFilter = searchFilter;
		this.treeFilter = treeFilter;
		this.columnsFilter = columnsFilter;
		if (columnsFilter != null)
			this.passesColumnFiltersTmp = new boolean[this.columnsFilter.size()];
	}

	private StringBuilder sb = new StringBuilder();
	private boolean passesTreeFilterTmp = false;
	private boolean passesTreeSearchTmp = false;
	private boolean passesColumnFiltersTmp[] = null;
	private Set<WebTreeNode> nodesToCheckFilter = new HashSet<WebTreeNode>();

	public void assertEmptyNodesToCheckFilter() {
		OH.assertEmpty(this.nodesToCheckFilter);
	}
	public void clearNodesToCheckFilter() {
		this.nodesToCheckFilter.clear();
	}
	public void addNodeToCheckFilter(WebTreeNode node) {
		nodesToCheckFilter.add(node);
	}
	public void checkNodesToCheckFilter() {
		if (this.searchFilter == null && this.treeFilter == null && this.columnsFilter == null) {
			this.nodesToCheckFilter.clear();
			return;
		}
		for (WebTreeNode n : nodesToCheckFilter)
			this.checkFilter(n);
		this.nodesToCheckFilter.clear();
	}

	private void resetFilterFlags() {
		this.passesTreeFilterTmp = false;
		this.passesTreeSearchTmp = false;
		if (this.columnsFilter == null)
			this.passesColumnFiltersTmp = null;
		else if (this.passesColumnFiltersTmp != null)
			Arrays.fill(this.passesColumnFiltersTmp, false);
	}

	private void debugNodeFlags(WebTreeNode node) {
		sb.append("\ntf- TreeFilter (WhereClause) ");
		sb.append("\tts- TreeSearch ");
		sb.append("\ttc- TreeColumn (Filters)");
		sb.append("\n");
		sb.append("\ttf: ").append(this.passesTreeFilterTmp);
		sb.append("\tts: ").append(this.passesTreeSearchTmp);
		sb.append("\ttc: [");
		if (this.passesColumnFiltersTmp != null) {
			int nfilters = this.passesColumnFiltersTmp.length;
			for (int i = 0; i < nfilters; i++)
				sb.append(this.passesColumnFiltersTmp[i]).append(", ");
		}
		sb.append("]");
		node.debugFilteredCount(sb);
	}

	private boolean isFilterFlagsAllPass(WebTreeNode node) {
		boolean pass = this.passesTreeFilterTmp && this.passesTreeSearchTmp;
		if (pass && this.passesColumnFiltersTmp != null) {
			int nfilters = this.passesColumnFiltersTmp.length;
			for (int i = 0; pass && i < nfilters; i++)
				pass &= this.passesColumnFiltersTmp[i];
		}
		return pass;
	}
	private void checkFilterFlagsIsPass(WebTreeNode node) {
		if (!this.passesTreeFilterTmp)
			this.passesTreeFilterTmp = this.passesTreeFilter(node);
		if (!this.passesTreeSearchTmp)
			this.passesTreeSearchTmp = this.passesTreeSearch(node);
		if (this.columnsFilter != null) {
			int nfilters = this.columnsFilter.size();
			for (int i = 0; i < nfilters; i++) {
				if (!this.passesColumnFiltersTmp[i])
					this.passesColumnFiltersTmp[i] = this.passesColumnFilter(node, i);
			}
		}
	}

	public boolean checkFilter(WebTreeNode node) {
		// 1) Reset temp flags
		this.resetFilterFlags();
		// 2) Walk up parents to calc filter flags
		WebTreeNode parent = node.getParent();
		this.checkFilterFlagsIsPass(node);
		while (parent != null && parent != this.root && !this.isFilterFlagsAllPass(node)) {
			//			this.debugNodeFlags(parent);
			this.checkFilterFlagsIsPass(parent);
			parent = parent.getParent();
		}

		// 3) Call checkFilterHelper on parent / child node
		parent = node.getParent();
		boolean pass = this.checkFilterH(parent, node);
		// 4) Walk up ancestors and update the filtered status on each ancestor
		if (parent != null) {
			node = parent;
			parent = node.getParent();
			while (parent != null && parent != this.root) {
				parent.setChildFilterered(node, !pass);
				node = parent;
				parent = parent.getParent();
			}
		}
		return pass;
	}

	//Return if a child passed
	private boolean checkFilterH(WebTreeNode parent, WebTreeNode node) {
		boolean pass = this.isFilterFlagsAllPass(node); // check if already pass
		// 1) Check if this node passes the filters
		if (!pass) {
			this.checkFilterFlagsIsPass(node);
			pass = this.isFilterFlagsAllPass(node);
		}

		// 2) If the filters don't all pass: walk through all children and check if the children passes
		if (!pass) {
			boolean childPass = false;
			// Child pass is true if any child passes
			boolean oldPassesTreeFilterTmp2 = this.passesTreeFilterTmp;
			boolean oldPassesTreeSearchTmp2 = this.passesTreeSearchTmp;
			boolean oldPassesColumnFiltersTmp2[] = this.passesColumnFiltersTmp == null ? null : new boolean[passesColumnFiltersTmp.length];
			if (this.passesColumnFiltersTmp != null)
				System.arraycopy(this.passesColumnFiltersTmp, 0, oldPassesColumnFiltersTmp2, 0, passesColumnFiltersTmp.length);

			for (WebTreeNode child : node.getChildrenAndFiltered()) {
				childPass |= this.checkFilterH(node, child);
				// Reset to old values
				this.passesTreeFilterTmp = oldPassesTreeFilterTmp2;
				this.passesTreeSearchTmp = oldPassesTreeSearchTmp2;
				if (oldPassesColumnFiltersTmp2 == null)
					this.passesColumnFiltersTmp = null;
				else
					System.arraycopy(oldPassesColumnFiltersTmp2, 0, this.passesColumnFiltersTmp, 0, passesColumnFiltersTmp.length);
			}

			// If childPass: apply pass to this node
			if (parent != null)
				parent.setChildFilterered(node, !childPass);
			return childPass;
		} else {
			// 3) If all filters pass: we walk down the children to the leaf and update the filter status
			// If pass: walk down children apply pass from that node
			for (WebTreeNode child : node.getChildrenAndFiltered())
				this.checkFilterH(node, child);

			if (parent != null)
				parent.setChildFilterered(node, false);
			return true;
		}
	}

	public boolean passesColumnFilter(WebTreeNode node, int filterIndex) {
		//		OH.assertTrue(node.isLeaf() || node.isRecursive());
		//		OH.assertTrue(node.isLeaf());
		//		OH.assertTrue(node.isLeaf() || node.isRecursive());
		return columnsFilter == null || (node != root && columnsFilter.shouldKeepAt(node, filterIndex));
	}
	@Deprecated
	public boolean passesColumnFilter(WebTreeNode node) {
		//		OH.assertTrue(node.isLeaf() || node.isRecursive());
		//		OH.assertTrue(node.isLeaf());
		//		OH.assertTrue(node.isLeaf() || node.isRecursive());
		return columnsFilter == null || (node != root && columnsFilter.shouldKeep(node));
	}
	public boolean passesTreeFilter(WebTreeNode node) {
		return treeFilter == null || (node != root && treeFilter.shouldKeep(node));
	}
	public boolean passesTreeSearch(WebTreeNode node) {
		return searchFilter == null || (node != root && searchFilter.shouldKeep(node));
	}

	public IntSet getSelected() {
		return this.selected;
	}
	@Override
	public void setActiveSelectedNode(WebTreeNode node) {
		int t = node == null ? -1 : node.getUid();
		if (t == this.activeSelectedNodeUid)
			return;
		this.activeSelectedNodeUid = t;
		this.fastWebTree.flagActiveChanged();
	}
	@Override
	public WebTreeNode getActiveSelectedNode() {
		return this.activeSelectedNodeUid == -1 ? null : this.getTreeNodeNoThrow(this.activeSelectedNodeUid);
	}
	public int getActiveSelectedNodeUid() {
		return this.activeSelectedNodeUid;
	}

	protected FastWebTreeColumnFilter getColumnsFilter() {
		return this.columnsFilter;
	}
	protected WebTreeFilter getTreesFilter() {
		return this.treeFilter;
	}
	protected WebTreeFilter getSearchFilter() {
		return this.searchFilter;
	}
	@Override
	public List<WebTreeNode> getSelectedNodes() {
		List<WebTreeNode> r = new ArrayList<WebTreeNode>(this.selected.size());
		for (IntIterator i = this.selected.iterator(); i.hasNext();)
			r.add(getTreeNode(i.nextInt()));
		Collections.sort(r, POSITION_SORTER);
		return r;
	}

	WebTreeNode[] tmp = new WebTreeNode[20];

	public void moveTo(int blockStart, int blockEnd, int destStart) {
		int size = blockEnd - blockStart + 1;
		if (destStart > blockStart) {
			if (size == 1) {
				WebTreeNode t = this.nodes.remove(blockStart);
				this.nodes.add(destStart - size, t);
			} else {
				if (this.tmp.length < size)
					this.tmp = new WebTreeNode[size];
				nodes.getAll(blockStart, blockStart + size, tmp, 0);
				nodes.removeAll(blockStart, blockStart + size);
				nodes.addAll(destStart - size, tmp, 0, size);
				for (int i = 0; i < size; i++)
					tmp[i] = null;
			}
		} else {
			if (size == 1) {
				WebTreeNode t = this.nodes.remove(blockStart);
				this.nodes.add(destStart, t);

			} else {
				if (this.tmp.length < size)
					this.tmp = new WebTreeNode[size];
				nodes.getAll(blockStart, blockStart + size, tmp, 0);
				nodes.removeAll(blockStart, blockStart + size);
				nodes.addAll(destStart, tmp, 0, size);
				for (int i = 0; i < size; i++)
					tmp[i] = null;
			}
		}
		this.fastWebTree.onRangeChanged(Math.min(blockStart, destStart), Math.max(blockStart, destStart) + size);
	}
	public void setWebTree(FastWebTree fastWebTree) {
		this.fastWebTree = fastWebTree;
	}

	public SparseList<WebTreeNode> getNodes() {
		return this.nodes;
	}
	public boolean hasSelected() {
		return !this.selected.isEmpty();
	}

	public void ensureVisible(WebTreeNode node) {
		this.fastWebTree.ensureVisible(node);
	}

}
