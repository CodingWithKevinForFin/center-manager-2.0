package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.OH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator1;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionRound extends AbstractMethodDerivedCellCalculator1 {

	private static final ParamsDefinition VERIFIER = new ParamsDefinition("round", Long.class, "Number value");
	static {
		VERIFIER.addDesc("Returns the closest Long value given the supplied value. Equivalent to roundDown(value+.5).");
		VERIFIER.addParamDesc(0, "Value to round");
		VERIFIER.addExample(32.5);
		VERIFIER.addExample(-32.5);
		VERIFIER.addExample(32.2);
		VERIFIER.addExample(-32.2);
		VERIFIER.addExample(32.8);
		VERIFIER.addExample(-32.8);
		VERIFIER.addExample(14);
		VERIFIER.addExample(-14);
	}

	public AmiWebFunctionRound(int position, DerivedCellCalculator param) {
		super(position, param);
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object eval(Object p1) {
		if (p1 == null)
			return null;
		return round(p1);
	}

	static private Long round(Object t) {
		return OH.valueOf((long) Math.round(((Number) t).doubleValue()));
	}
	@Override
	public AmiWebFunctionRound copy(DerivedCellCalculator param) {
		return new AmiWebFunctionRound(getPosition(), param);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionRound(position, calcs[0]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}
	}

}
