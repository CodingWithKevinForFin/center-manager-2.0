package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.TextMatcher;
import com.f1.utils.impl.StringCharReader;
import com.f1.utils.impl.TextMatcherFactory;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator2;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionStrMatchesFilter extends AbstractMethodDerivedCellCalculator2 {
	public static final TextMatcherFactory MATCHER_FACTORY = new TextMatcherFactory(true, false, false);
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("strMatchesFilter", Boolean.class, "String text,String pattern");
	static {
		VERIFIER.addDesc("Returns true if the text matches the pattern, false otherwise.");
		VERIFIER.addRetDesc("true if matches");
		VERIFIER.addParamDesc(0, "String to evaluate");
		VERIFIER.addParamDesc(1, "pattern");
		VERIFIER.addExample("this", "*is");
		VERIFIER.addExample("this", "THIS");
		VERIFIER.addExample("this", "");
		//		VERIFIER.addExample("this", -10, 1);

	}

	public AmiWebFunctionStrMatchesFilter(int position, DerivedCellCalculator p0, DerivedCellCalculator p1) {
		super(position, p0, p1);
		evalConsts();
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}
	@Override
	protected TextMatcher get1(Object s) {
		try {
			String val = (String) s;
			if (val.startsWith("^") && !val.endsWith("*") && !val.endsWith("$"))
				val = val + '*';
			if (val.endsWith("$") && !val.startsWith("*") && !val.startsWith("^"))
				val = "*" + val;
			return MATCHER_FACTORY.toMatcherNoThrow(new StringCharReader(val), new StringBuilder());
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Object eval(Object o0, Object o1) {
		String text = (String) o0;
		TextMatcher matcher = (TextMatcher) o1;
		return matcher.matches(text);
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p0, DerivedCellCalculator p1) {
		return new AmiWebFunctionStrMatchesFilter(getPosition(), p0, p1);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionStrMatchesFilter(position, calcs[0], calcs[1]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
