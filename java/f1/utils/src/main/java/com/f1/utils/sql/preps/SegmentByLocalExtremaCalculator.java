package com.f1.utils.sql.preps;

import com.f1.base.CalcFrame;
import com.f1.utils.LAH;
import com.f1.utils.MH;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorRef;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class SegmentByLocalExtremaCalculator extends AbstractPrepCalculator {

	public static final String METHOD_NAME = "segmentByLocalExtrema";
	public final static ParamsDefinition paramsDefinition;
	static {
		paramsDefinition = new ParamsDefinition(METHOD_NAME, Integer.class, "Number x,Number y,Number dx,Number dataFilterLength,Number derivedFilterLength");
		//TODO: add desc
		paramsDefinition.addDesc("segment data based on local extremas of function f(x,y)");
		paramsDefinition.addParamDesc(0, "Number array x");
		paramsDefinition.addParamDesc(1, "Number array y");
		paramsDefinition.addParamDesc(2, "step size for calculating the derivate");
		paramsDefinition.addParamDesc(3, "Filter length used in the low-pass filter to smooth the number array y. If the length is equal to 1, smoothing is skipped");
		paramsDefinition.addParamDesc(4, "Filter length used in the low-pass filter to smooth the derivate. If the length is equal to 1, smoothing is skipped");
		paramsDefinition.addAdvancedExample(
				"CREATE TABLE input(x int, y int);\nINSERT INTO input VALUES (1,50), (2,200), (3,400), (4,50), (5,250), (6,300);\nCREATE TABLE result AS PREPARE *, segmentByLocalExtrema(x, y, 1, 1, 1) as segment from input;\nTable input = SELECT * FROM input;\nTABLE result = SELECT * FROM result;",
				new String[] { "input", "result" });
	}
	public final static PrepMethodFactory FACTORY = new PrepMethodFactory() {

		@Override
		public ParamsDefinition getDefinition() {
			return paramsDefinition;
		}

		@Override
		public AbstractPrepCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new SegmentByLocalExtremaCalculator(position, calcs);
		}
	};
	private final DerivedCellCalculator inputXCalc;
	private final DerivedCellCalculator inputYCalc;
	private int dataFilterLength;
	private int derivFilterLength;
	private int[] output;
	private boolean isNull;
	private static final int NONE = -1;
	private int dx;
	private final DerivedCellCalculator dxCalc;
	private final DerivedCellCalculator dataFilterLengthCalc;
	private final DerivedCellCalculator derivFilterLengthCalc;

	public SegmentByLocalExtremaCalculator(int position, DerivedCellCalculator[] inners) {
		super(position, inners);
		this.inputXCalc = inners[0];
		this.inputYCalc = inners[1];
		this.dxCalc = inners[2];
		this.dataFilterLengthCalc = inners[3];
		this.derivFilterLengthCalc = inners[4];
		if (!Number.class.isAssignableFrom(this.dxCalc.getReturnType()) || (!this.dxCalc.isConst() && !(this.dxCalc instanceof DerivedCellCalculatorRef))) {
			throw new ExpressionParserException(position, getMethodName() + "(...) param dx must be integer constant");
		}
		if (!Number.class.isAssignableFrom(this.dataFilterLengthCalc.getReturnType())
				|| (!this.dataFilterLengthCalc.isConst() && !(this.dataFilterLengthCalc instanceof DerivedCellCalculatorRef))) {
			throw new ExpressionParserException(position, getMethodName() + "(...) param dataFilterLength must be integer constant");
		}
		if (!Number.class.isAssignableFrom(this.derivFilterLengthCalc.getReturnType())
				|| (!this.derivFilterLengthCalc.isConst() && !(this.derivFilterLengthCalc instanceof DerivedCellCalculatorRef))) {
			throw new ExpressionParserException(position, getMethodName() + "(...) param derivFilterLength must be integer constant");
		}
		if (this.dxCalc.isConst()) {
			Object dxCalcVal = dxCalc.get(null);
			this.dx = dxCalcVal == null ? 1 : Caster_Integer.PRIMITIVE.cast(dxCalcVal);
		}
		if (this.dataFilterLengthCalc.isConst()) {
			Object dataFilterLengthCalcVal = dataFilterLengthCalc.get(null);
			this.dataFilterLength = dataFilterLengthCalcVal == null ? 1 : Caster_Integer.PRIMITIVE.cast(dataFilterLengthCalcVal);
		}
		if (this.derivFilterLengthCalc.isConst()) {
			Object derivFilterLengthCalcVal = derivFilterLengthCalc.get(null);
			this.derivFilterLength = derivFilterLengthCalcVal == null ? 1 : Caster_Integer.PRIMITIVE.cast(derivFilterLengthCalcVal);
		}
	}
	@Override
	public Object get(CalcFrameStack lcvs, int pos) {
		int value = this.output[pos];
		return returnNull(value) ? null : value;
	}

	private boolean returnNull(int value) {
		return this.isNull || value == NONE;
	}

	@Override
	protected void visit(ReusableCalcFrameStack sf, PrepRows values) {
		if (values.isEmpty()) {
			this.isNull = true;
		} else {
			sf.reset(values.get(0));
			if (this.dxCalc instanceof DerivedCellCalculatorRef) {
				Object dxCalcVal = this.dxCalc.get(sf);
				this.dx = dxCalcVal == null ? 1 : Caster_Integer.PRIMITIVE.cast(dxCalcVal);
				if (this.dx < 1) {
					throw new IllegalArgumentException("Illegal value for dx: dx must be greater than or equal to 1.");
				}
			}
			if (this.dataFilterLengthCalc instanceof DerivedCellCalculatorRef) {
				Object dataFilterLengthCalcVal = this.dataFilterLengthCalc.get(sf);
				this.dataFilterLength = dataFilterLengthCalcVal == null ? 1 : Caster_Integer.PRIMITIVE.cast(dataFilterLengthCalcVal);
				if (this.dataFilterLength < 1) {
					throw new IllegalArgumentException("Illegal value for dataFilterLength: dataFilterLength must be greater than or equal to 1.");
				}
			}
			if (this.derivFilterLengthCalc instanceof DerivedCellCalculatorRef) {
				Object derivFilterLengthCalcVal = this.derivFilterLengthCalc.get(sf);
				this.derivFilterLength = derivFilterLengthCalcVal == null ? 1 : Caster_Integer.PRIMITIVE.cast(derivFilterLengthCalcVal);
				if (this.derivFilterLength < 1) {
					throw new IllegalArgumentException("Illegal value for derivFilterLength: derivFilterLength must be greater than or equal to 1.");
				}
			}
			this.isNull = false;

			int N = values.size();
			this.output = new int[N];
			CalcFrame row;
			double[] inputX = new double[N];
			double[] inputY = new double[N];
			Object objXCur, objYCur;
			// Get data into arrays
			for (int i = 0; i < N; i += this.dx) { // Handle case where dx >= N
				sf.reset(values.get(i));
				objXCur = this.inputXCalc.get(sf);
				objYCur = this.inputYCalc.get(sf);
				if (objXCur instanceof Number && objYCur instanceof Number) {
					inputX[i] = Caster_Double.PRIMITIVE.cast(objXCur);
					inputY[i] = Caster_Double.PRIMITIVE.cast(objYCur);
				} else {
					this.output[i] = NONE;
					inputX[i] = Double.NaN;
					inputY[i] = Double.NaN;
				}
			}

			// Low-pass filter inputY
			double[] inputYSmooth = this.dataFilterLength == 1 ? inputY
					: LAH.convolveNaiveSame(inputY, LAH.generateGaussianFilter(Caster_Integer.PRIMITIVE.cast(this.dataFilterLength)));

			double yPrev = Double.NaN;
			double derivativePrev = Double.NaN;
			double yCur;
			double derivativeCur;
			int group = 0;
			boolean allPrevsNull = true;
			double[] derivative = new double[N];

			// Calculate derivative
			for (int i = 0; i < N - this.dx; i += this.dx) {
				yCur = inputYSmooth[i];
				if (MH.isntNumber(yCur)) {
					derivative[i] = Double.NaN;
					continue;
				}
				if (allPrevsNull) { // If we haven't found any non-null values yet
					allPrevsNull = false;
				} else {
					derivative[i] = (yCur - yPrev) / this.dx;
				}
				yPrev = yCur;
			}

			// Low-pass filter derivative
			double[] derivSmooth = this.derivFilterLength == 1 ? derivative
					: LAH.convolveNaiveSame(derivative, LAH.generateGaussianFilter(Caster_Integer.PRIMITIVE.cast(this.derivFilterLength)));
			allPrevsNull = true;
			int iFinal = 0;
			for (int i = 0; i < N - this.dx; i += this.dx) { // Handle case where dx >= N
				derivativeCur = derivSmooth[i];
				if (MH.isntNumber(derivativeCur)) {
					continue;
				}
				if (allPrevsNull) { // If we haven't found any non-null values yet
					allPrevsNull = false;
				} else {
					if (Math.signum(derivativePrev) != Math.signum(derivativeCur)) {
						group++;
					}
					this.output[i] = group;
					for (int j = i - this.dx + 1; j < i; j++) {
						this.output[j] = group;
					}
					derivativePrev = derivativeCur;
					iFinal = i;
				}
			}

			// Can't calculate derivative for last data point; just use last group
			for (int i = iFinal + 1; i < N; i++) {
				if (MH.isNumber(derivSmooth[i])) {
					this.output[i] = group;
				}
			}
		}
	}
	@Override
	public String getMethodName() {
		return METHOD_NAME;
	}

	@Override
	public Class<?> getReturnType() {
		return Integer.class;
	}

	@Override
	public DerivedCellCalculator copy() {
		return new SegmentByLocalExtremaCalculator(getPosition(), copyInners());
	}

}
