package com.f1.utils.concurrent;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.f1.base.Generator;

public class ObjectPool<T> {

	final private BlockingQueue<T> pool;
	final private AtomicInteger createdCount = new AtomicInteger();
	final private int maxPoolSize;
	final private boolean dynamic;
	final private Generator<T> generator;

	public ObjectPool(int maxPoolSize, boolean dynamic, Generator<T> generator) {
		this.generator = generator;
		if (maxPoolSize <= 0)
			throw new IllegalArgumentException("size must be positive: " + maxPoolSize);
		pool = new ArrayBlockingQueue<T>(maxPoolSize, true);
		this.maxPoolSize = maxPoolSize;
		this.dynamic = dynamic;
		if (!dynamic)
			for (int i = 0; i < maxPoolSize; ++i)
				pool.add(createObject());
	}

	public T aquireNoThrow(long timeout, TimeUnit unit) {
		try {
			return aquire(timeout, unit);
		} catch (InterruptedException e) {
			return null;
		}
	}
	public T aquire(long timeout, TimeUnit unit) throws InterruptedException {
		if (!dynamic)
			return pool.poll(timeout, unit);
		final T r = pool.poll();
		if (r != null)
			return r;
		for (;;) {
			final int c = createdCount.get();
			if (c == maxPoolSize)
				return pool.poll(timeout, unit);
			else if (createdCount.compareAndSet(c, c + 1))
				return createObject();
		}
	}

	public void recylcle(T resource) {
		if (resource == null)
			throw new NullPointerException("resource");
		pool.add(resource);
	}

	protected T createObject() {
		T r = generator.nw();
		if (r == null)
			throw new NullPointerException("Factory returned null: " + generator);
		return r;
	}

}
