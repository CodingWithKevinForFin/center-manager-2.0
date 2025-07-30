package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculatorN;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionStrIndexOf2 extends AbstractMethodDerivedCellCalculatorN {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("strIndexOf", Integer.class, "String text,String toFind,Integer start,Boolean ignoreCase");
	static {
		VERIFIER.addDesc(
				"Returns the 0 based index of the toFind within the text starting at the specified index, with the option to ignore case. If toFind does not exist from the specified start index, returns -1.");
		VERIFIER.addParamDesc(0, "The base string to to find substring from");
		VERIFIER.addParamDesc(1, "the substring to find");
		VERIFIER.addParamDesc(2, "the char to start looking at, null indicates from start");
		VERIFIER.addParamDesc(3, "true indicates it should case insensitive match");
		VERIFIER.addExample("what,now", "hat", 0, true);
		VERIFIER.addExample("what,now", "hat", 1, true);
		VERIFIER.addExample("what,now", "hat", 2, true);
		VERIFIER.addExample("cat,cat,cat", "CAT", 5, false);
		VERIFIER.addExample("cat,cat,cat", "CAT", 5, true);
		VERIFIER.addExample(null, "CAT", 5, true);
		VERIFIER.addExample("cat", null, 5, true);
		VERIFIER.addExample("An example,of, multiple, delims", ", ", 10, false);

	}

	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	public AmiWebFunctionStrIndexOf2(int position, DerivedCellCalculator[] params) {
		super(position, params);
		evalConsts();
	}

	@Override
	public Object eval(Object o[]) {
		String text = (String) o[0];
		String toFind = (String) o[1];
		if (toFind == null)
			return -1;
		Integer start = (Integer) o[2];
		if (start == null)
			start = 0;
		boolean ignoreCase = (Boolean.TRUE.equals((Boolean) o[3]));
		if (ignoreCase)
			return SH.indexOfIgnoreCase(text, toFind, start);
		else
			return SH.indexOf(text, toFind, start);
	}

	@Override
	protected Object shortCircuit(int i, Object val) {
		if (val == null && (i == 0 || i == 1))//if text is null or tofind is null... just return null
			return null;
		return KEEP_GOING;
	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator[] params2) {
		return new AmiWebFunctionStrIndexOf2(getPosition(), params2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionStrIndexOf2(position, calcs);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
