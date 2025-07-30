package com.f1.suite.web.portal;

public interface PortletSocketListener {

	void onDisconnect(PortletSocket basicPortletSocket, PortletSocket remoteSocket);

	void onConnect(PortletSocket basicPortletSocket, PortletSocket remoteSocket);

	void onMessage(PortletSocket basicPortletSocket, PortletSocket origin, InterPortletMessage message);

	public void onInterPortletMessage(PortletSocket localSocket, PortletSocket remoteSocket, InterPortletMessage message);
}
