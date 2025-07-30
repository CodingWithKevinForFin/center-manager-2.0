package com.f1.ami.web.auth;

import java.util.Map;

import com.f1.utils.EH;

public class AmiSsoSessionImpl implements AmiSsoSession {

	final private String provider;
	final private Map<String, Object> properties;
	final private String providerUrl;
	private String accessToken;
	private boolean isAlive = true;
	private long start;
	private final String username;

	public AmiSsoSessionImpl(String accessToken, String provider, String providerUrl, String username, Map<String, Object> properties) {
		super();
		this.start = EH.currentTimeMillis();
		this.accessToken = accessToken;
		this.provider = provider;
		this.providerUrl = providerUrl;
		this.properties = properties;
		this.username = username;
	}

	protected void setAccesToken(String accessToken) {
		this.accessToken = accessToken;
	}

	@Override
	public String getAccessToken() {
		return accessToken;
	}

	@Override
	public String getProvider() {
		return provider;
	}

	@Override
	public Map<String, Object> getProperties() {
		return properties;
	}

	@Override
	public void killSession() {
		this.isAlive = false;
	}

	@Override
	public boolean isAlive() {
		return isAlive;
	}

	@Override
	public String getProviderUrl() {
		return this.providerUrl;
	}

	public String getUsername() {
		return username;
	}

}
