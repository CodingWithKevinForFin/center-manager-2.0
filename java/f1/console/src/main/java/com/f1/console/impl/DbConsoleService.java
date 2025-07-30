package com.f1.console.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.f1.base.Table;
import com.f1.console.ConsoleConnection;
import com.f1.console.ConsoleSession;
import com.f1.utils.DBH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.TableHelper;

public class DbConsoleService extends AbstractConsoleService {
	private static final Logger log = Logger.getLogger(DbConsoleService.class.getName());
	private static final String KEY_DBCONNECTION = "DBCONNECTION";
	public DbConsoleService() {
		super("db", "DB( +CONNECT)? +(.*);", "Connects to a database. Usage: db connect <url> <password> --or-- db sql");
	}

	@Override
	public void doRequest(ConsoleSession session, String[] options) {
		if (options[1] != null) {
			final String urlAndPassword = options[2];
			final Connection connection = (Connection) session.getValue(KEY_DBCONNECTION);
			if (connection != null) {
				IOH.close(connection);
				session.getConnection().comment(ConsoleConnection.COMMENT_MESSAGE, "Closed current connection. ");
			}
			try {
				String url = SH.beforeLast(urlAndPassword, ' ').trim();
				String password = SH.afterLast(urlAndPassword, ' ').trim();
				DataSource datasource = DBH.createDataSource(url, password);
				session.setValue(KEY_DBCONNECTION, datasource.getConnection());
				session.getConnection().comment(ConsoleConnection.COMMENT_MESSAGE, "Connected. ");
			} catch (SQLException e) {
				session.getConnection().comment(ConsoleConnection.COMMENT_ERROR, "Error connecting: " + e.getMessage());
			}
		} else {
			final String sql = options[2];
			final Connection connection = (Connection) session.getValue(KEY_DBCONNECTION);
			if (connection == null) {
				session.getConnection().comment(ConsoleConnection.COMMENT_ERROR, "not connected. Run command: DB CONNECT url password");
				return;
			}
			try {
				PreparedStatement statement = connection.prepareStatement(sql);
				StringBuilder sb = new StringBuilder();
				int resultSetCount = 1;
				do {
					ResultSet result = statement.executeQuery();
					Table table = DBH.toTable(result);
					table.setTitle("ResultSet " + resultSetCount + "  (" + table.getSize() + " rows)");
					TableHelper.toString(table, "", TableHelper.SHOW_ALL, SH.clear(sb));
					session.getConnection().println(sb);
					resultSetCount++;
				} while (statement.getMoreResults());
			} catch (SQLException e) {
				session.getConnection().comment(ConsoleConnection.COMMENT_ERROR, "Error execution statment: " + e.getMessage());
				LH.info(log, "error running statement on: ", sql, e);
			}
		}
	}
}



