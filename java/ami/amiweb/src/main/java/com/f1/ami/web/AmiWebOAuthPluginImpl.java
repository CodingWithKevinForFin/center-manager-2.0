package com.f1.ami.web;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.web.auth.AmiAuthUser;
import com.f1.ami.web.auth.AmiSsoSession;
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
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.encrypt.EncoderUtils;
import com.f1.utils.jsonmap.JsonMapHelper;
import com.f1.utils.rest.RestHelper;

public class AmiWebOAuthPluginImpl implements AmiWebSSOPlugin, AmiWebOAuthPlugin {
	public static final int ACCESS_TOKEN_NO_EXPIRE = -1;
	public static final String AMISCRIPT_VARIABLE = "amiscript.variable.";
	public static final String OAUTH_STATE = "OAUTH_STATE";
	public static final String OAUTH_STATE_CSRF_PROTECT = "OAUTH_STATE";
	public static final String OAUTH_CODE_VERIFIER = "OAUTH_CODE_VERIFIER";
	public static final String OAUTH_NONCE = "OAUTH_NONCE";
	public static Logger log = LH.get();
	protected String authServerDomain;
	protected String clientId;
	protected String clientSecret;
	protected String redirectUri;
	protected String logoutRedirectUri;
	protected String authorizationEndpoint;
	protected String logoutEndpoint;
	protected String accessTokenEndpoint;
	protected String refreshTokenEndpoint;
	protected String codeChallengeMethod;
	protected String digestAlgo;
	protected String scope;
	protected String userNameField;
	protected Set<String> amiAdmins;
	protected Set<String> amiDevs;
	protected String amiIsAdminField;
	protected String amiIsDevField;
	private boolean singleLogoutEnabled;
	protected Boolean debug;
	protected String accessTokenExpiresInParam;
	private int sessionCheckPeriodSeconds;
	private String amiRolesField;
	private String amiDefaultRoleField;
	private LinkedHashSet<String> amiRolesSet;
	private LinkedHashMap<String, LinkedHashMap<String, String>> amiRolesDefinition;
	private String refreshGrantType;
	private String refreshClientSecret;
	private String refreshRedirectUri;
	private String refreshScope;
	private String refreshClientId;
	private Boolean validateCerts;
	private String redirectEndpoint; // ex: myEndPoint/
	private boolean dynamicRedirect;

	@Override
	public void init(ContainerTools tools, PropertyController props) {
		this.authServerDomain = props.getRequired(AmiWebProperties.PROPERTY_OAUTH_SERVER_DOMAIN); //providerUrl
		this.clientId = props.getRequired(AmiWebProperties.PROPERTY_OAUTH_CLIENT_ID);
		this.clientSecret = props.getRequired(AmiWebProperties.PROPERTY_OAUTH_CLIENT_SECRET);
		this.redirectUri = props.getRequired(AmiWebProperties.PROPERTY_OAUTH_REDIRECT_URI);
		this.dynamicRedirect = props.getOptional(AmiWebProperties.PROPERTY_OAUTH_DYNAMIC_REDIRECT, false);
		this.logoutRedirectUri = props.getOptional(AmiWebProperties.PROPERTY_OAUTH_LOGOUT_REDIRECT_URI, "");
		if (SH.isnt(logoutRedirectUri))
			logoutRedirectUri = null;
		this.singleLogoutEnabled = props.getOptional(AmiWebProperties.PROPERTY_OAUTH_SINGLE_LOGOUT_ENABLED, false);
		this.authorizationEndpoint = props.getOptional(AmiWebProperties.PROPERTY_OAUTH_AUTHORIZATION_ENDPOINT, "/oauth2/default/v1/authorize");
		this.logoutEndpoint = props.getOptional(AmiWebProperties.PROPERTY_OAUTH_LOGOUT_ENDPOINT, "/oauth2/default/v1/logout");
		this.accessTokenEndpoint = props.getOptional(AmiWebProperties.PROPERTY_OAUTH_TOKEN_ENDPOINT, "/oauth2/default/v1/token");

		// REFRESH TOKEN
		this.refreshTokenEndpoint = props.getOptional(AmiWebProperties.PROPERTY_OAUTH_REFRESH_TOKEN_ENDPOINT, "/oauth2/default/v1/token");
		this.refreshGrantType = props.getOptional(AmiWebProperties.PROPERTY_OAUTH_REFRESH_GRANT_TYPE, "refresh_token");
		this.refreshClientId = props.getOptional(AmiWebProperties.PROPERTY_OAUTH_REFRESH_CLIENT_ID, this.clientId);
		this.refreshClientSecret = props.getOptional(AmiWebProperties.PROPERTY_OAUTH_REFRESH_CLIENT_SECRET, this.clientSecret);
		this.refreshScope = props.getOptional(AmiWebProperties.PROPERTY_OAUTH_REFRESH_SCOPE);
		this.refreshRedirectUri = props.getOptional(AmiWebProperties.PROPERTY_OAUTH_REFRESH_REDIRECT_URI);

		this.codeChallengeMethod = props.getOptional(AmiWebProperties.PROPERTY_OAUTH_CODE_CHALLENGE_METHOD, "S256");
		this.digestAlgo = props.getOptional(AmiWebProperties.PROPERTY_OAUTH_CODE_CHALLENGE_METHOD, "SHA-256");
		this.scope = props.getOptional(AmiWebProperties.PROPERTY_OAUTH_SCOPE, "openid profile email offline_access");
		this.userNameField = props.getOptional(AmiWebProperties.PROPERTY_OAUTH_USERNAME_FIELD, "email");
		this.accessTokenExpiresInParam = props.getOptional(AmiWebProperties.PROPERTY_OAUTH_ACCESS_TOKEN_EXPIRES_IN, "expires_in");
		this.amiIsAdminField = props.getOptional(AmiWebProperties.PROPERTY_OAUTH_AMI_ISADMIN_FIELD);
		this.amiIsDevField = props.getOptional(AmiWebProperties.PROPERTY_OAUTH_AMI_ISDEV_FIELD);

		this.amiRolesField = props.getOptional(AmiWebProperties.PROPERTY_OAUTH_AMI_ROLES_FIELD);
		this.amiDefaultRoleField = props.getOptional(AmiWebProperties.PROPERTY_OAUTH_AMI_DEFAULT_ROLE_FIELD);
		String amiRolesEnabled = props.getOptional(AmiWebProperties.PROPERTY_OAUTH_AMI_ROLES_ENABLED);
		this.amiRolesSet = CH.s(new LinkedHashSet<String>(), SH.split(',', amiRolesEnabled));
		this.amiRolesDefinition = new LinkedHashMap<String, LinkedHashMap<String, String>>();
		for (String amiRole : this.amiRolesSet) {
			PropertyController subProps = props.getSubPropertyController(AmiWebProperties.PROPERTY_OAUTH_AMI_ROLE_ + amiRole + '.');
			LinkedHashMap<String, String> amiRoleDef = new LinkedHashMap<String, String>();
			Set<String> roleAttributes = subProps.getKeys();
			for (String roleAttr : roleAttributes) {
				amiRoleDef.put(roleAttr, subProps.getRequired(roleAttr));
			}
			amiRolesDefinition.put(amiRole, amiRoleDef);
		}

		this.sessionCheckPeriodSeconds = props.getOptional(AmiWebProperties.PROPERTY_OAUTH_SESSION_CHECK_PERIOD_SECONDS, 60);

		String amiAdminsValues = props.getOptional(AmiWebProperties.PROPERTY_OAUTH_AMI_ISADMIN_VALUES);
		String amiDevValues = props.getOptional(AmiWebProperties.PROPERTY_OAUTH_AMI_ISDEV_VALUES);
		if (amiAdminsValues != null)
			this.amiAdmins = SH.splitToSet(",", amiAdminsValues);
		if (amiDevValues != null)
			this.amiDevs = SH.splitToSet(",", amiDevValues);

		this.debug = props.getOptional(AmiWebProperties.PROPERTY_OAUTH_DEBUG, Boolean.FALSE);
		this.validateCerts = props.getOptional(AmiWebProperties.PROPERTY_OAUTH_VALIDATE_CERTS, Boolean.TRUE);
	}

	@Override
	public String getPluginId() {
		return "AmiOAuthPlugin";
	}

	// Set Property: PROPERTY_OAUTH_REFRESH_TOKEN_ENDPOINT to empty string to disable
	public boolean isRefreshTokenEnabled() {
		return SH.is(this.refreshTokenEndpoint);
	}
	//Ex: 
	//Required
	//authServerDomain		https://login.microsoftonline.com/{tenant}
	//authEndpoint			/oauth2/v2.0/authorize?
	//client_id				acme
	//response_type			code (default) option code id_token token *property
	//redirect_url			https://localhost:33332/login
	//scope					ex okta openid profile email...

	//Optional:
	//code_challenge		...
	//code_challenge_method	default S256
	//state					
	//nonce
	@Override
	public String buildAuthRequest(HttpRequestResponse req) throws Exception {
		HttpSession session = req.getSession(true);
		String sessionId = (String) session.getSessionId();
		if (sessionId == null) {
			LH.warning(log, "OAuth: Could not send request to " + this.authorizationEndpoint, " sessionId is null");
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
		// 1| Generate code verifier, state csrf token, and nonce and store it in session attributes
		String codeVerifier = WebHelper.generateUrlSafeStringLen(43);
		String stateCSRFprot = WebHelper.generateUrlSafeStringLen(11);
		String nonce = WebHelper.generateUrlSafeString(24);

		attributes.put(OAUTH_CODE_VERIFIER, codeVerifier);
		attributes.put(OAUTH_STATE_CSRF_PROTECT, stateCSRFprot);
		attributes.put(OAUTH_NONCE, nonce);

		try {
			String sessionCookieName = session.getManager().getCookieName();
			req.putCookie(sessionCookieName, sessionId, null, -1, null);
			// 2| Generate authorize req redirect url
			String state = generateState(SH.toString(session.getSessionId()), stateCSRFprot);
			String code_challenge = generateHashAndEncodeUrl(codeVerifier);

			String urlString = this.authServerDomain + this.authorizationEndpoint;
			Map<String, String> queryParams = new HasherMap<String, String>();
			queryParams.put("client_id", this.clientId); //required
			queryParams.put("redirect_uri", SH.encodeUrl(this.redirectUri));
			queryParams.put("scope", SH.encodeUrl(this.scope));
			queryParams.put("response_type", "code");
			queryParams.put("code_challenge_method", this.codeChallengeMethod);
			queryParams.put("code_challenge", code_challenge);
			queryParams.put("state", state);
			queryParams.put("nonce", nonce);
			urlString += "?" + SH.joinMap('&', '=', queryParams);
			if (this.debug)
				LH.info(log, "Build oauth authorize req: ", urlString, " for session: ", sessionId);
			return urlString;
		} catch (Exception e) {
			LH.warning(log, "OAuth: Could not send request to " + this.authorizationEndpoint, " for session: ", sessionId, e);
			return null;
		}
	}

	protected Map<String, Object> doAccessTokenRequest(HttpRequestResponse request) {
		HttpSession session = request.getSession(false);
		String sessionId = (String) session.getSessionId();

		Map<String, Object> sessionAttributes = session.getAttributes();
		Map<String, String> authorizeResponseParams = request.getParams();
		if (this.debug) {
			LH.info(log, "OAuth authorize response: ", authorizeResponseParams, " for session: ", sessionId);
		}

		// 1| Validate state
		if (!this.validateState(request)) {
			LH.warning(log, "OAuth state changed, for session: ", sessionId);
			return null;
		}
		// 2| Get code_verifier and authorize code
		String codeVerifier = (String) sessionAttributes.get(OAUTH_CODE_VERIFIER);
		if (codeVerifier == null) {
			LH.warning(log, "OAuth Invalid code verifier, for session: ", sessionId);
			return null;
		}
		String authorizationCode = authorizeResponseParams.get("code");

		// 3| Build Access Token Request
		byte httpMethod = RestHelper.HTTP_POST;

		String urlBase = this.authServerDomain + this.accessTokenEndpoint;
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

		boolean ignoreCerts = !this.validateCerts; //Ignore certs if no validation required
		int timeout = -1;
		Map<String, List<String>> returnHeadersSink = new LinkedHashMap<String, List<String>>();
		byte[] data = RestHelper.sendRestRequest(httpMethod, urlBase, httpHeaders, bodyParams, ignoreCerts, timeout, returnHeadersSink, this.debug);

		Map<String, Object> myResult = (Map<String, Object>) RestHelper.parseRestResponse(data, this.debug);
		return myResult;
	}

	protected Map<String, Object> doRefreshTokenRequest(AmiWebOAuthSsoSession ssoSession) {
		Map<String, Object> accessTokenResponse = ssoSession.getAccessTokenResponse();

		byte httpMethod = RestHelper.HTTP_POST;

		// We may need to handle refresh_token_expires_in
		String refreshToken = (String) accessTokenResponse.get("refresh_token");
		if (SH.isnt(refreshToken)) {
			LH.warning(log, "Error renewing access token, no refreshToken available");

			return null;
		}

		String endpoint = this.getProviderUrl() + refreshTokenEndpoint;

		Map<String, String> httpHeaders = new HashMap<String, String>();
		RestHelper.addContentType(httpHeaders, RestHelper.CONTENT_TYPE_FORM_URLENCODED);
		RestHelper.addBasicAuthentication(httpHeaders, this.refreshClientId, this.refreshClientSecret.toCharArray());
		httpHeaders.put("accept", "application/json");

		// 4 HTTP Body
		Map<String, String> bodyParamsMap = new HashMap<String, String>();
		bodyParamsMap.put("grant_type", this.refreshGrantType);
		if (SH.is(this.refreshRedirectUri))
			bodyParamsMap.put("redirect_uri", SH.encodeUrl(this.refreshRedirectUri));
		if (SH.is(this.refreshScope))
			bodyParamsMap.put("scope", SH.encodeUrl(this.refreshScope));
		bodyParamsMap.put("refresh_token", refreshToken);

		String bodyParams = SH.joinMap("&", "=", bodyParamsMap);

		boolean ignoreCerts = !this.validateCerts;
		int timeout = -1;
		Map<String, List<String>> returnHeadersSink = new LinkedHashMap<String, List<String>>();
		try {
			byte[] data = RestHelper.sendRestRequest(httpMethod, endpoint, httpHeaders, bodyParams, ignoreCerts, timeout, returnHeadersSink, this.debug);

			Map<String, Object> myResult = (Map<String, Object>) RestHelper.parseRestResponse(data, this.debug);
			return myResult;

		} catch (Exception e) {
			LH.warning(log, "Exception renewing access token ", e);
			return null;
		}

	}

	@Override
	public String handleLogout(HttpRequestResponse req) throws Exception {
		HttpSession session = req.getSession(false);
		Map<String, Object> sessionAttributes = session.getAttributes();
		if (singleLogoutEnabled == false) {
			sessionAttributes.put(AmiAuthUser.PROPERTY_SSO_SESSION, null);
			return null;
		}

		AmiWebOAuthSsoSession ssoSession = (AmiWebOAuthSsoSession) sessionAttributes.get(AmiAuthUser.PROPERTY_SSO_SESSION);

		String idToken = (String) ssoSession.getProperties().get("id_token");
		if (SH.isnt(idToken)) {
			LH.warning(log, "Error processing oauth logout for the user ", ssoSession.getUsername(), " no id_token in the user's sso session");
			sessionAttributes.put(AmiAuthUser.PROPERTY_SSO_SESSION, null);
			return null;
		}

		// 4 HTTP Body
		Map<String, String> bodyParamsMap = new HashMap<String, String>();
		bodyParamsMap.put("id_token_hint", SH.encodeUrl(idToken));
		// (Optional) post_logout_redirect_url needs to match app's logout url in the idp 
		if (this.logoutRedirectUri != null)
			bodyParamsMap.put("post_logout_redirect_uri", SH.encodeUrl(this.logoutRedirectUri));
		// (Optional) state is a param that can be passed during the redirect to the logout url
		//		bodyParamsMap.put("state", null);

		String bodyParams = '?' + SH.joinMap("&", "=", bodyParamsMap);
		String endpoint = this.getProviderUrl() + this.logoutEndpoint + bodyParams;
		sessionAttributes.put(AmiAuthUser.PROPERTY_SSO_SESSION, null);
		return endpoint;

	}

	@Override
	public AmiAuthUser processResponse(HttpRequestAction req) throws Exception {
		HttpRequestResponse authorizeResponse = req.getRequest();

		// 1| Create access token request
		Map<String, Object> accessTokenResult = doAccessTokenRequest(authorizeResponse);
		if (accessTokenResult == null)
			return null;
		return createAmiUserSSOSession(authorizeResponse, accessTokenResult);
	}
	protected AmiAuthUser createAmiUserSSOSession(HttpRequestResponse request, Map<String, Object> accessTokenResult) throws Exception {
		HttpSession session = request.getSession(false);
		String sessionId = (String) session.getSessionId();
		Map<String, Object> sessionAttributes = session.getAttributes();

		String accessToken = (String) accessTokenResult.get("access_token");
		if (SH.isnt(accessToken)) {
			LH.warning(log, "OAuth unable to get access token for session: ", sessionId);
			return null;
		}

		String idToken = (String) accessTokenResult.get("id_token");
		String[] idChunks = idToken.split("\\.");
		OH.assertGe(idChunks.length, 2);
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
			LH.info(log, "Session ", sessionId, "returned Id Header: ", idHeader, " Id Payload: ", idPayload);

		// Original code for creating the sso session
		/*
		Map<String, Object> attr = CH.<String, Object> m();
		AmiOAuthSsoSession sso = (AmiOAuthSsoSession) attr.get(AmiAuthUser.PROPERTY_SSO_SESSION);
		if (sso == null) {
			sso = new AmiOAuthSsoSession((String) pollingData.get("access_token"), this.authServerDomain, this.userNameField, pollingData);
			addAttribute(attr, AmiAuthUser.PROPERTY_SSO_SESSION, sso, idPayloadMap);
		}
		*/

		String uid = (String) JsonMapHelper.INSTANCE_JSON_NESTED_GETTER_SINGLE.getAlt(idPayloadMap, this.userNameField, '.');

		// First Process AmiFields to prevent creating an unnecessary session
		Map<String, Object> amiAttributes = CH.<String, Object> m();
		boolean success = processAmiFields(uid, idPayloadMap, amiAttributes);
		if (!success) {
			LH.warning(log, "Session had invalid entitlements: ", sessionId);
			return null;
		}
		//
		AmiWebOAuthSsoSession sso = (AmiWebOAuthSsoSession) sessionAttributes.get(AmiAuthUser.PROPERTY_SSO_SESSION);
		if (sso == null)
			sso = (AmiWebOAuthSsoSession) this.createSsoSession(request, accessTokenResult, uid);
		for (String key : accessTokenResult.keySet()) {
			sso.getProperties().put(key, accessTokenResult.get(key));
		}

		//		addAttribute(amiAttributes, this.userNameField, uid, idPayloadMap); //Adding then removing is pointless
		addAttribute(amiAttributes, AmiAuthUser.PROPERTY_SSO_SESSION, sso);
		sessionAttributes.put(AmiAuthUser.PROPERTY_SSO_SESSION, sso);

		if (this.debug)
			LH.info(log, "AMI returned attributes: ", amiAttributes);
		//		String username = (String) amiAttributes.remove(this.userNameField);
		return new BasicAmiAuthUser(uid, amiAttributes);
	}

	protected AmiSsoSession createSsoSession(HttpRequestResponse request, Map<String, Object> accessTokenResult, String username) {
		return new AmiWebOAuthSsoSession(this, (String) accessTokenResult.get("access_token"), username, accessTokenResult);
	}

	final protected boolean processAmiFields(String uid, Map<String, Object> idPayloadMap, Map<String, Object> attrSink) {
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

		if (this.amiRolesField != null || this.amiDefaultRoleField != null) {

			String defaultRole = null;
			ArrayList<String> rolesList = null;
			LinkedHashMap<String, String> defaultRoleDef = null;
			if (this.amiDefaultRoleField != null) {
				defaultRole = (String) JsonMapHelper.INSTANCE_JSON_NESTED_GETTER_SINGLE.getAlt(idPayloadMap, this.amiDefaultRoleField, '.');
				defaultRoleDef = this.amiRolesDefinition.get(defaultRole);
				if (defaultRoleDef == null)
					LH.warning(log, "Role: ", SH.quoteOrNull(defaultRole), " is not enabled, see the properties: ", AmiWebProperties.PROPERTY_OAUTH_AMI_ROLES_ENABLED,
							" and define the role through properties ", AmiWebProperties.PROPERTY_OAUTH_AMI_ROLE_, defaultRole);
			}

			if (this.amiRolesField != null)
				rolesList = (ArrayList<String>) JsonMapHelper.INSTANCE_JSON_NESTED_GETTER_SINGLE.getAlt(idPayloadMap, this.amiRolesField, '.');

			// Find the first valid default role
			if (rolesList != null && rolesList.size() > 0 && defaultRoleDef == null) {
				for (String role : rolesList) {
					defaultRole = role; // Default Role is first role if default role is null
					defaultRoleDef = this.amiRolesDefinition.get(defaultRole);
					if (defaultRoleDef != null)
						break;
				}
				if (defaultRoleDef == null)
					defaultRole = null;
			}

			LH.info(log, "User ", uid, " has default role: ", SH.quoteOrNull(defaultRole), " roles list: ", ObjectToJsonConverter.INSTANCE_COMPACT.objectToString(rolesList));

			Map<String, Object> userRoleAttributes = new LinkedHashMap<String, Object>();
			List<String> layoutsList = new ArrayList<String>();
			// Handle default role, get all attributes

			// If there is no default role reject the login request
			if (defaultRoleDef == null) {
				LH.warning(log, "User ", uid, " doesn't have a default role and has no applicable roles that has access");
				return false;
			} else {
				// Adding all properties for the role
				for (String key : defaultRoleDef.keySet()) {
					userRoleAttributes.put(key, defaultRoleDef.get(key));
				}
				// Adding layouts attribute
				if (defaultRoleDef.containsKey(AmiAuthUser.PROPERTY_LAYOUTS))
					layoutsList.add(defaultRoleDef.get(AmiAuthUser.PROPERTY_LAYOUTS));
			}

			// For all other roles only get layouts attribute and join the values
			if (rolesList != null) {
				for (String role : rolesList) {
					if (SH.equals(defaultRole, role)) // Skip role if it's defaultRole as this has already been added
						continue;
					LinkedHashMap<String, String> roleDef = this.amiRolesDefinition.get(role);
					if (roleDef == null) {
						LH.warning(log, "Role: ", SH.quoteOrNull(role), " is not enabled, see the properties: ", AmiWebProperties.PROPERTY_OAUTH_AMI_ROLES_ENABLED,
								" and define the role through properties ", AmiWebProperties.PROPERTY_OAUTH_AMI_ROLE_, role);
						continue;
					}
					if (roleDef.containsKey(AmiAuthUser.PROPERTY_LAYOUTS))
						layoutsList.add(roleDef.get(AmiAuthUser.PROPERTY_LAYOUTS));
				}
				userRoleAttributes.put(AmiAuthUser.PROPERTY_LAYOUTS, SH.join(',', layoutsList));
			}

			// Copy attributes to attr sink
			LH.info(log, "Obtained attributes to User: ", uid, " Attributes: ", ObjectToJsonConverter.INSTANCE_COMPACT.objectToString(userRoleAttributes));
			for (String key : userRoleAttributes.keySet()) {
				//Do we need to double quote value if key is string
				attrSink.put(key, userRoleAttributes.get(key));
			}
		}

		if (this.amiIsAdminField != null) {
			if (attrSink.containsKey(AmiAuthUser.PROPERTY_ISADMIN))
				LH.info(log, "Overriding  ", AmiAuthUser.PROPERTY_ISADMIN, " because property ", AmiWebProperties.PROPERTY_OAUTH_AMI_ISADMIN_FIELD, " is set");

			addAttribute(attrSink, AmiAuthUser.PROPERTY_ISADMIN, isAdmin ? "true" : "false");
		}
		if (this.amiIsDevField != null) {
			if (attrSink.containsKey(AmiAuthUser.PROPERTY_ISDEV))
				LH.info(log, "Overriding  ", AmiAuthUser.PROPERTY_ISDEV, " because property ", AmiWebProperties.PROPERTY_OAUTH_AMI_ISDEV_FIELD, " is set");
			addAttribute(attrSink, AmiAuthUser.PROPERTY_ISDEV, isDev ? "true" : "false");
		}

		for (Entry<String, Object> e : idPayloadMap.entrySet()) {
			addAmiscriptVariableAttribute(attrSink, e.getKey(), e.getValue());
		}
		return true;
	}
	final protected void addAmiscriptVariableAttribute(Map<String, Object> attributes, String key, Object val) {
		key = AMISCRIPT_VARIABLE + key;
		if (attributes.containsKey(key))
			if (this.debug)
				LH.info(log, "Attributes key `", key, "` already exists. Attributes:  ", attributes, " for oauth response: ");
			else
				LH.info(log, "Attributes key `", key, "` already exists. ");

		if (val instanceof String)
			val = SH.doubleQuote((String) val);
		attributes.put(key, val);
	}
	final protected void addAttribute(Map<String, Object> attributes, String key, Object val) {
		if (attributes.containsKey(key))
			if (this.debug)
				LH.info(log, "Attributes key `", key, "` already exists. Attributes:  ", attributes);
			else
				LH.info(log, "Attributes key `", key, "` already exists. ");

		attributes.put(key, val);
	}
	@Override
	public String getExpectedResponsePath() {
		return this.redirectEndpoint = SH.afterFirst(SH.afterFirst(this.redirectUri, "://", null), '/');
	}

	@Override
	public String getLogoutRedirectPath() {
		return SH.afterFirst(SH.afterFirst(this.logoutRedirectUri, "://", null), '/');
	}
	protected String generateHashAndEncodeUrl(String codeVerifier) {
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

		String challenge = EncoderUtils.encode64UrlSafe(hash);
		challenge = SH.replaceAll(challenge, '+', '-');
		challenge = SH.replaceAll(challenge, '/', '_');
		challenge = SH.replaceAll(challenge, '=', "");
		return challenge;
	}
	protected String generateState(String sessionId, String stateCSRFprot) {
		if (sessionId == null)
			return null;
		String value = stateCSRFprot + "|" + generateHashAndEncodeUrl(sessionId);
		String state = EncoderUtils.encode64UrlSafe(value.getBytes());
		return state;

	}
	protected String getAccessToken(Map<String, Object> accessTokenResponse) {
		return CH.getOrThrow(Caster_String.INSTANCE, accessTokenResponse, "access_token");
	}
	// By default if the expires_in param isn't set the token shouldn't expire
	protected int getAccessTokenExpiresIn(Map<String, Object> accessTokenResponse) {
		if (accessTokenResponse.containsKey(this.accessTokenExpiresInParam)) {
			return CH.getOrThrow(Caster_Integer.PRIMITIVE, accessTokenResponse, this.accessTokenExpiresInParam);
		} else
			return ACCESS_TOKEN_NO_EXPIRE;
	}
	protected boolean validateState(HttpRequestResponse request) {
		HttpSession session = request.getSession(false);
		Map<String, Object> sessionAttributes = session.getAttributes();
		String origStateCSRFprot = (String) sessionAttributes.get(OAUTH_STATE_CSRF_PROTECT);

		// Make sure the session id hasn't changed
		String sessionCookieName = session.getManager().getCookieName();
		String sessionId = request.getCookies().get(sessionCookieName);
		String origState = generateState(sessionId, origStateCSRFprot);
		String responseState = request.getParams().get("state");

		return SH.equals(origState, responseState);
	}

	public String getProviderUrl() {
		return this.authServerDomain;
	}

	public int getSessionCheckPeriodSeconds() {
		return sessionCheckPeriodSeconds;
	}

}
