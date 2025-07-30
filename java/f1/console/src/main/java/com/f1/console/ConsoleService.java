package com.f1.console;

import com.f1.console.impl.shell.ShellAutoCompletion;

public interface ConsoleService {
	void doRequest(ConsoleSession session, ConsoleRequest request);

	void doStartup(ConsoleSession session);

	void doShutdown(ConsoleSession session);

	public boolean canProcessRequest(String request);

	String getHelp();

	String getName();

	String getDescription();

	boolean canAutoComplete(String partialText);

	ShellAutoCompletion autoComplete(ConsoleSession session, String partialText);

	boolean availableWihtoutLogin();

	boolean saveCommandToHistory();

}
