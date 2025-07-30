package com.f1.suite.web.tree.impl;

import java.util.List;
import java.util.Set;

import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.TextMatcher;

public class WebTreeHelper {

	private WebTreeHelper() {
	}

	public static void setAllExpanded(WebTreeNode node, boolean b) {
		int cnt = node.getChildrenCount();
		if (cnt == 0)
			return;
		node.setIsExpanded(b);
		for (int i = 0; i < cnt; i++)
			setAllExpanded(node.getChildAt(i), b);
	}
	public static void toggleCheckbox(WebTreeNode node) {
		if (node.getIsCascadeCheck()) {
			if (!node.getChecked() && node.getAllChildrenCheckedCount() > 0)
				setAllChecked(node, false);
			else
				setAllChecked(node, !node.getChecked());
		} else
			node.setChecked(!node.getChecked());
	}
	public static void setAllChecked(WebTreeNode node, boolean b) {
		setAllChecked2(node, b);
		for (WebTreeNode n = node.getParent(); n != null && n.getHasCheckbox(); n = n.getParent())
			n.setChecked(n.getAllChildrenCount() == n.getAllChildrenCheckedCount() + (n.getChecked() ? 0 : 1));
	}
	private static void setAllChecked2(WebTreeNode node, boolean b) {
		if (node.getHasCheckbox())
			node.setChecked(b);
		for (WebTreeNode i : node.getChildren())
			setAllChecked(i, b);
	}

	public static boolean isInSelection(WebTreeNode node) {
		while (node != null)
			if (node.getSelected())
				return true;
			else
				node = node.getParent();
		return false;
	}

	public static void getAllChildren(WebTreeNode parent, List<WebTreeNode> sink) {
		for (WebTreeNode child : parent.getChildren()) {
			sink.add(child);
			getAllChildren(child, sink);
		}
	}

	public static void getAllLeafs(WebTreeNode parent, List<WebTreeNode> sink) {
		if (parent.getChildrenCount() != 0) {
			for (WebTreeNode child : parent.getChildren())
				getAllLeafs(child, sink);
		} else
			sink.add(parent);
	}
	static public void getChecked(WebTreeNode node, List<WebTreeNode> sink, boolean onlyLeafs) {
		if (node.getChecked() && (!onlyLeafs || node.getChildrenCount() == 0))
			sink.add(node);
		for (int i = 0; i < node.getChildrenCount(); i++)
			getChecked(node.getChildAt(i), sink, onlyLeafs);
	}

	static public void search(WebTreeNode node, TextMatcher matcher) {
		if (node.getIsSelectable()) {
			if (node.getName() != null && matcher.matches(node.getName())) {
				ensureVisible(node);
				node.setSelected(true);
			} else
				node.setSelected(false);
		}
		for (int i = 0; i < node.getChildrenCount(); i++)
			search(node.getChildAt(i), matcher);
	}
	static public void search(WebTreeNode node, Set<String> names) {
		if (node.getName() != null && names.contains(node.getName())) {
			ensureVisible(node);
			node.setSelected(true);
		} else
			node.setSelected(false);
		for (int i = 0; i < node.getChildrenCount(); i++)
			search(node.getChildAt(i), names);
	}

	static public void ensureVisible(WebTreeNode node) {
		for (WebTreeNode p = node.getParent(); p != null; p = p.getParent()) {
			p.setIsExpanded(true);
		}
	}

	public static void autoExpandUntilMultipleNodes(WebTreeNode node) {
		node.setIsExpanded(true);
		while (node.getChildrenCount() == 1) {
			node = node.getChildAt(0);
			node.setIsExpanded(true);
		}
	}

	public static void autoCollapseChildren(WebTreeNode node, boolean force) {
		if (node.getIsExpanded()) {
			node.setIsExpanded(false);
		} else if (!force)
			return;
		for (int i = 0, l = node.getChildrenCount(); i < l; i++)
			autoCollapseChildren(node.getChildAt(i), force);
	}

	public static void removeCssClass(WebTreeNode node, String string, boolean recurse) {
		node.setCssClass(WebHelper.removeCssClass(node.getCssClass(), string));
		if (recurse)
			for (WebTreeNode i : node.getChildren())
				removeCssClass(i, string, recurse);
	}

	public static void applyCssClass(WebTreeNode node, String string, boolean recurse) {
		node.setCssClass(WebHelper.applyCssClass(node.getCssClass(), string));
		if (recurse)
			for (WebTreeNode i : node.getChildren())
				applyCssClass(i, string, recurse);
	}

	public static WebTreeNode getNext(WebTreeNode cur) {
		if (cur.getChildrenCount() > 0)
			return cur.getChildAt(0);
		for (;;) {
			WebTreeNode p = cur.getParent();
			if (p == null)
				return null;
			else if (p.getChildrenCount() > cur.getPosition() + 1)
				return p.getChildAt(cur.getPosition() + 1);
			else
				cur = p;
		}
	}
	public static WebTreeNode getPrevious(WebTreeNode cur) {
		if (cur.getPosition() > 0)
			return getLast(cur.getParent().getChildAt(cur.getPosition() - 1));
		return cur.getParent();
	}
	public static WebTreeNode getLast(WebTreeNode root) {
		while (root.getChildrenCount() > 0)
			root = root.getChildAt(root.getChildrenCount() - 1);
		return root;
	}

}
