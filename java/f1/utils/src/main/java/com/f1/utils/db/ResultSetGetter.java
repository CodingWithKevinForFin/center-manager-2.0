/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.db;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;

import com.f1.base.Bytes;
import com.f1.base.DateMillis;
import com.f1.base.DateNanos;
import com.f1.base.Valued;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.converter.json2.ObjectToJsonConverter;

public abstract class ResultSetGetter<T> {

	@Override
	public ResultSetGetter<T> clone() {
		try {
			return (ResultSetGetter<T>) super.clone();
		} catch (CloneNotSupportedException e) {
			throw OH.toRuntime(e);
		}
	}

	public void setToPid(ResultSet rs, int field, byte pid, Valued sink, boolean ignoreNulls) throws SQLException {
		final Object val = get(rs, field);
		if (!ignoreNulls || val != null)
			sink.put(pid, val);
	}

	final public T get(ResultSet rs, int field) {
		try {
			T r = getInner(rs, field);
			if (rs.wasNull())
				return null;
			return r;
		} catch (SQLException e) {
			throw new RuntimeException("Error getting param " + field, e);
		}
	}

	abstract public Class<T> getReturnType();
	abstract protected T getInner(ResultSet rs, int field) throws SQLException;
	abstract public void set(PreparedStatement ps, int parameterIndex, T value) throws SQLException;

	public static class StringResultSetGetter extends ResultSetGetter<String> {
		public static StringResultSetGetter INSTANCE = new StringResultSetGetter();

		private StringResultSetGetter() {
		}
		@Override
		protected String getInner(ResultSet rs, int field) throws SQLException {
			return rs.getString(field);
		}

		@Override
		public void set(PreparedStatement ps, int parameterIndex, String value) throws SQLException {
			ps.setString(parameterIndex, value);
		}

		@Override
		public Class<String> getReturnType() {
			return String.class;
		}

	}

	public static class DateResultSetGetter extends ResultSetGetter<Date> {
		public static DateResultSetGetter INSTANCE = new DateResultSetGetter();

		private DateResultSetGetter() {
		}
		@Override
		protected Date getInner(ResultSet rs, int field) throws SQLException {
			return rs.getDate(field);
		}

		@Override
		public void set(PreparedStatement ps, int parameterIndex, Date value) throws SQLException {
			ps.setDate(parameterIndex, value);
		}

		@Override
		public Class<Date> getReturnType() {
			return Date.class;
		}
	}

	public static class DateMillisResultSetGetter extends ResultSetGetter<DateMillis> {
		public static DateMillisResultSetGetter INSTANCE = new DateMillisResultSetGetter();

		private DateMillisResultSetGetter() {
		}

		@Override
		protected DateMillis getInner(ResultSet rs, int field) throws SQLException {
			Timestamp date = rs.getTimestamp(field);
			if (date == null)
				return null;
			return new DateMillis(date.getTime());
		}

		@Override
		public void set(PreparedStatement ps, int parameterIndex, DateMillis value) throws SQLException {
			ps.setDate(parameterIndex, value == null ? null : new Date(value.longValue()));
		}

		@Override
		public Class<DateMillis> getReturnType() {
			return DateMillis.class;
		}
	}

	public static class TemporalToDateMillisResultSetGetter extends ResultSetGetter<DateMillis> {
		public static TemporalToDateMillisResultSetGetter INSTANCE = new TemporalToDateMillisResultSetGetter();

		private TemporalToDateMillisResultSetGetter() {
		}

		@Override
		protected DateMillis getInner(ResultSet rs, int field) throws SQLException {
			Temporal date = (Temporal) rs.getObject(field);
			if (date == null)
				return null;
			long day = date.getLong(ChronoField.EPOCH_DAY);
			long ms = date.getLong(ChronoField.MILLI_OF_DAY);
			return new DateMillis(day * 86400000L + ms);
		}

		@Override
		public void set(PreparedStatement ps, int parameterIndex, DateMillis value) throws SQLException {
			ps.setDate(parameterIndex, value == null ? null : new Date(value.longValue()));
		}

		@Override
		public Class<DateMillis> getReturnType() {
			return DateMillis.class;
		}
	}

	public static class DateNanosResultSetGetter extends ResultSetGetter<DateNanos> {
		public static DateNanosResultSetGetter INSTANCE = new DateNanosResultSetGetter();

		private DateNanosResultSetGetter() {
		}

		@Override
		protected DateNanos getInner(ResultSet rs, int field) throws SQLException {
			Timestamp date = rs.getTimestamp(field);
			if (date == null)
				return null;
			return new DateNanos(date);
		}

		@Override
		public void set(PreparedStatement ps, int parameterIndex, DateNanos value) throws SQLException {
			if (value == null)
				ps.setTimestamp(parameterIndex, null);
			else {
				Timestamp date = new Timestamp(value.getTimeMillis());
				date.setNanos(value.getNanos());
				ps.setTimestamp(parameterIndex, date);
			}
		}

		@Override
		public Class<DateNanos> getReturnType() {
			return DateNanos.class;
		}
	}

	public static class DayTimeResultSetGetter extends ResultSetGetter<Long> {
		public static DayTimeResultSetGetter INSTANCE = new DayTimeResultSetGetter();

		private DayTimeResultSetGetter() {
		}

		@Override
		protected Long getInner(ResultSet rs, int field) throws SQLException {
			Timestamp date = rs.getTimestamp(field);
			if (date == null)
				return null;
			return date.getTime();
		}

		@Override
		public void set(PreparedStatement ps, int parameterIndex, Long value) throws SQLException {
			ps.setTimestamp(parameterIndex, value == null ? null : new Timestamp(value.longValue()));
		}

		@Override
		public Class<Long> getReturnType() {
			return Long.class;
		}

	}

	public static class TimestampResultSetGetter extends ResultSetGetter<java.sql.Timestamp> {
		public static TimestampResultSetGetter INSTANCE = new TimestampResultSetGetter();

		private TimestampResultSetGetter() {
		}

		@Override
		protected java.sql.Timestamp getInner(ResultSet rs, int field) throws SQLException {
			return rs.getTimestamp(field);
		}

		@Override
		public void set(PreparedStatement ps, int parameterIndex, Timestamp value) throws SQLException {
			ps.setTimestamp(parameterIndex, value);
		}

		@Override
		public Class<Timestamp> getReturnType() {
			return Timestamp.class;
		}

	}

	public static class TimeResultSetGetter extends ResultSetGetter<Time> {
		public static TimeResultSetGetter INSTANCE = new TimeResultSetGetter();

		private TimeResultSetGetter() {
		}

		@Override
		protected Time getInner(ResultSet rs, int field) throws SQLException {
			return rs.getTime(field);
		}

		@Override
		public void set(PreparedStatement ps, int parameterIndex, Time value) throws SQLException {
			ps.setTime(parameterIndex, value);
		}

		@Override
		public Class<Time> getReturnType() {
			return Time.class;
		}

	}

	public static class ByteResultSetGetter extends ResultSetGetter<Byte> {
		public static ByteResultSetGetter INSTANCE = new ByteResultSetGetter();

		private ByteResultSetGetter() {
		}

		@Override
		protected Byte getInner(ResultSet rs, int field) throws SQLException {
			return rs.getByte(field);
		}

		@Override
		public void setToPid(ResultSet rs, int field, byte pid, Valued sink, boolean ignoreNulls) throws SQLException {
			byte val = rs.getByte(field);
			if (rs.wasNull()) {
				if (!ignoreNulls)
					sink.put(pid, null);
			} else
				sink.putByte(pid, val);
		}

		@Override
		public void set(PreparedStatement ps, int parameterIndex, Byte value) throws SQLException {
			if (value == null)
				ps.setNull(parameterIndex, Types.TINYINT);
			else
				ps.setByte(parameterIndex, value.byteValue());
		}

		@Override
		public Class<Byte> getReturnType() {
			return Byte.class;
		}
	}

	public static class ByteArrayResultSetGetter extends ResultSetGetter<byte[]> {
		public static ByteArrayResultSetGetter INSTANCE = new ByteArrayResultSetGetter();

		private ByteArrayResultSetGetter() {
		}

		@Override
		protected byte[] getInner(ResultSet rs, int field) throws SQLException {
			return rs.getBytes(field);
		}

		@Override
		public void setToPid(ResultSet rs, int field, byte pid, Valued sink, boolean ignoreNulls) throws SQLException {
			byte[] val = rs.getBytes(field);
			if (!ignoreNulls || !rs.wasNull())
				sink.put(pid, val);
		}

		@Override
		public void set(PreparedStatement ps, int parameterIndex, byte[] value) throws SQLException {
			ps.setBytes(parameterIndex, value);
		}

		@Override
		public Class<byte[]> getReturnType() {
			return byte[].class;
		}

	}

	public static class BytesResultSetGetter extends ResultSetGetter<Bytes> {
		public static BytesResultSetGetter INSTANCE = new BytesResultSetGetter();

		private BytesResultSetGetter() {
		}

		@Override
		protected Bytes getInner(ResultSet rs, int field) throws SQLException {
			return Bytes.valueOf(rs.getBytes(field));
		}

		@Override
		public void setToPid(ResultSet rs, int field, byte pid, Valued sink, boolean ignoreNulls) throws SQLException {
			byte[] val = rs.getBytes(field);
			if (!ignoreNulls || !rs.wasNull())
				sink.put(pid, val);
		}

		@Override
		public void set(PreparedStatement ps, int parameterIndex, Bytes value) throws SQLException {
			ps.setBytes(parameterIndex, value == null ? null : value.getBytes());
		}

		@Override
		public Class<Bytes> getReturnType() {
			return Bytes.class;
		}

	}

	public static class ShortResultSetGetter extends ResultSetGetter<Short> {
		public static ShortResultSetGetter INSTANCE = new ShortResultSetGetter();

		private ShortResultSetGetter() {
		}

		@Override
		protected Short getInner(ResultSet rs, int field) throws SQLException {
			return rs.getShort(field);
		}

		@Override
		public void setToPid(ResultSet rs, int field, byte pid, Valued sink, boolean ignoreNulls) throws SQLException {
			short val = rs.getShort(field);
			if (rs.wasNull()) {
				if (!ignoreNulls)
					sink.put(pid, null);
			} else
				sink.putShort(pid, val);
		}

		@Override
		public void set(PreparedStatement ps, int parameterIndex, Short value) throws SQLException {
			if (value == null)
				ps.setNull(parameterIndex, Types.SMALLINT);
			else
				ps.setShort(parameterIndex, value.shortValue());
		}

		@Override
		public Class<Short> getReturnType() {
			return Short.class;
		}
	}

	public static class IntegerResultSetGetter extends ResultSetGetter<Integer> {
		public static IntegerResultSetGetter INSTANCE = new IntegerResultSetGetter();

		private IntegerResultSetGetter() {
		}

		@Override
		protected Integer getInner(ResultSet rs, int field) throws SQLException {
			return rs.getInt(field);
		}

		@Override
		public void setToPid(ResultSet rs, int field, byte pid, Valued sink, boolean ignoreNulls) throws SQLException {
			int val = rs.getInt(field);
			if (rs.wasNull()) {
				if (!ignoreNulls)
					sink.put(pid, null);
			} else
				sink.putInt(pid, val);
		}

		@Override
		public void set(PreparedStatement ps, int parameterIndex, Integer value) throws SQLException {
			if (value == null)
				ps.setNull(parameterIndex, Types.INTEGER);
			else
				ps.setInt(parameterIndex, value.intValue());
		}

		@Override
		public Class<Integer> getReturnType() {
			return Integer.class;
		}
	}

	public static class LongResultSetGetter extends ResultSetGetter<Long> {
		public static LongResultSetGetter INSTANCE = new LongResultSetGetter();

		private LongResultSetGetter() {
		}

		@Override
		protected Long getInner(ResultSet rs, int field) throws SQLException {
			return rs.getLong(field);
		}

		@Override
		public void setToPid(ResultSet rs, int field, byte pid, Valued sink, boolean ignoreNulls) throws SQLException {
			long val = rs.getLong(field);
			if (rs.wasNull()) {
				if (!ignoreNulls)
					sink.put(pid, null);
			} else
				sink.putLong(pid, val);
		}
		@Override
		public void set(PreparedStatement ps, int parameterIndex, Long value) throws SQLException {
			if (value == null)
				ps.setNull(parameterIndex, Types.BIGINT);//SEEMS SURPRISING, but that's the spec
			else
				ps.setLong(parameterIndex, value.longValue());
		}

		@Override
		public Class<Long> getReturnType() {
			return Long.class;
		}
	}

	public static class FloatResultSetGetter extends ResultSetGetter<Float> {
		public static FloatResultSetGetter INSTANCE = new FloatResultSetGetter();

		private FloatResultSetGetter() {
		}

		@Override
		protected Float getInner(ResultSet rs, int field) throws SQLException {
			return rs.getFloat(field);
		}

		@Override
		public void setToPid(ResultSet rs, int field, byte pid, Valued sink, boolean ignoreNulls) throws SQLException {
			float val = rs.getFloat(field);
			if (rs.wasNull()) {
				if (!ignoreNulls)
					sink.put(pid, null);
			} else
				sink.putFloat(pid, val);
		}

		@Override
		public void set(PreparedStatement ps, int parameterIndex, Float value) throws SQLException {
			if (value == null)
				ps.setNull(parameterIndex, Types.REAL);
			else
				ps.setFloat(parameterIndex, value.floatValue());
		}

		@Override
		public Class<Float> getReturnType() {
			return Float.class;
		}
	}

	public static class DoubleResultSetGetter extends ResultSetGetter<Double> {
		public static DoubleResultSetGetter INSTANCE = new DoubleResultSetGetter();

		private DoubleResultSetGetter() {
		}

		@Override
		protected Double getInner(ResultSet rs, int field) throws SQLException {
			return rs.getDouble(field);
		}

		@Override
		public void setToPid(ResultSet rs, int field, byte pid, Valued sink, boolean ignoreNulls) throws SQLException {
			double val = rs.getDouble(field);
			if (rs.wasNull()) {
				if (!ignoreNulls)
					sink.put(pid, null);
			} else
				sink.putDouble(pid, val);
		}

		@Override
		public void set(PreparedStatement ps, int parameterIndex, Double value) throws SQLException {
			if (value == null)
				ps.setNull(parameterIndex, Types.DOUBLE);
			else
				ps.setDouble(parameterIndex, value.doubleValue());
		}

		@Override
		public Class<Double> getReturnType() {
			return Double.class;
		}
	}

	public static class CharResultSetGetter extends ResultSetGetter<Character> {
		public static CharResultSetGetter INSTANCE = new CharResultSetGetter();

		private CharResultSetGetter() {
		}

		@Override
		protected Character getInner(ResultSet rs, int field) throws SQLException {
			String r = rs.getString(field);
			return r == null || r.length() == 0 ? null : r.charAt(0);
		}

		@Override
		public void setToPid(ResultSet rs, int field, byte pid, Valued sink, boolean ignoreNulls) throws SQLException {
			String r = rs.getString(field);
			if (r == null || r.length() == 0) {
				if (!ignoreNulls)
					sink.put(pid, null);
			} else
				sink.putChar(pid, r.charAt(0));
		}

		@Override
		public void set(PreparedStatement ps, int parameterIndex, Character value) throws SQLException {
			ps.setString(parameterIndex, SH.toString(value));
		}

		@Override
		public Class<Character> getReturnType() {
			return Character.class;
		}
	}

	public static class BooleanResultSetGetter extends ResultSetGetter<Boolean> {
		public static BooleanResultSetGetter INSTANCE = new BooleanResultSetGetter();

		private BooleanResultSetGetter() {
		}

		@Override
		protected Boolean getInner(ResultSet rs, int field) throws SQLException {
			return rs.getBoolean(field);
		}
		@Override
		public void setToPid(ResultSet rs, int field, byte pid, Valued sink, boolean ignoreNulls) throws SQLException {
			boolean val = rs.getBoolean(field);
			if (rs.wasNull()) {
				if (!ignoreNulls)
					sink.put(pid, null);
			} else
				sink.putBoolean(pid, val);
		}
		@Override
		public void set(PreparedStatement ps, int parameterIndex, Boolean value) throws SQLException {
			if (value == null)
				ps.setNull(parameterIndex, Types.BOOLEAN);
			else
				ps.setBoolean(parameterIndex, value.booleanValue());
		}

		@Override
		public Class<Boolean> getReturnType() {
			return Boolean.class;
		}

	}

	public static class BigDecimalResultSetGetter extends ResultSetGetter<BigDecimal> {
		public static BigDecimalResultSetGetter INSTANCE = new BigDecimalResultSetGetter();

		private BigDecimalResultSetGetter() {
		}

		@Override
		protected BigDecimal getInner(ResultSet rs, int field) throws SQLException {
			return rs.getBigDecimal(field);
		}

		@Override
		public void set(PreparedStatement ps, int parameterIndex, BigDecimal value) throws SQLException {
			ps.setBigDecimal(parameterIndex, value);
		}

		@Override
		public Class<BigDecimal> getReturnType() {
			return BigDecimal.class;
		}

	}

	public static class BigIntegerResultSetGetter extends ResultSetGetter<BigInteger> {
		public static BigIntegerResultSetGetter INSTANCE = new BigIntegerResultSetGetter();

		private BigIntegerResultSetGetter() {
		}

		@Override
		protected BigInteger getInner(ResultSet rs, int field) throws SQLException {
			Object o = rs.getObject(field);
			if (o == null || o instanceof BigInteger)
				return (BigInteger) o;
			return BigInteger.valueOf(((Number) o).longValue());
		}

		@Override
		public void set(PreparedStatement ps, int parameterIndex, BigInteger value) throws SQLException {
			ps.setBigDecimal(parameterIndex, value == null ? null : new BigDecimal(value));
		}

		@Override
		public Class<BigInteger> getReturnType() {
			return BigInteger.class;
		}

	}

	public static class ObjectResultSetGetter<T> extends ResultSetGetter<T> {
		final private Class<T> type;

		public ObjectResultSetGetter(Class<T> clazz) {
			this.type = clazz;
		}

		@Override
		protected T getInner(ResultSet rs, int field) throws SQLException {
			return (T) rs.getObject(field);
		}

		@Override
		public void set(PreparedStatement ps, int parameterIndex, T value) throws SQLException {
			ps.setObject(parameterIndex, value);
		}

		@Override
		public Class<T> getReturnType() {
			return type;
		}
	}

	public static class JsonResultSetGetter extends ResultSetGetter<Object> {
		public static JsonResultSetGetter INSTANCE = new JsonResultSetGetter();

		private JsonResultSetGetter() {
		}

		private ObjectToJsonConverter converter;

		public JsonResultSetGetter(ObjectToJsonConverter converter) {
			this.converter = converter;
		}
		@Override
		protected Object getInner(ResultSet rs, int field) throws SQLException {
			String json = rs.getString(field);
			if (json == null)
				return null;
			return converter.stringToObject(json);
		}

		@Override
		public void set(PreparedStatement ps, int parameterIndex, Object value) throws SQLException {
			ps.setObject(parameterIndex, value == null ? null : converter.objectToString(value));
		}
		@Override
		public Class<Object> getReturnType() {
			return Object.class;
		}
	}

	public static class NullAsDoubleNan extends ResultSetGetter<Object> {
		public static NullAsDoubleNan INSTANCE = new NullAsDoubleNan();

		private NullAsDoubleNan() {
		}

		@Override
		protected Double getInner(ResultSet rs, int field) throws SQLException {
			double r = rs.getDouble(field);
			return rs.wasNull() ? Double.NaN : Double.valueOf(r);
		}
		@Override
		public void setToPid(ResultSet rs, int field, byte pid, Valued sink, boolean ignoreNulls) throws SQLException {
			double val = rs.getDouble(field);
			if (rs.wasNull())
				sink.putDouble(pid, Double.NaN);
			else
				sink.putDouble(pid, val);
		}
		@Override
		public void set(PreparedStatement ps, int parameterIndex, Object value) throws SQLException {
			if (value == null)
				ps.setNull(parameterIndex, java.sql.Types.DECIMAL);
			else
				ps.setObject(parameterIndex, value);
		}

		@Override
		public Class<Object> getReturnType() {
			return Object.class;
		}

	}

	public static class CastToBoolean extends ResultSetGetter<Boolean> {
		public static CastToBoolean INSTANCE = new CastToBoolean();

		private CastToBoolean() {
		}

		@Override
		protected Boolean getInner(ResultSet rs, int field) throws SQLException {
			int r = rs.getInt(field);
			return rs.wasNull() ? null : r != 0;
		}
		@Override
		public void setToPid(ResultSet rs, int field, byte pid, Valued sink, boolean ignoreNulls) throws SQLException {
			int val = rs.getInt(field);
			if (!ignoreNulls || !rs.wasNull())
				sink.putBoolean(pid, val != 0);
		}
		@Override
		public void set(PreparedStatement ps, int parameterIndex, Boolean value) throws SQLException {
			if (value == null)
				ps.setNull(parameterIndex, Types.BOOLEAN);
			else
				ps.setBoolean(parameterIndex, value.booleanValue());
		}

		@Override
		public Class<Boolean> getReturnType() {
			return Boolean.class;
		}

	}

	public static class NullAsFloatNan extends ResultSetGetter<Object> {
		public static NullAsFloatNan INSTANCE = new NullAsFloatNan();

		private NullAsFloatNan() {
		}

		@Override
		protected Float getInner(ResultSet rs, int field) throws SQLException {
			float r = rs.getFloat(field);
			return rs.wasNull() ? Float.NaN : Float.valueOf(r);
		}
		@Override
		public void setToPid(ResultSet rs, int field, byte pid, Valued sink, boolean ignoreNulls) throws SQLException {
			float val = rs.getFloat(field);
			if (!rs.wasNull())
				sink.putFloat(pid, Float.NaN);
			else
				sink.putFloat(pid, val);
		}
		@Override
		public void set(PreparedStatement ps, int parameterIndex, Object value) throws SQLException {
			if (value == null)
				ps.setNull(parameterIndex, java.sql.Types.REAL);
			else
				ps.setObject(parameterIndex, value);
		}

		@Override
		public Class<Object> getReturnType() {
			return Object.class;
		}

	}

}
