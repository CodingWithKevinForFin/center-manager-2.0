/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.concurrent;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;
import java.util.logging.Logger;

import com.f1.utils.LH;
import com.f1.utils.OH;

public class FastThreadPool implements Runnable, Executor {
	private static final Logger log = Logger.getLogger(FastThreadPool.class.getName());
	final private int size;
	final private FastStackLite<Thread> threads = new FastStackLite<Thread>();
	final private FastQueue<Runnable> tasks = new FastQueue<Runnable>();
	final private Thread[] threadArray;
	final private ThreadFactory factory;
	final private AtomicInteger threadsCount = new AtomicInteger(0);
	private boolean allThreadsCreated = false;
	private boolean running;

	static private class ThreadNode extends ConcurrentNode<Thread> {
		public volatile boolean inPool = false;
		public boolean isAlive = true;

		public ThreadNode(Thread thread) {
			this.value = thread;
		}
		public void unpark() {
			inPool = false;
			LockSupport.unpark(value);
		}
	}

	public FastThreadPool(int size, String name) {
		this(size, new NamedThreadFactory(name, false));
	}
	public FastThreadPool(int size, ThreadFactory factory) {
		OH.assertNotNull(factory, "factory");
		this.size = size;
		this.factory = factory;
		this.threadArray = new Thread[size];
	}

	public void start() {
		OH.assertFalse(this.running, "already running");
		this.running = true;
	}

	@Override
	public void run() {
		try {
			final ThreadNode tn = new ThreadNode(Thread.currentThread());
			while (running && tn.isAlive) {
				for (Runnable task = tasks.getThreadSafe(); task != null; task = tasks.getThreadSafe())
					try {
						task.run();
					} catch (Throwable e) {
						LH.severe(log, "Uncaught throwable", e);
					}
				tn.inPool = true;
				threads.push(tn);
				if (!tasks.isEmpty())
					unparkThread();
				while (tn.inPool)
					LockSupport.park();
				Thread.interrupted();
			}
		} catch (Throwable e) {
			LH.severe(log, "General Error", e);
		}
	}
	private boolean unparkThread() {
		ThreadNode tn2 = (ThreadNode) threads.pop();
		if (tn2 == null)
			return false;
		tn2.unpark();
		return true;
	}

	@Override
	public void execute(Runnable r) {
		tasks.put(r);
		if (!unparkThread() && !allThreadsCreated) {
			for (int pos = threadsCount.get(); pos != size; pos = threadsCount.get())
				if (threadsCount.compareAndSet(pos, pos + 1)) {
					(threadArray[pos] = factory.newThread(this)).start();
					return;
				}
			allThreadsCreated = true;
		}
	}

	public void stop() {
		OH.assertTrue(this.running, "not running");
		this.running = false;
		for (Thread t : this.threadArray)
			if (t != null)
				t.interrupt();
		for (ThreadNode thread = (ThreadNode) threads.pop(); thread != null; thread = (ThreadNode) threads.pop()) {
			thread.isAlive = false;
			thread.unpark();
		}
	}

	public boolean isRunning() {
		return this.running;
	}
}
