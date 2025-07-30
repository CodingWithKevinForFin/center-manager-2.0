package com.f1.ami.center;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiEntityByteUtils;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiDataEntity;
import com.f1.ami.amicommon.msg.AmiDatasourceColumn;
import com.f1.ami.amicommon.msg.AmiRelayObjectMessage;
import com.f1.ami.center.table.AmiColumnImpl;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.table.AmiRowImpl;
import com.f1.ami.center.table.AmiTable;
import com.f1.ami.center.table.AmiTableDef;
import com.f1.ami.center.table.AmiTableImpl;
import com.f1.ami.center.table.AmiTableSetterGetter;
import com.f1.ami.center.triggers.AmiTimedRunnable;
import com.f1.base.BasicTypes;
import com.f1.base.Bytes;
import com.f1.base.Complex;
import com.f1.base.DateMillis;
import com.f1.base.DateNanos;
import com.f1.base.UUID;
import com.f1.utils.ByteHelper;
import com.f1.utils.FastByteArrayDataOutputStream;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_BigDecimal;
import com.f1.utils.casters.Caster_BigInteger;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Byte;
import com.f1.utils.casters.Caster_Character;
import com.f1.utils.casters.Caster_Complex;
import com.f1.utils.casters.Caster_DateMillis;
import com.f1.utils.casters.Caster_DateNanos;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.casters.Caster_Float;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_Long;
import com.f1.utils.casters.Caster_Short;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.casters.Caster_UUID;
import com.f1.utils.structs.table.columnar.ColumnarColumn;
import com.f1.utils.structs.table.columnar.ColumnarColumnBoolean;
import com.f1.utils.structs.table.columnar.ColumnarColumnByte;
import com.f1.utils.structs.table.columnar.ColumnarColumnChar;
import com.f1.utils.structs.table.columnar.ColumnarColumnDateMillis;
import com.f1.utils.structs.table.columnar.ColumnarColumnDateNanos;
import com.f1.utils.structs.table.columnar.ColumnarColumnDouble;
import com.f1.utils.structs.table.columnar.ColumnarColumnEnum;
import com.f1.utils.structs.table.columnar.ColumnarColumnFloat;
import com.f1.utils.structs.table.columnar.ColumnarColumnInt;
import com.f1.utils.structs.table.columnar.ColumnarColumnLong;
import com.f1.utils.structs.table.columnar.ColumnarColumnObject;
import com.f1.utils.structs.table.columnar.ColumnarColumnShort;
import com.f1.utils.structs.table.columnar.ColumnarColumnString_BitMap;
import com.f1.utils.structs.table.columnar.ColumnarRow;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiCenterAmiUtilsForTable {

	private static final Logger log = LH.get();

	public static <T extends ColumnarColumn<?>> AmiTableSetterGetter<T> getSetterGetter(AmiColumnImpl<T> column) {
		return getSetterGetter(column.getAmiType());
	}
	public static <T extends ColumnarColumn<?>> AmiTableSetterGetter<T> getSetterGetter(byte type) {
		switch (type) {
			case AmiTable.TYPE_BOOLEAN:
				return (AmiTableSetterGetter) GETTER_SETTER_BOOLEAN;
			case AmiTable.TYPE_STRING:
				return (AmiTableSetterGetter) GETTER_SETTER_STRING;
			case AmiTable.TYPE_DOUBLE:
				return (AmiTableSetterGetter) GETTER_SETTER_DOUBLE;
			case AmiTable.TYPE_FLOAT:
				return (AmiTableSetterGetter) GETTER_SETTER_FLOAT;
			case AmiTable.TYPE_BYTE:
				return (AmiTableSetterGetter) GETTER_SETTER_BYTE;
			case AmiTable.TYPE_SHORT:
				return (AmiTableSetterGetter) GETTER_SETTER_SHORT;
			case AmiTable.TYPE_INT:
				return (AmiTableSetterGetter) GETTER_SETTER_INT;
			case AmiTable.TYPE_LONG:
				return (AmiTableSetterGetter) GETTER_SETTER_LONG;
			case AmiTable.TYPE_UTC:
				return (AmiTableSetterGetter) GETTER_SETTER_UTC;
			case AmiTable.TYPE_UTCN:
				return (AmiTableSetterGetter) GETTER_SETTER_UTCN;
			case AmiTable.TYPE_ENUM:
				return (AmiTableSetterGetter) GETTER_SETTER_ENUM;
			case AmiTable.TYPE_CHAR:
				return (AmiTableSetterGetter) GETTER_SETTER_CHAR;
			case AmiTable.TYPE_BINARY:
				return (AmiTableSetterGetter) GETTER_SETTER_BINARY;
			case AmiTable.TYPE_COMPLEX:
				return (AmiTableSetterGetter) GETTER_SETTER_COMPLEX;
			case AmiTable.TYPE_UUID:
				return (AmiTableSetterGetter) GETTER_SETTER_UUID;
			case AmiTable.TYPE_BIGINT:
				return (AmiTableSetterGetter) GETTER_SETTER_BIGINT;
			case AmiTable.TYPE_BIGDEC:
				return (AmiTableSetterGetter) GETTER_SETTER_BIGDEC;
			default:
				throw new RuntimeException("Unknown type: " + type);
		}
	}

	public static final AmiTableSetterGetterBoolean GETTER_SETTER_BOOLEAN = new AmiTableSetterGetterBoolean();
	public static final AmiTableSetterGetterLong GETTER_SETTER_LONG = new AmiTableSetterGetterLong();
	public static final AmiTableSetterGetterDateMillis GETTER_SETTER_UTC = new AmiTableSetterGetterDateMillis();
	public static final AmiTableSetterGetterDateNanos GETTER_SETTER_UTCN = new AmiTableSetterGetterDateNanos();
	public static final AmiTableSetterGetterDouble GETTER_SETTER_DOUBLE = new AmiTableSetterGetterDouble();
	public static final AmiTableSetterGetterFloat GETTER_SETTER_FLOAT = new AmiTableSetterGetterFloat();
	public static final AmiTableSetterGetterInt GETTER_SETTER_INT = new AmiTableSetterGetterInt();
	public static final AmiTableSetterGetterShort GETTER_SETTER_SHORT = new AmiTableSetterGetterShort();
	public static final AmiTableSetterGetterByte GETTER_SETTER_BYTE = new AmiTableSetterGetterByte();
	public static final AmiTableSetterGetterChar GETTER_SETTER_CHAR = new AmiTableSetterGetterChar();
	public static final AmiTableSetterGetterString GETTER_SETTER_STRING = new AmiTableSetterGetterString();
	public static final AmiTableSetterGetterEnum GETTER_SETTER_ENUM = new AmiTableSetterGetterEnum();
	public static final AmiTableSetterGetterBinary GETTER_SETTER_BINARY = new AmiTableSetterGetterBinary();
	public static final AmiTableSetterGetterComplex GETTER_SETTER_COMPLEX = new AmiTableSetterGetterComplex();
	public static final AmiTableSetterGetterUUID GETTER_SETTER_UUID = new AmiTableSetterGetterUUID();
	public static final AmiTableSetterGetterBigInt GETTER_SETTER_BIGINT = new AmiTableSetterGetterBigInt();
	public static final AmiTableSetterGetterBigDec GETTER_SETTER_BIGDEC = new AmiTableSetterGetterBigDec();

	public static class AmiTableSetterGetterLong implements AmiTableSetterGetter<ColumnarColumnLong> {
		@Override
		public String getString(ColumnarColumnLong column, AmiRowImpl row) {
			long value = column.getLongOr(row, AmiTable.NULL_NUMBER);
			if (value == AmiTable.NULL_NUMBER)
				return null;
			return SH.toString(value);
		}
		@Override
		public long getLong(ColumnarColumnLong column, AmiRowImpl row) {
			return column.getLongOr(row, AmiTable.NULL_NUMBER);
		}
		@Override
		public double getDouble(ColumnarColumnLong column, AmiRowImpl row) {
			long r = column.getLongOr(row, AmiTable.NULL_NUMBER);
			if (r == AmiTable.NULL_NUMBER)
				return AmiTable.NULL_DECIMAL;
			return r;
		}
		@Override
		public boolean setString(ColumnarColumnLong column, AmiRowImpl row, String value) {
			if (value == null)
				return column.setNull(row);
			else
				return setLong(column, row, SH.parseLong(value));
		}
		@Override
		public boolean setLong(ColumnarColumnLong column, AmiRowImpl row, long value) {
			if (value == AmiTable.NULL_NUMBER)
				return column.setNull(row);
			else
				return column.setLong(row, value);
		}
		@Override
		public boolean setDouble(ColumnarColumnLong column, AmiRowImpl row, double value) {
			if (value != value)
				return column.setNull(row);
			else
				return column.setLong(row, (long) value);
		}
		@Override
		public Comparable getComparable(ColumnarColumnLong column, AmiRowImpl row) {
			if (column.isNull(row))
				return null;
			return column.getLong(row);
		}
		@Override
		public boolean setComparable(ColumnarColumnLong column, AmiRowImpl row, Comparable value) {
			Long v = Caster_Long.INSTANCE.cast(value);
			if (v == null)
				return column.setNull(row);
			else
				return column.setLong(row, v);
		}
		@Override
		public boolean isPrimitive() {
			return true;
		}
		@Override
		public Object getDefaultValue() {
			return 0L;
		}
	}

	public static class AmiTableSetterGetterByte implements AmiTableSetterGetter<ColumnarColumnByte> {
		@Override
		public String getString(ColumnarColumnByte column, AmiRowImpl row) {
			if (column.isNull(row))
				return null;
			return SH.toString(column.getByte(row));
		}
		@Override
		public long getLong(ColumnarColumnByte column, AmiRowImpl row) {
			if (column.isNull(row))
				return AmiTable.NULL_NUMBER;
			return column.getByte(row);
		}
		@Override
		public double getDouble(ColumnarColumnByte column, AmiRowImpl row) {
			if (column.isNull(row))
				return AmiTable.NULL_DECIMAL;
			return column.getByte(row);
		}
		@Override
		public boolean setString(ColumnarColumnByte column, AmiRowImpl row, String value) {
			if (value == null)
				return column.setNull(row);
			else
				return setLong(column, row, SH.parseLong(value));
		}
		@Override
		public boolean setLong(ColumnarColumnByte column, AmiRowImpl row, long value) {
			if (value == AmiTable.NULL_NUMBER)
				return column.setNull(row);
			else
				return column.setByte(row, (byte) value);
		}
		@Override
		public boolean setDouble(ColumnarColumnByte column, AmiRowImpl row, double value) {
			if (value != value)
				return column.setNull(row);
			else
				return column.setByte(row, (byte) value);
		}
		@Override
		public Comparable getComparable(ColumnarColumnByte column, AmiRowImpl row) {
			if (column.isNull(row))
				return null;
			return column.getByte(row);
		}

		@Override
		public boolean setComparable(ColumnarColumnByte column, AmiRowImpl row, Comparable value) {
			Byte v = Caster_Byte.INSTANCE.cast(value);
			if (v == null)
				return column.setNull(row);
			else
				return column.setByte(row, v);
		}
		@Override
		public boolean isPrimitive() {
			return true;
		}
		@Override
		public Object getDefaultValue() {
			return (byte) 0;
		}
	}

	public static class AmiTableSetterGetterShort implements AmiTableSetterGetter<ColumnarColumnShort> {
		@Override
		public String getString(ColumnarColumnShort column, AmiRowImpl row) {
			if (column.isNull(row))
				return null;
			return SH.toString(column.getShort(row));
		}
		@Override
		public long getLong(ColumnarColumnShort column, AmiRowImpl row) {
			if (column.isNull(row))
				return AmiTable.NULL_NUMBER;
			return column.getShort(row);
		}
		@Override
		public double getDouble(ColumnarColumnShort column, AmiRowImpl row) {
			if (column.isNull(row))
				return AmiTable.NULL_DECIMAL;
			return column.getShort(row);
		}
		@Override
		public boolean setString(ColumnarColumnShort column, AmiRowImpl row, String value) {
			if (value == null)
				return column.setNull(row);
			else
				return setLong(column, row, SH.parseLong(value));
		}
		@Override
		public boolean setLong(ColumnarColumnShort column, AmiRowImpl row, long value) {
			if (value == AmiTable.NULL_NUMBER)
				return column.setNull(row);
			else
				return column.setShort(row, (short) value);
		}
		@Override
		public boolean setDouble(ColumnarColumnShort column, AmiRowImpl row, double value) {
			if (value != value)
				return column.setNull(row);
			else
				return column.setShort(row, (short) value);
		}
		@Override
		public Comparable getComparable(ColumnarColumnShort column, AmiRowImpl row) {
			if (column.isNull(row))
				return null;
			return column.getShort(row);
		}

		@Override
		public boolean setComparable(ColumnarColumnShort column, AmiRowImpl row, Comparable value) {
			Short v = Caster_Short.INSTANCE.cast(value);
			if (v == null)
				return column.setNull(row);
			else
				return column.setShort(row, v);
		}
		@Override
		public boolean isPrimitive() {
			return true;
		}
		@Override
		public Object getDefaultValue() {
			return (short) 0;
		}
	}

	public static class AmiTableSetterGetterInt implements AmiTableSetterGetter<ColumnarColumnInt> {
		@Override
		public String getString(ColumnarColumnInt column, AmiRowImpl row) {
			if (column.isNull(row))
				return null;
			return SH.toString(column.getInt(row));
		}
		@Override
		public long getLong(ColumnarColumnInt column, AmiRowImpl row) {
			if (column.isNull(row))
				return AmiTable.NULL_NUMBER;
			return column.getInt(row);
		}
		@Override
		public double getDouble(ColumnarColumnInt column, AmiRowImpl row) {
			if (column.isNull(row))
				return AmiTable.NULL_DECIMAL;
			return column.getInt(row);
		}
		@Override
		public boolean setString(ColumnarColumnInt column, AmiRowImpl row, String value) {
			if (value == null)
				return column.setNull(row);
			else
				return setLong(column, row, SH.parseLong(value));
		}
		@Override
		public boolean setLong(ColumnarColumnInt column, AmiRowImpl row, long value) {
			if (value == AmiTable.NULL_NUMBER)
				return column.setNull(row);
			else
				return column.setInt(row, (int) value);
		}
		@Override
		public boolean setDouble(ColumnarColumnInt column, AmiRowImpl row, double value) {
			if (value != value)
				return column.setNull(row);
			else
				return column.setInt(row, (int) value);
		}
		@Override
		public Comparable getComparable(ColumnarColumnInt column, AmiRowImpl row) {
			if (column.isNull(row))
				return null;
			return column.getInt(row);
		}

		@Override
		public boolean setComparable(ColumnarColumnInt column, AmiRowImpl row, Comparable value) {
			Integer v = Caster_Integer.INSTANCE.cast(value);
			if (v == null)
				return column.setNull(row);
			else
				return column.setInt(row, v);
		}
		@Override
		public boolean isPrimitive() {
			return true;
		}
		@Override
		public Object getDefaultValue() {
			return 0;
		}
	}

	public static class AmiTableSetterGetterChar implements AmiTableSetterGetter<ColumnarColumnChar> {
		@Override
		public String getString(ColumnarColumnChar column, AmiRowImpl row) {
			if (column.isNull(row))
				return null;
			return SH.toString(column.getCharacter(row));
		}
		@Override
		public long getLong(ColumnarColumnChar column, AmiRowImpl row) {
			if (column.isNull(row))
				return AmiTable.NULL_NUMBER;
			return column.getCharacter(row);
		}
		@Override
		public double getDouble(ColumnarColumnChar column, AmiRowImpl row) {
			if (column.isNull(row))
				return AmiTable.NULL_DECIMAL;
			return column.getCharacter(row);
		}
		@Override
		public boolean setString(ColumnarColumnChar column, AmiRowImpl row, String value) {
			if (value == null)
				return column.setNull(row);
			else
				return setLong(column, row, SH.parseChar(value));
		}
		@Override
		public boolean setLong(ColumnarColumnChar column, AmiRowImpl row, long value) {
			if (value == AmiTable.NULL_NUMBER)
				return column.setNull(row);
			else
				return column.setCharacter(row, (char) value);
		}
		@Override
		public boolean setDouble(ColumnarColumnChar column, AmiRowImpl row, double value) {
			if (value != value)
				return column.setNull(row);
			else
				return column.setCharacter(row, (char) value);
		}
		@Override
		public Comparable getComparable(ColumnarColumnChar column, AmiRowImpl row) {
			if (column.isNull(row))
				return null;
			return column.getCharacter(row);
		}
		@Override
		public boolean setComparable(ColumnarColumnChar column, AmiRowImpl row, Comparable value) {
			Character v = Caster_Character.INSTANCE.cast(value);
			if (v == null)
				return column.setNull(row);
			else
				return column.setCharacter(row, v);
		}
		@Override
		public boolean isPrimitive() {
			return true;
		}
		@Override
		public Object getDefaultValue() {
			return (char) 0;
		}
	}

	public static class AmiTableSetterGetterFloat implements AmiTableSetterGetter<ColumnarColumnFloat> {
		@Override
		public String getString(ColumnarColumnFloat column, AmiRowImpl row) {
			if (column.isNull(row))
				return null;
			return SH.toString(column.getFloat(row));
		}
		@Override
		public long getLong(ColumnarColumnFloat column, AmiRowImpl row) {
			if (column.isNull(row))
				return AmiTable.NULL_NUMBER;
			return (long) column.getFloat(row);
		}
		@Override
		public double getDouble(ColumnarColumnFloat column, AmiRowImpl row) {
			if (column.isNull(row))
				return AmiTable.NULL_DECIMAL;
			return column.getFloat(row);
		}
		@Override
		public boolean setString(ColumnarColumnFloat column, AmiRowImpl row, String value) {
			if (value == null)
				return column.setNull(row);
			else
				return setDouble(column, row, SH.parseDouble(value));
		}
		@Override
		public boolean setLong(ColumnarColumnFloat column, AmiRowImpl row, long value) {
			if (value == AmiTable.NULL_NUMBER)
				return column.setNull(row);
			else
				return column.setFloat(row, value);
		}
		@Override
		public boolean setDouble(ColumnarColumnFloat column, AmiRowImpl row, double value) {
			if (value != value)
				return column.setNull(row);
			else
				return column.setFloat(row, (float) value);
		}
		@Override
		public Comparable getComparable(ColumnarColumnFloat column, AmiRowImpl row) {
			if (column.isNull(row))
				return null;
			return column.getFloat(row);
		}
		@Override
		public boolean setComparable(ColumnarColumnFloat column, AmiRowImpl row, Comparable value) {
			Float v = Caster_Float.INSTANCE.cast(value);
			if (v == null)
				return column.setNull(row);
			else
				return column.setFloat(row, v);
		}
		@Override
		public boolean isPrimitive() {
			return true;
		}
		@Override
		public Object getDefaultValue() {
			return 0f;
		}
	}

	public static class AmiTableSetterGetterDouble implements AmiTableSetterGetter<ColumnarColumnDouble> {
		@Override
		public String getString(ColumnarColumnDouble column, AmiRowImpl row) {
			if (column.isNull(row))
				return null;
			return SH.toString(column.getDouble(row));
		}
		@Override
		public long getLong(ColumnarColumnDouble column, AmiRowImpl row) {
			if (column.isNull(row))
				return AmiTable.NULL_NUMBER;
			return (long) column.getDouble(row);
		}
		@Override
		public double getDouble(ColumnarColumnDouble column, AmiRowImpl row) {
			if (column.isNull(row))
				return AmiTable.NULL_DECIMAL;
			return column.getDouble(row);
		}
		@Override
		public boolean setString(ColumnarColumnDouble column, AmiRowImpl row, String value) {
			if (value == null)
				return column.setNull(row);
			else
				return setDouble(column, row, SH.parseDouble(value));
		}
		@Override
		public boolean setLong(ColumnarColumnDouble column, AmiRowImpl row, long value) {
			if (value == AmiTable.NULL_NUMBER)
				return column.setNull(row);
			else
				return column.setDouble(row, value);
		}
		@Override
		public boolean setDouble(ColumnarColumnDouble column, AmiRowImpl row, double value) {
			if (value != value)
				return column.setNull(row);
			else
				return column.setDouble(row, value);
		}
		@Override
		public Comparable getComparable(ColumnarColumnDouble column, AmiRowImpl row) {
			if (column.isNull(row))
				return null;
			return column.getDouble(row);
		}
		@Override
		public boolean setComparable(ColumnarColumnDouble column, AmiRowImpl row, Comparable value) {
			Double v = Caster_Double.INSTANCE.cast(value);
			if (v == null)
				return column.setNull(row);
			else
				return column.setDouble(row, v);
		}
		@Override
		public boolean isPrimitive() {
			return true;
		}
		@Override
		public Object getDefaultValue() {
			return 0d;
		}
	}

	public static class AmiTableSetterGetterBoolean implements AmiTableSetterGetter<ColumnarColumnBoolean> {
		@Override
		public String getString(ColumnarColumnBoolean column, AmiRowImpl row) {
			if (column.isNull(row))
				return null;
			return SH.toString(column.getBoolean(row));
		}
		@Override
		public long getLong(ColumnarColumnBoolean column, AmiRowImpl row) {
			if (column.isNull(row))
				return AmiTable.NULL_NUMBER;
			return column.getBoolean(row) ? 1L : 0L;
		}
		@Override
		public double getDouble(ColumnarColumnBoolean column, AmiRowImpl row) {
			if (column.isNull(row))
				return AmiTable.NULL_DECIMAL;
			return column.getBoolean(row) ? 1D : 0D;
		}
		@Override
		public boolean setString(ColumnarColumnBoolean column, AmiRowImpl row, String value) {
			if (value == null)
				return column.setNull(row);
			else
				return column.setBoolean(row, "true".equals(value));
		}
		@Override
		public boolean setLong(ColumnarColumnBoolean column, AmiRowImpl row, long value) {
			if (value == AmiTable.NULL_NUMBER)
				return column.setNull(row);
			else
				return column.setBoolean(row, value != 0L);
		}
		@Override
		public boolean setDouble(ColumnarColumnBoolean column, AmiRowImpl row, double value) {
			if (value != value)
				return column.setNull(row);
			else
				return column.setBoolean(row, value != 0D);
		}
		@Override
		public Comparable getComparable(ColumnarColumnBoolean column, AmiRowImpl row) {
			if (column.isNull(row))
				return null;
			return column.getBoolean(row);
		}

		@Override
		public boolean setComparable(ColumnarColumnBoolean column, AmiRowImpl row, Comparable value) {
			Boolean v = Caster_Boolean.INSTANCE.cast(value);
			if (v == null)
				return column.setNull(row);
			else
				return column.setBoolean(row, v);
		}
		@Override
		public boolean isPrimitive() {
			return true;
		}
		@Override
		public Object getDefaultValue() {
			return Boolean.FALSE;
		}
	}

	public static class AmiTableSetterGetterString implements AmiTableSetterGetter<ColumnarColumn<String>> {
		@Override
		public String getString(ColumnarColumn<String> column, AmiRowImpl row) {
			return column.getValue(row);
		}
		@Override
		public long getLong(ColumnarColumn<String> column, AmiRowImpl row) {
			String value = column.getValue(row);
			if (value == null)
				return AmiTable.NULL_NUMBER;
			return SH.parseLong(value);
		}
		@Override
		public double getDouble(ColumnarColumn<String> column, AmiRowImpl row) {
			String value = column.getValue(row);
			if (value == null)
				return AmiTable.NULL_DECIMAL;
			return SH.parseDouble(value);
		}
		@Override
		public boolean setString(ColumnarColumn<String> column, AmiRowImpl row, String value) {
			return column.setValue(row, value);
		}
		@Override
		public boolean setLong(ColumnarColumn<String> column, AmiRowImpl row, long value) {
			return column.setValue(row, SH.toString(value));
		}
		@Override
		public boolean setDouble(ColumnarColumn<String> column, AmiRowImpl row, double value) {
			return column.setValue(row, SH.toString(value));
		}
		@Override
		public Comparable getComparable(ColumnarColumn<String> column, AmiRowImpl row) {
			return column.getValue(row);
		}
		@Override
		public boolean setComparable(ColumnarColumn<String> column, AmiRowImpl row, Comparable value) {
			return column.setValue(row, Caster_String.INSTANCE.cast(value));
		}
		@Override
		public boolean isPrimitive() {
			return false;
		}
		@Override
		public Object getDefaultValue() {
			return "";
		}
	}

	public static class AmiTableSetterGetterBinary implements AmiTableSetterGetter<ColumnarColumn<Bytes>> {
		@Override
		public String getString(ColumnarColumn<Bytes> column, AmiRowImpl row) {
			return OH.toString(column.getValue(row));
		}
		@Override
		public long getLong(ColumnarColumn<Bytes> column, AmiRowImpl row) {
			return AmiTable.NULL_NUMBER;
		}
		@Override
		public double getDouble(ColumnarColumn<Bytes> column, AmiRowImpl row) {
			return AmiTable.NULL_DECIMAL;
		}
		@Override
		public boolean setString(ColumnarColumn<Bytes> column, AmiRowImpl row, String value) {
			return column.setValue(row, new Bytes(value.getBytes()));
		}
		@Override
		public boolean setLong(ColumnarColumn<Bytes> column, AmiRowImpl row, long value) {
			return false;
		}
		@Override
		public boolean setDouble(ColumnarColumn<Bytes> column, AmiRowImpl row, double value) {
			return false;
		}
		@Override
		public Comparable getComparable(ColumnarColumn<Bytes> column, AmiRowImpl row) {
			return column.getValue(row);
		}
		@Override
		public boolean setComparable(ColumnarColumn<Bytes> column, AmiRowImpl row, Comparable value) {
			if (value instanceof Bytes)
				return column.setValue(row, (Bytes) value);
			else if (value == null)
				return column.setNull(row);
			else
				return false;
		}
		@Override
		public boolean isPrimitive() {
			return false;
		}
		@Override
		public Object getDefaultValue() {
			return Bytes.EMPTY;
		}
	}

	public static class AmiTableSetterGetterComplex implements AmiTableSetterGetter<ColumnarColumn<Complex>> {
		@Override
		public String getString(ColumnarColumn<Complex> column, AmiRowImpl row) {
			return OH.toString(column.getValue(row));
		}
		@Override
		public long getLong(ColumnarColumn<Complex> column, AmiRowImpl row) {
			if (column.isNull(row.getLocation()))
				return AmiTable.NULL_NUMBER;
			return column.getValue(row).longValue();
		}
		@Override
		public double getDouble(ColumnarColumn<Complex> column, AmiRowImpl row) {
			if (column.isNull(row.getLocation()))
				return AmiTable.NULL_DECIMAL;
			return column.getValue(row).doubleValue();
		}
		@Override
		public boolean setString(ColumnarColumn<Complex> column, AmiRowImpl row, String value) {
			Complex c = Caster_Complex.INSTANCE.cast(value, false, false);
			if (c == null)
				return false;
			column.setValue(row, c);
			return true;
		}
		@Override
		public boolean setLong(ColumnarColumn<Complex> column, AmiRowImpl row, long value) {
			Complex c = new Complex(value);
			column.setValue(row, c);
			return true;
		}
		@Override
		public boolean setDouble(ColumnarColumn<Complex> column, AmiRowImpl row, double value) {
			Complex c = new Complex(value);
			column.setValue(row, c);
			return true;
		}
		@Override
		public Comparable getComparable(ColumnarColumn<Complex> column, AmiRowImpl row) {
			return column.getValue(row);
		}
		@Override
		public boolean setComparable(ColumnarColumn<Complex> column, AmiRowImpl row, Comparable value) {
			Complex c = Caster_Complex.INSTANCE.cast(value, false, false);
			if (c == null)
				column.setNull(row);
			else
				column.setValue(row, c);
			return true;
		}
		@Override
		public boolean isPrimitive() {
			return false;
		}
		@Override
		public Object getDefaultValue() {
			return Complex.ZERO;
		}
	}

	public static class AmiTableSetterGetterUUID implements AmiTableSetterGetter<ColumnarColumn<UUID>> {
		@Override
		public String getString(ColumnarColumn<UUID> column, AmiRowImpl row) {
			return OH.toString(column.getValue(row));
		}
		@Override
		public long getLong(ColumnarColumn<UUID> column, AmiRowImpl row) {
			return AmiTable.NULL_NUMBER;
		}
		@Override
		public double getDouble(ColumnarColumn<UUID> column, AmiRowImpl row) {
			return AmiTable.NULL_DECIMAL;
		}
		@Override
		public boolean setString(ColumnarColumn<UUID> column, AmiRowImpl row, String value) {
			UUID v = Caster_UUID.INSTANCE.cast(value, false, false);
			if (v == null)
				return false;
			column.setValue(row, v);
			return true;
		}
		@Override
		public boolean setLong(ColumnarColumn<UUID> column, AmiRowImpl row, long value) {
			return false;
		}
		@Override
		public boolean setDouble(ColumnarColumn<UUID> column, AmiRowImpl row, double value) {
			return false;
		}
		@Override
		public Comparable getComparable(ColumnarColumn<UUID> column, AmiRowImpl row) {
			return column.getValue(row);
		}
		@Override
		public boolean setComparable(ColumnarColumn<UUID> column, AmiRowImpl row, Comparable value) {
			UUID v = Caster_UUID.INSTANCE.cast(value, false, false);
			if (v == null)
				column.setNull(row);
			else
				column.setValue(row, (UUID) v);
			return true;
		}
		@Override
		public boolean isPrimitive() {
			return false;
		}
		@Override
		public Object getDefaultValue() {
			return UUID.EMPTY;
		}
	}

	public static class AmiTableSetterGetterBigInt implements AmiTableSetterGetter<ColumnarColumn<BigInteger>> {
		@Override
		public String getString(ColumnarColumn<BigInteger> column, AmiRowImpl row) {
			return OH.toString(column.getValue(row));
		}
		@Override
		public long getLong(ColumnarColumn<BigInteger> column, AmiRowImpl row) {
			if (column.isNull(row.getLocation()))
				return AmiTable.NULL_NUMBER;
			return column.getValue(row).longValue();
		}
		@Override
		public double getDouble(ColumnarColumn<BigInteger> column, AmiRowImpl row) {
			if (column.isNull(row.getLocation()))
				return AmiTable.NULL_DECIMAL;
			return column.getValue(row).doubleValue();
		}
		@Override
		public boolean setString(ColumnarColumn<BigInteger> column, AmiRowImpl row, String value) {
			BigInteger v = Caster_BigInteger.INSTANCE.cast(value, false, false);
			if (v == null)
				return false;
			column.setValue(row, v);
			return true;
		}
		@Override
		public boolean setLong(ColumnarColumn<BigInteger> column, AmiRowImpl row, long value) {
			column.setValue(row, BigInteger.valueOf(value));
			return true;
		}
		@Override
		public boolean setDouble(ColumnarColumn<BigInteger> column, AmiRowImpl row, double value) {
			column.setValue(row, new BigDecimal(value).toBigInteger());
			return true;
		}
		@Override
		public Comparable getComparable(ColumnarColumn<BigInteger> column, AmiRowImpl row) {
			return column.getValue(row);
		}
		@Override
		public boolean setComparable(ColumnarColumn<BigInteger> column, AmiRowImpl row, Comparable value) {
			BigInteger v = Caster_BigInteger.INSTANCE.cast(value, false, false);
			if (v == null)
				column.setNull(row);
			else
				column.setValue(row, (BigInteger) v);
			return true;
		}
		@Override
		public boolean isPrimitive() {
			return false;
		}
		@Override
		public Object getDefaultValue() {
			return BigInteger.ZERO;
		}
	}

	public static class AmiTableSetterGetterBigDec implements AmiTableSetterGetter<ColumnarColumn<BigDecimal>> {
		@Override
		public String getString(ColumnarColumn<BigDecimal> column, AmiRowImpl row) {
			return OH.toString(column.getValue(row));
		}
		@Override
		public long getLong(ColumnarColumn<BigDecimal> column, AmiRowImpl row) {
			if (column.isNull(row.getLocation()))
				return AmiTable.NULL_NUMBER;
			return column.getValue(row).longValue();
		}
		@Override
		public double getDouble(ColumnarColumn<BigDecimal> column, AmiRowImpl row) {
			if (column.isNull(row.getLocation()))
				return AmiTable.NULL_DECIMAL;
			return column.getValue(row).doubleValue();
		}
		@Override
		public boolean setString(ColumnarColumn<BigDecimal> column, AmiRowImpl row, String value) {
			BigDecimal v = Caster_BigDecimal.INSTANCE.cast(value, false, false);
			if (v == null)
				return false;
			column.setValue(row, v);
			return true;
		}
		@Override
		public boolean setLong(ColumnarColumn<BigDecimal> column, AmiRowImpl row, long value) {
			column.setValue(row, BigDecimal.valueOf(value));
			return true;
		}
		@Override
		public boolean setDouble(ColumnarColumn<BigDecimal> column, AmiRowImpl row, double value) {
			column.setValue(row, BigDecimal.valueOf(value));
			return true;
		}
		@Override
		public Comparable getComparable(ColumnarColumn<BigDecimal> column, AmiRowImpl row) {
			return column.getValue(row);
		}
		@Override
		public boolean setComparable(ColumnarColumn<BigDecimal> column, AmiRowImpl row, Comparable value) {
			BigDecimal v = Caster_BigDecimal.INSTANCE.cast(value, false, false);
			if (v == null)
				column.setNull(row);
			else
				column.setValue(row, (BigDecimal) v);
			return true;
		}
		@Override
		public boolean isPrimitive() {
			return false;
		}
		@Override
		public Object getDefaultValue() {
			return BigDecimal.ZERO;
		}
	}

	public static class AmiTableSetterGetterDateMillis implements AmiTableSetterGetter<ColumnarColumnDateMillis> {
		@Override
		public String getString(ColumnarColumnDateMillis column, AmiRowImpl row) {
			if (column.isNull(row.getLocation()))
				return null;
			return SH.toString(column.getInnerValue(row));
		}
		@Override
		public long getLong(ColumnarColumnDateMillis column, AmiRowImpl row) {
			if (column.isNull(row.getLocation()))
				return AmiTable.NULL_NUMBER;
			return column.getInnerValue(row);
		}
		@Override
		public double getDouble(ColumnarColumnDateMillis column, AmiRowImpl row) {
			if (column.isNull(row.getLocation()))
				return AmiTable.NULL_DECIMAL;
			return column.getInnerValue(row);
		}
		@Override
		public boolean setString(ColumnarColumnDateMillis column, AmiRowImpl row, String value) {
			if (value == null)
				return column.setNull(row);
			else
				return column.setInnerValue(row, SH.parseLong(value));
		}
		@Override
		public boolean setLong(ColumnarColumnDateMillis column, AmiRowImpl row, long value) {
			return column.setInnerValue(row, value);
		}
		@Override
		public boolean setDouble(ColumnarColumnDateMillis column, AmiRowImpl row, double value) {
			return column.setInnerValue(row, (long) value);
		}
		@Override
		public Comparable getComparable(ColumnarColumnDateMillis column, AmiRowImpl row) {
			return column.getValue(row);
		}
		@Override
		public boolean setComparable(ColumnarColumnDateMillis column, AmiRowImpl row, Comparable value) {
			return column.setValue(row, Caster_DateMillis.INSTANCE.cast(value));
		}
		@Override
		public boolean isPrimitive() {
			return false;
		}
		@Override
		public Object getDefaultValue() {
			return DateMillis.ZERO;
		}
	}

	public static class AmiTableSetterGetterDateNanos implements AmiTableSetterGetter<ColumnarColumnDateNanos> {
		@Override
		public String getString(ColumnarColumnDateNanos column, AmiRowImpl row) {
			if (column.isNull(row.getLocation()))
				return null;
			return SH.toString(column.getInnerValue(row));
		}
		@Override
		public long getLong(ColumnarColumnDateNanos column, AmiRowImpl row) {
			if (column.isNull(row.getLocation()))
				return AmiTable.NULL_NUMBER;
			return column.getInnerValue(row);
		}
		@Override
		public double getDouble(ColumnarColumnDateNanos column, AmiRowImpl row) {
			if (column.isNull(row.getLocation()))
				return AmiTable.NULL_DECIMAL;
			return column.getInnerValue(row);
		}
		@Override
		public boolean setString(ColumnarColumnDateNanos column, AmiRowImpl row, String value) {
			if (value == null)
				return column.setNull(row);
			else
				return column.setInnerValue(row, SH.parseLong(value));
		}
		@Override
		public boolean setLong(ColumnarColumnDateNanos column, AmiRowImpl row, long value) {
			return column.setInnerValue(row, value);
		}
		@Override
		public boolean setDouble(ColumnarColumnDateNanos column, AmiRowImpl row, double value) {
			return column.setInnerValue(row, (long) value);
		}
		@Override
		public Comparable getComparable(ColumnarColumnDateNanos column, AmiRowImpl row) {
			return column.getValue(row);
		}
		@Override
		public boolean setComparable(ColumnarColumnDateNanos column, AmiRowImpl row, Comparable value) {
			return column.setValue(row, Caster_DateNanos.INSTANCE.cast(value));
		}
		@Override
		public boolean isPrimitive() {
			return false;
		}
		@Override
		public Object getDefaultValue() {
			return DateNanos.ZERO;
		}
	}

	public static class AmiTableSetterGetterEnum implements AmiTableSetterGetter<ColumnarColumnEnum> {
		@Override
		public String getString(ColumnarColumnEnum column, AmiRowImpl row) {
			return column.getValue(row);
		}
		@Override
		public long getLong(ColumnarColumnEnum column, AmiRowImpl row) {
			String value = column.getValue(row);
			if (value == null)
				return AmiTable.NULL_NUMBER;
			return SH.parseLong(value);
		}
		@Override
		public double getDouble(ColumnarColumnEnum column, AmiRowImpl row) {
			String value = column.getValue(row);
			if (value == null)
				return AmiTable.NULL_DECIMAL;
			return SH.parseDouble(value);
		}
		@Override
		public boolean setString(ColumnarColumnEnum column, AmiRowImpl row, String value) {
			return column.setValue(row, value);
		}
		@Override
		public boolean setLong(ColumnarColumnEnum column, AmiRowImpl row, long value) {
			return column.setValue(row, SH.toString(value));
		}
		@Override
		public boolean setDouble(ColumnarColumnEnum column, AmiRowImpl row, double value) {
			return column.setValue(row, SH.toString(value));
		}
		@Override
		public Comparable getComparable(ColumnarColumnEnum column, AmiRowImpl row) {
			return column.getValue(row);
		}
		@Override
		public boolean setComparable(ColumnarColumnEnum column, AmiRowImpl row, Comparable value) {
			return column.setValue(row, Caster_String.INSTANCE.cast(value));
		}
		@Override
		public boolean isPrimitive() {
			return false;
		}
		@Override
		public Object getDefaultValue() {
			return "";
		}
	}

	public static boolean setValueLong(ColumnarRow row, ColumnarColumn<?> column, long val) {
		switch (column.getBasicType()) {
			case BasicTypes.LONG:
				((ColumnarColumnLong) column).setLong(row, (long) val);
				break;
			case BasicTypes.INT:
				((ColumnarColumnInt) column).setInt(row, (int) val);
				break;
			case BasicTypes.FLOAT:
				((ColumnarColumnFloat) column).setFloat(row, (float) val);
				break;
			case BasicTypes.DOUBLE:
				((ColumnarColumnDouble) column).setDouble(row, (double) val);
				break;
			case BasicTypes.STRING:
				((ColumnarColumnObject<String>) column).setObject(row, SH.toString(val));
				break;
			case BasicTypes.BOOLEAN:
				((ColumnarColumnBoolean) column).setBoolean(row, val != 0L);
				break;
			default:
				return false;
		}
		return true;
	}

	public static boolean setValueInt(ColumnarRow row, ColumnarColumn<?> column, int val) {
		switch (column.getBasicType()) {
			case BasicTypes.LONG:
				((ColumnarColumnLong) column).setLong(row, (long) val);
				break;
			case BasicTypes.INT:
				((ColumnarColumnInt) column).setInt(row, (int) val);
				break;
			case BasicTypes.FLOAT:
				((ColumnarColumnFloat) column).setFloat(row, (float) val);
				break;
			case BasicTypes.DOUBLE:
				((ColumnarColumnDouble) column).setDouble(row, (double) val);
				break;
			case BasicTypes.STRING:
				((ColumnarColumnObject<String>) column).setObject(row, SH.toString(val));
				break;
			case BasicTypes.BOOLEAN:
				((ColumnarColumnBoolean) column).setBoolean(row, val != 0);
				break;
			default:
				return false;
		}
		return true;
	}

	public static boolean setValueFloat(ColumnarRow row, ColumnarColumn<?> column, float val) {
		switch (column.getBasicType()) {
			case BasicTypes.LONG:
				((ColumnarColumnLong) column).setLong(row, (long) val);
				break;
			case BasicTypes.INT:
				((ColumnarColumnInt) column).setInt(row, (int) val);
				break;
			case BasicTypes.FLOAT:
				((ColumnarColumnFloat) column).setFloat(row, (float) val);
				break;
			case BasicTypes.DOUBLE:
				((ColumnarColumnDouble) column).setDouble(row, (double) val);
				break;
			case BasicTypes.STRING:
				((ColumnarColumnObject<String>) column).setObject(row, SH.toString(val));
				break;
			case BasicTypes.BOOLEAN:
				((ColumnarColumnBoolean) column).setBoolean(row, val != 0f);
				break;
			default:
				return false;
		}
		return true;
	}

	public static boolean setValueDouble(ColumnarRow row, ColumnarColumn<?> column, double val) {
		switch (column.getBasicType()) {
			case BasicTypes.LONG:
				((ColumnarColumnLong) column).setLong(row, (long) val);
				break;
			case BasicTypes.INT:
				((ColumnarColumnInt) column).setInt(row, (int) val);
				break;
			case BasicTypes.FLOAT:
				((ColumnarColumnFloat) column).setFloat(row, (float) val);
				break;
			case BasicTypes.DOUBLE:
				((ColumnarColumnDouble) column).setDouble(row, (double) val);
				break;
			case BasicTypes.STRING:
				((ColumnarColumnObject<String>) column).setObject(row, SH.toString(val));
				break;
			case BasicTypes.BOOLEAN:
				((ColumnarColumnBoolean) column).setBoolean(row, val != 0d);
				break;
			default:
				return false;
		}
		return true;
	}

	public static boolean setValueBoolean(ColumnarRow row, ColumnarColumn<?> column, boolean val) {
		switch (column.getBasicType()) {
			case BasicTypes.LONG:
				((ColumnarColumnLong) column).setLong(row, val ? 1L : 0L);
				break;
			case BasicTypes.INT:
				((ColumnarColumnInt) column).setInt(row, val ? 1 : 0);
				break;
			case BasicTypes.FLOAT:
				((ColumnarColumnFloat) column).setFloat(row, val ? 1F : 0F);
				break;
			case BasicTypes.DOUBLE:
				((ColumnarColumnDouble) column).setDouble(row, val ? 1D : 0D);
				break;
			case BasicTypes.STRING:
				((ColumnarColumnObject<String>) column).setObject(row, SH.toString(val));
				break;
			case BasicTypes.BOOLEAN:
				((ColumnarColumnBoolean) column).setBoolean(row, val);
				break;
			default:
				return false;
		}
		return true;
	}

	public static String getValueString(ColumnarRow row, ColumnarColumn<?> column) {
		if (column.isNull(row))
			return null;
		switch (column.getBasicType()) {
			case BasicTypes.LONG:
				return SH.toString(((ColumnarColumnLong) column).getLong(row));
			case BasicTypes.INT:
				return SH.toString(((ColumnarColumnInt) column).getInt(row));
			case BasicTypes.FLOAT:
				return SH.toString(((ColumnarColumnFloat) column).getFloat(row));
			case BasicTypes.DOUBLE:
				return SH.toString(((ColumnarColumnDouble) column).getDouble(row));
			case BasicTypes.STRING:
				return SH.toString(((ColumnarColumnObject<String>) column).getObject(row));
			case BasicTypes.BOOLEAN:
				return SH.toString(((ColumnarColumnBoolean) column).getBoolean(row));
			default:
		}
		return null;
	}
	public static long getValueLong(ColumnarRow row, ColumnarColumn<?> column) {
		if (column.isNull(row))
			return AmiTable.NULL_NUMBER;
		switch (column.getBasicType()) {
			case BasicTypes.LONG:
				return ((ColumnarColumnLong) column).getLong(row);
			case BasicTypes.INT:
				return ((ColumnarColumnInt) column).getInt(row);
			case BasicTypes.FLOAT:
				return (long) ((ColumnarColumnFloat) column).getFloat(row);
			case BasicTypes.DOUBLE:
				return (long) ((ColumnarColumnDouble) column).getDouble(row);
			case BasicTypes.STRING:
				return AmiTable.NULL_NUMBER;
			case BasicTypes.BOOLEAN:
				return ((ColumnarColumnBoolean) column).getBoolean(row) ? 1L : 0L;
			default:
		}
		return AmiTable.NULL_NUMBER;
	}
	public static double getValueDouble(ColumnarRow row, ColumnarColumn<?> column) {
		if (column.isNull(row))
			return AmiTable.NULL_DECIMAL;
		switch (column.getBasicType()) {
			case BasicTypes.LONG:
				return ((ColumnarColumnLong) column).getLong(row);
			case BasicTypes.INT:
				return ((ColumnarColumnInt) column).getInt(row);
			case BasicTypes.FLOAT:
				return ((ColumnarColumnFloat) column).getFloat(row);
			case BasicTypes.DOUBLE:
				return ((ColumnarColumnDouble) column).getDouble(row);
			case BasicTypes.STRING:
				return AmiTable.NULL_NUMBER;
			case BasicTypes.BOOLEAN:
				return ((ColumnarColumnBoolean) column).getBoolean(row) ? 1D : 0D;
			default:
		}
		return AmiTable.NULL_DECIMAL;
	}

	public static void onObject(AmiCenterState state, short typeId, AmiTableImpl table, AmiRelayObjectMessage event, AmiCenterApplication eApp, long now, CalcFrameStack sf) {
		table.addRowFromEntity(eApp.getAppName(), eApp.getAppId(), event.getId(), event.getExpires(), now, event.getParams(), sf);
	}
	public static void logTriggerError(AmiTimedRunnable trigger, String string, Throwable t, AmiRow row) {
		if (row != null)
			LH.warning(log, "Trigger error for ", OH.getClassName(trigger), " on row ", row, ": ", t);
		else
			LH.warning(log, "Trigger error for ", OH.getClassName(trigger), ": ", t);
	}

	public static void onDelete(AmiCenterState state, short typeId, AmiTableImpl table, String id, byte[] params, AmiCenterApplication eApp, long now, CalcFrameStack sf) {
		table.removeRowFromEntity(eApp.getAppName(), eApp.getAppId(), id, now, params, sf);
	}
	static public void writeField(FastByteArrayDataOutputStream buf, AmiColumnImpl pos, AmiRowImpl row) {
		switch (pos.getAmiType()) {
			case AmiTable.TYPE_INT:
			case AmiTable.TYPE_SHORT:
			case AmiTable.TYPE_BYTE: {
				long value = pos.getLong(row);
				if (value == AmiTable.NULL_NUMBER && pos.getIsNull(row))
					AmiEntityByteUtils.writeNull(buf);
				else
					AmiEntityByteUtils.writeInt(buf, (int) value);
				break;
			}
			case AmiTable.TYPE_BOOLEAN: {
				long value = pos.getLong(row);
				if (value == AmiTable.NULL_NUMBER)
					AmiEntityByteUtils.writeNull(buf);
				else
					AmiEntityByteUtils.writeBoolean(buf, value != 0L);
				break;
			}
			case AmiTable.TYPE_LONG: {
				long value = pos.getLong(row);
				if (value == AmiTable.NULL_NUMBER && pos.getIsNull(row))
					AmiEntityByteUtils.writeNull(buf);
				else
					AmiEntityByteUtils.writeLong(buf, value);
				break;
			}
			case AmiTable.TYPE_DOUBLE: {
				double value = pos.getDouble(row);
				if (value != value)
					AmiEntityByteUtils.writeNull(buf);
				else
					AmiEntityByteUtils.writeDouble(buf, value);
				break;
			}
			case AmiTable.TYPE_FLOAT: {
				double value = pos.getDouble(row);
				if (value != value)
					AmiEntityByteUtils.writeNull(buf);
				else
					AmiEntityByteUtils.writeFloat(buf, (float) value);
				break;
			}
			case AmiTable.TYPE_STRING: {
				AmiEntityByteUtils.writeCharSequence(buf, pos.getString(row));
				break;
			}
			case AmiTable.TYPE_UTC: {
				AmiEntityByteUtils.writeUtc(buf, pos.getLong(row));
				break;
			}
			case AmiTable.TYPE_UTCN: {
				AmiEntityByteUtils.writeUtcn(buf, pos.getLong(row));
				break;
			}
			case AmiTable.TYPE_CHAR: {
				long value = pos.getLong(row);
				if (value == AmiTable.NULL_NUMBER)
					AmiEntityByteUtils.writeNull(buf);
				else
					AmiEntityByteUtils.writeCharacter(buf, (char) value);
				break;
			}
			case AmiTable.TYPE_ENUM: {
				ColumnarColumnInt inner = ((ColumnarColumnEnum) pos.getColumn()).getInner();
				if (inner.isNull(row))
					AmiEntityByteUtils.writeNull(buf);
				else {
					int i = inner.getInt(row);
					if (i == -1)
						AmiEntityByteUtils.writeNull(buf);
					else {
						AmiEntityByteUtils.writeEnum(buf, i);
					}
				}
				break;
			}
			case AmiTable.TYPE_BINARY: {
				Bytes bytes = (Bytes) pos.getComparable(row);
				if (bytes == null)
					AmiEntityByteUtils.writeNull(buf);
				else
					AmiEntityByteUtils.writeBinary(buf, bytes.getBytes());
				break;
			}
			case AmiTable.TYPE_COMPLEX: {
				Complex bytes = (Complex) pos.getComparable(row);
				if (bytes == null)
					AmiEntityByteUtils.writeNull(buf);
				else
					AmiEntityByteUtils.writeComplex(buf, bytes);
				break;
			}
			case AmiTable.TYPE_UUID: {
				UUID bytes = (UUID) pos.getComparable(row);
				if (bytes == null)
					AmiEntityByteUtils.writeNull(buf);
				else
					AmiEntityByteUtils.writeUUID(buf, bytes);
				break;
			}
			case AmiTable.TYPE_BIGINT: {
				BigInteger bytes = (BigInteger) pos.getComparable(row);
				if (bytes == null)
					AmiEntityByteUtils.writeNull(buf);
				else
					AmiEntityByteUtils.writeBigInt(buf, bytes);
				break;
			}
			case AmiTable.TYPE_BIGDEC: {
				BigDecimal bytes = (BigDecimal) pos.getComparable(row);
				if (bytes == null)
					AmiEntityByteUtils.writeNull(buf);
				else
					AmiEntityByteUtils.writeBigDec(buf, bytes);
				break;
			}
			default:
				throw new RuntimeException("Unknown type: " + pos.getAmiType());
		}
	}
	public static boolean updateRow(AmiRow row, byte[] data, StringBuilder tmpbuf, AmiTableImpl impl, byte onUndefCol, CalcFrameStack sf) {
		if (data != null) {
			final int keysLength = ByteHelper.readShort(data, 0);
			final AmiCenterState state = impl.getState();
			for (int i = 0, valPos = (keysLength << 1) + 2, len; i < keysLength; i++, valPos += len - 1) {
				len = AmiUtils.getDataLength(data, valPos);
				final byte type = data[valPos];
				valPos++;
				final short key = ByteHelper.readShort(data, (i << 1) + 2);
				AmiColumnImpl<?> col = impl.getColumn(key);
				if (col == null) {
					switch (onUndefCol) {
						case AmiTableDef.ON_UNDEFINED_COLUMN_IGNORE:
							continue;
						case AmiTableDef.ON_UNDEFINED_COLUMN_ADD: {
							if (type == AmiDataEntity.PARAM_TYPE_NULL)
								continue;
							String cname = state.getAmiKeyString(key);
							LH.info(log, "Table '", impl.getName(), "' has OnUndefColumn='ADD' so auto-creating new column: '", cname, "'");
							final byte type2;
							if (type == AmiDataEntity.PARAM_TYPE_INT1 || type == AmiDataEntity.PARAM_TYPE_INT2 || type == AmiDataEntity.PARAM_TYPE_INT3)
								type2 = AmiDatasourceColumn.TYPE_INT;
							else
								type2 = type;
							col = impl.addColumn(impl.getColumnsCount(), type2, cname, Collections.EMPTY_MAP, sf);
							impl.getImdb().onSchemaChanged(sf);
							return false;
						}
						case AmiTableDef.ON_UNDEFINED_COLUMN_REJECT: {
							if (log.isLoggable(Level.INFO)) {
								String cname = state.getAmiKeyString(key);
								LH.info(log, "Table '", impl.getName(), "' has OnUndefColumn='REJECT' so dropping record with unknown column: '", cname, "'");
							}
							return false;
						}
					}
				}
				try {
					switch (type) {
						case AmiDataEntity.PARAM_TYPE_NULL:
							row.setNull(col, sf);
							break;
						case AmiDataEntity.PARAM_TYPE_ASCII: {
							int len2 = ByteHelper.readInt(data, valPos);
							int pos = valPos + 4;
							SH.ensureExtraCapacity(tmpbuf, len2);
							final int last = pos + len2;
							while (pos < last)
								tmpbuf.append((char) ByteHelper.readByte(data, pos++));
							row.setString(col, toString(tmpbuf, col), sf);
							break;
						}
						case AmiDataEntity.PARAM_TYPE_ASCII_SMALL: {
							int len2 = ByteHelper.readByte(data, valPos);
							int pos = valPos + 1;
							SH.ensureExtraCapacity(tmpbuf, len2);
							final int last = pos + len2;
							while (pos < last)
								tmpbuf.append((char) ByteHelper.readByte(data, pos++));
							row.setString(col, toString(tmpbuf, col), sf);
							break;
						}
						case AmiDataEntity.PARAM_TYPE_STRING: {
							int len2 = ByteHelper.readInt(data, valPos);
							int pos = valPos + 4;
							SH.ensureExtraCapacity(tmpbuf, len2);
							final int last = pos + len2 * 2;
							while (pos < last) {
								tmpbuf.append(ByteHelper.readChar(data, pos));
								pos += 2;
							}
							row.setString(col, toString(tmpbuf, col), sf);
							break;
						}
						case AmiDataEntity.PARAM_TYPE_ASCII_ENUM:
							int enumValue = state.getAmiStringPool(data, valPos + 1, len - 2);
							final String str;
							if (enumValue != 0) {
								str = state.getAmiValueString(enumValue);
								row.setString(col, str, sf);
							} else {
								//TODO: I don't think this gets hit
								int len2 = ByteHelper.readByte(data, valPos);
								int pos = valPos;//TODO: should be valPos+1 ?
								SH.ensureExtraCapacity(tmpbuf, len2);
								final int last = pos + len2;
								while (pos < last)
									tmpbuf.append((char) ByteHelper.readByte(data, pos++));
								row.setString(col, toString(tmpbuf, col), sf);
							}
							break;
						case AmiDataEntity.PARAM_TYPE_BOOLEAN:
							row.setLong(col, data[valPos] == 1 ? 1 : 0, sf);
							break;
						case AmiDataEntity.PARAM_TYPE_INT1:
							row.setLong(col, (int) ByteHelper.readByte(data, valPos), sf);
							break;
						case AmiDataEntity.PARAM_TYPE_INT2:
							row.setLong(col, (int) ByteHelper.readShort(data, valPos), sf);
							break;
						case AmiDataEntity.PARAM_TYPE_INT3:
							row.setLong(col, (int) ByteHelper.readInt3(data, valPos), sf);
							break;
						case AmiDataEntity.PARAM_TYPE_INT4:
							row.setLong(col, (int) ByteHelper.readInt(data, valPos), sf);
							break;
						case AmiDataEntity.PARAM_TYPE_CHAR:
							row.setLong(col, (char) ByteHelper.readChar(data, valPos), sf);
							break;
						case AmiDataEntity.PARAM_TYPE_LONG1:
							row.setLong(col, ByteHelper.readByte(data, valPos), sf);
							break;
						case AmiDataEntity.PARAM_TYPE_LONG2:
							row.setLong(col, ByteHelper.readShort(data, valPos), sf);
							break;
						case AmiDataEntity.PARAM_TYPE_LONG3:
							row.setLong(col, ByteHelper.readInt3(data, valPos), sf);
							break;
						case AmiDataEntity.PARAM_TYPE_LONG4:
							row.setLong(col, ByteHelper.readInt(data, valPos), sf);
							break;
						case AmiDataEntity.PARAM_TYPE_LONG5:
							row.setLong(col, ByteHelper.readLong5(data, valPos), sf);
							break;
						case AmiDataEntity.PARAM_TYPE_LONG6:
							row.setLong(col, ByteHelper.readLong6(data, valPos), sf);
							break;
						case AmiDataEntity.PARAM_TYPE_LONG7:
							row.setLong(col, ByteHelper.readLong7(data, valPos), sf);
							break;
						case AmiDataEntity.PARAM_TYPE_LONG8:
							row.setLong(col, ByteHelper.readLong(data, valPos), sf);
							break;
						case AmiDataEntity.PARAM_TYPE_UTC6:
							row.setLong(col, ByteHelper.readLong6(data, valPos), sf);
							break;
						case AmiDataEntity.PARAM_TYPE_UTCN:
							row.setLong(col, ByteHelper.readLong(data, valPos), sf);
							break;
						case AmiDataEntity.PARAM_TYPE_DOUBLE:
							row.setDouble(col, ByteHelper.readDouble(data, valPos), sf);
							break;
						case AmiDataEntity.PARAM_TYPE_FLOAT:
							row.setDouble(col, ByteHelper.readFloat(data, valPos), sf);
							break;
						case AmiDataEntity.PARAM_TYPE_COMPLEX: {
							double r = ByteHelper.readDouble(data, valPos);
							double ii = ByteHelper.readDouble(data, valPos + 8);
							Complex value = new Complex(r, ii);
							row.setComparable(col, value, sf);
							break;
						}
						case AmiDataEntity.PARAM_TYPE_UUID: {
							long m = ByteHelper.readLong(data, valPos);
							long l = ByteHelper.readLong(data, valPos + 8);
							UUID value = new UUID(m, l);
							row.setComparable(col, value, sf);
							break;
						}
						case AmiDataEntity.PARAM_TYPE_BINARY: {
							int len2 = ByteHelper.readInt(data, valPos);
							byte data2[] = new byte[len2];
							System.arraycopy(data, valPos + 4, data2, 0, len2);
							Bytes value = new Bytes(data2);
							row.setComparable(col, value, sf);
							break;
						}
					}
				} catch (NumberFormatException e) {
					SH.clear(tmpbuf);
					LH.warning(log, "Error for column ", col.getAmiTable().getName(), ".", col.getName(), ": ", e.getMessage());
				} catch (Exception e) {
					SH.clear(tmpbuf);
					LH.warning(log, "Error for column ", col.getAmiTable().getName(), ".", col.getName(), e);
				}

			}
		}
		return true;
	}

	private static String toString(StringBuilder tmpbuf, AmiColumnImpl<?> col) {
		String r;
		if (col.isStringBitmap()) {
			r = ((ColumnarColumnString_BitMap) col.getColumn()).getCachedString(tmpbuf);
			if (r == null)
				r = tmpbuf.toString();
		} else
			r = tmpbuf.toString();
		tmpbuf.setLength(0);
		return r;
	}
}
