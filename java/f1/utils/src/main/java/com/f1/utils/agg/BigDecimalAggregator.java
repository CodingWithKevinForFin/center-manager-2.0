/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.agg;

import java.math.BigDecimal;

public class BigDecimalAggregator {
	private BigDecimal max = null, min = null;
	private long count;
	private BigDecimal total = BigDecimal.ZERO;
	private BigDecimal first;
	private BigDecimal last;
	private long runningAverageMaxSamples = 1;
	private BigDecimal runningAverage;

	public BigDecimalAggregator(BigDecimal... longs) {
		add(longs);
	}

	public void add(BigDecimal... longs) {
		for (BigDecimal i : longs)
			add(i);
	}

	public BigDecimal add(BigDecimal v) {
		if (v == null)
			return null;
		if (++count == 1) {
			runningAverage = first = total = max = min = v;
			return v;
		}
		last = v;
		total = total.add(v);
		if (v.compareTo(max) > 0)
			max = v;
		else if (v.compareTo(min) < 0)
			min = v;
		if (runningAverageMaxSamples > 1) {
			long weight = Math.min(runningAverageMaxSamples, count);
			runningAverage = runningAverage.multiply(BigDecimal.valueOf(weight)).add(v).divide(BigDecimal.valueOf(weight + 1), BigDecimal.ROUND_HALF_EVEN);
		}
		return v;
	}

	public BigDecimal getRunningAverage() {
		return runningAverage;
	}

	public BigDecimal getMax() {
		return max;
	}

	public BigDecimal getMin() {
		return min;
	}

	public long getCount() {
		return count;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public BigDecimal getAverage() {
		return total.divide(BigDecimal.valueOf(count), BigDecimal.ROUND_HALF_EVEN);
	}

	@Override
	public String toString() {
		if (count == 0)
			return "(count=0)";
		String r = "count=" + count + ", total=" + total + ",min=" + min + ", max=" + max + ", first=" + first + ", last=" + last + ", avg=" + getAverage();
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
