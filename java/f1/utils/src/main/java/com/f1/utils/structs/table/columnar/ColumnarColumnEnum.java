package com.f1.utils.structs.table.columnar;

import com.f1.base.BasicTypes;

public class ColumnarColumnEnum extends ColumnarColumn<String> {

	private ColumnarColumnEnumMapper mapper;
	private ColumnarColumnInt inner;

	public ColumnarColumnEnum(ColumnarColumnEnumMapper mapper, ColumnsTable table, int location, String id, int capacity, boolean allowNulls) {
		super(table, location, String.class, id, allowNulls);
		this.mapper = mapper;
		this.inner = new ColumnarColumnInt(table, location, id, capacity, allowNulls);
	}

	@Override
	protected void clearData() {
		inner.clearData();
	}

	@Override
	protected boolean setNullAtArrayIndex(int index) {
		return inner.setNullAtArrayIndex(index);
	}

	@Override
	protected boolean isNullAtArrayIndex(int index) {
		return inner.isNullAtArrayIndex(index);
	}

	@Override
	protected String getValueAtArrayIndex(int index) {
		if (inner.isNullAtArrayIndex(index))
			return null;
		return mapper.getEnumString(inner.getIntAtArrayIndex(index));
	}
	@Override
	protected void ensureCapacity(int size) {
		inner.ensureCapacity(size);
	}

	@Override
	public byte getBasicType() {
		return BasicTypes.STRING;
	}

	@Override
	protected String setValueAtArrayIndex(int index, String value) {
		String r = getValueAtArrayIndex(index);
		if (value == null)
			inner.setNullAtArrayIndex(index);
		else
			inner.setIntAtArrayIndex(index, this.mapper.getEnumId(value));
		return r;
	}
	public boolean setValue(ColumnarRow row, String value) {
		if (value == null) {
			if (noNull)
				throw new NullPointerException();
			if (inner.isNull(row))
				return false;
		}
		return inner.setInt(row, mapper.getEnumId(value));
	}
	@Override
	protected void setValues(Object valuesArray, long[] nulls) {
		final Object[] values = (Object[]) valuesArray;
		final int intValues[] = new int[values.length];
		for (int i = 0; i < values.length; i++) {
			Object v = values[i];
			intValues[i] = v == null ? 0 : mapper.getEnumId((String) v);
		}
		this.inner.setValues(intValues, nulls);
	}

	@Override
	public long[] getValueNullsMasks() {
		return this.inner.getValueNullsMasks();
	}

	@Override
	public Object[] getValues() {
		final int intValues[] = (int[]) inner.getValues();
		final Object[] values = new Object[intValues.length];
		for (int i = 0; i < values.length; i++)
			values[i] = mapper.getEnumString(intValues[i]);
		return values;
	}
	@Override
	public StringBuilder toString(ColumnarRow row, StringBuilder sink) {
		return sink.append(getValue(row));
	}

	@Override
	public String getValue(int location) {
		if (inner.isNull(location))
			return null;
		return mapper.getEnumString(inner.getInt(location));
	}

	public String getValue(ColumnarRow location) {
		if (inner.isNull(location))
			return null;
		return mapper.getEnumString(inner.getInt(location));
	}

	public ColumnarColumnInt getInner() {
		return this.inner;
	}

	@Override
	public Object[] getValuesCloned() {
		return getValues();//already cloned
	}

	@Override
	public long getMemorySize() {
		return inner.getMemorySize();
	}

}
