package com.f1.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Formatter;
import java.util.Locale;

public class FastPrintStream extends FastFilterOutputStream implements Println {

	private boolean autoFlush = false;
	private boolean error = false;
	private Formatter formatter;

	private FastOutputStreamWriter inner;

	public FastPrintStream(OutputStream out) {
		this(out, false);
	}

	private FastPrintStream(boolean autoFlush, OutputStream out) {
		super(out);
		if (out == null)
			throw new NullPointerException("Null output stream");
		this.autoFlush = autoFlush;
	}

	private void init(FastOutputStreamWriter inner) {
		this.inner = inner;
	}

	public FastPrintStream(OutputStream out, boolean autoFlush) {
		this(autoFlush, out);
		init(new FastOutputStreamWriter(this));
	}

	public FastPrintStream(OutputStream out, boolean autoFlush, String encoding) throws UnsupportedEncodingException {
		this(autoFlush, out);
		init(new FastOutputStreamWriter(this, encoding));
	}

	public FastPrintStream(String fileName) throws FileNotFoundException {
		this(false, new FileOutputStream(fileName));
		init(new FastOutputStreamWriter(this));
	}

	public FastPrintStream(String fileName, String csn) throws FileNotFoundException, UnsupportedEncodingException {
		this(false, new FileOutputStream(fileName));
		init(new FastOutputStreamWriter(this, csn));
	}

	public FastPrintStream(File file) throws FileNotFoundException {
		this(false, new FileOutputStream(file));
		init(new FastOutputStreamWriter(this));
	}

	public FastPrintStream(File file, String csn) throws FileNotFoundException, UnsupportedEncodingException {
		this(false, new FileOutputStream(file));
		init(new FastOutputStreamWriter(this, csn));
	}

	private void ensureOpen() throws IOException {
		if (out == null)
			throw new IOException("Stream closed");
	}

	@Override
	public void flush() {
		try {
			ensureOpen();
			out.flush();
		} catch (IOException x) {
			error = true;
		}
	}

	private boolean closing = false;

	@Override
	public void close() {
		if (!closing) {
			closing = true;
			try {
				out.close();
			} catch (IOException x) {
				error = true;
			}
			inner = null;
			out = null;
		}
	}

	public boolean checkError() {
		if (out != null)
			flush();
		if (out instanceof FastPrintStream) {
			FastPrintStream ps = (FastPrintStream) out;
			return ps.checkError();
		}
		return error;
	}

	protected void setError() {
		error = true;
	}

	protected void clearError() {
		error = false;
	}

	@Override
	public void write(int b) {
		try {
			ensureOpen();
			out.write(b);
			if ((b == '\n') && autoFlush)
				out.flush();
		} catch (InterruptedIOException x) {
			Thread.currentThread().interrupt();
		} catch (IOException x) {
			error = true;
		}
	}

	@Override
	public void write(byte buf[], int off, int len) {
		try {
			ensureOpen();
			out.write(buf, off, len);
			if (autoFlush)
				out.flush();
		} catch (InterruptedIOException x) {
			Thread.currentThread().interrupt();
		} catch (IOException x) {
			error = true;
		}
	}

	private void write(char buf[]) {
		try {
			ensureOpen();
			inner.write(buf);
			inner.flushBuffer();
			if (autoFlush) {
				for (int i = 0; i < buf.length; i++)
					if (buf[i] == '\n')
						out.flush();
			}
		} catch (InterruptedIOException x) {
			Thread.currentThread().interrupt();
		} catch (IOException x) {
			error = true;
		}
	}

	private void write(String s) {
		try {
			ensureOpen();
			inner.write(s);
			inner.flushBuffer();
			if (autoFlush && (s.indexOf('\n') >= 0))
				out.flush();
		} catch (InterruptedIOException x) {
			Thread.currentThread().interrupt();
		} catch (IOException x) {
			error = true;
		}
	}
	private void write(CharSequence s) {
		write(s, 0, s.length());
	}
	private void write(CharSequence s, int offset, int length) {
		try {
			ensureOpen();
			inner.write(s, offset, length);
			inner.flushBuffer();
			if (autoFlush && (SH.indexOf(s, '\n', 0) >= 0))
				out.flush();
		} catch (InterruptedIOException x) {
			Thread.currentThread().interrupt();
		} catch (IOException x) {
			error = true;
		}
	}

	private void newLine() {
		try {
			ensureOpen();
			inner.newLine();
			inner.flushBuffer();
			if (autoFlush)
				out.flush();
		} catch (InterruptedIOException x) {
			Thread.currentThread().interrupt();
		} catch (IOException x) {
			error = true;
		}
	}

	public void print(boolean b) {
		write(b ? "true" : "false");
	}

	private char[] buf = new char[1];

	public void print(char c) {
		buf[0] = c;
		write(buf);
	}

	public void print(int i) {
		write(SH.toString(i));
	}

	public void print(long l) {
		write(SH.toString(l));
	}

	public void print(float f) {
		write(String.valueOf(f));
	}

	public void print(double d) {
		write(String.valueOf(d));
	}

	public void print(char s[]) {
		write(s);
	}

	public FastPrintStream print(CharSequence s) {
		write(s == null ? "null" : s);
		return this;
	}

	public FastPrintStream print(Object obj) {
		write(String.valueOf(obj));
		return this;
	}

	public FastPrintStream println() {
		newLine();
		return this;
	}
	public void println(boolean value) {
		print(value);
		newLine();
	}

	public void println(char value) {
		print(value);
		newLine();
	}

	public void println(int value) {
		print(value);
		newLine();
	}

	public void println(long value) {
		print(value);
		newLine();
	}

	public void println(float value) {
		print(value);
		newLine();
	}

	public void println(double value) {
		print(value);
		newLine();
	}

	public void println(char value[]) {
		print(value);
		newLine();
	}

	public void println(String value) {
		print(value);
		newLine();
	}

	public FastPrintStream println(Object value) {
		String s = String.valueOf(value);
		print(s);
		newLine();
		return this;
	}
	public FastPrintStream println(CharSequence value) {
		write(value == null ? "null" : value);
		newLine();
		return this;
	}

	public FastPrintStream printf(String format, Object... args) {
		return format(format, args);
	}

	public FastPrintStream printf(Locale l, String format, Object... args) {
		return format(l, format, args);
	}

	public FastPrintStream format(String format, Object... args) {
		try {
			ensureOpen();
			if ((formatter == null) || (formatter.locale() != Locale.getDefault()))
				formatter = new Formatter((Appendable) this);
			formatter.format(Locale.getDefault(), format, args);
		} catch (InterruptedIOException x) {
			Thread.currentThread().interrupt();
		} catch (IOException x) {
			error = true;
		}
		return this;
	}

	public FastPrintStream format(Locale l, String format, Object... args) {
		try {
			ensureOpen();
			if ((formatter == null) || (formatter.locale() != l))
				formatter = new Formatter(this, l);
			formatter.format(l, format, args);
		} catch (InterruptedIOException x) {
			Thread.currentThread().interrupt();
		} catch (IOException x) {
			error = true;
		}
		return this;
	}

	public FastPrintStream append(CharSequence csq) {
		if (csq == null)
			print("null");
		else
			print(csq.toString());
		return this;
	}

	public FastPrintStream append(CharSequence csq, int start, int end) {
		CharSequence cs = (csq == null ? "null" : csq);
		write(cs, start, end);
		return this;
	}

	public FastPrintStream append(char c) {
		print(c);
		return this;
	}

	@Override
	public void print(String sb) {
		write(sb == null ? "null" : sb);
	}

}
