package com.f1.utils.structs.table.columnar;

import java.util.Arrays;

import com.f1.base.BasicTypes;
import com.f1.utils.MH;

public class ColumnarColumnDouble extends ColumnarColumnPrimitive<Double> {

	private double[] values;

	public ColumnarColumnDouble(ColumnsTable table, int location, String id, int capacity, boolean allowNull) {
		super(table, location, Double.class, id, capacity, allowNull);
		this.values = new double[capacity];
	}

	@Override
	protected void ensureCapacity(int size) {
		if (values.length < size)
			values = Arrays.copyOf(values, size);
		super.ensureCapacity(size);
	}

	@Override
	protected Double setValueAtArrayIndex(int index, Double value) {
		Double r = getValueAtArrayIndex(index);
		if (value == null)
			setNullAtArrayIndex(index);
		else {
			values[index] = value;
			setNotNullAtArrayIndex(index);
		}
		return r;
	}

	public void setDoubleAtArrayIndex(int index, double value) {
		values[index] = value;
		setNotNullAtArrayIndex(index);
	}

	@Override
	protected Double getValueAtArrayIndex(int index) {
		if (isNullAtArrayIndex(index))
			return null;
		return values[index];
	}

	@Override
	public double getDouble(int location) {
		int idx = table.mapRowNumToIndex(location);
		if (isNullAtArrayIndex(idx))
			throw new NullPointerException();
		return values[idx];
	}

	public boolean setDouble(int location, double value) {
		int idx = table.mapRowNumToIndex(location);
		if (!setNotNullAtArrayIndex(idx) && MH.eq(values[idx], value))
			return false;
		values[idx] = value;
		return true;
	}
	public double getDouble(ColumnarRow location) {
		int idx = location.getArrayIndex();
		if (isNullAtArrayIndex(idx))
			throw new NullPointerException();
		return values[idx];
	}

	public boolean setDouble(ColumnarRow location, double value) {
		int idx = location.getArrayIndex();
		if (!setNotNullAtArrayIndex(idx) && MH.eq(values[idx], value))
			return false;
		values[idx] = value;
		return true;
	}
	@Override
	protected void setValues(Object valuesArray, long[] nulls) {
		double[] values = (double[]) valuesArray;
		super.setValueNullsMask(nulls);
		this.values = values;
	}
	@Override
	public double[] getValues() {
		return values;
	}

	@Override
	public byte getBasicType() {
		return BasicTypes.DOUBLE;
	}
	@Override
	public StringBuilder toString(ColumnarRow row, StringBuilder sink) {
		if (isNull(row))
			return sink.append("null");
		else
			return sink.append(getDouble(row));

	}
	@Override
	public void copyValue(ColumnarRow source, ColumnarRow target) {
		if (isNull(source))
			setNull(target);
		else
			setDouble(target, getDouble(source));
	}

	@Override
	public double[] getValuesCloned() {
		return getValues().clone();
	}

	@Override
	public int getPrimitiveMemorySize() {
		return 8;
	}
	@Override
	public long getLong(int loc) {
		return (long) getDouble(loc);
	}

	@Override
	public boolean isFloat() {
		return true;
	}

}
