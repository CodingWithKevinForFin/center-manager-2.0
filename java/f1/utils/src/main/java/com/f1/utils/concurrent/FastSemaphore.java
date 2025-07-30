/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.concurrent;

import java.util.concurrent.atomic.AtomicInteger;

final public class FastSemaphore {

	private static final int RELEASED = 0, AQUIRED = 1;
	final private AtomicInteger aquired = new AtomicInteger(RELEASED);

	public boolean tryAquire() {
		return aquired.compareAndSet(RELEASED, AQUIRED);
	}

	public void aquire() {
		while (!tryAquire())
			;
	}

	public void release() {
		if (!aquired.compareAndSet(AQUIRED, RELEASED))
			throw new RuntimeException("invalid state: " + aquired);
	}
}
