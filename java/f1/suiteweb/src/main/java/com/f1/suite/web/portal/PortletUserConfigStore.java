package com.f1.suite.web.portal;

import java.util.Set;

public interface PortletUserConfigStore {
	public void saveFile(String key, String value);
	public Set<String> getFiles();
	public String loadFile(String key);
	//	public boolean isFileWriteable(String key);
	public void removeFile(String key);
	public void putSetting(String key, Object value);
	public void removeSetting(String key);
	public String getSettingString(String key);
	public <T> T getSetting(String key, Class<T> type);
	public void saveSettings();
	//	public PortletFile getFile(String key);
	public void setFileWriteable(String key, boolean b);
	public void moveFile(String name, String newFile);

}
