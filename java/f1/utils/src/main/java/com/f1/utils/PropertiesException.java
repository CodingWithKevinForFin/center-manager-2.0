package com.f1.utils;

import com.f1.base.Legible;

public class PropertiesException extends RuntimeException implements Legible {

	@Override
	public String toLegibleString() {
		return toString();
	}

	public PropertiesException() {
		super();
	}

	public PropertiesException(String message, Throwable cause) {
		super(message, cause);
	}

	public PropertiesException(String message) {
		super(message);
	}

	public PropertiesException(Throwable cause) {
		super(cause);
	}

}
