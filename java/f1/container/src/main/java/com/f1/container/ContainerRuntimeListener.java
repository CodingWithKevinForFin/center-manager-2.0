package com.f1.container;

/**
 * Listen for runtime changes to the container. THREADING NOTE: all methods must be concurrently safe, meaning multiple threads can call into a method simultaneously.
 * 
 * @author rcooke
 * 
 */
public interface ContainerRuntimeListener {

	/**
	 * Called when a partition is created (directly after). THREADING NOTE: this is called in line of the thread creating the partition
	 */
	public void onPartitionCreated(PartitionController partitionController, Partition partition);

	/**
	 * Called when a partition is removed (directly after). THREADING NOTE: this is called in line of the thread removing the partition
	 */
	public void onPartitionRemoved(PartitionController partitionController, Partition partition);

	/**
	 * Called when a state is created (directly after). THREADING NOTE: this is called in line of the thread creating the state
	 */
	public void onStateCreated(PartitionController partitionController, State state);

	/**
	 * Called when a state is removed (directly after). THREADING NOTE: this is called in line of the thread removing the state
	 */
	public void onStateRemoved(PartitionController partitionController, State state);

	/**
	 * Called when a thread scope is created (directly after). THREADING NOTE: this is called in line of the thread removing the thread scope
	 */
	public void onThreadScopeCreated(ThreadScopeController threadScopeController, ThreadScope threadScope);

	/**
	 * Called when a thread scope is removed (directly after). THREADING NOTE: this is called in line of the thread removing the thread scope
	 */
	public void onThreadScopeRemoved(ThreadScopeController threadScopeController, ThreadScope threadScope);
}
