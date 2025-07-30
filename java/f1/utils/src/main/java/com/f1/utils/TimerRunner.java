package com.f1.utils;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TimerRunner implements Runnable {

	private static final Logger log = Logger.getLogger(CronTab.class.getName());
	final private Runnable runnable;
	final private Timer timer;
	private int remaining;
	AtomicBoolean isRunning = new AtomicBoolean(false);

	public TimerRunner(Timer timer, Runnable runnable) {
		this.runnable = runnable;
		this.timer = timer;
	}

	/**
	 * Creates a new thread and begins running the job on it.
	 * 
	 * @param deamon
	 *            Marks this thread as a daemon thread. If true, the Java Virtual Machine exits even if this crontab is running. please note, this means the job may exit while in
	 *            process. This could be avoided by having the job kick off a non-daemon thread to do the actual work see {@link ThreadRunner} for details.
	 */
	public void start(boolean deamon) {
		Thread t = new Thread(this, "Crontab: " + runnable.getClass().getSimpleName());
		t.setDaemon(deamon);
		t.start();
	}

	@Override
	public void run() {
		if (!isRunning.compareAndSet(false, true))
			throw new IllegalStateException("Already running");
		try {
			long last = 0;
			while (remaining > 0 || remaining == -1) {
				long now = System.currentTimeMillis();
				if (now == last) // Ensure that can't run twice in one
									// millisecond
					now++;
				long nextOccurance = timer.calculateNextOccurance(now);
				if (log.isLoggable(Level.FINE))
					LH.fine(log, "Scheduled for ", new Date(nextOccurance), ": ", runnable.getClass().getName());
				while (nextOccurance > now) {
					if (!OH.sleep(nextOccurance - now)) {
						LH.info(log, "crontab Interrupted. Stopping.");
						return;
					}
					now = System.currentTimeMillis();
				}
				try {
					LH.fine(log, "Running Crontab: ", runnable.getClass().getName());
					last = now;
					runnable.run();
					LH.fine(log, "Crontab completed: ", runnable.getClass().getName());
				} catch (Throwable t) {
					try {
						LH.warning(log, "Error running event", t);
					} catch (Throwable t2) {
						LH.severe(log, "Critical error logging error, crontab stopping", t2);
						return;
					}
				}
				if (remaining != -1)
					remaining--;
			}
		} finally {
			isRunning.set(false);
		}
	}

}
