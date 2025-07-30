package com.f1.console.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.f1.console.ConsoleRequest;
import com.f1.console.ConsoleService;
import com.f1.console.ConsoleSession;
import com.f1.console.impl.shell.ShellAutoCompletion;

public abstract class AbstractConsoleService implements ConsoleService {
	private final Pattern pattern;
	private final String command;
	private final String description;

	public AbstractConsoleService(String command, String pattern, String description) {
		this.command = command.toLowerCase();
		this.description = description;
		this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
	}

	public AbstractConsoleService(String command, Pattern pattern, String description) {
		this.pattern = pattern;
		this.command = command.toUpperCase();
		this.description = description;
	}

	@Override
	public void doRequest(ConsoleSession session, ConsoleRequest request) {
		doRequest(session, parsePattern(request.getText()));
	}

	public abstract void doRequest(ConsoleSession session, String[] options);

	@Override
	public void doStartup(ConsoleSession session) {

	}

	@Override
	public void doShutdown(ConsoleSession session) {
	}

	@Override
	public boolean canProcessRequest(String request) {
		return pattern.matcher(request).matches();
	}

	@Override
	public String getHelp() {
		return command;
	}

	@Override
	public String getName() {
		return command;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public boolean canAutoComplete(String partialText) {
		return false;
	}

	@Override
	public ShellAutoCompletion autoComplete(ConsoleSession session, String partialText) {
		throw new RuntimeException();
	}

	protected String[] parsePattern(String text) {
		Matcher matcher = pattern.matcher(text);
		matcher.matches();
		String[] params = new String[matcher.groupCount() + 1];
		for (int i = 0; i < params.length; i++)
			params[i] = matcher.group(i);
		return params;
	}

	@Override
	public boolean availableWihtoutLogin() {
		return false;
	}

	@Override
	public boolean saveCommandToHistory() {
		return true;
	}

}
