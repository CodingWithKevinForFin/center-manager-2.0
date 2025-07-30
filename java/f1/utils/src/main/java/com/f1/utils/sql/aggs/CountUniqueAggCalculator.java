package com.f1.utils.sql.aggs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f1.base.CalcFrame;
import com.f1.utils.OH;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class CountUniqueAggCalculator extends AbstractAggCalculator {
	public static final String METHOD_NAME = "countUnique";
	public final static ParamsDefinition paramsDefinition;
	static {
		paramsDefinition = new ParamsDefinition(METHOD_NAME, Long.class, "Object value");
		paramsDefinition.addDesc("Count of unique non-null values.");
		paramsDefinition.addParamDesc(0, "");
	}
	public final static AggMethodFactory FACTORY = new AggMethodFactory() {

		@Override
		public ParamsDefinition getDefinition() {
			return paramsDefinition;
		}

		@Override
		public AbstractAggCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new CountUniqueAggCalculator(position, calcs[0]);
		}
	};
	private long longValue = 0;

	public CountUniqueAggCalculator(int position, DerivedCellCalculator inner) {
		super(position, inner);
	}
	@Override
	public Object get(CalcFrameStack lcvs) {
		return longValue;
	}
	@Override
	public void visit(ReusableCalcFrameStack sf, List<? extends CalcFrame> values) {
		longValue = 0;
		boolean oneVal = true;
		boolean firstRow = true;
		Object firstArg = null;
		Map usedValues = new HashMap<Object, Object>();
		for (CalcFrame row : values) {
			Object value = inner.get(sf.reset(row));
			if (value == null)
				continue;
			if (firstRow) {
				firstArg = value;
				firstRow = false;
				longValue += 1;
			} else if (!oneVal || OH.ne(value, firstArg)) {
				if (oneVal) {
					usedValues.put(firstArg, null);
					oneVal = false;
				}
				if (!usedValues.containsKey(value)) {
					usedValues.put(value, null);
					longValue += 1;
				}
			}
		}
	}
	@Override
	public DerivedCellCalculator copy() {
		return new CountUniqueAggCalculator(getPosition(), inner.copy());
	}
	@Override
	public Class<?> getReturnType() {
		return Long.class;
	}
	@Override
	public String getMethodName() {
		return "countUnique";
	}
	@Override
	public void setValue(Object object) {
		longValue = (Long) object;
	}
	@Override
	public void visitRows(CalcFrameStack values, long count) {
		Object value = inner.get(values);
		setValue(value != null ? 1 : 0);
	}
}
