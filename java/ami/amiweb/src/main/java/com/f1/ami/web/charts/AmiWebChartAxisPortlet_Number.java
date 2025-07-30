package com.f1.ami.web.charts;

import java.math.BigDecimal;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.utils.casters.Caster_BigDecimal;
import com.f1.utils.structs.table.stack.SingletonCalcFrame;

public class AmiWebChartAxisPortlet_Number extends AmiWebChartAxisFormatter {

	private SingletonCalcFrame tmp = new SingletonCalcFrame(AmiWebChartAxisPortlet.VAR_NAME, Number.class);

	public AmiWebChartAxisPortlet_Number(AmiWebChartAxisPortlet axis) {
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
		double diff = toDouble(d);
		if (diff < 1) {
			onFormatChanged("formatDecimal(n," + getDecimals(diff) + ")");
		} else if (diff < 1000)
			onFormatChanged("formatInteger(n)");
		else if (diff < 1000000)
			onFormatChanged("formatInteger(n/1000D)+\"k\"");
		else if (diff < 1000000000)
			onFormatChanged("formatInteger(n/1000000D)+\"m\"");
		else if (diff < 1000000000000L)
			onFormatChanged("formatInteger(n/1000000000D)+\"b\"");
		else
			onFormatChanged("formatInteger(n/1000000000000D)+\"t\"");
	}

	static private int getDecimals(double diff) {
		int dec = 0;
		double test = .1;
		while (dec++ < 100 && diff < test)
			test /= 10d;
		return dec;
	}

	private double toDouble(Number d) {
		return d == null ? Double.NaN : d.doubleValue();
	}

	@Override
	public String formatExact(Number min) {
		BigDecimal bd = Caster_BigDecimal.INSTANCE.cast(min);
		return bd.stripTrailingZeros().toPlainString();
	}

}
