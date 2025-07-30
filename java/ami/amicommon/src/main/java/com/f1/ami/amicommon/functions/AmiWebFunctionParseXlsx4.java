package com.f1.ami.amicommon.functions;

import java.util.List;

import com.f1.base.Bytes;
import com.f1.base.Table;
import com.f1.utils.SH;
import com.f1.utils.XlsxHelper;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculatorN;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionParseXlsx4 extends AbstractMethodDerivedCellCalculatorN {

	private static final ParamsDefinition VERIFIER = new ParamsDefinition("parseXlsx", Table.class,
			"com.f1.base.Bytes data,Boolean firstLineIsHeader,String tableName,java.util.List classes,String dimensions");
	static {
		VERIFIER.addDesc("Given a valid Xlsx byte array data, will parse and return the specified table with the given types.");
		VERIFIER.addParamDesc(0, "Excel byte array to parse");
		VERIFIER.addParamDesc(1, "Should the first row be considered the column titles, otherwise columns are automatically labeled: col1,col2,col3,...");
		VERIFIER.addParamDesc(2, "Table/Sheet name to return");
		VERIFIER.addParamDesc(3,
				"List of expected column class types, use null to skip, speeds up parsing by removing type deduction (Supported: \"String\",\"Boolean\",\"Integer\",\"Long\",\"Float\",\"Double\")");
		VERIFIER.addParamDesc(4, "Excel dimensions to use for parsing");
	}

	public AmiWebFunctionParseXlsx4(int position, DerivedCellCalculator[] p) {
		super(position, p);
		evalConsts();
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object eval(Object[] o) {
		Bytes b = (Bytes) o[0];
		Boolean b2 = (Boolean) o[1];
		@SuppressWarnings("unchecked")
		List<String> list = (List<String>) o[3];
		Class<?>[] classArray = null;

		if (list != null && !list.isEmpty()) {
			classArray = new Class<?>[list.size()];
			for (int i = 0; i < list.size(); ++i) {
				String s = SH.toLowerCase(list.get(i));
				if (s.equals("boolean"))
					classArray[i] = Boolean.class;
				else if (s.equals("integer"))
					classArray[i] = Integer.class;
				else if (s.equals("float"))
					classArray[i] = Float.class;
				else if (s.equals("double"))
					classArray[i] = Double.class;
				else if (s.equals("long"))
					classArray[i] = Long.class;
				else
					classArray[i] = String.class;
			}
		}

		String tableName = (String) o[2];
		String dimensions = (String) o[4];

		return XlsxHelper.parseXlsxRange(b.getBytes(), b2, classArray, tableName, dimensions);
	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator[] p) {
		return new AmiWebFunctionParseXlsx4(getPosition(), p);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionParseXlsx4(position, calcs);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
