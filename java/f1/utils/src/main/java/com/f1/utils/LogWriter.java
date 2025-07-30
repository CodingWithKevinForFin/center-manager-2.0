package com.f1.utils;

import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogWriter extends Writer {

	private Logger log;
	final private Level level;

	public LogWriter(Logger log, Level level) {
		this.log = log;
		this.level = level;
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
	LH.log(log,level, new String(cbuf, off, len));
	}

	@Override
	public void flush() throws IOException {
	}

	@Override
	public void close() throws IOException {
		log = null;
	}

}

