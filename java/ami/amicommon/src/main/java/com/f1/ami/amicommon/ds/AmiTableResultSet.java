package com.f1.ami.amicommon.ds;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.f1.base.DateMillis;
import com.f1.base.DateNanos;
import com.f1.base.Table;

public interface AmiTableResultSet extends ResultSet {

	public DateMillis getMillis(int columnIndex) throws SQLException;
	public DateNanos getNanos(int columnIndex) throws SQLException;

	public Table getUnderlyingTable();

}
