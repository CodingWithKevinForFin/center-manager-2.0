package com.f1.utils.structs.table.columnar;

import com.f1.base.BasicTypes;
import com.f1.base.DateMillis;

public class ColumnarColumnDateMillis extends ColumnarColumnLongWrapper<DateMillis> {

	public ColumnarColumnDateMillis(ColumnsTable table, int location, String id, int capacity, boolean allowNulls) {
		super(table, location, DateMillis.class, id, capacity, allowNulls);
	}

	@Override
	protected DateMillis wrap(long v) {
		return new DateMillis(v);
	}

	@Override
	protected long unwrap(DateMillis v) {
		return v.getDate();
	}

	@Override
	public byte getBasicType() {
		return BasicTypes.DATE_MILLIS;
	}

}
