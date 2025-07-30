package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.base.Table;
import com.f1.utils.CsvHelper;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator2;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionParseCsv extends AbstractMethodDerivedCellCalculator2 {

	private static final ParamsDefinition VERIFIER = new ParamsDefinition("parseCsv", Table.class, "String text,Boolean firstLineIsHeader");
	static {
		VERIFIER.addDesc("Given a valid csv string, will parse and returns it as a table. All the columns will be of type String.");
		VERIFIER.addParamDesc(0, "String to parse");
		VERIFIER.addParamDesc(1, "Should the first row be considered the column titles, otherwise columns are automatically labeled: col1,col2,col3,...");
	}

	public AmiWebFunctionParseCsv(int position, DerivedCellCalculator p0, DerivedCellCalculator p1) {
		super(position, p0, p1);
		evalConsts();
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object eval(Object o0, Object o1) {
		return CsvHelper.parseCsv((String) o0, (Boolean) o1);
	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p0, DerivedCellCalculator p1) {
		return new AmiWebFunctionParseCsv(getPosition(), p0, p1);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionParseCsv(position, calcs[0], calcs[1]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}
}
