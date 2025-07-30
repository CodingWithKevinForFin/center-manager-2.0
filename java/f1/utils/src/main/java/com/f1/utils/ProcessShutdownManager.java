package com.f1.utils;

import java.util.Iterator;
import java.util.logging.Logger;

import com.f1.utils.concurrent.IdentityHashSet;

public class ProcessShutdownManager {

	private static final Logger log = LH.get();
	private static final long CLEANING_PERIOD_MS = 60000;
	public static final ProcessShutdownManager INSTANCE = new ProcessShutdownManager();
	private IdentityHashSet<Process> processes = new IdentityHashSet<Process>();
	private ShutdownHook shutdownHook;

	synchronized public void addProcessForDestroyOnShutdown(Process process) {
		ensureInit();
		this.processes.add(process);
	}

	private void ensureInit() {
		if (shutdownHook != null)
			return;
		this.shutdownHook = new ShutdownHook();
		Runtime.getRuntime().addShutdownHook(new Thread(this.shutdownHook, "ProcessShutdownHook"));
		new Thread(new DeadProcessCollector(), "ProcessShutdownManagerGc").start();
	}

	synchronized public boolean removeProcessFromDestroyOnShutdown(Process process) {
		return this.processes != null && this.processes.remove(process);
	}

	synchronized private void shutdownAllProcessesNow() {
		for (Process i : this.processes) {
			try {
				i.destroy();
			} catch (Throwable t) {
			}
		}
		this.processes = null;//don't let people add more processes!
	}
	synchronized private void removeAllDeadProcesses() {
		for (Iterator<Process> i = this.processes.iterator(); i.hasNext();) {
			Process t = i.next();
			try {
				t.exitValue();//wish there was a way to test w/o throwing an exception!
			} catch (IllegalThreadStateException th) {
				continue;
			} catch (Throwable th) {
				LH.severe(log, "Error testing state of process: ", t, th);
			}
			i.remove();
		}
	}

	private class ShutdownHook implements Runnable {

		@Override
		public void run() {
			shutdownAllProcessesNow();
		}
	}

	private class DeadProcessCollector implements Runnable {

		@Override
		public void run() {
			for (;;) {
				removeAllDeadProcesses();
				OH.sleep(CLEANING_PERIOD_MS);
			}
		}

	}

}
