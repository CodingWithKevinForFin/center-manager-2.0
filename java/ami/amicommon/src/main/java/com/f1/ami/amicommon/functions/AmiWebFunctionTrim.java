package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator1;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionTrim extends AbstractMethodDerivedCellCalculator1 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("strTrim", String.class, "String text");
	static {
		VERIFIER.addDesc(
				"Returns a string with leading/trailing white space removed, including tabs, line returns. If there is no leading/trailing white space then the supplied string is returned unchanged.");
		VERIFIER.addParamDesc(0, "String to trim white space from");
		VERIFIER.addExample("No Change!");
		VERIFIER.addExample(" Change \t\n\r  ");
		VERIFIER.addExample("  ");
	}

	public AmiWebFunctionTrim(int position, DerivedCellCalculator params) {
		super(position, params);
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object eval(Object o) {
		String text = (String) o;
		return SH.trim(text);
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator params2) {
		return new AmiWebFunctionTrim(getPosition(), params2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionTrim(position, calcs[0]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}
}
