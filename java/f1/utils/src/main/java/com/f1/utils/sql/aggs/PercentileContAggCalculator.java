package com.f1.utils.sql.aggs;

import java.util.List;

import com.f1.base.CalcFrame;
import com.f1.utils.DoubleArrayList;
import com.f1.utils.MH;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class PercentileContAggCalculator extends AbstractAggCalculator {
	public static final String METHOD_NAME = "percentileCont";
	public final static ParamsDefinition paramsDefinition;
	static {
		paramsDefinition = new ParamsDefinition(METHOD_NAME, Double.class, "Number value,Number percent");
		paramsDefinition.addDesc("returns the value at the percentile provided.  Is interpolated and might not be equal to any value within the supplied dataset");
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
			return new PercentileContAggCalculator(position, calcs[0], calcs[1]);
		}
	};
	private Double value;
	private DerivedCellCalculator percent;
	private double percentConst;
	private boolean percentIsConst;
	private DoubleArrayList list;

	public PercentileContAggCalculator(int position, DerivedCellCalculator inner, DerivedCellCalculator percent) {
		super(position, inner, percent);
		this.percent = percent;
		if (!Number.class.isAssignableFrom(percent.getReturnType()))
			throw new ExpressionParserException(position, "percentile(...) second argument must be a number, not " + percent.getReturnType().getSimpleName());
		if (!Number.class.isAssignableFrom(inner.getReturnType()))
			throw new ExpressionParserException(position, "percentile(...) first argument must be a number, not " + percent.getReturnType().getSimpleName());
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
			return pct == null ? null : getAtPct(this.list, pct);
		}
	}
	@Override
	public void visit(ReusableCalcFrameStack sf, List<? extends CalcFrame> values) {
		if (percentIsConst && Double.isNaN(percentConst)) {
			this.value = null;
			return;
		}

		DoubleArrayList list = AggHelper.getDoubles(sf, values, this.inner);
		if (!percentIsConst) {
			this.list = list;
		} else
			this.value = getAtPct(list, this.percentConst);
	}

	private static Double getAtPct(DoubleArrayList list, double percent) {
		if (list == null)
			return null;
		int size = list.size();
		if (size == 1 || percent <= 0d) {
			return list.get(0);
		} else if (percent >= 1d) {
			return list.get(size - 1);
		} else {
			final double posRn = (size - 1) * percent;
			final int posFl = (int) Math.floor(posRn);
			final double deltaFl = posRn - posFl;
			return deltaFl < .0000001d ? list.getDouble(posFl) : ((list.getDouble(posFl) * (1d - deltaFl)) + (list.getDouble(posFl + 1) * (deltaFl)));
		}
	}
	@Override
	public DerivedCellCalculator copy() {
		return new PercentileContAggCalculator(getPosition(), inner.copy(), percent.copy());
	}
	@Override
	public Class<?> getReturnType() {
		return Double.class;
	}
	@Override
	public String getMethodName() {
		return "percentileCont";
	}
	@Override
	public boolean getOrderingMatters() {
		return true;
	}
	@Override
	public void setValue(Object object) {
		this.percentIsConst = true;
		this.value = (Double) object;
	}
	@Override
	public void visitRows(CalcFrameStack values, long count) {
		setValue(getReturnType().cast(inner.get(values)));
	}
}
