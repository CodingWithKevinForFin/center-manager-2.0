package com.f1.fixclientsim;

import quickfix.Message;

public interface FixClientSimListener {

	void onText(String text);
	void onMessage(FixClientSimSession session, Message message);
	void onLogon(FixClientSimSession session);
	void onLogout(FixClientSimSession session);
	void onAdminMessage(FixClientSimSession fixClientSimSession, Message message);

}
