package com.vortex.sso;

import java.util.Random;

import com.f1.container.OutputPort;
import com.f1.container.RequestMessage;
import com.f1.container.RequestOutputPort;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicRequestProcessor;
import com.f1.email.EmailClient;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.db.DbService;
import com.sso.messages.ResetPasswordRequest;
import com.sso.messages.ResetPasswordResponse;
import com.sso.messages.SsoUpdateEvent;
import com.sso.messages.SsoUser;
import com.sso.messages.UpdateSsoUserRequest;
import com.sso.messages.UpdateSsoUserResponse;

public class ResetPasswordProcessor extends BasicRequestProcessor<ResetPasswordRequest, SsoState, ResetPasswordResponse> {

	public final OutputPort<SsoUpdateEvent> broadcastPort = newOutputPort(SsoUpdateEvent.class);
	public final RequestOutputPort<UpdateSsoUserRequest, UpdateSsoUserResponse> updatePort = newRequestOutputPort(UpdateSsoUserRequest.class, UpdateSsoUserResponse.class);

	private ThreadScope threadScope;
	private ResetPasswordResponse r;
	private String session;

	public ResetPasswordProcessor() {
		super(ResetPasswordRequest.class, SsoState.class, ResetPasswordResponse.class);
	}

	@Override
	protected ResetPasswordResponse processRequest(RequestMessage<ResetPasswordRequest> action, SsoState state, ThreadScope threadScope) throws Exception {
		final DbService dbservice = (DbService) getServices().getService("DB");
		final ResetPasswordRequest request = action.getAction();
		session = request.getSession();
		this.threadScope = threadScope;
		r = nw(ResetPasswordResponse.class);

		SsoUser user = null;
		int status = 0;
		String message = null, resetq = null;

		if (SH.is(request.getEmail())) {
			user = state.getUserByEmail(request.getEmail());
			if (user == null) {
				status = ResetPasswordResponse.STATUS_USER_NOT_FOUND;
				message = "email not found. ";
			}
		} else if (SH.is(request.getUserName())) {
			user = state.getUserByUserName(request.getUserName());
			if (user == null) {
				status = ResetPasswordResponse.STATUS_USER_NOT_FOUND;
				message = "username not found. ";
			}
		} else {
			status = ResetPasswordResponse.STATUS_USER_NOT_FOUND;
			message = "must supply an email or username.";
		}

		if (user != null) {
			if (SH.isnt(request.getResetAnswer())) {
				resetq = user.getResetQuestion();
				status = ResetPasswordResponse.STATUS_NEED_ANSWER;
			} else if (OH.ne(SsoHelper.encode(request.getResetAnswer(), request.getEncodingAlgorithm(), user.getEncodingAlgorithm()), user.getResetAnswer())) {
				resetq = user.getResetQuestion();
				message = "Answer incorrect.";
				status = ResetPasswordResponse.STATUS_ANSWER_WRONG;
			} else if (user.getStatus() == SsoUser.STATUS_DISABLED || (user.getExpires() > 0 && user.getExpires() < getTools().getNow())) {
				resetq = user.getResetQuestion();
				message = "User account disabled.";
				status = ResetPasswordResponse.STATUS_USER_DISABLED;
			} else {
				String password = SsoHelper.generatePassword(new Random());
				final SsoUser user2 = nw(SsoUser.class);
				user2.setStatus(SsoUser.STATUS_ENABLED);
				user2.setPassword(SsoHelper.encode(password, SsoUser.ENCODING_PLAIN, user.getEncodingAlgorithm()));
				user2.setEncodingAlgorithm(user.getEncodingAlgorithm());
				final UpdateSsoUserRequest update = nw(UpdateSsoUserRequest.class);
				update.setSsoUserId(user.getId());
				update.setSsoUser(user2);

				UpdateSsoUserResponse updateResponse = updatePort.requestWithFuture(update, threadScope).getResult().getAction();
				if (!updateResponse.getOk()) {
					message = "internal error: " + updateResponse.getMessage();
					status = ResetPasswordResponse.STATUS_INTERNAL_ERROR;
				} else {
					message = "email sent to " + user.getEmail();
					status = ResetPasswordResponse.STATUS_PASSWORD_RESET;
				}
				user = user2;
				EmailClient emailClient = (EmailClient) getServices().getService("EMAIL");
				emailClient.sendEmail("new email:<B>" + password + "</B>", "Email reset", CH.l(user.getEmail()), "admin@3forge.com", true, null);
			}
		}

		r.setMessage(message);
		r.setStatus(status);
		r.setResetQuestion(resetq);
		broadcastSsoEvent(user, request.getNamespace(), request.getClientLocation());
		return r;
	}

	private void broadcastSsoEvent(SsoUser user, String namespace, String clientLocation) {
		SsoUpdateEvent event = nw(SsoUpdateEvent.class);
		event.setUsers(CH.l(user));
		event.setOk(r.getStatus() == ResetPasswordResponse.STATUS_PASSWORD_RESET);
		event.setMessage(r.getMessage());
		event.setType(SsoUpdateEvent.USER_RESET);
		event.setSession(session);
		event.setNow(this.getTools().getNow());
		event.setName(user.getUserName());
		event.setNamespace(namespace);
		event.setClientLocation(clientLocation);
		event.setMemberId(user.getId());
		broadcastPort.send(event, threadScope);
	}
}
