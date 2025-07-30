package com.f1.utils.sql.aggs;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import com.f1.base.Caster;
import com.f1.base.CalcFrame;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.math.PrimitiveMath;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class SumAggCalculator extends AbstractAggCalculator implements AggDeltaCalculator {
	public static final String METHOD_NAME = "sum";
	public final static ParamsDefinition paramsDefinition;
	static {
		paramsDefinition = new ParamsDefinition(METHOD_NAME, Number.class, "Number value");
		paramsDefinition.addDesc("Summation of all values, skips nulls.");
		paramsDefinition.addParamDesc(0, "");
	}
	public final static AggMethodFactory FACTORY = new AggMethodFactory() {

		@Override
		public ParamsDefinition getDefinition() {
			return paramsDefinition;
		}

		@Override
		public AbstractAggCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new SumAggCalculator(position, calcs[0]);
		}
	};
	private static final byte TYPE_LONG = 1;
	private static final byte TYPE_FLOAT = 2;
	private static final byte TYPE_OTHER = 3;
	private Number objectValue;
	private boolean isNull;
	final private byte type;
	private final Class<?> returnType;
	private final Caster<?> caster;
	private PrimitiveMath manager;

	public SumAggCalculator(int position, DerivedCellCalculator inner) {
		super(position, inner);
		this.manager = getPrimitiveMathManager(inner);
		this.returnType = manager.getReturnType();
		this.caster = manager.getCaster();
		if (manager.getReturnType() == BigInteger.class || manager.getReturnType() == BigDecimal.class)
			type = TYPE_OTHER;
		else
			type = (returnType == Float.class || returnType == Double.class || returnType == float.class || returnType == double.class) ? TYPE_FLOAT : TYPE_LONG;
	}
	@Override
	public Object get(CalcFrameStack lcvs) {
		return isNull ? null : this.caster.cast(objectValue);
	}
	@Override
	public void visit(ReusableCalcFrameStack sf, List<? extends CalcFrame> values) {
		if (values.isEmpty())
			isNull = true;
		else if (type == TYPE_FLOAT) {
			isNull = false;
			double doubleValue = 0;
			for (CalcFrame row : values) {
				Object value = inner.get(sf.reset(row));
				if (value instanceof Number)
					doubleValue += ((Number) value).doubleValue();
			}
			this.objectValue = doubleValue;
		} else if (type == TYPE_LONG) {
			isNull = false;
			long longValue = 0;
			for (CalcFrame row : values) {
				Object value = inner.get(sf.reset(row));
				if (value instanceof Number)
					longValue += ((Number) value).longValue();
			}
			this.objectValue = longValue;
		} else {
			isNull = false;
			Number numValue = manager.cast(0);
			for (CalcFrame row : values) {
				Object value = inner.get(sf.reset(row));
				if (value instanceof Number)
					numValue = manager.add(numValue, (Number) value);
			}
			this.objectValue = numValue;
		}

	}
	@Override
	public DerivedCellCalculator copy() {
		return new SumAggCalculator(getPosition(), inner.copy());
	}
	@Override
	public Class<?> getReturnType() {
		return returnType;
	}
	@Override
	public String getMethodName() {
		return "sum";
	}
	@Override
	public void setValue(Object value) {
		if (value == null)
			isNull = true;
		else {
			isNull = false;
			this.objectValue = (Number) value;
		}
	}
	@Override
	public Object applyDelta(Object val, Object oldValue, Object newValue) {
		if (oldValue != null && type == TYPE_FLOAT && MH.isntNumber(((Number) oldValue).doubleValue()))
			return NOT_AGGEGATED;//we are removing the min value, this means a recalc :(
		if (val == null)
			return newValue == null ? 0 : manager.cast((Number) newValue);
		else if (OH.eq(oldValue, newValue))
			return val;
		if (oldValue != null)
			val = this.manager.subtract((Number) val, (Number) oldValue);
		if (newValue != null)
			val = this.manager.add((Number) val, (Number) newValue);
		return val;
	}
	@Override
	public Object getUnderlying(CalcFrameStack values) {
		Object r = inner.get(values);
		if (!(r instanceof Number))
			return null;
		Number n = (Number) r;
		switch (type) {
			case TYPE_FLOAT:
				return n instanceof Double ? n : n.doubleValue();
			case TYPE_LONG:
				return n instanceof Long ? n : n.longValue();
			default:
				return (Number) r;
		}
	}
	@Override
	public void visitRows(CalcFrameStack values, long count) {
		Object value = inner.get(values);
		if (value instanceof Number)
			setValue(manager.multiply((Number) value, count));
		else
			setValue(null);
	}
}
