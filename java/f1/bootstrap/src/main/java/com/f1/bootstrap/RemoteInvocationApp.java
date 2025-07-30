package com.f1.bootstrap;

import com.f1.console.ConsoleClientResult;
import com.f1.console.impl.BasicConsoleClient;

public class RemoteInvocationApp {

	public static void main(String a[]) {
		System.setProperty("java.util.logging.manager", "com.f1.speedlogger.sun.SunSpeedLoggerLogManager");
		final Bootstrap am = new Bootstrap(RemoteInvocationApp.class, a);
		am.setStartConsole(false);
		am.processProperties();
		final String remoteHost = am.getProperties().getOptional("remote.host", "localhost");
		final int remotePort = am.getProperties().getRequired("remote.port", Integer.class);
		final String command = am.getProperties().getRequired("remote.command", String.class);
		BasicConsoleClient client = new BasicConsoleClient(remoteHost, remotePort);
		ConsoleClientResult result = client.execute(command);
		System.err.println(result);
		client.execute("quit");
	}
}
