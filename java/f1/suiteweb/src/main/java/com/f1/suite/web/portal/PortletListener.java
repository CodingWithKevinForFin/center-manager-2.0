/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.portal;

public interface PortletListener {

	public void onPortletAdded(Portlet newPortlet);

	public void onPortletClosed(Portlet oldPortlet);

	public void onSocketConnected(PortletSocket initiator, PortletSocket remoteSocket);

	public void onSocketDisconnected(PortletSocket initiator, PortletSocket remoteSocket);

	public void onPortletParentChanged(Portlet newPortlet, PortletContainer oldParent);

	public void onJavascriptQueued(Portlet portlet);

	public void onPortletRenamed(Portlet portlet, String oldName, String newName);

	public void onLocationChanged(Portlet portlet);

}
