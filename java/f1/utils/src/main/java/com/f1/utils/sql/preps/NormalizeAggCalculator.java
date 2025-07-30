package com.f1.utils.sql.preps;

import java.util.ArrayList;

import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class NormalizeAggCalculator extends AbstractPrepCalculator {
	public static final String METHOD_NAME = "norm";
	public final static ParamsDefinition paramsDefinition;
	static {
		paramsDefinition = new ParamsDefinition(METHOD_NAME, Double.class, "Number value");
		paramsDefinition.addDesc("similar to stack, but scaled so that the max value is one. Short hand for: stack(value)/sum(value). See dnorm(...)");
		paramsDefinition.addParamDesc(0, "");
		paramsDefinition.addAdvancedExample(
				"CREATE TABLE input(symbol String, id int, price double);\nINSERT INTO input VALUES (\"CAT\", 1, 50),(\"CAT\", 2, 54),(\"CAT\", 3, 52),(\"CAT\", 4, 52),(\"CAT\", 5, null),(\"CAT\", 6, 57);\nINSERT INTO input VALUES (\"IBM\", 1, 20),(\"IBM\", 2, 24),(\"IBM\", 3, 22),(\"IBM\", 4, 27),(\"IBM\", 5, null);\nCREATE TABLE result AS PREPARE *,norm(price) AS newCol FROM input PARTITION BY symbol;\nTable input = SELECT * FROM input;\nTABLE result = SELECT * FROM result;",
				new String[] { "input", "result" });
	}
	public final static PrepMethodFactory FACTORY = new PrepMethodFactory() {

		@Override
		public ParamsDefinition getDefinition() {
			return paramsDefinition;
		}

		@Override
		public AbstractPrepCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new NormalizeAggCalculator(position, calcs[0]);
		}
	};
	private boolean isNull;
	private ArrayList<Number> values = new ArrayList<Number>();

	public NormalizeAggCalculator(int position, DerivedCellCalculator inner) {
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
			isNull = false;
			double doubleValue = 0;
			for (int j = 0, n = values.size(); j < n; j++) {
				Object value = inner.get(sf.reset(values.get(j)));
				if (value instanceof Number)
					doubleValue += ((Number) value).doubleValue();
				this.values.add(doubleValue);
			}
			normalizeValues();
		}
	}
	private void normalizeValues() {
		Double max = (Double) values.get(values.size() - 1);
		int i = 0;
		for (Number number : values) {
			values.set(i, number.doubleValue() / max);
			i++;
		}
	}
	@Override
	public DerivedCellCalculator copy() {
		return new NormalizeAggCalculator(getPosition(), inner.copy());
	}
	@Override
	public Class<?> getReturnType() {
		return Double.class;
	}
	@Override
	public String getMethodName() {
		return METHOD_NAME;
	}
}