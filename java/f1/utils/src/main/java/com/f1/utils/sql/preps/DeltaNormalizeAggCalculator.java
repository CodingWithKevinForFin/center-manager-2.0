package com.f1.utils.sql.preps;

import java.util.ArrayList;

import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.CalcTypesStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class DeltaNormalizeAggCalculator extends AbstractPrepCalculator {
	private boolean isNull;
	private ArrayList<Double> values = new ArrayList<Double>();
	public static final String METHOD_NAME = "dnorm";
	public final static ParamsDefinition paramsDefinition;
	static {
		paramsDefinition = new ParamsDefinition(METHOD_NAME, Double.class, "Number value");
		paramsDefinition.addDesc(
				"similar to stack, but scaled so that the min value is zero. Short hand for: (stack(value) - value)/sum(value). This, in conjunction with norm(...) can used for preparing area charts that should fill an area, such as a pie chart.");
		paramsDefinition.addParamDesc(0, "");
		paramsDefinition.addAdvancedExample(
				"CREATE TABLE input(symbol String, id int, price double);\nINSERT INTO input VALUES (\"CAT\", 1, 50),(\"CAT\", 2, 54),(\"CAT\", 3, 52),(\"CAT\", 4, 52),(\"CAT\", 5, null),(\"CAT\", 6, 57);\nINSERT INTO input VALUES (\"IBM\", 1, 20),(\"IBM\", 2, 24),(\"IBM\", 3, 22),(\"IBM\", 4, 27),(\"IBM\", 5, null);\nCREATE TABLE result AS PREPARE *,dnorm(price) AS newCol FROM input PARTITION BY symbol;\nTable input = SELECT * FROM input;\nTABLE result = SELECT * FROM result;",
				new String[] { "input", "result" });
	}
	public final static PrepMethodFactory FACTORY = new PrepMethodFactory() {

		@Override
		public ParamsDefinition getDefinition() {
			return paramsDefinition;
		}

		@Override
		public AbstractPrepCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, CalcTypesStack context) {
			return new DeltaNormalizeAggCalculator(position, calcs[0]);
		}
	};

	public DeltaNormalizeAggCalculator(int position, DerivedCellCalculator inner) {
		super(position, inner);
	}

	@Override
	public Object get(CalcFrameStack lcvs, int position) {
		return isNull ? null : values.get(position);
	}
	@Override
	protected void visit(ReusableCalcFrameStack sf, PrepRows values) {
		this.values.clear();
		this.values.ensureCapacity(values.size());
		if (values.isEmpty())
			isNull = true;
		else {
			isNull = false;
			double doubleValue = 0;
			for (int i = 0, n = values.size(); i < n; i++) {
				Object value = inner.get(sf.reset(values.get(i)));
				if (value instanceof Number) {
					doubleValue += ((Number) value).doubleValue();
					this.values.add(doubleValue - ((Number) value).doubleValue());
				} else
					this.values.add(null);

			}
			normalizeValues(doubleValue);
		}
	}
	private void normalizeValues(Double doubleValue) {
		int i = 0;
		for (Number number : values) {
			if (number != null)
				values.set(i, number.doubleValue() / doubleValue);
			i++;
		}
	}
	@Override
	public DerivedCellCalculator copy() {
		return new DeltaNormalizeAggCalculator(getPosition(), inner.copy());
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