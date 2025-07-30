package com.f1.ami.plugins.ssh;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import com.f1.utils.AH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.InteractiveCallback;
import ch.ethz.ssh2.Session;

public class AmiSshClient implements Runnable, Closeable {

	public class BasicInteractiveCallback implements InteractiveCallback {

		private String password;

		public BasicInteractiveCallback(String password) {
			this.password = password;
		}

		@Override
		public String[] replyToChallenge(String name, String instruction, int numPrompts, String[] prompt, boolean[] echo) throws Exception {
			String[] r = new String[numPrompts];
			AH.fill(r, password);
			return r;
		}
	}

	public static final int GENERAL_ERROR = -1;
	public static final int AUTH_FAILED = -2;
	public static final int UNKNOWN_HOST = -3;
	public static final int TIMEOUT = -4;
	final private String hostName;
	final private String userName;
	final private String password;
	final private String command;
	final private byte[] stdin;
	final private int timeout;
	private InputStream stdout;
	private InputStream stderr;

	private Exception exception;
	private int exitCode = 0;
	private boolean complete = false;
	private char[] pemData;
	private boolean useDumbPty;
	private int port;
	private Session sess;
	private Connection conn;
	final private int authMode;
	private static final Logger log = LH.get();
	public static final byte AUTHMODE_PASSWORD = 1;
	public static final byte AUTHMODE_PUBLICKEY = 2;
	public static final byte AUTHMODE_KEYBOARD_INTERACTIVE = 3;

	public AmiSshClient(String hostName, int port, String userName, String password, String command, byte stdin[], int timeout) {
		this(hostName, port, userName, password, command, stdin, timeout, AUTHMODE_PASSWORD, null, false);
	}
	public AmiSshClient(String hostName, int port, String userName, String password, String command, byte stdin[], int timeout, byte authMode, char[] optionalPemData,
			boolean useDumbPty) {
		this.port = port;
		this.hostName = hostName;
		this.userName = userName;
		this.password = password;
		this.command = command;
		this.stdin = stdin;
		this.authMode = authMode;
		this.timeout = timeout;
		this.pemData = optionalPemData;
		this.useDumbPty = useDumbPty;
	}

	public boolean connect() {
		try {
			conn = new Connection(hostName, port);
			conn.connect(null, timeout, timeout);
			boolean isAuthenticated;
			try {
				switch (authMode) {
					case AUTHMODE_PUBLICKEY:
						isAuthenticated = conn.authenticateWithPublicKey(userName, pemData, password);
						break;
					case AUTHMODE_PASSWORD:
						isAuthenticated = conn.authenticateWithPassword(userName, password);
						break;
					case AUTHMODE_KEYBOARD_INTERACTIVE:
						isAuthenticated = conn.authenticateWithKeyboardInteractive(userName, new BasicInteractiveCallback(password));
						break;
					default:
						throw new RuntimeException("bad mode: " + authMode);
				}

			} catch (Exception e) {
				LH.warning(log, "Error authenticating to :" + userName + "@" + hostName, e);
				this.exitCode = AUTH_FAILED;
				this.complete = true;
				return false;
			}
			if (isAuthenticated == false) {
				this.exitCode = AUTH_FAILED;
				this.complete = true;
				return false;
			}
		} catch (UnknownHostException e) {
			this.exitCode = UNKNOWN_HOST;
			this.complete = true;
			return false;
		} catch (SocketTimeoutException e) {
			this.complete = true;
			this.exitCode = TIMEOUT;
			this.exception = e;
			return false;
		} catch (Exception e) {
			this.complete = true;
			if (e.getCause() != null && e.getCause() instanceof UnknownHostException)
				this.exitCode = UNKNOWN_HOST;
			else {
				this.exitCode = GENERAL_ERROR;
				this.exception = e;
			}
			return false;
		}
		return true;
	}

	@Override
	public void run() {
		if (!connect())
			return;
		try {
			this.sess = conn.openSession();
			if (useDumbPty)
				sess.requestDumbPTY();
			sess.execCommand(command);
			if (stdin != null) {
				sess.getStdin().write(stdin);
			}
			sess.getStdin().flush();
			this.stdout = sess.getStdout();
			this.stderr = sess.getStderr();
			sess.getStdin().close();
		} catch (Exception e) {
			this.complete = true;
			if (e.getCause() != null && e.getCause() instanceof UnknownHostException)
				this.exitCode = UNKNOWN_HOST;
			else {
				this.exitCode = GENERAL_ERROR;
				this.exception = e;
			}
		}
	}

	@Override
	public void close() {
		if (sess != null) {
			IOH.close(this.sess.getStderr());
			IOH.close(this.sess.getStdout());
			IOH.close(this.sess.getStdin());
		}
		if (conn != null)
			this.conn.close();
		if (sess != null)
			this.sess.close();
		this.sess = null;
		this.conn = null;
	}

	public String getHostName() {
		return hostName;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

	public String getCommand() {
		return command;
	}
	public int getExitCode() {
		return this.exitCode;
	}

	public InputStream getStdout() {
		return stdout;
	}
	public InputStream getStderr() {
		return stderr;
	}

	public Exception getException() {
		return exception;
	}

	public boolean isComplete() {
		return complete;
	}
	public int waitFor() {
		try {
			sess.waitForCondition(ChannelCondition.EXIT_STATUS, timeout);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.exitCode = OH.noNull(sess.getExitStatus(), -1);
		sess.close();
		return this.exitCode;
	}
	public Session getSession() {
		return this.sess;
	}
	public Connection getConnection() {
		return this.conn;
	}

}
