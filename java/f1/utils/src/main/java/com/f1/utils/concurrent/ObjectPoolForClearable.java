package com.f1.utils.concurrent;

import java.util.Arrays;

import com.f1.base.Clearable;
import com.f1.base.Generator;
import com.f1.utils.AH;

public class ObjectPoolForClearable<T extends Clearable> implements Generator<T>, Clearable {

	final private int maxSize;
	final private Generator<T> generator;
	private int size;
	private Object[] pool;

	public ObjectPoolForClearable(Generator<T> generator, int maxSize) {
		this.maxSize = maxSize;
		this.pool = new Object[Math.min(256, maxSize)];
		this.generator = generator;
	}

	@Override
	public T nw() {
		return (size == 0) ? generator.nw() : (T) pool[--size];
	}

	public void recycle(T object) {
		object.clear();
		if (size >= maxSize)
			return;
		if (size >= this.pool.length)
			this.pool = Arrays.copyOf(this.pool, Math.min(maxSize, this.pool.length << 1));
		pool[size++] = object;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public Generator<T> getGenerator() {
		return generator;
	}

	public int getSize() {
		return size;
	}

	public void clear() {
		AH.fill(pool, null);
		this.size = 0;
	}

}
