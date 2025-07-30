package com.vortex.ssoweb;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.f1.container.ContainerServices;
import com.f1.container.RequestOutputPort;
import com.f1.http.HttpHandler;
import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpSession;
import com.f1.suite.web.HttpRequestAction;
import com.f1.suite.web.HttpStateCreator;
import com.f1.suite.web.HttpStateHandler;
import com.f1.suite.web.UserLoginListener;
import com.f1.suite.web.WebState;
import com.f1.suite.web.WebStatesManager;
import com.f1.utils.FastByteArrayInputStream;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;
import com.sso.messages.LoginSsoUserRequest;
import com.sso.messages.LoginSsoUserResponse;
import com.sso.messages.SsoGroupAttribute;
import com.sso.messages.SsoUser;

public class LoginHttpHandler extends HttpStateHandler implements HttpHandler {

	final private UserLoginListener listener;

	private long timeoutMs;

	public LoginHttpHandler(HttpStateCreator stateCreator, UserLoginListener listener) {
		this(stateCreator, listener, 5000);
	}
	public LoginHttpHandler(HttpStateCreator stateCreator, UserLoginListener listener, long timeoutMs) {
		super(stateCreator);
		this.listener = listener;
		this.timeoutMs = timeoutMs;
	}

	private String indexPage = "/index.htm";

	final public RequestOutputPort<LoginSsoUserRequest, LoginSsoUserResponse> loginPort = newRequestOutputPort(LoginSsoUserRequest.class, LoginSsoUserResponse.class);
	private String namespace;
	private boolean shouldEncode = true;

	@Override
	public Object handle(HttpRequestAction request, WebState state) {
		HttpRequestResponse request2 = request.getRequest();
		Map<String, String> params = request2.getParams();
		if (!params.containsKey("username") && !params.containsKey("password")) {
			try {
				request2.forward(indexPage);
				return null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		final String password = params.get("password");
		final String username = params.get("username");
		String userAgent = request2.getHeader().get("User-Agent");
		final String tac = params.get("accept_agreement");
		String expectedTac = (String) request2.getHttpServer().getAttributes().get("termsSignature");
		if (expectedTac != null) {
			if (tac == null) {
				error(request, username, "Must accept the agreement below", "Must accept the agreement below", false);
				return null;
			} else if (OH.ne(expectedTac, tac)) {
				error(request, username, "Wrong agreement accepted", "Wrong agreement accepted", false);
				return null;
			}
		}
		final String newPassword = params.get("new_password");
		final LoginSsoUserRequest r = nw(LoginSsoUserRequest.class);
		r.setUserName(username);
		r.setSession(state.getPartitionId().toString());
		if (isShouldEncode()) {
			r.setEncodingAlgorithm(SsoUser.ENCODING_CHECKSUM64);
			r.setPassword(encode(password));
			r.setNewPassword(encode(newPassword));
		} else {
			r.setEncodingAlgorithm(SsoUser.ENCODING_PLAIN);
			r.setPassword(password);
			r.setNewPassword(newPassword);
		}
		WebStatesManager wsm = state.getWebStatesManager();
		r.setClientLocation(wsm.getRemoteAddress());
		r.setClientAgent(userAgent);
		r.setNamespace(namespace);
		final LoginSsoUserResponse response;
		try {
			response = loginPort.requestWithFuture(r, null).getResult(timeoutMs, TimeUnit.MILLISECONDS).getAction();
		} catch (Exception e) {
			LH.warning(log, "Error logging in", e);
			request2.getAttributes().put("error", "Internal Error");
			try {
				request2.forward(indexPage);
			} catch (IOException e1) {
				LH.warning(log, "Error forwarding", e);
			}
			return null;
		}
		state.touch(getTools().getNow());
		if (!wsm.isLoggedIn()) {
			if (evaluateResponse(response, username, request, state)) {
				success(log, getServices(), request.getRequest(), wsm, response.getUser(), response.getGroupAttributes(), tac);
				String redirect = this.listener.onLoginSuccess(request, state, false);
				if (redirect != null)
					request2.sendRedirect(redirect);
			} else
				state.reset();
		} else {
			if (evaluateResponse(response, username, request, state)) {
				HttpSession session = request2.getSession(false);
				if (OH.ne(wsm.getUser().getUserName(), username)) {
					if (session != null) {
						state.reset();
					}
					success(log, getServices(), request.getRequest(), wsm, response.getUser(), response.getGroupAttributes(), tac);
				}
				String redirect = this.listener.onLoginSuccess(request, state, true);
				if (redirect != null)
					request2.sendRedirect(redirect);
			} else
				state.reset();
		}
		return null;
	}

	public void init() {
		this.namespace = getTools().getRequired("sso.namespace");
		shouldEncode = getTools().getOptional("sso.encode", true);
		super.init();
	}
	static public void success(Logger log, ContainerServices services, HttpRequestResponse request, WebStatesManager state, SsoUser ssoUser,
			Map<String, SsoGroupAttribute> attributes, String tac) {
		if (tac != null)
			LH.info(log, "User '", ssoUser.getUserName(), "' checked box 'I accept the agreement below' for: '", tac, "'");
		state.setUser(new SsoWebUser(ssoUser));
		request.getSession(true).getAttributes().put("username", ssoUser.getUserName());
		SsoGroupAttribute timezoneAtt = attributes.get("Timezone");
		SsoGroupAttribute localeAtt = attributes.get("Locale");
		HashMap<String, Object> attributes2 = new HashMap<String, Object>();
		for (Entry<String, SsoGroupAttribute> e : attributes.entrySet())
			attributes2.put(e.getKey(), e.getValue());
		state.setUserAttributes(attributes2);

		String timezone = Caster_String.INSTANCE.cast((timezoneAtt == null ? TimeZone.getDefault().getID() : timezoneAtt.getValue()));
		String locale;
		if (localeAtt == null) {
			Locale defaultLocale = Locale.getDefault();
			locale = SH.join(":", defaultLocale.getLanguage(), defaultLocale.getCountry(), defaultLocale.getVariant());
		} else {
			locale = Caster_String.INSTANCE.cast(localeAtt.getValue());
		}
		String[] localeVals = SH.split(":", locale);
		LocaleFormatter formatter = services.getLocaleFormatterManager().createLocaleFormatter(new Locale(localeVals[0], localeVals[1], localeVals[2]),
				TimeZone.getTimeZone(timezone));
		//		state.setFormatter(formatter);
		state.logIn(services.getTools().getNow());

	}
	private boolean evaluateResponse(LoginSsoUserResponse response, String username, HttpRequestAction httpRequest, WebState state) {
		byte status = response.getStatus();
		String message = response.getMessage();
		String defaultMessage = "";
		switch (status) {
			case LoginSsoUserResponse.STATUS_OK:
				return true;
			case LoginSsoUserResponse.STATUS_PASSWORD_INVALID:
				defaultMessage = "Incorrect Password";
				break;
			case LoginSsoUserResponse.STATUS_USER_NOT_FOUND:
				defaultMessage = "User not found";
				break;
			case LoginSsoUserResponse.STATUS_ACCOUNT_DISABLED:
				defaultMessage = "Account inactive";
				break;
			case LoginSsoUserResponse.STATUS_INVALID_ENCODING:
				defaultMessage = "Invalid password encoding";
				break;
			case LoginSsoUserResponse.STATUS_INTERNAL_ERROR:
				defaultMessage = "Internal error, please try again";
				break;
			case LoginSsoUserResponse.STATUS_USER_COUNT_EXCEEDED:
				defaultMessage = "User Count for this server exceeded";
				break;
		}
		error(httpRequest, username, message, defaultMessage, status == LoginSsoUserResponse.STATUS_PASSWORD_EXPIRED || status == LoginSsoUserResponse.STATUS_PASSWORD_MUST_CHANGE);
		return false;
	}
	private void error(HttpRequestAction httpRequest, String username, String message, String defaultMessage, boolean resetPwd) {
		HttpRequestResponse req = httpRequest.getRequest();
		req.getAttributes().put("username", username);
		req.getAttributes().put("error", OH.noNull(message, defaultMessage));
		req.getAttributes().put("resetPwd", resetPwd);
		try {
			req.forward(indexPage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String encode(String text) {
		try {
			return text == null ? null : SH.toString(IOH.checkSumBsdLong(new FastByteArrayInputStream(text.getBytes())));
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}

	public boolean isShouldEncode() {
		return shouldEncode;
	}

	public void setShouldEncode(boolean shouldEncode) {
		assertNotStarted();
		this.shouldEncode = shouldEncode;
	}

	public String getIndexPage() {
		return indexPage;
	}

	public void setIndexPage(String indexPage) {
		this.indexPage = indexPage;
	}

	@Override
	public void handleAfterUnlock(HttpRequestResponse req, Object data) {
	}

}
