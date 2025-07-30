package com.f1.utils.ftp.client;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.f1.utils.IOH;
import com.f1.utils.SH;

public class FtpClient implements Closeable {

	private FtpConnection connection;
	private boolean binary = true;
	private boolean loggedIn = false;
	private int port;
	private String host;
	private String user;
	private String pass;

	public FtpClient() {
	}
	public FtpClient(String host, int port) throws UnknownHostException, IOException {
		connect(host, port);
	}
	public FtpClient(String host, int port, String user, String pass) throws UnknownHostException, IOException {
		connect(host, port);
		login(user, pass);
	}

	public String connect(String host, int port) throws UnknownHostException, IOException {
		if (connection != null)
			throw new RuntimeException("arleady connected");
		connection = new FtpConnection(host, port);
		this.host = host;
		this.port = port;
		return connection.getWelcomeMessage();
	}

	public void disconnect() {
		if (connection != null) {
			try {
				if (loggedIn)
					connection.quit();
			} catch (Exception e) {
				//ignore 
			}
			connection.close();
		}
		connection = null;
	}

	public void login(String user, String pass) {
		this.user = user;
		this.pass = pass;
		if (connection == null)
			throw new RuntimeException("not connected");
		if (loggedIn)
			throw new RuntimeException("already logged in");
		connection.login(user, pass);
		this.loggedIn = true;
		connection.type(FtpConstants.TYPE_IMAGE_BINARY_DATA);
		this.binary = true;
	}

	public void binary() {
		assertLoggedIn();
		if (binary)
			return;
		connection.type(FtpConstants.TYPE_IMAGE_BINARY_DATA);
		this.binary = true;
	}
	public void text() {
		assertLoggedIn();
		if (!binary)
			return;
		connection.type(FtpConstants.TYPE_ASCII_TEXT);
		this.binary = false;
	}

	public void put(String file, byte[] data) {
		binary();
		connection.stor(file, data);
	}
	public void put(String file, String data) {
		text();
		connection.stor(file, data.getBytes());
	}
	public void cd(String directoryName) {
		connection.cwd(directoryName);
	}
	public void cdUp() {
		connection.cdup();
	}

	public byte[] getBinary(String file) {
		binary();
		int retryCount = 0;
		for (;;) {
			try {
				return connection.retr(file);
			} catch (FtpException e) {
				if (e.getCause() instanceof SocketTimeoutException) {
					if (retryCount < 2) {
						retryCount++;
						System.err.println("Retrying for file: " + file);
					}
				} else
					throw e;
			} catch (Exception e) {
				throw new FtpException("could not get file: " + file, e);
			}
		}
	}

	private void reconnect() {
		close();
		try {
			connect(host, port);
		} catch (Exception e) {
			throw new FtpException("Error reconnecting", e);
		}
		login(user, pass);
	}
	public String getText(String file) {
		text();
		try {
			return IOH.toString(connection.retr(file));
		} catch (Exception e) {
			throw new FtpException("could not get file: " + file, e);
		}
	}

	public List<FtpFile> listFiles() {
		return listFiles(null);
	}
	public List<FtpFile> listFiles(String path) {
		String[] lines = connection.mlsd(path);
		List<FtpFile> r = new ArrayList<FtpFile>(lines.length);
		for (String line : lines) {
			if (SH.is(line))
				r.add(FtpUtils.parseFile(line, connection.getDateParser()));
		}
		return r;
	}

	public String pwd() {
		return connection.pwd();
	}

	public boolean isConnected() {
		return connection != null;
	}

	private void assertLoggedIn() {
		if (connection == null)
			throw new RuntimeException("not logged in");
	}
	public void close() {
		IOH.close(connection);
		connection = null;
		loggedIn = false;
	}
}
