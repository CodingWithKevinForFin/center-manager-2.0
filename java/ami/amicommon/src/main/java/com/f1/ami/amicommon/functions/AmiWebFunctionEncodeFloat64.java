package com.f1.ami.amicommon.functions;

import com.f1.utils.encrypt.EncoderUtils;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator1;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionEncodeFloat64 extends AbstractMethodDerivedCellCalculator1 {

	private static final ParamsDefinition VERIFIER = new ParamsDefinition("encodeFloat64", String.class, "Number value");
	static {
		VERIFIER.addDesc("Encodes a float to a base64 string prepended with an F. This is the same encoding used in AmiClient::addMessageParamFloatEncoded");
		VERIFIER.addParamDesc(0, "float value to encode");
		VERIFIER.addRetDesc("7 character encoded string");
		VERIFIER.addExample(123f);
		VERIFIER.addExample(-123f);
		VERIFIER.addExample(0);
	}

	public AmiWebFunctionEncodeFloat64(int position, DerivedCellCalculator params) {
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
		StringBuilder sink = new StringBuilder(7);
		EncoderUtils.encodeInt64(Float.floatToRawIntBits(((Number) t).floatValue()), sink.append("F"));
		return sink.toString();

	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator params2) {
		return new AmiWebFunctionEncodeFloat64(getPosition(), params2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionEncodeFloat64(position, calcs[0]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
