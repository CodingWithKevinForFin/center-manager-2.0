package com.f1.utils.sql.aggs;

import java.util.List;

import com.f1.base.CalcFrame;
import com.f1.utils.MH;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class PercentileDiscAggCalculator extends AbstractAggCalculator {
	public static final String METHOD_NAME = "percentileDisc";
	public final static ParamsDefinition paramsDefinition;
	static {
		paramsDefinition = new ParamsDefinition(METHOD_NAME, Double.class, "Number value,Number percent");
		paramsDefinition.addDesc("returns the value nearest to the percentile provided");
		paramsDefinition.addParamDesc(0, "column of dataset to consider");
		paramsDefinition.addParamDesc(1, "percentile between 0 and 1");
	}
	public final static AggMethodFactory FACTORY = new AggMethodFactory() {

		@Override
		public ParamsDefinition getDefinition() {
			return paramsDefinition;
		}

		@Override
		public AbstractAggCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new PercentileDiscAggCalculator(position, calcs[0], calcs[1]);
		}
	};
	private Object value;
	private DerivedCellCalculator percent;
	private double percentConst;
	private boolean percentIsConst;
	private Object[] list;
	private int listSize;

	public PercentileDiscAggCalculator(int position, DerivedCellCalculator inner, DerivedCellCalculator percent) {
		super(position, inner, percent);
		this.percent = percent;
		if (!Number.class.isAssignableFrom(percent.getReturnType()))
			throw new ExpressionParserException(position, "percentile(...) second argument must be a number, not " + percent.getReturnType().getSimpleName());
		this.percentIsConst = this.percent.isConst();
		if (this.percentIsConst) {
			Double t = Caster_Double.INSTANCE.cast(this.percent.get(null), false);
			this.percentConst = MH.isNumber(t) ? t : Double.NaN;
		} else
			this.percentConst = Double.NaN;

	}
	@Override
	public Object get(CalcFrameStack lcvs) {
		if (percentIsConst)
			return value;
		else {
			Double pct = Caster_Double.INSTANCE.cast(this.percent.get(lcvs));
			return getAtPct(this.list, this.listSize, pct);
		}
	}
	@Override
	public void visit(ReusableCalcFrameStack sf, List<? extends CalcFrame> values) {
		if (percentIsConst && Double.isNaN(percentConst)) {
			this.value = null;
			return;
		}

		Object[] list = null;
		int size = 0;
		for (int i = 0, origSize = values.size(); i < origSize; i++) {
			Object d = this.inner.get(sf.reset(values.get(i)));
			if (d != null) {
				if (list == null)
					list = new Object[origSize - i];
				list[size++] = d;
			}
		}
		if (!percentIsConst) {
			this.list = list;
			this.listSize = size;
		} else
			this.value = getAtPct(list, size, this.percentConst);
	}
	private static Object getAtPct(Object[] list, int size, double percent) {
		if (size == 0) {
			return null;
		} else if (size == 1 || percent <= 0d) {
			return list[0];
		} else if (percent >= 1d) {
			return list[size - 1];
		} else {
			final double posRn = (size - 1) * percent;
			final int posFl = (int) Math.floor(posRn);
			return list[posFl];
		}
	}
	@Override
	public DerivedCellCalculator copy() {
		return new PercentileDiscAggCalculator(getPosition(), inner.copy(), percent.copy());
	}
	@Override
	public Class<?> getReturnType() {
		return this.inner.getReturnType();
	}
	@Override
	public String getMethodName() {
		return "percentileDisc";
	}
	@Override
	public boolean getOrderingMatters() {
		return true;
	}
	@Override
	public void setValue(Object value) {
		this.percentIsConst = true;
		this.value = value;
	}
	@Override
	public void visitRows(CalcFrameStack values, long count) {
		setValue(getReturnType().cast(inner.get(values)));
	}
}
