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

public class InterpolationCalculator extends AbstractPrepCalculator {

	public static final String METHOD_NAME = "interpolate";
	public final static ParamsDefinition paramsDefinition;
	static {
		paramsDefinition = new ParamsDefinition(METHOD_NAME, String.class, "Number x,Number y,String method");
		//TODO: add desc
		paramsDefinition.addDesc(
				"forward interpolation between the current data point (xi,yi) and its subsequent data point(xi+1,yi+1),(xi+2,yi+2)..., using the method specified, returns the coefficients of the line/curve(interpolant). <br> In the case of a linear interpolation, the function returns the intercept and slope of the interpolant.");
		paramsDefinition.addParamDesc(0, "the data to interpolate on the X-axis");
		paramsDefinition.addParamDesc(1, "the data to interpolate on the Y-axis");
		paramsDefinition.addParamDesc(2, "the method used to interpolate the data, can be either linear or spline");
		paramsDefinition.addAdvancedExample(
				"CREATE TABLE input(symbol String, x int, y int);\nINSERT INTO input VALUES (\"CAT\", 1, 2),(\"CAT\", 2, 4),(\"CAT\", 3, 10),(\"CAT\", 4, 1),(\"CAT\", 5, 20);\nINSERT INTO input VALUES (\"IBM\", 1, 4),(\"IBM\", 2, 6),(\"IBM\", 3, 9),(\"IBM\", 4, 2),(\"IBM\", 5, 30);\nCREATE TABLE result AS PREPARE *,interpolate(x,y,\"linear\") as linear, interpolate(x,y,\"spline\") as spline from input PARTITION BY symbol;\nTable input = SELECT * FROM input;\nTABLE result = SELECT * FROM result;",
				new String[] { "input", "result" });
	}
	public final static PrepMethodFactory FACTORY = new PrepMethodFactory() {

		@Override
		public ParamsDefinition getDefinition() {
			return paramsDefinition;
		}

		@Override
		public AbstractPrepCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new InterpolationCalculator(position, calcs);
		}
	};
	private String[] output;
	private boolean isNull;
	private final DerivedCellCalculator inputXCalc;
	private final DerivedCellCalculator inputYCalc;
	private final DerivedCellCalculator methodCalc;
	private String method;

	public InterpolationCalculator(int position, DerivedCellCalculator[] inners) {
		super(position, inners);
		this.inputXCalc = inners[0];
		this.inputYCalc = inners[1];
		this.methodCalc = inners[2];
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
		String value = this.output[pos];
		return this.isNull || SH.isnt(value) ? null : value;
	}

	@Override
	public void visit(ReusableCalcFrameStack sf, PrepRows values) {
		if (values.isEmpty()) {
			this.isNull = true;
		} else {
			if (this.methodCalc instanceof DerivedCellCalculatorRef) {
				Object dxCalcVal = this.methodCalc.get(sf.reset(values.get(0)));
				this.method = dxCalcVal == null ? LAH.INTERP_METHOD_LINEAR : Caster_String.INSTANCE.cast(dxCalcVal);
			}
			if (!LAH.INTERP_METHODS.contains(this.method))
				throw new ExpressionParserException(this.methodCalc.getPosition(),
						"Illegal method argument: " + this.method + " (must be either: " + SH.join(',', LAH.INTERP_METHODS) + ")");
			this.isNull = false;

			int N = values.size();
			// Copy input into vectors
			double[] x = new double[N];
			double[] y = new double[N];
			Object objX, objY;
			for (int i = 0; i < N; i++) {
				sf.reset(values.get(i));
				objX = this.inputXCalc.get(sf);
				objY = this.inputYCalc.get(sf);
				if (objX instanceof Number && objY instanceof Number) {
					x[i] = Caster_Double.PRIMITIVE.cast(objX);
					y[i] = Caster_Double.PRIMITIVE.cast(objY);
				} else {
					x[i] = Double.NaN;
					y[i] = Double.NaN;
				}
			}
			try {
				double[][] params = LAH.interp(x, y, this.method);
				StringBuilder sb;
				this.output = new String[N];
				int numSegments = params.length;
				int numParams = params[0].length;
				for (int i = 0; i < numSegments; i++) {
					sb = new StringBuilder();
					for (int j = 0; j < numParams; j++) {
						sb.append(params[i][j]);
						if (j < numParams - 1) {
							sb.append(",");
						}
					}
					this.output[i] = sb.toString();
				}
				this.output[N - 1] = null;
			} catch (Exception e) {
				throw new ExpressionParserException(-1, e.getMessage(), e.getCause());
			}
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
		return new InterpolationCalculator(getPosition(), copyInners());
	}

}
