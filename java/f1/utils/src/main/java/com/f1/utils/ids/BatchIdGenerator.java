package com.f1.utils.ids;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.f1.utils.OH;

public class BatchIdGenerator<T> implements IdGenerator<T> {
	private static final int BATCH_SIZE = 1000;

	private volatile List<T> reservedIds;
	final private AtomicInteger nextIdLocation;
	final private IdGenerator<? extends T> inner;
	final private int batchSize;

	public BatchIdGenerator(IdGenerator<? extends T> inner, int batchSize) {
		OH.assertBetween(batchSize, 1, Integer.MAX_VALUE);
		this.inner = inner;
		this.batchSize = batchSize;
		this.reservedIds = new ArrayList<T>(this.batchSize);
		this.inner.createNextIds(this.batchSize, reservedIds);
		this.nextIdLocation = new AtomicInteger(0);
	}

	@Override
	public T createNextId() {
		List<T> reservedIds = this.reservedIds;
		int location = nextIdLocation.getAndIncrement();
		if (location < batchSize)
			return reservedIds.get(location);

		synchronized (this) {
			location = nextIdLocation.getAndIncrement();
			if (location < this.batchSize)
				return this.reservedIds.get(location);
			this.reservedIds = new ArrayList<T>(this.batchSize);
			inner.createNextIds(this.batchSize, this.reservedIds);
			nextIdLocation.set(1);
			return this.reservedIds.get(0);
		}
	}

	@Override
	public void createNextIds(int count, Collection<? super T> sink) {
		for (int i = 0; i < count; ++i)
			sink.add(createNextId());
	}

	public static class Factory<T> implements com.f1.base.Factory<String, BatchIdGenerator<T>> {
		private final int batchSize;
		private final com.f1.base.Factory<String, ? extends IdGenerator<T>> inner;

		public Factory(com.f1.base.Factory<String, ? extends IdGenerator<T>> inner, int batchSize) {
			this.inner = inner;
			this.batchSize = batchSize;
		}

		@Override
		public BatchIdGenerator<T> get(String key) {
			return new BatchIdGenerator<T>(inner.get(key), batchSize);
		}

	}
}
