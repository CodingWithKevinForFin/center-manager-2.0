package com.f1.utils.structs;

import com.f1.utils.AH;

public class BigArray<T> {

	private static final int BUCKET_BITS = 30;//just over 1 billion records
	private static final int BUCKET_SIZE = (1 << BUCKET_BITS);
	private static final int BUCKET_MASK = BUCKET_SIZE - 1;

	final public Object[] data;
	final private long size;
	final private boolean small;

	static BigArray newBigArray(long size) {
		return new BigArray(size);
	}

	private BigArray(long size) {
		this.size = size;
		this.small = size <= BUCKET_SIZE;
		if (small)
			data = new Object[(int) size];
		else {
			int buckets = (int) ((size + BUCKET_MASK) / BUCKET_SIZE);
			data = new Object[buckets];
			for (int i = 0; i < buckets - 1; i++)
				data[i] = new Object[BUCKET_SIZE];
			data[buckets - 1] = new Object[(int) (size - (BUCKET_SIZE * (buckets - 1L)))];
		}

	}
	public long getSize() {
		return size;

	}

	public void set(long pos, T value) {
		if (small) {
			data[(int) pos] = value;
		} else {
			Object[] bucket = (Object[]) data[(int) (pos >> BUCKET_BITS)];
			bucket[(int) (pos & BUCKET_MASK)] = value;
		}
	}
	public T get(long pos) {
		if (small)
			return (T) data[(int) pos];
		else {
			Object[] bucket = (Object[]) data[(int) (pos >> BUCKET_BITS)];
			return (T) bucket[(int) (pos & BUCKET_MASK)];
		}
	}
	public void fill(T value) {
		if (small)
			AH.fill(data, value);
		else
			for (Object o : data)
				AH.fill((Object[]) o, value);
	}

}
