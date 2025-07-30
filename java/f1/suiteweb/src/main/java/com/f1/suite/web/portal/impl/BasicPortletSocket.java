package com.f1.suite.web.portal.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.f1.suite.web.portal.InterPortletMessage;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletSocket;
import com.f1.suite.web.portal.PortletSocketListener;
import com.f1.utils.CH;

public class BasicPortletSocket implements PortletSocket {

	final private String name;
	final private boolean supportsMultipleConnections;
	final private Set<Class<? extends InterPortletMessage>> outboundMessageTypes;
	final private Set<Class<? extends InterPortletMessage>> inboundMessageTypes;
	final private Portlet portlet;
	final private List<PortletSocket> remoteSockets = new ArrayList<PortletSocket>();
	final private List<PortletSocketListener> listeners = new ArrayList<PortletSocketListener>();
	final private String title;
	final private boolean isInitiator;

	public BasicPortletSocket(Portlet portlet, boolean isInitiator, String name, String title, boolean supportsMultipleConnections, Collection<Class> outboundMessageTypes,
			Collection<Class> inboundMessageTypes) {
		this.name = name;
		this.supportsMultipleConnections = supportsMultipleConnections;
		this.outboundMessageTypes = (Set) new HashSet(outboundMessageTypes);
		this.inboundMessageTypes = (Set) new HashSet(inboundMessageTypes);
		this.portlet = portlet;
		this.title = title;
		this.isInitiator = isInitiator;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canConnectTo(PortletSocket remoteSocket) {
		if (remoteSocket.getIsInitiator() == getIsInitiator())
			return false;
		Set<Class<? extends InterPortletMessage>> otherInbound = remoteSocket.getInboundMessageTypes();
		Set<Class<? extends InterPortletMessage>> otherOutbound = remoteSocket.getOutboundMessageTypes();
		for (Class<? extends InterPortletMessage> c : outboundMessageTypes)
			if (!canConnect(c, otherInbound))
				return false;
		for (Class<? extends InterPortletMessage> c : otherOutbound)
			if (!canConnect(c, inboundMessageTypes))
				return false;
		return true;
	}

	private boolean canConnect(Class<? extends InterPortletMessage> outbound, Set<Class<? extends InterPortletMessage>> inbound) {
		for (Class<? extends InterPortletMessage> in : inbound)
			if (in.isAssignableFrom(outbound))
				return true;
		return false;
	}

	@Override
	public Portlet getPortlet() {
		return portlet;
	}

	@Override
	public boolean getSupportsMultipleConnections() {
		return supportsMultipleConnections;
	}

	@Override
	public void connectTo(PortletSocket remoteSocket) {
		if (!canConnectTo(remoteSocket))
			throw new IllegalArgumentException("can not connect to: " + remoteSocket);
		if (!getSupportsMultipleConnections() && !getRemoteConnections().isEmpty()) {
			throw new IllegalStateException("socket does not support multiple connections");
		}
		if (remoteSockets.contains(remoteSocket))
			throw new RuntimeException("Already connected to: " + remoteSocket);
		remoteSockets.add(remoteSocket);
		if (isInitiator) {
			remoteSocket.connectTo(this);
			portlet.getManager().onSocketConnected(this, remoteSocket);
		}
		for (PortletSocketListener listener : listeners)
			listener.onConnect(this, remoteSocket);
	}

	@Override
	public Collection<PortletSocket> getRemoteConnections() {
		return remoteSockets;
	}

	@Override
	public Set<Class<? extends InterPortletMessage>> getOutboundMessageTypes() {
		return outboundMessageTypes;
	}

	@Override
	public Set<Class<? extends InterPortletMessage>> getInboundMessageTypes() {
		return inboundMessageTypes;
	}

	@Override
	public String toString() {
		return name + "[" + portlet.getPortletId() + "]";
	}

	@Override
	public void disconnectFrom(PortletSocket remoteSocket) {
		remoteSockets.remove(remoteSocket);
		for (PortletSocketListener listener : listeners)
			listener.onDisconnect(this, remoteSocket);
		if (isInitiator) {
			remoteSocket.disconnectFrom(this);
			portlet.getManager().onSocketDisconnected(this, remoteSocket);
		}
	}

	@Override
	public void addListener(PortletSocketListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void removeListener(PortletSocketListener listener) {
		this.listeners.remove(listener);

	}

	protected void onMessage(PortletSocket origin, InterPortletMessage message) {
		for (PortletSocketListener listener : listeners)
			listener.onMessage(this, origin, message);
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void sendMessage(InterPortletMessage message) {
		for (PortletSocket remoteSocket : remoteSockets) {
			((BasicPortletSocket) remoteSocket).onMessage(this, message);
		}
	}

	@Override
	public boolean getIsInitiator() {
		return isInitiator;
	}

	public boolean hasConnections() {
		return !remoteSockets.isEmpty();
	}

	@Override
	public boolean canAcceptMoreConnections() {
		return this.supportsMultipleConnections || CH.isEmpty(remoteSockets);
	}

	@Override
	public boolean isConnectedTo(BasicPortletSocket recv) {
		return this.remoteSockets.contains(recv);
	}
}
