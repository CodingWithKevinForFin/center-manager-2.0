package com.f1.utils.structs.table.columnar;

import java.util.Arrays;

import com.f1.base.BasicTypes;

public class ColumnarColumnChar extends ColumnarColumnPrimitive<Character> {

	private char[] values;

	public ColumnarColumnChar(ColumnsTable table, int location, String id, int capacity, boolean allowNulls) {
		super(table, location, Character.class, id, capacity, allowNulls);
		this.values = new char[capacity];
	}

	@Override
	protected void ensureCapacity(int size) {
		if (values.length < size)
			values = Arrays.copyOf(values, size);
		super.ensureCapacity(size);
	}

	@Override
	protected Character setValueAtArrayIndex(int index, Character value) {
		Character r = getValueAtArrayIndex(index);
		if (value == null)
			setNullAtArrayIndex(index);
		else {
			values[index] = value;
			setNotNullAtArrayIndex(index);
		}
		return r;
	}
	public void setCharAtArrayIndex(int index, char value) {
		values[index] = value;
		setNotNullAtArrayIndex(index);
	}
	@Override
	protected Character getValueAtArrayIndex(int index) {
		if (isNullAtArrayIndex(index))
			return null;
		return values[index];
	}

	public char getCharacter(int location) {
		int idx = table.mapRowNumToIndex(location);
		if (isNullAtArrayIndex(idx))
			throw new NullPointerException();
		return values[idx];
	}

	public boolean setCharacter(int location, char value) {
		int idx = table.mapRowNumToIndex(location);
		if (!setNotNullAtArrayIndex(idx) && values[idx] == value)
			return false;
		values[idx] = value;
		return true;
	}
	public char getCharacter(ColumnarRow location) {
		int idx = location.getArrayIndex();
		if (isNullAtArrayIndex(idx))
			throw new NullPointerException();
		return values[idx];
	}

	public boolean setCharacter(ColumnarRow location, char value) {
		int idx = location.getArrayIndex();
		if (!setNotNullAtArrayIndex(idx) && values[idx] == value)
			return false;
		values[idx] = value;
		return true;
	}
	@Override
	protected void setValues(Object valuesArray, long[] nulls) {
		char[] values = (char[]) valuesArray;
		this.values = values;
		super.setValueNullsMask(nulls);
	}
	@Override
	public char[] getValues() {
		return values;
	}
	@Override
	public byte getBasicType() {
		return BasicTypes.CHAR;
	}
	@Override
	public StringBuilder toString(ColumnarRow row, StringBuilder sink) {
		if (isNull(row))
			return sink.append("null");
		else
			return sink.append(getCharacter(row));

	}
	@Override
	public void copyValue(ColumnarRow source, ColumnarRow target) {
		if (isNull(source))
			setNull(target);
		else
			setCharacter(target, getCharacter(source));
	}

	@Override
	public char[] getValuesCloned() {
		return getValues().clone();
	}

	@Override
	public int getPrimitiveMemorySize() {
		return 2;
	}

	@Override
	public long getLong(int loc) {
		return getCharacter(loc);
	}
	@Override
	public double getDouble(int loc) {
		return getCharacter(loc);
	}
}
