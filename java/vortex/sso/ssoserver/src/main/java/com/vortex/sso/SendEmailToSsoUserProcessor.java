package com.vortex.sso;

import java.util.Collections;

import com.f1.container.OutputPort;
import com.f1.container.RequestMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicRequestProcessor;
import com.f1.email.EmailClient;
import com.f1.utils.CH;
import com.sso.messages.SendEmailToSsoUserRequest;
import com.sso.messages.SendEmailToSsoUserResponse;
import com.sso.messages.SsoUpdateEvent;
import com.sso.messages.SsoUser;

public class SendEmailToSsoUserProcessor extends BasicRequestProcessor<SendEmailToSsoUserRequest, SsoState, SendEmailToSsoUserResponse> {

	public final OutputPort<SsoUpdateEvent> broadcastPort = newOutputPort(SsoUpdateEvent.class);
	public SendEmailToSsoUserProcessor() {
		super(SendEmailToSsoUserRequest.class, SsoState.class, SendEmailToSsoUserResponse.class);
	}

	@Override
	protected SendEmailToSsoUserResponse processRequest(RequestMessage<SendEmailToSsoUserRequest> action, SsoState state, ThreadScope threadScope) throws Exception {
		SendEmailToSsoUserRequest request = action.getAction();
		SendEmailToSsoUserResponse r = nw(SendEmailToSsoUserResponse.class);
		EmailClient client = (EmailClient) getServices().getService("EMAIL");
		SsoUser user = null;
		if (request.getEmail() != null) {
			user = state.getUserByEmail(request.getEmail());
			if (user == null) {
				r.setMessage("Email not found: " + request.getEmail());
				return r;
			}
		}
		if (request.getUserName() != null) {
			SsoUser user2 = state.getUserByUserName(request.getUserName());
			if (user2 == null) {
				r.setMessage("user name not found: " + request.getUserName());
				return r;
			}
			if (user != null || user != user2) {
				r.setMessage("user name not associated with email: " + request.getUserName() + ", " + request.getEmail());
				return r;
			}
			user = user2;
		}

		if (request.getUserId() != null) {
			SsoUser user2 = state.getUserByGroupId(request.getUserId());
			if (user2 == null) {
				r.setMessage("user id not found: " + request.getUserId());
				return r;
			} else {
				if (user != user2) {
					r.setMessage("user id missmatch: " + request.getUserId());
					return r;
				}
			}
			user = user2;
		}

		r.setUser(user);
		client.sendEmail(request.getBody(), request.getSubject(), CH.l(user.getEmail()), client.getUsername(), request.getIsHtml(), Collections.EMPTY_LIST);
		r.setOk(true);
		broadcastSsoEvent(request, r, user, request.getSession(), request.getNamespace(), threadScope);
		return r;
	}

	private void broadcastSsoEvent(SendEmailToSsoUserRequest request, SendEmailToSsoUserResponse response, SsoUser user, String session, String namespace, ThreadScope threadScope) {
		SsoUpdateEvent event = nw(SsoUpdateEvent.class);
		event.setUsers(CH.l(user));
		event.setOk(response.getOk());
		event.setMessage(response.getMessage());
		event.setType(SsoUpdateEvent.USER_EMAIL);
		event.setSession(session);
		event.setName(user == null ? null : user.getUserName());
		event.setNow(this.getTools().getNow());
		event.setNamespace(namespace);
		event.setClientLocation(request.getClientLocation());
		event.setMemberId(user == null ? -1 : user.getId());
		broadcastPort.send(event, threadScope);
	}
}
