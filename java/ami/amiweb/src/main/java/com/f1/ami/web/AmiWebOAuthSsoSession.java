package com.f1.ami.web;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.web.auth.AmiSsoSessionImpl;
import com.f1.utils.EH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class AmiWebOAuthSsoSession extends AmiSsoSessionImpl implements Runnable {

	private static final Logger log = LH.get();
	private Thread thread;
	private AmiWebOAuthPluginImpl ssoPlugin;
	private Map<String, Object> accessTokenResponse;
	private long accessTokenModifiedTime;
	private int accessTokenExpiresIn;
	private int sessionCheckPeriodMillis;

	public AmiWebOAuthSsoSession(AmiWebOAuthPluginImpl ssoPlugin, String accessToken, String username, Map<String, Object> accessTokenResult) {
		super(accessToken, "OAuth", ssoPlugin.getProviderUrl(), username, new HashMap<String, Object>());
		this.accessTokenModifiedTime = EH.currentTimeMillis();
		this.ssoPlugin = ssoPlugin;
		this.accessTokenResponse = accessTokenResult;
		this.accessTokenExpiresIn = this.ssoPlugin.getAccessTokenExpiresIn(this.getAccessTokenResponse());
		this.sessionCheckPeriodMillis = this.ssoPlugin.getSessionCheckPeriodSeconds() * 1000;
		LH.info(log, accessTokenModifiedTime, "::Creating OAuth SSO Session and access token for ", username, " expires in ", this.accessTokenExpiresIn);
		if (accessTokenExpiresIn != AmiWebOAuthPluginImpl.ACCESS_TOKEN_NO_EXPIRE) {
			this.thread = new Thread(this, "OAuth-" + username);
			thread.setDaemon(false);
			thread.start();
		}
	}

	public void run() {
		LH.fine(log, EH.currentTimeMillis(), "Starting Polling thread for " + getUsername() + " with sessionCheckPeriodMillis=" + this.sessionCheckPeriodMillis);
		for (;;) {
			OH.sleep(this.sessionCheckPeriodMillis);
			try {
				if (isAlive() && !renewAccessToken())
					super.killSession();
			} catch (Exception e) {
				LH.warning(log, "OAuth sso session Error", e);
				killSession();
			}
			if (!isAlive())
				break;
		}
		thread = null;
	}
	@Override
	public void killSession() {
		super.killSession();
		LH.warning(log, "Closing session for " + getUsername());
		if (thread != null)
			thread.interrupt();
	}
	private boolean renewAccessToken() {
		LH.fine(log, EH.currentTimeMillis(), "Polling for new token at " + getProviderUrl() + " for " + getUsername());
		Map<String, Object> response = this.ssoPlugin.doRefreshTokenRequest(this);
		if (response != null) {
			String newAccessToken = this.ssoPlugin.getAccessToken(response);
			if (SH.is(newAccessToken)) {
				this.accessTokenModifiedTime = EH.currentTimeMillis();
				if (newAccessToken != getAccessToken())
					setAccesToken(newAccessToken);
				this.accessTokenResponse = response;
				this.accessTokenExpiresIn = this.ssoPlugin.getAccessTokenExpiresIn(this.getAccessTokenResponse());
				LH.fine(log, accessTokenModifiedTime, "Access Token Refreshed for ", this.getUsername(), " expires in ", this.accessTokenExpiresIn);
				return true;
			}
		}
		return false;

	}

	public Map<String, Object> getAccessTokenResponse() {
		return this.accessTokenResponse;
	}

}