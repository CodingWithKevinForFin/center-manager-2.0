package com.f1.utils.structs.table.columnar;

import java.util.Arrays;

import com.f1.utils.MH;
import com.f1.utils.OH;

public abstract class ColumnarColumnPrimitive<T> extends ColumnarColumn<T> {

	private static final long[] REMAININGS = new long[64];
	static {
		long l = 0;
		for (int i = 0; i < 64; i++) {
			REMAININGS[i] = l;
			l = (l << 1) | 1;
		}
	}

	private long[] nulls = OH.EMPTY_LONG_ARRAY;//0=null;1=not null

	public ColumnarColumnPrimitive(ColumnsTable table, int location, Class<T> clazz, String id, int capacity, boolean allowNull) {
		super(table, location, clazz, id, allowNull);
		if (allowNull)
			nulls = new long[(capacity + 63) >> 6];
		else
			nulls = null;
	}

	@Override
	public void clearData() {
		if (nulls != null)
			for (int i = 0; i < nulls.length; i++)
				nulls[i] = 0;
	}

	@Override
	final protected boolean isNullAtArrayIndex(int index) {
		if (noNull)
			return false;
		return isNullAtArrayIndex(nulls, index);
	}

	public static boolean isNullAtArrayIndex(long nulls[], int index) {
		int pos = index >> 6;
		return !MH.isBitSetAt(nulls[pos], index - (pos << 6));
	}
	public static void setNullAtArrayIndex(long nulls[], int index, boolean isNull) {
		int pos = index >> 6;
		nulls[pos] = MH.setBitAt(nulls[pos], index - (pos << 6), !isNull);
	}

	@Override
	final protected boolean setNullAtArrayIndex(int index) {
		int pos = index >> 6;
		long old = nulls[pos];
		long nuw = MH.setBitAt(old, index & 63, false);
		if (nuw == old)
			return false;
		nulls[pos] = nuw;
		return true;
	}

	//returns true if changed
	final protected boolean setNotNullAtArrayIndex(int index) {
		if (noNull)
			return false;
		int pos = index >> 6;
		long old = nulls[pos];
		long nuw = MH.setBitAt(old, index & 63, true);
		if (nuw == old)
			return false;
		nulls[pos] = nuw;
		return true;
	}

	@Override
	protected void ensureCapacity(int size) {
		size = (63 + size) >> 6;
		if (nulls != null && nulls.length < size)
			nulls = Arrays.copyOf(nulls, size);
	}

	protected void initNotNull(int length) {
		if (noNull)
			return;
		this.nulls = new long[(length + 7) / 8];
		for (int i = 0; i < this.nulls.length - 1; i++)
			this.nulls[i] = 0xffffffffL;
		int remaining = length & 63;
		if (remaining > 0)
			this.nulls[this.nulls.length - 1] = REMAININGS[remaining];
	}
	public long[] getValueNullsMasks() {
		return this.nulls;
	}

	public void setValueNullsMask(long[] nulls) {
		if (this.noNull != (nulls == null))
			throw new IllegalArgumentException();
		this.nulls = nulls;
	}

	@Override
	public long getMemorySize() {
		return getPrimitiveMemorySize() * this.table.getSize();
	}

	public abstract int getPrimitiveMemorySize();

	public abstract long getLong(int loc);
	public abstract double getDouble(int loc);

	public boolean isFloat() {
		return false;
	}

}
