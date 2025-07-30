package com.f1.console.impl;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import com.f1.console.ConsoleSession;
import com.f1.utils.SH;

public class ShowVmOptionsConsoleService extends AbstractConsoleService {
	public ShowVmOptionsConsoleService() {
		super("show vm", "SHOW +VM", "displays VM Options. Usage: show vm");
	}

	@Override
	public void doRequest(ConsoleSession session, String[] options) {
		StringBuilder sb = new StringBuilder();
		RuntimeMXBean runtimeInfo = ManagementFactory.getRuntimeMXBean();
		sb.append("boot-class-path: ").append(runtimeInfo.getBootClassPath()).append(SH.NEWLINE);
		sb.append("input-arguments: ");
		SH.join(" ", runtimeInfo.getInputArguments(), sb);
		sb.append(SH.NEWLINE);

		session.getConnection().println(sb.toString());
	}
}
