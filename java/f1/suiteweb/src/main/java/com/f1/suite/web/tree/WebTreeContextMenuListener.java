/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.tree;

import com.f1.suite.web.fastwebcolumns.FastWebColumnsListener;
import com.f1.suite.web.tree.impl.FastWebTree;
import com.f1.suite.web.tree.impl.FastWebTreeColumn;

public interface WebTreeContextMenuListener extends FastWebColumnsListener {

	public void onContextMenu(FastWebTree tree, String action);
	public void onNodeClicked(FastWebTree tree, WebTreeNode node);
	public void onCellMousedown(FastWebTree tree, WebTreeNode start, FastWebTreeColumn col);
	public void onNodeSelectionChanged(FastWebTree fastWebTree, WebTreeNode node);

}
