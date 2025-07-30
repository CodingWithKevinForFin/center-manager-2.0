package com.f1.persist;

import com.f1.utils.DetailedException;

public class PersistException extends DetailedException {

	public PersistException() {
		super();
	}

	public PersistException(String message, Throwable cause) {
		super(message, cause);
	}

	public PersistException(String message) {
		super(message);
	}

	public PersistException(Throwable cause) {
		super(cause);
	}

}
