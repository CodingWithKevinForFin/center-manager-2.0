package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator1;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionFormatNumberCompact extends AbstractMethodDerivedCellCalculator1 {

	public AmiWebFunctionFormatNumberCompact(int position, DerivedCellCalculator param) {
		super(position, param);
	}

	private static final ParamsDefinition VERIFIER = new ParamsDefinition("formatNumberCompact", String.class, "Number value");

	static {
		VERIFIER.addDesc("Converts a numeric value to short string using suffixes (max of 4 chars, or 5 for negative numbers)");
		VERIFIER.addParamDesc(0, "value to convert to string");
		VERIFIER.addExample(1);
		VERIFIER.addExample(12);
		VERIFIER.addExample(123);
		VERIFIER.addExample(1234);
		VERIFIER.addExample(12345);
		VERIFIER.addExample(123456);
		VERIFIER.addExample(1234567);
		VERIFIER.addExample(12345678);
		VERIFIER.addExample(.1);
		VERIFIER.addExample(.2);
		VERIFIER.addExample(.3);
		VERIFIER.addExample(.4);
		VERIFIER.addExample(.5);
		VERIFIER.addExample(-.5);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionFormatNumberCompact(position, calcs[0]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

	@Override
	public Object eval(Object p1) {
		return p1 == null ? null : SH.formatNumberCompact((Number) p1);
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator param) {
		return new AmiWebFunctionFormatNumberCompact(getPosition(), param);
	}

	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

}
