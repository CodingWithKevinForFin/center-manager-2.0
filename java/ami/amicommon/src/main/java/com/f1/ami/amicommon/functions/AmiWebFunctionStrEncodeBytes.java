package com.f1.ami.amicommon.functions;

import java.io.UnsupportedEncodingException;

import com.f1.base.Bytes;
import com.f1.base.Mapping;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator1;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionStrEncodeBytes extends AbstractMethodDerivedCellCalculator1 {
	private static final String DEFAULT_ENCODING = "UTF-8";
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("strEncodeBytes", Bytes.class, "String str");
	static {
		VERIFIER.addDesc("Encodes str using UTF-8 and returns it as a Byte.");
		VERIFIER.addParamDesc(0, "string to encode");
		VERIFIER.addExample("Hi this is Jay.");
		VERIFIER.addExample("");
		VERIFIER.addExample("123");
	}

	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	public AmiWebFunctionStrEncodeBytes(int position, DerivedCellCalculator p0) {
		super(position, p0);
	}

	@Override
	public Bytes eval(Object o0) {
		String stringData = (String) o0;
		if (stringData == null)
			return null;
		byte[] data = null;
		try {
			data = stringData.getBytes(DEFAULT_ENCODING);
			return new Bytes(data);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator params01) {
		return new AmiWebFunctionStrEncodeBytes(getPosition(), params01);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionStrEncodeBytes(position, calcs[0]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
