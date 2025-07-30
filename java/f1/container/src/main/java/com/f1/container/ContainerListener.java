package com.f1.container;


/**
 * Represents a hook interested in the various stages of a container's life cycle (as described in {@link ContainerScope}). The following stages are:
 * 
 * <pre>
 * 01. {@link Container#start()} is called on the container
 * 02. {@link ContainerListener#onPreStart(Container)} is called on each container listener
 * 03. {@link ContainerScope#start()} is called on all container scoped children of the container
 * 04. {@link ContainerListener#onPostStart(Container)} is called on each container listener
 *  -- at this point, all {@link ContainerScope}'d children should be in a started state. -- 
 * 05. {@link ContainerListener#onPreStartDispatching(Container)} is called on each container listener
 * 06. {@link ContainerScope#startDispatching()} is called on all container scoped children of the container
 * 07. {@link ContainerListener#onPostStartDispatching(Container)} is called on each container listener
 *  -- at this point, messages will start flowing through the container --
 * 08. {@link Container#start()} is called on the container
 * 09. {@link ContainerListener#onPreStop(Container)} is called on each container listener
 * 10. {@link ContainerScope#stop()} is called on all container scoped children of the container
 * 11. {@link ContainerListener#onPostStop(Container)} is called on each container listener
 *  -- at this point, all {@link ContainerScope}'d children should be in a stopped state. --
 * </pre>
 */
public interface ContainerListener {

	/**
	 * a hook called by the {@link Container} immediatly after {@link Container#start()} has being called and before any children of the container have had
	 * {@link ContainerScope#start()} called
	 * 
	 * @param container
	 *            the container that this listener is added to
	 */
	void onPreStart(Container container);

	/**
	 * a hook called by the {@link Container} after {@link Container#start()} has being called and after all children of the container have had {@link ContainerScope#start()}
	 * called.
	 * 
	 * @param container
	 *            the container that this listener is added to
	 */
	void onPostStart(Container container);

	/**
	 * a hook called by the {@link Container} immediatly after {@link Container#stop()} has being called and before any children of the container have had
	 * {@link ContainerScope#stop()} called
	 * 
	 * @param container
	 *            the container that this listener is added to
	 */
	void onPreStop(Container container);

	/**
	 * a hook called by the {@link Container} after {@link Container#stop()} has being called and after all children of the container have had {@link ContainerScope#stop()} called
	 * 
	 * @param container
	 *            the container that this listener is added to
	 */
	void onPostStop(Container container);

	/**
	 * a hook called by the {@link Container} immediately after {@link Container#startDispatching()} has being called and before any children of the container have had
	 * {@link ContainerScope#startDispatching()} called
	 * 
	 * @param container
	 *            the container that this listener is added to
	 */
	void onPreStartDispatching(Container container);

	/**
	 * a hook called by the {@link Container} after {@link Container#startDispatching()} has being called and after all children of the container have had
	 * {@link ContainerScope#startDispatching()} called
	 * 
	 * @param container
	 *            the container that this listener is added to
	 */
	void onPostStartDispatching(Container container);

	/**
	 * a hook called by the {@link Container} immediately after {@link Container#startDispatching()} has being called and before any children of the container have had
	 * {@link ContainerScope#startDispatching()} called
	 * 
	 * @param container
	 *            the container that this listener is added to
	 */
	void onPreStopDispatching(Container container);

	/**
	 * a hook called by the {@link Container} after {@link Container#startDispatching()} has being called and after all children of the container have had
	 * {@link ContainerScope#startDispatching()} called
	 * 
	 * @param container
	 *            the container that this listener is added to
	 */
	void onPostStopDispatching(Container container);

	void onContainerScopeAdded(ContainerScope abstractContainerScope);
}
