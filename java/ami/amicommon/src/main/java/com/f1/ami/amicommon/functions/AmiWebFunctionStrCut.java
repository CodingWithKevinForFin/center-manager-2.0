package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator3;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionStrCut extends AbstractMethodDerivedCellCalculator3 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("strCut", String.class, "String text,String delim,String fieldList");
	static {
		VERIFIER.addDesc("Splits the line into a list using the supplied delimiter and returns those fields listed by position. Returns the joined strings using the supplied delim.");
		VERIFIER.addParamDesc(0, "The string to split");
		VERIFIER.addParamDesc(1, "delimiter, literal not a pattern");
		VERIFIER.addParamDesc(2, "fields to return, Can use n-m for range; n,m,... for individual fields or -n for first number or n- for last n entries");
		VERIFIER.addExample("these are some words", " ", "2-3");
		VERIFIER.addExample("these are some words", " ", "1,3,5");
		VERIFIER.addExample("these are some words", " ", "-2");
		VERIFIER.addExample("these are some words", " ", "3-");
		VERIFIER.addExample("these are some words", " ", "3-1,3");
	}

	public AmiWebFunctionStrCut(int position, DerivedCellCalculator p0, DerivedCellCalculator p1, DerivedCellCalculator p2) {
		super(position, p0, p1, p2);
		evalConsts();
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	protected int[] get2(Object o2) {
		return parseFields((String) o2);
	}
	private int[] parseFields(String string) {
		try {
			if (string == null)
				return null;
			else if (string.indexOf(',') != -1) {
				String[] parts = SH.split(',', string);
				int r[] = new int[parts.length * 2];
				int pos = 0;
				for (String part : parts) {
					String start = SH.trim(SH.beforeFirst(part, '-'));
					String end = SH.trim(SH.afterFirst(part, '-'));
					int s = "".equals(start) ? 0 : SH.parseIntSafe(start, false, false);
					int e = "".equals(end) ? Integer.MAX_VALUE : SH.parseIntSafe(end, false, false);
					r[pos++] = s;
					r[pos++] = e;
				}
				return r;
			} else if (string.indexOf('-') != -1) {
				String start = SH.trim(SH.beforeFirst(string, '-'));
				String end = SH.trim(SH.afterFirst(string, '-'));
				int s = "".equals(start) ? 0 : SH.parseIntSafe(start, false, false);
				int e = "".equals(end) ? Integer.MAX_VALUE : SH.parseIntSafe(end, false, false);
				return new int[] { s, e };
			} else {
				int t = SH.parseIntSafe(SH.trim(string), false, false);
				return new int[] { t, t };
			}
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Object eval(Object o0, Object o1, Object o2) {
		String text = (String) o0;
		String delim = (String) o1;
		int[] fields = (int[]) o2;
		return fields == null ? null : SH.cut(text, delim, fields);
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p0, DerivedCellCalculator p1, DerivedCellCalculator p2) {
		return new AmiWebFunctionStrCut(getPosition(), p0, p1, p2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionStrCut(position, calcs[0], calcs[1], calcs[2]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
