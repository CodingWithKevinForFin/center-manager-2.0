package com.f1.ami.amicommon.functions;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.f1.base.Mapping;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator2;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionUrlDecode2 extends AbstractMethodDerivedCellCalculator2 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("urlDecode", String.class, "String url,String encoding");
	static {
		VERIFIER.addDesc("Decodes URL with the specified encoding scheme and returns the resulting string.");
		VERIFIER.addParamDesc(0, "URL to decode");
		VERIFIER.addParamDesc(1, "text encoding");
	}

	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	public AmiWebFunctionUrlDecode2(int position, DerivedCellCalculator p0, DerivedCellCalculator p1) {
		super(position, p0, p1);
		evalConsts();
	}

	@Override
	public String eval(Object o0, Object o1) {
		String url = (String) o0;
		String encoding = (String) o1;
		if (url == null || encoding == null)
			return null;
		String decoded;
		try {
			decoded = URLDecoder.decode(url, encoding);
		} catch (UnsupportedEncodingException e) {
			return null;
		}

		return decoded;
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator params01, DerivedCellCalculator params02) {
		return new AmiWebFunctionUrlDecode2(getPosition(), params01, params02);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionUrlDecode2(position, calcs[0], calcs[1]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
