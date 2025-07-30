package com.f1.amioktaauth;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.web.AmiWebProperties;
import com.f1.ami.web.AmiWebSSOPlugin;
import com.f1.ami.web.auth.AmiAuthUser;
import com.f1.ami.web.auth.BasicAmiAuthUser;
import com.f1.container.ContainerTools;
import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpSession;
import com.f1.suite.web.HttpRequestAction;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.encrypt.EncoderUtils;
import com.f1.utils.jsonmap.JsonMapHelper;
import com.f1.utils.rest.RestHelper;

// NOTE: please refer to AmiWebOAuthPlugin and AmiWebOAuthSsoSession before making changes.
public class AmiOktaOauthPlugin implements AmiWebSSOPlugin {
	private static final String AMISCRIPT_VARIABLE = "amiscript.variable.";
	private static final String OAUTH_STATE = "OAUTH_STATE";
	private static final String OAUTH_STATE_CSRF_PROTECT = "OAUTH_STATE_CSRF_PROT";
	private static final String OAUTH_CODE_VERIFIER = "OAUTH_CODE_VERIFIER";
	private static final String OAUTH_NONCE = "OAUTH_NONCE";
	public static Logger log = LH.get();
	private String authServerDomain;
	private String clientId;
	private String clientSecret;
	private String redirectUri;
	private String authorizationEndpoint;
	private String accessTokenEndpoint;
	private String codeChallengeMethod;
	private Boolean debug;
	private String digestAlgo;
	private String scope;
	private String userNameField;
	private Set<String> amiAdmins;
	private Set<String> amiDevs;
	private String amiIsAdminField;
	private String amiIsDevField;
	private String redirectEndpoint; // ex: myEndPoint/
	private boolean dynamicRedirect;

	public void init(ContainerTools tools, PropertyController props) {
		this.authServerDomain = props.getRequired(AmiWebProperties.PROPERTY_OKTA_OAUTH_SERVER_DOMAIN);
		this.clientId = props.getRequired(AmiWebProperties.PROPERTY_OKTA_OAUTH_CLIENT_ID);
		this.clientSecret = props.getRequired(AmiWebProperties.PROPERTY_OKTA_OAUTH_CLIENT_SECRET);
		this.redirectUri = props.getRequired(AmiWebProperties.PROPERTY_OKTA_OAUTH_REDIRECT_URI);
		this.dynamicRedirect = props.getOptional(AmiWebProperties.PROPERTY_OAUTH_DYNAMIC_REDIRECT, false);
		this.authorizationEndpoint = props.getOptional(AmiWebProperties.PROPERTY_OKTA_OAUTH_AUTHORIZATION_ENDPOINT, "/oauth2/default/v1/authorize");
		this.accessTokenEndpoint = props.getOptional(AmiWebProperties.PROPERTY_OKTA_OAUTH_TOKEN_ENDPOINT, "/oauth2/default/v1/token");
		this.codeChallengeMethod = props.getOptional(AmiWebProperties.PROPERTY_OKTA_OAUTH_CODE_CHALLENGE_METHOD, "S256");
		this.digestAlgo = props.getOptional(AmiWebProperties.PROPERTY_OKTA_OAUTH_CODE_CHALLENGE_METHOD, "SHA-256");
		this.scope = props.getOptional(AmiWebProperties.PROPERTY_OKTA_OAUTH_SCOPE, "openid profile email offline_access");
		this.userNameField = props.getOptional(AmiWebProperties.PROPERTY_OKTA_OAUTH_USERNAME_FIELD, "email");
		this.amiIsAdminField = props.getOptional(AmiWebProperties.PROPERTY_OKTA_OAUTH_AMI_ISADMIN_FIELD);
		this.amiIsDevField = props.getOptional(AmiWebProperties.PROPERTY_OKTA_OAUTH_AMI_ISDEV_FIELD);

		String amiAdminsValues = props.getOptional(AmiWebProperties.PROPERTY_OKTA_OAUTH_AMI_ISADMIN_VALUES);
		String amiDevValues = props.getOptional(AmiWebProperties.PROPERTY_OKTA_OAUTH_AMI_ISDEV_VALUES);
		if (amiAdminsValues != null)
			this.amiAdmins = SH.splitToSet(",", amiAdminsValues);
		if (amiDevValues != null)
			this.amiDevs = SH.splitToSet(",", amiDevValues);

		this.debug = props.getOptional(AmiWebProperties.PROPERTY_OKTA_OAUTH_DEBUG, Boolean.FALSE);
	}

	public String getPluginId() {
		return "AmiOktaOauthPlugin";
	}
	public String buildAuthRequest(HttpRequestResponse req) throws Exception {
		HttpSession session = req.getSession(true);
		String sessionId = (String) session.getSessionId();
		if (sessionId == null) {
			LH.warning(log, "Could not send request to " + this.authorizationEndpoint, " sessionId is null");
			return null;
		}
		if (this.dynamicRedirect) {
			//		 build uri from request
			String baseUri = req.getHeader().get("Host");
			StringBuilder biu = new StringBuilder("http");
			String redirectUri;
			if (req.getIsSecure()) {
				biu.append("s");
			}
			biu.append("://").append(baseUri).append("/").append(this.redirectEndpoint);
			redirectUri = biu.toString();
			this.redirectUri = redirectUri;
		}
		Map<String, Object> attributes = session.getAttributes();
		String codeVerifier = WebHelper.generateUrlSafeStringLen(43);
		String codeChallenge = generateHashAndEncodeUrl(codeVerifier);
		String stateCSRFprot = WebHelper.generateUrlSafeStringLen(11);
		String nonce = WebHelper.generateUrlSafeString(24);
		String state = generateState(req, sessionId, stateCSRFprot);
		attributes.put(OAUTH_CODE_VERIFIER, codeVerifier);
		attributes.put(OAUTH_STATE_CSRF_PROTECT, stateCSRFprot);
		attributes.put(OAUTH_NONCE, nonce);
		//		String options = "Secure; HttpOnly; Path=" + this.redirectUri + ";SameSite=none;";
		//		req.putCookie("myCookie", "test123", null, 0l, options);

		try {
			String urlString = this.authServerDomain + "/" + this.authorizationEndpoint;
			Map<String, String> queryParams = new HasherMap<String, String>();
			queryParams.put("client_id", this.clientId);
			queryParams.put("redirect_uri", SH.encodeUrl(this.redirectUri));
			queryParams.put("scope", SH.encodeUrl(this.scope));
			queryParams.put("response_type", "code");
			queryParams.put("code_challenge_method", this.codeChallengeMethod);
			queryParams.put("code_challenge", codeChallenge);
			queryParams.put("state", state);
			queryParams.put("nonce", nonce);
			urlString += "?" + SH.joinMap('&', '=', queryParams);
			if (this.debug)
				LH.info(log, "Build oauth authorize req: " + urlString);
			return urlString;
		} catch (Exception e) {
			LH.warning(log, "Could not send request to " + this.authorizationEndpoint, e);
			return null;
		}
	}

	public AmiAuthUser processResponse(HttpRequestAction req) throws Exception {
		HttpRequestResponse request = req.getRequest();
		Map<String, Object> sessAttributes = request.getSession(false).getAttributes();
		if (this.debug)
			LH.info(log, "Okta authorize response: ", request.getParams());

		if (!this.validateState(request, sessAttributes))
			return null;

		String codeVerifier = (String) sessAttributes.get(OAUTH_CODE_VERIFIER);
		if (codeVerifier == null) {
			LH.warning(log, "Invalid code verifier");
			return null;
		}
		String authorizationCode = request.getParams().get("code");
		Map<String, Object> result = getToken(authorizationCode, codeVerifier);
		String accessToken = (String) result.get("access_token");
		if (SH.is(accessToken)) {
			String idToken = (String) result.get("id_token");
			Map<String, Object> pollingData = createPollingProperties(result);
			Map<String, Object> amiAttributes = processIdToken(idToken, sessAttributes, pollingData);
			if (this.debug)
				LH.info(log, "AMI returned attributes: ", amiAttributes);
			String username = (String) amiAttributes.remove(this.userNameField);
			return new BasicAmiAuthUser(username, amiAttributes);
		} else {
			LH.warning(log, "Unable to retrieve access token. Response: ", result);
			return null;
		}
	}
	private Map<String, Object> createPollingProperties(Map<String, Object> initialTokenResponse) {
		Map pollingProps = new HasherMap<String, Object>();
		pollingProps.put("access_token", initialTokenResponse.get("access_token"));
		pollingProps.put("refresh_token", initialTokenResponse.get("refresh_token"));
		pollingProps.put("token_exp_sec", initialTokenResponse.get("expires_in"));
		pollingProps.put("client_id", this.clientId);
		pollingProps.put("client_secret", this.clientSecret);
		pollingProps.put("scope", this.scope);
		pollingProps.put("redirect_uri", this.redirectUri);
		pollingProps.put("auth_server_domain", this.authServerDomain);
		pollingProps.put("token_endpoint", this.accessTokenEndpoint);
		pollingProps.put("debug", this.debug);
		return pollingProps;

	};
	private String generateState(HttpRequestResponse request, String sessionId, String stateCSRFprot) {
		if (sessionId == null)
			return null;
		String value = stateCSRFprot + "|" + generateHashAndEncodeUrl(sessionId);
		String state = EncoderUtils.encode64UrlSafe(value.getBytes());
		return state;

	}
	private boolean validateState(HttpRequestResponse request, Map<String, Object> sessAttributes) {
		String origStateCSRFprot = (String) sessAttributes.get(OAUTH_STATE_CSRF_PROTECT);

		String sessionId = (String) request.getSession(false).getSessionId();
		//		String sessionId = request.getCookies().get(request.getSession(false).getManager().getCookieName());
		String origState = generateState(request, sessionId, origStateCSRFprot);
		String responseState = request.getParams().get("state");

		if (!SH.equals(origState, responseState)) {
			LH.warning(log, "State did not match. Oauth server responded with an invalid state");
			return false;
		}
		return true;

	}

	private Map<String, Object> processIdToken(String idToken, Map<String, Object> sessionAttributes, Map<String, Object> pollingData) {
		String[] idChunks = idToken.split("\\.");
		OH.assertGe(idChunks.length, 2);
		//		String idHeader = new String(Base64.getUrlDecoder().decode(idChunks[0]));
		//		String idPayload = new String(Base64.getUrlDecoder().decode(idChunks[1]));
		String idHeader = new String(EncoderUtils.decode64(idChunks[0]));
		String idPayload = new String(EncoderUtils.decode64(idChunks[1]));
		Map<String, Object> idPayloadMap = (Map<String, Object>) ObjectToJsonConverter.INSTANCE_COMPACT.stringToObject(idPayload);

		String origNonce = (String) sessionAttributes.get(OAUTH_NONCE);
		String responseNonce = (String) idPayloadMap.get("nonce");
		if (!SH.equals(origNonce, responseNonce)) {
			LH.warning(log, "Nonce did not match. Oauth server responded with an invalid nonce");
			return null;
		}

		if (this.debug)
			LH.info(log, "Id Header: ", idHeader, " Id Payload: ", idPayload);

		Map<String, Object> attr = CH.<String, Object> m();
		processAmiFields(idPayloadMap, attr, pollingData);

		return attr;
	}
	private void processAmiFields(Map<String, Object> idPayloadMap, Map<String, Object> attrSink, Map<String, Object> pollingData) {
		// Username
		String uid = (String) JsonMapHelper.INSTANCE_JSON_NESTED_GETTER_SINGLE.getAlt(idPayloadMap, this.userNameField, '.');
		addAttribute(attrSink, this.userNameField, uid, idPayloadMap);

		boolean isAdmin = false;
		boolean isDev = false;
		//Handle AmiAdmins
		if (this.amiIsAdminField != null) {
			Object value = JsonMapHelper.INSTANCE_JSON_NESTED_GETTER_SINGLE.getAlt(idPayloadMap, this.amiIsAdminField, '.');
			if (value instanceof String) {
				String vs = (String) value;
				if (vs != null && this.amiAdmins.contains(vs))
					isAdmin = true;
			} else if (value instanceof List) {
				List<String> vl = (List<String>) value;
				if (vl != null && CH.containsAny(this.amiAdmins, vl))
					isAdmin = true;
			}

		}

		//Handle AmiDevs
		if (this.amiIsDevField != null) {
			Object value = JsonMapHelper.INSTANCE_JSON_NESTED_GETTER_SINGLE.getAlt(idPayloadMap, this.amiIsDevField, '.');
			if (value instanceof String) {
				String vs = (String) value;
				if (vs != null && this.amiDevs.contains(vs))
					isDev = true;
			} else if (value instanceof List) {
				List<String> vl = (List<String>) value;
				if (vl != null && CH.containsAny(this.amiDevs, vl))
					isDev = true;
			}
		}

		if (this.amiIsAdminField != null)
			addAttribute(attrSink, AmiAuthUser.PROPERTY_ISADMIN, isAdmin ? "true" : "false", idPayloadMap);
		if (this.amiIsDevField != null)
			addAttribute(attrSink, AmiAuthUser.PROPERTY_ISDEV, isDev ? "true" : "false", idPayloadMap);

		AmiOktaSsoSession sso = (AmiOktaSsoSession) attrSink.get(AmiAuthUser.PROPERTY_SSO_SESSION);
		if (sso == null) {
			sso = new AmiOktaSsoSession((String) pollingData.get("access_token"), this.authServerDomain, this.userNameField, pollingData);
			addAttribute(attrSink, AmiAuthUser.PROPERTY_SSO_SESSION, sso, idPayloadMap);
		}
		for (Entry<String, Object> e : idPayloadMap.entrySet()) {
			addAmiscriptVariableAttribute(attrSink, e.getKey(), e.getValue(), idPayloadMap);
		}

	}
	private void addAmiscriptVariableAttribute(Map<String, Object> attributes, String key, Object val, Object payload) {
		key = AMISCRIPT_VARIABLE + key;
		if (attributes.containsKey(key))
			if (this.debug)
				LH.info(log, "Attributes key `", key, "` already exists. Attributes:  ", attributes, " for oauth response: ", payload);
			else
				LH.info(log, "Attributes key `", key, "` already exists. ");

		if (val instanceof String)
			val = SH.doubleQuote((String) val);
		attributes.put(key, val);
	}
	private void addAttribute(Map<String, Object> attributes, String key, Object val, Object payload) {
		if (attributes.containsKey(key))
			if (this.debug)
				LH.info(log, "Attributes key `", key, "` already exists. Attributes:  ", attributes, " for oauth response: ", payload);
			else
				LH.info(log, "Attributes key `", key, "` already exists. ");

		attributes.put(key, val);
	}
	public String getExpectedResponsePath() {
		return this.redirectEndpoint = SH.afterFirst(SH.afterFirst(this.redirectUri, "://", null), '/');
	}
	@Override
	public String getLogoutRedirectPath() {
		return null;
	}
	private String generateHashAndEncodeUrl(String codeVerifier) {
		if (codeVerifier == null)
			return null;
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance(this.digestAlgo);
		} catch (NoSuchAlgorithmException e) {
			LH.warning(log, "Could not get algorithm instance ", this.digestAlgo, e);
			return null;
		}
		byte[] hash = digest.digest(codeVerifier.getBytes(Charset.forName("UTF-8")));
		//		String hashStr = Base64.getUrlEncoder().encodeToString(hash);
		String hashStr = EncoderUtils.encode64UrlSafe(hash);
		hashStr = SH.replaceAll(hashStr, '+', '-');
		hashStr = SH.replaceAll(hashStr, '/', '_');
		hashStr = SH.replaceAll(hashStr, '=', "");
		String challenge = hashStr;
		return challenge;
	}
	private Map<String, Object> getToken(String authorizationCode, String codeVerifier) {
		StringBuilder sb = new StringBuilder();
		SH.clear(sb);
		byte httpMethod = RestHelper.HTTP_POST;

		// To prevent confusion and prevent double `/` eg https://domain//api/accessToken
		// This should be this.authServerDomain + this.accessTokenEndpoint;
		// However making this change can break existing configurations, as users should update their properties to add a leading slash
		// Otherwise they might generate the following url https://domainapi/accessToken
		String urlBase = this.authServerDomain + "/" + this.accessTokenEndpoint;
		Map<String, String> httpHeaders = new HashMap<String, String>();
		RestHelper.addContentType(httpHeaders, RestHelper.CONTENT_TYPE_FORM_URLENCODED);
		RestHelper.addBasicAuthentication(httpHeaders, this.clientId, this.clientSecret.toCharArray());
		httpHeaders.put("accept", "application/json");

		// 4 HTTP Body
		Map<String, String> bodyParamsMap = new HashMap<String, String>();
		bodyParamsMap.put("grant_type", "authorization_code");
		bodyParamsMap.put("redirect_uri", SH.encodeUrl(this.redirectUri));
		bodyParamsMap.put("code", authorizationCode);
		bodyParamsMap.put("code_verifier", codeVerifier);

		String bodyParams = SH.joinMap("&", "=", bodyParamsMap);

		boolean ignoreCerts = false;
		int timeout = -1;
		Map<String, List<String>> returnHeadersSink = new LinkedHashMap<String, List<String>>();
		byte[] data = RestHelper.sendRestRequest(httpMethod, urlBase, httpHeaders, bodyParams, ignoreCerts, timeout, returnHeadersSink, this.debug);

		Map<String, Object> myResult = (Map<String, Object>) RestHelper.parseRestResponse(data, this.debug);
		return myResult;
	}
	@Override
	public String handleLogout(HttpRequestResponse req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
