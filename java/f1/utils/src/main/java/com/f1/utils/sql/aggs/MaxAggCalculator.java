package com.f1.utils.sql.aggs;

import java.util.List;

import com.f1.base.CalcFrame;
import com.f1.utils.OH;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class MaxAggCalculator extends AbstractAggCalculator implements AggDeltaCalculator {
	public static final String METHOD_NAME = "max";
	public final static ParamsDefinition paramsDefinition;
	static {
		paramsDefinition = new ParamsDefinition(METHOD_NAME, Comparable.class, "Comparable value");
		paramsDefinition.addDesc("Maximum value, skips nulls.");
		paramsDefinition.addParamDesc(0, "");
	}
	public final static AggMethodFactory FACTORY = new AggMethodFactory() {

		@Override
		public ParamsDefinition getDefinition() {
			return paramsDefinition;
		}

		@Override
		public AbstractAggCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new MaxAggCalculator(position, calcs[0]);
		}
	};
	private Object value = null;

	public MaxAggCalculator(int position, DerivedCellCalculator inner) {
		super(position, inner);
		if (!Comparable.class.isAssignableFrom(inner.getReturnType()))
			throw new ExpressionParserException(position, "min(...) can only operate on ordinal values, not " + inner.getReturnType().getSimpleName());
	}
	@Override
	public Object get(CalcFrameStack lcvs) {
		return value;
	}
	@Override
	public void visit(ReusableCalcFrameStack sf, List<? extends CalcFrame> values) {
		value = null;
		for (CalcFrame row : values) {
			Object value = inner.get(sf.reset(row));
			if (value != null && (this.value == null || OH.compare((Comparable) value, (Comparable) this.value) > 0))
				this.value = value;
		}
	}
	@Override
	public DerivedCellCalculator copy() {
		return new MaxAggCalculator(getPosition(), inner.copy());
	}
	@Override
	public Class<?> getReturnType() {
		return inner.getReturnType();
	}

	@Override
	public String getMethodName() {
		return "max";
	}
	@Override
	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public Object applyDelta(Object val, Object oldValue, Object newValue) {
		if (val == NOT_AGGEGATED)
			return NOT_AGGEGATED;
		if (val == null)
			return newValue == null ? null : newValue;
		else if (OH.eq(oldValue, newValue))
			return val;
		else if (newValue != null && gt(newValue, val)) //add or update
			return newValue;
		else if (oldValue != null && OH.eq(oldValue, val)) //remove or update
			return NOT_AGGEGATED;//we are removing the min value, this means a recalc :(
		return val;
	}
	private boolean gt(Object val, Object val2) {
		return OH.compare((Comparable) val, (Comparable) val2) > 0;
	}
	@Override
	public Object getUnderlying(CalcFrameStack values) {
		return inner.get(values);
	}
	@Override
	public void visitRows(CalcFrameStack values, long count) {
		setValue(getReturnType().cast(inner.get(values)));
	}
}
