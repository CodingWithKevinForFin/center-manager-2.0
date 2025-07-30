/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.table;

import com.f1.suite.web.menu.WebMenu;

public interface WebContextMenuFactory {

	public WebMenu createMenu(WebTable table);
}
