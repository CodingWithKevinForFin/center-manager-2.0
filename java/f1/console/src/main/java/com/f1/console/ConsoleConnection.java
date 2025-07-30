package com.f1.console;

import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.Writer;

import com.f1.base.Password;
import com.f1.console.impl.TelnetShellConnection;

public interface ConsoleConnection {
	String PREFIX = "#";

	String COMMENT_MESSAGE = "MESSAGE";
	String COMMENT_ERROR = "ERROR";
	String COMMENT_EXECUTED = "EXECUTED";
	String COMMENT_EXIT = "EXIT";
	String COMMENT_PROGRESS = "PROGRESS";

	ConsoleSession getSession();

	LineNumberReader getIn();

	PrintWriter getOut();

	void println(Object text);

	void comment(String keyword, String text);

	void print(Object text);

	void shutdown();

	void println();

	void flush();

	void processLine(String text, boolean canSaveToHistory);

	String getConnectionIdentifier();

	Writer getRedirectOut();

	void setRedirectOut(Writer redirectOut, String details);

	void processUserCommand(String userCommand);

	Password promptForPassword(String string);

	TelnetShellConnection getConnection();
}
