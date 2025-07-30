package com.vortex.ssoweb;

import java.io.IOException;
import java.security.SecureRandom;
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
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.SH;
import com.sso.messages.SendEmailToSsoUserRequest;
import com.sso.messages.SendEmailToSsoUserResponse;

public class ResetPasswordHttpHandler extends HttpStateHandler implements HttpHandler {

	public ResetPasswordHttpHandler(HttpStateCreator stateCreator) {
		super(stateCreator);
		// TODO Auto-generated constructor stub
	}

	final public RequestOutputPort<SendEmailToSsoUserRequest, SendEmailToSsoUserResponse> loginPort = newRequestOutputPort(SendEmailToSsoUserRequest.class,
			SendEmailToSsoUserResponse.class);
	private String namespace;

	@Override
	public Object handle(HttpRequestAction request, WebState state) {
		final String email = request.getRequest().getParams().get("email");
		final SendEmailToSsoUserRequest r = nw(SendEmailToSsoUserRequest.class);
		final Map<String, Object> attributes = request.getRequest().getSession(false).getAttributes();
		request.getRequest().getAttributes().put("email", email);
		r.setEmail(email);
		attributes.remove("error3");
		attributes.remove("error");
		attributes.remove("error2");
		r.setSubject("Password reset request for " + namespace);
		String resetToken = generatePassword(new SecureRandom());
		r.setBody("Enter the below Token into the 'Security Token' field in the reset password dialog:\n\n" + resetToken);

		r.setSession(state.getPartitionId().toString());
		r.setClientLocation(state.getWebStatesManager().getRemoteAddress());
		r.setNamespace(namespace);
		final SendEmailToSsoUserResponse response;
		try {
			response = loginPort.requestWithFuture(r, null).getResult(10, TimeUnit.SECONDS).getAction();
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
			attributes.put("PASSWORD_RESET_TOKEN", resetToken);
			attributes.put("PASSWORD_RESET_QUESTION", response.getUser().getResetQuestion());
			attributes.put("PASSWORD_RESET_USER", response.getUser());
			attributes.remove("error2");
			request.getRequest().sendRedirect("reset2.htm");
		} else {
			attributes.put("error2", response.getMessage());
			try {
				request.getRequest().forward("reset.htm");
			} catch (IOException e) {
				LH.warning(log, "Error forwarding", e);
			}
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
		List<Character> chars = SH.toCharList(sb);
		Collections.shuffle(chars, random);
		return SH.fromCharList(chars);
	}

	@Override
	public void handleAfterUnlock(HttpRequestResponse req, Object data) {
		// TODO Auto-generated method stub

	}

}
