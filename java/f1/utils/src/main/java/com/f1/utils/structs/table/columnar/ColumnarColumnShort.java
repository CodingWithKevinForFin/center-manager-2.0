package com.f1.utils.structs.table.columnar;

import java.util.Arrays;

import com.f1.base.BasicTypes;

public class ColumnarColumnShort extends ColumnarColumnPrimitive<Short> {

	private short[] values;

	public ColumnarColumnShort(ColumnsTable table, int location, String id, int capacity, boolean allowNull) {
		super(table, location, Short.class, id, capacity, allowNull);
		this.values = new short[capacity];
	}

	@Override
	protected void ensureCapacity(int size) {
		if (values.length < size)
			values = Arrays.copyOf(values, size);
		super.ensureCapacity(size);
	}

	@Override
	protected Short setValueAtArrayIndex(int index, Short value) {
		Short r = getValueAtArrayIndex(index);
		if (value == null)
			setNullAtArrayIndex(index);
		else {
			values[index] = value;
			setNotNullAtArrayIndex(index);
		}
		return r;
	}
	public void setShortAtArrayIndex(int index, short value) {
		values[index] = value;
		setNotNullAtArrayIndex(index);
	}
	@Override
	protected Short getValueAtArrayIndex(int index) {
		if (isNullAtArrayIndex(index))
			return null;
		return values[index];
	}
	public short getShortAtArrayIndex(int index) {
		if (isNullAtArrayIndex(index))
			throw new NullPointerException();
		return values[index];
	}

	public short getShort(int location) {
		int idx = table.mapRowNumToIndex(location);
		if (isNullAtArrayIndex(idx))
			throw new NullPointerException();
		return values[idx];
	}

	public boolean setShort(int location, short value) {
		int idx = table.mapRowNumToIndex(location);
		if (!setNotNullAtArrayIndex(idx) && values[idx] == value)
			return false;
		values[idx] = value;
		return true;
	}
	public short getShort(ColumnarRow location) {
		int idx = location.getArrayIndex();
		if (isNullAtArrayIndex(idx))
			throw new NullPointerException();
		return values[idx];
	}

	public boolean setShort(ColumnarRow location, short value) {
		int idx = location.getArrayIndex();
		if (!setNotNullAtArrayIndex(idx) && values[idx] == value)
			return false;
		values[idx] = value;
		return true;
	}
	@Override
	protected void setValues(Object valuesArray, long nulls[]) {
		short[] values = (short[]) valuesArray;
		this.values = values;
		super.setValueNullsMask(nulls);
	}

	@Override
	public short[] getValues() {
		return values;
	}

	@Override
	public byte getBasicType() {
		return BasicTypes.SHORT;
	}
	@Override
	public StringBuilder toString(ColumnarRow row, StringBuilder sink) {
		if (isNull(row))
			return sink.append("null");
		else
			return sink.append(getShort(row));

	}
	@Override
	public void copyValue(ColumnarRow source, ColumnarRow target) {
		if (isNull(source))
			setNull(target);
		else
			setShort(target, getShort(source));
	}

	@Override
	public Object getValuesCloned() {
		return getValues().clone();
	}

	@Override
	public int getPrimitiveMemorySize() {
		return 2;
	}
	@Override
	public long getLong(int loc) {
		return getShort(loc);
	}
	@Override
	public double getDouble(int loc) {
		return getShort(loc);
	}
}
