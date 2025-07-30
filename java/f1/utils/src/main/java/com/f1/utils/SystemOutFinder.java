package com.f1.utils;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;

public class SystemOutFinder extends PrintStream {

	private PrintStream inner;
	private String type;

	public SystemOutFinder(String type, PrintStream out) {
		super(out);
		this.inner = out;
		this.type = type;
	}

	public static void init() {
		if (!(System.out instanceof SystemOutFinder))
			System.setOut(new SystemOutFinder("out", System.out));
		if (!(System.err instanceof SystemOutFinder))
			System.setErr(new SystemOutFinder("err", System.err));

	}
	static private void append(PrintStream sb, long day) {
		sb.print(SH.toString(day));
	}

	private PrintStream onCalled() {
		StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		if (ste != null) {
			for (StackTraceElement st : ste)
				if ("com.f1.speedlogger.impl.BasicSpeedLoggerSink".equals(st.getClassName()) || "com.f1.bootstrap.Bootstrap".equals(st.getClassName()))
					return inner;

			if (ste.length > 3) {
				if (ste[3].getClassName().startsWith("java.lang.Throwable")) {
					for (int i = 4; i < ste.length; i++)
						if (!ste[i].getClassName().contains("Exception") && !ste[i].getClassName().contains("Throwable")) {
							inner.print(getClass().getSimpleName() + " | System." + type + "." + ste[i - 1].getMethodName() + "(..) | " + ste[i] + " | ");
							return inner;
						}

				}
				long daytime = System.currentTimeMillis();
				long day = daytime / 86400000;
				long time = daytime % 86400000;
				long ms = time % 1000;
				long sc = (time / 1000) % 60;
				long mn = (time / 60000) % 60;
				long hr = (time / 3600000);
				final PrintStream sb = new DummyPrintStream(new FastByteArrayOutputStream());

				sb.append(getClass().getSimpleName());
				sb.append(" | ");
				sb.append(Thread.currentThread().getName());
				sb.append(" | ");
				append(sb, day);
				sb.append(" ");
				if (hr < 10)
					sb.append('0');
				append(sb, hr);
				sb.append(':');
				if (mn < 10)
					sb.append('0');
				append(sb, mn);
				sb.append(':');
				if (sc < 10)
					sb.append('0');
				append(sb, sc);
				sb.append('.');
				if (ms < 10)
					sb.append('0');
				if (ms < 100)
					sb.append('0');
				append(sb, ms);
				sb.append(" | System.").append(type).append(".").append(ste[2].getMethodName()).append("(..)");
				sb.append(" | ");
				sb.append(ste[3].toString());
				sb.append(" | ");
				sb.flush();
				return sb;
			}
		}
		return inner;

	}
	public void write(byte[] b) throws IOException {
		final PrintStream ps = onCalled();
		ps.write(b);
		writeToInner(ps);
	}

	public void write(int b) {
		final PrintStream ps = onCalled();
		ps.write(b);
		writeToInner(ps);
	}

	public void write(byte[] buf, int off, int len) {
		final PrintStream ps = onCalled();
		ps.write(buf, off, len);
		writeToInner(ps);
	}

	public void print(boolean b) {
		final PrintStream ps = onCalled();
		ps.print(b);
		writeToInner(ps);
	}

	public void print(char c) {
		final PrintStream ps = onCalled();
		ps.print(c);
		writeToInner(ps);
	}

	public void print(int i) {
		final PrintStream ps = onCalled();
		ps.print(i);
		writeToInner(ps);
	}

	public void print(long l) {
		final PrintStream ps = onCalled();
		ps.print(l);
		writeToInner(ps);
	}

	public void print(float f) {
		final PrintStream ps = onCalled();
		ps.print(f);
		writeToInner(ps);
	}

	public void print(double d) {
		final PrintStream ps = onCalled();
		ps.print(d);
		writeToInner(ps);
	}

	public void print(char[] s) {
		final PrintStream ps = onCalled();
		ps.print(s);
		writeToInner(ps);
	}

	public void print(String s) {
		final PrintStream ps = onCalled();
		ps.print(s);
		writeToInner(ps);
	}

	public void print(Object obj) {
		final PrintStream ps = onCalled();
		ps.print(obj);
		writeToInner(ps);
	}

	public void println() {
		final PrintStream ps = onCalled();
		ps.println();
		writeToInner(ps);
	}

	public void println(boolean x) {
		final PrintStream ps = onCalled();
		ps.println(x);
		writeToInner(ps);
	}

	public void println(char x) {
		final PrintStream ps = onCalled();
		ps.println(x);
		writeToInner(ps);
	}

	public void println(int x) {
		final PrintStream ps = onCalled();
		ps.println(x);
		writeToInner(ps);
	}

	public void println(long x) {
		final PrintStream ps = onCalled();
		ps.println(x);
		writeToInner(ps);
	}

	public void println(float x) {
		final PrintStream ps = onCalled();
		ps.println(x);
		writeToInner(ps);
	}

	public void println(double x) {
		final PrintStream ps = onCalled();
		ps.println(x);
		writeToInner(ps);
	}

	public void println(char[] x) {
		final PrintStream ps = onCalled();
		ps.println(x);
		writeToInner(ps);
	}

	public void println(String x) {
		final PrintStream ps = onCalled();
		ps.println(x);
		writeToInner(ps);
	}

	public void println(Object x) {
		final PrintStream ps = onCalled();
		ps.println(x);
		writeToInner(ps);
	}

	final public PrintStream printf(String format, Object... args) {
		final PrintStream ps = onCalled();
		ps.printf(format, args);
		writeToInner(ps);
		return inner;
	}

	final public PrintStream printf(Locale l, String format, Object... args) {
		final PrintStream ps = onCalled();
		ps.printf(l, format, args);
		writeToInner(ps);
		return inner;
	}

	final public PrintStream format(String format, Object... args) {
		final PrintStream ps = onCalled();
		ps.format(format, args);
		writeToInner(ps);
		return inner;
	}

	final public PrintStream format(Locale l, String format, Object... args) {
		final PrintStream ps = onCalled();
		ps.format(l, format, args);
		writeToInner(ps);
		return inner;
	}

	final public PrintStream append(CharSequence csq) {
		final PrintStream ps = onCalled();
		final PrintStream r = ps.append(csq);
		writeToInner(ps);
		return inner;
	}

	final public PrintStream append(CharSequence csq, int start, int end) {
		final PrintStream ps = onCalled();
		ps.append(csq, start, end);
		writeToInner(ps);
		return inner;
	}

	final public PrintStream append(char c) {
		final PrintStream ps = onCalled();
		ps.append(c);
		writeToInner(ps);
		return inner;
	}

	private void writeToInner(PrintStream ps) {
		if (ps instanceof DummyPrintStream) {
			DummyPrintStream dps = (DummyPrintStream) ps;
			try {
				dps.getBuf().writeTo(this.inner);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static class DummyPrintStream extends PrintStream {

		private FastByteArrayOutputStream buf;

		public DummyPrintStream(FastByteArrayOutputStream out) {
			super(out);
			this.buf = out;
		}
		public FastByteArrayOutputStream getBuf() {
			return buf;

		}

	}

}
