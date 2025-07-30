package com.f1.utils.sql.preps;

import java.util.ArrayList;
import java.util.HashMap;

import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class LastAggCalculator extends AbstractPrepCalculator {
	public static final String METHOD_NAME = "lastNotNull";
	public static ParamsDefinition paramsDefinition;
	static {
		paramsDefinition = new ParamsDefinition(METHOD_NAME, Object.class, "Object value");
		paramsDefinition.addDesc("Retrieves the last non-null value up to the current row, if all prior values are null then lastNotNull will return null");
		paramsDefinition.addParamDesc(0, "column to query");
		paramsDefinition.addAdvancedExample(
				"CREATE TABLE input(symbol String, id int, price double);\nINSERT INTO input VALUES (\"CAT\", 1, 50),(\"CAT\", 2, 54),(\"CAT\", 3, 52),(\"CAT\", 4, 52),(\"CAT\", 5, null),(\"CAT\", 6, 57);\nINSERT INTO input VALUES (\"IBM\", 1, 20),(\"IBM\", 2, 24),(\"IBM\", 3, 22),(\"IBM\", 4, 27),(\"IBM\", 5, null);\nCREATE TABLE result AS PREPARE *,lastNotNull(price) AS newCol FROM input PARTITION BY symbol;\nTable input = SELECT * FROM input;\nTABLE result = SELECT * FROM result;",
				new String[] { "input", "result" });
	}
	public static PrepMethodFactory FACTORY = new PrepMethodFactory() {

		@Override
		public ParamsDefinition getDefinition() {
			return paramsDefinition;
		}

		@Override
		public AbstractPrepCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new LastAggCalculator(position, calcs[0]);
		}
	};
	private boolean isNull;
	private ArrayList<Object> values = new ArrayList<Object>();
	private HashMap<String, Integer> map;

	public LastAggCalculator(int position, DerivedCellCalculator inner) {
		super(position, inner);
	}

	@Override
	public Object get(CalcFrameStack lcvs, int position) {
		return isNull ? null : values.get(position);
	}
	@Override
	public void visit(ReusableCalcFrameStack sf, PrepRows values) {
		this.values.clear();
		this.values.ensureCapacity(values.size());
		if (values.isEmpty()) {
			isNull = true;
		} else {
			isNull = false;
			int count = 0;
			Object v = null;
			for (int j = 0, n = values.size(); j < n; j++) {
				Object value = inner.get(sf.reset(values.get(j)));
				if (value != null)
					v = value;
				this.values.add(v);
			}
		}
	}
	@Override
	public DerivedCellCalculator copy() {
		return new LastAggCalculator(getPosition(), inner.copy());
	}
	@Override
	public Class<?> getReturnType() {
		return this.inner.getReturnType();
	}
	@Override
	public String getMethodName() {
		return METHOD_NAME;
	}
}