/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.msg;

import java.util.Collection;
import java.util.Set;

/**
 * keeps track of a set of Connections. This is the "highest level" representation of messaging.
 * 
 * @author rcooke
 * 
 */
public interface MsgManager {
	public void addConnection(MsgConnection connection);

	public MsgConnection getConnection(String name);

	public Set<String> getConnections();

	public void startup();

	public void shutdown();

	public boolean isRunning();

	void assertNotRunning();

	void assertRunning();

	public void addMsgManagerListener(MsgManagerListener listener);

	public Collection<MsgManagerListener> getListeners();
}
