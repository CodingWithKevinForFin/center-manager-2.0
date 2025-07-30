package com.f1.http;

public interface HttpSessionManagerListener {

	void onNewSession(HttpSessionManager manager, HttpSession session);
	void onSessionClosed(HttpSessionManager manager, HttpSession session);
	void onSessionExpired(HttpSessionManager manager, HttpSession session);
	void onSessionReaperRan(HttpSessionManager manager, long now);

}
