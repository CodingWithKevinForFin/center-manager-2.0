package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.JsonUtils;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator2;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionJsonExtract extends AbstractMethodDerivedCellCalculator2 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("jsonExtract", String.class, "String json,String xpath");
	static {
		VERIFIER.addDesc(
				"Walks through JSON to extract a particular value. If the value is a const then the string representation is returned, if the value is a data structure (list or map) then the value is returned as legal JSON (i.e. quotes are properly escaped). Returns null if (a) the supplied json is not legal JSON, (b) either supplied string is null, or (c) the xpath does not resolve to an existing value in the supplied JSON.");
		VERIFIER.addParamDesc(0, "Must be legal JSON, ex: 123 or {a:1,b:[1,2,\"what\"]}");
		VERIFIER.addParamDesc(1,
				"The path to supplied value, with each 'step' separated by period (.), To index into arrays use the zero based integer offset. To index into maps use the key value");
		VERIFIER.addExample("\"simple\"", "");
		VERIFIER.addExample("{a:1,b:2}", "a");
		VERIFIER.addExample("{a:1,b:['rob','dave','steve\\'s']}", "b");
		VERIFIER.addExample("{a:1,b:['rob','dave','steve\\'s']}", "b.2");
		VERIFIER.addExample("{a:1,b:['rob','dave','steve\\'s',{e:1,f:2}]]},", "b.2.f");
	}

	public AmiWebFunctionJsonExtract(int position, DerivedCellCalculator p0, DerivedCellCalculator p1) {
		super(position, p0, p1);
		evalConsts();
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	protected Object get1(Object o) {
		return SH.split('.', (String) o);
	}

	@Override
	public Object eval(Object o0, Object o1) {
		try {
			String json = (String) o0;
			return JsonUtils.extractFromJson(json, (String[]) o1, false);
		} catch (Exception e) {
			return null;
		}

	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p0, DerivedCellCalculator p1) {
		return new AmiWebFunctionJsonExtract(getPosition(), p0, p1);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionJsonExtract(position, calcs[0], calcs[1]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
