package com.f1.utils.ids;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BatchLongSequenceIdGenerator implements LongSequenceIdGenerator {

	static class Batch {
		final AtomicLong next;
		private long endExclusive;
		private Batch(long start, long batchSize) {
			this.next = new AtomicLong(start);
			this.endExclusive = start + batchSize;
		}
	}
	private LongSequenceIdGenerator inner;
	private int batchSize;
	private volatile Batch currentBatch;

	public BatchLongSequenceIdGenerator(LongSequenceIdGenerator inner, int batchSize) {
		this.inner = inner;
		this.currentBatch = new Batch(inner.createNextLongIds(batchSize), batchSize);
		this.batchSize = batchSize;

	}
	@Override
	public Long createNextId() {
		return createNextLongId();
	}

	@Override
	public void createNextIds(int count, Collection<? super Long> sink) {
		long start = createNextLongIds(count);
		for (long i = 0; i < count; i++)
			sink.add(i + start);
	}

	private final Lock lock = new ReentrantLock();
	@Override
	public long createNextLongId() {
		for (;;) {
			Batch batch = this.currentBatch;
			long r = batch.next.getAndIncrement();
			if (r < batch.endExclusive)
				return r;
			try {
				lock.lock();
				if (batch == this.currentBatch)
					this.currentBatch = new Batch(inner.createNextLongIds(batchSize), batchSize);
			} finally {
				lock.unlock();
			}
		}
	}
	@Override
	public long createNextLongIds(int count) {
		for (;;) {
			Batch batch = this.currentBatch;
			long r = batch.next.getAndAdd(count);
			if (r + count <= batch.endExclusive)
				return r;
			try {
				lock.lock();
				if (batch == this.currentBatch) {
					r = inner.createNextLongIds(count + batchSize);
					this.currentBatch = new Batch(r + count, batchSize);
					return r;
				}
			} finally {
				lock.unlock();
			}
		}
	}

	public static class Factory implements com.f1.base.Factory<String, BatchLongSequenceIdGenerator> {
		private final int batchSize;
		private final com.f1.base.Factory<String, ? extends LongSequenceIdGenerator> inner;

		public Factory(com.f1.base.Factory<String, ? extends LongSequenceIdGenerator> inner, int batchSize) {
			this.inner = inner;
			this.batchSize = batchSize;
		}

		@Override
		public BatchLongSequenceIdGenerator get(String key) {
			return new BatchLongSequenceIdGenerator(inner.get(key), batchSize);
		}

	}
}
