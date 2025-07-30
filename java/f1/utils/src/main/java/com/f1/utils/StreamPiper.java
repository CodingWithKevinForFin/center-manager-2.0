package com.f1.utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

public class StreamPiper implements Runnable {

	private static final Logger log = Logger.getLogger(StreamPiper.class.getName());

	private OutputStream out;
	private InputStream in;
	private int bufSize;

	private boolean autocloseSink;

	public StreamPiper(InputStream in, OutputStream out, int bufSize) {
		this.in = in;
		this.out = out;
		this.bufSize = bufSize;
	}

	@Override
	public void run() {
		byte[] buffer = new byte[bufSize];
		try {
			int len;
			while (-1 != (len = in.read(buffer)))
				out.write(buffer, 0, len);
			if (autocloseSink)
				out.close();
		} catch (Throwable e) {
			LH.warning(log, "Error while streaming... Thread ending & no longer piping", e);
		}
	}

	public void setAutocloseSink(boolean b) {
		this.autocloseSink = b;
	}

}

