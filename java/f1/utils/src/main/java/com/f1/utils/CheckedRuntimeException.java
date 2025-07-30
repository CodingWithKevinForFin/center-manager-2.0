/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

public class CheckedRuntimeException extends RuntimeException {

	static public RuntimeException wrap(Throwable e) {
		if (e instanceof RuntimeException)
			return (RuntimeException) e;
		if (F1GlobalProperties.getReplaceCheckedExceptions())
			return new CheckedRuntimeException(e);
		else
			return new CheckedRuntimeException("see cause", e);
	}

	final private Throwable e;

	private CheckedRuntimeException(String message, Throwable e) {
		super(message, e);
		this.e = e;
	}

	private CheckedRuntimeException(Throwable e) {
		super(SH.noNull(e.getMessage()), e.getCause());
		this.e = e;
		this.setStackTrace(e.getStackTrace());
	}

	public Throwable getCheckedException() {
		return e;
	}
}
