package com.f1.utils.structs.table.columnar;

import java.util.Arrays;

import com.f1.base.BasicTypes;

public class ColumnarColumnBoolean extends ColumnarColumnPrimitive<Boolean> {

	private boolean[] values;

	public ColumnarColumnBoolean(ColumnsTable table, int location, String id, int capacity, boolean allowNull) {
		super(table, location, Boolean.class, id, capacity, allowNull);
		this.values = new boolean[capacity];
	}

	@Override
	protected void ensureCapacity(int size) {
		if (values.length < size)
			values = Arrays.copyOf(values, size);
		super.ensureCapacity(size);
	}

	@Override
	protected Boolean setValueAtArrayIndex(int index, Boolean value) {
		Boolean r = getValueAtArrayIndex(index);
		if (value == null)
			setNullAtArrayIndex(index);
		else {
			values[index] = value;
			setNotNullAtArrayIndex(index);
		}
		return r;
	}
	@Override
	protected Boolean getValueAtArrayIndex(int index) {
		if (isNullAtArrayIndex(index))
			return null;
		return values[index];
	}

	public void setBooleanAtArrayIndex(int index, boolean value) {
		values[index] = value;
		setNotNullAtArrayIndex(index);
	}

	public boolean getBoolean(int location) {
		int idx = table.mapRowNumToIndex(location);
		if (isNullAtArrayIndex(idx))
			throw new NullPointerException();
		return values[idx];
	}

	public boolean setBoolean(int location, boolean value) {
		int idx = table.mapRowNumToIndex(location);
		if (!setNotNullAtArrayIndex(idx) && values[idx] == value)
			return false;
		values[idx] = value;
		return true;
	}
	public boolean getBoolean(ColumnarRow location) {
		int idx = location.getArrayIndex();
		if (isNullAtArrayIndex(idx))
			throw new NullPointerException();
		return values[idx];
	}

	//returns true if changed
	public boolean setBoolean(ColumnarRow location, boolean value) {
		int idx = location.getArrayIndex();
		if (!setNotNullAtArrayIndex(idx) && values[idx] == value)
			return false;
		values[idx] = value;
		return true;
	}
	@Override
	protected void setValues(Object valuesArray, long[] nulls) {
		boolean[] values = (boolean[]) valuesArray;
		super.setValueNullsMask(nulls);
		this.values = values;
	}
	@Override
	public boolean[] getValues() {
		return values;
	}

	@Override
	public byte getBasicType() {
		return BasicTypes.BOOLEAN;
	}

	@Override
	public StringBuilder toString(ColumnarRow row, StringBuilder sink) {
		if (isNull(row))
			return sink.append("null");
		else
			return sink.append(getBoolean(row));

	}
	@Override
	public void copyValue(ColumnarRow source, ColumnarRow target) {
		if (isNull(source))
			setNull(target);
		else
			setBoolean(target, getBoolean(source));
	}

	@Override
	public boolean[] getValuesCloned() {
		return this.getValues().clone();
	}

	@Override
	public int getPrimitiveMemorySize() {
		return 1;
	}

	@Override
	public long getLong(int loc) {
		return getBoolean(loc) ? 1 : 0;
	}

	@Override
	public double getDouble(int loc) {
		return getBoolean(loc) ? 1D : 0D;
	}

}
