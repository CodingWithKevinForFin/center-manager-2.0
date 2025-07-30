package com.f1.ami.amicommon.functions;

import java.util.logging.Logger;

import com.f1.base.Mapping;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.converter.json2.TableToJsonConverter;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator2;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionToJson extends AbstractMethodDerivedCellCalculator2 {
	private static final Logger log = LH.get();

	public static final ObjectToJsonConverter INSTANCE = new ObjectToJsonConverter();
	public static final ObjectToJsonConverter INSTANCE_NOT_COMPACT = new ObjectToJsonConverter();
	static {
		INSTANCE.setCompactMode(true);
		INSTANCE.registerConverter(new TableToJsonConverter());
		INSTANCE_NOT_COMPACT.setCompactMode(false);
		INSTANCE_NOT_COMPACT.registerConverter(new TableToJsonConverter());
	}
	
	public static final String DETAILED_DESCRIPTION= ("\n\n"
			+ "For a more readable output, the json can be rendered with additional whitespace,"
			+ "or can be stored in condensed form."
			+ "\n\n");
	
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("toJson", String.class, "Object mapListOrLiteral,Boolean isCompact");
	static {
		VERIFIER.addDesc("Takes a map, list, or literal and returns a string representation of the json that would represent that object."
				+ DETAILED_DESCRIPTION);
		VERIFIER.addParamDesc(0, "A map, list, or literal");
		VERIFIER.addParamDesc(1, "\"true\" if the resulting json should contain minimal whitespace, \"false\" otherwise (for better readability).");
//		VERIFIER.addRetDesc("string representation of json"); // testing the add ret desc
		VERIFIER.addExample("test", true);
		VERIFIER.addExample(CH.l(1, 2, 3, CH.m("a", "apple", "b", "berry")), true);
		VERIFIER.addExample(CH.l(1, 2, 3, CH.m("a", "apple", "b", "berry")), false);
	}

	public AmiWebFunctionToJson(int position, DerivedCellCalculator p0, DerivedCellCalculator p1) {
		super(position, p0, p1);
		evalConsts();
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	protected ObjectToJsonConverter get1(Object o1) {
		return Boolean.TRUE.equals(o1) ? INSTANCE : INSTANCE_NOT_COMPACT;
	}

	private int logCount = 0;

	@Override
	public Object eval(Object o0, Object o1) {
		try {
			return ((ObjectToJsonConverter) o1).objectToString(o0);
		} catch (Exception e) {
			if (++logCount < 10)
				LH.warning(log, e);
			else if (logCount == 10)
				LH.warning(log, "Too many warnings, not logging anymore");
			return null;
		}

	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p0, DerivedCellCalculator p1) {
		return new AmiWebFunctionToJson(getPosition(), p0, p1);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionToJson(position, calcs[0], calcs[1]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
