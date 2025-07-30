package com.f1.utils.structs.table.columnar;

import java.util.Arrays;

import com.f1.base.BasicTypes;

public class ColumnarColumnLong extends ColumnarColumnPrimitive<Long> {

	private long[] values;

	public ColumnarColumnLong(ColumnsTable table, int location, String id, int capacity, boolean allowNulls) {
		super(table, location, Long.class, id, capacity, allowNulls);
		this.values = new long[capacity];
	}

	@Override
	protected void ensureCapacity(int size) {
		if (values.length < size)
			values = Arrays.copyOf(values, size);
		super.ensureCapacity(size);
	}

	@Override
	protected Long setValueAtArrayIndex(int index, Long value) {
		Long r = getValueAtArrayIndex(index);
		if (value == null)
			setNullAtArrayIndex(index);
		else {
			values[index] = value;
			setNotNullAtArrayIndex(index);
		}
		return r;
	}
	@Override
	protected Long getValueAtArrayIndex(int index) {
		if (isNullAtArrayIndex(index))
			return null;
		return values[index];
	}

	@Override
	public long getLong(int location) {
		int idx = table.mapRowNumToIndex(location);
		if (isNullAtArrayIndex(idx))
			throw new NullPointerException();
		return values[idx];
	}

	public long getLongAtArrayIndex(int idx) {
		if (isNullAtArrayIndex(idx))
			throw new NullPointerException();
		return values[idx];
	}
	public boolean setLongAtArrayIndex(int idx, long value) {
		if (!setNotNullAtArrayIndex(idx) && values[idx] == value)
			return false;
		values[idx] = value;
		return true;
	}

	public boolean setLong(int location, long value) {
		int idx = table.mapRowNumToIndex(location);
		if (!setNotNullAtArrayIndex(idx) && values[idx] == value)
			return false;
		values[idx] = value;
		return true;
	}
	public long getLongOr(ColumnarRow location, long nullValue) {
		int idx = location.getArrayIndex();
		if (isNullAtArrayIndex(idx))
			return nullValue;
		return values[idx];
	}
	public long getLongOr(int location, long nullValue) {
		int idx = table.mapRowNumToIndex(location);
		if (isNullAtArrayIndex(idx))
			return nullValue;
		return values[idx];
	}
	public long getLong(ColumnarRow location) {
		int idx = location.getArrayIndex();
		if (isNullAtArrayIndex(idx))
			throw new NullPointerException();
		return values[idx];
	}

	public boolean setLong(ColumnarRow location, long value) {
		int idx = location.getArrayIndex();
		if (!setNotNullAtArrayIndex(idx) && values[idx] == value)
			return false;
		values[idx] = value;
		return true;
	}
	@Override
	protected void setValues(Object valuesArray, long[] nulls) {
		long[] values = (long[]) valuesArray;
		super.setValueNullsMask(nulls);
		this.values = values;
	}

	@Override
	public long[] getValues() {
		return values;
	}
	@Override
	public byte getBasicType() {
		return BasicTypes.LONG;
	}
	@Override
	public StringBuilder toString(ColumnarRow row, StringBuilder sink) {
		if (isNull(row))
			return sink.append("null");
		else
			return sink.append(getLong(row));

	}
	@Override
	public void copyValue(ColumnarRow source, ColumnarRow target) {
		if (isNull(source))
			setNull(target);
		else
			setLong(target, getLong(source));
	}

	@Override
	public long[] getValuesCloned() {
		return getValues().clone();
	}
	@Override
	public int getPrimitiveMemorySize() {
		return 8;
	}
	@Override
	public double getDouble(int loc) {
		return (double) getLong(loc);
	}
}
