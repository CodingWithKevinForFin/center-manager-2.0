/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.table;

import com.f1.suite.web.menu.WebMenu;

public interface WebTableColumnContextMenuFactory {

	public WebMenu createColumnMenu(WebTable table, WebColumn column, WebMenu defaultMenu);
	public WebMenu createColumnMenu(WebTable table, WebMenu defaultMenu);
}
