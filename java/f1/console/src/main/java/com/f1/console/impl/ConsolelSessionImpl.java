package com.f1.console.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f1.console.ConsoleConnection;
import com.f1.console.ConsoleEvent;
import com.f1.console.ConsoleManager;
import com.f1.console.ConsoleSession;

public class ConsolelSessionImpl implements ConsoleSession {
	private final Map<String, Object> values;
	private final List<ConsoleEvent> history;
	private final ConsoleManager manager;
	private final ConsoleConnection connection;
	private HashMap<String, Object> userAttributes;
	private String username;

	public ConsolelSessionImpl(ConsoleConnection connection, ConsoleManager manager) {
		this.manager = manager;
		this.connection = connection;
		values = new HashMap<String, Object>();
		if (manager.getAuthenticator() == null)
			history = new ArrayList<ConsoleEvent>(manager.getAllHistory(null));
		else
			history = new ArrayList<ConsoleEvent>();
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
	public ConsoleManager getManager() {
		return manager;
	}

	@Override
	public List<ConsoleEvent> getHistory() {
		return history;
	}

	@Override
	public List<ConsoleEvent> getCommandHistory() {

		List<ConsoleEvent> r = new ArrayList<ConsoleEvent>(history.size());
		for (ConsoleEvent e : history)
			if (e.getType() == ConsoleEvent.TYPE_COMMAND)
				r.add(e);
		return r;
	}

	@Override
	public ConsoleConnection getConnection() {
		return connection;
	}

	@Override
	public void onEvent(ConsoleEvent event, boolean storeToHistory) {
		if (storeToHistory)
			history.add(event);
		getManager().onEvent(event, storeToHistory);
	}

	@Override
	public void setUserLoggedin(String username, Map<String, Object> attributes) {
		this.username = username;
		this.userAttributes = new HashMap<String, Object>(attributes);
		this.history.clear();
		if (username != null) {
			List<String> t = new ArrayList<String>();
			this.history.addAll(this.manager.getAllHistory(username));
			for (ConsoleEvent e : history)
				if (e.getType() == ConsoleEvent.TYPE_COMMAND)
					t.add(e.getText());
			this.connection.getConnection().getUserShell().setHistory(t);
		}
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public Map<String, Object> getUserAttributes() {
		return this.userAttributes;
	}

	@Override
	public boolean isLoggedIn() {
		return username != null;
	}

}
