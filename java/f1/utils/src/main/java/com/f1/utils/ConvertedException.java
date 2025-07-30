/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

public class ConvertedException extends DetailedException {

	final private String exceptionClassName;
	final private String origMessage;
	final private String originatingHostName;
	final private String pid;

	public String getExceptionClassName() {
		return exceptionClassName;
	}

	public ConvertedException(String originatingHostName, String pid, String exceptionClassName, String message) {
		super(message);
		this.exceptionClassName = exceptionClassName;
		this.pid = pid;
		this.origMessage = message;
		this.originatingHostName = originatingHostName;
	}

	public String getOrigMessage() {
		return origMessage;
	}

	public String getOriginatingHostName() {
		return originatingHostName;
	}

	public String getPid() {
		return pid;
	}

}
