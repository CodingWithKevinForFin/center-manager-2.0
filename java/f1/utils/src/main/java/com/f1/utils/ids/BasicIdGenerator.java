package com.f1.utils.ids;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;

public class BasicIdGenerator implements IdGenerator<Long> {

	final private AtomicLong firstId;
	final private long skip;

	public BasicIdGenerator(long firstId) {
		this(firstId, 1);
	}
	public BasicIdGenerator(long firstId, long skip) {
		this.skip = skip;
		this.firstId = new AtomicLong(firstId);
	}

	@Override
	public Long createNextId() {
		return firstId.getAndAdd(skip);
	}

	@Override
	public void createNextIds(int count, Collection<? super Long> sink) {
		long start = firstId.getAndAdd(count);
		for (int i = 0; i < count; i++)
			sink.add(start + i);
	}

	public static class Factory implements com.f1.base.Factory<String, IdGenerator<Long>> {

		final private long first;

		public Factory(long first) {
			this.first = first;
		}

		@Override
		public BasicIdGenerator get(String key) {
			return new BasicIdGenerator(first);
		}

	}

}
