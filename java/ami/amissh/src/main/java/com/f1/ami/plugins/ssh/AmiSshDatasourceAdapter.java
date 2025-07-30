package com.f1.ami.plugins.ssh;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiDatasourceAdapter;
import com.f1.ami.amicommon.AmiDatasourceException;
import com.f1.ami.amicommon.AmiDatasourceTracker;
import com.f1.ami.amicommon.AmiServiceLocator;
import com.f1.ami.amicommon.msg.AmiCenterQuery;
import com.f1.ami.amicommon.msg.AmiCenterQueryResult;
import com.f1.ami.amicommon.msg.AmiCenterUpload;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.ami.center.ds.AmiDatasourceUtils;
import com.f1.ami.center.ds.AmiFlatFileDatasourceAdapter2;
import com.f1.base.Generator;
import com.f1.base.Table;
import com.f1.container.ContainerTools;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.MonitoredRunnable;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.derived.TimeoutController;
import com.f1.utils.structs.table.online.OnlineTable;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Session;

public class AmiSshDatasourceAdapter implements AmiDatasourceAdapter {
	public static Map<String, String> buildOptions() {
		Map<String, String> r = new HashMap<String, String>();
		r.put(OPTION_PUBLIC_KEY_FILE, "Location Public Key File");
		r.put(OPTION_USE_DUMB_PTY, "true=Use Dumb Pty Terminal (default is false)");
		r.put(OPTION_AUTH_MODE, "keyboardInteractive or password (default is password)");
		return r;
	}

	public static final String OPTION_PUBLIC_KEY_FILE = "publicKeyFile";
	public static final String OPTION_USE_DUMB_PTY = "useDumbPty";
	public static final String OPTION_AUTH_MODE = "authMode";
	private static final Logger log = LH.get();

	public static class NullPiper implements Runnable {

		private InputStream input;

		public NullPiper(InputStream inputStream) {
			this.input = inputStream;
		}

		@Override
		public void run() {
			byte[] buffer = new byte[2048];
			try {
				while (input.read(buffer) != -1)
					;
			} catch (IOException e) {
				throw OH.toRuntime(e);
			}
		}

	}

	public static class Streamer implements Generator<Reader> {

		private InputStreamReader stream;

		@Override
		public Reader nw() {
			if (this.stream == null)
				throw new RuntimeException("Can not reuse stream");
			InputStreamReader r = stream;
			this.stream = null;
			return r;
		}

		public void setStream(InputStream t) {
			this.stream = new InputStreamReader(t);
		}

	}

	private AmiServiceLocator serviceLocator;
	private ContainerTools tools;
	private int port;
	private String hostName;
	private Map<String, String> options;

	@Override
	public void init(ContainerTools tools, AmiServiceLocator serviceLocator) throws AmiDatasourceException {
		this.tools = tools;
		this.serviceLocator = serviceLocator;
		String url = this.serviceLocator.getUrl();
		this.hostName = SH.beforeFirst(url, ':', url);
		try {
			this.port = SH.parseInt(SH.trim(SH.afterFirst(url, ':', "22")));
		} catch (Exception e) {
			throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "Invalid url format, port should be a number", e);
		}
		this.options = SH.splitToMap(',', '=', '\\', serviceLocator.getOptions());
	}

	@Override
	public List<AmiDatasourceTable> getTables(AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException {
		boolean succeeded = false;
		try {
			succeeded = runProcess("exit", null, tc);
		} finally {
			IOH.close(this.process);
		}
		if (!succeeded) {
			if (this.process == null)
				return Collections.EMPTY_LIST;//was cancelled
			this.process = null;
			throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "Connection test did not complete");
		}
		this.process = null;
		AmiDatasourceTable sample = tools.nw(AmiDatasourceTable.class);
		sample.setCollectionName(null);
		sample.setName("sample");
		sample.setColumns(Collections.EMPTY_LIST);
		sample.setDatasourceName(this.serviceLocator.getTargetName());
		sample.setCustomQuery("select * from cmd");
		sample.setCustomUse("_cmd=\"ls\"");
		return CH.l(sample);
	}
	@Override
	public List<AmiDatasourceTable> getPreviewData(List<AmiDatasourceTable> tables, int previewCount, AmiDatasourceTracker debugSink, TimeoutController tc)
			throws AmiDatasourceException {
		AmiDatasourceTable sample = tools.nw(AmiDatasourceTable.class);
		sample.setCollectionName(null);
		sample.setName("sample");
		sample.setCreateTableClause("stdout,stderr,exitCode");
		sample.setColumns(Collections.EMPTY_LIST);
		sample.setDatasourceName(this.serviceLocator.getTargetName());
		sample.setCustomQuery("select * from cmd");
		sample.setCustomUse("_cmd=\"ls\"");
		return CH.l(sample);
	}

	private AmiSshClient process;

	@Override
	public void processQuery(AmiCenterQuery query, AmiCenterQueryResult resultSink, AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException {
		Map<String, Object> directives = query.getDirectives();
		String command = AmiDatasourceUtils.getOptional(directives, "cmd");
		if (SH.isnt(command))
			throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "_cmd is required");
		String stdinString = AmiDatasourceUtils.getOptional(directives, "stdin");
		String capture = AmiDatasourceUtils.getOptional(directives, "capture");

		String[] captureParts;
		boolean captureStdout = false;
		boolean captureStderr = false;
		boolean captureExitCode = false;
		if (capture == null) {
			captureParts = null;
			captureStdout = captureStderr = captureExitCode = true;
		} else {
			captureParts = SH.split(',', capture);
			for (String s : captureParts) {
				s = s.trim();
				if ("stdout".equalsIgnoreCase(s)) {
					if (captureStdout)
						throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "_include duplicate entry: stdout");
					captureStdout = true;
				} else if ("stderr".equalsIgnoreCase(s)) {
					if (captureStderr)
						throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "_include duplicate entry: stderr");
					captureStderr = true;
				} else if ("exitCode".equalsIgnoreCase(s)) {
					if (captureExitCode)
						throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "_include duplicate entry: exitCode");
					captureExitCode = true;
				} else
					throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR,
							"_include expecting comma delimited list of either STDOUT,STDERR,EXITCODE.  Not: '" + s + "'");
			}
		}

		final byte[] stdin = stdinString == null ? null : stdinString.getBytes();

		try {
			final Streamer stdoutStream;
			final OnlineTable stdoutOnlineTable;
			if (captureStdout) {
				stdoutStream = new Streamer();
				stdoutOnlineTable = AmiFlatFileDatasourceAdapter2.streamToTable(directives, stdoutStream);
			} else {
				stdoutStream = null;
				stdoutOnlineTable = null;
			}

			final Streamer stderrStream;
			final OnlineTable stderrOnlineTable;
			if (captureStderr) {
				stderrStream = new Streamer();
				stderrOnlineTable = AmiFlatFileDatasourceAdapter2.streamToTable(directives, stderrStream);
			} else {
				stderrStream = null;
				stderrOnlineTable = null;
			}

			if (!runProcess(command, stdin, tc)) {
				return;
			}

			int limit = query.getLimit();
			if (limit == NO_LIMIT)
				limit = Integer.MAX_VALUE;

			final MonitoredRunnable stdoutRunner;
			final Piper stdoutPiper;
			Thread stdoutThread;
			if (captureStdout) {
				stdoutStream.setStream(process.getStdout());
				stdoutPiper = new Piper(stdoutOnlineTable, limit, query.getQuery(), "stdout", debugSink, process.getStdout(), process, tc);
				stdoutRunner = new MonitoredRunnable(stdoutPiper, false);
				stdoutThread = new Thread(stdoutRunner, "STDOUT");
				stdoutThread.start();
			} else {
				stdoutRunner = new MonitoredRunnable(new NullPiper(process.getStdout()), false);
				stdoutThread = new Thread(stdoutRunner, "STDOUT");
				stdoutThread.start();
				stdoutPiper = null;
			}

			final MonitoredRunnable stderrRunner;
			final Piper stderrPiper;
			Thread stderrThread;
			if (captureStderr) {
				stderrStream.setStream(process.getStderr());
				stderrPiper = new Piper(stderrOnlineTable, limit, query.getQuery(), "stderr", debugSink, process.getStderr(), process, tc);
				stderrRunner = new MonitoredRunnable(stderrPiper, false);
				stderrThread = new Thread(stderrRunner, "STDERR");
				stderrThread.start();
			} else {
				stderrRunner = new MonitoredRunnable(new NullPiper(process.getStderr()), false);
				stderrThread = new Thread(stderrRunner, "STDERR");
				stderrThread.start();
				stderrPiper = null;
			}

			long timeoutMillisRemaining = tc.getTimeoutMillisRemaining();
			join(stdoutThread, timeoutMillisRemaining);
			join(stderrThread, timeoutMillisRemaining);
			AmiSshClient p = this.process;
			if (p == null)
				return;
			Session session = p.getSession();
			if (session != null) {
				session.waitForCondition(ChannelCondition.EOF, timeoutMillisRemaining);
				if (captureStdout)
					stdoutRunner.waitUntilComplete(timeoutMillisRemaining);
				if (captureStderr)
					stderrRunner.waitUntilComplete(timeoutMillisRemaining);
				IOH.close(p.getStdout());
				IOH.close(p.getStderr());
				if (captureStderr && stderrRunner.getThrown() != null)
					throw new AmiDatasourceException(AmiDatasourceException.SYNTAX_ERROR, stderrRunner.getThrown());
				if (captureStdout && stdoutRunner.getThrown() != null)
					throw new AmiDatasourceException(AmiDatasourceException.SYNTAX_ERROR, stdoutRunner.getThrown());
				session.waitForCondition(ChannelCondition.EXIT_SIGNAL | ChannelCondition.EXIT_SIGNAL, tc.getTimeoutMillisRemaining());
			}

			List<Table> r = new ArrayList<Table>(3);
			int exitCode = session == null ? -1 : OH.noNull(session.getExitStatus(), -1);
			if (capture == null) {
				r.add(stdoutPiper.outTable);
				r.add(stderrPiper.outTable);
				BasicTable exitCodeTable = new BasicTable(Integer.class, "exitCode");
				exitCodeTable.getRows().addRow(exitCode);
				r.add(exitCodeTable);
			} else {
				for (String s : captureParts) {
					s = s.trim();
					if ("stdout".equalsIgnoreCase(s))
						r.add(stdoutPiper.outTable);
					else if ("stderr".equalsIgnoreCase(s))
						r.add(stderrPiper.outTable);
					else if ("exitCode".equalsIgnoreCase(s)) {
						BasicTable exitCodeTable = new BasicTable(Integer.class, "exitCode");
						exitCodeTable.setTitle("exitcode");
						exitCodeTable.getRows().addRow(exitCode);
						r.add(exitCodeTable);
					} else
						throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR,
								"_include expecting comma delimited list of either STDOUT,STDERR,EXITCODE.  Not: '" + s + "'");
				}
			}

			resultSink.setTables(r);

		} catch (AmiDatasourceException e) {
			LH.info(log, this.getServiceLocator().getTargetName(), ": Query error: ", e.getMessage());
			throw e;
		} catch (Exception e) {
			LH.info(log, this.getServiceLocator().getTargetName(), ": Query error: ", e.getMessage());
			throw OH.toRuntime(e);
		} finally {
			IOH.close(this.process);
			this.process = null;
		}
	}
	private void join(Thread t, long timeout) throws AmiDatasourceException {
		if (t == null || !t.isAlive())
			return;
		if (timeout <= 0)
			throw new AmiDatasourceException(AmiDatasourceException.TIMEOUT_EXCEEDED, "Timeout exceeded durring connection: " + timeout + " ms");
		try {
			t.join(timeout);
		} catch (InterruptedException e) {
		}
		if (t.isAlive())
			throw new AmiDatasourceException(AmiDatasourceException.TIMEOUT_EXCEEDED, "Timeout exceeded durring connection: " + timeout + " ms");
	}

	private static boolean available(InputStream is) {
		try {
			return is.read() != -1;
		} catch (IOException e) {
			return false;
		}
	}

	private boolean runProcess(String command, final byte[] stdin, TimeoutController tc) throws AmiDatasourceException {
		LH.info(log, this.getServiceLocator().getTargetName(), ": Running query");
		String publicKeyFile = this.options.get(OPTION_PUBLIC_KEY_FILE);
		final char[] publicKey;
		byte mode;
		if (publicKeyFile != null) {
			File file = new File(publicKeyFile);
			if (!file.isFile())
				throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "publicKeyFile not found: " + IOH.getFullPath(file));
			if (!file.canRead())
				throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "publicKeyFile not readable: " + IOH.getFullPath(file));
			try {
				publicKey = IOH.readText(file).toCharArray();
			} catch (Exception e) {
				throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "Error reading publicKeyFile: " + IOH.getFullPath(file), e);
			}
			mode = AmiSshClient.AUTHMODE_PUBLICKEY;
		} else {
			String authMode = this.options.get(OPTION_AUTH_MODE);
			if (authMode == null || authMode.equals("password"))
				mode = AmiSshClient.AUTHMODE_PASSWORD;
			else if (authMode.equals("keyboardInteractive"))
				mode = AmiSshClient.AUTHMODE_KEYBOARD_INTERACTIVE;
			else
				throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "authMode option must be either 'password' or 'keyboardInteractive'");
			publicKey = null;
		}
		boolean useDumbPty = "true".equals(this.options.get(OPTION_USE_DUMB_PTY));
		long timeout = tc.getTimeoutMillisRemaining();
		AmiSshClient p = this.process = new AmiSshClient(hostName, port, this.serviceLocator.getUsername(),
				this.serviceLocator.getPassword() == null ? null : new String(this.serviceLocator.getPassword()), command, stdin, (int) timeout, mode, publicKey, useDumbPty);
		LH.info(log, this.getServiceLocator().getTargetName(), ": Running Process, timeout=", timeout);
		p.run();
		if (p.isComplete()) {
			switch (this.process.getExitCode()) {
				case AmiSshClient.TIMEOUT:
					throw new AmiDatasourceException(AmiDatasourceException.TIMEOUT_EXCEEDED, "Timeout exceeded durring connection: " + timeout + " ms");
				case AmiSshClient.AUTH_FAILED:
					throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "Authentication failed");
				case AmiSshClient.UNKNOWN_HOST:
					throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "Unknown Host");
				case AmiSshClient.GENERAL_ERROR:
					throw new AmiDatasourceException(AmiDatasourceException.CONNECTION_FAILED, "General Error", this.process.getException());
				default:
					return true;
			}
		} else if (this.process == null) {
			LH.info(log, this.getServiceLocator().getTargetName(), ": Running Process was cancelled");
			return false;
		} else {
			return true;
		}
	}
	@Override
	public boolean cancelQuery() {
		LH.info(log, this.getServiceLocator().getTargetName(), ": Query Cancelled");
		AmiSshClient p = process;
		if (p != null) {
			IOH.close(p);
			this.process = null;
			return true;
		}
		return false;
	}

	@Override
	public void processUpload(AmiCenterUpload upload, AmiCenterQueryResult results, AmiDatasourceTracker tracker) throws AmiDatasourceException {
		throw new AmiDatasourceException(AmiDatasourceException.UNSUPPORTED_OPERATION_ERROR, "Upload to datasource");
	}

	public static class Piper implements Runnable {

		final private int limit;
		final private String query;
		final private OnlineTable table;
		private Table outTable;
		final private AmiDatasourceTracker debugSink;
		private InputStream me;
		private Closeable other;
		private Thread otherThread;
		private TimeoutController timeout;
		private String outputTablename;

		public Piper(OnlineTable table, int limit, String query, String outputTableName, AmiDatasourceTracker debugSink, InputStream me, Closeable other,
				TimeoutController timeout) {
			this.timeout = timeout;
			this.limit = limit;
			this.query = query;
			this.table = table;
			this.debugSink = debugSink;
			this.me = me;
			this.other = other;
			this.outputTablename = outputTableName;
		}

		@Override
		public void run() {
			LH.info(log, "Running");
			this.outTable = AmiDatasourceUtils.processOnlineTable(query, limit, "cmd", this.table, this.debugSink, timeout);
			if (this.outTable != null)
				this.outTable.setTitle(this.outputTablename);
			LH.info(log, "Done");
			try {
				if (available(this.me)) {
					LH.info(log, "Limit was reached, Closing process");
					IOH.close(this.other);
				}
			} catch (Exception e) {
			}
		}
	}

	@Override
	public AmiServiceLocator getServiceLocator() {
		return this.serviceLocator;
	}

}
