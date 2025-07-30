package com.f1.ami.amicommon.functions;

import java.util.Collection;
import java.util.Iterator;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.base.Mapping;
import com.f1.utils.CH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator2;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionStrJoin extends AbstractMethodDerivedCellCalculator2 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("strJoin", String.class, "String delimiter,java.util.Collection data");
	static {
		VERIFIER.addDesc("Concatenates all of the data in the list into a single delimited string, returns that string.");
		VERIFIER.addParamDesc(0, "The Delimiter");
		VERIFIER.addParamDesc(1, "the List to concatenate");
		VERIFIER.addExample(",", CH.l("what", "now"));
		VERIFIER.addExample(";", CH.l("what", null, 742.4d));
		VERIFIER.addExample(null, CH.l("what", null, "now"));
		VERIFIER.addExample(null, CH.s("what", "now", "what"));

	}

	public AmiWebFunctionStrJoin(int position, DerivedCellCalculator p0, DerivedCellCalculator p1) {
		super(position, p0, p1);
		evalConsts();
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object eval(Object o0, Object o1) {
		String delim = (String) o0;
		Collection collection = (Collection) o1;
		return join(delim, collection);
	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p0, DerivedCellCalculator p1) {
		return new AmiWebFunctionStrJoin(getPosition(), p0, p1);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionStrJoin(position, calcs[0], calcs[1]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

	public static String join(String delim, Collection<?> tokens) {
		if (tokens.isEmpty())
			return "";
		StringBuilder r = new StringBuilder();
		Iterator<?> i = tokens.iterator();
		if (i.hasNext())
			AmiUtils.s(i.next(), r);
		while (i.hasNext())
			AmiUtils.s(i.next(), r.append(delim));
		return r.toString();
	}

}
