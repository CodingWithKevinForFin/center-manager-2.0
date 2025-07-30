package com.f1.transportManagement;

public abstract class AbstractSession {

	public void init() throws Exception {
	}

	public void start() throws Exception {
	}

	public void shutdown() throws Exception {
	}

	public abstract boolean isDone();

	public abstract boolean isActive();
}