package com.f1.ami.amicommon.rest;

import java.lang.Thread.State;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.f1.ami.web.auth.AmiAuthUser;
import com.f1.container.ContainerTools;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.ContentType;
import com.f1.utils.EH;
import com.f1.utils.GcMemoryMonitor;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;

public class AmiRestPlugin_Stats implements AmiRestPlugin {

	private static Map<String, AmiRestStatsGetter> restStatsGetter = new HashMap<String, AmiRestStatsGetter>();
	private static AmiRestStatsGetter[] restStats = new AmiRestStatsGetter[0];

	synchronized public static void addStatsGetter(AmiRestStatsGetter stats) {
		if (restStatsGetter.containsKey(stats))
			throw new RuntimeException("Already registered: " + stats.getKey() + " ==> " + stats);
		restStatsGetter.put(stats.getKey(), stats);
		restStats = AH.append(restStats, stats);
	}

	private ContainerTools tools;

	@Override
	public void init(ContainerTools tools, PropertyController props) {
		this.tools = tools;

	}

	@Override
	public String getPluginId() {
		return "REST_STATS";
	}

	@Override
	public String getEndpoint() {
		return "stats";
	}

	@Override
	public void handler(AmiRestRequest rr, AmiAuthUser user) {
		com.sun.management.OperatingSystemMXBean osBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
		double cpuLoad = osBean.getProcessCpuLoad();
		long mm = EH.getMaxMemory();
		long tm = EH.getTotalMemory();
		long unallocated = mm - tm;
		long um = tm - EH.getFreeMemory();
		long lastUm = tm - GcMemoryMonitor.getLastFreeMemory();
		Thread[] threads = EH.getAllThreads();
		int threadsRunnable = 0;
		for (Thread thread : threads) {
			State threadState = thread.getState();
			switch (thread.getState()) {
				case RUNNABLE:
					threadsRunnable++;
					break;
				default:
					continue;
			}
		}
		long now = EH.currentTimeMillis();
		Map<String, Object> top = new HashMap<String, Object>();
		Map<String, Object> values = new HashMap<String, Object>();
		long startTime = EH.getStartTime();
		double threadsPct = toPct((double) threadsRunnable / threads.length);
		double memPct = toPct((double) Math.max(um, lastUm) / mm);
		if (rr.isDisplayText()) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS zzz");
			sdf.setTimeZone(EH.getGMT());
			rr.println(SH.centerAlign('=', " JAVA VIRTUAL MACHINE ", 40, false));
			rr.println("");
			rr.println("           Start Time: " + sdf.format(new Date(startTime)));
			rr.println("                  Now: " + sdf.format(new Date(now)));
			rr.println("             CPU Used: " + toPct(cpuLoad) + "%");
			rr.println("         Threads Used: " + threadsPct + "%");
			rr.println("Estimated Memory Used: " + memPct + "%");
			rr.println("          Used Memory: " + SH.formatMemory(um));
			rr.println(" Used Memory After GC: " + SH.formatMemory(lastUm));
			rr.println("           Max Memory: " + SH.formatMemory(mm));
			rr.println("         Total Memory: " + SH.formatMemory(tm));
			rr.println("  Garbage Collections: " + GcMemoryMonitor.getCount());
			rr.println("        Total Threads: " + threads.length);
			rr.println("      Running Threads: " + threadsRunnable);
			rr.setContentType(ContentType.TEXT);
			for (AmiRestStatsGetter i : restStats) {
				rr.println("");
				rr.println(SH.centerAlign('=', " " + i.getKey().toUpperCase() + " ", 40, false));
				rr.println("");
				Map<String, Object> m = (Map) i.getStats();
				for (Map.Entry<String, Object> e : m.entrySet()) {
					rr.println(SH.rightAlign(' ', e.getKey(), 21, false) + ": " + e.getValue());
				}
			}
		} else {
			values.put("startTime", startTime);
			values.put("now", now);
			values.put("cpuPct", toPct(cpuLoad));
			values.put("threadPct", threadsPct);
			values.put("estMemUsedPct", memPct);
			top.put("vm", values);
			top.put("vmraw", CH.m("startTime", startTime, "now", now, "memUsed", um, "memMax", mm, "memTot", tm, "memUsedAfterGc", lastUm, "gc", GcMemoryMonitor.getCount(),
					"threadsTotal", threads.length, "threadsRunnable", threadsRunnable));
			for (AmiRestStatsGetter i : restStats) {
				top.put(i.getKey(), i.getStats());
			}
			rr.printJson(top);
		}

	}

	private double toPct(double num) {
		int n = (int) (num * 10000);
		return n / 100d;
	}

	@Override
	public boolean requiresAuth() {
		return false;
	}

}
