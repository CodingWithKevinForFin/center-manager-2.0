package com.f1.utils.structs.table.columnar;

import java.util.Arrays;

import com.f1.base.BasicTypes;

public class ColumnarColumnByte extends ColumnarColumnPrimitive<Byte> {

	private byte[] values;

	public ColumnarColumnByte(ColumnsTable table, int location, String id, int capacity, boolean allowNulls) {
		super(table, location, Byte.class, id, capacity, allowNulls);
		this.values = new byte[capacity];
	}

	@Override
	protected void ensureCapacity(int size) {
		if (values.length < size)
			values = Arrays.copyOf(values, size);
		super.ensureCapacity(size);
	}

	@Override
	protected Byte setValueAtArrayIndex(int index, Byte value) {
		Byte r = getValueAtArrayIndex(index);
		if (value == null)
			setNullAtArrayIndex(index);
		else {
			values[index] = value;
			setNotNullAtArrayIndex(index);
		}
		return r;
	}
	public void setByteAtArrayIndex(int index, byte value) {
		values[index] = value;
		setNotNullAtArrayIndex(index);
	}
	@Override
	protected Byte getValueAtArrayIndex(int index) {
		if (isNullAtArrayIndex(index))
			return null;
		return values[index];
	}
	public byte getByteAtArrayIndex(int index) {
		if (isNullAtArrayIndex(index))
			throw new NullPointerException();
		return values[index];
	}

	public byte getByte(int location) {
		int idx = table.mapRowNumToIndex(location);
		if (isNullAtArrayIndex(idx))
			throw new NullPointerException();
		return values[idx];
	}

	public boolean setByte(int location, byte value) {
		int idx = table.mapRowNumToIndex(location);
		if (!setNotNullAtArrayIndex(idx) && values[idx] == value)
			return false;
		values[idx] = value;
		return true;
	}
	public byte getByte(ColumnarRow location) {
		int idx = location.getArrayIndex();
		if (isNullAtArrayIndex(idx))
			throw new NullPointerException();
		return values[idx];
	}

	public boolean setByte(ColumnarRow location, byte value) {
		int idx = location.getArrayIndex();
		if (!setNotNullAtArrayIndex(idx) && values[idx] == value)
			return false;
		values[idx] = value;
		return true;
	}
	@Override
	protected void setValues(Object valuesArray, long[] nulls) {
		byte[] values = (byte[]) valuesArray;
		super.setValueNullsMask(nulls);
		this.values = values;
	}
	@Override
	public byte[] getValues() {
		return values;
	}
	@Override
	public byte getBasicType() {
		return BasicTypes.BYTE;
	}
	@Override
	public StringBuilder toString(ColumnarRow row, StringBuilder sink) {
		if (isNull(row))
			return sink.append("null");
		else
			return sink.append(getByte(row));

	}
	@Override
	public void copyValue(ColumnarRow source, ColumnarRow target) {
		if (isNull(source))
			setNull(target);
		else
			setByte(target, getByte(source));
	}

	@Override
	public byte[] getValuesCloned() {
		return getValues().clone();
	}

	@Override
	public int getPrimitiveMemorySize() {
		return 1;
	}

	@Override
	public long getLong(int loc) {
		return getByte(loc);
	}

	@Override
	public double getDouble(int loc) {
		return getByte(loc);
	}
}
