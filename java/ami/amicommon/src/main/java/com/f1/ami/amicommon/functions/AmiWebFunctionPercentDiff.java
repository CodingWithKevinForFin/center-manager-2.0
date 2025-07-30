package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.MH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator2;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionPercentDiff extends AbstractMethodDerivedCellCalculator2 {

	private static final ParamsDefinition VERIFIER = new ParamsDefinition("percentDiff", Double.class, "Number a,Number b");
	static {
		VERIFIER.addDesc(
				"This method will use this formula, diff(a,b) / avg(a,b), to obtain the result. Returns null if either one is null, returns NaN if either is negative or if both are zero, otherwise follows formula.");
		VERIFIER.addParamDesc(0, "first value, should be positive");
		VERIFIER.addParamDesc(1, "second value, should be positive");
		VERIFIER.addExample(10, 15);
		VERIFIER.addExample(5, 20);
		VERIFIER.addExample(-4, 30);
	}

	public AmiWebFunctionPercentDiff(int position, DerivedCellCalculator p0, DerivedCellCalculator p1) {
		super(position, p0, p1);
		evalConsts();
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object eval(Object o0, Object o1) {
		return calc((Number) o0, (Number) o1);
	}
	private Object calc(Number a, Number b) {
		return MH.pctDiff(a.doubleValue(), b.doubleValue());
	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p0, DerivedCellCalculator p1) {
		return new AmiWebFunctionPercentDiff(getPosition(), p0, p1);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionPercentDiff(position, calcs[0], calcs[1]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
