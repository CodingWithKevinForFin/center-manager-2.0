package com.f1.ami.amidb.jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.logging.Logger;

import com.f1.base.Table;
import com.f1.utils.LH;

public class TableResultSetMetaData implements ResultSetMetaData {

	private Table t;
	private static final Logger log = LH.get();

	public TableResultSetMetaData(Table t) {
		this.t = t;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}

	@Override
	public int getColumnType(int column) throws SQLException {
		return SqlTypesMap.toType(t.getColumnAt(column - 1).getType());
	}
	@Override
	public String getColumnTypeName(int column) throws SQLException {
		return SqlTypesMap.toName(t.getColumnAt(column - 1).getType());
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	@Override
	public int getColumnCount() throws SQLException {
		return t.getColumnsCount();
	}

	@Override
	public boolean isAutoIncrement(int column) throws SQLException {
		return false;
	}

	@Override
	public boolean isCaseSensitive(int column) throws SQLException {
		return false;
	}

	@Override
	public boolean isSearchable(int column) throws SQLException {
		return false;
	}

	@Override
	public boolean isCurrency(int column) throws SQLException {
		return false;
	}

	@Override
	public int isNullable(int column) throws SQLException {
		return ResultSetMetaData.columnNullableUnknown;
	}

	@Override
	public boolean isSigned(int column) throws SQLException {
		return true;
	}

	@Override
	public int getColumnDisplaySize(int column) throws SQLException {
		return 16;
	}
	@Override
	public int getPrecision(int column) throws SQLException {
		return 0;
	}

	@Override
	public int getScale(int column) throws SQLException {
		return 0;
	}
	@Override
	public String getColumnLabel(int column) throws SQLException {
		return (String) t.getColumnAt(column - 1).getId();
	}

	@Override
	public String getColumnName(int column) throws SQLException {
		return (String) t.getColumnAt(column - 1).getId();
	}

	@Override
	public String getSchemaName(int column) throws SQLException {
		return "AMI";
	}

	@Override
	public String getTableName(int column) throws SQLException {
		return t.getTitle();
	}

	@Override
	public String getCatalogName(int column) throws SQLException {
		return null;
	}

	@Override
	public boolean isReadOnly(int column) throws SQLException {
		return true;
	}

	@Override
	public boolean isWritable(int column) throws SQLException {
		return false;
	}

	@Override
	public boolean isDefinitelyWritable(int column) throws SQLException {
		return false;
	}

	@Override
	public String getColumnClassName(int column) throws SQLException {
		return t.getColumnAt(column - 1).getType().getName();
	}

	public static Class<?> toClass(int type) {
		Class<?> result = Object.class;

		switch (type) {
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
				result = String.class;
				break;

			case Types.NUMERIC:
			case Types.DECIMAL:
				result = java.math.BigDecimal.class;
				break;

			case Types.BIT:
				result = Boolean.class;
				break;

			case Types.TINYINT:
				result = Byte.class;
				break;

			case Types.SMALLINT:
				result = Short.class;
				break;

			case Types.INTEGER:
				result = Integer.class;
				break;

			case Types.BIGINT:
				result = Long.class;
				break;

			case Types.REAL:
			case Types.FLOAT:
				result = Float.class;
				break;

			case Types.DOUBLE:
				result = Double.class;
				break;

			case Types.BINARY:
			case Types.VARBINARY:
			case Types.LONGVARBINARY:
				result = Byte[].class;
				break;

			case Types.DATE:
				result = java.sql.Date.class;
				break;

			case Types.TIME:
				result = java.sql.Time.class;
				break;

			case Types.TIMESTAMP:
				result = java.sql.Timestamp.class;
				break;
		}

		return result;
	}
	private UnsupportedOperationException unsupported(String msg) {
		LH.info(log, getClass().getSimpleName() + ":Going to throw unsupported operation exception: " + msg);
		return new UnsupportedOperationException(msg);
	}
}
