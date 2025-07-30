package com.f1.bootstrap;

public class AppMainTest {

	static {
		System.setProperty("AppMain.dir.conf", "test/config");
		System.setProperty("java.util.logging.manager", "com.f1.speedlogger.sun.SunSpeedLoggerLogManager");
	}

	public static void main(String a[]) {
		Bootstrap am = new ContainerBootstrap(AppMainTest.class, a);
		am.setLoggingOverrideProperty("normal");
		am.readProperties();
		am.processProperties();
		am.startup();
		am.keepAlive();
	}
}

