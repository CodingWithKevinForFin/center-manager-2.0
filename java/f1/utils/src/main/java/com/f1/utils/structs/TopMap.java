package com.f1.utils.structs;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

import com.f1.utils.AH;
import com.f1.utils.OH;
import com.f1.utils.SH;

/**
 * 
 * Keep track of N most significant (lowest value) elements based on comparator.
 * <P>
 * 
 * Note: Most degenerate case: starting with least significant data added first and then continuously increasingly significant data is added,
 * 
 * @param <V>
 */
public class TopMap<V> implements Iterable<V> {

	private static final int FACTOR = 2;//How much potentially "insignificant" data will be held onto before a sort (multiple of n)
	private int size = 0;
	private int pos = 0;
	final private int topSize;
	final private Comparator<V> comparator;
	final private V[] values;
	private V min;
	private boolean arranged;

	public TopMap(int size, Comparator<V> comparator) {
		this.topSize = size;
		this.comparator = comparator;
		this.values = (V[]) new Object[topSize * FACTOR];
	}

	public void add(V value) {
		if (min != null && comparator.compare(min, value) < 0)
			return;
		values[pos++] = value;
		if (size < topSize)
			size++;
		if (pos < topSize * FACTOR) {
			arranged = false;
			return;
		}
		Arrays.sort(values, comparator);
		pos = topSize;
		min = values[topSize - 1];
		for (int i = topSize; i < topSize * FACTOR; i++)
			values[i] = null;
		arranged = true;
	}
	public String toString() {
		arrange();
		return SH.joinSub(',', 0, size, values);
	}

	public int getSize() {
		return size;
	}
	public int getMaxSize() {
		return topSize;
	}
	public V get(int i) {
		OH.assertLt(i, size);
		arrange();
		return values[i];
	}

	private void arrange() {
		if (arranged)
			return;
		Arrays.sort(values, 0, Math.min(values.length, pos), comparator);
		if (pos > topSize) {
			pos = topSize;
			min = values[topSize - 1];
			for (int i = topSize; i < topSize * FACTOR; i++)
				values[i] = null;
		}

		arranged = true;
	}
	@Override
	public Iterator<V> iterator() {
		return new ArrayIterator<V>(values, 0, size);
	}

	public void clear() {
		if (pos == 0 && size == 0)
			return;
		this.size = 0;
		this.pos = 0;
		AH.fill(this.values, null);
		this.min = null;
	}

}
