/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.tree;

import java.util.Comparator;
import java.util.List;

public interface WebTreeManager {

	int ROOT_UID = 0;

	// ==== Add/Remove Nodes ====
	public WebTreeNode createNode(String name, WebTreeNode parent, boolean expanded, Object data);
	public WebTreeNode createNode(String name, WebTreeNode parent, boolean expanded);
	WebTreeNode buildNode(String name, boolean expanded, boolean isRollup, Object data);
	void addNode(WebTreeNode parent, WebTreeNode r);
	void removeNode(WebTreeNode child);
	public void clear();

	// ==== Add/Remove Listener ====
	public void addListener(WebTreeNodeListener treeListener);
	public void removeListener(WebTreeNodeListener treeListener);

	// ==== Let Tree know data has changed, which could effect sorting, filtering, formatting ====
	public void onNodeDataChanged(WebTreeNode webTreeNode);

	public void setAllExpanded(boolean isExpanded);

	void setComparator(Comparator<WebTreeNode> comparator);
	Comparator<WebTreeNode> getComparator();

	//get Nodees
	public WebTreeNode getRoot();
	public WebTreeNode getTreeNode(int uid);
	public WebTreeNode getTreeNodeNoThrow(int uid);
	public WebTreeNode getActiveSelectedNode();
	public void setActiveSelectedNode(WebTreeNode node);
	public List<WebTreeNode> getSelectedNodes();
}
