package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator2;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionStrLastIndexOf extends AbstractMethodDerivedCellCalculator2 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("strLastIndexOf", Integer.class, "String text,String toFind");
	static {
		VERIFIER.addDesc("Returns the last 0 based index of toFind within the text. If toFind does not exist in text, returns -1.");
		VERIFIER.addParamDesc(0, "The base string to get a substring from");
		VERIFIER.addParamDesc(1, "the substring to find");
		VERIFIER.addExample("what,now", "hat");
		VERIFIER.addExample("what,now", "what");
		VERIFIER.addExample("What now", " ");
		VERIFIER.addExample("what,now", " ");
		VERIFIER.addExample("what,now", "");
		VERIFIER.addExample("what,now", null);
		VERIFIER.addExample(null, "test");
		VERIFIER.addExample("An example,of, multiple, delims", ", ");

	}

	public AmiWebFunctionStrLastIndexOf(int position, DerivedCellCalculator p0, DerivedCellCalculator p1) {
		super(position, p0, p1);
		evalConsts();
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object eval(Object o0, Object o1) {
		String text = (String) o0;
		if (text == null)
			return null;
		String toFind = (String) o1;
		if (toFind == null)
			return -1;
		return text.lastIndexOf(toFind);
	}

	@Override
	protected boolean shortCircuitNull() {
		return false;
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p0, DerivedCellCalculator p1) {
		return new AmiWebFunctionStrLastIndexOf(getPosition(), p0, p1);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionStrLastIndexOf(position, calcs[0], calcs[1]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
