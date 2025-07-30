package com.f1.ami.amicommon.functions;

import java.io.UnsupportedEncodingException;

import com.f1.base.Bytes;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator2;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionStrDecodeBytes2 extends AbstractMethodDerivedCellCalculator2 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("strDecodeBytes", String.class, "com.f1.base.Bytes data,String encoding");
	static {
		VERIFIER.addDesc(
				"Decodes bytes using the given encoding scheme and returns them as a String. Supported schemes include US-ASCII, ISO-8859-1, UTF-8, UTF-16BE, UTF-16LE, UTF-16.");
		VERIFIER.addRetDesc("decoded String");
		VERIFIER.addParamDesc(0, "bytes to decode");
		VERIFIER.addParamDesc(1, "encoding");
	}

	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	public AmiWebFunctionStrDecodeBytes2(int position, DerivedCellCalculator p0, DerivedCellCalculator p1) {
		super(position, p0, p1);
		evalConsts();
	}

	@Override
	public String eval(Object o0, Object o1) {
		Bytes bytes = (Bytes) o0;
		String encoding = (String) o1;
		if (bytes == null || encoding == null)
			return null;
		try {
			return new String(bytes.getBytes(), encoding);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator params01, DerivedCellCalculator params02) {
		return new AmiWebFunctionStrDecodeBytes2(getPosition(), params01, params02);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionStrDecodeBytes2(position, calcs[0], calcs[1]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
