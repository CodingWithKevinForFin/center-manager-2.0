package com.f1.suite.web.portal.impl.visual;

import com.f1.suite.web.menu.WebMenu;

public interface WebTreemapContextMenuFactory {

	public WebMenu createMenu(TreemapPortlet treemap, TreemapNode selected);
}
