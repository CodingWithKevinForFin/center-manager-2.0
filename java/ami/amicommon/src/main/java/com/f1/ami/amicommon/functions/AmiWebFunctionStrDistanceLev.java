package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator3;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionStrDistanceLev extends AbstractMethodDerivedCellCalculator3 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("strDistanceLev", Integer.class, "String left,String right,Boolean ignoreCase");
	static {
		VERIFIER.addDesc("Return the levenshtein distance between two strings. This is naturally a case insensitve comparison.");
		VERIFIER.addParamDesc(0, "The First string to text");
		VERIFIER.addParamDesc(1, "The Second string to text");
		VERIFIER.addParamDesc(2, "Ignore case when calculating distance");
		VERIFIER.addExample("FEATHER", "FEATHER", true);
		VERIFIER.addExample("FEATHER", "Farther", true);
		VERIFIER.addExample("FEATHER", "Farther", false);
		VERIFIER.addExample("Color", "flower", true);
		VERIFIER.addExample("Some one", "Some thing", true);
		VERIFIER.addExample(null, null, true);
		VERIFIER.addExample(null, "I", true);
	}

	public AmiWebFunctionStrDistanceLev(int position, DerivedCellCalculator p0, DerivedCellCalculator p1, DerivedCellCalculator p2) {
		super(position, p0, p1, p2);
		evalConsts();
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object eval(Object o0, Object o1, Object o2) {
		String value1 = (String) o0;
		String value2 = (String) o1;
		boolean ignoreCase = Boolean.TRUE.equals((Boolean) o2);
		if (value1 == null)
			value1 = "";
		if (value2 == null)
			value2 = "";
		if (ignoreCase ? SH.equalsIgnoreCase(value1, value2) : SH.equals(value1, value2))
			return 0;
		return SH.getDistance(value1, value2, ignoreCase);
	}

	@Override
	protected boolean shortCircuitNull() {
		return false;
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p0, DerivedCellCalculator p1, DerivedCellCalculator p2) {
		return new AmiWebFunctionStrDistanceLev(getPosition(), p0, p1, p2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionStrDistanceLev(position, calcs[0], calcs[1], calcs[2]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
