package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator1;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionParseJson extends AbstractMethodDerivedCellCalculator1 {

	private static ObjectToJsonConverter INSTANCE = new ObjectToJsonConverter();
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("parseJson", Object.class, "String legalJson");
	static {
		VERIFIER.addDesc("Returns a nested structure of lists/maps or literals present in the script.");
		VERIFIER.addParamDesc(0, "json");
		VERIFIER.addExample("[1,2,3]");
		VERIFIER.addExample("{\"shape\":'cube',\"size\":[1,2,3],\"solid\":true}");
		VERIFIER.addExample("true");
		VERIFIER.addExample("17");
		VERIFIER.addExample("'red'");
	}

	public AmiWebFunctionParseJson(int position, DerivedCellCalculator params) {
		super(position, params);
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object eval(Object o) {
		try {
			return INSTANCE.stringToObject((String) o);
		} catch (Exception e) {
			return null;
		}

	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator params2) {
		return new AmiWebFunctionParseJson(getPosition(), params2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionParseJson(position, calcs[0]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
