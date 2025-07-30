package com.f1.ami.amicommon.functions;

import java.util.Map;

import com.f1.base.Bytes;
import com.f1.utils.XlsxHelper;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator1;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionParseRawXlsx extends AbstractMethodDerivedCellCalculator1 {

	private static final ParamsDefinition VERIFIER = new ParamsDefinition("parseRawXlsx", Map.class, "com.f1.base.Bytes data");
	static {
		VERIFIER.addDesc("Given a valid Xlsx byte array data, will parse and return files as a Map of XmlElements");
		VERIFIER.addParamDesc(0, "Excel byte array to parse");
	}

	public AmiWebFunctionParseRawXlsx(int position, DerivedCellCalculator p0) {
		super(position, p0);
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object eval(Object o0) {
		Bytes b = (Bytes)o0;
		return XlsxHelper.parseRawXlsx(b.getBytes());
	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p0) {
		return new AmiWebFunctionParseRawXlsx(getPosition(), p0);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionParseRawXlsx(position, calcs[0]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}
}
