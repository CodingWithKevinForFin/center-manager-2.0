package com.f1.ami.amicommon.functions;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.f1.base.Mapping;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator1;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionUrlDecode extends AbstractMethodDerivedCellCalculator1 {
	private static final String DEFAULT_ENCODING = "UTF-8";
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("urlDecode", String.class, "String url");
	static {
		VERIFIER.addDesc("Decodes URL with UTF-8 and returns the resulting string.");
		VERIFIER.addParamDesc(0, "URL to decode");
	}

	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	public AmiWebFunctionUrlDecode(int position, DerivedCellCalculator p0) {
		super(position, p0);
	}

	@Override
	public String eval(Object o0) {
		String url = (String) o0;
		if (url == null)
			return null;
		String decoded;
		try {
			decoded = URLDecoder.decode(url, DEFAULT_ENCODING);
		} catch (UnsupportedEncodingException e) {
			return null;
		}

		return decoded;
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator params01) {
		return new AmiWebFunctionUrlDecode(getPosition(), params01);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionUrlDecode(position, calcs[0]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
