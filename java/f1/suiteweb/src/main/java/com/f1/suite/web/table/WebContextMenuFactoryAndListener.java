package com.f1.suite.web.table;

import com.f1.suite.web.menu.WebMenu;

public interface WebContextMenuFactoryAndListener {
	public WebMenu createMenu(WebTable table);
	public void onContextMenu(WebTable table, String action);
}
