package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator2;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionStrRepeat extends AbstractMethodDerivedCellCalculator2 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("strRepeat", String.class, "String text,Integer count");
	static {
		VERIFIER.addDesc("Returns a string that repeats the supplied text a specified number of times.");
		VERIFIER.addParamDesc(0, "The string to repeat");
		VERIFIER.addParamDesc(1, "number of times to repeat");
		VERIFIER.addExample("abc", 3);
		VERIFIER.addExample("0", 4);
		VERIFIER.addExample("", 10);
		VERIFIER.addExample(null, 2);
		VERIFIER.addExample("2", null);
	}

	public AmiWebFunctionStrRepeat(int position, DerivedCellCalculator p0, DerivedCellCalculator p1) {
		super(position, p0, p1);
		evalConsts();
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object eval(Object o0, Object o1) {
		String value = (String) o0;
		Integer count = (Integer) o1;
		return SH.repeat(value, count);
	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p0, DerivedCellCalculator p1) {
		return new AmiWebFunctionStrRepeat(getPosition(), p0, p1);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionStrRepeat(position, calcs[0], calcs[1]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
