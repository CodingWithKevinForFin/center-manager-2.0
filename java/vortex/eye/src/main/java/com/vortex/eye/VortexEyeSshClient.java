package com.vortex.eye;

import java.net.UnknownHostException;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;

import com.f1.utils.FastByteArrayOutputStream;
import com.f1.utils.LH;
import com.f1.utils.MonitoredRunnable;
import com.f1.utils.SH;
import com.f1.utils.StreamPiper;

public class VortexEyeSshClient implements Runnable {

	public static final int GENERAL_ERROR = -1;
	public static final int AUTH_FAILED = -2;
	private static final int UNKNOWN_HOST = -3;
	final private String hostName;
	final private String userName;
	final private String password;
	final private String command;
	final private byte[] stdin;
	final private Executor executor;
	final private int timeout;
	private byte[] stdout;
	private byte[] stderr;

	private Exception exception;
	private int exitCode;
	private boolean complete = false;
	private char[] pemData;
	private boolean useDumbPty;
	private int port;
	private static final Logger log = LH.get(VortexEyeSshClient.class);

	public VortexEyeSshClient(String hostName, int port, String userName, String password, String command, byte stdin[], Executor executor, int timeout) {
		this(hostName, port, userName, password, command, stdin, executor, timeout, null, false);
	}
	public VortexEyeSshClient(String hostName, int port, String userName, String password, String command, byte stdin[], Executor executor, int timeout, char[] optionalPemData,
			boolean useDumbPty) {
		this.port = port;
		this.hostName = hostName;
		this.userName = userName;
		this.password = password;
		this.command = command;
		this.stdin = stdin;
		this.executor = executor;
		this.timeout = timeout;
		this.pemData = optionalPemData;
		this.useDumbPty = useDumbPty;
	}

	@Override
	public void run() {
		Connection conn = null;
		try {
			conn = new Connection(hostName, port);
			conn.connect();
			boolean isAuthenticated;
			try {
				if (pemData != null)
					isAuthenticated = conn.authenticateWithPublicKey(userName, pemData, password);
				else
					isAuthenticated = conn.authenticateWithPassword(userName, password);
			} catch (Exception e) {
				LH.warning(log, "Error authenticating to :" + userName + "@" + hostName, e);
				this.exitCode = AUTH_FAILED;
				this.complete = true;
				return;
			}
			if (isAuthenticated == false) {
				this.exitCode = AUTH_FAILED;
				this.complete = true;
				return;
			}

			Session sess = conn.openSession();

			if (useDumbPty)
				sess.requestDumbPTY();
			sess.execCommand(command);

			FastByteArrayOutputStream stdout = new FastByteArrayOutputStream();
			FastByteArrayOutputStream stderr = new FastByteArrayOutputStream();
			MonitoredRunnable m1 = new MonitoredRunnable(new StreamPiper(sess.getStdout(), stdout, 1024));
			MonitoredRunnable m2 = new MonitoredRunnable(new StreamPiper(sess.getStderr(), stderr, 1024));
			executor.execute(m1);
			executor.execute(m2);
			if (stdin != null) {
				sess.getStdin().write(stdin);
				sess.getStdin().flush();
				sess.getStdin().close();
			}
			m1.waitUntilComplete(timeout);
			m2.waitUntilComplete(timeout);
			sess.waitForCondition(ChannelCondition.EXIT_STATUS, timeout);
			sess.close();
			this.exitCode = sess.getExitStatus();
			this.stdout = stdout.toByteArray();
			this.stderr = stderr.toByteArray();
		} catch (UnknownHostException e) {
			this.exitCode = UNKNOWN_HOST;
		} catch (Exception e) {
			this.exitCode = GENERAL_ERROR;
			this.exception = e;
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
		}
		this.complete = true;
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

	public byte[] getStdout() {
		return stdout;
	}
	public byte[] getStderr() {
		return stderr;
	}

	public Exception getException() {
		return exception;
	}

	public boolean isComplete() {
		return complete;
	}
	public String getStderrAsString(int maxSize) {
		return stderr == null ? "" : SH.ddd(new String(stderr), maxSize);
	}

}
