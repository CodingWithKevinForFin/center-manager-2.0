package com.f1.console.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.f1.console.ConsoleConnection;
import com.f1.console.ConsoleSession;
import com.f1.utils.IOH;
import com.f1.utils.LogWriter;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class RedirectConsoleService extends AbstractConsoleService {
	public RedirectConsoleService() {
		super("REDIRECT", "REDIRECT (FILE|LOG|CONSOLE) *(.+)?", "redirect standard out to the log or a file");
	}

	@Override
	public void doRequest(ConsoleSession session, String[] options) {
		ConsoleConnection out = session.getConnection();
		out.println();

		String mode = options[1];
		String details = options[2];
		if ("console".equalsIgnoreCase(mode)) {
			IOH.close(out.getRedirectOut());
			out.setRedirectOut(null, "");
		} else if ("file".equalsIgnoreCase(mode)) {
			IOH.close(out.getRedirectOut());
			try {
				out.setRedirectOut(new PrintWriter(new FileWriter(new File(details))), "redirected to file: " + details);
			} catch (Exception e) {
				OH.toRuntime(e);
			}
		} else if ("log".equalsIgnoreCase(mode)) {
			IOH.close(out.getRedirectOut());
			try {
				String id = SH.beforeLast(details, ":", "CONSOLE_REDIRECT");
				String level = SH.afterLast(details, ":", Level.INFO.getName());
				out.setRedirectOut(new PrintWriter(new LogWriter(Logger.getLogger(id), Level.parse(level))), "redirected to log: " + id + " level=" + level);
			} catch (Exception e) {
				OH.toRuntime(e);
			}
		}

	}

	@Override
	public String getHelp() {
		return "REDIRECT STDOUT | REDIRECT FILE <filename> | REDIRECT LOG <level>";
	}

}
