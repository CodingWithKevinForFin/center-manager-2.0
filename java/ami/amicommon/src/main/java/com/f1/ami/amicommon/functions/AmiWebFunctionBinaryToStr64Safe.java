package com.f1.ami.amicommon.functions;

import com.f1.base.Bytes;
import com.f1.base.Mapping;
import com.f1.utils.encrypt.EncoderUtils;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator1;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionBinaryToStr64Safe extends AbstractMethodDerivedCellCalculator1 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("binaryToStr64Safe", String.class, "com.f1.base.Bytes data");
	static {
		VERIFIER.addDesc("Converts binary to a base 64 url-safe-encoded string.");
		VERIFIER.addParamDesc(0, "The binary data to convert");
	}

	public AmiWebFunctionBinaryToStr64Safe(int position, DerivedCellCalculator params) {
		super(position, params);
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object eval(Object o) {
		Bytes value = (Bytes) o;
		if (value == null)
			return null;
		return new String(EncoderUtils.encode64UrlSafe(value.getBytes()));
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator params2) {
		return new AmiWebFunctionBinaryToStr64Safe(getPosition(), params2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionBinaryToStr64Safe(position, calcs[0]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
