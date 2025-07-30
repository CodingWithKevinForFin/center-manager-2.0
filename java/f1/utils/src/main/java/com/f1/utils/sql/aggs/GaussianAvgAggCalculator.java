package com.f1.utils.sql.aggs;

import java.util.List;

import com.f1.base.CalcFrame;
import com.f1.utils.DoubleArrayList;
import com.f1.utils.LAH;
import com.f1.utils.MH;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class GaussianAvgAggCalculator extends AbstractAggCalculator {
	public static final String METHOD_NAME = "avgGauss";
	public final static ParamsDefinition paramsDefinition;
	static {
		paramsDefinition = new ParamsDefinition(METHOD_NAME, Double.class, "Number value,Number variance");
		paramsDefinition.addDesc("The Gaussian weighted average of non-null values, such that the middle value has highest weight.");
		paramsDefinition.addParamDesc(0, "");
		paramsDefinition.addParamDesc(1, "");
	}
	public final static AggMethodFactory FACTORY = new AggMethodFactory() {

		@Override
		public ParamsDefinition getDefinition() {
			return paramsDefinition;
		}

		@Override
		public AbstractAggCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new GaussianAvgAggCalculator(position, calcs[0], calcs[1]);
		}
	};
	private static double MIN_THRESHOLD = 1e-6;
	private double var;
	private Double output;
	private final DerivedCellCalculator varCalc;
	private boolean varIsConst;
	private DoubleArrayList list;

	public GaussianAvgAggCalculator(int position, DerivedCellCalculator inner, DerivedCellCalculator varCalc) {
		super(position, inner, varCalc);
		this.varCalc = varCalc;
		if (!Number.class.isAssignableFrom(inner.getReturnType())) {
			throw new ExpressionParserException(position, getMethodName() + "(...) first argument must be a number, not " + varCalc.getReturnType().getSimpleName());
		}
		if (!Number.class.isAssignableFrom(varCalc.getReturnType())) {
			throw new ExpressionParserException(position, getMethodName() + "(...) param var must be a number, not " + varCalc.getReturnType().getSimpleName());
		}
		this.varIsConst = this.varCalc.isConst();
		if (this.varIsConst) {
			Double t = Caster_Double.INSTANCE.cast(this.varCalc.get(null), false);
			if (t < 0) {
				throw new ExpressionParserException(position, "Variance must be positive.");
			}
			this.var = MH.isNumber(t) ? t : Double.NaN;
		} else {
			this.var = Double.NaN;
		}
	}

	@Override
	public Object get(CalcFrameStack lcvs) {
		if (this.varIsConst) {
			return this.output;
		} else {
			Double var = Caster_Double.INSTANCE.cast(this.varCalc.get(lcvs));
			return var == null ? null : calculateAvgGauss(this.list, var);
		}
	}

	@Override
	protected void visit(ReusableCalcFrameStack sf, List<? extends CalcFrame> values) {
		if (this.varIsConst && Double.isNaN(this.var)) {
			this.output = null;
			return;
		}

		DoubleArrayList list = AggHelper.getDoubles(sf, values, this.inner);
		if (!this.varIsConst) {
			this.list = list;
		} else {
			this.output = calculateAvgGauss(list, this.var);
		}
	}
	private static Double calculateAvgGauss(DoubleArrayList list, double var) {
		if (list == null || var < 0) {
			return null;
		}
		int size = list.size();
		if (size == 0)
			return null;
		if (size == 1) {
			return list.get(0);
		}

		double weightSum = 0;
		double weight;
		double den = 2 * var;
		double sum = 0;
		int middle = size / 2;
		if (size % 2 == 0) { // even number of samples
			for (int i = 0; i < middle; i++) {
				double arg = i + 0.5;
				weight = Math.exp(-(arg * arg) / den);
				sum += (list.getDouble(middle - i - 1) + list.getDouble(middle + i)) * weight;
				weightSum += 2 * weight;
				if (weight < MIN_THRESHOLD) {
					break;
				}
			}
		} else { // odd number of samples
			// Middle element
			weight = 1;
			sum = list.getDouble(middle);
			weightSum++;
			// Rest of elements
			for (int i = 1; i <= middle; i++) {
				weight = Math.exp(-((double) i * i) / den);
				sum += (list.getDouble(middle - i) + list.getDouble(middle + i)) * weight;
				weightSum += 2 * weight;
				if (weight < MIN_THRESHOLD) {
					break;
				}
			}
		}
		return sum / weightSum;
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
		return new GaussianAvgAggCalculator(getPosition(), this.inner.copy(), this.varCalc.copy());
	}

	private void autoCalculateVariance(int N) {
		this.var = LAH.calculateGaussianFilterVariance(N, 0.05);
	}

	@Override
	public boolean getOrderingMatters() {
		return true;
	}

	@Override
	public void setValue(Object object) {
		this.varIsConst = true;
		this.output = (Double) object;
	}
	@Override
	public void visitRows(CalcFrameStack values, long count) {
		setValue(Caster_Double.INSTANCE.cast(inner.get(values), false, false));
	}

}
