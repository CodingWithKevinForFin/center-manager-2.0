/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container.impl;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.f1.base.Clock;
import com.f1.container.ContainerRuntimeListener;
import com.f1.container.ThreadScope;
import com.f1.container.ThreadScopeController;
import com.f1.utils.CH;

public class BasicThreadScopeController extends AbstractContainerScope implements ThreadScopeController {

	private Map<Long, ThreadScope> threadScopes = new ConcurrentHashMap<Long, ThreadScope>();
	private Iterable<ContainerRuntimeListener> listeners;

	@Override
	public Collection<ThreadScope> getThreadScopes() {
		return threadScopes.values();
	}

	@Override
	public ThreadScope getThreadScope(long containerUid) {
		return threadScopes.get(containerUid);
	}

	@Override
	public ThreadScope newThreadScope(String threadPoolId, Runnable runnable, String threadName) {
		assertStarted();
		Clock clock = getServices().getClock();
		ThreadScope r = new BasicThreadScope(threadPoolId, getServices().getLocaleFormatterManager().createLocaleFormatter(clock.getLocale(), clock.getTimeZone()), runnable,
				threadName);
		threadScopes.put(r.getContainerScopeUid(), r);
		if (listeners != null)
			for (ContainerRuntimeListener listener : listeners)
				listener.onThreadScopeCreated(this, r);
		return r;
	}

	@Override
	public void removeThreadScope(ThreadScope ts) {
		threadScopes.remove(ts.getContainerScopeUid());
		if (listeners != null)
			for (ContainerRuntimeListener listener : listeners)
				listener.onThreadScopeRemoved(this, ts);
	}

	@Override
	public void start() {
		super.start();
		this.listeners = CH.noEmpty(getContainer().getRuntimeListeners(), null);
	}

}
