package com.f1.console.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import com.f1.console.ConsoleEvent;
import com.f1.console.ConsoleManager;
import com.f1.console.ConsoleServer;
import com.f1.console.ConsoleService;
import com.f1.console.ConsoleServicePackage;
import com.f1.utils.EH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.impl.StringCharReader;

public class ConsoleManagerImpl implements ConsoleManager {
	private static final Logger log = Logger.getLogger(ConsoleManagerImpl.class.getName());
	private ConsoleServer terminalServer;
	private List<ConsoleService> terminalServices;
	private Map<String, Object> values;
	private PrintWriter globalHistoryOut = null;
	private File globalHistoryFile;
	private String prompt = ">";
	private ConsoleAuthenticator consoleAuthenticator;

	@Override
	public File getGlobalHistoryFile() {
		return globalHistoryFile;
	}

	public ConsoleManagerImpl(ConsoleServer terminalServer, File globalHistoryFile, ConsoleAuthenticator authenticator) {
		this.terminalServer = terminalServer;
		this.consoleAuthenticator = authenticator;
		values = new HashMap<String, Object>();
		terminalServices = new CopyOnWriteArrayList<ConsoleService>();
		if (globalHistoryFile != null)
			try {
				LH.info(log, "Console history file at: ", IOH.getFullPath(globalHistoryFile));
				this.globalHistoryFile = globalHistoryFile;
				globalHistoryOut = new PrintWriter(new FileWriter(globalHistoryFile, true));
			} catch (IOException e) {
				throw OH.toRuntime(e);
			}
		onEvent(new BasicConsoleEvent(ConsoleEvent.TYPE_STARTUP, EH.currentTimeMillis(), null, null, null), true);
	}

	@Override
	public Object getValue(String name) {
		return values.get(name);
	}

	@Override
	public void setValue(String name, Object value) {
		values.put(name, value);
	}

	@Override
	public void removeValue(String name) {
		values.remove(name);
	}

	@Override
	public ConsoleServer getServer() {
		return terminalServer;
	}

	@Override
	public Iterable<ConsoleService> getServices() {
		return terminalServices;
	}

	@Override
	public void addConsoleService(ConsoleService terminalService) {
		terminalServices.add(terminalService);
	}

	@Override
	public void removeService(ConsoleService terminalService) {
		terminalServices.remove(terminalService);
	}

	@Override
	public void addServicePackage(ConsoleServicePackage terminalServicePackage) {
		terminalServicePackage.init(this);
	}
	@Override
	public List<ConsoleEvent> getAllHistory(String userNameToFind) {
		if (globalHistoryFile == null)
			return Collections.EMPTY_LIST;
		BufferedReader reader = null;
		try {
			List<ConsoleEvent> r = new ArrayList<ConsoleEvent>();
			reader = new BufferedReader(new FileReader(globalHistoryFile));
			String line;
			StringBuilder temp = new StringBuilder();
			StringCharReader sreader = new StringCharReader("");
			while ((line = reader.readLine()) != null) {
				sreader.reset(line.toCharArray());
				byte type = -1;
				long time = 0;
				String connectionDetails = null;
				String text = null;
				String username = null;
				int pos = 0;
				while (sreader.peakOrEof() != -1) {
					temp.setLength(0);
					if (sreader.peak() == '\"') {
						sreader.expect('\"');
						sreader.readUntil('"', '\\', temp);
						sreader.expect('\"');
					} else
						sreader.readUntil(';', '\\', temp);
					sreader.expect(';');
					if (pos == 0)
						type = Byte.parseByte(temp.toString());
					else if (pos == 1)
						time = Long.parseLong(temp.toString());
					else if (pos == 2)
						connectionDetails = temp.toString();
					else if (pos == 3)
						text = temp.toString();
					else if (pos == 4)
						username = temp.toString();
					pos++;
				}
				if (OH.eq(username, userNameToFind))
					r.add(new BasicConsoleEvent(type, time, trimQuotes(connectionDetails), trimQuotes(text), username));
			}
			IOH.close(reader);
			return r;
		} catch (IOException e) {
			IOH.close(reader);
			throw OH.toRuntime(e);
		}
	}

	private String trimQuotes(String text) {
		return SH.isnt(text) ? null : SH.toStringDecode(text);
	}

	@Override
	public void onEvent(ConsoleEvent event, boolean storeToHistory) {
		if (storeToHistory && globalHistoryOut != null) {
			StringBuilder sb = new StringBuilder();
			sb.append(event.getType());
			sb.append(';');
			sb.append(event.getTime());
			sb.append(';');
			sb.append(SH.CHAR_QUOTE);
			if (event.getConnectionDescription() != null)
				SH.toStringEncode(event.getConnectionDescription(), SH.CHAR_QUOTE, sb);
			sb.append(SH.CHAR_QUOTE);
			sb.append(';');
			sb.append(SH.CHAR_QUOTE);
			if (event.getText() != null)
				SH.toStringEncode(event.getText(), SH.CHAR_QUOTE, sb);
			sb.append(SH.CHAR_QUOTE);
			sb.append(';');
			sb.append(SH.CHAR_QUOTE);
			if (event.getUsername() != null)
				SH.toStringEncode(event.getUsername(), SH.CHAR_QUOTE, sb);
			sb.append(SH.CHAR_QUOTE);
			sb.append(';');
			synchronized (this) {
				globalHistoryOut.println(sb);
				globalHistoryOut.flush();
			}
		}
	}

	@Override
	public String getPrompt() {
		return prompt;
	}

	@Override
	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	@Override
	public ConsoleAuthenticator getAuthenticator() {
		return this.consoleAuthenticator;
	}

	@Override
	public void setAuthenticator(ConsoleAuthenticator authenticator) {
		this.consoleAuthenticator = authenticator;
	}
}
