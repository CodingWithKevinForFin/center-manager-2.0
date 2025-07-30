package com.f1.utils.structs.table.columnar;

import java.util.Arrays;

import com.f1.utils.AH;

public class ColumnarColumnObject_Cached<T> extends ColumnarColumn<T> {

	private ColumnCache<T> cache;
	protected long data[];
	private boolean isRestoring;

	public ColumnarColumnObject_Cached(ColumnsTable table, int location, Class<T> type, String id, int capacity, boolean allowNull, ColumnCache<T> c) {
		super(table, location, type, id, allowNull);
		this.cache = c;
		this.isRestoring = false;
		data = new long[capacity];
		AH.fill(data, ColumnCache.POINTER_NULL);
	}

	@Override
	public void onRemoved() {
		synchronized (cache) {
			this.cache.close(true);
		}
	}

	public long getInnerLong(ColumnarRow location) {
		return data[location.getArrayIndex()];
	}
	public boolean setInnerLong(ColumnarRow location, long value) {
		int arrayIndex = location.getArrayIndex();
		if (data[arrayIndex] == value)
			return false;
		data[arrayIndex] = value;
		return true;
	}

	public boolean defrag(boolean doDefrag) {
		int size = this.table.getSize();
		long vals[] = new long[size];
		for (int i = 0; i < size; i++)
			vals[i] = data[this.table.mapRowNumToIndex(i)];
		long vals2[];
		synchronized (cache) {
			if (doDefrag)
				vals2 = this.cache.openAndDefrag(vals);
			else
				vals2 = this.cache.open(vals);
			if (vals2 == null)
				return false;
		}

		for (int i = 0; i < size; i++) {
			if (vals2[i] == ColumnCache.POINTER_NULL && !allowNull)
				data[this.table.mapRowNumToIndex(i)] = ColumnCache.POINTER_EMPTY;
			else if (vals[i] != vals2[i])
				data[this.table.mapRowNumToIndex(i)] = vals2[i];
		}
		return true;
	}

	@Override
	protected void clearData() {
		AH.fill(this.data, ColumnCache.POINTER_NULL);
	}

	@Override
	protected boolean setNullAtArrayIndex(int index) {
		if (this.cache.isClosed()) {
			this.data[index] = ColumnCache.POINTER_NULL;
			return false;
		}
		if (!allowNull)
			throw new NullPointerException();
		long oldValue = this.data[index];
		if (oldValue == ColumnCache.POINTER_NULL)
			return false;
		if (!isRestoring) {
			synchronized (cache) {
				cache.remove(oldValue);
			}
		}
		this.data[index] = ColumnCache.POINTER_NULL;
		return true;
	}

	@Override
	protected boolean isNullAtArrayIndex(int index) {
		return this.data[index] == ColumnCache.POINTER_NULL;
	}

	@Override
	protected T getValueAtArrayIndex(int index) {
		if (isRestoring)
			return null;
		long oldValue = this.data[index];
		if (oldValue == ColumnCache.POINTER_NULL)
			return null;
		synchronized (cache) {
			return this.cache.get(oldValue);
		}
	}
	@Override
	protected T setValueAtArrayIndex(int index, T value) {
		if (!allowNull && value == null)
			throw new NullPointerException();
		if (isRestoring) {
			if (value == getEmptyValue()) {
				this.data[index] = ColumnCache.POINTER_EMPTY;
				return null;
			}
			throw new IllegalStateException("Can not set value while restoring: " + value);
		}
		if (this.data[index] == ColumnCache.POINTER_NULL) {
			if (value != null) { //old == null, new != null;
				long val;
				synchronized (cache) {
					val = this.cache.add(value);
				}
				this.data[index] = val;
			}
			return null;
		} else {
			long oldIdx = this.data[index];
			if (value == null) { //old != null, new == null;
				this.data[index] = ColumnCache.POINTER_NULL;
				synchronized (cache) {
					T r = cache.getAndRemove(oldIdx);
					return r;
				}
			} else {//old !=null , new !=null
				T r;
				long idx;
				synchronized (cache) {
					r = this.cache.get(oldIdx);
					idx = this.cache.set(oldIdx, value);
				}
				if (idx != oldIdx)
					this.data[index] = idx;
				return r;
			}
		}
	}
	@Override
	protected void ensureCapacity(int size) {
		if (size > this.data.length) {
			int oldSize = data.length;
			data = Arrays.copyOf(data, size);
			AH.fill(data, oldSize, size, ColumnCache.POINTER_NULL);
		}
	}

	@Override
	protected void setValues(Object valuesArray, long[] nullsMasks) {
		throw new UnsupportedOperationException();

	}

	@Override
	public Object getValues() {
		throw new UnsupportedOperationException();
	}
	@Override
	public Object getValuesCloned() {
		throw new UnsupportedOperationException();
	}

	@Override
	public long[] getValueNullsMasks() {
		throw new UnsupportedOperationException();
	}

	@Override
	public StringBuilder toString(ColumnarRow row, StringBuilder sink) {
		throw new UnsupportedOperationException();
	}

	public void onRestoreStarting() {
		this.isRestoring = true;
	}

	public boolean onRestoreComplete(boolean doDefrag) {
		boolean r = defrag(doDefrag);
		this.isRestoring = false;
		return r;
	}

	public T getEmptyValue() {
		return this.cache.getEmptyValue();
	}

	@Override
	public long getMemorySize() {
		return this.table.getSize() * 8 + this.cache.getMemorySize();
	}

	public ColumnCache<T> getCache() {
		return this.cache;
	}

}
