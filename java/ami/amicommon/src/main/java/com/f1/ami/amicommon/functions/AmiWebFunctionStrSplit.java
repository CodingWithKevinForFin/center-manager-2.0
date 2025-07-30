package com.f1.ami.amicommon.functions;

import java.util.List;

import com.f1.base.Mapping;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator2;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionStrSplit extends AbstractMethodDerivedCellCalculator2 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("strSplit", List.class, "String text,String delim");
	static {
		VERIFIER.addDesc("Splits line into a list using the supplied delimiter, returns that list.");
		VERIFIER.addParamDesc(0, "The string to split");
		VERIFIER.addParamDesc(1, "delimiter, literal not a pattern");
		VERIFIER.addExample("these are some words", " ");
		VERIFIER.addExample("these are words that are split by a word", " are ");
		VERIFIER.addExample("Simple|case|right|here", "|");
		VERIFIER.addExample("abcd", "");
	}

	public AmiWebFunctionStrSplit(int position, DerivedCellCalculator p0, DerivedCellCalculator p1) {
		super(position, p0, p1);
		evalConsts();
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}
	@Override
	public boolean isConst() {
		return false;//always force a new list because it's mutable
	}

	@Override
	public Object eval(Object o0, Object o1) {
		String value = (String) o0;
		String delim = (String) o1;
		List<String> r = split(delim, value);
		return r;
	}

	@Override
	protected boolean shortCircuitNull() {
		return true;
	}

	private List<String> split(String delim, String value) {
		return CH.l(delim.length() == 1 ? SH.split(delim.charAt(0), value) : SH.split(delim, value));
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p0, DerivedCellCalculator p1) {
		return new AmiWebFunctionStrSplit(getPosition(), p0, p1);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionStrSplit(position, calcs[0], calcs[1]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
