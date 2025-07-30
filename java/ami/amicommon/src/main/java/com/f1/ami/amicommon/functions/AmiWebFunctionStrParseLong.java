package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator2;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionStrParseLong extends AbstractMethodDerivedCellCalculator2 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("strParseLong", Long.class, "String text,Number base");
	static {
		VERIFIER.addDesc(
				"Parses a string to a Long for a given radix (aka base) and returns that Long. Note, Radix up to 36 means the text is parsed as case insensitive, higher than 36, then it's case sensitive.");
		VERIFIER.addParamDesc(0, "The text to parse");
		VERIFIER.addParamDesc(1, "the base of the text, ex: base 10 is typical, 16 for hex");
		VERIFIER.addExample("1234", 10);
		VERIFIER.addExample("12fa", 16);
		VERIFIER.addExample("12Zz", 64);

	}

	public AmiWebFunctionStrParseLong(int position, DerivedCellCalculator p0, DerivedCellCalculator p1) {
		super(position, p0, p1);
		evalConsts();
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object eval(Object o0, Object o1) {
		int base = ((Number) o1).intValue();
		if (base < 2 || base > 63)
			return null;
		String object = (String) o0;
		try {
			return SH.parseLong(object, base);
		} catch (Exception e) {
			return null;
		}

	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p0, DerivedCellCalculator p1) {
		return new AmiWebFunctionStrParseLong(getPosition(), p0, p1);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionStrParseLong(position, calcs[0], calcs[1]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
