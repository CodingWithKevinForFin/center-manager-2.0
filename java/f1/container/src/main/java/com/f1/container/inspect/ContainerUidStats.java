package com.f1.container.inspect;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import com.f1.container.ContainerUid;

public class ContainerUidStats {
	private final ReentrantLock lock = new ReentrantLock();
	private volatile AtomicLong statistics[];//TODO: AtomicLongArray does not support resizing so we need to make our own. Until then this will work....
	private volatile int max = 0;

	public ContainerUidStats(int size) {
		statistics = new AtomicLong[size];
		for (int i = 0; i < statistics.length; i++)
			statistics[i] = new AtomicLong(0);
	}
	public int getMax() {
		return max;
	}

	/**
	 * @param sink
	 *            filled with statistics
	 * @return the count of changed items
	 */
	public int getAllStatistics(long sink[]) {
		final int len = Math.min(sink.length, statistics.length);
		int r = 0;
		for (int i = 0; i < len; i++) {
			long t = statistics[i].get();
			if (sink[i] != t) {
				sink[i] = statistics[i].get();
				r++;
			}
		}
		return r;
	}

	public void inc(ContainerUid cs) {
		if (cs == null)
			return;
		int uid = (int) cs.getContainerScopeUid();
		if (uid >= statistics.length) {
			lock.lock();
			try {
				if (uid >= statistics.length) {
					final int newLength = Math.max(uid, statistics.length * 2);
					AtomicLong[] newStats = new AtomicLong[newLength];
					int i = 0;
					for (; i < statistics.length; i++)
						newStats[i] = statistics[i];
					for (; i < newLength; i++)
						newStats[i] = new AtomicLong(0);
					statistics = newStats;
				}
			} finally {
				lock.unlock();
			}
		}

		statistics[uid].incrementAndGet();
		if (max < uid)
			max = uid;
	}
}
