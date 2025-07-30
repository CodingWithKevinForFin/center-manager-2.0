package com.f1.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Executor;

public class ProcessExecutor {

	private static final int DEFAULT_BUFFER_SIZE = 2048;
	private static final int NO_EXIT_CODE = -1;
	final private Process process;
	final private Executor executor;
	private MonitoredRunnable stdErrRunner = null;
	private MonitoredRunnable stdOutRunner = null;
	private MonitoredRunnable stdInRunner = null;
	private int bufferSize = DEFAULT_BUFFER_SIZE;

	public ProcessExecutor(Executor exec, String command) throws IOException {
		this.executor = exec;
		this.process = Runtime.getRuntime().exec(command);
	}

	public void pipeStdErrTo(OutputStream stdErr) {
		if (stdErrRunner != null)
			throw new IllegalStateException("already piping stderr");
		final StreamPiper piper = new StreamPiper(process.getErrorStream(), stdErr, bufferSize);
		stdErrRunner = new MonitoredRunnable(piper);
		executor.execute(stdErrRunner);
	}
	public void pipeStdOutTo(OutputStream stdOut) {
		if (stdOutRunner != null)
			throw new IllegalStateException("already piping stdout");
		final StreamPiper piper = new StreamPiper(process.getInputStream(), stdOut, bufferSize);
		stdOutRunner = new MonitoredRunnable(piper);
		executor.execute(stdOutRunner);
	}
	public void pipeToStdin(InputStream in) {
		if (stdInRunner != null)
			throw new IllegalStateException("already piping stdin");
		final StreamPiper piper = new StreamPiper(in, process.getOutputStream(), bufferSize);
		stdInRunner = new MonitoredRunnable(piper);
	}

	public InputStream getStdOut() {
		if (stdOutRunner != null)
			throw new IllegalStateException("already piping stdout");
		return process.getInputStream();
	}
	public InputStream getStdErr() {
		if (stdErrRunner != null)
			throw new IllegalStateException("already piping stderr");
		return process.getErrorStream();
	}
	public OutputStream getStdIn() {
		if (stdInRunner != null)
			throw new IllegalStateException("already piping stdin");
		return process.getOutputStream();
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public int getExitCode() {
		try {
			return process.exitValue();
		} catch (IllegalThreadStateException e) {
			return NO_EXIT_CODE;
		}
	}

	public int waitFor() throws InterruptedException {
		try {
			int r = process.waitFor();
			if (stdInRunner != null)
				stdInRunner.waitUntilComplete();
			if (stdOutRunner != null)
				stdOutRunner.waitUntilComplete();
			if (stdErrRunner != null)
				stdErrRunner.waitUntilComplete();
			return r;
		} finally {
			IOH.close(process.getInputStream());
			IOH.close(process.getOutputStream());
			IOH.close(process.getErrorStream());
		}
	}

	public void setBufferSize(int bufferSize) {
		if (stdOutRunner != null)
			throw new IllegalStateException("already piping stdout");
		if (stdErrRunner != null)
			throw new IllegalStateException("already piping stderr");
		if (stdInRunner != null)
			throw new IllegalStateException("already piping stdin");
		if (bufferSize < 1)
			throw new IllegalArgumentException(SH.toString(bufferSize));
		this.bufferSize = bufferSize;
	}

}
