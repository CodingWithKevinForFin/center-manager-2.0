package com.vortex.ssoweb;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.f1.container.RequestOutputPort;
import com.f1.http.HttpHandler;
import com.f1.http.HttpRequestResponse;
import com.f1.suite.web.HttpRequestAction;
import com.f1.suite.web.HttpStateCreator;
import com.f1.suite.web.HttpStateHandler;
import com.f1.suite.web.WebState;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.sso.messages.SsoUser;
import com.sso.messages.UpdateSsoUserRequest;
import com.sso.messages.UpdateSsoUserResponse;

public class ResetPassword2HttpHandler extends HttpStateHandler implements HttpHandler {

	public ResetPassword2HttpHandler(HttpStateCreator stateCreator) {
		super(stateCreator);
		// TODO Auto-generated constructor stub
	}

	final public RequestOutputPort<UpdateSsoUserRequest, UpdateSsoUserResponse> loginPort = newRequestOutputPort(UpdateSsoUserRequest.class, UpdateSsoUserResponse.class);
	private String namespace;

	@Override
	public Object handle(HttpRequestAction request, WebState state) {
		final Map<String, Object> attributes = request.getRequest().getSession(false).getAttributes();
		final Map<String, String> params = request.getRequest().getParams();
		final String token = params.get("token");
		final String question = params.get("question");
		final String password = params.get("password");
		final String password2 = params.get("password2");

		request.getRequest().getAttributes().put("token", token);
		SsoUser origUser = (SsoUser) attributes.get("PASSWORD_RESET_USER");
		attributes.remove("error3");
		attributes.remove("error");
		attributes.remove("error2");

		if (SH.isnt(token)) {
			attributes.put("error3", "Token Required");
		} else if (SH.isnt(password)) {
			attributes.put("error3", "Password Required");
		} else if (SH.isnt(password2)) {
			attributes.put("error3", "Password Confirm Required");
		} else if (SH.isnt(question)) {
			attributes.put("error3", "Answer to custom question required");
		} else if (OH.ne(token.trim(), attributes.get("PASSWORD_RESET_TOKEN"))) {
			attributes.put("error3", "Invalid Token");
		} else if (OH.ne(NewSsoUserFormPortlet.encode(question, origUser.getEncodingAlgorithm()), origUser.getResetAnswer())) {
			attributes.put("error3", "Answer to custom question incorrect");
		} else if (OH.ne(password, password2)) {
			attributes.put("error3", "New passwords do not match");
		}
		if (attributes.containsKey("error3")) {
			try {
				request.getRequest().forward("reset2.htm");
			} catch (IOException e1) {
				LH.warning(log, "Error forwarding", e1);
			}
			return null;
		}

		final UpdateSsoUserRequest r = nw(UpdateSsoUserRequest.class);

		r.setSession(state.getPartitionId().toString());
		r.setClientLocation(state.getWebStatesManager().getRemoteAddress());
		r.setNamespace(namespace);
		r.setSsoUser(origUser.clone());
		r.setSsoUserId(origUser.getId());
		r.getSsoUser().setPassword(NewSsoUserFormPortlet.encode(password, origUser.getEncodingAlgorithm()));
		final UpdateSsoUserResponse response;
		try {
			response = loginPort.requestWithFuture(r, null).getResult(5, TimeUnit.SECONDS).getAction();
		} catch (Exception e) {
			LH.warning(log, "Error logging in", e);
			request.getRequest().getAttributes().put("error", "Internal Error");
			try {
				request.getRequest().forward("/index.htm");
			} catch (IOException e1) {
				LH.warning(log, "Error forwarding", e);
			}
			return null;
		}
		if (response.getOk()) {
			CH.removeOrThrow(attributes, "PASSWORD_RESET_USER");
			CH.removeOrThrow(attributes, "PASSWORD_RESET_TOKEN");
			CH.removeOrThrow(attributes, "PASSWORD_RESET_QUESTION");
			request.getRequest().sendRedirect("reset3.htm");
		} else {
			attributes.put("error3", response.getMessage());
			request.getRequest().sendRedirect("reset2.htm");
		}
		return null;
	}
	public void init() {
		this.namespace = getTools().getRequired("sso.namespace");
		super.init();
	}

	public static String generatePassword(Random random) {
		StringBuilder sb = new StringBuilder();
		sb.append((char) MH.rand(random, 'a', 'z' + 1));
		sb.append((char) MH.rand(random, 'a', 'z' + 1));
		sb.append((char) MH.rand(random, 'a', 'z' + 1));
		sb.append((char) MH.rand(random, 'A', 'Z' + 1));
		sb.append((char) MH.rand(random, 'A', 'Z' + 1));
		sb.append((char) MH.rand(random, 'A', 'Z' + 1));
		sb.append((char) MH.rand(random, '0', '9' + 1));
		sb.append((char) MH.rand(random, '0', '9' + 1));
		sb.append((char) MH.rand(random, '0', '9' + 1));
		sb.append("_");
		List<Character> chars = SH.toCharList(sb);
		Collections.shuffle(chars, random);
		return SH.fromCharList(chars);
	}
	@Override
	public void handleAfterUnlock(HttpRequestResponse req, Object data) {
		// TODO Auto-generated method stub

	}

}
