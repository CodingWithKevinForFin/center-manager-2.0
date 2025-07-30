package com.f1.suite.web.portal;

import java.util.Collection;
import java.util.Set;

import com.f1.suite.web.portal.impl.BasicPortletSocket;

public interface PortletSocket {

	String getName();

	String getTitle();

	boolean canConnectTo(PortletSocket remoteSocket);

	Portlet getPortlet();

	boolean getSupportsMultipleConnections();

	void connectTo(PortletSocket remoteSocket);

	void disconnectFrom(PortletSocket remoteSocket);

	Collection<PortletSocket> getRemoteConnections();

	Set<Class<? extends InterPortletMessage>> getOutboundMessageTypes();

	Set<Class<? extends InterPortletMessage>> getInboundMessageTypes();

	void addListener(PortletSocketListener listener);

	void removeListener(PortletSocketListener listener);

	void sendMessage(InterPortletMessage message);

	public boolean getIsInitiator();

	boolean canAcceptMoreConnections();

	boolean isConnectedTo(BasicPortletSocket recv);
}
