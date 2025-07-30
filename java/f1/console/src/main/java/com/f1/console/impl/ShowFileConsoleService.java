package com.f1.console.impl;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import com.f1.console.ConsoleConnection;
import com.f1.console.ConsoleSession;
import com.f1.console.impl.shell.ShellAutoCompletion;
import com.f1.utils.IOH;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class ShowFileConsoleService extends AbstractConsoleService {

	public ShowFileConsoleService() {
		super("show file", "SHOW +FILE +(.*?)", "Show the contents of a file or directory. Usage: show file <absolute_filename>");
	}

	@Override
	public void doRequest(ConsoleSession session, String[] options) {

		String filePath = options[1];
		File file = new File(filePath);
		if (!file.exists()) {
			session.getConnection().comment(ConsoleConnection.COMMENT_ERROR, "File not found: " + IOH.getFullPath(file));
			return;
		}

		if (file.isDirectory()) {
			session.getConnection().println("contents of directory " + file);
			for (File child : file.listFiles())
				session.getConnection().println("  " + describe(child));
		} else
			try {
				session.getConnection().print(IOH.readText(file));
			} catch (IOException e_) {
				OH.toRuntime(e_);
			}
	}

	private String describe(File file) {
		return "[" + new Date(file.lastModified()).toString() + "]  " + SH.rightAlign(' ', file.isDirectory() ? "directory" : Long.toString(file.length()), 10, false) + "  "
				+ file.getName();
	}

	@Override
	public boolean canAutoComplete(String partialText) {
		return canProcessRequest(partialText);
	}

	@Override
	public ShellAutoCompletion autoComplete(ConsoleSession session, String partialText) {
		BasicTelnetAutoCompletion r = new BasicTelnetAutoCompletion(partialText);
		String parts[] = super.parsePattern(partialText);
		String filePath = SH.beforeLast(parts[1], "/", ".");
		if (filePath.isEmpty())
			filePath = "/";
		File directory = new File(filePath);
		if (!directory.isDirectory())
			return r;
		String filePattern = SH.afterLast(parts[1], "/", parts[1]);
		for (String file : directory.list()) {
			if (file.startsWith(filePattern)) {
				if (new File(directory, file).isDirectory())
					r.add(file.substring(filePattern.length()) + "/");
				else
					r.add(file.substring(filePattern.length()));
			}
		}
		return r;
	}
}
