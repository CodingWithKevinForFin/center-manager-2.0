package com.f1.utils.db;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

public abstract class AbstractDataSource implements DataSource {

	private PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(System.out));
	private int loginTimeoutSeconds;

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return printWriter;
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		this.printWriter = out;

	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		this.loginTimeoutSeconds = seconds;

	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return this.loginTimeoutSeconds;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		if (!isWrapperFor(iface))
			throw new SQLException("can not unwrap: " + iface);
		return (T) this;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return iface.isAssignableFrom(getClass());
	}

	@Override
	abstract public Connection getConnection() throws SQLException;

	@Override
	abstract public Connection getConnection(String username, String password) throws SQLException;

}
