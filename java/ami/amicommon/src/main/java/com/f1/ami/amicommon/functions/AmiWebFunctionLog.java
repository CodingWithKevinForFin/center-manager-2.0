package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator2;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionLog extends AbstractMethodDerivedCellCalculator2 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("log", Double.class, "Number Value,Number Base");
	static {
		VERIFIER.addDesc("Returns a Double the log of a double value in a given base.");
		VERIFIER.addParamDesc(0, "value to get log for", "base to use when calculating log");
		VERIFIER.addExample(100, 10);
		VERIFIER.addExample(1000, 10);
		VERIFIER.addExample(4, 2);
		VERIFIER.addExample(8, 2);
		VERIFIER.addExample(16, 2);
	}

	public AmiWebFunctionLog(int position, DerivedCellCalculator p0, DerivedCellCalculator p1) {
		super(position, p0, p1);
		evalConsts();
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object eval(Object p1, Object p2) {
		return log((Number) p1, (Number) p2);
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p1, DerivedCellCalculator p2) {
		return new AmiWebFunctionLog(getPosition(), p1, p2);
	}

	private static Double log(Number numberVal, Number baseVal) {
		return Math.log(numberVal.doubleValue()) / Math.log(baseVal.doubleValue());
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionLog(position, calcs[0], calcs[1]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}