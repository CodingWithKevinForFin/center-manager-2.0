package com.f1.ami.amicommon.functions;

import java.io.UnsupportedEncodingException;

import com.f1.base.Bytes;
import com.f1.base.Mapping;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator1;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionStrDecodeBytes extends AbstractMethodDerivedCellCalculator1 {
	private static final String DEFAULT_ENCODING = "UTF-8";
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("strDecodeBytes", String.class, "com.f1.base.Bytes data");
	static {
		VERIFIER.addDesc("Decodes bytes using UTF-8 and returns them as a String.");
		VERIFIER.addParamDesc(0, "bytes to decode");
	}

	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	public AmiWebFunctionStrDecodeBytes(int position, DerivedCellCalculator p0) {
		super(position, p0);
	}

	@Override
	public String eval(Object o0) {
		Bytes bytes = (Bytes) o0;
		if (bytes == null)
			return null;
		try {
			return new String(bytes.getBytes(), DEFAULT_ENCODING);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator params01) {
		return new AmiWebFunctionStrDecodeBytes(getPosition(), params01);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionStrDecodeBytes(position, calcs[0]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
