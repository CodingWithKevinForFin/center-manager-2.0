package com.f1.utils.structs.table.columnar;

import com.f1.base.BasicTypes;
import com.f1.base.DateNanos;

public class ColumnarColumnDateNanos extends ColumnarColumnLongWrapper<DateNanos> {

	public ColumnarColumnDateNanos(ColumnsTable table, int location, String id, int capacity, boolean allowNulls) {
		super(table, location, DateNanos.class, id, capacity, allowNulls);
	}

	@Override
	protected DateNanos wrap(long v) {
		return new DateNanos(v);
	}

	@Override
	protected long unwrap(DateNanos v) {
		return v.getTimeNanos();
	}

	@Override
	public byte getBasicType() {
		return BasicTypes.DATE_NANOS;
	}

}
