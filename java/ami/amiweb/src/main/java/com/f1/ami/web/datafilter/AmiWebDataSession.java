package com.f1.ami.web.datafilter;

import java.util.Set;

public interface AmiWebDataSession {

	public String getUsername();

	public Set<String> getVariableNames();
	public Object getVariableValue(String name);
	public Class<?> getVariableType(String name);
	public <T> void putVariable(String key, T value, Class<T> type);
	public void logout();
	public String getBrowserIP();
}
