package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator1;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionQuote extends AbstractMethodDerivedCellCalculator1 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("quote", String.class, "Object text");
	static {
		VERIFIER.addDesc(
				"Returns a string that surrounds the supplied string in quotes(\") and escape any existing quotes with a backslash(\\). Returns null if supplied string is null.");
		VERIFIER.addParamDesc(0, "String to escape");
		VERIFIER.addExample("simple");
		VERIFIER.addExample("this \"is\" less simple");
	}

	public AmiWebFunctionQuote(int position, DerivedCellCalculator params) {
		super(position, params);
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public String eval(Object value) {
		if (value == null)
			return null;
		StringBuilder sb = new StringBuilder();
		sb.append('"');
		SH.s(value, sb);
		SH.escapeInplace(sb, 1, sb.length(), '"', '\\');
		sb.append('"');
		return sb.toString();
	}
	@Override
	public boolean isConst() {
		return super.isConst();
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator params2) {
		return new AmiWebFunctionQuote(getPosition(), params2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionQuote(position, calcs[0]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
