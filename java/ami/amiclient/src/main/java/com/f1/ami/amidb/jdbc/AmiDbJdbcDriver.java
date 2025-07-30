package com.f1.ami.amidb.jdbc;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

import com.f1.utils.LH;

public class AmiDbJdbcDriver implements Driver {
	private static final Logger log = LH.get();

	static {
		try {
			DriverManager.registerDriver(new AmiDbJdbcDriver());
		} catch (SQLException e) {
			System.err.println(AmiDbJdbcDriver.class.getName() + ": Could not register AmiJdbcDriver driver: " + e.getMessage());
		}
	}

	@Override
	public Connection connect(String url, Properties info) throws SQLException {
		if (!acceptsURL(url))
			return null;
		try {
			return new AmiDbJdbcConnection(new AmiDbJdbcClient(url, null, info));
		} catch (IOException e) {
			throw new SQLException(e.getMessage(), e);
		}
	}

	@Override
	public boolean acceptsURL(String url) throws SQLException {
		return url.startsWith(AmiDbJdbcClient.JDBC_AMISQL);
	}

	@Override
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
		return new DriverPropertyInfo[0];
	}

	@Override
	public int getMajorVersion() {
		return 1;
	}

	@Override
	public int getMinorVersion() {
		return 1;
	}

	@Override
	public boolean jdbcCompliant() {
		return false;
	}

	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return log;
	}

}
