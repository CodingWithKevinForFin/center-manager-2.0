package com.f1.utils.sql.aggs;

import java.util.List;

import com.f1.base.CalcFrame;
import com.f1.utils.DoubleArrayList;
import com.f1.utils.MH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class ExponentialAvgAggCalculator extends AbstractAggCalculator {
	public static final String METHOD_NAME = "avgExp";
	public final static ParamsDefinition paramsDefinition;
	static {
		paramsDefinition = new ParamsDefinition(METHOD_NAME, Double.class, "Number value,Number decay,Boolean desc");
		paramsDefinition
				.addDesc("The exponentially weighted average of non-null values with a specified decay rate. Desc of true/false means first/last row are highest weighted.");
		paramsDefinition.addParamDesc(0, "");
		paramsDefinition.addParamDesc(1, "");
		paramsDefinition.addParamDesc(2, "");
	}
	public final static AggMethodFactory FACTORY = new AggMethodFactory() {

		@Override
		public ParamsDefinition getDefinition() {
			return paramsDefinition;
		}

		@Override
		public AbstractAggCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new ExponentialAvgAggCalculator(position, calcs[0], calcs[1], calcs[2]);
		}
	};
	private static double MIN_THRESHOLD = 1e-6;
	private double r; // Common ratio (decay factor)
	private Boolean desc; // Descending/Ascending
	private Double output;
	private final DerivedCellCalculator rCalc;
	private final DerivedCellCalculator descCalc;

	public ExponentialAvgAggCalculator(int position, DerivedCellCalculator inner, DerivedCellCalculator rCalc, DerivedCellCalculator descCalc) {
		super(position, inner, rCalc, descCalc);
		this.rCalc = rCalc;
		this.descCalc = descCalc;
		if (!Number.class.isAssignableFrom(inner.getReturnType())) {
			throw new ExpressionParserException(position, getMethodName() + "(...) first argument must be a number, not " + rCalc.getReturnType().getSimpleName());
		}
		if (!Number.class.isAssignableFrom(rCalc.getReturnType()) || !rCalc.isConst()) {
			throw new ExpressionParserException(position, getMethodName() + "(...) param decay must be a constant number, not " + rCalc.getReturnType().getSimpleName());
		}
		if (descCalc.getReturnType() != Boolean.class || !descCalc.isConst()) {
			throw new ExpressionParserException(position, getMethodName() + "(...) param desc must be constant boolean, not " + descCalc.getReturnType().getSimpleName());
		}
		Double t = Caster_Double.INSTANCE.cast(rCalc.get(null), false);
		if (Math.abs(t) >= 1) {
			throw new ExpressionParserException(position, "Absolute value of decay rate must be less than 1.");
		}
		this.r = MH.isNumber(t) ? t : Double.NaN;
		this.desc = Caster_Boolean.INSTANCE.cast(descCalc.get(null), false);
	}

	@Override
	public Object get(CalcFrameStack lcvs) {
		return this.output;
	}

	@Override
	protected void visit(ReusableCalcFrameStack sf, List<? extends CalcFrame> values) {
		if ((Double.isNaN(this.r)) || this.desc == null) {
			this.output = null;
		} else {
			DoubleArrayList list = AggHelper.getDoubles(sf, values, this.inner);
			this.output = calculateAvgExp(list, this.r, this.desc);
		}
	}
	private static Double calculateAvgExp(DoubleArrayList list, double decay, boolean desc) {
		if (list == null || Math.abs(decay) >= 1) {
			return null;
		}
		int size = list.size();
		if (size == 0)
			return null;
		if (size == 1) {
			return list.get(0);
		}

		double sum = 0;
		double rProd = 1;
		if (desc)
			for (int i = 0; i < size; i++) {
				sum += list.getDouble(i) * rProd;
				rProd *= decay;
				if (rProd < MIN_THRESHOLD) {
					break;
				}
			}
		else
			for (int i = 1; i <= size; i++) {
				sum += list.getDouble(size - i) * rProd;
				rProd *= decay;
				if (rProd < MIN_THRESHOLD) {
					break;
				}
			}
		return (1.0 - decay) / (1.0 - rProd) * sum;
	}
	@Override
	public String getMethodName() {
		return METHOD_NAME;
	}

	@Override
	public Class<?> getReturnType() {
		return Double.class;
	}

	@Override
	public DerivedCellCalculator copy() {
		return new ExponentialAvgAggCalculator(getPosition(), this.inner.copy(), this.rCalc.copy(), this.descCalc.copy());
	}

	@Override
	public boolean getOrderingMatters() {
		return true;
	}

	@Override
	public void setValue(Object object) {
		this.output = (Double) object;
	}
	@Override
	public void visitRows(CalcFrameStack values, long count) {
		setValue(Caster_Double.INSTANCE.cast(inner.get(values), false, false));
	}

}
