package com.f1.utils.structs.table.derived;

import com.f1.base.Row;
import com.f1.base.Table;

public class AggregateCountColumn extends AggregateColumn {

	public AggregateCountColumn(Table table, int uid, int location, String id, String innerId) {
		super(table, uid, location, Integer.class, id, innerId);
	}

	@Override
	public Object calculate(Object val, Object oldValue, Object newValue) {
		Integer current = val == null ? 0 : (Integer) val;
		if ((oldValue == null) == (newValue == null))
			return current;
		else if (oldValue == null)
			return current + 1;
		else
			return current - 1;
	}

	@Override
	public String getMethodName() {
		return "count";
	}

	public Object recalc(Iterable<Row> innerRows) {
		int r = 0;
		int il = getInnerColumnLocation();
		for (Row row : innerRows)
			if (row.getAt(il) != null)
				r++;
		return r;
	}
}
