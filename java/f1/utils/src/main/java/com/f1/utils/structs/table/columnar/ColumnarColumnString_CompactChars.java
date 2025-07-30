package com.f1.utils.structs.table.columnar;

import com.f1.base.BasicTypes;
import com.f1.utils.OH;
import com.f1.utils.StringArrayList_Chars;
import com.f1.utils.ToDoException;

public class ColumnarColumnString_CompactChars extends ColumnarColumn<String> {

	private StringArrayList_Chars strings = new StringArrayList_Chars();

	public ColumnarColumnString_CompactChars(ColumnsTable table, int location, String id, int capacity, boolean allowNull) {
		super(table, location, String.class, id, allowNull);
		while (strings.size() < capacity)
			strings.add(null);
	}

	@Override
	protected void ensureCapacity(int size) {
		while (strings.size() < size)
			strings.add(null);
	}
	@Override
	protected String getValueAtArrayIndex(int index) {
		return strings.get(index);
	}

	@Override
	protected void clearData() {
		int n = strings.size();
		strings.clear();
		for (int i = 0; i < n; i++)
			strings.add(null);
	}

	@Override
	protected String setValueAtArrayIndex(int index, String value) {
		if (noNull && value == null)
			throw new NullPointerException();
		Object r = strings.get(index);
		strings.set(index, value);
		return (String) r;
	}

	@Override
	protected boolean setNullAtArrayIndex(int index) {
		if (strings.isNull(index))
			return false;
		strings.set(index, null);
		return true;
	}

	@Override
	protected boolean isNullAtArrayIndex(int index) {
		return strings.isNull(index);
	}

	@Override
	protected void setValues(Object valuesArray, long[] nulls) {
		OH.assertNull(nulls);
		throw new ToDoException();
	}

	@Override
	public long[] getValueNullsMasks() {
		return null;
	}

	public Object[] getValues() {
		throw new ToDoException();
	}
	public Object[] getValuesCloned() {
		return this.getValues();
	}

	public String getObject(int location) {
		int idx = table.mapRowNumToIndex(location);
		return strings.get(idx);
	}

	public boolean setObject(int location, String value) {
		if (noNull && value == null)
			throw new NullPointerException();
		int idx = table.mapRowNumToIndex(location);
		if (strings.isEqual(idx, value))
			return false;
		strings.set(idx, value);
		return true;
	}
	public String getObject(ColumnarRow location) {
		int idx = location.getArrayIndex();
		return (String) strings.get(idx);
	}

	public boolean setObject(ColumnarRow location, String value) {
		if (noNull && value == null)
			throw new NullPointerException();
		int idx = location.getArrayIndex();
		if (strings.isEqual(idx, value))
			return false;
		strings.set(idx, value);
		return true;
	}

	@Override
	public byte getBasicType() {
		return BasicTypes.STRING;
	}
	@Override
	public StringBuilder toString(ColumnarRow row, StringBuilder sink) {
		return sink.append(getObject(row));
	}

	@Override
	public long getMemorySize() {
		return this.strings.getDataSize() * 2;
	}

}
