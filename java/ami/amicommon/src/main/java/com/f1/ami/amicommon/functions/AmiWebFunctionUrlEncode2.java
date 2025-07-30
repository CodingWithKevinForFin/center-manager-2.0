package com.f1.ami.amicommon.functions;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator2;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionUrlEncode2 extends AbstractMethodDerivedCellCalculator2 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("urlEncode", String.class, "String url,String encoding");
	static {
		VERIFIER.addDesc("Encodes URL with the specified encoding scheme and returns the resulting string.");
		VERIFIER.addParamDesc(0, "URL to encode");
		VERIFIER.addParamDesc(1,
				"The encoding scheme. Supported encodings are US-ASCII, ISO-8859-1, UTF-8, UTF-16BE, UTF-16LE, UTF-16. See Java's standard charset for detail on each.");
	}

	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	public AmiWebFunctionUrlEncode2(int position, DerivedCellCalculator p0, DerivedCellCalculator p1) {
		super(position, p0, p1);
		evalConsts();
	}

	@Override
	public String eval(Object o0, Object o1) {
		String url = (String) o0;
		String encoding = (String) o1;
		if (url == null || encoding == null)
			return null;
		String encoded;
		try {
			encoded = URLEncoder.encode(url, encoding);
		} catch (UnsupportedEncodingException e) {
			return null;
		}

		return encoded;
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator params01, DerivedCellCalculator params02) {
		return new AmiWebFunctionUrlEncode2(getPosition(), params01, params02);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionUrlEncode2(position, calcs[0], calcs[1]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
