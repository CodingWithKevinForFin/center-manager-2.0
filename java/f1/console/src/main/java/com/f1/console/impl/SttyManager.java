package com.f1.console.impl;

import java.io.PrintStream;

import com.f1.utils.EH;
import com.f1.utils.concurrent.SimpleExecutor;

public class SttyManager {
	private static final String SHELL = "/bin/sh";
	private static final String DEV_TTY = "/dev/tty";
	private static final String STTY = "/bin/stty";
	private static final String STTY_CMD = STTY + " -F " + DEV_TTY + " ";
	private static final String RAW = "raw";

	private static class ShutdownHook extends Thread {

		@Override
		public void run() {
			saveStty(origConfig);
		}
	}

	private static final Thread SHUTDOWN = new ShutdownHook();

	private SttyManager() {
	}

	private static String origConfig = null;
	private static TelnetOutputStream telnetStdoutStream;
	private static TelnetOutputStream telnetStderrStream;
	private static Boolean canDoSttyRaw;

	static private String loadStty() {
		return new String(EH.execToStdout(SimpleExecutor.DEFAULT, new String[] { SHELL, "-c", STTY_CMD + "-g" }, null, 0)).trim();
	}
	static private String loadSttyRaw() {
		return new String(EH.execToStdout(SimpleExecutor.DEFAULT, new String[] { SHELL, "-c", STTY_CMD + "-a" }, null, 0)).trim();
	}
	static private void saveStty(String config) {
		EH.execToStdout(SimpleExecutor.DEFAULT, new String[] { SHELL, "-c", STTY_CMD + config }, null, 0);
	}

	static public void setSttyRaw() {
		setSttySetting(RAW);
		setSttySetting("-echo");
	}

	static public boolean canDoSttyRaw() {
		if (canDoSttyRaw == null)
			canDoSttyRaw = !loadSttyRaw().contains("-echo ");
		return canDoSttyRaw;
	}
	static synchronized public void setSttySetting(String setting) {
		if (origConfig == null) {
			if (!canDoSttyRaw())
				throw new RuntimeException("raw stty not supported, must be running from console");
			origConfig = loadStty();
			Runtime.getRuntime().addShutdownHook(SHUTDOWN);
			System.setOut(new PrintStream(telnetStdoutStream = new TelnetOutputStream(System.out), false));
			System.setErr(new PrintStream(telnetStderrStream = new TelnetOutputStream(System.err), false));
		}
		saveStty(setting);
	}

}
