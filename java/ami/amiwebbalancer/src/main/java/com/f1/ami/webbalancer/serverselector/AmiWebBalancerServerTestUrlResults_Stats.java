package com.f1.ami.webbalancer.serverselector;

import com.f1.ami.webbalancer.AmiWebBalancerServerInstance;

public class AmiWebBalancerServerTestUrlResults_Stats implements AmiWebBalancerServerTestUrlResults {

	final private AmiWebBalancerServerInstance serverInstance;
	final private boolean isAlive;
	final private double cpuPct;
	final private int logins;
	final private int sessions;
	final private long memMax;
	final private long memUsed;
	final private long memUsedAfterGc;
	final private long startTime;
	final private long threadsRunnable;
	final private long threadsTotal;
	final private boolean isLegacy;

	public AmiWebBalancerServerTestUrlResults_Stats(AmiWebBalancerServerInstance serverInstance, double cpuPct, int logins, int sessions, long memMax, long memUsed,
			long memUsedAfterGc, long startTime, long threadsRunnable, long threadsTotal) {
		this.serverInstance = serverInstance;
		this.isAlive = true;
		this.isLegacy = false;
		this.cpuPct = cpuPct;
		this.logins = logins;
		this.sessions = sessions;
		this.memMax = memMax;
		this.memUsed = memUsed;
		this.memUsedAfterGc = memUsedAfterGc;
		this.startTime = startTime;
		this.threadsRunnable = threadsRunnable;
		this.threadsTotal = threadsTotal;
	}

	public AmiWebBalancerServerTestUrlResults_Stats(AmiWebBalancerServerInstance serverInstance, boolean isAlive) {
		this.serverInstance = serverInstance;
		this.isAlive = isAlive;
		this.isLegacy = true;
		this.cpuPct = -1;
		this.logins = -1;
		this.sessions = -1;
		this.memMax = -1;
		this.memUsed = -1;
		this.memUsedAfterGc = -1;
		this.startTime = -1;
		this.threadsRunnable = -1;
		this.threadsTotal = -1;
	}

	@Override
	public boolean isAlive() {
		return isAlive;
	}

	public AmiWebBalancerServerInstance getServerInstance() {
		return serverInstance;
	}

	public double getCpuPct() {
		return cpuPct;
	}

	public int getLogins() {
		return isLegacy ? this.serverInstance.getActiveSessionsCount() : logins;
	}

	public int getSessions() {
		return sessions;
	}

	public long getMemMax() {
		return memMax;
	}

	public long getMemUsed() {
		return memUsed;
	}

	public long getMemUsedAfterGc() {
		return memUsedAfterGc;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getThreadsRunnable() {
		return threadsRunnable;
	}

	public long getThreadsTotal() {
		return threadsTotal;
	}

	public boolean isLegacy() {
		return isLegacy;
	}

}
