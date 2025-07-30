/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container.exceptions;

public class ContainerTimeoutException extends RuntimeException {

	public ContainerTimeoutException() {
		super();
	}

	public ContainerTimeoutException(String message, Throwable cause) {
		super(message, cause);
	}

	public ContainerTimeoutException(String message) {
		super(message);
	}

	public ContainerTimeoutException(Throwable cause) {
		super(cause);
	}

}
