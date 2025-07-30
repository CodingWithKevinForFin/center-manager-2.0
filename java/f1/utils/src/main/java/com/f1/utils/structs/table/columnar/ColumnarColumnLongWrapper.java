package com.f1.utils.structs.table.columnar;

public abstract class ColumnarColumnLongWrapper<T> extends ColumnarColumn<T> {

	protected final ColumnarColumnLong inner;

	public ColumnarColumnLongWrapper(ColumnsTable table, int location, Class<T> clazz, String id, int capacity, boolean allowNulls) {
		super(table, location, clazz, id, allowNulls);
		this.inner = new ColumnarColumnLong(table, location, id, capacity, allowNulls);
	}
	@Override
	protected void clearData() {
		this.inner.clearData();
	}

	@Override
	protected boolean setNullAtArrayIndex(int index) {
		return this.inner.setNullAtArrayIndex(index);
	}

	@Override
	protected boolean isNullAtArrayIndex(int index) {
		return this.inner.isNullAtArrayIndex(index);
	}

	@Override
	protected T getValueAtArrayIndex(int index) {
		if (this.inner.isNullAtArrayIndex(index))
			return null;
		return wrap(this.inner.getLongAtArrayIndex(index));
	}
	@Override
	protected T setValueAtArrayIndex(int index, T value) {
		T r = getValueAtArrayIndex(index);
		if (value == null)
			this.inner.setNullAtArrayIndex(index);
		else
			this.inner.setLongAtArrayIndex(index, unwrap(value));
		return r;
	}

	@Override
	protected void ensureCapacity(int size) {
		this.inner.ensureCapacity(size);
	}

	@Override
	protected void setValues(Object valuesArray, long[] nullsMasks) {
		this.inner.setValues(valuesArray, nullsMasks);

	}

	@Override
	public Object getValues() {
		return this.inner.getValues();
	}
	@Override
	public Object getValuesCloned() {
		return this.inner.getValuesCloned();
	}

	@Override
	public long[] getValueNullsMasks() {
		return this.inner.getValueNullsMasks();
	}

	@Override
	public StringBuilder toString(ColumnarRow row, StringBuilder sink) {
		return inner.toString(row, sink);
	}

	abstract protected T wrap(long v);
	abstract protected long unwrap(T v);

	public long getInnerValue(ColumnarRow location) {
		return inner.getValue(location);
	}

	public boolean setInnerValue(ColumnarRow location, long parseLong) {
		return inner.setValue(location, parseLong);
	}

	@Override
	public long getMemorySize() {
		return this.inner.getMemorySize();
	}
}
