package com.f1.ami.center.ds;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f1.ami.amicommon.AmiDatasourceAdapter;
import com.f1.ami.amicommon.AmiDatasourceException;
import com.f1.ami.amicommon.AmiDatasourceTracker;
import com.f1.ami.amicommon.AmiServiceLocator;
import com.f1.ami.amicommon.msg.AmiCenterQuery;
import com.f1.ami.amicommon.msg.AmiCenterQueryResult;
import com.f1.ami.amicommon.msg.AmiCenterUpload;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.base.Generator;
import com.f1.base.Table;
import com.f1.container.ContainerTools;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.IOH;
import com.f1.utils.MonitoredRunnable;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.derived.TimeoutController;
import com.f1.utils.structs.table.online.OnlineTable;

public class AmiShellAdapter implements AmiDatasourceAdapter {

	public static Map<String, String> buildOptions() {
		return new HashMap<String, String>();
	}

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

	@Override
	public void init(ContainerTools tools, AmiServiceLocator serviceLocator) throws AmiDatasourceException {
		this.tools = tools;
		this.serviceLocator = serviceLocator;

	}

	@Override
	public List<AmiDatasourceTable> getTables(AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException {
		AmiDatasourceTable sample = tools.nw(AmiDatasourceTable.class);
		sample.setCollectionName(null);
		sample.setName("sample");
		sample.setColumns(Collections.EMPTY_LIST);
		sample.setDatasourceName(this.serviceLocator.getTargetName());
		sample.setCustomQuery("select * from cmd");
		if (EH.isWindows())
			sample.setCustomUse("_cmd=\"dir\"");
		else
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
		if (EH.isWindows())
			sample.setCustomUse("_cmd=\"dir\"");
		else
			sample.setCustomUse("_cmd=\"ls\"");
		return CH.l(sample);
	}

	private Process process;

	@Override
	public void processQuery(AmiCenterQuery query, AmiCenterQueryResult resultSink, AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException {
		Map<String, Object> directives = query.getDirectives();
		String command = AmiDatasourceUtils.getOptional(directives, "cmd");
		if (SH.isnt(command))
			throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "_cmd is required");
		String envString = AmiDatasourceUtils.getOptional(directives, "env");
		String stdinString = AmiDatasourceUtils.getOptional(directives, "stdin");
		String capture = AmiDatasourceUtils.getOptional(directives, "capture");
		String useHostEnvString = AmiDatasourceUtils.getOptional(directives, "useHostEnv");

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

		boolean useHostEnv;
		if (useHostEnvString != null) {
			if ("true".equals(useHostEnvString)) {
				useHostEnv = true;
			} else if ("false".equals(useHostEnvString))
				useHostEnv = false;
			else
				throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "_useEnv expecting true or false");
		} else
			useHostEnv = false;

		String args[];
		if (EH.isWindows())
			args = new String[] { "cmd.exe", "/c", command };
		else
			args = new String[] { "sh", "-c", command };

		final String[] env;
		if (useHostEnv && envString != null)
			env = AH.appendArray(EH.getEnv(), SH.splitWithEscape(',', '\\', envString));
		else if (envString != null)
			env = SH.splitWithEscape(',', '\\', envString);
		else if (useHostEnv)
			env = EH.getEnv();
		else
			env = OH.EMPTY_STRING_ARRAY;
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

			this.process = Runtime.getRuntime().exec(args, env, new File(OH.noNull(this.serviceLocator.getUrl(), ".")));

			int limit = query.getLimit();
			if (limit == NO_LIMIT)
				limit = Integer.MAX_VALUE;

			final MonitoredRunnable stdoutRunner;
			final Piper stdoutPiper;
			if (captureStdout) {
				stdoutStream.setStream(process.getInputStream());
				stdoutPiper = new Piper(stdoutOnlineTable, limit, query.getQuery(), "stdout", debugSink, tc);
				stdoutRunner = new MonitoredRunnable(stdoutPiper, false);
				Thread stdoutThread = new Thread(stdoutRunner, "STDOUT");
				stdoutThread.start();
			} else {
				stdoutRunner = new MonitoredRunnable(new NullPiper(process.getInputStream()), false);
				stdoutPiper = null;
			}

			final MonitoredRunnable stderrRunner;
			final Piper stderrPiper;
			if (captureStderr) {
				stderrStream.setStream(process.getErrorStream());
				stderrPiper = new Piper(stderrOnlineTable, limit, query.getQuery(), "stderr", debugSink, tc);
				stderrRunner = new MonitoredRunnable(stderrPiper, false);
				Thread stderrThread = new Thread(stderrRunner, "STDERR");
				stderrThread.start();
			} else {
				stderrRunner = new MonitoredRunnable(new NullPiper(process.getErrorStream()), false);
				stderrPiper = null;
			}

			if (stdin != null) {
				OutputStream out = process.getOutputStream();
				out.write(stdin);
				out.flush();
				out.close();
			} else {
				IOH.close(process.getOutputStream());
			}

			int exitCode = process.waitFor();
			if (captureStderr)
				stderrRunner.waitUntilComplete(tc.getTimeoutMillisRemaining());
			if (captureStdout)
				stdoutRunner.waitUntilComplete(tc.getTimeoutMillisRemaining());
			IOH.close(process.getInputStream());
			IOH.close(process.getOutputStream());
			IOH.close(process.getErrorStream());
			if (captureStderr && stderrRunner.getThrown() != null)
				throw new AmiDatasourceException(AmiDatasourceException.SYNTAX_ERROR, stderrRunner.getThrown());
			if (captureStdout && stdoutRunner.getThrown() != null)
				throw new AmiDatasourceException(AmiDatasourceException.SYNTAX_ERROR, stdoutRunner.getThrown());

			List<Table> r = new ArrayList<Table>(3);

			if (capture == null) {
				r.add(stdoutPiper.outTable);
				r.add(stderrPiper.outTable);
				BasicTable exitCodeTable = new BasicTable(Integer.class, "exitCode");
				exitCodeTable.setTitle("exitcode");
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
						exitCodeTable.getRows().addRow(exitCode);
						r.add(exitCodeTable);
					} else
						throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR,
								"_include expecting comma delimited list of either STDOUT,STDERR,EXITCODE.  Not: '" + s + "'");
				}
			}

			resultSink.setTables(r);

		} catch (AmiDatasourceException e) {
			throw e;
		} catch (Exception e) {
			throw OH.toRuntime(e);
		} finally {
			this.process = null;
		}
	}
	@Override
	public boolean cancelQuery() {
		Process p = process;
		if (p != null) {
			p.destroy();
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

		private int limit;
		private String query;
		private OnlineTable table;
		private Table outTable;
		private AmiDatasourceTracker debugSink;
		private TimeoutController timeout;
		private String outputTablename;

		public Piper(OnlineTable table, int limit, String query, String outputTableName, AmiDatasourceTracker debugSink, TimeoutController to) {
			this.debugSink = debugSink;
			this.limit = limit;
			this.query = query;
			this.table = table;
			this.timeout = to;
			this.outputTablename = outputTableName;
		}

		@Override
		public void run() {
			this.outTable = AmiDatasourceUtils.processOnlineTable(query, limit, "cmd", this.table, this.debugSink, timeout);
			if (this.outTable != null)
				this.outTable.setTitle(this.outputTablename);
		}

	}

	@Override
	public AmiServiceLocator getServiceLocator() {
		return this.serviceLocator;
	}

}
