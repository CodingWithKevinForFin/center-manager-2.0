/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.agg;

public class LongAggregator {
	private long max = Long.MAX_VALUE, min = Long.MIN_VALUE;
	private long count;
	private long total;
	private long first;
	private long last;
	private long runningAverageMaxSamples = 1;
	private double runningAverage;

	public LongAggregator(long... longs) {
		add(longs);
	}

	public LongAggregator() {
	}

	public void add(long... longs) {
		for (long i : longs)
			add(i);
	}

	public long add(long v) {
		if (++count == 1) {
			runningAverage = first = total = max = min = v;
			return v;
		}
		last = v;
		total += v;
		if (v > max)
			max = v;
		else if (v < min)
			min = v;
		if (runningAverageMaxSamples > 1) {
			long weight = Math.min(runningAverageMaxSamples, count);
			runningAverage = (runningAverage * weight + v) / (weight + 1);
		}
		return v;
	}

	public double getRunningAverage() {
		return runningAverageMaxSamples > 1 ? runningAverage : count > 0 ? last : Double.NaN;
	}

	public long getMax() {
		return max;
	}

	public long getMin() {
		return min;
	}

	public long getCount() {
		return count;
	}

	public long getTotal() {
		return total;
	}

	public double getAverage() {
		return (double) total / count;
	}
	public double getMiddle() {
		return min + ((max - min) >> 1);
	}
	public void clear() {
		total = min = max = count = 0;
	}

	@Override
	public String toString() {
		if (count == 0)
			return "(count=0)";
		String r = "count=" + count + ", total=" + total + ",min=" + min + ", max=" + max + ", first=" + first + ", last=" + last + ", avg=" + ((double) total / count);
		if (runningAverageMaxSamples > 1)
			r += ", running avg for last " + Math.min(count, runningAverageMaxSamples) + " samples: " + runningAverage;
		return r;
	}

	public void setRunningAverageMaxSamples(long runningAverageMaxSamples) {
		this.runningAverageMaxSamples = runningAverageMaxSamples;
	}

	public long getRunningAverageMaxSamples() {
		return runningAverageMaxSamples;
	}

}
