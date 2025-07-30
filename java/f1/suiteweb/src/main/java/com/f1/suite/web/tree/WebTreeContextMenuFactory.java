/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.tree;

import java.util.List;

import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.tree.impl.FastWebTree;

public interface WebTreeContextMenuFactory {

	public WebMenu createMenu(FastWebTree fastWebTree, List<WebTreeNode> selected);

	@Deprecated
	public boolean formatNode(WebTreeNode node, StringBuilder sink);
}
