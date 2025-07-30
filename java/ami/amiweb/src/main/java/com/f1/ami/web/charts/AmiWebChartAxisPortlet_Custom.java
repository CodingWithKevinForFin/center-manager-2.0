package com.f1.ami.web.charts;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.utils.structs.table.stack.SingletonCalcFrame;

public class AmiWebChartAxisPortlet_Custom extends AmiWebChartAxisFormatter {

	private SingletonCalcFrame tmp = new SingletonCalcFrame(AmiWebChartAxisPortlet.VAR_NAME, Number.class);

	public AmiWebChartAxisPortlet_Custom(AmiWebChartAxisPortlet axis) {
		super(axis, axis.getFormulas());
	}

	//TODO: make abstract
	@Override
	public String format(Number val) {
		if (formatter == null)
			return AmiUtils.s(val);
		tmp.setValue(val);
		return AmiUtils.s(formatter.get(getStackFrame().reset(tmp)));
	}

	@Override
	public double calcUnitSize(Number d, Number diff, byte type) {
		return AmiWebChartUtils.calcUnitSize(toDouble(d), toDouble(diff));
	}
	@Override
	public double calcMinorUnitSize(Number majorUnit) {
		return AmiWebChartUtils.calcMinorUnitSize(toDouble(majorUnit));
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
	public void useDefaultFormatter(Number d, Number min, Number max, byte type) {
	}

	private double toDouble(Number d) {
		return d == null ? Double.NaN : d.doubleValue();
	}

	@Override
	public String formatExact(Number val) {
		return format(val);
	}

}
