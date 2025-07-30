package com.f1.utils.concurrent;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public class SimpleExecutor implements Executor {

	public static final SimpleExecutor DEFAULT = new SimpleExecutor(NamedThreadFactory.DEFAULT);
	public static final SimpleExecutor DEFAULT_DEAMON = new SimpleExecutor(NamedThreadFactory.DEFAULT_DEAMON);

	final public ThreadFactory factory;

	public SimpleExecutor(ThreadFactory factory) {
		this.factory = factory;
	}
	@Override
	public void execute(Runnable command) {
		factory.newThread(command).start();
	}

}
