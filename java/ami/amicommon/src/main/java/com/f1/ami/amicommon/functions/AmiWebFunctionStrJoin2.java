package com.f1.ami.amicommon.functions;

import java.util.Map;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.base.Mapping;
import com.f1.utils.CH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator3;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionStrJoin2 extends AbstractMethodDerivedCellCalculator3 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("strJoin", String.class, "String delimiter,String associator,java.util.Map data");
	static {
		VERIFIER.addDesc(
				"Concatenates all of the data in the map into a single delimited string, such that each entry is in the format: key followed by associator followed by value. Returns that string.");
		VERIFIER.addParamDesc(0, "The Delimiter");
		VERIFIER.addParamDesc(1, "the List to concatenate");
		VERIFIER.addExample(",", "=", CH.m("where", "here", "when", "now"));
		VERIFIER.addExample(";", " is ", CH.m("firstName", "Eric", "lastName", "Johns"));
		VERIFIER.addExample(null, null, CH.m("what", "now"));

	}

	public AmiWebFunctionStrJoin2(int position, DerivedCellCalculator p0, DerivedCellCalculator p1, DerivedCellCalculator p2) {
		super(position, p0, p1, p2);
		evalConsts();
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object eval(Object o0, Object o1, Object o2) {
		String delim = (String) o0;
		String associator = (String) o1;
		Map map = (Map) o2;
		return joinMap(delim, associator, map);
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p0, DerivedCellCalculator p1, DerivedCellCalculator p2) {
		return new AmiWebFunctionStrJoin2(getPosition(), p0, p1, p2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionStrJoin2(position, calcs[0], calcs[1], calcs[2]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

	public static String joinMap(String delim, String associator, Map<?, ?> map) {
		if (map.isEmpty())
			return "";
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Map.Entry<?, ?> e : map.entrySet()) {
			if (first) {
				first = false;
			} else
				sb.append(delim);
			AmiUtils.s(e.getValue(), AmiUtils.s(e.getKey(), sb).append(associator));
		}
		return sb.toString();
	}
}
