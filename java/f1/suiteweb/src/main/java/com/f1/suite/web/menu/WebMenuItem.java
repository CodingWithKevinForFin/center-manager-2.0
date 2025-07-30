/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.menu;

import com.f1.suite.web.portal.style.PortletStyleManager_Menu;

public interface WebMenuItem {

	public String getText();

	public boolean getEnabled();

	public int getPriority();

	String getCssStyle();

	public String getBackgroundImage();

	public PortletStyleManager_Menu getStyle();
	public void setStyle(PortletStyleManager_Menu setStyle);

	public WebMenu getParent();
	public void setParent(WebMenu parent);

	//Only supported for the RootPortlet show menu at this point.
	public WebMenuLinkListener getListener();
	void setListener(WebMenuLinkListener listener);

	public String getHtmlIdSelector();
	public void setHtmlIdSelector(String his);

}
