package com.f1.ami.amicommon.functions;

import java.util.Map;

import com.f1.base.Mapping;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator3;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionStrSplitToMap extends AbstractMethodDerivedCellCalculator3 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("strSplitToMap", Map.class, "String text,String delim,String associatorDelim");
	static {
		VERIFIER.addDesc(
				"Splits line into a Map using the supplied delimiters and returns that map. The expected format of the input string is key=value,key=value,... where the equal and comma delimiters are configurable.");
		VERIFIER.addParamDesc(0, "The string to split");
		VERIFIER.addParamDesc(1, "delimiter, literal not a pattern");
		VERIFIER.addParamDesc(2, "delimiter between keys and values, literal not a pattern");
		VERIFIER.addExample("apple=green cherry=red potato=brown", " ", "=");
		VERIFIER.addExample("apple==>green|cherry==>red|potato==>brown|apple==>red", "|", "==>");
	}

	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	public AmiWebFunctionStrSplitToMap(int position, DerivedCellCalculator p0, DerivedCellCalculator p1, DerivedCellCalculator p2) {
		super(position, p0, p1, p2);
		evalConsts();
	}
	@Override
	public boolean isConst() {
		return false;//always force a new map because it's mutable
	}

	@Override
	public Object eval(Object o0, Object o1, Object o2) {
		String value = (String) o0;
		String delim = (String) o1;
		String associatorDelim = (String) o2;
		return SH.splitToMap(delim, associatorDelim, value);
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p0, DerivedCellCalculator p1, DerivedCellCalculator p2) {
		return new AmiWebFunctionStrSplitToMap(getPosition(), p0, p1, p2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionStrSplitToMap(position, calcs[0], calcs[1], calcs[2]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
