package com.f1.utils;

import java.io.IOException;

public class ConnectionIOException extends IOException {

	private static final int MAX_LENGTH = 1000;
	private byte[] errorStream;

	public ConnectionIOException() {
		super();
	}

	public ConnectionIOException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConnectionIOException(String message) {
		super(message);
	}

	public ConnectionIOException(Throwable cause) {
		super(cause);
	}
	public ConnectionIOException(byte[] errorStream, Throwable cause) {
		super(cause);
		this.errorStream = errorStream;
	}

	public ConnectionIOException(byte[] errorStream) {
		super();
		this.errorStream = errorStream;
	}

	public byte[] getErrorStream() {
		return errorStream;
	}

	@Override
	public String getMessage() {
		if (errorStream == null)
			return super.getMessage();
		if (SH.isAscii(errorStream, .95)) {
			if (errorStream.length < MAX_LENGTH)
				return super.getMessage() + " " + new String(errorStream);
			else
				return super.getMessage() + " " + SH.ddd(new String(errorStream), MAX_LENGTH);
		} else
			return super.getMessage() + "(Error Stream contains " + errorStream.length + " bytes)";
	}
}
