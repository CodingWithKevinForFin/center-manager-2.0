package com.f1.utils;

/**
 * Indicates that an expected state was inconsistent failed (not
 */
public class AssertionException extends RuntimeException {

	public AssertionException() {
		super();
	}

	public AssertionException(String message_, Throwable cause_) {
		super(message_, cause_);
	}

	public AssertionException(String message_) {
		super(message_);
	}

	public AssertionException(Throwable cause_) {
		super(cause_);
	}

}
