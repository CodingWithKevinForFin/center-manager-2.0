package com.f1.ami.web.charts;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.utils.math.DateNanosMath;
import com.f1.utils.structs.table.stack.SingletonCalcFrame;

public class AmiWebChartAxisPortlet_Time extends AmiWebChartAxisFormatter {

	private SingletonCalcFrame tmp = new SingletonCalcFrame(AmiWebChartAxisPortlet.VAR_NAME, Number.class);
	private byte type;

	public AmiWebChartAxisPortlet_Time(AmiWebChartAxisPortlet axis) {
		super(axis, axis.getFormulas());
	}

	//TODO: make abstract
	@Override
	public String format(Number val) {
		if (val == null)
			return null;
		if (type == AXIS_DATA_TYPE_UTCN) {
			val = DateNanosMath.INSTANCE.cast(val);
		}
		if (formatter == null)
			return AmiUtils.s(val);
		tmp.setValue(val);
		return AmiUtils.snn(formatter.get(getStackFrame().reset(tmp)), "null");
	}

	private static final int SECOND = 1000;
	private static final int MINUTE = 60 * SECOND;
	private static final int HOUR = 60 * MINUTE;
	private static final int DAY = 24 * HOUR;
	private static final int[] HUMAN_MILLIS_FACTORS = new int[] { 1 * SECOND, 5 * SECOND, 15 * SECOND, 30 * SECOND, MINUTE, 2 * MINUTE, 5 * MINUTE, 10 * MINUTE, 15 * MINUTE,
			30 * MINUTE, HOUR, 2 * HOUR, 4 * HOUR, 6 * HOUR, 12 * HOUR, DAY, 2 * DAY, 5 * DAY, 10 * DAY, 20 * DAY, 50 * DAY, 100 * DAY, 250 * DAY, 1000 * DAY };

	@Override
	public double calcUnitSize(Number m, Number df, byte type) {
		double maxBucketsCount = toDouble(m);
		double diff = toDouble(df);
		if (type == AXIS_DATA_TYPE_UTCN) {
			diff = diff / 1000000d;
			double d = diff / maxBucketsCount;
			if (d < 10000)
				return AmiWebChartUtils.calcUnitSize(maxBucketsCount, diff) * 1000000d;
			for (int i : HUMAN_MILLIS_FACTORS) {
				if (i > d)
					return i * 1000000d;
			}
			double r = AmiWebChartUtils.calcUnitSize(maxBucketsCount, diff);
			return r * 1000000d;
		} else {
			double d = diff / maxBucketsCount;
			if (d < 10000)
				return AmiWebChartUtils.calcUnitSize(maxBucketsCount, diff);
			for (int i : HUMAN_MILLIS_FACTORS) {
				if (i > d)
					return i;
			}
			double r = AmiWebChartUtils.calcUnitSize(maxBucketsCount, diff);
			return r;
		}
	}

	@Override
	public Number roundUp(Number max, Number size) {
		return AmiWebChartUtils.roundUp(max, size);
	}

	@Override
	public Number roundDown(Number min, Number size) {
		return AmiWebChartUtils.roundDown(min, size);
	}

	@Override
	public double calcMinorUnitSize(Number majorUnit) {
		return AmiWebChartUtils.calcMinorUnitSize(toDouble(majorUnit));
	}

	@Override
	public void useDefaultFormatter(Number d, Number mi, Number ma, byte type) {
		double diff = toDouble(d);
		double min = toDouble(mi);
		double max = toDouble(ma);
		this.type = type;
		if (type == AXIS_DATA_TYPE_UTCN)
			diff = diff / 1000000d;
		if (diff < .005 && type == AXIS_DATA_TYPE_UTCN)
			onFormatChanged("formatTimeWithNanos(n)");
		else if (diff < 1 && type == AXIS_DATA_TYPE_UTCN)
			onFormatChanged("formatTimeWithMicros(n)");
		else if (diff < 2000)
			onFormatChanged("formatTimeWithMillis(n)");
		else if (diff < MINUTE)
			onFormatChanged("formatTimeWithSeconds(n)");
		else
			onFormatChanged("formatTime(n)");
	}
	private double toDouble(Number d) {
		return d == null ? Double.NaN : d.doubleValue();
	}

	@Override
	public String formatExact(Number val) {
		long t = val.longValue();
		if (type == AXIS_DATA_TYPE_UTCN) {
			if ((t % 1000) != 0) {
				onFormatChanged("formatTimeWithNanos(n)");
				return format(t);
			}
			if ((t % 1000000) != 0) {
				onFormatChanged("formatTimeWithMicros(n)");
				return format(t);
			}
			if ((t % 1000000000) != 0) {
				onFormatChanged("formatTimeWithMillis(n)");
				return format(t);
			} else if (t % 60000000000L != 0) {
				onFormatChanged("formatTimeWithSeconds(n)");
				return format(t);
			} else {
				onFormatChanged("formatTime(n)");
				return format(t);
			}
		}
		if ((t % 1000) != 0) {
			onFormatChanged("formatTimeWithMillis(n)");
			return format(t);
		} else if (t % 60000 != 0) {
			onFormatChanged("formatTimeWithSeconds(n)");
			return format(t);
		} else {
			onFormatChanged("formatTime(n)");
			return format(t);
		}
	}

}
