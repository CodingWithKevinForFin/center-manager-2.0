package com.f1.suite.web.portal.impl;

import com.f1.suite.web.portal.Portlet;

public interface RootPortletListener {

	public void onPopupWindowclosed(RootPortlet rootPortlet);
	public void onContentChanged(RootPortlet rootPortlet, Portlet old, Portlet nuw);

}
