package com.f1.ami.web.charts;

public class AmiWebChartAxisPortlet_Auto extends AmiWebChartAxisFormatter {

	private AmiWebChartAxisFormatter inner;
	private byte type = -1;

	public AmiWebChartAxisPortlet_Auto(AmiWebChartAxisPortlet axis) {
		super(axis, axis.getFormulas());
	}

	@Override
	public String format(Number val) {
		if (type == AXIS_DATA_TYPE_CONST)
			return null;
		return inner.format(val);
	}

	@Override
	public double calcUnitSize(Number m, Number df, byte type) {
		updateInner(type);
		if (type == AXIS_DATA_TYPE_CONST)
			return df.doubleValue();
		return this.inner.calcUnitSize(m, df, type);
	}

	@Override
	public Number roundUp(Number max, Number size) {
		if (type == AXIS_DATA_TYPE_CONST)
			return max.doubleValue() + size.doubleValue() * .1;
		return this.inner.roundUp(max, size);
	}

	@Override
	public Number roundDown(Number min, Number size) {
		if (type == AXIS_DATA_TYPE_CONST)
			return min.doubleValue() - size.doubleValue() * .1;
		return this.inner.roundDown(min, size);
	}

	@Override
	public double calcMinorUnitSize(Number majorUnit) {
		if (type == AXIS_DATA_TYPE_CONST)
			return Double.MAX_VALUE;
		return this.inner.calcMinorUnitSize(majorUnit);
	}

	@Override
	public void useDefaultFormatter(Number d, Number mi, Number ma, byte type) {
		updateInner(type);
		if (type == AXIS_DATA_TYPE_CONST)
			return;
		this.inner.useDefaultFormatter(d, mi, ma, type);
	}

	private void updateInner(byte type) {
		if (type != this.type) {
			switch (type) {
				case AXIS_DATA_TYPE_CONST:
					this.inner = null;
					break;
				case AXIS_DATA_TYPE_REAL:
				case AXIS_DATA_TYPE_WHOLE:
					this.inner = new AmiWebChartAxisPortlet_Number(this.axis);
					break;
				case AXIS_DATA_TYPE_UTC:
				case AXIS_DATA_TYPE_UTCN:
					this.inner = new AmiWebChartAxisPortlet_Datetime(this.axis, false);
					break;
				case AXIS_DATA_TYPE_UNKNOWN:
					this.inner = new AmiWebChartAxisPortlet_Custom(this.axis);
			}
			this.type = type;
		}

	}

	@Override
	public String formatExact(Number min) {
		if (inner == null)
			this.inner = new AmiWebChartAxisPortlet_Number(this.axis);
		return this.inner.formatExact(min);
	}

}
