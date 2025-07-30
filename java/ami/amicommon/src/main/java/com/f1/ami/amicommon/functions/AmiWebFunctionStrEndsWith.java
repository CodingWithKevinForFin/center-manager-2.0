package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator3;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionStrEndsWith extends AbstractMethodDerivedCellCalculator3 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("strEndsWith", Boolean.class, "String text,String toFind,Boolean ignoreCase");
	static {
		VERIFIER.addDesc("Returns true if text ends with toFind, false otherwise.");
		VERIFIER.addParamDesc(0, "The base string to text");
		VERIFIER.addParamDesc(1, "the substring to find at start of text");
		VERIFIER.addParamDesc(2, "Should the text be case sensitive");
		VERIFIER.addExample("what,now", "NOW", true);
		VERIFIER.addExample("what,now", "NOW", false);
		VERIFIER.addExample("what,now", "where", false);
		VERIFIER.addExample("what,now", null, false);

	}

	public AmiWebFunctionStrEndsWith(int position, DerivedCellCalculator p0, DerivedCellCalculator p1, DerivedCellCalculator p2) {
		super(position, p0, p1, p2);
		evalConsts();
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object eval(Object o0, Object o1, Object o2) {
		String text = (String) o0;
		if (text == null)
			return Boolean.FALSE;
		String toFind = (String) o1;
		if (toFind == null)
			return Boolean.FALSE;
		Boolean ignoreCase = (Boolean) o2;
		if (Boolean.TRUE.equals(ignoreCase))
			return SH.endsWithIgnoreCase(text, toFind);
		return text.endsWith(toFind);
	}

	@Override
	protected boolean shortCircuitNull() {
		return false;
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p0, DerivedCellCalculator p1, DerivedCellCalculator p2) {
		return new AmiWebFunctionStrEndsWith(getPosition(), p0, p1, p2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionStrEndsWith(position, calcs[0], calcs[1], calcs[2]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
