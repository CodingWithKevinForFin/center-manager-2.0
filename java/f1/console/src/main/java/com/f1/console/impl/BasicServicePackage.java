package com.f1.console.impl;

import com.f1.console.ConsoleManager;
import com.f1.console.ConsoleServicePackage;

public class BasicServicePackage implements ConsoleServicePackage {
	@Override
	public void init(ConsoleManager app) {
		app.addConsoleService(new LoginConsoleService());
		app.addConsoleService(new HelpConsoleService());
		app.addConsoleService(new HistoryConsoleService());
		//		app.addConsoleService(new QuickHistoryConsoleService());
		app.addConsoleService(new QuitConsoleService());
		app.addConsoleService(new ShowEnvConsoleService());
		app.addConsoleService(new ShowThreadsConsoleService());
		app.addConsoleService(new ShowVmOptionsConsoleService());
		app.addConsoleService(new ShowCpuUsageConsoleService());
		//		app.addConsoleService(new RunBatchProcessConsoleService());
		//		app.addConsoleService(new ShowFileConsoleService());
		//		app.addConsoleService(new RunProcessConsoleService());
		//		app.addConsoleService(new RedirectConsoleService());
		//		app.addConsoleService(new DbConsoleService());
	}
}
