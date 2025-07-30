/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.DataInput;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.base.BasicTypes;
import com.f1.base.DateMillis;
import com.f1.base.DateNanos;
import com.f1.utils.FastDataInput;
import com.f1.utils.FastDataOutput;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.structs.table.columnar.ColumnarColumn;
import com.f1.utils.structs.table.columnar.ColumnarTable;

public class ColumnarTableToByteArrayConverter implements ByteArrayConverter<ColumnarTable> {
	private static final Logger log = LH.get();

	@Override
	public void write(ColumnarTable table, ToByteArrayConverterSession session) throws IOException {
		if (session.handleIfAlreadyConverted(table))
			return;
		long start = System.currentTimeMillis();

		int rowsCount = table.getSize();
		ObjectToByteArrayConverter converter = session.getConverter();
		ByteArrayConverter<Class> classConverter = (ByteArrayConverter<Class>) converter.getConverter(BasicTypes.CLASS);
		FastDataOutput stream = session.getStream();
		session.getConverter().write(table.getTitle(), session);
		int colsCount = table.getColumnsCount();
		stream.writeShort(colsCount);
		stream.writeInt(rowsCount);
		if (table.isMangled()) {
			for (int i = 0; i < colsCount; i++) {
				ColumnarColumn col = table.getColumnAt(i);
				classConverter.write(col.getType(), session);
				converter.write(col.getId(), session);
				if (col.getAllowNull()) {
					stream.writeBoolean(true);
					if (col.getValueNullsMasks() != null) {
						long[] nulls = new long[(rowsCount + 7) / 8];
						for (int n = 0; n < rowsCount; n++)
							if (!col.isNull(n))
								nulls[n >> 6] |= 1L << (n & 63);
						LongArrayToByteArrayConverter.writeLongs(nulls, stream);
					} else
						LongArrayToByteArrayConverter.writeLongs(null, stream);
				} else {
					stream.writeBoolean(false);
				}
				writeValuesMangled(col.getValues(), rowsCount, session, table);
			}
		} else {
			for (int i = 0; i < colsCount; i++) {
				ColumnarColumn col = table.getColumnAt(i);
				classConverter.write(col.getType(), session);
				converter.write(col.getId(), session);
				stream.writeBoolean(col.getAllowNull());
				if (col.getAllowNull())
					LongArrayToByteArrayConverter.writeLongs(col.getValueNullsMasks(), stream);
				writeValues(col.getValues(), rowsCount, session);
			}
		}
		if (log.isLoggable(Level.FINE))
			LH.fine(log, "Converted table to bytes: ", table.getSize() + " x ", table.getColumnsCount(), " in ", (System.currentTimeMillis() - start));
	}
	@Override
	public ColumnarTable read(FromByteArrayConverterSession session) throws IOException {
		int id = session.handleIfAlreadyConverted();
		if (id < 0)
			return (ColumnarTable) session.get(id);
		long start = System.currentTimeMillis();
		ObjectToByteArrayConverter converter = session.getConverter();
		ByteArrayConverter<Class> classConverter = (ByteArrayConverter<Class>) converter.getConverter(BasicTypes.CLASS);
		DataInput stream = session.getStream();
		String title = (String) session.getConverter().read(session);
		int colsCount = stream.readShort();
		int rowsCount = stream.readInt();
		ColumnarTable r = new ColumnarTable(OH.EMPTY_CLASS_ARRAY, OH.EMPTY_STRING_ARRAY, rowsCount);
		r.setTitle(title);
		for (int i = 0; i < colsCount; i++) {
			final Class colType = classConverter.read(session);
			final String colId = (String) converter.read(session);
			final boolean allowNull = stream.readBoolean();
			long[] nullsMask;
			if (allowNull)
				nullsMask = LongArrayToByteArrayConverter.readLongs(stream);
			else
				nullsMask = null;
			final Object colVal = readValues(colType, rowsCount, session);
			r.addColumnWithValues(colType, colId, colVal, nullsMask, allowNull);
		}
		session.store(id, r);
		if (log.isLoggable(Level.FINE))
			LH.fine(log, "Converted bytes to table: ", r.getSize() + " x ", r.getColumnsCount(), " in ", (System.currentTimeMillis() - start));
		return r;
	}
	@Override
	public byte getBasicType() {
		return BasicTypes.TABLE_COLUMNAR;
	}

	@Override
	public boolean isCompatible(Class<?> o) {
		return ColumnarTable.class.isAssignableFrom(o);
	}
	private void writeValuesMangled(Object values, int len, ToByteArrayConverterSession session, ColumnarTable table) throws IOException {
		FastDataOutput o = session.getStream();
		Class clazz = values.getClass().getComponentType();
		if (clazz == Double.class || clazz == double.class) {
			double[] vals = (double[]) values;
			for (int i = 0; i < len; i++)
				o.writeDouble(vals[table.mapRowNumToIndex(i)]);
		} else if (clazz == Float.class || clazz == float.class) {
			float[] vals = (float[]) values;
			for (int i = 0; i < len; i++)
				o.writeFloat(vals[table.mapRowNumToIndex(i)]);
		} else if (clazz == Boolean.class || clazz == boolean.class) {
			boolean[] vals = (boolean[]) values;
			for (int i = 0; i < len; i++)
				o.writeBoolean(vals[table.mapRowNumToIndex(i)]);
		} else if (clazz == Character.class || clazz == char.class) {
			char[] vals = (char[]) values;
			for (int i = 0; i < len; i++)
				o.writeChar(vals[table.mapRowNumToIndex(i)]);
		} else if (clazz == Long.class || clazz == long.class) {
			long[] vals = (long[]) values;
			for (int i = 0; i < len; i++)
				o.writeLong(vals[table.mapRowNumToIndex(i)]);
		} else if (clazz == Integer.class || clazz == int.class) {
			int[] vals = (int[]) values;
			for (int i = 0; i < len; i++)
				o.writeInt(vals[table.mapRowNumToIndex(i)]);
		} else if (clazz == Short.class || clazz == short.class) {
			short[] vals = (short[]) values;
			for (int i = 0; i < len; i++)
				o.writeShort(vals[table.mapRowNumToIndex(i)]);
		} else if (clazz == Byte.class || clazz == byte.class) {
			byte[] vals = (byte[]) values;
			for (int i = 0; i < len; i++)
				o.writeByte(vals[table.mapRowNumToIndex(i)]);
		} else if (clazz == String.class) {
			String[] vals = (String[]) values;
			for (int i = 0; i < len; i++)
				StringToByteArrayConverter.writeString(vals[table.mapRowNumToIndex(i)], o);
		} else {
			Object[] vals = (Object[]) values;
			ObjectToByteArrayConverter converter = session.getConverter();
			for (int i = 0; i < len; i++) {
				converter.write(vals[table.mapRowNumToIndex(i)], session);
			}
		}

	}

	private void writeValues(Object values, int len, ToByteArrayConverterSession session) throws IOException {
		FastDataOutput o = session.getStream();
		Class clazz = values.getClass().getComponentType();
		if (clazz == Double.class || clazz == double.class) {
			double[] vals = (double[]) values;
			o.write(vals, 0, len);
		} else if (clazz == Float.class || clazz == float.class) {
			float[] vals = (float[]) values;
			o.write(vals, 0, len);
		} else if (clazz == Boolean.class || clazz == boolean.class) {
			boolean[] vals = (boolean[]) values;
			o.write(vals, 0, len);
		} else if (clazz == Character.class || clazz == char.class) {
			char[] vals = (char[]) values;
			o.write(vals, 0, len);
		} else if (clazz == Long.class || clazz == long.class) {
			long[] vals = (long[]) values;
			o.write(vals, 0, len);
		} else if (clazz == Integer.class || clazz == int.class) {
			int[] vals = (int[]) values;
			o.write(vals, 0, len);
		} else if (clazz == Short.class || clazz == short.class) {
			short[] vals = (short[]) values;
			o.write(vals, 0, len);
		} else if (clazz == Byte.class || clazz == byte.class) {
			byte[] vals = (byte[]) values;
			o.write(vals, 0, len);
		} else if (clazz == String.class) {
			String[] vals = (String[]) values;
			for (int i = 0; i < len; i++)
				StringToByteArrayConverter.writeString(vals[i], o);
		} else {
			Object[] vals = (Object[]) values;
			ObjectToByteArrayConverter converter = session.getConverter();
			for (int i = 0; i < len; i++)
				converter.write(vals[i], session);
		}

	}
	private Object readValues(Class clazz, int len, FromByteArrayConverterSession session) throws IOException {
		FastDataInput o = session.getStream();
		if (clazz == Character.class || clazz == char.class) {
			return o.readFully(new char[len], 0, len);
		} else if (clazz == Boolean.class || clazz == boolean.class) {
			return o.readFully(new boolean[len], 0, len);
		} else if (clazz == Double.class || clazz == double.class) {
			return o.readFully(new double[len], 0, len);
		} else if (clazz == Float.class || clazz == float.class) {
			return o.readFully(new float[len], 0, len);
		} else if (clazz == Long.class || clazz == long.class || clazz == DateMillis.class || clazz == DateNanos.class) {
			return o.readFully(new long[len], 0, len);
		} else if (clazz == Integer.class || clazz == int.class) {
			return o.readFully(new int[len], 0, len);
		} else if (clazz == Short.class || clazz == short.class) {
			return o.readFully(new short[len], 0, len);
		} else if (clazz == Byte.class || clazz == byte.class) {
			byte[] vals = new byte[len];
			o.readFully(vals, 0, len);
			return vals;
		} else if (clazz == String.class) {
			String[] vals = new String[len];
			for (int i = 0; i < len; i++)
				vals[i] = StringToByteArrayConverter.readString(o);
			return vals;
		} else {
			Object[] vals = (Object[]) Array.newInstance(clazz, len);
			for (int i = 0; i < len; i++)
				vals[i] = session.getConverter().read(session);
			return vals;
		}
	}

}
