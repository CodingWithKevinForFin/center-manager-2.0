/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container;

import com.f1.container.exceptions.ContainerInterruptedException;
import com.f1.container.impl.ContainerHelper;
import com.f1.utils.AH;
import com.f1.utils.LocaleFormatter;

/**
 * Represents a thread created by the f1 container. See the {@link ThreadScopeController} for details.
 */
public class ThreadScope extends Thread implements ContainerUid {

	final private LocaleFormatter localFormatter;
	final private long containerUid = ContainerHelper.nextContainerScopeUid();
	final private String threadPoolKey;

	public ThreadScope(LocaleFormatter localFormatter, Runnable runnable, String threadName, String threadPoolKey) {
		super(runnable, threadName);
		this.threadPoolKey = threadPoolKey;
		this.localFormatter = localFormatter;
	}

	/**
	 * checks to see if this thread has been interruped
	 * 
	 * @throws ContainerInterruptedException
	 *             iff {@link #interrupt()} has been called, such that {@link #interrupted()} would return true.
	 */
	public void assertNotInterrupt() throws ContainerInterruptedException {
		if (interrupted())
			throw new ContainerInterruptedException("was interrupted", null, null);
	}

	/**
	 * @return the locale formatter assocated with this thread scope. Because some formatters may not be thread safe, a local version is kept on the thread itself.
	 */
	public LocaleFormatter getFormatter() {
		return localFormatter;
	}

	@Override
	public long getContainerScopeUid() {
		return containerUid;
	}

	final private long startTime = System.currentTimeMillis();

	public long getStartTimeMillis() {
		return startTime;
	}

	public String getThreadPoolKey() {
		return threadPoolKey;
	}

	private ProcessActionListener[] processListeners = ContainerHelper.EMPTY_PROCESS_EVENT_LISTENER_ARRAY;

	public void addProcessActionListener(ProcessActionListener listener) {
		processListeners = AH.insert(processListeners, processListeners.length, listener);
	}

	public ProcessActionListener[] getProcessActionListeners() {
		return processListeners;
	}
}
