package com.f1.console.impl;

import com.f1.console.ConsoleConnection;
import com.f1.console.ConsoleService;
import com.f1.console.ConsoleSession;
import com.f1.utils.LH;

public class HelpConsoleService extends AbstractConsoleService {
	public HelpConsoleService() {
		super("help", "HELP(?: +(.+))?", "Lists all help, or displays help for a particular command. Usage: help <command>");
	}

	private static java.util.logging.Logger log = java.util.logging.Logger.getLogger(HelpConsoleService.class.getName());

	@Override
	public void doRequest(ConsoleSession session, String[] options) {
		ConsoleConnection out = session.getConnection();
		out.println();
		if (options[1] != null) {
			String function = options[1];
			for (ConsoleService terminalService : session.getManager().getServices()) {
				if (function.equalsIgnoreCase(terminalService.getName())) {
					try {
						out.println();
						out.println(terminalService.getName() + " - " + terminalService.getDescription());
						out.println();
						out.println("USAGE: " + terminalService.getHelp());
						out.println();
						return;
					} catch (Exception e) {
						LH.severe(log, "error printing help", e);
					}
				}
			}
			out.comment(ConsoleConnectionImpl.COMMENT_ERROR, "Command not recognized: " + function + "\n\n");
		}
		for (ConsoleService terminalService : session.getManager().getServices()) {
			try {
				out.println(terminalService.getName() + " - " + terminalService.getDescription());
			} catch (Exception e) {
				LH.severe(log, "error printing description", e);
			}
		}
		//		if (session.getManager().getGlobalHistoryFile() != null) {
		//			out.println();
		//			out.println("history file: " + IOH.getFullPath(session.getManager().getGlobalHistoryFile()));
		//		}
		out.println();
	}

	@Override
	public void doStartup(ConsoleSession session) {
	}

	@Override
	public void doShutdown(ConsoleSession session) {
	}

}
