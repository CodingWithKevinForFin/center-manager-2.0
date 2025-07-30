package com.f1.utils.db;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import com.f1.utils.SH;

public class DriverToDataSource extends AbstractDataSource {

	final private Driver driver;
	final private Properties properties;
	final private String url;
	private Properties innerProperties;

	public DriverToDataSource(Driver driver, String url, Properties properties) throws SQLException {
		this.driver = driver;
		this.url = url;
		this.properties = properties;
		this.innerProperties = new Properties();
		for (Map.Entry<Object, Object> e : properties.entrySet())
			if (!e.getKey().toString().startsWith("f1."))
				innerProperties.put(e.getKey(), e.getValue());
		if (!this.driver.acceptsURL(this.url))
			throw new SQLException("Driver " + driver.getClass().getName() + " can not accept url: " + this.url);
	}

	@Override
	public Connection getConnection() throws SQLException {
		return driver.connect(url, innerProperties);
	}

	@Override
	public Connection getConnection(String username_, String password_) throws SQLException {
		throw new SQLException("not supported. Can not log in " + username_ + " / " + SH.password(password_));
	}

	public Properties getProperties() {
		return properties;
	}

	public String getProperty(String name) {
		return (String) properties.get(name);
	}

	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}

}
