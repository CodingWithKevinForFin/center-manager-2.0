/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container;

import java.util.Collection;

/**
 * responsible for creating new {@link ThreadScope}s. Note that {@link ThreadScope}s are, infact, {@link Thread}s. In addition to creating thread scopes, an internal list of thread
 * scopes that have already been created is also maintained. <BR>
 * Note: all methods must be thread safe as per {@link ContainerScope} threading specification
 */
public interface ThreadScopeController extends ContainerScope {

	/**
	 * create a new thread with the associated id.
	 * 
	 * @param threadPoolId
	 *            the id of the new thread
	 * @param runnable
	 *            the object which should imediately have {@link Runnable#run()} called on it (in the newly forked thread)
	 * @param threadName
	 *            the name of the newly created thread. Should be returned by calles to {@link Thread#getName()}
	 * @return the newely created thread. never null
	 */
	ThreadScope newThreadScope(String threadPoolId, Runnable runnable, String threadName);

	void removeThreadScope(ThreadScope ts);

	/**
	 * This controller keeps a list of all threadscopes which have been created via calls to {@link #newThreadScope(String, Runnable, String)}.
	 * 
	 * @return all {@link ThreadScope}s created by this controller. never null
	 */
	public Collection<ThreadScope> getThreadScopes();

	/**
	 * return the thread scope using the {@link ThreadScope#getContainerScopeUid()}
	 */
	ThreadScope getThreadScope(long containerUid);

}
