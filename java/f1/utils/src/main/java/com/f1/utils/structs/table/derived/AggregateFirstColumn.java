package com.f1.utils.structs.table.derived;

import com.f1.base.Row;
import com.f1.base.Table;

public class AggregateFirstColumn extends AggregateColumn {

	public AggregateFirstColumn(Table table, int uid, int location, Class<?> type, String id, String innerId) {
		super(table, uid, location, type, id, innerId);
	}

	@Override
	public Object calculate(Object val, Object oldValue, Object newValue) {
		if (val == oldValue)
			return AggregateRow.NOT_AGGEGATED;
		if (val == null)
			return newValue;
		else
			return val;
	}

	@Override
	public Object recalc(Iterable<Row> innerRows) {
		for (Row row : innerRows)
			return row.getAt(getInnerColumnLocation());
		return null;
	}
	@Override
	public String getMethodName() {
		return "min";
	}
}
