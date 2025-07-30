package com.f1.dropCopy;

import java.io.IOException;

import com.f1.bootstrap.Bootstrap;
import com.f1.bootstrap.ContainerBootstrap;
import com.f1.transportManagement.SessionManager;

import quickfix.ConfigError;
import quickfix.FieldConvertError;

public class DropCopyMain {
	public static void main(String args[]) throws IOException, ConfigError, FieldConvertError, InterruptedException {
		Bootstrap bs = new ContainerBootstrap(DropCopyMain.class, args);
		bs.setLoggingOverrideProperty("quiet");
		bs.setConfigDirProperty("./src/main/config");
		bs.startup();
		SessionManager sessionManager = new SessionManager(bs.getProperties());

		sessionManager.start();
		sessionManager.waitForComplete();
	}

}
