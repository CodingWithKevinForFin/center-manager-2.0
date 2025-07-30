package com.f1.utils.sql.preps;

import java.util.ArrayList;

import com.f1.utils.math.PrimitiveMath;
import com.f1.utils.math.PrimitiveMathManager;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class RunningSumAggCalculator extends AbstractPrepCalculator {
	public static final String METHOD_NAME = "stack";
	public final static ParamsDefinition paramsDefinition;
	static {
		paramsDefinition = new ParamsDefinition(METHOD_NAME, Number.class, "Number value");
		paramsDefinition.addDesc("running sum of value from first to row n, inclusive");
		paramsDefinition.addParamDesc(0, "");
		paramsDefinition.addAdvancedExample(
				"CREATE TABLE input(symbol String, id int, price double);\nINSERT INTO input VALUES (\"CAT\", 1, 50),(\"CAT\", 2, 54),(\"CAT\", 3, 52),(\"CAT\", 4, 52),(\"CAT\", 5, null),(\"CAT\", 6, 57);\nINSERT INTO input VALUES (\"IBM\", 1, 20),(\"IBM\", 2, 24),(\"IBM\", 3, 22),(\"IBM\", 4, 27),(\"IBM\", 5, null);\nCREATE TABLE result AS PREPARE *,stack(price) AS newCol FROM input PARTITION BY symbol;\nTable input = SELECT * FROM input;\nTABLE result = SELECT * FROM result;",
				new String[] { "input", "result" });
	}
	public final static PrepMethodFactory FACTORY = new PrepMethodFactory() {

		@Override
		public ParamsDefinition getDefinition() {
			return paramsDefinition;
		}

		@Override
		public AbstractPrepCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new RunningSumAggCalculator(position, calcs[0]);
		}
	};
	private boolean isNull;
	final private boolean isFloat;
	private Class returnType;
	private ArrayList<Number> values = new ArrayList<Number>();

	public RunningSumAggCalculator(int position, DerivedCellCalculator inner) {
		super(position, inner);
		PrimitiveMath manager = PrimitiveMathManager.INSTANCE.get(inner.getReturnType());
		this.returnType = manager.getReturnType();
		isFloat = returnType == Float.class || returnType == Double.class || returnType == float.class || returnType == double.class;
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
		else if (isFloat) {
			isNull = false;
			double doubleValue = 0;
			for (int i = 0; i < values.size(); i++) {
				Object value = inner.get(sf.reset(values.get(i)));
				if (value instanceof Number)
					doubleValue += ((Number) value).doubleValue();
				this.values.add(doubleValue);
			}
		} else {
			isNull = false;
			long longValue = 0;
			for (int i = 0; i < values.size(); i++) {
				Object value = inner.get(sf.reset(values.get(i)));
				if (value instanceof Number)
					longValue += ((Number) value).longValue();
				this.values.add(longValue);
			}
		}
	}
	@Override
	public DerivedCellCalculator copy() {
		return new RunningSumAggCalculator(getPosition(), inner.copy());
	}
	@Override
	public Class<?> getReturnType() {
		return isFloat ? Double.class : Long.class;
	}
	@Override
	public String getMethodName() {
		return METHOD_NAME;
	}
}