package com.f1.utils.sql.aggs;

import java.util.List;

import com.f1.base.CalcFrame;
import com.f1.base.TableList;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class CountAggCalculator extends AbstractAggCalculator implements AggDeltaCalculator {
	public static final String METHOD_NAME = "count";
	public final static ParamsDefinition paramsDefinition;
	static {
		paramsDefinition = new ParamsDefinition(METHOD_NAME, Long.class, "Object value");
		paramsDefinition.addDesc("Count of non-null values.");
		paramsDefinition.addParamDesc(0, "");
	}
	public final static AggMethodFactory FACTORY = new AggMethodFactory() {

		@Override
		public ParamsDefinition getDefinition() {
			return paramsDefinition;
		}

		@Override
		public AbstractAggCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new CountAggCalculator(position, calcs[0]);
		}
	};
	private long longValue = 0;

	private long constValue;

	public CountAggCalculator(int position, DerivedCellCalculator inner) {
		super(position, inner);
		if (inner.isConst())
			constValue = inner.get(null) == null ? 0 : 1;
		else
			constValue = -1;
	}
	@Override
	public Object get(CalcFrameStack lcvs) {
		return longValue;
	}
	@Override
	public void visit(ReusableCalcFrameStack sf, List<? extends CalcFrame> values) {
		longValue = 0;
		if (constValue != -1) {
			if (values instanceof TableList)
				longValue = ((TableList) values).getLongSize() * constValue;
			else
				longValue = values.size() * constValue;
		} else
			for (CalcFrame row : values) {
				Object value = inner.get(sf.reset(row));
				if (is(value))
					longValue += 1;
			}
	}
	private boolean is(Object value) {
		return value != null && !Boolean.FALSE.equals(value);
	}
	@Override
	public DerivedCellCalculator copy() {
		return new CountAggCalculator(getPosition(), inner.copy());
	}
	@Override
	public Class<?> getReturnType() {
		return Long.class;
	}
	@Override
	public String getMethodName() {
		return "count";
	}
	@Override
	public void setValue(Object object) {
		this.longValue = (Long) object;
	}
	@Override
	public Object applyDelta(Object val, Object oldValue, Object newValue) {
		Long current = val == null ? 0 : (Long) val;
		if (is(newValue))
			return is(oldValue) ? current : current + 1;
		else
			return is(oldValue) ? current - 1 : current;
	}
	@Override
	public Object getUnderlying(CalcFrameStack values) {
		return inner.get(values);
	}

	@Override
	public void visitRows(CalcFrameStack values, long count) {
		Object value = inner.get(values);
		setValue(is(value) ? count : 0L);
	}
}
