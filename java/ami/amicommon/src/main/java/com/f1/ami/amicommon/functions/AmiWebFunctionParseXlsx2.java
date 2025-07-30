package com.f1.ami.amicommon.functions;

import com.f1.base.Bytes;
import com.f1.base.Mapping;
import com.f1.base.Table;
import com.f1.utils.XlsxHelper;
import com.f1.utils.sql.Tableset;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator3;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionParseXlsx2 extends AbstractMethodDerivedCellCalculator3 {

	private static final ParamsDefinition VERIFIER = new ParamsDefinition("parseXlsx", Table.class, "com.f1.base.Bytes data,Boolean firstLineIsHeader,String tableName");
	static {
		VERIFIER.addDesc("Given a valid Xlsx byte array data, will parse and return the specified table.");
		VERIFIER.addParamDesc(0, "Excel byte array to parse");
		VERIFIER.addParamDesc(1, "Should the first row be considered the column titles, otherwise columns are automatically labeled: col1,col2,col3,...");
		VERIFIER.addParamDesc(2, "Table/Sheet name to look for");
	}

	public AmiWebFunctionParseXlsx2(int position, DerivedCellCalculator p0, DerivedCellCalculator p1, DerivedCellCalculator p2) {
		super(position, p0, p1, p2);
		evalConsts();
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object eval(Object o0, Object o1, Object o2) {
		Bytes b = (Bytes) o0;
		Boolean b2 = (Boolean) o1;

		String tableName = (String) o2;
		Tableset ts = XlsxHelper.parseXlsx(b.getBytes(), b2, null, tableName);
		if (ts != null)
			return ts.getTableNoThrow(tableName);
		return null;
	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p0, DerivedCellCalculator p1, DerivedCellCalculator p2) {
		return new AmiWebFunctionParseXlsx2(getPosition(), p0, p1, p2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionParseXlsx2(position, calcs[0], calcs[1], calcs[2]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}
}
