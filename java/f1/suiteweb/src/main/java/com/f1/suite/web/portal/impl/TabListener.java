package com.f1.suite.web.portal.impl;

import com.f1.suite.web.portal.impl.TabPortlet.Tab;

public interface TabListener {

	public void onTabSelected(TabPortlet tabPortlet, Tab tab);
	public void onTabClicked(TabPortlet tabPortlet, Tab curTab, Tab prevTab, boolean onArrow);
	public void onTabRemoved(TabPortlet tabPortlet, Tab tab);
	public void onTabAdded(TabPortlet tabPortlet, Tab tab);
	public void onTabMoved(TabPortlet tabPortlet, int origPosition, Tab tab);
}
