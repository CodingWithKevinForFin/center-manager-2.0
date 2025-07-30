package com.f1.utils;

import java.util.ArrayList;
import java.util.List;

import com.f1.base.ToStringable;
import com.f1.utils.structs.ComparableComparator;

public class DoubleGradient implements ToStringable {

	private static class Stop implements Comparable<Stop>, ToStringable {

		private final double source;
		private final double target;

		@Override
		public int compareTo(Stop o) {
			return OH.compare(source, o.source);
		}

		private Stop(double source, double target) {
			if (MH.isntNumber(source))
				source = 0;
			this.source = source;
			this.target = target;
		}

		@Override
		public StringBuilder toString(StringBuilder sink) {
			return sink.append(source).append(" -> ").append(target);
		}

		@Override
		public String toString() {
			return toString(new StringBuilder()).toString();
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof Stop && ((Stop) obj).source == source && ((Stop) obj).target == target;
		}
		@Override
		public int hashCode() {
			return OH.hashCode((int) Double.doubleToLongBits(source), (int) Double.doubleToLongBits(target));
		}

	}

	private List<Stop> stops = new ArrayList<DoubleGradient.Stop>();

	private void addStop(double values, Stop s) {
		CH.insertSorted(stops, s, ComparableComparator.instance(Stop.class), true);
	}
	public DoubleGradient addStop(double values, double target) {
		addStop(values, new Stop(values, target));
		return this;
	}

	public double toGradient(double val) {
		if (MH.isntNumber(val))
			val = 0;
		int len = stops.size();
		if (len < 2)
			return len == 0 ? 0 : stops.get(0).target;
		Stop lower = stops.get(0);
		if (val <= lower.source || Double.isNaN(val))
			return lower.target;
		Stop upper = stops.get(1);
		int pos = 1;
		for (;;) {
			if (val <= upper.source)
				return gradient(lower, upper, val);
			else if (++pos == len)
				return upper.target;
			lower = upper;
			upper = stops.get(pos);
		}

	}
	private double gradient(Stop lower, Stop upper, double v) {
		double v1 = lower.source;
		double v2 = upper.source;
		double c1 = lower.target;
		double c2 = upper.target;
		double p = (v - v1) / (v2 - v1);
		return gradient(c1, c2, p);
	}

	private double gradient(double c1, double c2, double p) {
		return c1 + (double) (p * (c2 - c1));
	}

	public int getStopsCount() {
		return this.stops.size();
	}
	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append("DoubleGradient[");
		SH.join(", ", this.stops, sink);
		return sink.append("]");
	}
}
