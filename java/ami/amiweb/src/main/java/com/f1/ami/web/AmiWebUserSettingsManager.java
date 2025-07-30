package com.f1.ami.web;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import com.f1.suite.web.portal.PortletManager;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.converter.json2.ObjectToJsonConverter;

public class AmiWebUserSettingsManager {

	private AmiWebSafeFile settingsFile;
	private Map<String, Object> settingsCache = new TreeMap<String, Object>();

	private boolean changed = false;

	private Map<String, Object> attributes;

	public AmiWebUserSettingsManager(PortletManager pm, AmiWebUserFilesManager filesManager) {
		this.attributes = pm.getState().getUserAttributes();
		this.settingsFile = filesManager.getSafeFile("ami_settings");
		if (!this.settingsFile.exists()) {
			this.saveSettings();
		} else {
			this.loadSettings();
		}
	}
	public void putSetting(String key, Object value) {
		if (this.attributes.containsKey(key))
			return;
		final Object existing = this.settingsCache.get(key);
		if (OH.eq(existing, value))
			return;
		this.settingsCache.put(key, value);
		this.changed = true;
	}
	public void removeSetting(String key) {
		if (!this.settingsCache.containsKey(key))
			return;
		this.settingsCache.remove(key);
		this.changed = true;
	}
	public String getSettingString(String key) {
		return getSetting(key, String.class);
	}

	private static final ObjectToJsonConverter JSON = ObjectToJsonConverter.INSTANCE_CLEAN;

	public void saveSettings() {
		if (!changed)
			return;
		try {
			this.settingsFile.setText(AmiWebLayoutHelper.toJson(this.settingsCache, ObjectToJsonConverter.MODE_CLEAN));
			this.changed = false;
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}
	private void loadSettings() {
		try {
			String text = this.settingsFile.getText();
			this.settingsCache.putAll((Map) JSON.stringToObject(text));
		} catch (Exception e) {
			throw new RuntimeException("file has invalid JSON: " + this.settingsFile.getFile().getFullPath(), e);
		}
		changed = false;
	}

	public boolean isReadonlySetting(String key) {
		return this.attributes.containsKey(key);
	}
	public <T> T getSetting(String key, Class<T> type) {
		Object t = this.attributes.get(key);
		if (t != null)
			return OH.cast(t, type, false, false);
		return CH.getOr(type, this.settingsCache, key, null);
	}

}
