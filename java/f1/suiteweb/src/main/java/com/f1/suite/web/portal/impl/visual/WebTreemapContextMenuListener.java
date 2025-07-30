/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.portal.impl.visual;

import com.f1.utils.structs.IntKeyMap;

public interface WebTreemapContextMenuListener {

	public void onContextMenu(TreemapPortlet treemap, String action, TreemapNode node);

	public void onNodeClicked(TreemapPortlet portlet, TreemapNode node, int btn);

	public void onSelectionChanged(TreemapPortlet treemapPortlet, IntKeyMap<TreemapNode> selected, boolean userDriven);
}
