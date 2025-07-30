package com.f1.utils.agg;

public final class DoubleAggregator {

	private double max;
	private double min;
	private double total;
	private double first;
	private double last;
	private double runningAverage;
	private long count;
	private long runningAverageMaxSamples = 1;

	public void reset() {
		max = Double.NaN;
		min = Double.NaN;
		count = 0;
		total = 0d;
		first = Double.NaN;
		last = Double.NaN;
		runningAverage = Double.NaN;
	}

	public DoubleAggregator(double... longs) {
		reset();
		add(longs);
	}

	public DoubleAggregator() {
		reset();
	}

	public void add(double... longs) {
		for (double i : longs)
			add(i);
	}

	public double add(double v) {
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
			double weight = Math.min(runningAverageMaxSamples, count);
			runningAverage = (runningAverage * weight + v) / (weight + 1);
		}
		return v;
	}

	public double getRunningAverage() {
		return runningAverageMaxSamples > 1 ? runningAverage : count > 0 ? last : Double.NaN;
	}

	public double getMax() {
		return max;
	}

	public double getMin() {
		return min;
	}

	public long getCount() {
		return count;
	}

	public double getTotal() {
		return total;
	}

	public double getAverage() {
		return (double) total / count;
	}

	public double getRange() {
		return count == 0 ? 0d : (max - min);
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

	public double getMiddle() {
		return min + (max - min) / 2d;
	}

}
