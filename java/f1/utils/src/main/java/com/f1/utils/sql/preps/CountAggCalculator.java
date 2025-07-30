package com.f1.utils.sql.preps;

import java.util.ArrayList;

import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class CountAggCalculator extends AbstractPrepCalculator {
	private boolean isNull;
	private ArrayList<Integer> values = new ArrayList<Integer>();

	public static final String METHOD_NAME = "count";
	public static ParamsDefinition paramsDefinition;
	static {
		paramsDefinition = new ParamsDefinition(METHOD_NAME, Integer.class, "Object value");
		paramsDefinition.addDesc("running count where value!=null and value!=false to the current row, inclusive");
		paramsDefinition.addParamDesc(0, "value to test for not null and not false");
		paramsDefinition.addAdvancedExample(
				"CREATE TABLE input(symbol String, id int, price double);\nINSERT INTO input VALUES (\"CAT\", 1, 50),(\"CAT\", 2, 54),(\"CAT\", 3, 52),(\"CAT\", 4, 52),(\"CAT\", 5, null),(\"CAT\", 6, 57);\nINSERT INTO input VALUES (\"IBM\", 1, 20),(\"IBM\", 2, 24),(\"IBM\", 3, 22),(\"IBM\", 4, 27),(\"IBM\", 5, null);\nCREATE TABLE result AS PREPARE *,count(price) AS newCol FROM input PARTITION BY symbol;\nTable input = SELECT * FROM input;\nTABLE result = SELECT * FROM result;",
				new String[] { "input", "result" });
	}
	public static PrepMethodFactory FACTORY = new PrepMethodFactory() {

		@Override
		public ParamsDefinition getDefinition() {
			return paramsDefinition;
		}

		@Override
		public AbstractPrepCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new CountAggCalculator(position, calcs[0]);
		}

	};

	public CountAggCalculator(int position, DerivedCellCalculator inner) {
		super(position, inner);
	}

	@Override
	public Object get(CalcFrameStack sf, int position) {
		return isNull ? null : values.get(position);
	}
	@Override
	protected void visit(ReusableCalcFrameStack sf, PrepRows values) {
		this.values.clear();
		this.values.ensureCapacity(values.size());
		if (values.isEmpty()) {
			isNull = true;
		} else {
			isNull = false;
			int count = 0;
			for (int i = 0, n = values.size(); i < n; i++) {
				Object value = inner.get(sf.reset(values.get(i)));
				if (value != null && !Boolean.FALSE.equals(value))
					count += 1;
				this.values.add(count);
			}
		}
	}
	@Override
	public DerivedCellCalculator copy() {
		return new CountAggCalculator(getPosition(), inner.copy());
	}
	@Override
	public Class<?> getReturnType() {
		return Integer.class;
	}
	@Override
	public String getMethodName() {
		return METHOD_NAME;
	}
}