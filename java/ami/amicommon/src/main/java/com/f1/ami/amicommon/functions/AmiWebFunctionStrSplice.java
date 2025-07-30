package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculatorN;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionStrSplice extends AbstractMethodDerivedCellCalculatorN {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("strSplice", String.class, "String text,Number start,Number charsToReplace,String replacement");
	static {
		VERIFIER.addDesc(
				"Given the base string, a 0-based index, the length of the replacement string, and the replacement string, modifies part of the base string at the index with the specified replacement string. If start or charsToReplace extends beyond string length, they will be set to string limits.");
		VERIFIER.addRetDesc("String or null if text,start or charsToReplace is null");
		VERIFIER.addParamDesc(0, "The base string that will have a portion spliced with replacement text");
		VERIFIER.addParamDesc(1, "zero indexed start of replacement");
		VERIFIER.addParamDesc(2, "length of chars to replace");
		VERIFIER.addParamDesc(3, "text to replace specified subsequence with, null is treated as empty string");
		VERIFIER.addExample("this is test", 5, 2, "was");
		VERIFIER.addExample("this is test", 5, 2, null);
		VERIFIER.addExample("this is test", 8, 0, "inserted ");
		VERIFIER.addExample("this is test", 100, 0, " of limits");
	}

	public AmiWebFunctionStrSplice(int position, DerivedCellCalculator[] params) {
		super(position, params);
		evalConsts();
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object eval(Object o[]) {
		String text = (String) o[0];
		Number start = (Number) o[1];
		Number charsToReplace = (Number) o[2];
		String replacementText = (String) o[3];
		return splice(text, start.intValue(), charsToReplace.intValue(), replacementText);
	}
	@Override
	protected Object shortCircuit(int i, Object val) {
		if (val == null) {
			if (i == 3)//replacementText
				return KEEP_GOING;
			return null;
		}
		return KEEP_GOING;
	}

	private StringBuilder sink = new StringBuilder();

	private String splice(String source, int start, int charsToRemove, String insert) {
		if (source == null)
			return null;
		final int length = source.length();
		if (insert == null)
			insert = "";
		if (start < 0)
			start = 0;
		if (start > length)
			start = length;
		if (charsToRemove < 0)
			charsToRemove = 0;
		int end = charsToRemove + start;
		if (end > length)
			end = length;
		if (charsToRemove == 0 && insert.length() == 0)
			return source;
		return SH.toStringAndClear(SH.clear(sink).append(source, 0, start).append(insert).append(source, end, length));//copied from SH.splice(...)
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator[] params2) {
		return new AmiWebFunctionStrSplice(getPosition(), params2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionStrSplice(position, calcs);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
