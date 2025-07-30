package com.f1.ami.amidb.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.logging.Logger;

import com.f1.utils.AH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.ToDoException;

public class AmiDbJdbcPreparedStatement extends AmiDbJdbcStatement implements PreparedStatement {

	private String params[];
	private String parts[];
	private String sql;
	private static final Logger log = LH.get();

	public AmiDbJdbcPreparedStatement(AmiDbJdbcConnection connection, String sql) {
		super(connection);
		this.sql = sql;
		if (sql.length() == 0) {
			this.parts = new String[] { "" };
		} else
			this.parts = SH.split('?', sql);
		this.params = new String[parts.length - 1];

	}

	@Override
	public boolean execute() throws SQLException {
		return super.execute(substituteParams());
	}

	@Override
	public ResultSet executeQuery() throws SQLException {
		return super.executeQuery(substituteParams());
	}

	@Override
	public int executeUpdate() throws SQLException {
		return super.executeUpdate(substituteParams());
	}
	@Override
	public void clearParameters() throws SQLException {
		AH.fill(params, null);
	}
	@Override
	public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
		setObject(parameterIndex, SqlTypesMap.toCaster(targetSqlType).cast(x));
	}
	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		return this.getResultSet().getMetaData();
	}

	@Override
	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		set(parameterIndex, null);
	}

	@Override
	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		set(parameterIndex, x);
	}

	@Override
	public void setByte(int parameterIndex, byte x) throws SQLException {
		set(parameterIndex, x);
	}

	@Override
	public void setShort(int parameterIndex, short x) throws SQLException {
		set(parameterIndex, x);
	}

	@Override
	public void setInt(int parameterIndex, int x) throws SQLException {
		set(parameterIndex, x);
	}

	@Override
	public void setLong(int parameterIndex, long x) throws SQLException {
		set(parameterIndex, x);
	}

	@Override
	public void setFloat(int parameterIndex, float x) throws SQLException {
		set(parameterIndex, x);
	}

	@Override
	public void setDouble(int parameterIndex, double x) throws SQLException {
		set(parameterIndex, x);
	}

	@Override
	public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
		set(parameterIndex, x);
	}

	@Override
	public void setString(int parameterIndex, String x) throws SQLException {
		set(parameterIndex, x);
	}

	@Override
	public void setBytes(int parameterIndex, byte[] x) throws SQLException {
		set(parameterIndex, x);
	}

	@Override
	public void setDate(int parameterIndex, Date x) throws SQLException {
		set(parameterIndex, x);
	}

	@Override
	public void setTime(int parameterIndex, Time x) throws SQLException {
		set(parameterIndex, x);
	}

	@Override
	public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
		set(parameterIndex, x);
	}

	@Override
	public void setObject(int parameterIndex, Object x) throws SQLException {
		set(parameterIndex, x);

	}

	@Override
	public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
		set(parameterIndex, x, cal);
	}

	@Override
	public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
		set(parameterIndex, x, cal);
	}

	@Override
	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
		set(parameterIndex, x, cal);
	}

	@Override
	public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
		set(parameterIndex, null);
	}
	@Override
	public void setNString(int parameterIndex, String value) throws SQLException {
		set(parameterIndex, value);
	}

	@Override
	public void setURL(int parameterIndex, URL x) throws SQLException {
		throw unsupported("setURL");

	}

	@Override
	public ParameterMetaData getParameterMetaData() throws SQLException {
		throw unsupported("getParameterMetaData");
	}

	@Override
	public void setRowId(int parameterIndex, RowId x) throws SQLException {
		throw unsupported("setRowId");
	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
		throw unsupported("setNCharacterStream");
	}

	@Override
	public void setNClob(int parameterIndex, NClob value) throws SQLException {
		throw unsupported("setNClob");
	}

	@Override
	public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
		throw unsupported("setNClob2");
	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
		throw unsupported("setBlob");
	}

	@Override
	public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
		throw unsupported("setNClob");
	}

	@Override
	public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
		throw unsupported("setSQLMXML");
	}

	@Override
	public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
		throw unsupported("setObject");
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
		throw unsupported("setAsciiStream");

	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
		throw unsupported("setBinaryStream");
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
		throw unsupported("setCharacterStream");
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
		throw unsupported("setAsciiStream");
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
		throw unsupported("setBinaryStream");
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
		throw unsupported("setCharacterStream");
	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
		throw unsupported("setNCharacterStream");
	}

	@Override
	public void setClob(int parameterIndex, Reader reader) throws SQLException {
		throw unsupported("setClob");
	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
		throw unsupported("setBlob");
	}

	@Override
	public void setNClob(int parameterIndex, Reader reader) throws SQLException {
		throw unsupported("setNClob");
	}
	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
		throw unsupported("setAsciiStream");
	}

	@Override
	public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
		throw unsupported("setUnicodeStream");
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
		throw unsupported("setBinaryStream");
	}
	@Override
	public void addBatch() throws SQLException {
		throw unsupported("addBatch");
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
		throw unsupported("setCharacterStream");
	}

	@Override
	public void setRef(int parameterIndex, Ref x) throws SQLException {
		throw unsupported("setRef");
	}

	@Override
	public void setBlob(int parameterIndex, Blob x) throws SQLException {
		throw unsupported("setBlob");
	}

	@Override
	public void setClob(int parameterIndex, Clob x) throws SQLException {
		throw unsupported("setClob");
	}

	@Override
	public void setArray(int parameterIndex, Array x) throws SQLException {
		throw unsupported("setArray");
	}
	private UnsupportedOperationException unsupported(String msg) {
		LH.info(log, getClass().getSimpleName() + ":Going to throw unsupported operation exception: " + msg);
		return new UnsupportedOperationException(msg);
	}

	private void set(int parameterIndex, Object value) {
		if (parameterIndex > this.params.length || parameterIndex <= 0)
			throw new IndexOutOfBoundsException("Param index must be between 1 and " + this.params.length + ": " + parameterIndex);
		this.params[parameterIndex - 1] = toString(value);
	}
	private void set(int parameterIndex, Object x, Calendar cal) {
		throw new ToDoException("Support calendar");
	}
	private String toString(Object x) {
		if (x instanceof Long)
			return x.toString() + 'L';
		if (x instanceof Number)
			return x.toString();
		if (x == null)
			return "null";
		String s = x.toString();
		return SH.doubleQuote(s);
	}
	private String substituteParams() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < this.params.length; i++)
			sb.append(this.parts[i]).append(this.params[i]);
		sb.append(this.parts[this.parts.length - 1]);
		return sb.toString();
	}
}
