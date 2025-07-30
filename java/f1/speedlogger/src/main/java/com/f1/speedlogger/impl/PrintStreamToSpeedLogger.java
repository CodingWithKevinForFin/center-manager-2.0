package com.f1.speedlogger.impl;

import java.io.PrintStream;

import com.f1.speedlogger.SpeedLogger;
import com.f1.speedlogger.SpeedLoggerLevels;
import com.f1.utils.FastByteArrayOutputStream;

public class PrintStreamToSpeedLogger extends PrintStream {

	public static final int DEFAULT_STDOUT_LOGGER_LEVEL = SpeedLoggerLevels.INFO;
	public static final int DEFAULT_STDERR_LOGGER_LEVEL = SpeedLoggerLevels.ERROR;
	public static final SpeedLogger defaultStdOutLogger = SpeedLoggerInstance.getInstance().getLogger("STDOUT");
	public static final SpeedLogger defaultStdErrLogger = SpeedLoggerInstance.getInstance().getLogger("STDERR");
	private final PrintStream writer;
	private final FastByteArrayOutputStream dataOutputStream;
	private SpeedLogger logger;
	private int level;
	private final PrintStream inner;

	public static void init() {
		if (System.out instanceof PrintStreamToSpeedLogger)
			throw new IllegalStateException("stdout already init for speedlogger");
		if (System.err instanceof PrintStreamToSpeedLogger)
			throw new IllegalStateException("stderr already init for speedlogger");
		System.setOut(new PrintStreamToSpeedLogger(System.out, defaultStdOutLogger, DEFAULT_STDOUT_LOGGER_LEVEL));
		System.setErr(new PrintStreamToSpeedLogger(System.err, defaultStdErrLogger, DEFAULT_STDERR_LOGGER_LEVEL));
	}

	public PrintStreamToSpeedLogger(PrintStream inner, SpeedLogger logger, int loggerLevel) {
		super(inner);
		this.inner = inner;
		this.logger = logger;
		this.level = loggerLevel;
		dataOutputStream = new FastByteArrayOutputStream();
		writer = new PrintStream(dataOutputStream);
	}

	@Override
	public void write(int b) {
		super.write(b);
		synchronized (writer) {
			writer.write(b);
		}
	}

	@Override
	public void write(byte[] buf, int off, int len) {
		super.write(buf, off, len);
		synchronized (writer) {
			writer.write(buf, off, len);
		}
	}

	@Override
	public void println() {
		super.println();
		flushText();
	}

	@Override
	public void println(boolean x) {
		super.println(x);
		flushText();
	}

	@Override
	public void println(char x) {
		super.println(x);
		flushText();
	}

	@Override
	public void println(int x) {
		super.println(x);
		flushText();
	}

	@Override
	public void println(long x) {
		super.println(x);
		flushText();
	}

	@Override
	public void println(float x) {
		super.println(x);
		flushText();
	}

	@Override
	public void println(double x) {
		super.println(x);
		flushText();
	}

	@Override
	public void println(char[] x) {
		super.println(x);
		flushText();
	}

	private void flushText() {
		String s;
		synchronized (writer) {
			s = dataOutputStream.toString();
			dataOutputStream.reset();
		}
		if (s.endsWith("\n"))
			s = s.substring(0, s.length() - 1);
		logger.log(level, s);
	}

	@Override
	public void println(String x) {
		super.println(x);
		flushText();
	}

	@Override
	public void println(Object x) {
		super.println(x);
		flushText();
	}

	public PrintStream getInner() {
		return inner;
	}

}
