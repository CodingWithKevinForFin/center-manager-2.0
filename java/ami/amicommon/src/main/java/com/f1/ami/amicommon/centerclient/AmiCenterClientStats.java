package com.f1.ami.amicommon.centerclient;

import java.lang.Thread.State;

import com.f1.ami.amicommon.AmiCommonProperties;
import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.web.auth.AmiAuthManager;
import com.f1.base.DateMillis;
import com.f1.container.ContainerTools;
import com.f1.utils.EH;
import com.f1.utils.GcMemoryMonitor;
import com.f1.utils.OH;
import com.f1.utils.structs.table.columnar.ColumnarTable;

public class AmiCenterClientStats implements Runnable {

	public static final String SERVICE_STATS = "AMIWEBSTATS";
	private final ColumnarTable stats;
	private final ContainerTools tools;
	private final int statsPeriodMs;
	private final int statsRetentionMs;
	private final Thread thread;
	private long soonestExpiresTime = Long.MAX_VALUE;

	public AmiCenterClientStats(ContainerTools tools) {
		this.tools = tools;
		stats = new ColumnarTable(//
				DateMillis.class, AmiConsts.PARAM_STATS_TIME, //
				Long.class, AmiConsts.TABLE_PARAM_E, //
				Long.class, AmiConsts.PARAM_STATS_USED_MEMORY, //
				Long.class, AmiConsts.PARAM_STATS_MAX_MEMORY, //
				Long.class, AmiConsts.PARAM_STATS_POST_GC_USED_MEMORY, //
				Integer.class, AmiConsts.PARAM_STATS_RUNNING_THREADS, //
				Long.class, AmiConsts.PARAM_STATS_ROWS, //
				Long.class, AmiConsts.PARAM_STATS_EVENTS, //
				Long.class, AmiConsts.PARAM_STATS_QUERIES, //
				Integer.class, AmiConsts.PARAM_STATS_UNIQUE_USERS, //
				Integer.class, AmiConsts.PARAM_STATS_MAX_USERS);

		this.statsPeriodMs = getPeriod(AmiCommonProperties.PROPERTY_AMI_STATS_TABLE_PERIOD_MS, AmiCommonProperties.DEFAULT_STATS_PERIOD_MS);
		this.statsRetentionMs = getPeriod(AmiCommonProperties.PROPERTY_AMI_STATS_TABLE_RETENTION_MS, AmiCommonProperties.DEFAULT_STATS_RETENTION_MS);
		this.thread = new Thread(this);
		this.thread.setName("AMiWebStats");
		this.thread.setDaemon(true);
		this.thread.start();
	}
	private int getPeriod(String key, int dflt) {
		int r = this.tools.getOptional(key, dflt);
		if (r < 1)
			throw new RuntimeException("Invalid value for " + key + ":  Must be possitive number, not " + r);
		return r;
	}

	private static void addRow(ColumnarTable stats, long time, long expires) {
		long usedMemory = EH.getTotalMemory() - EH.getFreeMemory();
		long maxMemory = EH.getMaxMemory();
		long postGcUsedMemory = GcMemoryMonitor.getLastUsedMemory();
		int runningThreads = 0;
		for (Thread thread : EH.getAllThreads())
			if (thread.getState() == State.RUNNABLE)
				runningThreads++;
		int usersCount = AmiAuthManager.INSTANCE.getUsersCount();
		int maxUsers = AmiAuthManager.INSTANCE.getMaxUsers();
		long rows = 0;
		long events = 0;
		long queries = 0;
		stats.getRows().addRow(new DateMillis(time), expires, usedMemory, maxMemory, postGcUsedMemory, runningThreads, rows, events, queries, usersCount, maxUsers);
	}

	@Override
	public void run() {
		for (;;) {
			synchronized (stats) {
				long now = EH.currentTimeMillis();
				long expires = now + statsRetentionMs;
				addRow(stats, now, expires);
				if (expires < soonestExpiresTime)
					soonestExpiresTime = expires;
				if (this.soonestExpiresTime <= now) {
					this.soonestExpiresTime = Long.MAX_VALUE;
					for (int i = this.stats.getSize() - 1; i >= 0; i--) {
						long rowExpires = stats.getRow(i).get(AmiConsts.TABLE_PARAM_E, Long.class);
						if (rowExpires <= now)
							stats.removeRow(i);
						else if (rowExpires < soonestExpiresTime)
							soonestExpiresTime = rowExpires;
					}
				}
			}
			OH.sleep(statsPeriodMs);
		}
	}

	public ColumnarTable getStats() {
		ColumnarTable r;
		synchronized (stats) {
			r = new ColumnarTable(stats);
		}
		long now = EH.currentTimeMillis();
		addRow(r, now, now + statsRetentionMs);
		return r;
	}

}
