package com.f1.utils;

public class RestartableThread {

	private ThreadGroup group;
	private Runnable target;
	private String name;
	private long stackSize = 0;
	private boolean deamon = Thread.currentThread().isDaemon();
	private int priority = Thread.NORM_PRIORITY;
	volatile private Thread currentThread;

	public RestartableThread() {
		this.currentThread = newThread();
	}

	public RestartableThread(Runnable target, String name) {
		this.target = target;
		this.name = name;
		this.currentThread = newThread();
	}

	public RestartableThread(Runnable target) {
		this.target = target;
		this.currentThread = newThread();
	}

	public RestartableThread(String name) {
		this.name = name;
		this.currentThread = newThread();
	}

	public RestartableThread(ThreadGroup group, Runnable target, String name, long stackSize) {
		this.group = group;
		this.target = target;
		this.name = name;
		this.stackSize = stackSize;
		this.currentThread = newThread();
	}

	public RestartableThread(ThreadGroup group, Runnable target, String name) {
		this.group = group;
		this.target = target;
		this.name = name;
		this.currentThread = newThread();
	}

	public RestartableThread(ThreadGroup group, Runnable target) {
		this.group = group;
		this.target = target;
		this.currentThread = newThread();
	}

	public RestartableThread(ThreadGroup group, String name) {
		this.group = group;
		this.name = name;
		this.currentThread = newThread();
	}

	protected Thread newThread() {
		Thread r;
		if (name == null)
			r = new Thread(group, target);
		else
			r = new Thread(group, target, name, stackSize);
		r.setPriority(getPriority());
		r.setDaemon(deamon);
		return r;
	}

	public synchronized void start() {
		if (isAlive())
			throw new IllegalStateException("already running");
		this.currentThread = newThread();
		this.currentThread.start();
	}

	public void setDaemon(boolean deamon) {
		this.deamon = deamon;
	}

	public boolean isDeamon() {
		return deamon;
	}

	public boolean isAlive() {
		return currentThread.isAlive();
	}

	public void interrupt() {
		currentThread.interrupt();
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getPriority() {
		return priority;
	}

}
