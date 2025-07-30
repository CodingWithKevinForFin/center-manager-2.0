/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container;

import com.f1.base.ValuedListenable;

/**
 * represents a stateful object within a {@link Partition}. Note that states have an associated type and are a member of one partition
 */
public interface State extends ContainerUid {

	/**
	 * @return the type associated with this state. Note, that this may often be the class of the state itself
	 */
	Class<? extends State> getType();

	/**
	 * associate this state with a particular type
	 * 
	 * @param type
	 */
	void setType(Class<? extends State> type);

	/**
	 * check to see if {@link #invalidate()} has been called. By default should return true.
	 * 
	 * @return false iff {@link #invalidate()} has been called at least once.
	 */
	boolean isAlive();

	/**
	 * mark that this state is no longer alive, such that {@link #isAlive()} will return false. Please note that subsequent calls to invalidate should have no effect
	 */
	void invalidate();

	/**
	 * @return the partition which 'owns' this state.
	 */
	Partition getPartition();

	/**
	 * Associate this state to a particular {@link Partition}.
	 * 
	 * @param partition
	 *            the partition 'owning' this state.
	 */
	void setPartition(Partition partition);

	/**
	 * Container specific place where temporary information can be stored. Should return the last value supplied via {@link #setPeer(Object)}
	 * 
	 * @return may be null.
	 */
	Object getPeer();

	/**
	 * Container specific place where temporary information can be stored.
	 * 
	 * @param peer
	 *            may be null. Supplied value should be returned upon calls to {@link #getPeer()}
	 */
	void setPeer(Object peer);

	public void setPersistedRoot(ValuedListenable listenable);

	public ValuedListenable getPersistedRoot();

	public boolean getIsPersistedRootLocked();

	public void lockPersistedRoot();

	void initPersisted(boolean isRecovering);

	long getStartedTime();

}
