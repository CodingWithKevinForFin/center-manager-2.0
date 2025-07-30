package com.f1.ami.amicommon;

import java.lang.Thread.State;
import java.util.logging.Logger;

import com.f1.container.Container;
import com.f1.utils.AH;
import com.f1.utils.EH;
import com.f1.utils.GcMemoryMonitor;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class AmiProcessStatsLogger implements Runnable {

	private static final Logger log = LH.get();
	private static final Logger amilog = Logger.getLogger("AMI_STATS.PROCESS");

	public static AmiProcessStatsLogger INSTANCE = new AmiProcessStatsLogger();

	private AmiProcessStatsLogger() {
		GcMemoryMonitor.getCount();
	}

	public void logStats() {
		long fm = EH.getFreeMemory();
		long mm = EH.getMaxMemory();
		long tm = EH.getTotalMemory();
		Thread[] threads = EH.getAllThreads();
		short threadsBlocked = 0, threadsNew = 0, threadsRunnable = 0, threadsTerminated = 0, threadsTimedWaiting = 0, threadsWaiting = 0;
		for (Thread thread : threads) {
			State threadState = thread.getState();
			switch (thread.getState()) {
				case BLOCKED:
					threadsBlocked++;
					break;
				case NEW:
					threadsNew++;
					break;
				case RUNNABLE:
					threadsRunnable++;
					break;
				case TERMINATED:
					threadsTerminated++;
					break;
				case TIMED_WAITING:
					threadsTimedWaiting++;
					break;
				case WAITING:
					threadsWaiting++;
					break;
				default:
					LH.info(log, "unknown thread state: ", threadState);
					continue;
			}
		}
		log(amilog, "Process", "freeMem", fm, "maxMem", mm, "totMem", tm, "popsGcMem", GcMemoryMonitor.getLastUsedMemory(), "gcCount", GcMemoryMonitor.getCount(), "threadBlocked",
				threadsBlocked, "threadsNew", threadsNew, "threadsRunnable", threadsRunnable, "threadsTimedWaiting", threadsTimedWaiting, "threadsWaiting", threadsWaiting);
	}
	public static void log(Logger log, String type, Object... keyValue) {
		StringBuilder sb = new StringBuilder();
		sb.append("O|T=\"").append(type).append('"');
		sb.append("|now=").append(System.currentTimeMillis()).append('L');
		if ((keyValue.length & 1) == 1) {
			LH.warning(log, "odd number: " + SH.join(',', keyValue));
		} else {
			for (int i = 0; i < keyValue.length;) {
				Object key = keyValue[i++];
				Object val = keyValue[i++];
				sb.append('|').append(key).append('=');
				AmiUtils.appendObject(sb, val);
			}
		}
		LH.info(log, sb);
	}
	@Override
	public void run() {
		boolean worked = true;
		for (;;) {
			try {
				logStats();
				worked = true;
				for (Runnable t : this.runners)
					t.run();
			} catch (Throwable e) {
				if (worked) {
					LH.warning(log, e);
				}
				worked = false;
			}
			OH.sleep(frequency);
		}
	}

	private volatile long frequency = 15000;
	private Runnable[] runners = new Runnable[0];
	static private Thread thread = null;

	synchronized public static void startup() {
		if (thread != null)
			return;
		String cn = EH.getStartupClassName();
		LH.info(amilog, "L|I=\"" + SH.afterLast(cn, '.', cn) + "\"|pid=" + EH.getPid());
		thread = new Thread(INSTANCE, "AmiProcessStatsLogger");
		thread.start();
	}

	public long getFrequency() {
		return frequency;
	}

	public void setFrequency(long frequency) {
		OH.assertGt(frequency, 0, "frequency");
		this.frequency = frequency;
	}

	synchronized public void addLogger(Runnable r) {
		this.runners = AH.append(this.runners, r);
	}

	public void registerBootstrap(Container container) {
		addLogger(new AmiStatsContainerLogger(container));
	}

}
