package com.f1.utils.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface DbStatementFactory {

	public PreparedStatement createPreparedStatement(Connection c, String sql) throws SQLException;
}
