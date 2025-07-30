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
import java.sql.Ref;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.ds.AmiTableResultSet;
import com.f1.base.Caster;
import com.f1.base.DateMillis;
import com.f1.base.DateNanos;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.ToDoException;
import com.f1.utils.casters.Caster_Array;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Byte;
import com.f1.utils.casters.Caster_DateMillis;
import com.f1.utils.casters.Caster_DateNanos;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.casters.Caster_Float;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_Long;
import com.f1.utils.casters.Caster_Short;
import com.f1.utils.casters.Caster_Simple;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.casters.Caster_Timestamp;

public class TableResultSet implements AmiTableResultSet {

	private Table t;
	private int rowPos = -1;
	private boolean wasNull;
	private Row row;
	final private Statement statement;
	final private ResultSetMetaData metadata;
	final private TableResultSet nextResultSet;
	private static final Logger log = LH.get();

	public TableResultSet(Statement statement, Table t) {
		this(statement, t, null);

	}
	public TableResultSet(Statement statement, Table t, TableResultSet next) {
		this.statement = statement;
		this.t = t;
		this.metadata = new TableResultSetMetaData(t);
		this.nextResultSet = next;
	}

	public TableResultSet nextResultSet() {
		return this.nextResultSet;
	}

	@Override
	public int findColumn(String columnLabel) throws SQLException {
		return t.getColumn(columnLabel).getLocation() + 1;
	}

	@Override
	public boolean next() throws SQLException {
		return absolute(rowPos + 1);
	}
	@Override
	public boolean relative(int rows) throws SQLException {
		return absolute(rowPos + rows);
	}

	@Override
	public boolean previous() throws SQLException {
		return absolute(rowPos - 1);
	}

	@Override
	public void close() throws SQLException {
		this.t = null;
	}

	@Override
	public boolean wasNull() throws SQLException {
		return wasNull;
	}
	@Override
	public boolean absolute(int row) throws SQLException {
		if (row < 0 || row >= t.getSize())
			return false;
		this.row = t.getRows().get(rowPos = row);
		return true;
	}
	@Override
	public boolean isClosed() throws SQLException {
		return t == null;
	}
	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		return this.metadata;
	}

	@Override
	public boolean isBeforeFirst() throws SQLException {
		return rowPos == -1;
	}

	@Override
	public boolean isAfterLast() throws SQLException {
		return rowPos == this.t.getSize();
	}

	@Override
	public boolean isFirst() throws SQLException {
		return rowPos == 0;
	}

	@Override
	public boolean isLast() throws SQLException {
		return rowPos == this.t.getSize() - 1;
	}

	@Override
	public void beforeFirst() throws SQLException {
		this.rowPos = -1;
		this.row = null;
	}

	@Override
	public void afterLast() throws SQLException {
		this.rowPos = this.t.getSize();
		this.row = null;

	}

	@Override
	public boolean first() throws SQLException {
		return absolute(0);
	}

	@Override
	public boolean last() throws SQLException {
		return absolute(this.t.getSize() - 1);
	}

	@Override
	public int getRow() throws SQLException {
		return this.rowPos;
	}

	@Override
	public Statement getStatement() throws SQLException {
		return this.statement;
	}

	@Override
	public String getString(int columnIndex) throws SQLException {
		return get(columnIndex, Caster_String.INSTANCE, null);
	}

	@Override
	public boolean getBoolean(int columnIndex) throws SQLException {
		return get(columnIndex, Caster_Boolean.INSTANCE, false);
	}

	@Override
	public byte getByte(int columnIndex) throws SQLException {
		return get(columnIndex, Caster_Byte.INSTANCE, (byte) 0);
	}

	@Override
	public short getShort(int columnIndex) throws SQLException {
		return get(columnIndex, Caster_Short.INSTANCE, (short) 0);
	}

	@Override
	public int getInt(int columnIndex) throws SQLException {
		return get(columnIndex, Caster_Integer.INSTANCE, 0);
	}

	@Override
	public long getLong(int columnIndex) throws SQLException {
		return get(columnIndex, Caster_Long.INSTANCE, 0L);
	}

	@Override
	public float getFloat(int columnIndex) throws SQLException {
		return get(columnIndex, Caster_Float.INSTANCE, 0F);
	}

	@Override
	public double getDouble(int columnIndex) throws SQLException {
		return get(columnIndex, Caster_Double.INSTANCE, 0D);
	}

	@Override
	public byte[] getBytes(int columnIndex) throws SQLException {
		return get(columnIndex, Caster_Array.BYTE_PRIMITIVE, null);
	}
	@Override
	public Object getObject(int columnIndex) throws SQLException {
		return get(columnIndex, Caster_Simple.OBJECT, null);
	}
	@Override
	public String getNString(int columnIndex) throws SQLException {
		return getString(columnIndex);
	}

	private static final Caster<Date> CASTER_DATE = OH.getCaster(Date.class);
	private static final Caster<Time> CASTER_TIME = OH.getCaster(Time.class);
	private static final Caster<Timestamp> CASTER_TIMESTAMP = Caster_Timestamp.INSTANCE;

	@Override
	public Date getDate(int columnIndex) throws SQLException {
		return getDate(columnIndex, CASTER_DATE, null, null);
	}

	@Override
	public Time getTime(int columnIndex) throws SQLException {
		return getDate(columnIndex, CASTER_TIME, null, null);
	}

	@Override
	public Timestamp getTimestamp(int columnIndex) throws SQLException {
		return getDate(columnIndex, CASTER_TIMESTAMP, null, null);
	}
	@Override
	public Date getDate(int columnIndex, Calendar cal) throws SQLException {
		return getDate(columnIndex, CASTER_DATE, null, cal);
	}

	@Override
	public Time getTime(int columnIndex, Calendar cal) throws SQLException {
		return getDate(columnIndex, CASTER_TIME, null, cal);
	}

	@Override
	public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
		return getDate(columnIndex, CASTER_TIMESTAMP, null, cal);
	}

	@Override
	public String getString(String columnIndex) throws SQLException {
		return get(columnIndex, Caster_String.INSTANCE, null);
	}

	@Override
	public boolean getBoolean(String columnIndex) throws SQLException {
		return get(columnIndex, Caster_Boolean.INSTANCE, false);
	}

	@Override
	public byte getByte(String columnIndex) throws SQLException {
		return get(columnIndex, Caster_Byte.INSTANCE, (byte) 0);
	}

	@Override
	public short getShort(String columnIndex) throws SQLException {
		return get(columnIndex, Caster_Short.INSTANCE, (short) 0);
	}

	@Override
	public int getInt(String columnIndex) throws SQLException {
		return get(columnIndex, Caster_Integer.INSTANCE, 0);
	}

	@Override
	public long getLong(String columnIndex) throws SQLException {
		return get(columnIndex, Caster_Long.INSTANCE, 0L);
	}

	@Override
	public float getFloat(String columnIndex) throws SQLException {
		return get(columnIndex, Caster_Float.INSTANCE, 0F);
	}

	@Override
	public double getDouble(String columnIndex) throws SQLException {
		return get(columnIndex, Caster_Double.INSTANCE, 0D);
	}

	@Override
	public byte[] getBytes(String columnIndex) throws SQLException {
		return get(columnIndex, Caster_Array.BYTE_PRIMITIVE, null);
	}
	@Override
	public Object getObject(String columnIndex) throws SQLException {
		return get(columnIndex, Caster_Simple.OBJECT, null);
	}
	@Override
	public String getNString(String columnIndex) throws SQLException {
		return getString(columnIndex);
	}

	@Override
	public Date getDate(String columnIndex) throws SQLException {
		return getDate(columnIndex, CASTER_DATE, null, null);
	}

	@Override
	public Time getTime(String columnIndex) throws SQLException {
		return getDate(columnIndex, CASTER_TIME, null, null);
	}

	@Override
	public Timestamp getTimestamp(String columnIndex) throws SQLException {
		return getDate(columnIndex, CASTER_TIMESTAMP, null, null);
	}
	@Override
	public Date getDate(String columnIndex, Calendar cal) throws SQLException {
		return getDate(columnIndex, CASTER_DATE, null, cal);
	}

	@Override
	public Time getTime(String columnIndex, Calendar cal) throws SQLException {
		return getDate(columnIndex, CASTER_TIME, null, cal);
	}

	@Override
	public Timestamp getTimestamp(String columnIndex, Calendar cal) throws SQLException {
		return getDate(columnIndex, CASTER_TIMESTAMP, null, cal);
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	@Override
	public InputStream getAsciiStream(int columnIndex) throws SQLException {
		throw unsupported();
	}

	@Override
	public InputStream getUnicodeStream(int columnIndex) throws SQLException {
		throw unsupported();
	}

	@Override
	public InputStream getBinaryStream(int columnIndex) throws SQLException {
		throw unsupported();
	}

	@Override
	public InputStream getAsciiStream(String columnLabel) throws SQLException {
		throw unsupported();
	}

	@Override
	public InputStream getUnicodeStream(String columnLabel) throws SQLException {
		throw unsupported();
	}

	@Override
	public InputStream getBinaryStream(String columnLabel) throws SQLException {
		throw unsupported();
	}
	@Override
	public SQLWarning getWarnings() throws SQLException {
		return null;
	}

	@Override
	public void clearWarnings() throws SQLException {
	}

	@Override
	public String getCursorName() throws SQLException {
		return null;
	}
	@Override
	public void setFetchDirection(int direction) throws SQLException {
		throw unsupported();
	}

	@Override
	public int getFetchDirection() throws SQLException {
		return FETCH_FORWARD;
	}

	@Override
	public void setFetchSize(int rows) throws SQLException {
		throw unsupported();
	}

	@Override
	public int getFetchSize() throws SQLException {
		return Integer.MAX_VALUE;
	}
	@Override
	public int getType() throws SQLException {
		return TYPE_SCROLL_INSENSITIVE;
	}

	@Override
	public int getConcurrency() throws SQLException {
		return CONCUR_READ_ONLY;
	}

	@Override
	public boolean rowUpdated() throws SQLException {
		return false;
	}

	@Override
	public boolean rowInserted() throws SQLException {
		return false;
	}

	@Override
	public boolean rowDeleted() throws SQLException {
		return false;
	}

	@Override
	public void updateNull(int columnIndex) throws SQLException {
		throw unsupported();

	}

	@Override
	public void updateBoolean(int columnIndex, boolean x) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateByte(int columnIndex, byte x) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateShort(int columnIndex, short x) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateInt(int columnIndex, int x) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateLong(int columnIndex, long x) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateFloat(int columnIndex, float x) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateDouble(int columnIndex, double x) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateString(int columnIndex, String x) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateBytes(int columnIndex, byte[] x) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateDate(int columnIndex, Date x) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateTime(int columnIndex, Time x) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateObject(int columnIndex, Object x) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateNull(String columnLabel) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateBoolean(String columnLabel, boolean x) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateByte(String columnLabel, byte x) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateShort(String columnLabel, short x) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateInt(String columnLabel, int x) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateLong(String columnLabel, long x) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateFloat(String columnLabel, float x) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateDouble(String columnLabel, double x) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateString(String columnLabel, String x) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateBytes(String columnLabel, byte[] x) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateDate(String columnLabel, Date x) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateTime(String columnLabel, Time x) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateObject(String columnLabel, Object x) throws SQLException {
		throw unsupported();
	}

	@Override
	public void insertRow() throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateRow() throws SQLException {
		throw unsupported();
	}

	@Override
	public void deleteRow() throws SQLException {
		throw unsupported();
	}

	@Override
	public void refreshRow() throws SQLException {
		throw unsupported();
	}

	@Override
	public void cancelRowUpdates() throws SQLException {
		throw unsupported();
	}

	@Override
	public void moveToInsertRow() throws SQLException {
		throw unsupported();
	}

	@Override
	public Ref getRef(int columnIndex) throws SQLException {
		throw unsupported();
	}

	@Override
	public Blob getBlob(int columnIndex) throws SQLException {
		throw unsupported();
	}

	@Override
	public Clob getClob(int columnIndex) throws SQLException {
		throw unsupported();
	}

	@Override
	public Array getArray(int columnIndex) throws SQLException {
		throw unsupported();
	}

	@Override
	public Ref getRef(String columnLabel) throws SQLException {
		throw unsupported();
	}

	@Override
	public Blob getBlob(String columnLabel) throws SQLException {
		throw unsupported();
	}

	@Override
	public Clob getClob(String columnLabel) throws SQLException {
		throw unsupported();
	}

	@Override
	public Array getArray(String columnLabel) throws SQLException {
		throw unsupported();
	}

	@Override
	public URL getURL(int columnIndex) throws SQLException {
		throw unsupported();
	}

	@Override
	public URL getURL(String columnLabel) throws SQLException {
		throw unsupported();
	}
	@Override
	public void updateRef(int columnIndex, Ref x) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateRef(String columnLabel, Ref x) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateBlob(int columnIndex, Blob x) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateBlob(String columnLabel, Blob x) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateClob(int columnIndex, Clob x) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateClob(String columnLabel, Clob x) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateArray(int columnIndex, Array x) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateArray(String columnLabel, Array x) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateRowId(int columnIndex, RowId x) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateRowId(String columnLabel, RowId x) throws SQLException {
		throw unsupported();
	}

	@Override
	public int getHoldability() throws SQLException {
		return HOLD_CURSORS_OVER_COMMIT;
	}

	@Override
	public void updateNString(int columnIndex, String nString) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateNString(String columnLabel, String nString) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
		throw unsupported();

	}

	@Override
	public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
		throw unsupported();

	}

	@Override
	public NClob getNClob(int columnIndex) throws SQLException {
		throw unsupported();
	}

	@Override
	public NClob getNClob(String columnLabel) throws SQLException {
		throw unsupported();
	}

	@Override
	public SQLXML getSQLXML(int columnIndex) throws SQLException {
		throw unsupported();
	}

	@Override
	public SQLXML getSQLXML(String columnLabel) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
		throw unsupported();

	}

	@Override
	public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
		throw unsupported();

	}

	@Override
	public Reader getNCharacterStream(int columnIndex) throws SQLException {
		throw unsupported();
	}

	@Override
	public Reader getNCharacterStream(String columnLabel) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateClob(int columnIndex, Reader reader) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateClob(String columnLabel, Reader reader) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateNClob(int columnIndex, Reader reader) throws SQLException {
		throw unsupported();
	}

	@Override
	public void updateNClob(String columnLabel, Reader reader) throws SQLException {
		throw unsupported();
	}

	@Override
	public void moveToCurrentRow() throws SQLException {
		throw unsupported();
	}
	@Override
	public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
		throw unsupported();
	}
	@Override
	public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
		throw unsupported();
	}
	@Override
	public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		throw unsupported();
	}

	@Override
	public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
		throw unsupported();
	}
	@Override
	public Reader getCharacterStream(int columnIndex) throws SQLException {
		throw unsupported();
	}

	@Override
	public Reader getCharacterStream(String columnLabel) throws SQLException {
		throw unsupported();
	}
	@Override
	public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
		throw unsupported();
	}

	@Override
	public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
		throw unsupported();
	}
	@Override
	public RowId getRowId(int columnIndex) throws SQLException {
		throw unsupported();
	}

	@Override
	public RowId getRowId(String columnLabel) throws SQLException {
		throw unsupported();
	}

	private UnsupportedOperationException unsupported() {
		return new UnsupportedOperationException();
	}
	private <T> T get(int columnIndex, Caster<T> clazz, T dflt) {
		T r = this.row.getAt(columnIndex - 1, clazz);
		return (wasNull = (r == null)) ? dflt : r;
	}
	private <T> T get(String columnIndex, Caster<T> clazz, T dflt) {
		T r = this.row.get(columnIndex, clazz);
		return (wasNull = (r == null)) ? dflt : r;
	}
	private <T> T getDate(int columnIndex, Caster<T> clazz, T dflt, Calendar c) {
		T r = this.row.getAt(columnIndex - 1, clazz);
		return (wasNull = (r == null)) ? dflt : r;
	}
	private <T> T getDate(String columnIndex, Caster<T> clazz, T dflt, Calendar c) {
		T r = this.row.get(columnIndex, clazz);
		return (wasNull = (r == null)) ? dflt : r;
	}

	public <T> T getObject(int arg0, Class<T> arg1) throws SQLException {
		throw new ToDoException();
	}

	public <T> T getObject(String arg0, Class<T> arg1) throws SQLException {
		throw new ToDoException();
	}

	@Override
	public DateMillis getMillis(int columnIndex) throws SQLException {
		return this.row.getAt(columnIndex - 1, Caster_DateMillis.INSTANCE);
	}

	@Override
	public DateNanos getNanos(int columnIndex) throws SQLException {
		return this.row.getAt(columnIndex - 1, Caster_DateNanos.INSTANCE);
	}

	@Override
	public Table getUnderlyingTable() {
		return this.t;
	}

	private UnsupportedOperationException unsupported(String msg) {
		LH.info(log, getClass().getSimpleName() + ":Going to throw unsupported operation exception: " + msg);
		return new UnsupportedOperationException(msg);
	}
}
