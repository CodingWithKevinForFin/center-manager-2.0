/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container.exceptions;

import com.f1.container.ContainerScope;

public class ProcessorException extends ContainerException {

	public ProcessorException() {
		super();
	}

	public ProcessorException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProcessorException(String message) {
		super(message);
	}

	public ProcessorException(Throwable cause) {
		super(cause);
	}

	public ProcessorException(ContainerScope cs, String message, Throwable cause) {
		super(cs, message, cause);
	}

	public ProcessorException(ContainerScope cs, String message) {
		super(cs, message);
	}

}
