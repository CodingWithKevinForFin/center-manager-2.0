package com.f1.ami.amicommon.functions;

import com.f1.utils.encrypt.EncoderUtils;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator1;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionEncodeDouble64 extends AbstractMethodDerivedCellCalculator1 {

	private static final ParamsDefinition VERIFIER = new ParamsDefinition("encodeDouble64", String.class, "Number value");
	static {
		VERIFIER.addDesc("Encodes a double to a base64 string prepended with an F. This is the same encoding used in AmiClient::addMessageParamDoubleEncoded");
		VERIFIER.addParamDesc(0, "double value to encode");
		VERIFIER.addRetDesc("11 character encoded string");
		VERIFIER.addExample(123d);
		VERIFIER.addExample(-123d);
		VERIFIER.addExample(0d);
	}

	public AmiWebFunctionEncodeDouble64(int position, DerivedCellCalculator params) {
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
		StringBuilder sink = new StringBuilder(12);
		EncoderUtils.encodeLong64(Double.doubleToRawLongBits(((Number) t).doubleValue()), sink.append("D"));
		return sink.toString();

	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator params2) {
		return new AmiWebFunctionEncodeDouble64(getPosition(), params2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionEncodeDouble64(position, calcs[0]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
