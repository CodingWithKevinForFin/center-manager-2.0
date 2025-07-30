package com.f1.utils.structs.table.derived;

import java.util.Comparator;

import com.f1.base.Table;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.math.PrimitiveMath;
import com.f1.utils.math.PrimitiveMathManager;
import com.f1.utils.string.ExpressionParserException;

public class AggregateMinColumn extends AggregateColumn {

	private Comparator<Object> math;
	private PrimitiveMath primitiveMath;

	public AggregateMinColumn(Table table, int uid, int location, Class<?> type, String id, String innerId) {
		super(table, uid, location, type, id, innerId);
		if (type == String.class) {
			this.math = SH.COMPARATOR_CASEINSENSITIVE;
			this.primitiveMath = null;
		} else
			try {
				this.math = this.primitiveMath = PrimitiveMathManager.INSTANCE.get(type);
			} catch (Exception e) {
				throw new ExpressionParserException(-1, "Can not aggregate this type of data: " + type.getSimpleName());
			}
	}

	@Override
	public Object calculate(Object val, Object oldValue, Object newValue) {
		if (val == null)
			return newValue == null ? null : cast(newValue);
		else if (OH.eq(oldValue, newValue))
			return val;
		else if (newValue != null && lt(newValue, val)) //add or update
			return cast(newValue);
		else if (oldValue != null && OH.eq(oldValue, val)) //remove or update
			return AggregateRow.NOT_AGGEGATED;//we are removing the min value, this means a recalc :(
		return val;
	}
	private Object cast(Object newValue) {
		return primitiveMath == null ? newValue : primitiveMath.cast((Number) newValue);
	}
	private boolean lt(Object val, Object val2) {
		return math.compare(val, val2) < 0;
	}
	private boolean gt(Object val, Object val2) {
		return math.compare(val, val2) > 0;
	}
	@Override
	public String getMethodName() {
		return "min";
	}
}
