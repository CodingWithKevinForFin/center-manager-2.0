package com.f1.ami.amicommon.functions;

import java.util.List;

import com.f1.base.Mapping;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator1;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionStrSplitLines extends AbstractMethodDerivedCellCalculator1 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("strSplitLines", List.class, "String text");
	static {
		VERIFIER.addDesc("Splits a string with multiple lines into a list and returns that list. Handles newlines and carriage returns.");
		VERIFIER.addParamDesc(0, "The string to split");
		VERIFIER.addExample("test\nme\nout");
		VERIFIER.addExample("test");
		VERIFIER.addExample(null);
		VERIFIER.addExample("\n\n");
	}

	public AmiWebFunctionStrSplitLines(int position, DerivedCellCalculator params) {
		super(position, params);
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}
	@Override
	public boolean isConst() {
		return false;
	}

	@Override
	public Object eval(Object o) {
		String value = (String) o;
		return CH.l(SH.splitLines(value));
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator params2) {
		return new AmiWebFunctionStrSplitLines(getPosition(), params2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionStrSplitLines(position, calcs[0]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
