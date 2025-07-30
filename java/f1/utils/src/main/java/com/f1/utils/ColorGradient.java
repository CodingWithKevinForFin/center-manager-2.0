package com.f1.utils;

import java.util.ArrayList;
import java.util.List;

import com.f1.base.ToStringable;
import com.f1.utils.structs.ComparableComparator;

public final class ColorGradient implements ToStringable {

	private static class Stop implements Comparable<Stop>, ToStringable {

		private final double value;
		private final int color;

		@Override
		public int compareTo(Stop o) {
			return OH.compare(value, o.value);
		}

		private Stop(double value, int color) {
			this.value = MH.isntNumber(value) ? 0 : value;
			this.color = color;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null || obj.getClass() != Stop.class)
				return false;
			Stop other = (Stop) obj;
			return other.value == value && other.color == color;
		}

		@Override
		public StringBuilder toString(StringBuilder sink) {
			return ColorHelper.toString(this.color, sink.append(value).append(':'));
		}

		@Override
		public int hashCode() {
			return OH.hashCode((int) (value * 100), color);
		}
	}

	final private List<Stop> stops;

	private void addStop(double values, Stop s) {
		CH.insertSorted(stops, s, ComparableComparator.instance(Stop.class), true);
	}
	public ColorGradient addStop(double values, int color) {
		addStop(values, new Stop(values, color));
		return this;
	}
	public ColorGradient addStop(double values, String color) {
		addStop(values, new Stop(values, ColorHelper.parseRgb(color)));
		return this;
	}
	public ColorGradient addStop(double values, int r, int g, int b, int a) {
		addStop(values, new Stop(values, ColorHelper.toRgba(r, g, b, a)));
		return this;
	}
	public ColorGradient addStop(double values, int r, int g, int b) {
		addStop(values, new Stop(values, ColorHelper.toRgb(r, g, b)));
		return this;
	}

	public ColorGradient() {
		stops = new ArrayList<ColorGradient.Stop>();
	}
	public ColorGradient(String s) {
		if (SH.is(s)) {
			int count = SH.count(':', s);
			if (count == 0) {
				stops = new ArrayList<ColorGradient.Stop>(1);
				long color = ColorHelper.parseRgbNoThrow(s);
				if (color != ColorHelper.NO_COLOR)
					this.addStop(0, (int) color);
				return;
			}
			stops = new ArrayList<ColorGradient.Stop>(count);
			for (int start = 0; start < s.length();) {
				int mid = s.indexOf(':', start);
				int end = s.indexOf(',', mid);
				if (end == -1)
					end = s.length();
				double value = SH.parseDouble(s, start, mid);
				long color = ColorHelper.parseRgbNoThrow(s, mid + 1, end);
				if (color != ColorHelper.NO_COLOR)
					this.addStop(value, (int) color);
				start = end + 1;
			}
		} else {
			stops = new ArrayList<ColorGradient.Stop>();
		}
	}
	public ColorGradient(double min, double max, ColorGradient cs) {
		int size = cs.stops.size();
		stops = new ArrayList<ColorGradient.Stop>(size);
		for (int i = 0; i < size; i++) {
			Stop stop = cs.stops.get(i);
			stops.add(new Stop(MH.clip(stop.value, min, max), stop.color));
		}
	}
	public ColorGradient(double min, double max, String s) {
		if (SH.is(s)) {
			stops = new ArrayList<ColorGradient.Stop>(SH.count(':', s));
			for (int start = 0; start < s.length();) {
				int mid = s.indexOf(':', start);
				int end = s.indexOf(',', mid);
				if (end == -1)
					end = s.length();
				double value = MH.clip(SH.parseDouble(s, start, mid), min, max);
				long color = ColorHelper.parseRgbNoThrow(s, mid + 1, end);
				if (color != ColorHelper.NO_COLOR)
					this.addStop(value, (int) color);
				start = end + 1;
			}
			CH.sort(stops);
		} else {
			stops = new ArrayList<ColorGradient.Stop>();
		}
	}

	public String toColorRgb(double val) {
		return ColorHelper.toString(toColor(val));
	}
	public int toColor(double val) {
		if (MH.isntNumber(val))
			val = 0;
		int len = stops.size();
		if (len < 2)
			return len == 0 ? 0 : stops.get(0).color;
		Stop lower = stops.get(0);
		if (val <= lower.value)
			return lower.color;
		Stop upper = stops.get(1);
		int pos = 1;
		for (;;) {
			if (val <= upper.value)
				return gradient(lower, upper, val);
			else if (++pos == len)
				return upper.color;
			lower = upper;
			upper = stops.get(pos);
		}

	}

	private int gradient(Stop lower, Stop upper, double v) {
		double v1 = lower.value;
		double v2 = upper.value;
		int c1 = lower.color;
		int c2 = upper.color;
		double p = (v - v1) / (v2 - v1);
		return ColorHelper.toRgba(gradient(ColorHelper.getR(c1), ColorHelper.getR(c2), p), gradient(ColorHelper.getG(c1), ColorHelper.getG(c2), p),
				gradient(ColorHelper.getB(c1), ColorHelper.getB(c2), p), gradient(ColorHelper.getA(c1), ColorHelper.getA(c2), p));
	}

	private int gradient(int c1, int c2, double p) {
		return c1 + (int) (p * (c2 - c1));
	}

	public int getStopsCount() {
		return this.stops.size();
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && obj.getClass() == ColorGradient.class && OH.eq(this.stops, ((ColorGradient) obj).stops);
	}
	public double getStopValue(int i) {
		return this.stops.get(i).value;
	}
	public int getStopColor(int i) {
		return this.stops.get(i).color;
	}

	@Override
	public String toString() {
		if (getStopsCount() == 0)
			return "";
		return toString(new StringBuilder()).toString();
	}
	@Override
	public StringBuilder toString(StringBuilder sink) {
		int n = getStopsCount();
		for (int i = 0; i < n; i++) {
			if (i != 0)
				sink.append(',');
			this.stops.get(i).toString(sink);
		}
		return sink;
	}
	public ColorGradient clearAlpha() {
		ColorGradient r = new ColorGradient();
		int n = getStopsCount();
		for (int i = 0; i < n; i++) {
			Stop stop = stops.get(i);
			r.addStop(stop.value, ColorHelper.getR(stop.color), ColorHelper.getG(stop.color), ColorHelper.getB(stop.color));
		}
		return r;
	}
	public boolean hasAlpha() {
		int n = getStopsCount();
		for (int i = 0; i < n; i++)
			if (ColorHelper.getA(this.stops.get(i).color) != 255)
				return true;
		return false;
	}

}
