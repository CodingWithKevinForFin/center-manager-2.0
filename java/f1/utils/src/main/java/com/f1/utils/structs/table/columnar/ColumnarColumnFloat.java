package com.f1.utils.structs.table.columnar;

import java.util.Arrays;

import com.f1.base.BasicTypes;
import com.f1.utils.MH;

public class ColumnarColumnFloat extends ColumnarColumnPrimitive<Float> {

	private float[] values;

	public ColumnarColumnFloat(ColumnsTable table, int location, String id, int capacity, boolean allowNulls) {
		super(table, location, Float.class, id, capacity, allowNulls);
		this.values = new float[capacity];
	}

	@Override
	protected void ensureCapacity(int size) {
		if (values.length < size)
			values = Arrays.copyOf(values, size);
		super.ensureCapacity(size);
	}

	@Override
	protected Float setValueAtArrayIndex(int index, Float value) {
		Float r = getValueAtArrayIndex(index);
		if (value == null)
			setNullAtArrayIndex(index);
		else {
			values[index] = value;
			setNotNullAtArrayIndex(index);
		}
		return r;
	}

	public void setFloatAtArrayIndex(int index, float value) {
		values[index] = value;
		setNotNullAtArrayIndex(index);
	}

	@Override
	protected Float getValueAtArrayIndex(int index) {
		if (isNullAtArrayIndex(index))
			return null;
		return values[index];
	}

	public float getFloat(int location) {
		int idx = table.mapRowNumToIndex(location);
		if (isNullAtArrayIndex(idx))
			throw new NullPointerException();
		return values[idx];
	}

	public boolean setFloat(int location, float value) {
		int idx = table.mapRowNumToIndex(location);
		if (!setNotNullAtArrayIndex(idx) && MH.eq(values[idx], value))
			return false;
		values[idx] = value;
		return true;
	}
	public float getFloat(ColumnarRow location) {
		int idx = location.getArrayIndex();
		if (isNullAtArrayIndex(idx))
			throw new NullPointerException();
		return values[idx];
	}

	public boolean setFloat(ColumnarRow location, float value) {
		int idx = location.getArrayIndex();
		if (!setNotNullAtArrayIndex(idx) && MH.eq(values[idx], value))
			return false;
		values[idx] = value;
		return true;
	}
	@Override
	protected void setValues(Object valuesArray, long[] nulls) {
		float[] values = (float[]) valuesArray;
		super.setValueNullsMask(nulls);
		this.values = values;
	}
	@Override
	public float[] getValues() {
		return values;
	}

	@Override
	public byte getBasicType() {
		return BasicTypes.FLOAT;
	}
	@Override
	public StringBuilder toString(ColumnarRow row, StringBuilder sink) {
		if (isNull(row))
			return sink.append("null");
		else
			return sink.append(getFloat(row));

	}
	@Override
	public void copyValue(ColumnarRow source, ColumnarRow target) {
		if (isNull(source))
			setNull(target);
		else
			setFloat(target, getFloat(source));
	}

	@Override
	public float[] getValuesCloned() {
		return getValues().clone();
	}

	@Override
	public int getPrimitiveMemorySize() {
		return 4;
	}
	@Override
	public long getLong(int loc) {
		return (long) getFloat(loc);
	}
	@Override
	public double getDouble(int loc) {
		return getFloat(loc);
	}

	@Override
	public boolean isFloat() {
		return true;
	}
}
