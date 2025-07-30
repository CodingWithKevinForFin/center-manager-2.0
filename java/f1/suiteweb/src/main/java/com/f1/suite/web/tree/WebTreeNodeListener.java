/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.tree;

public interface WebTreeNodeListener {

	public void onNodeAdded(WebTreeNode node);
	public void onNodeRemoved(WebTreeNode node);
	public void onStyleChanged(WebTreeNode node);
	public void onExpanded(WebTreeNode node);
	public void onCheckedChanged(WebTreeNode node);
	public void onFilteredChanged(WebTreeNode child, boolean isFiltered);
}
