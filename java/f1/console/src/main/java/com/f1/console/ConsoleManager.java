package com.f1.console;

import java.io.File;
import java.util.List;

import com.f1.console.impl.ConsoleAuthenticator;

public interface ConsoleManager {
	Object getValue(String name);

	void setValue(String name, Object value);

	void removeValue(String name);

	ConsoleServer getServer();

	Iterable<ConsoleService> getServices();

	void addConsoleService(ConsoleService terminalService);

	void addServicePackage(ConsoleServicePackage terminalServicePackage);

	void removeService(ConsoleService terminalService);

	void onEvent(ConsoleEvent event, boolean storeToHistory);

	List<ConsoleEvent> getAllHistory(String username);

	File getGlobalHistoryFile();

	String getPrompt();

	void setPrompt(String prompt_);

	ConsoleAuthenticator getAuthenticator();

	void setAuthenticator(ConsoleAuthenticator amiConsoleAuthenticator);
}
