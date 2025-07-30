package com.f1.ami.amicommon.functions;

import java.net.MalformedURLException;
import java.net.URL;

import com.f1.base.Mapping;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator1;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionUrlPath extends AbstractMethodDerivedCellCalculator1 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("urlPath", String.class, "String url");
	static {
		VERIFIER.addDesc("Returns the path of the URL provided.");
		VERIFIER.addParamDesc(0, "URL to get path of");
		VERIFIER.addExample("https://google.com/");
		VERIFIER.addExample("https://google.com/test.png");
		VERIFIER.addExample("https://google.com/test2.html?name=james");
	}

	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	public AmiWebFunctionUrlPath(int position, DerivedCellCalculator p0) {
		super(position, p0);
	}

	@Override
	public String eval(Object o0) {
		if (o0 == null)
			return null;
		String strUrl = (String) o0;
		try {
			return new URL(strUrl).getPath();
		} catch (MalformedURLException e) {
			return null;
		}
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator params01) {
		return new AmiWebFunctionUrlPath(getPosition(), params01);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionUrlPath(position, calcs[0]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
