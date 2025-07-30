package com.f1.console.impl;

import java.lang.management.ManagementFactory;

import com.f1.console.ConsoleSession;
import com.f1.utils.SH;
import com.sun.management.OperatingSystemMXBean;

public class ShowCpuUsageConsoleService extends AbstractConsoleService {
	public ShowCpuUsageConsoleService() {
		super("show cpu", "SHOW +CPU", "displays recent CPU usage (from %0.00-%100.00). Usage: show cpu");
	}

	@Override
	public void doRequest(ConsoleSession session, String[] options) {
		com.sun.management.OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
		double cpuLoad = osBean.getProcessCpuLoad();
		StringBuilder sb = new StringBuilder();
		sb.append("CPU Usage: %").append(((int) (cpuLoad * 10000)) / 100d).append(SH.NEWLINE);
		sb.append(SH.NEWLINE);
		session.getConnection().println(sb.toString());
	}
}
