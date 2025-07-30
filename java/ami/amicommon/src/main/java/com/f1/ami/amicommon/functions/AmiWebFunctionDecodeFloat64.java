package com.f1.ami.amicommon.functions;

import com.f1.utils.encrypt.EncoderUtils;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator1;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionDecodeFloat64 extends AbstractMethodDerivedCellCalculator1 {

	private static final ParamsDefinition VERIFIER = new ParamsDefinition("decodeFloat64", Float.class, "String value");
	static {
		VERIFIER.addDesc("Decodes a float that was encoded using encodeFloat64. ");
		VERIFIER.addParamDesc(0, "7 character encoded string");
		VERIFIER.addRetDesc("Decoded float");
	}

	public AmiWebFunctionDecodeFloat64(int position, DerivedCellCalculator params) {
		super(position, params);
	}

	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}
	@Override
	public Class<?> getReturnType() {
		return String.class;
	}

	@Override
	public Object eval(Object t) {
		if (t == null)
			return null;
		String s = (String) t;
		if (s.length() != 7 || s.charAt(0) != 'F')
			return null;
		return Float.intBitsToFloat(EncoderUtils.decodeInt64(s, 1));

	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator params2) {
		return new AmiWebFunctionDecodeFloat64(getPosition(), params2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionDecodeFloat64(position, calcs[0]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
