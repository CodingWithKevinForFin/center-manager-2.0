/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.tree;

import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.tree.impl.FastWebTree;
import com.f1.suite.web.tree.impl.FastWebTreeColumn;

public interface WebTreeColumnMenuFactory {

	public WebMenu createColumnMenu(FastWebTree tree, FastWebTreeColumn column, WebMenu defaultMenu);
}
