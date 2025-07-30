/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.msg.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.f1.msg.MsgConnection;
import com.f1.msg.MsgException;
import com.f1.msg.MsgManager;
import com.f1.msg.MsgManagerListener;
import com.f1.utils.CH;

public class BasicMsgManager implements MsgManager {

	private boolean isRunning = false;
	private Map<String, MsgConnection> connections = new ConcurrentHashMap<String, MsgConnection>();
	private List<MsgManagerListener> msgManagerListeners = new CopyOnWriteArrayList<MsgManagerListener>();

	@Override
	public void addConnection(MsgConnection connection) {
		assertNotRunning();
		CH.putOrThrow(connections, connection.getConfiguration().getName(), connection);
		for (MsgManagerListener l : msgManagerListeners)
			l.onConnectionAdded(this, connection);
	}

	@Override
	public void assertNotRunning() {
		if (isRunning)
			throw new MsgException("already running");
	}

	@Override
	public MsgConnection getConnection(String name) {
		return CH.getOrThrow(connections, name);
	}

	@Override
	public boolean isRunning() {
		return isRunning;
	}

	@Override
	public void shutdown() {
		assertRunning();
		isRunning = false;
	}

	@Override
	public void startup() {
		assertNotRunning();
		isRunning = true;
	}

	@Override
	public void assertRunning() {
		if (!isRunning)
			throw new MsgException("already running");
	}

	@Override
	public Set<String> getConnections() {
		return connections.keySet();
	}

	@Override
	public void addMsgManagerListener(MsgManagerListener listener) {
		msgManagerListeners.add(listener);
	}

	@Override
	public Collection<MsgManagerListener> getListeners() {
		return msgManagerListeners;
	}

}
