/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.agg;

public class IntegerAggregator {
	private int max = Integer.MAX_VALUE, min = Integer.MIN_VALUE;
	private int count;
	private int total;

	public IntegerAggregator(int... ints) {
		add(ints);
	}

	public IntegerAggregator() {
	}

	public void add(int... ints) {
		for (int i : ints)
			add(i);
	}

	public int add(int v) {
		if (++count == 1) {
			total = max = min = v;
			return v;
		}
		total += v;
		if (v > max)
			max = v;
		else if (v < min)
			min = v;
		return v;
	}

	public int getMax() {
		return max;
	}

	public int getMin() {
		return min;
	}

	public int getCount() {
		return count;
	}

	public int getTotal() {
		return total;
	}

	public double getAverage() {
		return (double) total / count;
	}

	public void clear() {
		total = min = max = count = 0;
	}

	@Override
	public String toString() {
		if (count == 0)
			return "(count=0)";
		String r = "count=" + count + ", total=" + total + ",min=" + min + ", max=" + max + ", avg=" + ((double) total / count);
		return r;
	}
}
