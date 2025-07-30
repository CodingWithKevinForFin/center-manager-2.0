package com.f1.console.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f1.console.ConsoleManager;
import com.f1.console.ConsoleServer;
import com.f1.console.ConsoleServicePackage;
import com.f1.utils.CH;

public class InvokersConsoleServicePackage implements ConsoleServicePackage {
	private ShowObjectsConsoleService showObjectsService = new ShowObjectsConsoleService();
	private DescribeConsoleService describeService = new DescribeConsoleService();
	private InvokerConsoleService invokerService = new InvokerConsoleService();
	private ConsoleManager app;
	private Map<String, Object> invokables = new HashMap<String, Object>();
	private List<String> imports = new ArrayList<String>();

	//	public InvokersConsoleServicePackage(boolean init) {
	//		if (init) {
	//			imports.add("java.lang.*");
	//			imports.add(SH.class.getName());
	//			imports.add(OH.class.getName());
	//			imports.add(RH.class.getName());
	//			imports.add(CH.class.getName());
	//			imports.add(EH.class.getName());
	//			imports.add(IOH.class.getName());
	//		}
	//	}

	public InvokersConsoleServicePackage() {
		//		this(true);
	}

	public void addInvokable(String name, Object target) {
		invokables.put(name, target);
		if (app != null)
			ShowObjectsConsoleService.getGlobalInvokables(app).put(name, target);
	}

	public void addImport(String name) {
		imports.add(name);
		if (app != null)
			ShowObjectsConsoleService.getGlobalImports(app).add(name);
	}

	@Override
	public void init(ConsoleManager app) {
		this.app = app;
		app.addConsoleService(showObjectsService);
		app.addConsoleService(describeService);
		app.addConsoleService(invokerService);
		Map<String, Object> t = new HashMap<String, Object>();
		ShowObjectsConsoleService.setGlobalInvokables(app, new HashMap<String, Object>(t));
		ShowObjectsConsoleService.setGlobalImports(app, new ArrayList<String>(imports));
	}

	public static void registerObject(ConsoleServer consoleServer, String name, Object value) {
		CH.putOrThrow(ShowObjectsConsoleService.getGlobalInvokables(consoleServer.getManager()), name, value);
	}
	public static Object unregisterObject(ConsoleServer consoleServer, String name) {
		return ShowObjectsConsoleService.getGlobalInvokables(consoleServer.getManager()).remove(name);
	}

	public static Object getRegisteredObject(ConsoleServer consoleServer, String name) {
		return CH.getOrThrow(ShowObjectsConsoleService.getGlobalInvokables(consoleServer.getManager()), name);
	}

}
