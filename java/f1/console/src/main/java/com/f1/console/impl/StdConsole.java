package com.f1.console.impl;

import java.util.List;

import com.f1.console.impl.shell.ShellAutoCompleter;
import com.f1.console.impl.shell.UserShell;

public class StdConsole {

	private boolean lineMode;
	private UserShell shell;

	public StdConsole(List<String> history, ShellAutoCompleter telnetAutoCompleter, String prompt) {

		if (SttyManager.canDoSttyRaw()) {
			SttyManager.setSttyRaw();
			lineMode = true;
		} else
			lineMode = false;

		this.shell = new UserShell(System.in, System.out, history, telnetAutoCompleter, prompt);

	}

	public String readLineWithPrompt() {
		if (lineMode)
			return shell.readLine();
		else
			return shell.readLineNoIteract();
	}

	public UserShell getUserShell() {
		return shell;
	}
}
