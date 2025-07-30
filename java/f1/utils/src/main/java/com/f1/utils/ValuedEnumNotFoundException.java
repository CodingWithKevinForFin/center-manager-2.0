package com.f1.utils;

public class ValuedEnumNotFoundException extends RuntimeException {

	public ValuedEnumNotFoundException() {
		super();
	}

	public ValuedEnumNotFoundException(String message_, Throwable cause_) {
		super(message_, cause_);
	}

	public ValuedEnumNotFoundException(String message_) {
		super(message_);
	}

	public ValuedEnumNotFoundException(Throwable cause_) {
		super(cause_);
	}

}

