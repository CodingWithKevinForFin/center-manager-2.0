package com.f1.suite.web.portal.impl;

import java.util.Arrays;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.suite.web.tree.WebTreeNode;
import com.f1.utils.AH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.impl.ArrayHasher;

public class TreeStateCopier {
	private static final Logger log = LH.get();

	private static class Entry {
		private Object[] key;
		private boolean isExpanded;
		private boolean isSelected;
		private boolean isChecked;

		public Entry(Object[] key, WebTreeNode node) {
			this.key = key;
			this.isExpanded = node.getIsExpanded();
			this.isSelected = node.getSelected();
			this.isChecked = node.getChecked();
		}

		public void apply(WebTreeNode node) {
			if (node.getIsExpandable())
				node.setIsExpanded(this.isExpanded);
			if (node.getIsSelectable())
				node.setSelected(this.isSelected);
			if (node.getHasCheckbox())
				node.setChecked(this.isChecked);
		}
	}

	private TreeStateCopierIdGetter idGetter;
	Map<Object[], Entry> values = new HasherMap<Object[], Entry>(ArrayHasher.INSTANCE);
	private FastTreePortlet tree;
	private Object[] activeKey;

	public TreeStateCopier(FastTreePortlet tree, TreeStateCopierIdGetter getter) {
		this.tree = tree;
		this.idGetter = getter;
		reset();
	}

	public void reset() {
		values.clear();
		activeKey = null;
		WebTreeNode active = this.tree.getTreeManager().getActiveSelectedNode();
		getState(OH.EMPTY_OBJECT_ARRAY, tree.getRoot(), active);
	}

	private void getState(Object[] parent, WebTreeNode node, WebTreeNode active) {
		Object obj = this.idGetter.getId(node);
		Object[] key = Arrays.copyOf(parent, parent.length + 1);
		key[key.length - 1] = obj;
		Entry entry = new Entry(key, node);
		values.put(entry.key, entry);
		if (active == node)
			this.activeKey = key;
		for (WebTreeNode i : node.getChildren())
			getState(entry.key, i, active);
	}

	public void reapplyState() {
		if (this.values.isEmpty())
			return;
		setState(OH.EMPTY_OBJECT_ARRAY, tree.getRoot());
	}

	private void setState(Object[] parent, WebTreeNode node) {
		Object obj = this.idGetter.getId(node);
		Object[] key = Arrays.copyOf(parent, parent.length + 1);
		key[key.length - 1] = obj;
		Entry entry = values.get(key);
		if (entry != null)
			entry.apply(node);
		if (AH.eq(activeKey, key))
			tree.getTreeManager().setActiveSelectedNode(node);
		for (WebTreeNode i : node.getChildren())
			setState(key, i);
	}

}
