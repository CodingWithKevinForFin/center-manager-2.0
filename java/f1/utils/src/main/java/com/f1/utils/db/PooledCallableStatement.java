package com.f1.utils.db;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.f1.utils.OH;

public class PooledCallableStatement implements CallableStatement {

	private CallableStatement inner;
	private PooledConnection connection;
	private String sql;
	private List<String> sqlBatch = null;
	private ArrayList<Object> params = new ArrayList<Object>();

	public PooledCallableStatement(PooledConnection connection, CallableStatement inner) {
		connection.addStatement(this);
		this.inner = inner;
		this.connection = connection;
	}
	public PooledCallableStatement(PooledConnection connection, CallableStatement inner, String sql) {
		this(connection, inner);
		this.sql = sql;
	}
	private void fireQuery(String sql) {
		List<DatabaseListener> listeners = connection.getDatabase().getDatabaseListeners();
		Object[] p = params.size() == 0 ? OH.EMPTY_OBJECT_ARRAY : params.toArray();
		for (int i = 0; i < listeners.size(); i++)
			listeners.get(i).onQuery(sql, p, connection);
	}
	private void fireQuery() {
		if (sqlBatch != null) {
			for (String sql : sqlBatch)
				fireQuery(sql);
		} else
			fireQuery(this.sql);
	}

	public ResultSet executeQuery(String sql) throws SQLException {
		fireQuery(sql);
		return connection.addResultSet(inner.executeQuery(sql));
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		return inner.unwrap(iface);
	}

	public ResultSet executeQuery() throws SQLException {
		fireQuery();
		return connection.addResultSet(inner.executeQuery());
	}

	public int executeUpdate(String sql) throws SQLException {
		fireQuery(sql);
		return inner.executeUpdate(sql);
	}

	public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
		inner.registerOutParameter(parameterIndex, sqlType);
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return inner.isWrapperFor(iface);
	}

	public int executeUpdate() throws SQLException {
		fireQuery();
		return inner.executeUpdate();
	}

	public void close() throws SQLException {
		inner.close();
	}

	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		inner.setNull(parameterIndex, sqlType);
	}

	public int getMaxFieldSize() throws SQLException {
		return inner.getMaxFieldSize();
	}

	public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
		inner.registerOutParameter(parameterIndex, sqlType, scale);
	}

	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		inner.setBoolean(parameterIndex, x);
	}

	public void setMaxFieldSize(int max) throws SQLException {
		inner.setMaxFieldSize(max);
	}

	public void setByte(int parameterIndex, byte x) throws SQLException {
		inner.setByte(parameterIndex, x);
	}

	public void setShort(int parameterIndex, short x) throws SQLException {
		inner.setShort(parameterIndex, x);
	}

	public int getMaxRows() throws SQLException {
		return inner.getMaxRows();
	}

	public boolean wasNull() throws SQLException {
		return inner.wasNull();
	}

	public void setInt(int parameterIndex, int x) throws SQLException {
		inner.setInt(parameterIndex, x);
	}

	public void setMaxRows(int max) throws SQLException {
		inner.setMaxRows(max);
	}

	public String getString(int parameterIndex) throws SQLException {
		return inner.getString(parameterIndex);
	}

	public void setLong(int parameterIndex, long x) throws SQLException {
		inner.setLong(parameterIndex, x);
	}

	public void setEscapeProcessing(boolean enable) throws SQLException {
		inner.setEscapeProcessing(enable);
	}

	public boolean getBoolean(int parameterIndex) throws SQLException {
		return inner.getBoolean(parameterIndex);
	}

	public void setFloat(int parameterIndex, float x) throws SQLException {
		inner.setFloat(parameterIndex, x);
	}

	public int getQueryTimeout() throws SQLException {
		return inner.getQueryTimeout();
	}

	public void setDouble(int parameterIndex, double x) throws SQLException {
		inner.setDouble(parameterIndex, x);
	}

	public byte getByte(int parameterIndex) throws SQLException {
		return inner.getByte(parameterIndex);
	}

	public void setQueryTimeout(int seconds) throws SQLException {
		inner.setQueryTimeout(seconds);
	}

	public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
		inner.setBigDecimal(parameterIndex, x);
	}

	public short getShort(int parameterIndex) throws SQLException {
		return inner.getShort(parameterIndex);
	}

	public void cancel() throws SQLException {
		inner.cancel();
	}

	public void setString(int parameterIndex, String x) throws SQLException {
		inner.setString(parameterIndex, x);
	}

	public int getInt(int parameterIndex) throws SQLException {
		return inner.getInt(parameterIndex);
	}

	public SQLWarning getWarnings() throws SQLException {
		return inner.getWarnings();
	}

	public long getLong(int parameterIndex) throws SQLException {
		return inner.getLong(parameterIndex);
	}

	public void setBytes(int parameterIndex, byte[] x) throws SQLException {
		inner.setBytes(parameterIndex, x);
	}

	public float getFloat(int parameterIndex) throws SQLException {
		return inner.getFloat(parameterIndex);
	}

	public void clearWarnings() throws SQLException {
		inner.clearWarnings();
	}

	public void setDate(int parameterIndex, Date x) throws SQLException {
		inner.setDate(parameterIndex, x);
	}

	public void setCursorName(String name) throws SQLException {
		inner.setCursorName(name);
	}

	public double getDouble(int parameterIndex) throws SQLException {
		return inner.getDouble(parameterIndex);
	}

	public void setTime(int parameterIndex, Time x) throws SQLException {
		inner.setTime(parameterIndex, x);
	}

	public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
		return inner.getBigDecimal(parameterIndex, scale);
	}

	public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
		inner.setTimestamp(parameterIndex, x);
	}

	public boolean execute(String sql) throws SQLException {
		fireQuery(sql);
		return inner.execute(sql);
	}

	public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
		inner.setAsciiStream(parameterIndex, x, length);
	}

	public byte[] getBytes(int parameterIndex) throws SQLException {
		return inner.getBytes(parameterIndex);
	}

	public Date getDate(int parameterIndex) throws SQLException {
		return inner.getDate(parameterIndex);
	}

	public ResultSet getResultSet() throws SQLException {
		return connection.addResultSet(inner.getResultSet());
	}

	public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
		inner.setUnicodeStream(parameterIndex, x, length);
	}

	public Time getTime(int parameterIndex) throws SQLException {
		return inner.getTime(parameterIndex);
	}

	public int getUpdateCount() throws SQLException {
		return inner.getUpdateCount();
	}

	public Timestamp getTimestamp(int parameterIndex) throws SQLException {
		return inner.getTimestamp(parameterIndex);
	}

	public boolean getMoreResults() throws SQLException {
		return inner.getMoreResults();
	}

	public Object getObject(int parameterIndex) throws SQLException {
		return inner.getObject(parameterIndex);
	}

	public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
		inner.setBinaryStream(parameterIndex, x, length);
	}

	public void setFetchDirection(int direction) throws SQLException {
		inner.setFetchDirection(direction);
	}

	public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
		return inner.getBigDecimal(parameterIndex);
	}

	public void clearParameters() throws SQLException {
		inner.clearParameters();
	}

	public int getFetchDirection() throws SQLException {
		return inner.getFetchDirection();
	}

	public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
		inner.setObject(parameterIndex, x, targetSqlType);
	}

	public Object getObject(int parameterIndex, Map<String, Class<?>> map) throws SQLException {
		return inner.getObject(parameterIndex, map);
	}

	public void setFetchSize(int rows) throws SQLException {
		inner.setFetchSize(rows);
	}

	public int getFetchSize() throws SQLException {
		return inner.getFetchSize();
	}

	public void setObject(int parameterIndex, Object x) throws SQLException {
		inner.setObject(parameterIndex, x);
	}

	public Ref getRef(int parameterIndex) throws SQLException {
		return inner.getRef(parameterIndex);
	}

	public int getResultSetConcurrency() throws SQLException {
		return inner.getResultSetConcurrency();
	}

	public int getResultSetType() throws SQLException {
		return inner.getResultSetType();
	}

	public Blob getBlob(int parameterIndex) throws SQLException {
		return inner.getBlob(parameterIndex);
	}

	public void addBatch(String sql) throws SQLException {
		if (sqlBatch == null)
			sqlBatch = new ArrayList<String>();
		sqlBatch.add(sql);
		inner.addBatch(sql);
	}

	public Clob getClob(int parameterIndex) throws SQLException {
		return inner.getClob(parameterIndex);
	}

	public void clearBatch() throws SQLException {
		this.sqlBatch = null;
		inner.clearBatch();
	}

	public boolean execute() throws SQLException {
		fireQuery();
		return inner.execute();
	}

	public Array getArray(int parameterIndex) throws SQLException {
		return inner.getArray(parameterIndex);
	}

	public int[] executeBatch() throws SQLException {
		for (String sql : this.sqlBatch)
			fireQuery(sql);
		return inner.executeBatch();
	}

	public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
		return inner.getDate(parameterIndex, cal);
	}

	public void addBatch() throws SQLException {
		inner.addBatch();
	}

	public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
		inner.setCharacterStream(parameterIndex, reader, length);
	}

	public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
		return inner.getTime(parameterIndex, cal);
	}

	public void setRef(int parameterIndex, Ref x) throws SQLException {
		inner.setRef(parameterIndex, x);
	}

	public Connection getConnection() throws SQLException {
		return inner.getConnection();
	}

	public Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
		return inner.getTimestamp(parameterIndex, cal);
	}

	public void setBlob(int parameterIndex, Blob x) throws SQLException {
		inner.setBlob(parameterIndex, x);
	}

	public void registerOutParameter(int parameterIndex, int sqlType, String typeName) throws SQLException {
		inner.registerOutParameter(parameterIndex, sqlType, typeName);
	}

	public void setClob(int parameterIndex, Clob x) throws SQLException {
		inner.setClob(parameterIndex, x);
	}

	public boolean getMoreResults(int current) throws SQLException {
		return inner.getMoreResults(current);
	}

	public void setArray(int parameterIndex, Array x) throws SQLException {
		inner.setArray(parameterIndex, x);
	}

	public ResultSetMetaData getMetaData() throws SQLException {
		return inner.getMetaData();
	}

	public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
		inner.registerOutParameter(parameterName, sqlType);
	}

	public ResultSet getGeneratedKeys() throws SQLException {
		return connection.addResultSet(inner.getGeneratedKeys());
	}

	public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
		inner.setDate(parameterIndex, x, cal);
	}

	public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
		fireQuery(sql);
		return inner.executeUpdate(sql, autoGeneratedKeys);
	}

	public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
		inner.setTime(parameterIndex, x, cal);
	}

	public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
		inner.registerOutParameter(parameterName, sqlType, scale);
	}

	public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
		fireQuery(sql);
		return inner.executeUpdate(sql, columnIndexes);
	}

	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
		inner.setTimestamp(parameterIndex, x, cal);
	}

	public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
		inner.registerOutParameter(parameterName, sqlType, typeName);
	}

	public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
		inner.setNull(parameterIndex, sqlType, typeName);
	}

	public int executeUpdate(String sql, String[] columnNames) throws SQLException {
		fireQuery(sql);
		return inner.executeUpdate(sql, columnNames);
	}

	public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
		fireQuery(sql);
		return inner.execute(sql, autoGeneratedKeys);
	}

	public URL getURL(int parameterIndex) throws SQLException {
		return inner.getURL(parameterIndex);
	}

	public void setURL(int parameterIndex, URL x) throws SQLException {
		inner.setURL(parameterIndex, x);
	}

	public void setURL(String parameterName, URL val) throws SQLException {
		inner.setURL(parameterName, val);
	}

	public ParameterMetaData getParameterMetaData() throws SQLException {
		return inner.getParameterMetaData();
	}

	public void setNull(String parameterName, int sqlType) throws SQLException {
		inner.setNull(parameterName, sqlType);
	}

	public void setRowId(int parameterIndex, RowId x) throws SQLException {
		inner.setRowId(parameterIndex, x);
	}

	public boolean execute(String sql, int[] columnIndexes) throws SQLException {
		fireQuery(sql);
		return inner.execute(sql, columnIndexes);
	}

	public void setBoolean(String parameterName, boolean x) throws SQLException {
		inner.setBoolean(parameterName, x);
	}

	public void setNString(int parameterIndex, String value) throws SQLException {
		inner.setNString(parameterIndex, value);
	}

	public void setByte(String parameterName, byte x) throws SQLException {
		inner.setByte(parameterName, x);
	}

	public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
		inner.setNCharacterStream(parameterIndex, value, length);
	}

	public void setShort(String parameterName, short x) throws SQLException {
		inner.setShort(parameterName, x);
	}

	public boolean execute(String sql, String[] columnNames) throws SQLException {
		return inner.execute(sql, columnNames);
	}

	public void setInt(String parameterName, int x) throws SQLException {
		inner.setInt(parameterName, x);
	}

	public void setNClob(int parameterIndex, NClob value) throws SQLException {
		inner.setNClob(parameterIndex, value);
	}

	public void setLong(String parameterName, long x) throws SQLException {
		inner.setLong(parameterName, x);
	}

	public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
		inner.setClob(parameterIndex, reader, length);
	}

	public void setFloat(String parameterName, float x) throws SQLException {
		inner.setFloat(parameterName, x);
	}

	public void setDouble(String parameterName, double x) throws SQLException {
		inner.setDouble(parameterName, x);
	}

	public int getResultSetHoldability() throws SQLException {
		return inner.getResultSetHoldability();
	}

	public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
		inner.setBlob(parameterIndex, inputStream, length);
	}

	public boolean isClosed() throws SQLException {
		return inner.isClosed();
	}

	public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
		inner.setBigDecimal(parameterName, x);
	}

	public void setPoolable(boolean poolable) throws SQLException {
		inner.setPoolable(poolable);
	}

	public void setString(String parameterName, String x) throws SQLException {
		inner.setString(parameterName, x);
	}

	public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
		inner.setNClob(parameterIndex, reader, length);
	}

	public boolean isPoolable() throws SQLException {
		return inner.isPoolable();
	}

	public void setBytes(String parameterName, byte[] x) throws SQLException {
		inner.setBytes(parameterName, x);
	}

	public void setDate(String parameterName, Date x) throws SQLException {
		inner.setDate(parameterName, x);
	}

	public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
		inner.setSQLXML(parameterIndex, xmlObject);
	}

	public void setTime(String parameterName, Time x) throws SQLException {
		inner.setTime(parameterName, x);
	}

	public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
		inner.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
		setPooledParam(parameterIndex, x);
	}

	private void setPooledParam(int parameterIndex, Object x) {
		while (params.size() < parameterIndex)
			params.add(null);
		params.set(parameterIndex - 1, x);
	}
	public void setTimestamp(String parameterName, Timestamp x) throws SQLException {
		inner.setTimestamp(parameterName, x);
	}

	public void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {
		inner.setAsciiStream(parameterName, x, length);
	}

	public void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException {
		inner.setBinaryStream(parameterName, x, length);
	}

	public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
		inner.setAsciiStream(parameterIndex, x, length);
	}

	public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
		inner.setObject(parameterName, x, targetSqlType, scale);
	}

	public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
		inner.setBinaryStream(parameterIndex, x, length);
	}

	public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
		inner.setCharacterStream(parameterIndex, reader, length);
	}

	public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
		inner.setObject(parameterName, x, targetSqlType);
	}

	public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
		inner.setAsciiStream(parameterIndex, x);
	}

	public void setObject(String parameterName, Object x) throws SQLException {
		inner.setObject(parameterName, x);
	}

	public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
		inner.setBinaryStream(parameterIndex, x);
	}

	public void setCharacterStream(String parameterName, Reader reader, int length) throws SQLException {
		inner.setCharacterStream(parameterName, reader, length);
	}

	public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
		inner.setCharacterStream(parameterIndex, reader);
	}

	public void setDate(String parameterName, Date x, Calendar cal) throws SQLException {
		inner.setDate(parameterName, x, cal);
	}

	public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
		inner.setNCharacterStream(parameterIndex, value);
	}

	public void setTime(String parameterName, Time x, Calendar cal) throws SQLException {
		inner.setTime(parameterName, x, cal);
	}

	public void setClob(int parameterIndex, Reader reader) throws SQLException {
		inner.setClob(parameterIndex, reader);
	}

	public void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException {
		inner.setTimestamp(parameterName, x, cal);
	}

	public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
		inner.setBlob(parameterIndex, inputStream);
	}

	public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
		inner.setNull(parameterName, sqlType, typeName);
	}

	public void setNClob(int parameterIndex, Reader reader) throws SQLException {
		inner.setNClob(parameterIndex, reader);
	}

	public String getString(String parameterName) throws SQLException {
		return inner.getString(parameterName);
	}

	public boolean getBoolean(String parameterName) throws SQLException {
		return inner.getBoolean(parameterName);
	}

	public byte getByte(String parameterName) throws SQLException {
		return inner.getByte(parameterName);
	}

	public short getShort(String parameterName) throws SQLException {
		return inner.getShort(parameterName);
	}

	public int getInt(String parameterName) throws SQLException {
		return inner.getInt(parameterName);
	}

	public long getLong(String parameterName) throws SQLException {
		return inner.getLong(parameterName);
	}

	public float getFloat(String parameterName) throws SQLException {
		return inner.getFloat(parameterName);
	}

	public double getDouble(String parameterName) throws SQLException {
		return inner.getDouble(parameterName);
	}

	public byte[] getBytes(String parameterName) throws SQLException {
		return inner.getBytes(parameterName);
	}

	public Date getDate(String parameterName) throws SQLException {
		return inner.getDate(parameterName);
	}

	public Time getTime(String parameterName) throws SQLException {
		return inner.getTime(parameterName);
	}

	public Timestamp getTimestamp(String parameterName) throws SQLException {
		return inner.getTimestamp(parameterName);
	}

	public Object getObject(String parameterName) throws SQLException {
		return inner.getObject(parameterName);
	}

	public BigDecimal getBigDecimal(String parameterName) throws SQLException {
		return inner.getBigDecimal(parameterName);
	}

	public Object getObject(String parameterName, Map<String, Class<?>> map) throws SQLException {
		return inner.getObject(parameterName, map);
	}

	public Ref getRef(String parameterName) throws SQLException {
		return inner.getRef(parameterName);
	}

	public Blob getBlob(String parameterName) throws SQLException {
		return inner.getBlob(parameterName);
	}

	public Clob getClob(String parameterName) throws SQLException {
		return inner.getClob(parameterName);
	}

	public Array getArray(String parameterName) throws SQLException {
		return inner.getArray(parameterName);
	}

	public Date getDate(String parameterName, Calendar cal) throws SQLException {
		return inner.getDate(parameterName, cal);
	}

	public Time getTime(String parameterName, Calendar cal) throws SQLException {
		return inner.getTime(parameterName, cal);
	}

	public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
		return inner.getTimestamp(parameterName, cal);
	}

	public URL getURL(String parameterName) throws SQLException {
		return inner.getURL(parameterName);
	}

	public RowId getRowId(int parameterIndex) throws SQLException {
		return inner.getRowId(parameterIndex);
	}

	public RowId getRowId(String parameterName) throws SQLException {
		return inner.getRowId(parameterName);
	}

	public void setRowId(String parameterName, RowId x) throws SQLException {
		inner.setRowId(parameterName, x);
	}

	public void setNString(String parameterName, String value) throws SQLException {
		inner.setNString(parameterName, value);
	}

	public void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException {
		inner.setNCharacterStream(parameterName, value, length);
	}

	public void setNClob(String parameterName, NClob value) throws SQLException {
		inner.setNClob(parameterName, value);
	}

	public void setClob(String parameterName, Reader reader, long length) throws SQLException {
		inner.setClob(parameterName, reader, length);
	}

	public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {
		inner.setBlob(parameterName, inputStream, length);
	}

	public void setNClob(String parameterName, Reader reader, long length) throws SQLException {
		inner.setNClob(parameterName, reader, length);
	}

	public NClob getNClob(int parameterIndex) throws SQLException {
		return inner.getNClob(parameterIndex);
	}

	public NClob getNClob(String parameterName) throws SQLException {
		return inner.getNClob(parameterName);
	}

	public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
		inner.setSQLXML(parameterName, xmlObject);
	}

	public SQLXML getSQLXML(int parameterIndex) throws SQLException {
		return inner.getSQLXML(parameterIndex);
	}

	public SQLXML getSQLXML(String parameterName) throws SQLException {
		return inner.getSQLXML(parameterName);
	}

	public String getNString(int parameterIndex) throws SQLException {
		return inner.getNString(parameterIndex);
	}

	public String getNString(String parameterName) throws SQLException {
		return inner.getNString(parameterName);
	}

	public Reader getNCharacterStream(int parameterIndex) throws SQLException {
		return inner.getNCharacterStream(parameterIndex);
	}

	public Reader getNCharacterStream(String parameterName) throws SQLException {
		return inner.getNCharacterStream(parameterName);
	}

	public Reader getCharacterStream(int parameterIndex) throws SQLException {
		return inner.getCharacterStream(parameterIndex);
	}

	public Reader getCharacterStream(String parameterName) throws SQLException {
		return inner.getCharacterStream(parameterName);
	}

	public void setBlob(String parameterName, Blob x) throws SQLException {
		inner.setBlob(parameterName, x);
	}

	public void setClob(String parameterName, Clob x) throws SQLException {
		inner.setClob(parameterName, x);
	}

	public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {
		inner.setAsciiStream(parameterName, x, length);
	}

	public void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException {
		inner.setBinaryStream(parameterName, x, length);
	}

	public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
		inner.setCharacterStream(parameterName, reader, length);
	}

	public void setAsciiStream(String parameterName, InputStream x) throws SQLException {
		inner.setAsciiStream(parameterName, x);
	}

	public void setBinaryStream(String parameterName, InputStream x) throws SQLException {
		inner.setBinaryStream(parameterName, x);
	}

	public void setCharacterStream(String parameterName, Reader reader) throws SQLException {
		inner.setCharacterStream(parameterName, reader);
	}

	public void setNCharacterStream(String parameterName, Reader value) throws SQLException {
		inner.setNCharacterStream(parameterName, value);
	}

	public void setClob(String parameterName, Reader reader) throws SQLException {
		inner.setClob(parameterName, reader);
	}

	public void setBlob(String parameterName, InputStream inputStream) throws SQLException {
		inner.setBlob(parameterName, inputStream);
	}

	public void setNClob(String parameterName, Reader reader) throws SQLException {
		inner.setNClob(parameterName, reader);
	}

	public void closeOnCompletion() throws SQLException {
	}

	public boolean isCloseOnCompletion() throws SQLException {
		return false;
	}

	public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException {
		return null;
	}

	public <T> T getObject(String parameterName, Class<T> type) throws SQLException {
		return null;
	}
}
