package com.f1.amioktaauth;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.web.AmiWebLayoutHelper;
import com.f1.ami.web.auth.AmiSsoSessionImpl;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.rest.RestHelper;

// NOTE: please refer to AmiWebOAuthPlugin and AmiWebOAuthSsoSession before making changes.
public class AmiOktaSsoSession extends AmiSsoSessionImpl implements Runnable {

	private static final Logger log = LH.get();
	private Thread thread;
	private Map<String, Object> pollingData;

	public AmiOktaSsoSession(String accessToken, String providerUrl, String username, Map<String, Object> pollingData) {
		super(accessToken, "OKTA", providerUrl, username, Collections.EMPTY_MAP);
		LH.info(log, "Creating Okta SSO Session for ", username);
		this.pollingData = pollingData;
		this.thread = new Thread(this, "OKTA-" + username);
		thread.setDaemon(false);
		thread.start();
	}

	public void run() {
		for (;;) {
			int accessTokenExpMillis = (Integer) this.pollingData.get("token_exp_sec") * 1000;
			OH.sleep(accessTokenExpMillis);
			LH.info(log, "Access token expired. Polling for new token at " + getProviderUrl() + " for " + getUsername());
			if (!isAlive())
				break;
			try {
				if (!renewAccessToken())
					super.killSession();
			} catch (Exception e) {
				LH.warning(log, "Error", e);
				killSession();
			}
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

		StringBuilder sb = new StringBuilder();
		SH.clear(sb);
		byte httpMethod = RestHelper.HTTP_POST;

		String clientId = (String) pollingData.get("client_id");
		String clientSecret = (String) pollingData.get("client_secret");
		String redirectUri = (String) pollingData.get("redirect_uri");
		String scope = (String) pollingData.get("scope");
		String refreshToken = (String) pollingData.get("refresh_token");

		String endpoint = getProviderUrl() + (String) pollingData.get("token_endpoint");
		Map<String, String> httpHeaders = new HashMap<String, String>();
		RestHelper.addContentType(httpHeaders, RestHelper.CONTENT_TYPE_FORM_URLENCODED);
		RestHelper.addBasicAuthentication(httpHeaders, clientId, clientSecret.toCharArray());
		httpHeaders.put("accept", "application/json");

		// 4 HTTP Body
		Map<String, String> bodyParamsMap = new HashMap<String, String>();
		bodyParamsMap.put("grant_type", "refresh_token");
		bodyParamsMap.put("redirect_uri", SH.encodeUrl(redirectUri));
		bodyParamsMap.put("scope", SH.encodeUrl(scope));
		bodyParamsMap.put("refresh_token", refreshToken);

		String bodyParams = SH.joinMap("&", "=", bodyParamsMap);

		boolean debug = (Boolean) this.pollingData.get("debug");
		boolean ignoreCerts = false;
		int timeout = -1;
		Map<String, List<String>> returnHeadersSink = new LinkedHashMap<String, List<String>>();
		try {
			byte[] data = RestHelper.sendRestRequest(httpMethod, endpoint, httpHeaders, bodyParams, ignoreCerts, timeout, returnHeadersSink, debug);
			if (data != null) {
				Map<String, Object> response = AmiWebLayoutHelper.parseJsonSafe(new String(data), null);
				if (response != null || response.containsKey("access_token")) {
					String newAccessToken = (String) response.get("access_token");
					if (newAccessToken != getAccessToken())
						setAccesToken(newAccessToken);
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			LH.warning(log, "Exception renewing access token ", e);
			return false;
		}
	}

}
