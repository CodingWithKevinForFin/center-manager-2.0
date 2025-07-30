/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container.exceptions;

public class ContainerNestedException extends ContainerException {

	public ContainerNestedException() {
		super();
	}

	public ContainerNestedException(String message, Throwable cause) {
		super(message, cause);
	}

	public ContainerNestedException(String message) {
		super(message);
	}

	public ContainerNestedException(Throwable cause) {
		super(cause);
	}

}
