package com.f1.utils.agg;

import com.f1.utils.DoubleArrayList;
import com.f1.utils.MH;

public final class DoubleStatistics {

	final DoubleArrayList values;
	final DoubleAggregator aggregator;

	public DoubleStatistics() {
		values = new DoubleArrayList();
		aggregator = new DoubleAggregator();
	}

	public DoubleStatistics(double... doubles) {
		this();
		add(doubles);
	}
	public void reset() {
		aggregator.reset();
		values.clear();
	}
	public void add(double... doubles) {
		aggregator.add(doubles);
		values.addAll(doubles);
	}
	public double add(double v) {
		values.add(v);
		return aggregator.add(v);
	}
	public double getRunningAverage() {
		return aggregator.getRunningAverage();
	}
	public double getMax() {
		return aggregator.getMax();
	}
	public double getMin() {
		return aggregator.getMin();
	}
	public long getCount() {
		return aggregator.getCount();
	}
	public double getTotal() {
		return aggregator.getTotal();
	}
	public double getAverage() {
		return aggregator.getAverage();
	}
	public double getRange() {
		return aggregator.getRange();
	}
	public void setRunningAverageMaxSamples(long runningAverageMaxSamples) {
		aggregator.setRunningAverageMaxSamples(runningAverageMaxSamples);
	}
	public long getRunningAverageMaxSamples() {
		return aggregator.getRunningAverageMaxSamples();
	}
	public double getMiddle() {
		return aggregator.getMiddle();
	}

	public double getStdev() {
		return Math.sqrt(getVariance());
	}

	public double getVariance() {
		int i = values.size();
		if (i == 0)
			return Double.NaN;
		final double avg = aggregator.getAverage();
		double r = 0;
		while (i-- > 0)
			r += MH.sq(values.get(i) - avg);
		return r / values.size();
	}

}
