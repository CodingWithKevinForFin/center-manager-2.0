package com.f1.console;

import java.util.List;
import java.util.Map;

public interface ConsoleSession {
	Object getValue(String name);

	void setValue(String name, Object value);

	void removeValue(String name);

	List<ConsoleEvent> getHistory();

	List<ConsoleEvent> getCommandHistory();

	ConsoleConnection getConnection();

	ConsoleManager getManager();

	void onEvent(ConsoleEvent event, boolean storeToHistory);

	void setUserLoggedin(String username, Map<String, Object> attributes);

	public String getUsername();//returns null if not logged in

	public Map<String, Object> getUserAttributes();

	boolean isLoggedIn();

}
