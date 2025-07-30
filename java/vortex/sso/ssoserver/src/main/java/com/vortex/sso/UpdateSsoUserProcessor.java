package com.vortex.sso;

import java.sql.Connection;

import com.f1.base.ValuedParam;
import com.f1.container.OutputPort;
import com.f1.container.RequestMessage;
import com.f1.container.RequestOutputPort;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicRequestProcessor;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.VH;
import com.sso.messages.SsoGroup;
import com.sso.messages.SsoUpdateEvent;
import com.sso.messages.SsoUser;
import com.sso.messages.UpdateSsoGroupRequest;
import com.sso.messages.UpdateSsoGroupResponse;
import com.sso.messages.UpdateSsoUserRequest;
import com.sso.messages.UpdateSsoUserResponse;

public class UpdateSsoUserProcessor extends BasicRequestProcessor<UpdateSsoUserRequest, SsoState, UpdateSsoUserResponse> {

	public final OutputPort<SsoUpdateEvent> broadcastPort = newOutputPort(SsoUpdateEvent.class);
	public final RequestOutputPort<UpdateSsoGroupRequest, UpdateSsoGroupResponse> updateGroupPort = newRequestOutputPort(UpdateSsoGroupRequest.class, UpdateSsoGroupResponse.class);

	public UpdateSsoUserProcessor() {
		super(UpdateSsoUserRequest.class, SsoState.class, UpdateSsoUserResponse.class);
	}

	@Override
	protected UpdateSsoUserResponse processRequest(RequestMessage<UpdateSsoUserRequest> action, SsoState state, ThreadScope threadScope) throws Exception {
		final SsoDbService dbservice = (SsoDbService) getServices().getService("DB");
		final UpdateSsoUserRequest request = action.getAction();
		//final long[] groups = action.getAction().getParentGroups();
		final SsoUser user = request.getSsoUser();
		if (user == null)
			throw new NullPointerException("user is null");
		String session = request.getSession();
		UpdateSsoUserResponse r = nw(UpdateSsoUserResponse.class);

		SsoUser existingUser = state.getUser(request.getSsoUserId());
		if (existingUser == null) {
			r.setMessage("user not found: " + request.getSsoUserId());
			r.setOk(false);
			broadcastSsoEvent(request, r, session, threadScope);
			return r;
		}

		if (request.getUserSuppliedPassword() != null) {
			if (OH.ne(SsoHelper.encode(request.getUserSuppliedPassword(), request.getEncodingAlgorithm(), existingUser.getEncodingAlgorithm()), existingUser.getPassword())) {
				r.setMessage("Password incorrect");
				r.setOk(false);
				broadcastSsoEvent(request, r, session, threadScope);
				return r;
			}
		}
		if (request.getUserSuppliedAnswer() != null) {
			if (OH.ne(SsoHelper.encode(request.getUserSuppliedAnswer(), request.getEncodingAlgorithm(), existingUser.getEncodingAlgorithm()), existingUser.getResetAnswer())) {
				r.setMessage("Reset Answer incorrect");
				r.setOk(false);
				broadcastSsoEvent(request, r, session, threadScope);
				return r;
			}
		}

		//are we removing?
		if (user.getRevision() == 65535) {
			Connection con = dbservice.getConnection();

			try {
				existingUser.setRevision(65535);
				dbservice.insertUser(existingUser, con);
				state.removeUser(existingUser);
			} finally {
				con.close();
			}
			SsoGroup group = state.getGroup(user.getGroupId());
			;
			UpdateSsoGroupRequest req = nw(UpdateSsoGroupRequest.class);
			req.setNamespace(request.getNamespace());
			req.setClientLocation(request.getClientLocation());
			req.setSession(request.getSession());
			req.setGroup(group);
			updateGroupPort.request(req, threadScope);
			r.setSsoUser(existingUser);
			r.setMessage("user removed: " + user.getId());
			r.setOk(true);
			broadcastSsoEvent(request, r, session, threadScope);
		} else {
			if (user.getEmail() != null && OH.ne(user.getEmail(), existingUser.getEmail())) {
				if (state.getUserByEmail(user.getEmail()) != null) {
					r.setMessage("email address exists ");
					r.setOk(false);
					broadcastSsoEvent(request, r, session, threadScope);
					return r;
				}
			}

			boolean groupNameNeedsUpdate = false;
			if (user.getUserName() != null && OH.ne(user.getUserName(), existingUser.getUserName())) {
				if (state.getUserByUserName(user.getUserName()) != null) {
					r.setMessage("username exists.");
					r.setOk(false);
					broadcastSsoEvent(request, r, session, threadScope);
					return r;
				}
				if (state.getGroupByTypeAndName(SsoGroup.GROUP_TYPE_USER, user.getUserName()) != null) {
					r.setMessage("group name exists.");
					r.setOk(false);
					broadcastSsoEvent(request, r, session, threadScope);
					return r;
				}
				groupNameNeedsUpdate = true;
			}

			// unless password, reset answer and encoding are supplied we need
			// to translate to existing encoding
			if (SH.isnt(user.getPassword()) || SH.isnt(user.getResetAnswer()) || user.getEncodingAlgorithm() == 0) {
				if (SH.is(user.getPassword()))
					user.setPassword(SsoHelper.encode(user.getPassword(), user.getEncodingAlgorithm(), existingUser.getEncodingAlgorithm()));
				if (SH.is(user.getResetAnswer()))
					user.setResetAnswer(SsoHelper.encode(user.getResetAnswer(), user.getEncodingAlgorithm(), existingUser.getEncodingAlgorithm()));
				user.setEncodingAlgorithm(existingUser.getEncodingAlgorithm());
			}

			// Update non-supplied values with the existing values (if its not
			// supplied, then it means it didn't change!)
			for (ValuedParam<SsoUser> param : VH.getSchema(user).askValuedParams()) {
				if (!user.askExists(param.getPid()))
					param.copy(existingUser, user);
			}
			StringBuilder sb = new StringBuilder();

			state.removeUser(existingUser);
			final Connection con = dbservice.getConnection();
			if (groupNameNeedsUpdate) {
				UpdateSsoGroupRequest req = nw(UpdateSsoGroupRequest.class);
				req.setNamespace(request.getNamespace());
				req.setSession(request.getSession());
				SsoGroup group2 = state.getGroup(user.getGroupId()).clone();
				group2.setName(user.getUserName());
				req.setGroup(group2);
				updateGroupPort.request(req, threadScope);
			}
			try {
				user.setRevision(existingUser.getRevision() + 1);
				state.addUser(user);
				dbservice.insertUser(user, con);
				r.setSsoUser(user);
				r.setOk(true);
			} catch (RuntimeException e) {
				// roll back to original user
				state.removeUser(user);
				state.addUser(existingUser);
				throw e;
			} finally {
				IOH.close(con);
			}
		}
		broadcastSsoEvent(request, r, session, threadScope);
		return r;
	}
	private void broadcastSsoEvent(UpdateSsoUserRequest request, UpdateSsoUserResponse r, String session, ThreadScope threadScope) {
		SsoUpdateEvent event = nw(SsoUpdateEvent.class);
		event.setUsers(CH.l(r.getSsoUser()));
		if (r.getSsoUser() != null)
			event.setUsers(CH.l(r.getSsoUser()));
		event.setOk(r.getOk());
		event.setMessage(r.getMessage());
		event.setType(SsoUpdateEvent.USER_UPDATE);
		event.setSession(session);
		event.setNow(this.getTools().getNow());
		if (r.getSsoUser() != null)
			event.setName(r.getSsoUser().getUserName());
		event.setNamespace(request.getNamespace());
		event.setClientLocation(request.getClientLocation());
		event.setMemberId(r.getSsoUser() == null ? -1 : r.getSsoUser().getId());
		broadcastPort.send(event, threadScope);
	}
}
