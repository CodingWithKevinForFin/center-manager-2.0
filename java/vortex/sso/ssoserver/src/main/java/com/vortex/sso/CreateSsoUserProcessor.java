package com.vortex.sso;

import java.util.List;

import com.f1.container.OutputPort;
import com.f1.container.RequestMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicRequestProcessor;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.sso.messages.CreateSsoUserRequest;
import com.sso.messages.CreateSsoUserResponse;
import com.sso.messages.SsoGroup;
import com.sso.messages.SsoGroupAttribute;
import com.sso.messages.SsoGroupMember;
import com.sso.messages.SsoUpdateEvent;
import com.sso.messages.SsoUser;

public class CreateSsoUserProcessor extends BasicRequestProcessor<CreateSsoUserRequest, SsoState, CreateSsoUserResponse> {

	public final OutputPort<SsoUpdateEvent> broadcastPort = newOutputPort(SsoUpdateEvent.class);

	public CreateSsoUserProcessor() {
		super(CreateSsoUserRequest.class, SsoState.class, CreateSsoUserResponse.class);
	}

	@Override
	protected CreateSsoUserResponse processRequest(RequestMessage<CreateSsoUserRequest> action, SsoState state, ThreadScope threadScope) throws Exception {
		CreateSsoUserRequest request = action.getAction();
		SsoUser user = request.getUser();
		String session = request.getSession();
		final SsoDbService dbservice = (SsoDbService) getServices().getService("DB");
		final CreateSsoUserResponse r = nw(CreateSsoUserResponse.class);
		r.setOk(false);
		final boolean userNameExists = state.getUserByUserName(user.getUserName()) != null;
		final boolean emailExists = state.getUserByEmail(user.getEmail()) != null;

		if (userNameExists || emailExists) {
			StringBuilder errors = new StringBuilder();
			if (userNameExists)
				errors.append("Username already exists. ");
			if (emailExists)
				errors.append("Email already exists. ");
			r.setMessage(errors.toString());
		} else {
			if (request.getGroup() == null) {
				SsoGroup group = nw(SsoGroup.class);
				group.setName(user.getUserName());
				group.setType(SsoGroup.GROUP_TYPE_USER);
				request.setGroup(group);
			}
			long now = getTools().getNow();
			if (user.getExpires() == 0)
				user.setExpires(now + state.getDefaultExpiresDurationMs());
			StringBuilder errors = new StringBuilder();
			validate(request, errors, now);
			CreateSsoGroupProcessor.validate(request.getGroup(), request.getGroupAttributes(), errors);
			if (errors.length() > 0) {
				r.setMessage(errors.toString());
			} else {

				final java.sql.Connection con = dbservice.getConnection();
				try {
					CreateSsoGroupProcessor.createGroup(request, state, dbservice, con, r);
					user.setGroupId(request.getGroup().getId());
					dbservice.insertUser(user, con);
					state.addUser(user);
					r.setUser(user);
					r.setOk(true);
					r.setMessage(null);
				} catch (Exception e) {
					LH.warning(log, "Error creating user: ", user, e);
					r.setMessage("internal error");
				} finally {
					IOH.close(con);
				}
			}
		}

		broadcastSsoEvent(request, r.getUser(), r.getGroup(), r.getGroupAttributes(), r.getGroupMembers(), r.getMessage(), session, r.getOk(), threadScope);
		return r;
	}

	private void broadcastSsoEvent(CreateSsoUserRequest request, SsoUser responseUser, SsoGroup group, List<SsoGroupAttribute> attributes, List<SsoGroupMember> groupMembers,
			String message, String session, boolean okay, ThreadScope threadScope) {
		SsoUpdateEvent event = nw(SsoUpdateEvent.class);
		event.setUsers(CH.l(responseUser));
		event.setOk(okay);
		event.setMessage(message);
		event.setType(SsoUpdateEvent.USER_CREATE);
		event.setSession(session);
		event.setName(null);
		event.setNamespace(request.getNamespace());
		event.setClientLocation(request.getClientLocation());
		if (responseUser != null)
			event.setUsers(CH.l(responseUser));
		if (group != null)
			event.setGroups(CH.l(group));
		event.setGroupAttributes(attributes);
		event.setGroupMembers(groupMembers);
		event.setNow(this.getTools().getNow());
		broadcastPort.send(event, threadScope);
	}

	public static void validate(CreateSsoUserRequest request, StringBuilder errors, long now) {
		SsoUser user = request.getUser();
		if (!SH.isValidEmail(user.getEmail()))
			errors.append("Invalid email address. ");
		if (SH.isnt(user.getPassword()))
			errors.append("Password required. ");
		else if (user.getPassword().length() < 6)
			errors.append("Password must be at least 6 letters. ");
		if (SH.isnt(user.getUserName()))
			errors.append("Username required. ");
		if (user.getEncodingAlgorithm() == 0)
			errors.append("encoding required. ");
		if (user.getEncodingAlgorithm() == 0)
			errors.append("encoding required. ");
		if (SH.isnt(user.getResetQuestion()))
			errors.append("reset question required. ");
		if (SH.isnt(user.getResetAnswer()))
			errors.append("reset answer required. ");
		if (user.getExpires() < now)
			errors.append("Expiring time is in the past. ");
	}

}
