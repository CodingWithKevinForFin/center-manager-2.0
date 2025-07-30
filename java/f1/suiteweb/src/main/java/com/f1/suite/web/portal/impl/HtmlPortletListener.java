package com.f1.suite.web.portal.impl;

import com.f1.suite.web.portal.impl.HtmlPortlet.Callback;

public interface HtmlPortletListener {
	public void onUserClick(HtmlPortlet portlet);
	public void onUserCallback(HtmlPortlet htmlPortlet, String id, int mouseX, int mouseY, Callback cb);
	public void onHtmlChanged(String old, String nuw);
}
