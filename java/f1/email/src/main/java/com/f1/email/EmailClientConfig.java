package com.f1.email;

import java.util.Properties;

import com.f1.base.Password;
import com.f1.utils.SH;

public class EmailClientConfig {

	public static final int DEFAULT_PORT = 25;
	public static final int DEFAULT_RETRIES_COUNT = 3;
	public static final String DEFAULT_HOST = "localhost";
	public static final boolean DEFAULT_ENABLE_DEBUG = false;
	public static final boolean DEFAULT_ENABLE_SSL = false;
	public static final boolean DEFAULT_ENABLE_AUTHENTICATION = false;
	public static final boolean DEFAULT_ENABLE_STARTTLS = false;
	public static final int DEFAULT_CONNECTION_TIMEOUT = -1;
	public static final int DEFAULT_TIMEOUT = -1;
	public static final int DEFAULT_WRITE_TIMEOUT = -1;
	private String host = DEFAULT_HOST;
	private int port = DEFAULT_PORT;
	private String username;
	private Password password;
	private int retriesCount = DEFAULT_RETRIES_COUNT;
	private boolean enableDebug = DEFAULT_ENABLE_DEBUG;
	private boolean enableSSL = DEFAULT_ENABLE_SSL;
	private boolean enableAuthentication = DEFAULT_ENABLE_AUTHENTICATION;
	private boolean enableStartTLS = DEFAULT_ENABLE_STARTTLS;
	private String sslProtocols;
	private int connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
	private int timeout = DEFAULT_TIMEOUT;
	private int writeTimeout = DEFAULT_WRITE_TIMEOUT;

	public EmailClientConfig() {
	}
	public EmailClientConfig(EmailClientConfig ecc) {
		this.sslProtocols = ecc.getSslProtocols();
		this.host = ecc.getHost();
		this.port = ecc.getPort();
		this.username = ecc.getUsername();
		this.password = ecc.getPassword();
		this.retriesCount = ecc.getRetriesCount();
		this.enableDebug = ecc.getEnableDebug();
		this.enableSSL = ecc.getEnableSSL();
		this.enableAuthentication = ecc.getEnableAuthentication();
		this.enableStartTLS = ecc.getEnableStartTLS();
		this.connectionTimeout = ecc.getConnectionTimeout();
		this.timeout = ecc.getTimeout();
		this.writeTimeout = ecc.getWriteTimeout();
	}

	public Properties toProperties() {
		Properties properties = new Properties();
		properties.put("mail.smtp.host", this.host);
		properties.put("mail.smtp.port", "" + port);
		properties.put("mail.smtp.starttls.enable", enableStartTLS ? "true" : "false");
		properties.put("mail.smtp.starttls.required", enableStartTLS ? "true" : "false");
		properties.put("mail.smtp.auth", enableAuthentication ? "true" : "false");
		properties.put("mail.smtp.ssl.enable", enableSSL ? "true" : "false");
		properties.put("mail.smtp.enableSSL", enableSSL ? "true" : "false");
		properties.put("mail.transport.protocol", "smtp");
		properties.put("mail.smtp.connectiontimeout", this.connectionTimeout);
		properties.put("mail.smtp.timeout",  this.timeout);
		properties.put("mail.smtp.writetimeout", this.writeTimeout);

		if (SH.is(sslProtocols)) {
			properties.put("mail.smtp.ssl.protocols", sslProtocols);
		}
		if (enableSSL) {
			properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			properties.setProperty("mail.smtp.socketFactory.fallback", "false");
			properties.setProperty("mail.smtp.port", "" + port);
			properties.setProperty("mail.smtp.socketFactory.port", "" + port);
		}
		return properties;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Password getPassword() {
		return password;
	}

	public void setPassword(Password password) {
		this.password = password;
	}

	public int getRetriesCount() {
		return retriesCount;
	}

	public void setRetriesCount(int retriesCount) {
		this.retriesCount = retriesCount;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean getEnableDebug() {
		return enableDebug;
	}

	public void setEnableDebug(boolean enableDebug) {
		this.enableDebug = enableDebug;
	}

	public boolean getEnableSSL() {
		return enableSSL;
	}

	public void setEnableSSL(boolean enableSSL) {
		this.enableSSL = enableSSL;
	}

	public boolean getEnableAuthentication() {
		return enableAuthentication;
	}

	public void setEnableAuthentication(boolean enableAuthentication) {
		this.enableAuthentication = enableAuthentication;
	}

	public boolean getEnableStartTLS() {
		return enableStartTLS;
	}

	public void setEnableStartTLS(boolean enableStartTLS) {
		this.enableStartTLS = enableStartTLS;
	}
	public String getSslProtocols() {
		return sslProtocols;
	}
	public void setSslProtocols(String sslProtocols) {
		this.sslProtocols = sslProtocols;
	}
	public int getConnectionTimeout() {
		return this.connectionTimeout;
	}
	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}
	public int getTimeout() {
		return this.timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	public int getWriteTimeout() {
		return this.writeTimeout;
	}
	public void setWriteTimeout(int writeTimeout) {
		this.writeTimeout = writeTimeout;
	}
}
