package com.f1.utils.structs.table.columnar;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

import com.f1.base.Bytes;
import com.f1.base.Complex;
import com.f1.base.DateMillis;
import com.f1.base.DateNanos;
import com.f1.base.UUID;
import com.f1.utils.BasicTypeHelper;
import com.f1.utils.EH;
import com.f1.utils.OH;

public class ColumnarColumnObject<T> extends ColumnarColumn<T> {

	private Object[] values = OH.EMPTY_OBJECT_ARRAY;
	private final byte basicType;

	public ColumnarColumnObject(ColumnsTable table, int location, Class type, String id, int capacity, boolean allowNull) {
		super(table, location, type, id, allowNull);
		values = (Object[]) Array.newInstance(type, capacity);
		basicType = BasicTypeHelper.toType(type);
	}

	@Override
	protected void ensureCapacity(int size) {
		if (values == null || values.length < size)
			values = Arrays.copyOf(values == null ? OH.EMPTY_OBJECT_ARRAY : values, size);
	}
	@Override
	protected T getValueAtArrayIndex(int index) {
		return (T) values[index];
	}

	@Override
	protected void clearData() {
		for (int i = 0; i < values.length; i++)
			values[i] = null;
	}

	@Override
	protected T setValueAtArrayIndex(int index, T value) {
		if (noNull && value == null)
			throw new NullPointerException();
		Object r = values[index];
		values[index] = value;
		return (T) r;
	}

	@Override
	protected boolean setNullAtArrayIndex(int index) {
		if (values[index] == null)
			return false;
		values[index] = null;
		return true;
	}

	@Override
	protected boolean isNullAtArrayIndex(int index) {
		return values[index] == null;
	}

	@Override
	protected void setValues(Object valuesArray, long[] nulls) {
		Object[] values = (Object[]) valuesArray;

		OH.assertNull(nulls);
		OH.assertEq(values.getClass().getComponentType(), this.getType());
		this.values = values;
	}

	@Override
	public long[] getValueNullsMasks() {
		return null;
	}

	public Object[] getValues() {
		return values;
	}

	public T getObject(int location) {
		int idx = table.mapRowNumToIndex(location);
		return (T) values[idx];
	}

	public boolean setObject(int location, T value) {
		if (noNull && value == null)
			throw new NullPointerException();
		int idx = table.mapRowNumToIndex(location);
		if (OH.eq(values[idx], value))
			return false;
		values[idx] = value;
		return true;
	}
	public T getObject(ColumnarRow location) {
		int idx = location.getArrayIndex();
		return (T) values[idx];
	}

	public boolean setObject(ColumnarRow location, T value) {
		if (noNull && value == null)
			throw new NullPointerException();
		int idx = location.getArrayIndex();
		if (OH.eq(values[idx], value))
			return false;
		values[idx] = value;
		return true;
	}

	@Override
	public byte getBasicType() {
		return basicType;
	}
	@Override
	public StringBuilder toString(ColumnarRow row, StringBuilder sink) {
		return sink.append(getObject(row));

	}

	@Override
	public Object getValuesCloned() {
		return getValues().clone();
	}
	@Override
	public long getMemorySize() {
		long n = this.getTable().getSize();
		long r = n * EH.ADDRESS_SIZE;
		for (int i = 0; i < n; i++) {
			Object o = super.getValue(i);
			if (o != null)
				r += EH.ESTIMATED_GC_OVERHEAD + getMemorySize(o, 0);
		}
		return r;
	}

	//TODO: move to it's own helper
	public static long getMemorySize(Object o, long dflt) {
		if (o instanceof String)
			return EH.ADDRESS_SIZE + EH.ESTIMATED_GC_OVERHEAD + ((String) o).length() * 2;
		else if (o instanceof UUID)
			return 16;
		else if (o instanceof Bytes)
			return EH.ADDRESS_SIZE + EH.ESTIMATED_GC_OVERHEAD + ((Bytes) o).getBytes().length;
		else if (o instanceof DateMillis || o instanceof DateNanos)
			return 8;
		else if (o instanceof Complex)
			return 16;
		else if (o instanceof BigInteger)
			return EH.ADDRESS_SIZE + EH.ESTIMATED_GC_OVERHEAD + 24 + ((BigInteger) o).bitLength() / 8;
		else if (o instanceof BigDecimal) {
			BigDecimal bd = (BigDecimal) o;
			return EH.ADDRESS_SIZE + EH.ESTIMATED_GC_OVERHEAD + 44 + bd.toBigInteger().bitLength() / 8;
		} else
			return dflt;

	}

}
