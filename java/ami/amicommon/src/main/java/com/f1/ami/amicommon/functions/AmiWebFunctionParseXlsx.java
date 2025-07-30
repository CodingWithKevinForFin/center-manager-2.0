package com.f1.ami.amicommon.functions;

import com.f1.base.Bytes;
import com.f1.utils.XlsxHelper;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator2;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.sql.Tableset;

public class AmiWebFunctionParseXlsx extends AbstractMethodDerivedCellCalculator2 {

	private static final ParamsDefinition VERIFIER = new ParamsDefinition("parseXlsx", Tableset.class, "com.f1.base.Bytes data,Boolean firstLineIsHeader");
	static {
		VERIFIER.addDesc("Given a valid Xlsx byte array data, will parse and return it as a tableset.");
		VERIFIER.addParamDesc(0, "Excel byte array to parse");
		VERIFIER.addParamDesc(1, "Should the first row be considered the column titles, otherwise columns are automatically labeled: col1,col2,col3,...");
	}

	public AmiWebFunctionParseXlsx(int position, DerivedCellCalculator p0, DerivedCellCalculator p1) {
		super(position, p0, p1);
		evalConsts();
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object eval(Object o0, Object o1) {
		Bytes b = (Bytes)o0;
		Boolean b2 = (Boolean)o1;
		return XlsxHelper.parseXlsx(b.getBytes(), b2, null, null);
	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p0, DerivedCellCalculator p1) {
		return new AmiWebFunctionParseXlsx(getPosition(), p0, p1);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionParseXlsx(position, calcs[0], calcs[1]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}
}
