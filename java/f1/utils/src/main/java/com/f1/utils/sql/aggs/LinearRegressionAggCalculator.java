package com.f1.utils.sql.aggs;

import java.util.List;

import com.f1.base.CalcFrame;
import com.f1.utils.LAH;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class LinearRegressionAggCalculator extends AbstractAggCalculator {
	final public static String METHOD_NAME = "linearRegression";
	public final static ParamsDefinition paramsDefinition;
	static {
		paramsDefinition = new ParamsDefinition(METHOD_NAME, String.class, "Number value");
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
			return new LinearRegressionAggCalculator(position, calcs);
		}
	};

	final private DerivedCellCalculator targetCalc;
	final private DerivedCellCalculator[] dataCalcs;
	private final int numDataInputs;
	private String output;

	public LinearRegressionAggCalculator(int position, DerivedCellCalculator[] inners) {
		super(position, inners);
		int nParams = inners.length;
		this.targetCalc = inners[0];
		this.numDataInputs = nParams - 1;
		this.dataCalcs = new DerivedCellCalculator[this.numDataInputs];
		for (int i = 0; i < this.numDataInputs; i++) {
			this.dataCalcs[i] = inners[i + 1];
		}
	}

	@Override
	public Object get(CalcFrameStack lcvs) {
		return this.output;
	}

	@Override
	protected void visit(ReusableCalcFrameStack sf, List<? extends CalcFrame> values) {
		if (values.isEmpty()) {
			this.output = null;
		} else {
			int N = values.size();
			int numColsPhi = this.numDataInputs + 1;
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
						Phi[i - nullCnt][j + 1] = dataElement;
					}
				}
			}
			applyLinearRegression(Phi, t, nullCnt);
		}
	}

	protected void applyLinearRegression(double[][] Phi, double[] t, int nullCnt) {
		int N = Phi.length;
		int numColsPhi = Phi[0].length;
		if (nullCnt != 0) { // Generate new matrix with only non-null values
			Phi = LAH.getSection(Phi, 0, N - 1 - nullCnt, 0, numColsPhi - 1);
			t = LAH.getSection(t, 0, N - 1 - nullCnt);
		}
		if (t.length == 0) {
			this.output = "";
			return;
		}
		double[] w = LAH.linearRegression(t, Phi);
		StringBuilder output = new StringBuilder();
		int numWeights = w.length;
		for (int i = 0; i < numWeights; i++) {
			output.append(w[i]);
			if (i < numWeights - 1) {
				output.append(",");
			}
		}
		this.output = output.toString();
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
		return new LinearRegressionAggCalculator(getPosition(), copyInners());
	}
	@Override
	public void setValue(Object object) {
		this.output = (String) object;
	}
	@Override
	public void visitRows(CalcFrameStack values, long count) {
		setValue("" + Caster_Double.INSTANCE.cast(inner.get(values), false, false));
	}

}
