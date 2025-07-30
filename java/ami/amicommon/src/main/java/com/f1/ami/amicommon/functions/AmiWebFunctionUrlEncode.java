package com.f1.ami.amicommon.functions;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.f1.base.Mapping;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator1;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionUrlEncode extends AbstractMethodDerivedCellCalculator1 {
	private static final String DEFAULT_ENCODING = "UTF-8";
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("urlEncode", String.class, "String url");
	static {
		VERIFIER.addDesc("Encodes the URL with UTF-8 and returns the resulting string.");
		VERIFIER.addParamDesc(0, "URL to encode");
	}

	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	public AmiWebFunctionUrlEncode(int position, DerivedCellCalculator p0) {
		super(position, p0);
	}

	@Override
	public String eval(Object o0) {
		String url = (String) o0;
		if (url == null)
			return null;
		String encoded;
		try {
			encoded = URLEncoder.encode(url, DEFAULT_ENCODING);
		} catch (UnsupportedEncodingException e) {
			return null;
		}

		return encoded;
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator params01) {
		return new AmiWebFunctionUrlEncode(getPosition(), params01);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionUrlEncode(position, calcs[0]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
