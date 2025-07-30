/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container;

import com.f1.base.Factory;

/**
 * The container is the 'outer' object which contains all the various components that make the Container scope. Because the container is and instance of a {@link ContainerScope} it
 * in turn adhears to the {@link ContainerScope} contract as well. This includes, in part:<BR>
 * * A {@link PartitionController} for managing the various {@link Partition}s<BR>
 * * A {@link DispatchController}, which is responsible for dispatching {@link Action}s to the various {@link Processor} s<BR>
 * * A {@link ThreadPoolController} for managing and pooling {@link ThreadScope} s for reuse <BR>
 * * A {@link ThreadScopeController} for creating and destroying {@link ThreadScope}s<BR>
 * * A root {@link Suite} which houses all of the {@link Processor}s and child {@link Suite}s <BR>
 * {@link ContainerServices} and {@link ContainerTools} for various services and tools<BR>
 * 
 * The Container can be thought of as the 'entrance point' to the container scope. The container scope is the longest lived of all scopes so its logical that the Container is the
 * first to be started and the last to be stopped.
 * <P>
 * Note, it is permissible to have several containers running within a single JVM, however it is most common to only have one running. The containers configuration can be manually
 * constructed or you can use the facilities provided by the {@link ContainerAppMain}
 * 
 */
public interface Container extends ContainerScope {

	/**
	 * @return return the child partition controller
	 */
	PartitionController getPartitionController();

	/**
	 * @return return the child dispatch controller
	 */
	DispatchController getDispatchController();

	/**
	 * @return return the child threadscope controller
	 */
	ThreadScopeController getThreadScopeController();

	/**
	 * @return return the child thread pool controller
	 */
	ThreadPoolController getThreadPoolController();

	/**
	 * @return return the child suite controller
	 */
	SuiteController getSuiteController();

	/**
	 * @return return the persistence controller
	 */
	PersistenceController getPersistenceController();

	/**
	 * Convenience method for getting the root suite.
	 * 
	 * @return same as {@link #getSuiteController()}. {@link SuiteController#getRootSuite()} ()
	 */
	Suite getRootSuite();

	/**
	 * Add a listener to this container which will be notified of the various life cycle events. See {@link ContainerScope} for the various steps, hence events in the life cycle of
	 * the container.
	 * 
	 * @param containerLister
	 *            listener to be added, must not be null
	 */
	void addListener(ContainerListener containerListener);

	/**
	 * remove an existing listener from the container.
	 * 
	 * @param containerListner
	 *            listener that was previously added via {@link #addListener(ContainerListener)}
	 */
	void removeListener(ContainerListener containerListener);

	/**
	 * @return a list of all existing listeners, those that were added but not removed
	 */
	Iterable<ContainerListener> getListeners();

	/**
	 * Add a runtime listener to this container which will be notified of the various runtime events. the container.
	 * 
	 * @param containerRuntimeLister
	 *            listener to be added, must not be null
	 */
	void addRuntimeListener(ContainerRuntimeListener containerRuntimeListener);

	/**
	 * remove an existing runtime listener from the container.
	 * 
	 * @param containerListner
	 *            listener that was previously added via {@link #addRuntimeListener(ContainerRuntimeListener)}
	 */
	void removeRuntimeListener(ContainerRuntimeListener containerRuntimeListener);

	/**
	 * @return a list of all existing runtime listeners, those that were added but not removed
	 */
	Iterable<ContainerRuntimeListener> getRuntimeListeners();

	Factory<String, String> getLogNamer();

	public void setLogNamer(Factory<String, String> logFactory);

	/**
	 * @return return the result action future controller
	 */
	public ResultActionFutureController getResultActionFutureController();
}
