package com.f1.utils.sql.aggs;

import java.util.List;

import com.f1.base.CalcFrame;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorRef;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class PolynomialLinearRegressionAggCalculator extends LinearRegressionAggCalculator {
	public static final String METHOD_NAME = "linRegPoly";
	public final static ParamsDefinition paramsDefinition;
	static {
		paramsDefinition = new ParamsDefinition(METHOD_NAME, String.class, "Number target,Number ... values");
		//TODO: add desc
		paramsDefinition.addDesc("");
		paramsDefinition.addParamDesc(0, "");
	}
	public final static AggMethodFactory FACTORY = new AggMethodFactory() {

		@Override
		public ParamsDefinition getDefinition() {
			return paramsDefinition;
		}

		@Override
		public AbstractAggCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new PolynomialLinearRegressionAggCalculator(position, calcs);
		}
	};

	final private DerivedCellCalculator targetCalc;
	final private DerivedCellCalculator[] dataCalcs;
	final private DerivedCellCalculator orderCalc;
	final private int numDataInputs;
	private int order;

	public PolynomialLinearRegressionAggCalculator(int position, DerivedCellCalculator[] inners) {
		super(position, inners);
		int nParams = inners.length;
		this.numDataInputs = nParams - 2;
		this.targetCalc = inners[0];
		this.dataCalcs = new DerivedCellCalculator[this.numDataInputs];
		for (int i = 0; i < this.numDataInputs; i++) {
			this.dataCalcs[i] = inners[i + 1];
		}
		this.orderCalc = inners[nParams - 1];
		if (this.orderCalc.isConst()) {
			Object orderCalcVal = this.orderCalc.get(null);
			this.order = orderCalcVal == null ? 1 : Caster_Integer.PRIMITIVE.cast(orderCalcVal);
		}
	}

	@Override
	protected void visit(ReusableCalcFrameStack sf, List<? extends CalcFrame> values) {
		if (values.isEmpty()) {
			setValue(null);
		} else {
			if (this.orderCalc instanceof DerivedCellCalculatorRef) {
				Object orderCalcVal = this.orderCalc.get(sf.reset(values.get(0)));
				this.order = orderCalcVal == null ? 1 : Caster_Integer.PRIMITIVE.cast(orderCalcVal);
			}
			int N = values.size();
			int numColsPhi = this.numDataInputs * this.order + 1;
			double[][] Phi = new double[N][numColsPhi];
			double[] t = new double[N];
			Double dataElement, targetElement;
			boolean targetElementNull;
			int nullCnt = 0;
			sampleLoop: for (int i = 0; i < N; i++) { // Build design matrix
				Phi[i][0] = 1;
				sf.reset(values.get(i));
				targetElement = Caster_Double.INSTANCE.cast(this.targetCalc.get(sf));
				targetElementNull = targetElement == null;
				if (!targetElementNull) {
					t[i - nullCnt] = targetElement;
				}
				for (int j = 0; j < this.numDataInputs; j++) {
					dataElement = Caster_Double.INSTANCE.cast(this.dataCalcs[j].get(sf));
					if (dataElement == null || targetElementNull) {
						nullCnt++;
						continue sampleLoop;
					} else {
						for (int k = 1; k <= this.order; k++) {
							Phi[i - nullCnt][j + k] = Math.pow(dataElement, k);
						}
					}
				}
			}
			applyLinearRegression(Phi, t, nullCnt);
		}
	}
	@Override
	public String getMethodName() {
		return METHOD_NAME;
	}

	@Override
	public Class<?> getReturnType() {
		return String.class;
	}

	@Override
	public DerivedCellCalculator copy() {
		return new PolynomialLinearRegressionAggCalculator(getPosition(), copyInners());
	}

}
