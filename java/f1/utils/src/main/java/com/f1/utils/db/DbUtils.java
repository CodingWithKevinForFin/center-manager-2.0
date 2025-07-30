package com.f1.utils.db;

import java.io.IOException;
import java.io.StringReader;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;
import com.f1.utils.CharReader;
import com.f1.utils.OH;
import com.f1.utils.RH;
import com.f1.utils.SH;
import com.f1.utils.impl.StringCharReader;

public class DbUtils {

	public static DbUtils INSTANCE = new DbUtils();
	private static final int[] BRACKET_OR_COLON = new int[]{'{', ':'};

	public DataSource createDataSource(final String url, String password) throws SQLException {
		if (url.indexOf("*****") != -1)
			throw new SQLException("url must not contain a string consecutive stars longer than '****' for: " + url);
		final String urlWithPassword = SH.replaceAll(url, "****", password);
		if (urlWithPassword.equals(url))
			throw new SQLException("url must contain a '****' which will be replaced with a password for: " + url);
		try {
			return createDataSource(urlWithPassword);
		} catch (SQLException e) {
			throw new SQLException("Error connection to URL: " + url);
		}
	}

	public DataSource createDataSource(final String url) throws SQLException {
		final StringCharReader reader = new StringCharReader(url);
		final StringBuilder sink = new StringBuilder();
		final String className;
		final Properties properties = new Properties();
		final String remainder;
		try {
			reader.readUntil(':', SH.clear(sink));
			reader.expect(':');
			className = sink.toString();
			reader.skip(' ');
			if ('{' == reader.expectAny(BRACKET_OR_COLON)) {
				reader.readUntil('}', '\\', SH.clear(sink));
				reader.expect('}');
				reader.skip(' ');
				reader.expect(':');
				try {
					properties.load(new StringReader(sink.toString()));
				} catch (IOException e) {
					throw OH.toRuntime(e);
				}
			}
			reader.readUntil(CharReader.EOF, SH.clear(sink));
			remainder = sink.toString();
		} catch (Exception e) {
			throw new SQLException("URL must be in format: driver.full.class.name:{key=value,optional=options}:driver.specific.url", e);
		}
		Object obj = RH.invokeConstructor(className);
		if (obj instanceof Driver)
			return new DriverToDataSource((Driver) obj, remainder, properties);
		throw new RuntimeException("can not create datasource for: " + obj);
	}
}
