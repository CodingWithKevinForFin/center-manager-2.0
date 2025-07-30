package com.f1.ami.web;

import java.util.Set;

import com.f1.suite.web.portal.PortletUserConfigStore;

public class AmiWebUserConfigStore implements PortletUserConfigStore {

	private AmiWebUserFilesManager fm;
	private AmiWebUserSettingsManager sm;

	public AmiWebUserConfigStore(AmiWebUserFilesManager fm, AmiWebUserSettingsManager sm) {
		this.fm = fm;
		this.sm = sm;
	}

	@Override
	public void saveFile(String key, String value) {
		this.fm.saveFile(key, value);
	}

	@Override
	public Set<String> getFiles() {
		return this.fm.getFiles();
	}

	@Override
	public String loadFile(String key) {
		return this.fm.loadFile(key);
	}

	@Override
	public void removeFile(String key) {
		this.fm.removeFile(key);
	}

	@Override
	public void putSetting(String key, Object value) {
		this.sm.putSetting(key, value);
	}

	@Override
	public void removeSetting(String key) {
		this.sm.removeSetting(key);
	}

	@Override
	public String getSettingString(String key) {
		return this.sm.getSettingString(key);
	}

	@Override
	public <T> T getSetting(String key, Class<T> type) {
		return this.sm.getSetting(key, type);
	}

	//	@Override
	//	public void saveSettings() {
	//		this.sm.saveSettings();
	//	}

	@Override
	public void setFileWriteable(String key, boolean b) {
		this.fm.setFileWriteable(key, b);
	}

	@Override
	public void moveFile(String name, String newFile) {
		this.fm.moveFile(name, newFile);
	}

	@Override
	public void saveSettings() {
		this.sm.saveSettings();
	}

}
