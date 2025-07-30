package com.f1.suite.web.portal;

import java.util.Map;

import com.f1.suite.web.menu.WebMenuItem;
import com.f1.suite.web.menu.WebMenuLink;

public interface PortletMenuManager {

	Map<String, Object> setActiveMenuAndGenerateJson(WebMenuItem menu);
	public WebMenuLink fireLinkForId(String id);//will fire listeners, returning null if any listeners return true (meaning handled)
	@Deprecated
	public String getActionForId(String id);
	public void resetIds();
	public WebMenuItem getActiveMenu();

}
