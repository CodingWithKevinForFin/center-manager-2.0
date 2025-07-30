/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import com.f1.base.ObjectGenerator;
import com.f1.utils.DetailedException;
import com.f1.utils.SH;

public class BasicDbStatement implements DbStatement {

	private MultiDbStatementPreparer preparer;
	private String sql;

	public BasicDbStatement(String sql, ObjectGenerator generator) throws Exception {
		this.sql = sql;
		parseSql(sql);
	}

	protected void parseSql(String sqlText) throws Exception {
		try {
			BufferedReader reader = new BufferedReader(new StringReader(sqlText));

			StringBuilder sql = readUntilBlank(reader, SH.NEWLINE);
			String type = readUntilBlank(reader, "").toString().trim();
			if (type.equalsIgnoreCase("table") || SH.isnt(type)) {
			} else {
				if (type.indexOf('.') == -1)
					throw new RuntimeException("The statement directly following a blank line must resolve to either a class name or 'table'. At: " + type);
			}
			preparer = new MultiDbStatementPreparer(sql.toString());
		} catch (Exception e) {
			throw new RuntimeException("Erorr parsing sql: " + describe(), e);
		}

	}

	private StringBuilder readUntilBlank(BufferedReader in, String newLine) throws IOException {

		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = in.readLine()) != null && SH.is(line))
			sb.append(line).append(newLine);
		return sb;

	}

	protected List<PreparedStatement> prepareStatement(Map<Object, Object> params, Connection connection, DbStatementFactory factory) throws Exception {
		try {
			return preparer.prepareStatement(params, connection, factory);
		} catch (Exception e) {
			throw new DetailedException("error executing fileDbStatement", e).set("sql", describe()).set("params", params);
		}
	}

	@Override
	public void execute(Map<Object, Object> params, Connection connection, DbStatementFactory factory) throws Exception {
		executeInner(params, connection, false, factory);
	}

	@Override
	public int executeUpdate(Map<Object, Object> params, Connection connection, DbStatementFactory factory) throws Exception {
		return executeInner(params, connection, true, factory);
	}

	public int executeInner(Map<Object, Object> params, Connection connection, boolean isUpdate, DbStatementFactory factory) throws Exception {
		if (connection == null)
			throw new NullPointerException("connection");
		final boolean autoCommit = connection.getAutoCommit();
		boolean isMultiline = false;
		boolean error = false;
		try {
			List<PreparedStatement> ps = prepareStatement(params, connection, factory);
			isMultiline = ps.size() > 1;
			if (autoCommit && isMultiline) {
				connection.setAutoCommit(false);
			}
			int r = 0;
			for (int i = 0; i < ps.size(); i++)
				try {
					if (isUpdate)
						r += ps.get(i).executeUpdate();
					else
						ps.get(i).execute();
				} catch (Exception e) {
					error = true;
					if (ps.size() > 1)
						throw new Exception("error running query (statement " + (i + 1) + " of " + ps.size() + "): " + describe(), e);
					else
						throw new Exception("error running query: " + describe(), e);
				}
			return r;
		} finally {
			if (autoCommit && isMultiline) {
				try {
					if (error)
						connection.rollback();
					else
						connection.commit();
					connection.setAutoCommit(autoCommit);
				} catch (Exception e) {
					if (!error)
						throw new RuntimeException("Critical error while finalizing multiline autocommit statement", e);
				}
			}
		}
	}

	@Override
	public String describe() {
		return this.sql;
	}

	@Override
	public ResultSet executeQuery(Map<Object, Object> params, Connection connection, DbStatementFactory factory) throws Exception {
		if (connection == null)
			throw new NullPointerException("connection");
		try {
			List<PreparedStatement> ps = prepareStatement(params, connection, factory);
			if (ps.size() != 1)
				throw new Exception("Should only have one prepared statement when calling query: " + describe());
			try {
				return ps.get(0).executeQuery();
			} catch (Exception e) {
				throw new DetailedException("error running query: ", e).set("sql", describe()).set("query params", params);
			}
		} finally {
		}
	}

	@Override
	public int executeUpdate(Map<Object, Object> params, Connection connection) throws Exception {
		return executeUpdate(params, connection, null);
	}

	@Override
	public void execute(Map<Object, Object> params, Connection connection) throws Exception {
		execute(params, connection, null);
	}

	@Override
	public ResultSet executeQuery(Map<Object, Object> params, Connection connection) throws Exception {
		return executeQuery(params, connection, null);
	}

}
