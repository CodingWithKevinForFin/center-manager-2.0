package com.f1.console.impl;

import java.util.regex.Pattern;

import com.f1.console.ConsoleConnection;
import com.f1.console.ConsoleSession;

public class QuitConsoleService extends AbstractConsoleService {
	public QuitConsoleService() {
		super("QUIT", "QUIT", "Closes down the connection and frees all temporary session elements");
	}

	public static Pattern PATTERN = Pattern.compile("QUIT", Pattern.CASE_INSENSITIVE);

	@Override
	public void doRequest(ConsoleSession session, String[] options) {
		ConsoleConnection out = session.getConnection();
		out.comment(ConsoleConnectionImpl.COMMENT_MESSAGE, "Good bye");
		session.getConnection().shutdown();
	}

	@Override
	public void doStartup(ConsoleSession session) {
	}

	@Override
	public void doShutdown(ConsoleSession session) {
		session.getConnection().comment(ConsoleConnection.COMMENT_EXIT, "Good Bye");
	}

	@Override
	public String getDescription() {
		return "Closes down the connection and frees all temporary session elements";
	}

	@Override
	public boolean availableWihtoutLogin() {
		return true;
	}

}
