/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

public class ToDoException extends RuntimeException {

	public ToDoException(String message, Throwable cause) {
		super(message, cause);
	}

	public ToDoException(String message) {
		super(message);
	}

	public ToDoException() {
		super();
	}

	public static void throwNow(String message) {
		throw new ToDoException(message);
	}
	public static void throwNow() {
		throw new ToDoException();
	}

}
