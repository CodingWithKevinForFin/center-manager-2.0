package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.OH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator2;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionStrCharAt extends AbstractMethodDerivedCellCalculator2 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("strCharAt", Character.class, "String text,Number pos");
	static {
		VERIFIER.addDesc("Returns a single character from a string at a given postion.");
		VERIFIER.addParamDesc(0, "The base string to get a character from");
		VERIFIER.addParamDesc(1, "offset from first char");
		VERIFIER.addExample("this", 0);
		VERIFIER.addExample("this", 1);
		VERIFIER.addExample("this", -10);
		VERIFIER.addExample("this", 4);

	}

	public AmiWebFunctionStrCharAt(int position, DerivedCellCalculator p0, DerivedCellCalculator p1) {
		super(position, p0, p1);
		evalConsts();
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	private static Character getChar(String string, Number number) {
		int i = number.intValue();
		if (i < 0 || i >= string.length())
			return null;
		return OH.valueOf(string.charAt(i));
	}

	@Override
	public Object eval(Object o0, Object o1) {
		return getChar((String) o0, (Number) o1);
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p0, DerivedCellCalculator p1) {
		return new AmiWebFunctionStrCharAt(getPosition(), p0, p1);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionStrCharAt(position, calcs[0], calcs[1]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
