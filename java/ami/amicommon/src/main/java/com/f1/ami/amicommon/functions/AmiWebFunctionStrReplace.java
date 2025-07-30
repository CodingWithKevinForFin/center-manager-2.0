package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator3;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionStrReplace extends AbstractMethodDerivedCellCalculator3 {

	private static final ParamsDefinition VERIFIER = new ParamsDefinition("strReplace", String.class, "String text,String find,String replace");
	static {
		VERIFIER.addDesc("Given a string, this method finds and replaces all occurences of substring with another substring, and returns the resulting string.");
		VERIFIER.addParamDesc(0, "String to look look for matching substring(find param) in");
		VERIFIER.addParamDesc(1, "String to look for in text, literal not a pattern");
		VERIFIER.addParamDesc(2, "When find string is found, replace with this string");
		VERIFIER.addExample("cat in the hat", "at", "an");
		VERIFIER.addExample("tististis", "tis", "a");
		VERIFIER.addExample("Some example", "", ",");
		VERIFIER.addExample("Several\nlines\nare\none\nline", "\n", " ");
	}

	public AmiWebFunctionStrReplace(int position, DerivedCellCalculator p0, DerivedCellCalculator p1, DerivedCellCalculator p2) {
		super(position, p0, p1, p2);
		evalConsts();
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object eval(Object o0, Object o1, Object o2) {
		return SH.replaceAll((String) o0, (String) o1, (String) o2);
	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p0, DerivedCellCalculator p1, DerivedCellCalculator p2) {
		return new AmiWebFunctionStrReplace(getPosition(), p0, p1, p2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionStrReplace(position, calcs[0], calcs[1], calcs[2]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}
}
