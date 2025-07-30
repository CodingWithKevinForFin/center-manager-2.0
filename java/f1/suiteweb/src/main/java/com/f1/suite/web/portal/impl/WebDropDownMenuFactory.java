package com.f1.suite.web.portal.impl;

import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.WebMenuLink;

public interface WebDropDownMenuFactory {
	public WebMenu createMenu(DropDownMenuPortlet dropdown, String id, WebMenuLink menu);
}
