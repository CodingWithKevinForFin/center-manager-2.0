package com.f1.console.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import com.f1.base.Password;
import com.f1.console.ConsoleConnection;
import com.f1.console.ConsoleEvent;
import com.f1.console.ConsoleServer;
import com.f1.console.ConsoleService;
import com.f1.console.ConsoleSession;
import com.f1.console.impl.shell.ShellAutoCompleter;
import com.f1.console.impl.shell.ShellAutoCompletion;
import com.f1.utils.AutoFlushOutputStream;
import com.f1.utils.EH;
import com.f1.utils.F1GlobalProperties;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.SH;

public class ConsoleConnectionImpl implements ConsoleConnection, Runnable, ShellAutoCompleter {
	private static java.util.logging.Logger log = java.util.logging.Logger.getLogger(ConsoleConnectionImpl.class.getName());

	public static final Pattern PATTERN_QUIT = Pattern.compile("QUIT", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	private static final Pattern FILE_PATTERN = Pattern.compile("[A-Za-z0-9_\\\\\\/\\.\\-\\:]+");

	private final ConsoleServer terminalServer;
	private boolean isShutdown;
	private final LineNumberReader in;
	private TelnetShellConnection connection;
	private final Writer socketOut;
	private Writer redirectOut;
	private PrintWriter redirectOutPrefixWriter;
	private String redirectOutDetails = "";
	private final ConsoleSession terminalSession;

	private final String connectionDetails;

	private final PrintWriter socketOutWriter;

	private final PrintWriter socketOutPrefixWriter;

	private final PrefixWriter prefixWriter;

	public ConsoleConnectionImpl(ConsoleServer terminalServer, InputStream i, OutputStream o, String connectionDetails) {
		if (o != null)
			o = new TelnetOutputStream(o);
		if (i != null)
			i = new TelnetInputStream(i);
		this.terminalServer = terminalServer;
		terminalSession = new ConsolelSessionImpl(this, terminalServer.getManager());
		in = i == null ? null : new LineNumberReader(new InputStreamReader(i));
		socketOut = new OutputStreamWriter(new AutoFlushOutputStream(o, 1024, 100, true));
		socketOutWriter = new PrintWriter(socketOut, true);
		socketOutPrefixWriter = new PrintWriter(prefixWriter = new PrefixWriter(socketOut, " "), true);
		if (in == null)
			init();// we need to now, cause there isn't a second thread!
		else {
			List<String> history = new ArrayList<String>(terminalSession.getCommandHistory().size());
			for (ConsoleEvent h : terminalSession.getCommandHistory())
				if (SH.is(h.getText()))
					history.add(h.getText());
			connection = new TelnetShellConnection(i, o, history, this, getSession().getManager().getPrompt());
		}
		this.connectionDetails = connectionDetails;
	}

	@Override
	public TelnetShellConnection getConnection() {
		return this.connection;
	}

	@Override
	public ConsoleSession getSession() {
		return terminalSession;
	}

	@Override
	public LineNumberReader getIn() {
		return in;
	}

	@Override
	public PrintWriter getOut() {
		if (redirectOut != null)
			return new PrintWriter(redirectOut, true);
		return socketOutPrefixWriter;
	}

	public PrintWriter getSocketOut() {
		return socketOutWriter;
	}

	@Override
	public void run() {
		try {
			init();
			while (!isShutdown) {
				String line = readLine();
				processUserCommand(line);
			}
		} catch (Exception e) {
			LH.severe(log, "Unhandled client exception in manager; shutting down remote connection. Please see user history for details:", e);
			IOH.close(this.socketOut);
			IOH.close(this.in);
		}
		LH.info(log, "Connection closed: ", this);
	}

	private String readLine() throws IOException {
		StringBuilder textSb = new StringBuilder();
		for (;;) {
			String line = connection.readLine();
			if (line == null)
				return null;
			line = line.trim();
			if (SH.endsWith(line, '\\'))
				textSb.append(line, 0, line.length() - 1).append(SH.NEWLINE);
			else
				return textSb.append(line).toString();
		}
	}

	@Override
	public void processUserCommand(String line) {

		if (line == null)
			isShutdown = true;
		else {
			final long start = EH.currentTimeMillis();
			processLine(line, true);
			final long end = EH.currentTimeMillis();
			final Runtime runtime = Runtime.getRuntime();
			comment(COMMENT_EXECUTED,
					String.format("%.2f second(s). %.2f MB in use. %s\n", (end - start) / 1000d, (runtime.totalMemory() - runtime.freeMemory()) / 1048576d, new Date().toString()));
		}
	}

	@Override
	public void processLine(String text, boolean canSaveToHistory) {
		LH.info(log, "processLine for ", getConnectionIdentifier(), ": ", text);
		String redirectFile = SH.afterLast(text, '>', null);
		String command;
		boolean append = false;
		if (SH.is(text)) {
			if (redirectFile != null)
				redirectFile = redirectFile.trim();
			if (redirectFile != null && FILE_PATTERN.matcher(redirectFile).matches()) {
				command = SH.beforeLast(text, '>');
				append = SH.endsWith(command, '>');
				if (append)
					command = command.substring(0, command.length() - 1);
			} else {
				command = text;
				redirectFile = null;
			}
			Writer existingRedirect = null;
			String existingRedirectDetails = null;
			if (redirectFile != null) {
				existingRedirect = getRedirectOut();
				existingRedirectDetails = getRedirectOutDetails();
			}
			try {
				boolean saveToHistory = false;
				try {
					if (redirectFile != null) {
						setRedirectOut(new PrintWriter(new FileWriter(new File(redirectFile), append)), (append ? "APPEND " : "") + "FILE:" + redirectFile);
					}
					prefixWriter.setInLinefeed(true);
					saveToHistory = processLine2(command);
					if (!prefixWriter.isInLineFeed()) {// did the processor not
														// include an EOL? if
														// not, lets do it now
						socketOutWriter.println();
					}
				} catch (Exception e) {
					LH.severe(log, "There was an error processing your request.", e);
					comment(COMMENT_ERROR, SH.printStackTrace(e));
				}
				if (canSaveToHistory && saveToHistory) {
					BasicConsoleEvent event = new BasicConsoleEvent(ConsoleEvent.TYPE_COMMAND, EH.currentTimeMillis(), getConnectionIdentifier(), text, getSession().getUsername());
					terminalSession.onEvent(event, true);
				}
			} finally {
				if (redirectFile != null) {
					IOH.close(getRedirectOut());
					setRedirectOut(existingRedirect, existingRedirectDetails);
				}
			}
		}
	}

	/**
	 * 
	 * @param text
	 * @return true if it should be stored to history
	 * @throws Exception
	 */
	private boolean processLine2(String text) throws Exception {
		boolean storeToHistory = true;
		int cnt = 0;
		for (ConsoleService terminalService : terminalSession.getManager().getServices()) {
			if (terminalService.canProcessRequest(text)) {
				if (!terminalService.availableWihtoutLogin() && !this.terminalSession.isLoggedIn()) {
					comment("AUTHENTICATION REQUIRED", "Please use LOGIN command to login first");
					return false;
				}
				cnt++;
				BasicConsoleRequest request = new BasicConsoleRequest(text, true);
				try {
					terminalService.doRequest(terminalSession, request);
					if (!terminalService.saveCommandToHistory())
						storeToHistory = false;
				} catch (Exception e) {
					LH.info(log, terminalService.getClass().getName(), ": error process Line: ", text, e);
					comment(COMMENT_ERROR, SH.printStackTrace(e));
				}
			}
		}
		if (cnt == 0)
			comment(COMMENT_ERROR, "command syntax not recognized (type HELP<enter> for help)");

		if (this.terminalServer.getManager().getAuthenticator() != null && !this.getSession().isLoggedIn())
			return storeToHistory = false;
		return storeToHistory;
	}

	public void startup() {
		for (ConsoleService terminalService : terminalSession.getManager().getServices()) {
			try {
				terminalService.doStartup(terminalSession);
			} catch (Exception e) {
				LH.severe(log, "Error shutting down", e);
			}
		}
	}

	@Override
	public void shutdown() {
		for (ConsoleService terminalService : terminalSession.getManager().getServices()) {
			try {
				terminalService.doShutdown(terminalSession);
			} catch (Exception e) {
				LH.severe(log, "Error shutting down.", e);
			}
		}
		halt();
	}

	public void halt() {
		isShutdown = true;
		IOH.close(in);
		IOH.close(socketOut);
	}

	private void init() {
		for (ConsoleService terminalService : terminalSession.getManager().getServices()) {
			try {
				terminalService.doStartup(terminalSession);
			} catch (Exception e) {
				LH.severe(log, "Error starting up.", e);
			}
		}
		comment(COMMENT_MESSAGE, "Welcome to the 3Forge remote management utility - Version 2.2");
		comment(COMMENT_MESSAGE, F1GlobalProperties.getTitle());
	}

	@Override
	public void comment(String keyword, String comment) {
		if (!comment.endsWith("\n"))
			comment += SH.NEWLINE;
		socketOutWriter.print(SH.prefixLines(comment, PREFIX + keyword.toUpperCase() + ": "));
		flush();
	}

	@Override
	public void flush() {
		getOut().flush();
	}

	@Override
	public void println(Object text) {
		getOut().println(text);
	}

	@Override
	public void println() {
		getOut().println("");
	}

	@Override
	public void print(Object text) {
		getOut().print(text);
	}

	@Override
	public String getConnectionIdentifier() {
		return connectionDetails;
	}

	@Override
	public void setRedirectOut(Writer redirectOut, String details) {
		this.redirectOut = redirectOut;
		this.redirectOutPrefixWriter = redirectOut == null ? null : new PrintWriter(redirectOut, true);
		this.redirectOutDetails = details;
	}

	@Override
	public Writer getRedirectOut() {
		return redirectOut;
	}

	public String getRedirectOutDetails() {
		return redirectOutDetails;
	}

	@Override
	public ShellAutoCompletion autoComplete(String partialText) {
		if (!this.terminalSession.isLoggedIn())
			return null;
		for (ConsoleService terminalService : terminalSession.getManager().getServices())
			if (terminalService.canAutoComplete(partialText)) {
				ShellAutoCompletion r = terminalService.autoComplete(terminalSession, partialText);
				if (r != null)
					return r;
			}
		BasicTelnetAutoCompletion autocompleted = new BasicTelnetAutoCompletion(partialText);
		for (ConsoleService terminalService : terminalSession.getManager().getServices())
			if (terminalService.getName().toUpperCase().startsWith(partialText.toUpperCase()))
				autocompleted.add(terminalService.getName().substring(partialText.length()));
		return autocompleted;

	}

	@Override
	public Password promptForPassword(String string) {
		return new Password(connection.getUserShell().readPassword(string));
	}

}
