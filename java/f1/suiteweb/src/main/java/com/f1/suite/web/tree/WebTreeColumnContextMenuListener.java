package com.f1.suite.web.tree;

import com.f1.suite.web.tree.impl.FastWebTree;
import com.f1.suite.web.tree.impl.FastWebTreeColumn;

public interface WebTreeColumnContextMenuListener {
	public void onColumnContextMenu(FastWebTree table, FastWebTreeColumn column, String action);
}
