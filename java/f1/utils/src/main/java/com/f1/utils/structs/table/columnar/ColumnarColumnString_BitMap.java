package com.f1.utils.structs.table.columnar;

import java.util.Arrays;
import java.util.logging.Logger;

import com.f1.base.BasicTypes;
import com.f1.utils.AH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.concurrent.HasherMap.Entry;
import com.f1.utils.impl.CharSequenceHasher;

public class ColumnarColumnString_BitMap extends ColumnarColumn<String> {
	private static final Logger log = LH.get();

	private static final byte ONE = 1;
	private static final byte TWO = 2;
	private static final byte FOUR = 4;

	private ColumnarColumnPrimitive<?> inner;
	private String[] bytes2values = new String[256];
	private HasherMap<String, Integer> values2Bytes = new HasherMap<String, Integer>(CharSequenceHasher.INSTANCE, 256);

	private int bytes = ONE;
	private int capacity;

	public ColumnarColumnString_BitMap(ColumnsTable table, int location, String id, int capacity, boolean allowNulls) {
		super(table, location, String.class, id, allowNulls);
		this.inner = new ColumnarColumnByte(table, location, id, capacity, allowNulls);
		bytes = ONE;
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
		switch (bytes) {
			case ONE:
				return getEnumString(((ColumnarColumnByte) inner).getByteAtArrayIndex(index) & 0xff);
			case TWO:
				return getEnumString(((ColumnarColumnShort) inner).getShortAtArrayIndex(index) & 0xffff);
			case FOUR:
				return getEnumString(((ColumnarColumnInt) inner).getIntAtArrayIndex(index));
			default:
				throw new RuntimeException();
		}
	}

	@Override
	protected void ensureCapacity(int size) {
		inner.ensureCapacity(size);
		this.capacity = size;
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
		else {
			int enumId = getEnumId(value);
			switch (bytes) {
				case ONE:
					((ColumnarColumnByte) inner).setByteAtArrayIndex(index, (byte) enumId);
					break;
				case TWO:
					((ColumnarColumnShort) inner).setShortAtArrayIndex(index, (short) enumId);
					break;
				case FOUR:
					((ColumnarColumnInt) inner).setIntAtArrayIndex(index, (int) enumId);
					break;
				default:
					throw new RuntimeException();
			}
		}
		return r;
	}
	public boolean setValue(ColumnarRow row, String value) {
		if (value == null) {
			if (noNull)
				throw new NullPointerException();
			if (inner.isNull(row))
				return false;
		}
		int enumId = getEnumId(value);
		switch (bytes) {
			case ONE:
				return ((ColumnarColumnByte) inner).setByte(row, (byte) enumId);
			case TWO:
				return ((ColumnarColumnShort) inner).setShort(row, (short) enumId);
			case FOUR:
				return ((ColumnarColumnInt) inner).setInt(row, (int) enumId);
			default:
				throw new RuntimeException();
		}
	}

	@Override
	protected void setValues(Object valuesArray, long[] nulls) {
		final Object[] values = (Object[]) valuesArray;
		final int intValues[] = new int[values.length];
		for (int i = 0; i < values.length; i++) {
			Object v = values[i];
			intValues[i] = v == null ? 0 : getEnumId((String) v);
		}
		switch (bytes) {
			case ONE: {
				this.inner.setValues(AH.castToBytes(intValues), nulls);
				break;
			}
			case TWO: {
				this.inner.setValues(AH.castToShorts(intValues), nulls);
				break;
			}
			case FOUR: {
				this.inner.setValues(intValues, nulls);
				break;
			}
			default:
				throw new RuntimeException();
		}
	}

	@Override
	public long[] getValueNullsMasks() {
		return this.inner.getValueNullsMasks();
	}

	@Override
	public Object[] getValues() {
		switch (bytes) {
			case ONE: {
				final byte byteValues[] = (byte[]) inner.getValues();
				final Object[] values = new Object[byteValues.length];
				for (int i = 0; i < values.length; i++)
					values[i] = getEnumString(byteValues[i] & 0xff);
				return values;
			}
			case TWO: {
				final short shortValues[] = (short[]) inner.getValues();
				final Object[] values = new Object[shortValues.length];
				for (int i = 0; i < values.length; i++)
					values[i] = getEnumString(shortValues[i] & 0xffff);
				return values;
			}
			case FOUR: {
				final int intValues[] = (int[]) inner.getValues();
				final Object[] values = new Object[intValues.length];
				for (int i = 0; i < values.length; i++)
					values[i] = getEnumString(intValues[i]);
				return values;
			}
			default:
				throw new RuntimeException();
		}
	}
	@Override
	public StringBuilder toString(ColumnarRow row, StringBuilder sink) {
		return sink.append(getValue(row));
	}

	@Override
	public String getValue(int location) {
		if (inner.isNull(location))
			return null;
		switch (bytes) {
			case ONE:
				return getEnumString(((ColumnarColumnByte) inner).getByte(location & 0xff));
			case TWO:
				return getEnumString(((ColumnarColumnShort) inner).getShort(location) & 0xffff);
			case FOUR:
				return getEnumString(((ColumnarColumnInt) inner).getInt(location));
			default:
				throw new RuntimeException();
		}
	}

	public String getValue(ColumnarRow location) {
		if (inner.isNull(location))
			return null;
		switch (bytes) {
			case ONE:
				return getEnumString(((ColumnarColumnByte) inner).getByte(location) & 0xff);
			case TWO:
				return getEnumString(((ColumnarColumnShort) inner).getShort(location) & 0xffff);
			case FOUR:
				return getEnumString(((ColumnarColumnInt) inner).getInt(location));
			default:
				throw new RuntimeException();
		}
	}

	private String getEnumString(int loc) {
		return this.bytes2values[loc];
	}
	private int getEnumId(String value) {
		Integer r = this.values2Bytes.get(value);
		if (r == null) {
			int nextId = values2Bytes.size();
			if (nextId == 65536) {
				LH.info(log, "Switching from 16-bit to 32-bit addressing for column: ", this.getTable().getTitle(), "::", this.getId());
				OH.assertEq(this.bytes, TWO);
				this.bytes = FOUR;
				final short shortValues[] = (short[]) inner.getValues();
				final int intValues[] = new int[shortValues.length];
				for (int i = 0; i < shortValues.length; i++) {
					intValues[i] = (shortValues[i] & 0xffff);
				}
				ColumnarColumnInt tmp = new ColumnarColumnInt(this.getTable(), this.getLocation(), this.getId(), capacity, this.allowNull);
				tmp.setValues(intValues, this.inner.getValueNullsMasks());
				this.inner = tmp;
			} else if (nextId == 256) {
				LH.info(log, "Switching from 8-bit to 16-bit addressing for column: ", this.getTable().getTitle(), "::", this.getId());
				OH.assertEq(this.bytes, ONE);
				this.bytes = TWO;
				final byte byteValues[] = (byte[]) inner.getValues();
				final short shortValues[] = new short[byteValues.length];
				for (int i = 0; i < byteValues.length; i++)
					shortValues[i] = (short) (byteValues[i] & 0xff);
				ColumnarColumnShort tmp = new ColumnarColumnShort(this.getTable(), this.getLocation(), this.getId(), capacity, this.allowNull);
				tmp.setValues(shortValues, this.inner.getValueNullsMasks());
				this.inner = tmp;
			}
			values2Bytes.put(value, nextId);
			if (bytes2values.length < nextId + 1)
				bytes2values = Arrays.copyOf(bytes2values, MH.getArrayGrowth(bytes2values.length, nextId + 1));
			this.bytes2values[nextId] = value;
			return nextId;
		}
		return r;
	}

	@Override
	public Object getValuesCloned() {
		return getValues().clone();
	}

	@Override
	public long getMemorySize() {
		int r = inner.size();
		r += 8 * this.bytes2values.length;
		for (String i : this.bytes2values) {
			if (i != null)
				r += i.length() * 2 + 16;
		}
		return r;
	}

	public int getIndexSize() {
		return this.bytes;
	}

	public String getCachedString(StringBuilder tmpbuf) {
		Entry<String, Integer> entry = this.values2Bytes.getEntry(tmpbuf);
		return entry != null ? entry.getKey() : null;
	}
}
