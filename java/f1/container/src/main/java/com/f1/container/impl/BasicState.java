/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container.impl;

import com.f1.base.ValuedListenable;
import com.f1.container.Partition;
import com.f1.container.State;
import com.f1.container.exceptions.ContainerException;
import com.f1.utils.EH;

public class BasicState implements State {

	private Partition partition;
	private Class<? extends State> type = getClass();
	private boolean isAlive = true;
	private Object peer;
	private long startedTime = EH.currentTimeMillis();
	private boolean isPersistenceRootLocked;
	private ValuedListenable persistedRoot;
	final private long containerScopeUid = ContainerHelper.nextContainerScopeUid();

	@Override
	public Partition getPartition() {
		return partition;
	}

	@Override
	public Class<? extends State> getType() {
		return type;
	}

	@Override
	public void invalidate() {
		isAlive = false;
	}

	@Override
	public boolean isAlive() {
		return isAlive;
	}

	@Override
	public void setPartition(Partition partition) {
		this.partition = partition;
	}

	@Override
	public void setType(Class<? extends State> type) {
		this.type = type;
	}

	@Override
	public String toString() {
		Object id = getPartition() == null ? null : getPartition().getPartitionId();
		return getClass().getName() + "[type=" + getType() + ", id=" + id + "]";
	}

	@Override
	final public Object getPeer() {
		return peer;
	}

	@Override
	final public void setPeer(Object peer) {
		this.peer = peer;
	}

	@Override
	final public void setPersistedRoot(ValuedListenable listenable) {
		if (isPersistenceRootLocked)
			throw new ContainerException("Already locked").set("state", this);
		this.persistedRoot = listenable;
	}

	@Override
	final public ValuedListenable getPersistedRoot() {
		return persistedRoot;
	}

	@Override
	final public boolean getIsPersistedRootLocked() {
		return isPersistenceRootLocked;
	}

	@Override
	final public void lockPersistedRoot() {
		if (isPersistenceRootLocked)
			throw new ContainerException("Already locked").set("state", this);
		isPersistenceRootLocked = true;
	}

	@Override
	public void initPersisted(boolean isRecovering) {
	}

	public final Object getPartitionId() {
		return partition.getPartitionId();
	}

	@Override
	public long getContainerScopeUid() {
		return containerScopeUid;
	}

	@Override
	public long getStartedTime() {
		return startedTime;
	}

	public <T> T nw(Class<T> clazz) {
		return getPartition().getContainer().nw(clazz);
	}

}
