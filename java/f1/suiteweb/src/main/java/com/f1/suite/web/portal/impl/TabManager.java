package com.f1.suite.web.portal.impl;

import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.portal.impl.TabPortlet.Tab;

public interface TabManager {

	void onUserMenu(TabPortlet tabPortlet, Tab tab, String menuId);

	WebMenu createMenu(TabPortlet tabPortlet, Tab tab);

	void onUserAddTab(TabPortlet tabPortlet);

	void onUserRenamedTab(TabPortlet tabPortlet, Tab tab, String newName);

}
