package com.f1.utils.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Map;

public interface DbStatement {

	int executeUpdate(Map<Object, Object> params, Connection connection, DbStatementFactory factory) throws Exception;
	void execute(Map<Object, Object> params, Connection connection, DbStatementFactory factory) throws Exception;
	ResultSet executeQuery(Map<Object, Object> params, Connection connection, DbStatementFactory factory) throws Exception;

	int executeUpdate(Map<Object, Object> params, Connection connection) throws Exception;
	void execute(Map<Object, Object> params, Connection connection) throws Exception;
	ResultSet executeQuery(Map<Object, Object> params, Connection connection) throws Exception;

	//for file backed should return the file name, otherwise a short description
	String describe();

}
