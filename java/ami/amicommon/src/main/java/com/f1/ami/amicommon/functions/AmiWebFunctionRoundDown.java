package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.OH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator1;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionRoundDown extends AbstractMethodDerivedCellCalculator1 {

	private static final ParamsDefinition VERIFIER = new ParamsDefinition("roundDown", Long.class, "Number value");
	static {
		VERIFIER.addDesc("Returns the closest Long that rounds the number down.");
		VERIFIER.addParamDesc(0, "Value to round down");
		VERIFIER.addExample(32.5);
		VERIFIER.addExample(-32.5);
		VERIFIER.addExample(32.2);
		VERIFIER.addExample(-32.2);
		VERIFIER.addExample(32.8);
		VERIFIER.addExample(-32.8);
		VERIFIER.addExample(14);
		VERIFIER.addExample(-14);
	}

	public AmiWebFunctionRoundDown(int position, DerivedCellCalculator params) {
		super(position, params);
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object eval(Object t) {
		if (t == null)
			return null;
		return round(t);
	}

	static private Long round(Object t) {
		return OH.valueOf((long) Math.floor(((Number) t).doubleValue()));
	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator params) {
		return new AmiWebFunctionRoundDown(getPosition(), params);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionRoundDown(position, calcs[0]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}
	}

}
