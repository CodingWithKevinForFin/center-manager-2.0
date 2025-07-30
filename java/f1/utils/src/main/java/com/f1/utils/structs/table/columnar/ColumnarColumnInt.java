package com.f1.utils.structs.table.columnar;

import java.util.Arrays;

import com.f1.base.BasicTypes;

public class ColumnarColumnInt extends ColumnarColumnPrimitive<Integer> {

	private int[] values;

	public ColumnarColumnInt(ColumnsTable table, int location, String id, int capacity, boolean allowNulls) {
		super(table, location, Integer.class, id, capacity, allowNulls);
		this.values = new int[capacity];
	}

	@Override
	protected void ensureCapacity(int size) {
		if (values.length < size)
			values = Arrays.copyOf(values, size);
		super.ensureCapacity(size);
	}

	@Override
	protected Integer setValueAtArrayIndex(int index, Integer value) {
		Integer r = getValueAtArrayIndex(index);
		if (value == null)
			setNullAtArrayIndex(index);
		else {
			values[index] = value;
			setNotNullAtArrayIndex(index);
		}
		return r;
	}
	@Override
	protected Integer getValueAtArrayIndex(int index) {
		if (isNullAtArrayIndex(index))
			return null;
		return values[index];
	}

	public int getInt(int location) {
		int idx = table.mapRowNumToIndex(location);
		if (isNullAtArrayIndex(idx))
			throw new NullPointerException();
		return values[idx];
	}
	public int getIntAtArrayIndex(int idx) {
		if (isNullAtArrayIndex(idx))
			throw new NullPointerException();
		return values[idx];
	}

	public boolean setInt(int location, int value) {
		int idx = table.mapRowNumToIndex(location);
		if (!setNotNullAtArrayIndex(idx) && values[idx] == value)
			return false;
		values[idx] = value;
		return true;
	}
	public void setIntAtArrayIndex(int idx, int value) {
		values[idx] = value;
		setNotNullAtArrayIndex(idx);
	}

	public int getIntOr(ColumnarRow location, int onNull) {
		int idx = location.getArrayIndex();
		if (isNullAtArrayIndex(idx))
			return onNull;
		return values[idx];
	}
	public int getIntOr(int location, int onNull) {
		int idx = table.mapRowNumToIndex(location);
		if (isNullAtArrayIndex(idx))
			return onNull;
		return values[idx];
	}
	public int getInt(ColumnarRow location) {
		int idx = location.getArrayIndex();
		if (isNullAtArrayIndex(idx))
			throw new NullPointerException();
		return values[idx];
	}

	public boolean setInt(ColumnarRow location, int value) {
		int idx = location.getArrayIndex();
		if (!setNotNullAtArrayIndex(idx) && values[idx] == value)
			return false;
		values[idx] = value;
		return true;
	}
	@Override
	protected void setValues(Object valuesArray, long[] nulls) {
		int[] values = (int[]) valuesArray;
		super.setValueNullsMask(nulls);
		this.values = values;
	}

	@Override
	public int[] getValues() {
		return values;
	}

	@Override
	public byte getBasicType() {
		return BasicTypes.INT;
	}
	@Override
	public StringBuilder toString(ColumnarRow row, StringBuilder sink) {
		if (isNull(row))
			return sink.append("null");
		else
			return sink.append(getInt(row));

	}
	@Override
	public void copyValue(ColumnarRow source, ColumnarRow target) {
		if (isNull(source))
			setNull(target);
		else
			setInt(target, getInt(source));
	}

	@Override
	public int[] getValuesCloned() {
		return getValues().clone();
	}
	@Override
	public int getPrimitiveMemorySize() {
		return 4;
	}
	@Override
	public long getLong(int loc) {
		return getInt(loc);
	}
	@Override
	public double getDouble(int loc) {
		return getInt(loc);
	}

}
