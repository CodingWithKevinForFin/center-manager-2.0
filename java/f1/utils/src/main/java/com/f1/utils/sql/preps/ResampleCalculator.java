package com.f1.utils.sql.preps;

import com.f1.utils.LAH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorRef;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class ResampleCalculator extends AbstractPrepCalculator {

	public static final String METHOD_NAME = "resample";
	public final static ParamsDefinition paramsDefinition;
	static {
		paramsDefinition = new ParamsDefinition(METHOD_NAME, Object.class, "Number x,Number y,Number xq,String linearOrSpline");
		paramsDefinition.addDesc("Resample y to new x values using linear or spline approximation");
		paramsDefinition.addParamDesc(0, "");
		paramsDefinition.addParamDesc(1, "");
		paramsDefinition.addParamDesc(2, "");
		paramsDefinition.addParamDesc(3, "");
		paramsDefinition.addAdvancedExample(
				"CREATE TABLE input(symbol String, x int, y int);\nINSERT INTO input VALUES (\"CAT\", 1, 2),(\"CAT\", 3, 4),(\"CAT\", 5, 10),(\"CAT\", 7, 1),(\"CAT\", 9, 20);\nINSERT INTO input VALUES (\"IBM\", 1, 4),(\"IBM\", 3, 6),(\"IBM\", 5, 9),(\"IBM\", 7, 2),(\"IBM\", 9, 30);\nCREATE TABLE result AS PREPARE *,x+1 as new_x, resample(x,y,x+1,\"linear\") as new_y_linear, resample(x,y,x+1,\"spline\") as new_y_spline from input PARTITION BY symbol;\nTable input = SELECT * FROM input;\nTABLE result = SELECT * FROM result;",
				new String[] { "input", "result" });
	}
	public final static PrepMethodFactory FACTORY = new PrepMethodFactory() {

		@Override
		public ParamsDefinition getDefinition() {
			return paramsDefinition;
		}

		@Override
		public AbstractPrepCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new ResampleCalculator(position, calcs);
		}
	};
	private final DerivedCellCalculator inputXCalc; // Original x values
	private final DerivedCellCalculator inputYCalc; // Original y values
	private final DerivedCellCalculator inputXqCalc; // Query x values
	private final DerivedCellCalculator methodCalc;
	private double[] output;
	private String method;
	private boolean isNull;

	public ResampleCalculator(int position, DerivedCellCalculator[] inners) {
		super(position, inners);
		this.inputXCalc = inners[0];
		this.inputYCalc = inners[1];
		this.inputXqCalc = inners[2];
		this.methodCalc = inners[3];
		if (this.methodCalc.getReturnType() != String.class || (!this.methodCalc.isConst() && !(this.methodCalc instanceof DerivedCellCalculatorRef))) {
			throw new ExpressionParserException(position, getMethodName() + "(...) param method must be String constant");
		}
		if (this.methodCalc.isConst()) {
			Object methodCalcVal = methodCalc.get(null);
			this.method = methodCalcVal == null ? LAH.INTERP_METHOD_LINEAR : Caster_String.INSTANCE.cast(methodCalcVal);
		}
	}

	@Override
	public Object get(CalcFrameStack lcvs, int pos) {
		double value = this.output[pos];
		return returnNull(value) ? null : value;
	}

	private boolean returnNull(double value) {
		return this.isNull || value == Double.NaN;
	}

	@Override
	protected void visit(ReusableCalcFrameStack sf, PrepRows values) {
		if (values.isEmpty()) {
			this.isNull = true;
		} else {
			if (this.methodCalc instanceof DerivedCellCalculatorRef) {
				Object dxCalcVal = this.methodCalc.get(sf.reset(values.get(0)));
				this.method = dxCalcVal == null ? LAH.INTERP_METHOD_LINEAR : Caster_String.INSTANCE.cast(dxCalcVal);
			}
			if (!LAH.INTERP_METHODS.contains(this.method))
				throw new IllegalArgumentException("Illegal method argument: " + this.method + " (must be either: " + SH.join(',', LAH.INTERP_METHODS) + ")");
			this.isNull = false;

			// Copy input into vectors
			int N = values.size();
			double[] x = new double[N];
			double[] y = new double[N];
			double[] xq = new double[N];
			Object oX, oY, oXq;
			for (int i = 0; i < N; i++) {
				sf.reset(values.get(i));
				oX = this.inputXCalc.get(sf);
				oY = this.inputYCalc.get(sf);
				oXq = this.inputXqCalc.get(sf);
				if (oX instanceof Number && oY instanceof Number && oXq instanceof Number) {
					x[i] = Caster_Double.PRIMITIVE.cast(oX);
					y[i] = Caster_Double.PRIMITIVE.cast(oY);
					xq[i] = Caster_Double.PRIMITIVE.cast(oXq);
				} else {
					x[i] = Double.NaN;
					y[i] = Double.NaN;
					xq[i] = Double.NaN;
				}
			}
			this.output = LAH.resample(x, y, xq, this.method);
		}
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
		return new ResampleCalculator(getPosition(), copyInners());
	}

}
