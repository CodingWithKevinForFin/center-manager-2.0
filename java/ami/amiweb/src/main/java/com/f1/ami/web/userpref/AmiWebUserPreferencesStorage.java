package com.f1.ami.web.userpref;

import java.util.Set;

public interface AmiWebUserPreferencesStorage {

	public Set<String> getUserPreferenceKeys();
	public String getUserPreference(String key);
	public void setUserPreference(String preference, String value);
	public void removeUserPreference(String preference, String value);

}
