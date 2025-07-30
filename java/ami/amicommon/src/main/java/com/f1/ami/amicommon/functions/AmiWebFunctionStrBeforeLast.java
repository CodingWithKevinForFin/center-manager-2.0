package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator3;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionStrBeforeLast extends AbstractMethodDerivedCellCalculator3 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("strBeforeLast", String.class, "String text,String toFind,Boolean origIfNotFound");
	static {
		VERIFIER.addDesc(
				"Returns the portion of a string before the last occurence of a delimiter. If the delimiter is not found, then return either the original string or null depending on the origIfNotFound param.");
		VERIFIER.addParamDesc(0, "The base string to get a substring from");
		VERIFIER.addParamDesc(1, "the delimiter to find");
		VERIFIER.addParamDesc(2, "If the toFind parameter doesn't exist in the text: then return text if true or null if false");
		VERIFIER.addExample("What now", " ", true);
		VERIFIER.addExample("what,now", " ", true);
		VERIFIER.addExample("what,now", " ", false);
		VERIFIER.addExample("what,now", null, false);
		VERIFIER.addExample("what,now", null, true);
		VERIFIER.addExample(null, " ", false);
		VERIFIER.addExample("An example,of, multiple, delims", ", ", true);

	}

	public AmiWebFunctionStrBeforeLast(int position, DerivedCellCalculator p0, DerivedCellCalculator p1, DerivedCellCalculator p2) {
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
			return null;
		String toFind = (String) o1;
		Boolean origIfNotFound = (Boolean) o2;
		if (toFind == null)
			return Boolean.TRUE.equals(origIfNotFound) ? text : null;
		int i = text.lastIndexOf(toFind);
		if (i == -1)
			return Boolean.TRUE.equals(origIfNotFound) ? text : null;
		return text.substring(0, i);
	}

	@Override
	protected boolean shortCircuitNull() {
		return false;
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p0, DerivedCellCalculator p1, DerivedCellCalculator p2) {
		return new AmiWebFunctionStrBeforeLast(getPosition(), p0, p1, p2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionStrBeforeLast(position, calcs[0], calcs[1], calcs[2]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
