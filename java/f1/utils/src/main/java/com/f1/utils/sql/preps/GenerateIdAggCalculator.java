package com.f1.utils.sql.preps;

import java.util.ArrayList;

import com.f1.utils.OH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.concurrent.HasherMap.Entry;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.CalcTypesStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class GenerateIdAggCalculator extends AbstractPrepCalculator {
	public static final String METHOD_NAME = "unique";
	public final static ParamsDefinition paramsDefinition;
	static {
		paramsDefinition = new ParamsDefinition(METHOD_NAME, Integer.class, "Object value");
		paramsDefinition.addDesc("each unique evaluation of value will return a unique integer, starting at 1. Repeat occurrences of expr will return the same integer value.");
		paramsDefinition.addParamDesc(0, "");
		paramsDefinition.addAdvancedExample(
				"CREATE TABLE input(symbol String, id int, price double);\nINSERT INTO input VALUES (\"CAT\", 1, 50),(\"CAT\", 2, 54),(\"CAT\", 3, 52),(\"CAT\", 4, 52),(\"CAT\", 5, null),(\"CAT\", 6, 57);\nINSERT INTO input VALUES (\"IBM\", 1, 20),(\"IBM\", 2, 24),(\"IBM\", 3, 22),(\"IBM\", 4, 27),(\"IBM\", 5, null);\nCREATE TABLE result AS PREPARE *,unique(price) AS newCol FROM input PARTITION BY symbol;\nTable input = SELECT * FROM input;\nTABLE result = SELECT * FROM result;",
				new String[] { "input", "result" });
	}
	public final static PrepMethodFactory FACTORY = new PrepMethodFactory() {

		@Override
		public ParamsDefinition getDefinition() {
			return paramsDefinition;
		}

		@Override
		public AbstractPrepCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, CalcTypesStack context) {
			return new GenerateIdAggCalculator(position, calcs[0]);
		}
	};
	private boolean isNull;
	private ArrayList<Integer> values = new ArrayList<Integer>();

	public GenerateIdAggCalculator(int position, DerivedCellCalculator inner) {
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
		if (values.isEmpty())
			isNull = true;
		else {
			HasherMap<Object, Integer> map = new HasherMap<Object, Integer>();
			isNull = false;
			int i = 1;
			for (int j = 0, n = values.size(); j < n; j++) {
				Object value = inner.get(sf.reset(values.get(j)));
				Entry<Object, Integer> entry = map.getOrCreateEntry(value);
				Integer val = entry.getValue();
				if (val == null)
					entry.setValue(val = OH.valueOf(i++));
				this.values.add(OH.valueOf(val));
			}
		}
	}
	@Override
	public DerivedCellCalculator copy() {
		return new GenerateIdAggCalculator(getPosition(), inner.copy());
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
