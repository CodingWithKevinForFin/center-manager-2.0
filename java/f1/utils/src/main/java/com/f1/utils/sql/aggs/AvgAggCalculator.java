package com.f1.utils.sql.aggs;

import java.util.List;

import com.f1.base.CalcFrame;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.math.PrimitiveMath;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class AvgAggCalculator extends AbstractAggCalculator {
	public static final String METHOD_NAME = "avg";
	public final static ParamsDefinition paramsDefinition;
	static {
		paramsDefinition = new ParamsDefinition(METHOD_NAME, Double.class, "Number value");
		paramsDefinition.addDesc("Average of all non-null values.");
		paramsDefinition.addParamDesc(0, "value to take average of.");
		paramsDefinition.addAdvancedExample(
				"CREATE TABLE input(symbol String, price double);\nINSERT INTO input VALUES (\"MSFT\",100),(\"MSFT\",null),(\"MSFT\",200);\nINSERT INTO input VALUES (\"AAPL\",300),(\"AAPL\",450);\nTable result = SELECT symbol, avg(price) as avg FROM input GROUP BY symbol;\nTable input = SELECT * FROM input;",
				new String[] { "input", "result" }, "Note that the null value was avoided here");
	}
	public final static AggMethodFactory FACTORY = new AggMethodFactory() {

		@Override
		public ParamsDefinition getDefinition() {
			return paramsDefinition;
		}

		@Override
		public AbstractAggCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AvgAggCalculator(position, calcs[0]);
		}
	};
	private boolean isNull;
	final private boolean isFloat;
	private Class returnType;
	private double doubleValue;
	private int counter;

	public AvgAggCalculator(int position, DerivedCellCalculator inner) {
		super(position, inner);
		PrimitiveMath manager = getPrimitiveMathManager(inner);
		this.returnType = manager.getReturnType();
		isFloat = (returnType == Float.class || returnType == Double.class || returnType == float.class || returnType == double.class);
	}
	@Override
	public Object get(CalcFrameStack lcvs) {
		return this.isNull ? null : doubleValue;
	}
	@Override
	public void visit(ReusableCalcFrameStack sf, List<? extends CalcFrame> values) {
		if (values.isEmpty())
			isNull = true;
		else if (isFloat) {
			isNull = false;
			doubleValue = 0;
			counter = 0;
			for (int i = 0, l = values.size(); i < l; i++) {
				Object value = inner.get(sf.reset(values.get(i)));
				if (value instanceof Number) {
					doubleValue += ((Number) value).doubleValue();
					counter += 1;
				}
			}
			if (counter == 0)
				isNull = true;
			else
				doubleValue = doubleValue / counter;
		} else {
			isNull = false;
			doubleValue = 0;
			counter = 0;
			for (int i = 0, l = values.size(); i < l; i++) {
				Object value = inner.get(sf.reset(values.get(i)));
				if (value instanceof Number) {
					doubleValue += ((Number) value).longValue();
					counter += 1;
				}
			}
			if (counter == 0)
				isNull = true;
			else
				doubleValue = doubleValue / counter;
		}
	}
	@Override
	public DerivedCellCalculator copy() {
		return new AvgAggCalculator(getPosition(), inner.copy());
	}
	@Override
	public Class<?> getReturnType() {
		return Double.class;
	}
	@Override
	public String getMethodName() {
		return "avg";
	}
	@Override
	public void setValue(Object value) {
		if (value == null)
			isNull = true;
		else {
			isNull = false;
			this.doubleValue = (Double) value;
		}
	}
	@Override
	public void visitRows(CalcFrameStack values, long count) {
		setValue(Caster_Double.INSTANCE.cast(inner.get(values), false, false));
	}
}
