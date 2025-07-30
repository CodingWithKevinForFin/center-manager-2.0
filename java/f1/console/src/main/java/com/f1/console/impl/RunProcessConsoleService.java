package com.f1.console.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.f1.console.ConsoleConnection;
import com.f1.console.ConsoleEvent;
import com.f1.console.ConsoleSession;
import com.f1.utils.FastByteArrayOutputStream;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.StreamPiper;

public class RunProcessConsoleService extends AbstractConsoleService {

	public RunProcessConsoleService() {
		super("!", "!(.*)", "Execute process. Usage: !<process> --or-- !<history_number>");
	}

	@Override
	public void doRequest(ConsoleSession session, String[] options) {
		try {
			String command = options[0].substring(1);
			if (command.length() > 0 && OH.isBetween(command.charAt(0), '0', '9')) {
				final int commandIndex = Integer.parseInt(options[1]);
				final List<ConsoleEvent> history = session.getCommandHistory();
				if (commandIndex - 1 < 0 || commandIndex - 1 >= history.size())
					session.getConnection().comment(ConsoleConnection.COMMENT_ERROR, "invalid history index: " + commandIndex);
				else {
					final String cmd = history.get(commandIndex - 1).getText();
					session.getConnection().processLine(cmd, false);
				}
				return;
			}
			if (command.endsWith("&")) {
				command = command.substring(0, command.length() - 1);
				final Process process = Runtime.getRuntime().exec(options[0].substring(1));
				new Thread(new StreamPiper(process.getErrorStream(), System.err, 4096)).start();
				new Thread(new StreamPiper(process.getInputStream(), System.out, 4096)).start();
			} else {
				final Process process = Runtime.getRuntime().exec(options[0].substring(1));
				FastByteArrayOutputStream out = new FastByteArrayOutputStream();
				FastByteArrayOutputStream err = new FastByteArrayOutputStream();
				new Thread(new StreamPiper(process.getErrorStream(), err, 4096)).start();
				new Thread(new StreamPiper(process.getInputStream(), out, 4096)).start();
				int exitCode;
				try {
					exitCode = process.waitFor();
				} catch (InterruptedException e_) {
					throw OH.toRuntime(e_);
				}
				String outStr = out.toString();
				String errStr = err.toString();
				PrintWriter sink = session.getConnection().getOut();
				if (SH.is(outStr)) {
					sink.write("STDOUT: " + SH.NEWLINE);
					sink.write(outStr);
				}
				if (SH.is(errStr)) {
					sink.write("STDERR: " + SH.NEWLINE);
					sink.write(errStr);
				}
				session.getConnection().comment("EXITCODE", Integer.toString(exitCode));
			}
		} catch (IOException e) {
			OH.toRuntime(e);
		}
	}

}
