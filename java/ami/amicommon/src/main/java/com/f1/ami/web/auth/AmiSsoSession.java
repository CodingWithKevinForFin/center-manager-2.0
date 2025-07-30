package com.f1.ami.web.auth;

import java.util.Map;

//One per Login (not per session)
public interface AmiSsoSession {
	String getAccessToken();
	String getProvider();
	String getProviderUrl();
	Map<String, Object> getProperties();
	void killSession();//This will be called by AMI when the login session is closed... Subsequent calls to isAlive should return false
	boolean isAlive();//Returns false only if either killSession(...) has been called or the sso plugin has decided to close the session
}
