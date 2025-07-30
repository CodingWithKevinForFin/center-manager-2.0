package com.f1.ami.web.headless;

import java.util.Map;

import com.f1.ami.web.auth.AmiWebState;
import com.f1.suite.web.WebState;

public class AmiWebHeadlessWebState extends AmiWebState {

	final private Map<String, Object> userAttributes;
	final private String username;
	final private boolean isDev;
	final private boolean isAdmin;
	final private int maxSessions;
	final private AmiWebHeadlessSession session;

	public AmiWebHeadlessWebState(AmiWebHeadlessSession session, String pgId, String headlessName, String username, Map<String, Object> attributes, boolean isDev, boolean isAdmin,
			int maxSessions) {
		super(null, pgId, headlessName);
		this.session = session;
		this.session.getManager().incrementModCount();
		this.username = username;
		this.userAttributes = attributes;
		this.isDev = isDev;
		this.isAdmin = isAdmin;
		this.maxSessions = maxSessions;
		setType(WebState.class);
	}

	@Override
	public String getUserName() {
		return this.username;
	}

	@Override
	public Map<String, Object> getUserAttributes() {
		return this.userAttributes;
	}

	@Override
	public void killWebState() {
		super.removeMeFromManager();
	}

	public void forceKillHeadlessState() {
		super.killWebState();
	}

	@Override
	public boolean isAdmin() {
		return this.isAdmin;
	}
	@Override
	public boolean isDev() {
		return this.isDev;
	}

	@Override
	public int getMaxSessions() {
		return this.maxSessions;
	}

	@Override
	protected void onManagerChanged() {
		super.onManagerChanged();
		this.session.getManager().incrementModCount();
	}

}
