package com.f1.ami.amicommon.functions;

import java.util.List;

import com.f1.base.Mapping;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator1;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionSplitLines extends AbstractMethodDerivedCellCalculator1 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("splitLines", List.class, "String text");
	static {
		VERIFIER.addDesc("Returns a list of splitted lines using line feed and line return chars.");
		VERIFIER.addParamDesc(0, "The string to split");
		VERIFIER.addExample("these are\ntwo lines");
		VERIFIER.addExample("these are\r\ntwo lines");
		VERIFIER.addExample("this is one line");
		VERIFIER.addExample("this is one line\n");
	}

	public AmiWebFunctionSplitLines(int position, DerivedCellCalculator params) {
		super(position, params);
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object eval(Object o) {
		String value = (String) o;
		if (value == null)
			return null;
		List<String> r = CH.l(SH.splitLines(value));
		return r;
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator params2) {
		return new AmiWebFunctionSplitLines(getPosition(), params2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionSplitLines(position, calcs[0]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
