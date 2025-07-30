package com.f1.utils.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {
	public static final ThreadFactory DEFAULT = new NamedThreadFactory("F1-", false);
	public static final ThreadFactory DEFAULT_DEAMON = new NamedThreadFactory("F1-", true);

	final private AtomicInteger count = new AtomicInteger();
	final private String prefix;
	final private String suffix;
	private boolean startAsDaemon;

	public NamedThreadFactory(String prefix, boolean startAsDaemon) {
		this(prefix, "", startAsDaemon);
	}

	public NamedThreadFactory(String prefix, String suffix, boolean startAsDaemon) {
		this.prefix = prefix;
		this.suffix = suffix;
		this.startAsDaemon = startAsDaemon;
	}

	@Override
	public Thread newThread(Runnable runnable) {
		Thread r = new Thread(runnable, prefix + count.incrementAndGet() + suffix);
		r.setDaemon(startAsDaemon);
		return r;
	}

}
