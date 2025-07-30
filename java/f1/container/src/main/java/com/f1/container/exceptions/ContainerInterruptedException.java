/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container.exceptions;

import com.f1.base.Action;

public class ContainerInterruptedException extends ContainerException {

	final private Action action;

	public ContainerInterruptedException(String message, Throwable t, Action action) {
		super(message, t);
		this.action = action;
	}

	public Action getAction() {
		return action;
	}

}
