package com.f1.ami.center.ds;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.f1.base.Table;

public interface FastJdbcConnection extends Connection {

	public boolean supportFastInsert();
	void fastInsert(String tablename, List<String> columns, Table data, long timeoutMillis) throws SQLException;

}
