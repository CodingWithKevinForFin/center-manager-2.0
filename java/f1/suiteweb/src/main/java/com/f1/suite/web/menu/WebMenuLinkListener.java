package com.f1.suite.web.menu;

public interface WebMenuLinkListener {

	//return true if handled, false to keep walking up menu tree
	boolean onMenuItem(WebMenuLink item);
}
