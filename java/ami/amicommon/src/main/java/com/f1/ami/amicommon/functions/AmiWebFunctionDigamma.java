package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.MH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator1;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionDigamma extends AbstractMethodDerivedCellCalculator1 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("digamma", Double.class, "Number x");
	static {
		VERIFIER.addDesc("Returns the Double value of the digamma function for real positive values of x. Returns NaN when x <= 0");
		VERIFIER.addParamDesc(0, "Input value");
		VERIFIER.addExample(-1);
		VERIFIER.addExample(0);
		VERIFIER.addExample(1);
		VERIFIER.addExample(2.5);
	}

	public AmiWebFunctionDigamma(int position, DerivedCellCalculator params) {
		super(position, params);
	}

	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object eval(Object o) {
		Number value = (Number) o;
		if (value == null)
			return null;
		return MH.digamma(value.doubleValue());
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator params2) {
		return new AmiWebFunctionDigamma(getPosition(), params2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionDigamma(position, calcs[0]);
		}

	}
}
