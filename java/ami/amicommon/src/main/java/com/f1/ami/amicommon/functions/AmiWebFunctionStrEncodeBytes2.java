package com.f1.ami.amicommon.functions;

import java.io.UnsupportedEncodingException;

import com.f1.base.Bytes;
import com.f1.base.Mapping;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator2;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionStrEncodeBytes2 extends AbstractMethodDerivedCellCalculator2 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("strEncodeBytes", Bytes.class, "String str,String encoding");
	static {
		VERIFIER.addDesc(
				"Encodes String and returns it as a Byte using the given encoding scheme. Supported schemes include US-ASCII, ISO-8859-1, UTF-8, UTF-16BE, UTF-16LE, UTF-16.");
		VERIFIER.addParamDesc(0, "string to encode");
		VERIFIER.addParamDesc(1, "encoding scheme");
	}

	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	public AmiWebFunctionStrEncodeBytes2(int position, DerivedCellCalculator p0, DerivedCellCalculator p1) {
		super(position, p0, p1);
		evalConsts();
	}

	@Override
	public Bytes eval(Object o0, Object o1) {
		String stringData = (String) o0;
		String encoding = (String) o1;
		if (stringData == null || encoding == null)
			return null;
		byte[] data = null;
		try {
			data = stringData.getBytes(encoding);
			return new Bytes(data);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator params01, DerivedCellCalculator params02) {
		return new AmiWebFunctionStrEncodeBytes2(getPosition(), params01, params02);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionStrEncodeBytes2(position, calcs[0], calcs[1]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
