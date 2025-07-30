package com.f1.speedlogger.impl;

public class RollFileException extends RuntimeException {

	public RollFileException() {
		super();
	}

	public RollFileException(String message, Throwable cause) {
		super(message, cause);
	}

	public RollFileException(String message) {
		super(message);
	}

	public RollFileException(Throwable cause) {
		super(cause);
	}

}
