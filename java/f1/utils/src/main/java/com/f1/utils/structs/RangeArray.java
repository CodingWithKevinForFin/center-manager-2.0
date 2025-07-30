package com.f1.utils.structs;

import java.util.Arrays;

import com.f1.base.ToStringable;

public class RangeArray<T> implements ToStringable {

	private static final int GROWTH = 32;
	private int offset = 0, min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
	private T[] values;

	public T get(int key) {
		return (key < min || key > max) ? null : values[key - offset];
	}

	public void put(int key, T value) {
		if (values == null) {
			values = (T[]) new Object[GROWTH];
			offset = key - (values.length / 2);
		} else if (key < offset) {
			int growth = GROWTH + offset - key;
			T[] values2 = (T[]) new Object[values.length + growth];
			offset -= growth;
			System.arraycopy(this.values, 0, values2, growth, this.values.length);
			values = values2;
		} else if (key >= offset + values.length) {
			values = Arrays.copyOf(values, key - offset + GROWTH);
		}
		min = Math.min(min, key);
		max = Math.max(max, key);
		values[key - offset] = value;
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append('{');
		boolean first = true;
		for (int i = 0; i < values.length; i++) {
			T val = values[i];
			if (val != null) {
				if (first)
					first = false;
				else
					sink.append(", ");

				sink.append(i + offset).append("=").append(val);
			}
		}
		sink.append('}');
		return sink;
	}

	public static void main(String a[]) {
		RangeArray<Double> ra = new RangeArray<Double>();
		//		ra.put(123, 123d);
		//		ra.put(170, 170d);
		//		ra.put(140, 140d);
		//		ra.put(17, 17d);
		//		ra.put(16, 16d);
		//		ra.put(16, 16d);
		for (int i = 0; i < 200; i++)
			ra.put(i, (double) i);
		System.out.println(ra);
		Object[] o = ra.values;
		System.out.println(o.length);
	}

	public void clear() {
		this.values = null;
		offset = 0;
		min = Integer.MAX_VALUE;
		max = Integer.MIN_VALUE;
	}

}
