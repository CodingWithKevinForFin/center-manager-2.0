/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

import com.f1.base.Caster;
import com.f1.base.ObjectGeneratorForClass;
import com.f1.base.PartialMessage;
import com.f1.base.Table;
import com.f1.base.Valued;
import com.f1.base.ValuedParam;
import com.f1.base.ValuedSchema;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.db.Database;
import com.f1.utils.db.DriverToDataSource;
import com.f1.utils.db.PooledDataSource;
import com.f1.utils.db.ResultSetGetter;
import com.f1.utils.impl.StringCharReader;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.columnar.ColumnarColumn;
import com.f1.utils.structs.table.columnar.ColumnarColumnBoolean;
import com.f1.utils.structs.table.columnar.ColumnarColumnByte;
import com.f1.utils.structs.table.columnar.ColumnarColumnChar;
import com.f1.utils.structs.table.columnar.ColumnarColumnDouble;
import com.f1.utils.structs.table.columnar.ColumnarColumnFloat;
import com.f1.utils.structs.table.columnar.ColumnarColumnInt;
import com.f1.utils.structs.table.columnar.ColumnarColumnLong;
import com.f1.utils.structs.table.columnar.ColumnarColumnPrimitive;
import com.f1.utils.structs.table.columnar.ColumnarColumnShort;
import com.f1.utils.structs.table.columnar.ColumnarRow;
import com.f1.utils.structs.table.columnar.ColumnarTable;

public class DBH {

	public static final String PASSWORD_MASK = "****";
	private static Logger log = LH.get();
	public static final short TABLE_OPTIONS_NONE = 0;
	public static final short TABLE_OPTIONS_SKIP_TITLE = 1;

	private DBH() {
	}

	private static final int[] BRACKET_OR_COLON = new int[] { '{', ':' };
	private static final String PROPERTY_MIN_POOL_SIZE = "f1.poolsize.min";
	private static final String PROPERTY_MAX_POOL_SIZE = "f1.poolsize.max";
	private static final String PROPERTY_AUTOCOMMIT = "f1.autocommit";
	private static final String PROPERTY_MAX_POLICY = "f1.poolsize.max.policy";
	private static final String PROPERTY_VALIDATE_SECONDS = "f1.validate.sec";
	private static final HashMap<String, ResultSetGetter<?>> DEFAULT_GETTERS = new HashMap<String, ResultSetGetter<?>>();
	public static final String MAX_POLICY_BLOCK = "block";
	public static final String MAX_POLICY_NEW_CONNECTION = "newconnection";
	public static final int DEFAULT_LOG_LARGE_QUERY_PERIOD = 100000;
	private static final String PROPERTY_LEAK_CUTOFF_SEC = "f1.pool.leak.cutoff.sec";

	static {
		DEFAULT_GETTERS.put("IF_NULL_FLOAT_NAN", ResultSetGetter.NullAsFloatNan.INSTANCE);
		DEFAULT_GETTERS.put("IF_NULL_DOUBLE_NAN", ResultSetGetter.NullAsDoubleNan.INSTANCE);
		DEFAULT_GETTERS.put("BOOLEAN", ResultSetGetter.CastToBoolean.INSTANCE);
		DEFAULT_GETTERS.put("JSON", new ResultSetGetter.JsonResultSetGetter(new ObjectToJsonConverter()));

	}

	static public Database createPooledDataSource(final String url, String password) throws SQLException {
		try {
			final DriverToDataSource inner = createDataSource(url, password);
			final PooledDataSource r = new PooledDataSource(inner, url);
			final String minPoolSize = inner.getProperty(PROPERTY_MIN_POOL_SIZE);
			final String maxPoolSize = inner.getProperty(PROPERTY_MAX_POOL_SIZE);
			final String maxPoolSizePolicy = inner.getProperty(PROPERTY_MAX_POLICY);
			final String autoCommit = inner.getProperty(PROPERTY_AUTOCOMMIT);
			final String validate = inner.getProperty(PROPERTY_VALIDATE_SECONDS);
			final String leakSec = inner.getProperty(PROPERTY_LEAK_CUTOFF_SEC);
			if (minPoolSize != null)
				r.setMinPoolSize(Integer.parseInt(minPoolSize));
			if (maxPoolSize != null)
				r.setMaxPoolSize(Integer.parseInt(maxPoolSize));
			if (autoCommit != null) {
				if ("skip".equals(autoCommit))
					r.setSkipAutoCommit(true);
				else
					r.setAutoCommit(Boolean.parseBoolean(autoCommit));
			}
			if (leakSec != null) {
				r.setMonitorForConnectionLeaks(true);
				r.setConnectionIsLeakPeriodMs(SH.parseInt(leakSec) * 1000L);
			}
			if (validate != null)
				r.setValidateTimeoutSeconds(SH.parseInt(validate));
			if (maxPoolSizePolicy == null || MAX_POLICY_NEW_CONNECTION.equals(maxPoolSizePolicy))
				r.setMaxPoolSizePolicy(PooledDataSource.MAX_POLICY_NEW_CONNECTION);
			else if (MAX_POLICY_BLOCK.equals(maxPoolSizePolicy))
				r.setMaxPoolSizePolicy(PooledDataSource.MAX_POLICY_BLOCK);
			else
				throw new RuntimeException("Bad value for " + PROPERTY_MAX_POLICY);
			return r;
		} catch (Exception e) {
			throw new SQLException("could not connect to db at: " + url, e);
		}
	}

	/**
	 * Creates a datasource using a driver class name, and the sql url. should be in format:<BR>
	 * driver.full.class.name:{key=value,optional=options}:driver.specific.url <BR>
	 * -- or -- <BR>
	 * driver.full.class.name:driver.specific.url
	 * 
	 * @param url
	 *            the url in the specified format. All patterns with four starts (****) will be replaced by the supplied password. This allows for passwords to not be printed in
	 *            log files, etc.
	 * @param password
	 *            the url in the specified format
	 * @return a datasource connecting to supplied url using supplied driver
	 * @throws SQLException
	 */
	static public DriverToDataSource createDataSource(final String url, String password) throws SQLException {
		if (url.indexOf("*****") != -1)
			throw new SQLException("url must not contain a string of consecutive stars longer than '" + PASSWORD_MASK + "' for: " + url);
		if (password == null)
			throw new SQLException("password is null");
		final String urlWithPassword = replaceMaskWithPassword(url, password);
		if (urlWithPassword.equals(url))
			throw new SQLException("url must contain a '" + PASSWORD_MASK + "' which will be replaced with a password for: " + url);
		try {
			return createDataSource(urlWithPassword);
		} catch (SQLException e) {
			throw new SQLException("Error connection to URL: " + url + ": " + e.getMessage(), e);
		}
	}

	public static String replaceMaskWithPassword(final String url, String password) {
		final String urlWithPassword = SH.replaceAll(url, PASSWORD_MASK, password);
		return urlWithPassword;
	}

	/**
	 * Creates a datasource using a driver class name, and the sql url. should be in format:<BR>
	 * driver.full.class.name:{key=value,optional=options}:driver.specific.url <BR>
	 * -- or -- <BR>
	 * driver.full.class.name:driver.specific.url
	 * 
	 * @param url
	 *            the url in the specified format
	 * @return a datasource connecting to supplied url using supplied driver
	 * @throws SQLException
	 */
	static public DriverToDataSource createDataSource(final String url) throws SQLException {
		final StringCharReader reader = new StringCharReader(url);
		final StringBuilder sink = new StringBuilder();
		final String className;
		final Properties properties = new Properties();
		final String remainder;
		try {
			reader.readUntil(':', SH.clear(sink));
			reader.expect(':');
			className = sink.toString();
			reader.skip(' ');
			if ('{' == reader.expectAny(BRACKET_OR_COLON)) {
				reader.readUntil('}', '\\', SH.clear(sink));
				reader.expect('}');
				reader.skip(' ');
				reader.expect(':');
				for (Entry<String, String> i : SH.splitToMap(',', '=', '\\', sink.toString()).entrySet())
					properties.put(i.getKey(), i.getValue());
			}
			reader.readUntil(CharReader.EOF, SH.clear(sink));
			remainder = sink.toString();
		} catch (Exception e) {
			throw new SQLException("URL must be in format: driver.full.class.name:{key=value,optional=options}:driver.specific.url", e);
		}
		return createDatasource(className, remainder, properties);
	}

	private static CopyOnWriteHashMap<String, java.sql.Driver> drivers = new CopyOnWriteHashMap<String, Driver>();

	public static DriverToDataSource createDatasource(String className, String remainder, Properties properties) throws SQLException {
		Driver obj = drivers.get(className);
		if (obj == null) {
			obj = (Driver) RH.invokeConstructor(className);
			drivers.put(className, obj);
		}
		return new DriverToDataSource(obj, remainder, properties);
	}

	static public String expandParamToList(String sql, int paramLoc, int size) {
		if (size < 1)
			throw new IndexOutOfBoundsException(SH.toString(size));
		if (size == 1)
			return sql;
		int index = -1;
		for (int i = 0; i < paramLoc; i++) {
			index = sql.indexOf('?', index) + 1;
			if (index == 0)
				throw new IndexOutOfBoundsException("'" + sql + "' does not have '?' at " + paramLoc);
		}
		return sql.substring(0, index) + SH.repeat(",?", size - 1) + sql.substring(index);
	}

	private static Map<Class<?>, ResultSetGetter<?>> GETTERS = new HashMap<Class<?>, ResultSetGetter<?>>();

	private static ConcurrentMap<Class<?>, ResultSetGetter<?>> GETTERS_CACHE = new CopyOnWriteHashMap<Class<?>, ResultSetGetter<?>>();
	static {
		GETTERS.put(String.class, ResultSetGetter.StringResultSetGetter.INSTANCE);
		GETTERS.put(Integer.class, ResultSetGetter.IntegerResultSetGetter.INSTANCE);
		GETTERS.put(Integer.TYPE, ResultSetGetter.IntegerResultSetGetter.INSTANCE);
		GETTERS.put(Boolean.class, ResultSetGetter.BooleanResultSetGetter.INSTANCE);
		GETTERS.put(Boolean.TYPE, ResultSetGetter.BooleanResultSetGetter.INSTANCE);
		GETTERS.put(Long.class, ResultSetGetter.LongResultSetGetter.INSTANCE);
		GETTERS.put(Long.TYPE, ResultSetGetter.LongResultSetGetter.INSTANCE);
		GETTERS.put(Float.class, ResultSetGetter.FloatResultSetGetter.INSTANCE);
		GETTERS.put(Float.TYPE, ResultSetGetter.FloatResultSetGetter.INSTANCE);
		GETTERS.put(Double.class, ResultSetGetter.DoubleResultSetGetter.INSTANCE);
		GETTERS.put(Double.TYPE, ResultSetGetter.DoubleResultSetGetter.INSTANCE);
		GETTERS.put(Short.class, ResultSetGetter.ShortResultSetGetter.INSTANCE);
		GETTERS.put(Short.TYPE, ResultSetGetter.ShortResultSetGetter.INSTANCE);
		GETTERS.put(Byte.class, ResultSetGetter.ByteResultSetGetter.INSTANCE);
		GETTERS.put(Byte.TYPE, ResultSetGetter.ByteResultSetGetter.INSTANCE);
		GETTERS.put(byte[].class, ResultSetGetter.ByteArrayResultSetGetter.INSTANCE);
		GETTERS.put(Character.class, ResultSetGetter.CharResultSetGetter.INSTANCE);
		GETTERS.put(Character.TYPE, ResultSetGetter.CharResultSetGetter.INSTANCE);
		GETTERS.put(BigDecimal.class, ResultSetGetter.BigDecimalResultSetGetter.INSTANCE);
		GETTERS.put(BigInteger.class, ResultSetGetter.BigIntegerResultSetGetter.INSTANCE);
		GETTERS.put(java.sql.Date.class, ResultSetGetter.DateResultSetGetter.INSTANCE);
		GETTERS.put(java.util.Date.class, ResultSetGetter.TimestampResultSetGetter.INSTANCE);
		GETTERS.put(java.sql.Timestamp.class, ResultSetGetter.TimestampResultSetGetter.INSTANCE);
		GETTERS.put(java.sql.Time.class, ResultSetGetter.TimeResultSetGetter.INSTANCE);
		GETTERS.put(java.sql.Blob.class, ResultSetGetter.ByteArrayResultSetGetter.INSTANCE);
		GETTERS.put(java.sql.Clob.class, ResultSetGetter.StringResultSetGetter.INSTANCE);
		GETTERS_CACHE.putAll(GETTERS);
	}

	public static Map<Class<?>, ResultSetGetter<?>> getGetters() {
		return GETTERS;
	}
	public static <T> ResultSetGetter<T> getGetter(Class<T> clazz) throws SQLException {
		ResultSetGetter<?> r = GETTERS_CACHE.get(clazz);
		if (r == null) {
			for (Entry<Class<?>, ResultSetGetter<?>> i : GETTERS.entrySet())
				if (i.getKey().isAssignableFrom(clazz)) {
					r = i.getValue();
					GETTERS_CACHE.put(clazz, r);
					return (ResultSetGetter<T>) r;
				}
			GETTERS_CACHE.put(clazz, r = new ResultSetGetter.ObjectResultSetGetter(clazz));
		}
		return (ResultSetGetter<T>) r;
	}

	public static String[] getUniqueColumnNames(ResultSetMetaData md, char columnNameDelim) throws SQLException {
		int count = md.getColumnCount();
		if (count == 1)
			return new String[] { md.getColumnLabel(1) };
		String r[] = new String[count];
		Map<String, Integer> namesAndPosition = new HashMap<String, Integer>();

		for (int i = 0; i < count; i++) {
			String name = md.getColumnLabel(i + 1);
			Integer existing = namesAndPosition.get(name);
			if (existing == null) {
				namesAndPosition.put(name, i);
				r[i] = name;
			} else {
				namesAndPosition.put(name, -1);
				if (existing >= 0) {
					String tableName = md.getTableName(existing + 1);
					if (SH.is(tableName)) {
						String replaceName = tableName + columnNameDelim + name;
						if (namesAndPosition.containsKey(replaceName)) {
							name = SH.getNextId(replaceName, namesAndPosition.keySet());
							namesAndPosition.put(name, i);
							r[i] = name;
							continue;
						}
						namesAndPosition.put(replaceName, i);
						r[existing] = replaceName;
					}
				}
				String tableName = md.getTableName(i + 1);
				String replaceName;
				if (SH.is(tableName)) {
					replaceName = tableName + columnNameDelim + name;
				} else
					replaceName = name;
				replaceName = SH.getNextId(replaceName, namesAndPosition.keySet(), 2);
				namesAndPosition.put(replaceName, i);
				r[i] = replaceName;

			}
		}
		return r;
	}
	public static String[] getUniqueColumnNamesStrict(ResultSetMetaData md, char columnNameDelim) throws SQLException {
		int count = md.getColumnCount();
		if (count == 1)
			return new String[] { md.getColumnLabel(1) };
		String r[] = new String[count];
		Map<String, Integer> namesAndPosition = new HashMap<String, Integer>();

		for (int i = 0; i < count; i++) {
			String name = md.getColumnLabel(i + 1);
			Integer existing = namesAndPosition.get(name);
			if (existing == null) {
				namesAndPosition.put(name, i);
				r[i] = name;
			} else {
				if (existing >= 0) {
					String tableName = md.getTableName(existing + 1);
					if (SH.isnt(tableName))
						throw new RuntimeException("Duplicate column name: " + name);
					String replaceName = tableName + columnNameDelim + name;
					if (namesAndPosition.put(replaceName, i) != null)
						throw new RuntimeException("Duplicate column name: " + replaceName);
					r[existing] = replaceName;
					namesAndPosition.put(name, -1);
				}
				String tableName = md.getTableName(i + 1);
				if (SH.isnt(tableName))
					throw new RuntimeException("Duplicate column name: " + name);
				String replaceName = tableName + columnNameDelim + name;
				if (namesAndPosition.put(replaceName, i) != null)
					throw new RuntimeException("Duplicate column name: " + replaceName);
				r[i] = replaceName;
			}
		}
		return r;
	}
	public static Table toTable(ResultSet result) throws SQLException {
		return toTable(result, '.', Integer.MAX_VALUE, (byte) 0);
	}
	public static Table toTable(ResultSet result, int limit, short options) throws SQLException {
		return toTable(result, '.', limit, options);
	}
	public static Table toTable(ResultSet result, char tableNameColumnDelim, int limit, short options) throws SQLException {
		String names[] = getUniqueColumnNames(result.getMetaData(), tableNameColumnDelim);
		Class<?>[] types = getColumnTypes(result.getMetaData());
		ResultSetGetter[] getters = getColumnGetters(types);
		return toTable(result, types, getters, names, limit, options);
	}

	public static ResultSetGetter[] getColumnGetters(Class<?>[] types) throws SQLException {
		ResultSetGetter<?>[] getters = new ResultSetGetter[types.length];
		for (int i = 0; i < types.length; i++) {
			getters[i] = getGetter(types[i]);
		}
		return getters;
	}

	public static Class<?>[] getColumnTypes(ResultSetMetaData md) throws SQLException {
		final int width = md.getColumnCount();
		final Class<?>[] types = new Class[width];
		for (int i = 0; i < width; i++) {
			try {
				String columnClassName = md.getColumnClassName(i + 1);
				if ("java.lang.Object".equals(columnClassName)) {
					types[i] = typeToClass(md.getColumnType(i + 1));
				} else
					types[i] = Class.forName(columnClassName);
			} catch (ClassNotFoundException e) {
				types[i] = Object.class;
			}
		}
		return types;
	}
	private static Class typeToClass(int columnType) {
		switch (columnType) {
			case Types.CHAR:
				return Character.class;
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
				return String.class;
			case Types.NUMERIC:
			case Types.DECIMAL:
				return BigDecimal.class;
			case Types.BIT:
				return Boolean.class;
			case Types.TINYINT:
				return Byte.class;
			case Types.SMALLINT:
				return Short.class;
			case Types.INTEGER:
				return Integer.class;
			case Types.BIGINT:
				return Long.class;
			case Types.REAL:
			case Types.FLOAT:
				return Float.class;
			case Types.DOUBLE:
				return Double.class;
			case Types.BINARY:
			case Types.VARBINARY:
			case Types.LONGVARBINARY:
				return Byte[].class;
			case Types.DATE:
				return java.sql.Date.class;
			case Types.TIME:
				return java.sql.Time.class;
			case Types.TIMESTAMP:
				return java.sql.Timestamp.class;
			default:
				return Object.class;
		}
	}

	public static Table toTable(ResultSet resultSet, Class<?>[] columnTypes, ResultSetGetter[] getters, String[] columnIds, int limit, short options) throws SQLException {
		return toTable(resultSet, columnTypes, getters, columnIds, limit, options, DEFAULT_LOG_LARGE_QUERY_PERIOD);
	}

	public static Table toTable(ResultSet resultSet, Class<?>[] columnTypes, ResultSetGetter[] getters, String[] columnIds, int limit, short options, int logLargeQueryPeriod)
			throws SQLException {
		ColumnarTable r = new ColumnarTable(columnTypes, columnIds);

		ResultSetMetaData md = resultSet.getMetaData();
		int columnCount = md.getColumnCount();
		if (columnCount > 0 && !MH.anyBits(options, TABLE_OPTIONS_SKIP_TITLE))
			r.setTitle(md.getTableName(1));

		OH.assertEq(columnCount, columnIds.length);

		int rowNum = 0;
		Thread thread = Thread.currentThread();
		CellCopier[] casters = new CellCopier[columnCount];
		for (int i = 0; i < columnCount; i++)
			casters[i] = newCellCopier(getters[i], i + 1, r.getColumnAt(i));
		while (resultSet.next() && rowNum < limit) {
			if ((rowNum & 1023) == 0 && Thread.interrupted())
				throw new SQLException("interrupted at row " + rowNum);
			ColumnarRow row = r.newEmptyRow();
			for (int i = 0; i < columnCount; i++)
				casters[i].copy(resultSet, row);
			r.getRows().add(row);
			rowNum++;
			if (rowNum % logLargeQueryPeriod == 0)
				LH.info(log, "Large query processed ", rowNum, " rows");
		}
		if (rowNum >= logLargeQueryPeriod)
			LH.info(log, "Large query totalled ", rowNum, " rows");
		return r;
	}

	private static CellCopier newCellCopier(ResultSetGetter source, int sourceFieldPos, ColumnarColumn target) {
		if (target instanceof ColumnarColumnPrimitive) {
			if (target.getClass() == ColumnarColumnLong.class && source.getReturnType() == Long.class)
				return new CellCopier_Long(sourceFieldPos, (ColumnarColumnLong) target);
			else if (target.getClass() == ColumnarColumnInt.class && source.getReturnType() == Integer.class)
				return new CellCopier_Int(sourceFieldPos, (ColumnarColumnInt) target);
			else if (target.getClass() == ColumnarColumnFloat.class && source.getReturnType() == Float.class)
				return new CellCopier_Float(sourceFieldPos, (ColumnarColumnFloat) target);
			else if (target.getClass() == ColumnarColumnDouble.class && source.getReturnType() == Double.class)
				return new CellCopier_Double(sourceFieldPos, (ColumnarColumnDouble) target);
			else if (target.getClass() == ColumnarColumnShort.class && source.getReturnType() == Short.class)
				return new CellCopier_Short(sourceFieldPos, (ColumnarColumnShort) target);
			else if (target.getClass() == ColumnarColumnByte.class && source.getReturnType() == Byte.class)
				return new CellCopier_Byte(sourceFieldPos, (ColumnarColumnByte) target);
			else if (target.getClass() == ColumnarColumnChar.class && source.getReturnType() == Character.class)
				return new CellCopier_Char(sourceFieldPos, (ColumnarColumnChar) target);
			else if (target.getClass() == ColumnarColumnBoolean.class && source.getReturnType() == Boolean.class)
				return new CellCopier_Boolean(sourceFieldPos, (ColumnarColumnBoolean) target);
		}
		return new CellCopierCaster(source, sourceFieldPos, target);
	}

	private static interface CellCopier {
		void copy(ResultSet source, ColumnarRow target) throws SQLException;
	}

	private static class CellCopierCaster implements CellCopier {

		final private ResultSetGetter source;
		final private ColumnarColumn target;
		final private Caster caster;
		final private int sourceFieldPos;
		final private int targetFieldPos;

		public CellCopierCaster(ResultSetGetter source, int sourceFieldPos, ColumnarColumn target) {
			this.source = source;
			this.sourceFieldPos = sourceFieldPos;
			this.targetFieldPos = target.getLocation();
			this.caster = target.getTypeCaster();
			this.target = target;
		}

		public void copy(ResultSet resultSet, ColumnarRow row) {
			row.putAt(targetFieldPos, caster.castNoThrow(source.get(resultSet, sourceFieldPos)));
		}

	}

	public static class CellCopier_Long implements CellCopier {
		final private int sourceFieldPos;
		private ColumnarColumnLong target;

		public CellCopier_Long(int sourceFieldPos, ColumnarColumnLong target) {
			this.target = target;
			this.sourceFieldPos = sourceFieldPos;
		}
		@Override
		public void copy(ResultSet source, ColumnarRow row) throws SQLException {
			long v = source.getLong(sourceFieldPos);
			if (!source.wasNull())
				this.target.setLongAtArrayIndex(row.getArrayIndex(), v);
		}
	}

	public static class CellCopier_Int implements CellCopier {
		final private int sourceFieldPos;
		private ColumnarColumnInt target;

		public CellCopier_Int(int sourceFieldPos, ColumnarColumnInt target) {
			this.target = target;
			this.sourceFieldPos = sourceFieldPos;
		}
		@Override
		public void copy(ResultSet source, ColumnarRow row) throws SQLException {
			int v = source.getInt(sourceFieldPos);
			if (!source.wasNull())
				this.target.setIntAtArrayIndex(row.getArrayIndex(), v);
		}
	}

	public static class CellCopier_Float implements CellCopier {
		final private int sourceFieldPos;
		private ColumnarColumnFloat target;

		public CellCopier_Float(int sourceFieldPos, ColumnarColumnFloat target) {
			this.target = target;
			this.sourceFieldPos = sourceFieldPos;
		}
		@Override
		public void copy(ResultSet source, ColumnarRow row) throws SQLException {
			float v = source.getFloat(sourceFieldPos);
			if (!source.wasNull())
				this.target.setFloatAtArrayIndex(row.getArrayIndex(), v);
		}
	}

	public static class CellCopier_Double implements CellCopier {
		final private int sourceFieldPos;
		private ColumnarColumnDouble target;

		public CellCopier_Double(int sourceFieldPos, ColumnarColumnDouble target) {
			this.target = target;
			this.sourceFieldPos = sourceFieldPos;
		}
		@Override
		public void copy(ResultSet source, ColumnarRow row) throws SQLException {
			double v = source.getDouble(sourceFieldPos);
			if (!source.wasNull())
				this.target.setDoubleAtArrayIndex(row.getArrayIndex(), v);
		}
	}

	public static class CellCopier_Short implements CellCopier {
		final private int sourceFieldPos;
		private ColumnarColumnShort target;

		public CellCopier_Short(int sourceFieldPos, ColumnarColumnShort target) {
			this.target = target;
			this.sourceFieldPos = sourceFieldPos;
		}
		@Override
		public void copy(ResultSet source, ColumnarRow row) throws SQLException {
			short v = source.getShort(sourceFieldPos);
			if (!source.wasNull())
				this.target.setShortAtArrayIndex(row.getArrayIndex(), v);
		}
	}

	public static class CellCopier_Byte implements CellCopier {
		final private int sourceFieldPos;
		private ColumnarColumnByte target;

		public CellCopier_Byte(int sourceFieldPos, ColumnarColumnByte target) {
			this.target = target;
			this.sourceFieldPos = sourceFieldPos;
		}
		@Override
		public void copy(ResultSet source, ColumnarRow row) throws SQLException {
			byte v = source.getByte(sourceFieldPos);
			if (!source.wasNull())
				this.target.setByteAtArrayIndex(row.getArrayIndex(), v);
		}
	}

	public static class CellCopier_Char implements CellCopier {
		final private int sourceFieldPos;
		private ColumnarColumnChar target;

		public CellCopier_Char(int sourceFieldPos, ColumnarColumnChar target) {
			this.target = target;
			this.sourceFieldPos = sourceFieldPos;
		}
		@Override
		public void copy(ResultSet source, ColumnarRow row) throws SQLException {
			String v = source.getString(sourceFieldPos);
			if (v != null && v.length() > 0)
				this.target.setCharAtArrayIndex(row.getArrayIndex(), v.charAt(0));
		}
	}

	public static class CellCopier_Boolean implements CellCopier {
		final private int sourceFieldPos;
		private ColumnarColumnBoolean target;

		public CellCopier_Boolean(int sourceFieldPos, ColumnarColumnBoolean target) {
			this.target = target;
			this.sourceFieldPos = sourceFieldPos;
		}
		@Override
		public void copy(ResultSet source, ColumnarRow row) throws SQLException {
			boolean v = source.getBoolean(sourceFieldPos);
			if (!source.wasNull())
				this.target.setBooleanAtArrayIndex(row.getArrayIndex(), v);
		}
	}

	/**
	 * @see {@link #toValuedList(ResultSet, ObjectGeneratorForClass, Map)} and assume the customGetters is empty
	 */
	public static <V extends Valued> List<V> toValuedList(ResultSet resultSet, ObjectGeneratorForClass<V> generator) throws Exception {
		return toValuedList(resultSet, generator, Collections.EMPTY_MAP);
	}

	/**
	 * Extracts one valued object for each row of supplied resultset. The metadata (column names) of the result set will be evaluated to determine mapping to fields. <BR>
	 * <B>Custom Converting:</B> Sometimes, it is necessary to convert particular values to non-primitive/trivial params in the valued objects. For example, lets suppose the result
	 * set has a TEXT column of comma delimited data named 'widgets'. Additionally, the valued object has a setter setWidgetsList(List<String> widgets). First, a custom generator
	 * for doing the parsing would be passed in the customGetters param with a descriptive key, let's say in this case 'STRING_LIST'. Then the sql queries select clause would need
	 * an AS clause in the format <I>SELECT widgets as '(STRING_LIST)widgetsList'</I>
	 * 
	 * @param resultSet
	 *            the result set to iterate over.
	 * @param generator
	 *            the generator used for creating instances of the valued objects to return
	 * @param customGetters
	 *            see notes above.
	 * @return values objects of supplied type in oder they appear in the result set
	 * @throws SQLException
	 * @throws InterruptedException
	 *             if the thread was interrupted while traversing
	 */
	public static <V extends Valued> List<V> toValuedList(ResultSet resultSet, ObjectGeneratorForClass<V> generator, Map<String, ResultSetGetter<?>> customGetters)
			throws SQLException, InterruptedException {
		return toValuedList(resultSet, generator, customGetters, DEFAULT_LOG_LARGE_QUERY_PERIOD, new ArrayList<V>());
	}
	public static <V extends Valued> List<V> toValuedList(ResultSet resultSet, ObjectGeneratorForClass<V> generator, Map<String, ResultSetGetter<?>> customGetters,
			int logLargeQueryPeriod, List<V> sink) throws SQLException, InterruptedException {

		ResultSetMetaData md = resultSet.getMetaData();
		int columnCount = md.getColumnCount();

		String[] paramNames = new String[columnCount];
		ResultSetGetter<?>[] getters = new ResultSetGetter[columnCount];
		for (int i = 0; i < columnCount; i++) {
			String columnLabel = md.getColumnLabel(i + 1);
			if (SH.startsWith(columnLabel, '(')) {
				int split = columnLabel.indexOf(')');
				if (split != -1) {
					String name = columnLabel.substring(1, split);
					ResultSetGetter<?> getter;
					getter = DEFAULT_GETTERS.get(name);
					if (getter == null && customGetters != null)
						getter = customGetters.get(name);
					if (getter == null) {
						throw new DetailedException("not such custom getter: " + name).set("built-in getters", DEFAULT_GETTERS).set("custom getters", customGetters);
					}
					getters[i] = getter;
					paramNames[i] = columnLabel.substring(split + 1);
					continue;
				}
			}
			paramNames[i] = columnLabel;
		}
		V valued = generator.nw();
		boolean ignoreNulls = valued instanceof PartialMessage;
		ValuedSchema<? extends Valued> schema = valued.askSchema();
		int rowNum = 0;
		Thread thread = Thread.currentThread();
		if (schema.askSupportsPids()) {
			final byte[] pids = new byte[columnCount];
			final boolean[] isPrimitive = new boolean[columnCount];
			for (int i = 0; i < columnCount; i++) {
				final byte pid = pids[i] = schema.askPid(paramNames[i]);
				final ValuedParam<?> vp = schema.askValuedParam(pid);
				if (getters[i] == null) {
					isPrimitive[i] = vp.isPrimitive();
					getters[i] = getGetter(vp.getReturnType());
				}
			}
			while (resultSet.next()) {
				if (thread.isInterrupted())
					throw new InterruptedException("row " + rowNum);
				if (valued == null)
					valued = generator.nw();
				for (int i = 0; i < columnCount; i++)
					getters[i].setToPid(resultSet, i + 1, pids[i], valued, ignoreNulls || isPrimitive[i]);
				sink.add(valued);
				valued = null;
				rowNum++;
				if (rowNum % logLargeQueryPeriod == 0)
					LH.info(log, "Large query processed ", rowNum, " ", OH.getSimpleName(generator.askType()));
			}
			if (rowNum >= logLargeQueryPeriod)
				LH.info(log, "Large query totalled ", rowNum, " ", OH.getSimpleName(generator.askType()));
		} else {
			for (int i = 0; i < columnCount; i++) {
				if (getters[i] == null)
					getters[i] = getGetter(schema.askClass(paramNames[i]));
			}
			while (resultSet.next()) {
				if (thread.isInterrupted())
					throw new InterruptedException("row " + rowNum);
				if (valued == null)
					valued = generator.nw();
				for (int i = 0; i < columnCount; i++)
					valued.put(paramNames[i], getters[i].get(resultSet, i + 1));
				sink.add(valued);
				valued = null;
				rowNum++;
				if (rowNum % logLargeQueryPeriod == 0)
					LH.info(log, "Large query processed ", rowNum, " ", OH.getSimpleName(generator.askType()));
			}
			if (rowNum >= logLargeQueryPeriod)
				LH.info(log, "Large query totalled ", rowNum, " ", OH.getSimpleName(generator.askType()));
		}
		return sink;
	}

	public static List<Tuple2<Class<?>, String>> parseColumns(String columnDefinitions) throws SQLException {

		String columnDefs[] = SH.split(',', columnDefinitions);
		List<Tuple2<Class<?>, String>> r = new ArrayList<Tuple2<Class<?>, String>>();
		for (int i = 0; i < columnDefs.length; i++) {
			String colDef = columnDefs[i].trim();
			try {
				String type = SH.beforeFirst(colDef, " ", null);
				String name = SH.afterFirst(colDef, " ", null);
				Class<?> columnClass = CH.getOrThrow(COLUMN_TYPES, type.trim().toLowerCase());
				r.add(new Tuple2<Class<?>, String>(columnClass, name.trim()));
			} catch (Exception e) {
				throw new SQLException("error parsing column definition " + i + ": " + colDef);
			}
		}
		return r;
	}

	public static <T> T toObjectNoThrow(ResultSet rs, Caster<T> returnCaster) throws SQLException {
		return toObject(rs, returnCaster, false);
	}
	public static <T> T toObject(ResultSet rs, Caster<T> returnCaster) throws SQLException {
		return toObject(rs, returnCaster, true);
	}
	public static <T> T toObject(ResultSet rs, Caster<T> returnCaster, boolean throwOnError) throws SQLException {
		if (rs.getMetaData().getColumnCount() != 1)
			if (throwOnError)
				throw new SQLException("incorrect number of columns: " + rs.getMetaData().getColumnCount());
			else
				return null;
		if (!rs.next())
			if (throwOnError)
				throw new SQLException("empty result set");
			else
				return null;
		final Object r = rs.getObject(1);
		if (rs.next())
			if (throwOnError)
				throw new SQLException("multiple rows");
			else
				return null;
		return returnCaster.cast(r);
	}
	public static <K, V> Map<K, V> toMap(ResultSet rs, Class<K> keyReturnType, Class<V> valueReturnType) throws SQLException {
		return toMap(rs, keyReturnType, valueReturnType, DEFAULT_LOG_LARGE_QUERY_PERIOD);
	}
	public static <K, V> Map<K, V> toMap(ResultSet rs, Class<K> keyReturnType, Class<V> valueReturnType, int logLargeQueryPeriod) throws SQLException {
		Map<K, V> r = new HashMap<K, V>();
		int rowNum = 0;
		Caster<K> keyCaster = OH.getCaster(keyReturnType);
		Caster<V> valueCaster = OH.getCaster(valueReturnType);
		while (rs.next()) {
			r.put(keyCaster.cast(rs.getObject(1)), valueCaster.cast(rs.getObject(2)));
			if (++rowNum % logLargeQueryPeriod == 0)
				LH.info(log, "Large query processed ", rowNum, " map entries");
		}
		if (rowNum >= logLargeQueryPeriod)
			LH.info(log, "Large query totalled ", rowNum, " map entries");
		return r;
	}
	public static <T> List<T> toList(ResultSet rs, Class<T> returnType) throws SQLException {
		return toList(rs, returnType, DEFAULT_LOG_LARGE_QUERY_PERIOD);
	}
	public static <T> List<T> toList(ResultSet rs, Class<T> returnType, int logLargeQueryPeriod) throws SQLException {
		ArrayList<T> r = new ArrayList<T>();
		int rowNum = 0;
		Caster<T> caster = OH.getCaster(returnType);
		while (rs.next()) {
			r.add(caster.cast(rs.getObject(1)));
			if (++rowNum % logLargeQueryPeriod == 0)
				LH.info(log, "Large query processed ", rowNum, " list entries");
		}
		if (rowNum >= logLargeQueryPeriod)
			LH.info(log, "Large query totalled ", rowNum, " list entries");
		return r;
	}
	public static <T> T[] toArray(ResultSet rs, Class<T> returnType) throws SQLException {
		return toArray(rs, returnType, DEFAULT_LOG_LARGE_QUERY_PERIOD);
	}
	public static <T> T[] toArray(ResultSet rs, Class<T> returnType, int logLargeQueryPeriod) throws SQLException {
		ArrayList<T> r = new ArrayList<T>();
		int rowNum = 0;
		Caster<T> caster = OH.getCaster(returnType);
		while (rs.next()) {
			r.add(caster.cast(rs.getObject(1)));
			if (++rowNum % logLargeQueryPeriod == 0)
				LH.info(log, "Large query processed ", rowNum, " array entries");
		}
		if (rowNum >= logLargeQueryPeriod)
			LH.info(log, "Large query totalled ", rowNum, " array entries");
		return AH.toArray(r, returnType);
	}

	final private static Map<String, Class<?>> COLUMN_TYPES = new HashMap<String, Class<?>>();
	static {
		COLUMN_TYPES.put("int", Integer.class);
		COLUMN_TYPES.put("char", Character.class);
		COLUMN_TYPES.put("string", String.class);
		COLUMN_TYPES.put("long", Long.class);
		COLUMN_TYPES.put("double", Double.class);
		COLUMN_TYPES.put("float", Float.class);
		COLUMN_TYPES.put("short", Short.class);
		COLUMN_TYPES.put("byte", Byte.class);
		COLUMN_TYPES.put("byte[]", byte[].class);
		COLUMN_TYPES.put("boolean", Boolean.class);
		COLUMN_TYPES.put("date", java.util.Date.class);
		COLUMN_TYPES.put("object", Object.class);
	}

	public static void prepareException(SQLException e) {
		SQLException n;
		while ((n = e.getNextException()) != null && e.getCause() == null && e.getNextException() != e) {
			e.initCause(n);
			e = n;
		}

	}

}
