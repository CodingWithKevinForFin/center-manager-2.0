package com.f1.utils.structs.table.derived;

import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.math.PrimitiveMath;
import com.f1.utils.math.PrimitiveMathManager;

public class AggregateSumColumn extends AggregateColumn {

	private PrimitiveMath<?> math;
	private static final byte TYPE_FLOAT = 1;
	private static final byte TYPE_WHOLE = 2;
	private static final byte TYPE_OTHER = 0;
	private final byte type;

	public AggregateSumColumn(Table table, int uid, int location, Class<?> type, String id, String innerId) {
		super(table, uid, location, type = widenTypes(type), id, innerId);
		this.math = PrimitiveMathManager.INSTANCE.get(type);
		if (math.getReturnType() == Double.class || math.getReturnType() == Float.class) {
			this.type = TYPE_FLOAT;
		} else if (math.getReturnType() == Long.class || math.getReturnType() == Integer.class || math.getReturnType() == Byte.class || math.getReturnType() == Short.class) {
			this.type = TYPE_WHOLE;
		} else
			this.type = TYPE_OTHER;
	}

	private static Class<?> widenTypes(Class<?> type) {
		return type;
	}

	@Override
	public Object calculate(Object val, Object oldValue, Object newValue) {
		if (oldValue != null && type == TYPE_FLOAT && MH.isntNumber(((Number) oldValue).doubleValue()))
			return AggregateRow.NOT_AGGEGATED;//we are removing the min value, this means a recalc :(
		if (val == null)
			return newValue == null ? null : math.cast((Number) newValue);
		else if (OH.eq(oldValue, newValue))
			return val;
		if (oldValue != null)
			val = this.math.subtract((Number) val, (Number) oldValue);
		if (newValue != null)
			val = this.math.add((Number) val, (Number) newValue);
		return val;
	}
	@Override
	public String getMethodName() {
		return "sum";
	}
	public Object recalc(Iterable<Row> innerRows) {
		int il = getInnerColumnLocation();
		boolean isNull = true;
		if (type == TYPE_FLOAT) {
			double r = 0;
			for (Row row : innerRows) {
				Number val = (Number) row.getAt(il);
				if (val != null) {
					isNull = false;
					r += val.doubleValue();
				}
			}
			if (isNull)
				return null;
			return math.cast(r);
		} else if (type == TYPE_WHOLE) {
			long r = 0;
			for (Row row : innerRows) {
				Number val = (Number) row.getAt(il);
				if (val != null) {
					isNull = false;
					r += val.longValue();
				}
			}
			if (isNull)
				return null;
			return math.cast(r);
		} else {
			Number r = null;
			for (Row row : innerRows) {
				Number val = (Number) row.getAt(il);
				if (val != null) {
					if (r == null)
						r = val;
					else
						r = this.math.add(r, (Number) val);
				}
			}
			return r;
		}

	}

	//	private Number correctNan(Object val) {
	//		if (val instanceof Double && MH.isntNumber((Double) val))
	//			return null;
	//		if (val instanceof Float && MH.isntNumber((Float) val))
	//			return null;
	//		return val;
	//	}

}
