package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator3;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionStrSubstring extends AbstractMethodDerivedCellCalculator3 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("strSubstring", String.class, "String text,Number start,Number end");
	static {
		VERIFIER.addDesc(
				"Returns a substring from a string, based on the specified start and end. If start or charsToReplace extend beyond string length, they will be set to string length.");
		VERIFIER.addParamDesc(0, "The base string to get a substring from");
		VERIFIER.addParamDesc(1, "zero indexed start of substring to return");
		VERIFIER.addParamDesc(2, "zoro indexed end of substring to return");
		VERIFIER.addExample("this", 1, 2);
		VERIFIER.addExample("this", 0, 4);
		VERIFIER.addExample("this", 2, 100);
		VERIFIER.addExample("this", -10, 1);

	}

	public AmiWebFunctionStrSubstring(int position, DerivedCellCalculator p0, DerivedCellCalculator p1, DerivedCellCalculator p2) {
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
		Number start = (Number) o1;
		Number end = (Number) o2;
		return substring(text, start.intValue(), end.intValue());
	}

	private String substring(String source, int start, int end) {
		return SH.substring(source, start, end);
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p0, DerivedCellCalculator p1, DerivedCellCalculator p2) {
		return new AmiWebFunctionStrSubstring(getPosition(), p0, p1, p2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionStrSubstring(position, calcs[0], calcs[1], calcs[2]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
