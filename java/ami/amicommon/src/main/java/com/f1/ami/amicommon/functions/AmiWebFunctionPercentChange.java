package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.MH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator2;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionPercentChange extends AbstractMethodDerivedCellCalculator2 {

	private static final ParamsDefinition VERIFIER = new ParamsDefinition("percentChange", Double.class, "Number old,Number nuw");
	static {
		VERIFIER.addDesc(
				"This method will use this formula, (nuw-old) / old, to obtain the result. Returns either the formula result or null if either argument is null. Returns NaN if either is negative or if both are zero.");
		VERIFIER.addParamDesc(0, "first value, should be positive");
		VERIFIER.addParamDesc(1, "second value, should be positive");
		VERIFIER.addRetDesc("Returns null if either are null, NaN if either are negative, Nan if both are zero, otherwise follows formula");
		VERIFIER.addExample(10, 15);
		VERIFIER.addExample(5, 20);
		VERIFIER.addExample(-4, 30);
	}

	public AmiWebFunctionPercentChange(int position, DerivedCellCalculator p0, DerivedCellCalculator p1) {
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
	private Object calc(Number old, Number nuw) {
		return MH.pctChange(nuw.doubleValue(), old.doubleValue());
	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p0, DerivedCellCalculator p1) {
		return new AmiWebFunctionPercentChange(getPosition(), p0, p1);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionPercentChange(position, calcs[0], calcs[1]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
