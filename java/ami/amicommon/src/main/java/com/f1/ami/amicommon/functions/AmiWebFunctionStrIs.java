package com.f1.ami.amicommon.functions;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator1;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionStrIs extends AbstractMethodDerivedCellCalculator1 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("strIs", Boolean.class, "String text");
	static {
		VERIFIER.addDesc(
				"Returns true if the string contains characters other than whitespace characters, such as tabs, newlines, and spaces. Returns false if mismatch, text is null or empty. White space includes tabs, newlines, and spaces.");
		VERIFIER.addParamDesc(0, "The string to test");
		VERIFIER.addExample("    ");
		VERIFIER.addExample("");
		VERIFIER.addExample("I");
		VERIFIER.addExample("   abc ");
		VERIFIER.addExample(null);
	}

	public AmiWebFunctionStrIs(int position, DerivedCellCalculator params) {
		super(position, params);
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object eval(Object value) {
		return value != null && SH.is(AmiUtils.s(value));
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator params2) {
		return new AmiWebFunctionStrIs(getPosition(), params2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionStrIs(position, calcs[0]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
