/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.f1.suite.web.tree.impl.BasicWebTreeManager;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.IntKeyMap.Node;
import com.f1.utils.structs.IntSet;
import com.f1.utils.structs.SkipList;
import com.f1.utils.structs.SkipListEntry;

public class WebTreeNode extends SkipListEntry {
	private boolean shouldShowInQuickFilter; //true for every non-leaf node. For leaf node, only true when leaf formatting is configured. The flags are set in AmiWebTreeGroupByFormatter.getDisplayValue()

	public boolean getShouldShowInQuickFilter() {
		return shouldShowInQuickFilter;
	}
	public void setShouldShowInQuickFilter(boolean b) {
		this.shouldShowInQuickFilter = b;
	}

	public static class InnerIterator implements Iterator<WebTreeNode> {

		private Iterator<Inner> inner;

		public InnerIterator(Iterator<Inner> iterator) {
			this.inner = iterator;
		}

		@Override
		public boolean hasNext() {
			return this.inner.hasNext();
		}

		@Override
		public WebTreeNode next() {
			return this.inner.next().getNode();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("remove");
		}
	}

	public static class InnerIterable implements Iterable<WebTreeNode> {

		private Iterable<Inner> inner;

		public InnerIterable(Iterable<Inner> inner) {
			this.inner = inner;
		}

		@Override
		public Iterator<WebTreeNode> iterator() {
			return new InnerIterator(this.inner.iterator());
		}

	}

	private static final Object NULL_KEY = new Object();
	public static final int DEFAULT_GROUP_INDEX = -1; //-1 indicates uninit

	private String name;
	private String iconCssClass;
	private String iconCssStyle;
	final private int uid;
	private SkipList<WebTreeNode.Inner> children = null;
	private IntKeyMap<WebTreeNode.Inner> filtered = null;
	private ArrayList<WebTreeNode.Inner> movedChildren = null;
	private BasicWebTreeManager manager;
	private WebTreeNode parent;
	private WebTreeNode groupRoot;
	private boolean isExpanded;
	private boolean isCascadeCheck;
	private int depth = 0;//0 indicates root
	private int groupIndex = DEFAULT_GROUP_INDEX; //-1 indicates uninit
	private Map<Object, WebTreeNode> groupChildrenByKey = null;

	//	private int absPosition = -1;//-1 indicates not visible
	private int allChildrenCount = 1;
	private boolean isRollup;//if true, then its skipped for 
	private boolean selected;
	private Object data;
	private String cssClass;
	private Object key;
	private boolean checked = false;
	final private Inner inner;
	private boolean isUnsorted = true;
	private boolean isRecursive = false;

	public static class Inner extends SkipListEntry {

		private WebTreeNode node;

		public Inner(WebTreeNode node) {
			this.node = node;
		}

		public WebTreeNode getNode() {
			return this.node;
		}
	}

	public WebTreeNode(BasicWebTreeManager manager, int uid, String name, boolean expanded) {
		this.uid = uid;
		this.manager = manager;
		this.isExpanded = expanded;
		this.name = name;
		this.isCascadeCheck = true;
		this.inner = new Inner(this);
	}

	public String getName() {
		return name;
	}

	public int getUid() {
		return uid;
	}

	public int getChildrenCount() {
		return children == null ? 0 : children.size();
	}

	public void removeChild(WebTreeNode child) {
		this.removeChild(child, true);
	}
	public void removeChild(WebTreeNode child, boolean deselectRecursively) {
		if (child.getParent() != this)
			throw new NoSuchElementException("tree node uid: " + uid);
		if (child.isFiltered()) {
			this.filtered.removeOrThrow(child.getUid());
			child.setParent(null);
		} else {
			int pos = child.getPosition();
			if (pos == -1)
				this.filtered.removeOrThrow(child.getUid());
			WebTreeNode.Inner childInner = children.remove(pos);
			if (childInner == null)
				throw new NoSuchElementException("tree node uid: " + uid);
			OH.assertEqIdentity(child, childInner.node);
			//			WebTreeNode child = childInner.node;
			manager.onChildRemoved(child, deselectRecursively);
		}
		if (child.getKey() != null)
			onChildKeyChanged(child, child.getKey(), null);
		child.setParent(null);
		incChildCount(-child.getAllChildrenCount(), -child.getAllChildrenCheckedCount());
		// Removed because this is causing nodes to minimize
		//if (this.getAllChildrenCount() == 1 && this.isExpandable == true && this != this.getTreeManager().getRoot())
		//	// Not sure why all childrenCount is init to 1, but one means no children, 
		//	this.setIsExpandable(false);

	}
	private void incChildCount(int childCountDelta, int checkedCountDelta) {
		if (childCountDelta == 0 && checkedCountDelta == 0)
			return;
		for (WebTreeNode p = this; p != null; p = p.getParent()) {
			if (childCountDelta != 0)
				p.setAllChildrenCount(p.getAllChildrenCount() + childCountDelta);
			if (checkedCountDelta != 0)
				p.setAllChildrenCheckedCount(p.getAllChildrenCheckedCount() + checkedCountDelta);
			if (this.getIsCascadeCheck() && childCountDelta != 0 && childCountDelta != checkedCountDelta && p.getHasCheckbox())
				p.setChecked(p.getAllChildrenCount() == p.getAllChildrenCheckedCount() + (p.getChecked() ? 0 : 1));
		}
	}

	public void addChild(WebTreeNode child) {
		this.addChild(child, true);
	}

	public void addChild(WebTreeNode child, boolean checkFilter) {
		this.isExpandable = true;
		child.setParent(this);
		if (children == null)
			children = new SkipList<WebTreeNode.Inner>(1000);
		children.add(child.inner);
		ensureSorted(child);
		manager.onChildAdded(child);
		if (child.getKey() != null)
			onChildKeyChanged(child, null, child.getKey());
		incChildCount(child.getAllChildrenCount(), child.getAllChildrenCheckedCount());
	}

	public WebTreeNode getChildAt(int location) {
		if (children == null)
			throw new IndexOutOfBoundsException("location: " + location);

		return children.get(location).node;
	}

	public Iterable<WebTreeNode> getChildren() {
		if (CH.isEmpty(this.children))
			return Collections.EMPTY_LIST;
		return new InnerIterable(children);
	}
	public Iterable<WebTreeNode> getFilteredChildren() {
		if (CH.isEmpty(this.filtered))
			return Collections.EMPTY_LIST;
		return new InnerIterable(this.filtered.values());
	}

	public Iterable<WebTreeNode> getAllChildren() {
		if (CH.isEmpty(this.children))
			return Collections.EMPTY_LIST;
		List<WebTreeNode.Inner> sink = new ArrayList<WebTreeNode.Inner>();
		getAllChildren(this, sink);
		return new InnerIterable(sink);
	}
	public Iterable<WebTreeNode> getAllFilteredChildren() {
		if (CH.isEmpty(this.childrenByKey))
			return Collections.EMPTY_LIST;
		List<WebTreeNode.Inner> sink = new ArrayList<WebTreeNode.Inner>();
		getAllFilteredChildren(this, sink);
		return new InnerIterable(sink);
	}

	private void getAllChildren(WebTreeNode node, List<WebTreeNode.Inner> sink) {
		Iterable<WebTreeNode.Inner> childNodes = node.children;
		if (childNodes != null)
			for (WebTreeNode.Inner childNode : childNodes) {
				sink.add(childNode);
				getAllChildren(childNode.node, sink);
			}
	}

	private void getAllFilteredChildren(WebTreeNode node, List<WebTreeNode.Inner> sink) {
		IntKeyMap<Inner> childNodes = node.filtered;
		if (childNodes != null)
			for (WebTreeNode.Inner childNode : childNodes.values()) {
				sink.add(childNode);
				getAllFilteredChildren(childNode.node, sink);
			}
	}

	public void setParent(WebTreeNode parent) {
		if (this.parent != null && parent != null) {
			if (this.parent == parent)
				return;
			throw new RuntimeException("already in a tree!");
		}
		this.parent = parent;
		if (parent != null)
			setDepth(parent.getDepth() + 1);
	}
	private void setDepth(int depth) {
		if (this.depth == depth)
			return;
		this.depth = depth;
		if (this.children != null)
			for (int i = 0, l = this.children.size(); i < l; i++)
				children.get(i).node.setDepth(depth + 1);
		if (this.filtered != null)
			for (Inner i : this.filtered.values())
				i.node.setDepth(depth + 1);
	}

	public WebTreeNode getParent() {
		return parent;
	}

	public boolean getIsExpanded() {
		return isExpanded;
	}

	public boolean getIsCascadeCheck() {
		return this.isCascadeCheck;
	}

	public WebTreeNode setIsCascadeCheck(boolean isCascadeCheck) {
		this.isCascadeCheck = isCascadeCheck;
		return this;
	}

	public WebTreeNode setIsExpanded(boolean isExpanded) {
		if (this.isExpanded == isExpanded)
			return this;
		this.isExpanded = isExpanded;
		manager.onExpandedChanged(this);
		return this;
	}

	public WebTreeNode setName(String name) {
		if (OH.eq(this.name, name))
			return this;
		this.name = name;
		if (parent != null)
			manager.onNodeDataChanged(this);
		return this;
	}
	public WebTreeNode setIcon(String icon) {
		if (OH.eq(this.iconCssClass, icon))
			return this;
		this.iconCssClass = icon;
		if (parent != null)
			manager.onNodeDataChanged(this);
		return this;
	}
	public WebTreeNode setCssClass(String cssClass) {
		if (OH.eq(this.cssClass, cssClass))
			return this;
		this.cssClass = cssClass;
		if (parent != null)
			manager.onNodeDataChanged(this);
		return this;
	}

	public int getDepth() {
		return depth;
	}

	public int getPosition() {
		if (this.parent == null)
			return 0;
		return inner.getLocation();
	}

	public int getAbsolutePosition() {
		return super.getLocation();
	}

	public String toString() {
		return name;
	}

	public int getAllChildrenCount() {
		return allChildrenCount;
	}

	public void setAllChildrenCount(int count) {
		if (allChildrenCount == count)
			return;
		int oldCount = allChildrenCount;
		allChildrenCount = count;
		if ((oldCount == 0) != (count == 0))
			manager.onNodeDataChanged(this);
	}

	public void setSelected(boolean selected) {
		if (selected == this.selected)
			return;
		if (selected == true && !isSelectable)
			throw new IllegalStateException();
		this.selected = selected;
		manager.onSelectionChanged(this);
	}

	public boolean getSelected() {
		return selected;
	}

	public Object getData() {
		return data;
	}

	public WebTreeNode setData(Object data) {
		this.data = data;
		return this;
	}

	public String getCssClass() {
		return cssClass;
	}

	public String getIcon() {
		return iconCssClass;
	}

	public Object getKey() {
		return key;
	}

	public Object getKeyOrNull() {
		return key == NULL_KEY ? null : key;
	}

	public WebTreeNode setKey(Object key) {
		if (key == null)
			key = NULL_KEY;
		if (OH.eq(this.key, key))
			return this;
		Object oldKey = this.key;
		this.key = key;
		if (parent != null)
			parent.onChildKeyChanged(this, oldKey, key);
		if (groupRoot != null)
			groupRoot.onGroupChildKeyChanged(this, oldKey, key);

		return this;
	}

	private Map<Object, WebTreeNode> childrenByKey = null;
	private boolean isExpandable;
	private boolean isSelectable = true;
	private boolean hasCheckbox;

	public void onChildKeyChanged(WebTreeNode child, Object oldKey, Object newKey) {
		if (OH.eq(oldKey, newKey))
			return;
		if (childrenByKey == null)
			childrenByKey = new HashMap<Object, WebTreeNode>();
		if (newKey != null)
			CH.putOrThrow(childrenByKey, newKey, child);
		if (oldKey != null)
			CH.removeOrThrow(childrenByKey, oldKey);
	}

	public WebTreeNode getChildByKey(Object key) {
		if (key == null)
			key = NULL_KEY;
		return childrenByKey == null ? null : childrenByKey.get(key);
	}

	public boolean getIsExpandable() {
		return isExpandable;
	}

	public WebTreeNode setIsExpandable(boolean isExpandable) {
		if (this.isExpandable == isExpandable)
			return this;
		this.isExpandable = isExpandable;
		// if prev expandable was true and is expanded
		if (!isExpandable && this.isExpanded)
			setIsExpanded(false);
		else
			this.manager.onExpandedChanged(this);
		return this;
	}

	public boolean getIsSelectable() {
		return this.isSelectable;
	}

	public WebTreeNode setIsSelectable(boolean isSelectable) {
		if (this.isSelectable == isSelectable)
			return this;
		this.isSelectable = isSelectable;
		if (!isSelectable)
			setSelected(false);
		return this;
	}

	public WebTreeNode setHasCheckbox(boolean b) {
		if (this.hasCheckbox == b)
			return this;
		this.hasCheckbox = b;
		if (!b)
			setChecked(false);
		if (this.parent != null)
			manager.onNodeDataChanged(this);
		return this;
	}

	public boolean getHasCheckbox() {
		return this.hasCheckbox;
	}

	public void setChecked(boolean b) {
		if (this.checked == b)
			return;
		if (!hasCheckbox && b)
			throw new IllegalStateException();
		this.checked = b;
		incChildCount(0, b ? 1 : -1);
		if (!this.isCascadeCheck)
			manager.onCheckedChanged(this);
	}

	public void setCheckedNoFire(boolean b) {
		if (this.checked == b)
			return;
		if (!hasCheckbox && b)
			throw new IllegalStateException();
		this.checked = b;

	}
	public boolean getChecked() {
		return this.checked;
	}

	int childrenThatAreChecked = 0;

	public int getAllChildrenCheckedCount() {
		return childrenThatAreChecked;
	}

	public void setAllChildrenCheckedCount(int count) {
		if (count == this.childrenThatAreChecked)
			return;
		this.childrenThatAreChecked = count;
		if (this.isCascadeCheck)
			manager.onCheckedChanged(this);
	}

	public void sort() {
		//Note made this change because isUnsorted has a dual use purpose which is causing confusion:
		// It's being used to say if the sorting has already been calculated and
		// If a sort is being applied to this node.
		// Problem is it's not sorting when it should be sorting, and we will need to optimize this later
		// The change I am making here is based on the ensureSorted function
		if (this.children == null || this.children.size() == 0 || getAbsolutePosition() == -1 || manager.getComparator() == null)
			return;
		Collections.sort(this.children, manager.getInnerComparator());
		for (int i = 0; i < this.children.size(); i++) {
			WebTreeNode c = this.children.get(i).node;
			c.sort();
		}
		isUnsorted = false;
	}

	public void ensureSorted(WebTreeNode child) {
		if (isUnsorted || manager.getComparator() == null)
			return;
		if (getAbsolutePosition() == -1) {
			isUnsorted = true;
			return;
		}
		int oldPos = child.getPosition();
		Comparator<Inner> c = this.manager.getInnerComparator();
		int s = CH.isSorted(children, oldPos, c);
		if (s == 0)
			return;
		children.remove(oldPos);
		int newPos = CH.insertSorted(children, child.inner, c, false);
		int top = child.getAbsolutePosition();
		if (top != -1) {
			WebTreeNode lastNode = child.getBottomVisibleChildNode();
			int bottom = lastNode.getAbsolutePosition();
			if (newPos > oldPos) {
				//moved down
				int target = children.get(newPos - 1).node.getBottomVisibleChildNode().getAbsolutePosition();
				this.manager.moveTo(top, bottom, target + 1);
			} else {
				//moved up
				int target = children.get(newPos + 1).node.getAbsolutePosition();
				this.manager.moveTo(top, bottom, target);
			}
		}
	}

	public String getIconCssStyle() {
		return this.iconCssStyle;
	}

	public WebTreeNode setIconCssStyle(String style) {
		if (OH.eq(this.iconCssStyle, style))
			return this;
		this.iconCssStyle = style;
		manager.onNodeDataChanged(this);
		return this;
	}

	public int getGroupIndex() {
		return this.groupIndex;
	}

	public WebTreeNode setGroupIndex(int groupIndex) {
		this.groupIndex = groupIndex;
		return this;
	}

	public boolean getIsGroupRoot() {
		return this.groupChildrenByKey != null && this.groupChildrenByKey.size() > 0;
	}

	public boolean hasGroupAncestor(Object key, WebTreeNode groupRoot) {
		if (key == null)
			key = NULL_KEY;
		if (groupRoot == null)
			return false;

		WebTreeNode ancestor = this.getParent();
		while (ancestor != null && ancestor != groupRoot) {
			if (OH.eq(ancestor.getKey(), key))
				return true;
			ancestor = ancestor.getParent();
		}
		return false;

	}

	public void addGroupChild(WebTreeNode child) {
		if (groupChildrenByKey == null)
			groupChildrenByKey = new HashMap<Object, WebTreeNode>();

		Object key = child.getKey();
		if (key == null)
			key = NULL_KEY;
		CH.putOrThrow(this.groupChildrenByKey, key, child);
		child.setGroupRoot(this);

	}
	public WebTreeNode removeGroupChild(WebTreeNode child) {
		if (this.groupChildrenByKey == null || child.getGroupRoot() != this)
			throw new NoSuchElementException("tree node uid: " + child);
		if (child.getKey() != null)
			onGroupChildKeyChanged(child, child.getKey(), null);
		child.setGroupRoot(null);
		return child;
	}

	public void onGroupChildKeyChanged(WebTreeNode child, Object oldKey, Object newKey) {
		if (OH.eq(oldKey, newKey))
			return;
		if (this.groupChildrenByKey == null)
			groupChildrenByKey = new HashMap<Object, WebTreeNode>();
		if (newKey != null)
			CH.putOrThrow(this.groupChildrenByKey, key, child);
		if (oldKey != null)
			CH.removeOrThrow(this.groupChildrenByKey, oldKey);
	}

	public WebTreeNode getGroupChildByKey(Object key) {
		if (key == null)
			key = NULL_KEY;
		return groupChildrenByKey == null ? null : groupChildrenByKey.get(key);
	}

	public void clearGroupChildren() {
		if (this.groupChildrenByKey == null)
			return;
		this.groupChildrenByKey.clear();
	}

	public WebTreeNode getGroupRoot() {
		return groupRoot;
	}

	public void setGroupRoot(WebTreeNode groupRoot) {
		if (this.groupRoot != null && groupRoot != null) {
			if (this.groupRoot == groupRoot)
				return;
			throw new RuntimeException("already in a tree!");
		}
		this.groupRoot = groupRoot;
	}

	public WebTreeManager getTreeManager() {
		return this.manager;
	}

	public WebTreeNode getBottomVisibleChildNode() {
		if (getAbsolutePosition() == -1)
			return null;
		WebTreeNode r = this;
		while (r.getIsExpanded() && r.getChildrenCount() > 0)
			r = r.getChildAt(r.getChildrenCount() - 1);
		return r;
	}

	public boolean isUnsorted() {
		return this.isUnsorted;
	}

	public void onComparatorChanged() {
		this.isUnsorted = true;
		if (children != null) {
			for (int i = 0; i < this.children.size(); i++) {
				WebTreeNode c = this.children.get(i).node;
				c.onComparatorChanged();
			}
		}
	}

	public void addFilteredChild(WebTreeNode r) {
		if (this.filtered == null)
			this.filtered = new IntKeyMap<WebTreeNode.Inner>();
		this.filtered.put(r.getUid(), r.inner);
	}

	public boolean isFiltered() {
		int position = this.getPosition();
		return position == -1;
	}

	private boolean passesColumnFilter = true;//only used if this is a leaf
	private boolean passesTreeFilter = true;//does this row pass the tree filter
	private boolean passesTreeLineageFilter = true;//does this row or any parent pass the tree filter
	private boolean passesTreeSearch = true;//does this row pass the search filter
	private boolean passesTreeLineageSearch = true;//does this row or any parent pass the search filter

	//So my node's data changed which might cause a filter change

	public void debugFilteredCount(StringBuilder sb) {
		WebTreeNodeFormatter nodeFormatter = manager.getFormatter();
		//		sb.append("inc ").append(this.unfilteredChildrenCount);
		//		sb.append(" \tAP - ").append(this.passesFromAncestor).append(" \tDP - ").append(this.passesFromDescendent);

		sb.append("\t");
		nodeFormatter.formatToText(this, sb);
		sb.append("\t");
		System.out.println(SH.toStringAndClear(sb));
	}

	public boolean shouldKeep() {
		return this.passesTreeFilter && this.passesTreeSearch && this.passesColumnFilter;
	}

	public Iterable<WebTreeNode> getChildrenAndFiltered() {
		final List<WebTreeNode> l = new ArrayList<WebTreeNode>(this.getChildrenCount() + this.getFilteredChildrenCount());
		CH.l(l, getChildren());
		CH.l(l, getFilteredChildren());
		return l;
	}

	public void onFilterChanged() {
		this.passesTreeFilter = manager.passesTreeFilter(this);
		this.passesTreeSearch = manager.passesTreeSearch(this);
		if (parent == null) {
			this.passesTreeLineageFilter = this.passesTreeFilter;
			this.passesTreeLineageSearch = this.passesTreeSearch;
			if (isLeaf()) {
				this.passesColumnFilter = (passesTreeSearch && manager.passesColumnFilter(this));
			} else if (this.isRecursive) {
				this.passesColumnFilter = (passesTreeSearch && manager.passesColumnFilter(this));
				for (WebTreeNode i : getChildrenAndFiltered())
					i.onFilterChanged();
			} else {
				for (WebTreeNode i : getChildrenAndFiltered())
					i.onFilterChanged();
			}
		} else {
			this.passesTreeLineageFilter = this.passesTreeFilter || this.parent.passesTreeLineageFilter;
			this.passesTreeLineageSearch = this.passesTreeSearch || this.parent.passesTreeLineageSearch;
			if (isLeaf()) {
				this.passesColumnFilter = (passesTreeSearch || this.parent.passesTreeLineageSearch) && manager.passesColumnFilter(this);
				this.parent.setChildFilterered(this, !passesColumnFilter || !passesTreeLineageFilter || !passesTreeLineageSearch);
			} else if (this.isRecursive) {
				this.passesColumnFilter = (passesTreeSearch || this.parent.passesTreeLineageSearch) && manager.passesColumnFilter(this);
				for (WebTreeNode i : getChildrenAndFiltered())
					i.onFilterChanged();
				this.parent.setChildFilterered(this, !this.hasChildrenLeafs());
			} else {
				for (WebTreeNode i : getChildrenAndFiltered())
					i.onFilterChanged();
				this.parent.setChildFilterered(this, getChildrenCount() == 0);
				//				this.parent.setChildFilterered(this, this.isRecursive ? !this.hasChildrenLeafs() : (getChildrenCount() == 0));
			}
		}
	}

	public int getFilteredChildrenCount() {
		return this.filtered == null ? 0 : this.filtered.size();
	}

	public void setChildFilterered(WebTreeNode child, boolean b) {
		// Parent this
		//   - child a
		//   - child b
		//   - child c
		// Filter b true // need to remove b from this
		// 1) Remove the child from children
		// 2) Add to filtered map to save for later
		// 3) ?? manager.onChildRemoved
		// 4) Fire up the chain tell the parent to remove this if it's filtered
		if (b == child.isFiltered())
			return;
		OH.assertEqIdentity(child.getParent(), this);
		if (b) {
			// TODO: remove and replace with a listener.. manager.beforeChild...
			if (child.isRecursive)
				child.setFilteredMoveChildren();
			this.children.remove(child.inner);
			if (this.filtered == null)
				this.filtered = new IntKeyMap<WebTreeNode.Inner>();
			this.filtered.put(child.getUid(), child.inner);
			manager.onChildRemoved(child);
			if (parent != null && this.children.size() == 0 && !isFiltered())
				this.parent.setChildFilterered(this, true);
		} else {
			this.filtered.removeOrThrow(child.getUid());
			if (children == null)
				children = new SkipList<WebTreeNode.Inner>(1000);
			this.children.add(child.inner);
			if (child.isRecursive)
				child.resetFilteredMovedChildren();
			//			this.isUnsorted = true;
			manager.onChildAdded(child);
			if (parent != null && this.children.size() == 1 && isFiltered())
				this.parent.setChildFilterered(this, false);
		}
		manager.onChildFilteredChanged(child, b);
	}

	public boolean isLeaf() {
		return (this.children == null || this.children.isEmpty()) && (this.filtered == null || this.filtered.isEmpty());
	}

	public boolean isSelected(boolean checkParents) {
		IntSet selected2 = manager.getSelected();
		if (selected2.isEmpty())
			return false;
		if (selected2.contains(this.getUid()))
			return true;
		if (checkParents)
			for (WebTreeNode p = this.parent; p != null; p = p.getParent())
				if (selected2.contains(getUid()))
					return true;
		return false;
	}

	public boolean isRecursive() {
		return isRecursive;
	}

	public void setRecursive(boolean isRecursive) {
		this.isRecursive = isRecursive;
	}

	private void setFilteredMoveChildren() {
		if (this.movedChildren == null)
			this.movedChildren = new ArrayList<WebTreeNode.Inner>();

		List<WebTreeNode> children = new ArrayList<WebTreeNode>(this.getChildrenCount());
		CH.l(children, getChildren());
		if (this.filtered != null)
			for (Node<Inner> node : this.filtered) {
				WebTreeNode child = node.getValue().node;
				this.movedChildren.add(child.inner);
			}
		for (int i = children.size() - 1; i >= 0; i--) {
			WebTreeNode child = children.get(i);
			this.removeChild(child);
			this.getGroupRoot().addChild(child);
			this.movedChildren.add(child.inner);
		}
	}
	private void resetFilteredMovedChildren() {
		if (this.movedChildren == null || this.movedChildren.size() == 0)
			return;
		for (int i = 0; i < this.movedChildren.size(); i++) {
			WebTreeNode child = this.movedChildren.get(i).getNode();
			WebTreeNode parentNode = child.getParent();
			if (child.isFiltered() || (this.filtered != null && this.filtered.containsKey(child.getUid())))
				continue;
			if (this == parentNode)
				continue;
			parentNode.removeChild(child);
			this.addChild(child);
			parentNode.isUnsorted = true;

		}
		this.isUnsorted = true;
		this.movedChildren.clear();
	}

	public boolean hasChildrenLeafs() {
		if ((this.children == null || this.children.isEmpty()))
			return false;
		for (Inner child : this.children) {
			if (child.getNode().isLeaf())
				return true;
		}

		return false;
	}

	public void ensureVisible() {
		this.manager.ensureVisible(this);
	}

	public void removeMe() {
		WebTreeNode o = this.getParent();
		if (o != null) {
			o.removeChild(this);
			if (o.getChildrenAndFilteredCount() == 0)
				o.setIsExpandable(false);
		}
	}

	public void setIconFile(String icon) {
		if (SH.is(icon))
			setIconCssStyle("_bgi=url('" + icon + "')");
		else
			setIconCssStyle(null);
	}

	public int getChildrenAndFilteredCount() {
		return this.getChildrenCount() + getFilteredChildrenCount();
	}

}
