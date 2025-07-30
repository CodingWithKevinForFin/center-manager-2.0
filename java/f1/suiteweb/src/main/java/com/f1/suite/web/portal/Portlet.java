/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.portal;

import java.util.Map;

import com.f1.suite.web.peripheral.KeyEvent;
import com.f1.suite.web.peripheral.MouseEvent;

public interface Portlet extends BackendResponseListener {

	//Get the PortletManager
	public PortletManager getManager();

	//Get portletId
	public String getPortletId();

	//Visibility
	public boolean getVisible();
	public void setVisible(boolean isVisible);
	public void resetVisibility();

	//Title
	public String getTitle();
	public void setTitle(String title);

	//Size
	public void setSize(int width, int height);

	public int getWidth();
	public int getHeight();

	public int getInitWidth();
	public int getInitHeight();

	/**
	 * certain portlet containers can use this to guess logical positioning
	 * 
	 * @return -1 indicates no suggestion
	 */
	public int getSuggestedWidth(PortletMetrics metrics);

	/**
	 * certain portlet containers can use this to guess logical positioning
	 * 
	 * @return -1 indicates no suggestion
	 */
	public int getSuggestedHeight(PortletMetrics metrics);

	//PortletListener
	public void addPortletListener(PortletListener portletListener);
	public void removePortletListener(PortletListener portletListener);

	//Portlet parent
	public PortletContainer getParent();
	public void setParent(PortletContainer portletContainer);

	public void init(Map<String, Object> val, Map<String, String> origToNewPortletIdMapping, StringBuilder warningsSink);
	public void drainJavascript();
	public void handleCallback(String callback, Map<String, String> attributes);

	public PortletDownload handleContentRequest(String callback, Map<String, String> attributes);

	//Configuration
	public Map<String, Object> getConfiguration();
	PortletConfig getPortletConfig();

	//Schema
	public PortletSchema<?> getPortletSchema();

	//Close
	public void close();

	//Sockets
	public Map<String, PortletSocket> getSockets();

	//Handle Events
	public void onClosed();

	//MouseEvent may be null
	public void onUserRequestFocus(MouseEvent e);
	//true if handled event
	public boolean onUserKeyEvent(KeyEvent keyEvent);
	public boolean onUserMouseEvent(MouseEvent mouseEvent);

	String getHtmlIdSelector();
	void setHtmlIdSelector(String his);

	String getHtmlCssClass();
	void setHtmlCssClass(String his);
}
