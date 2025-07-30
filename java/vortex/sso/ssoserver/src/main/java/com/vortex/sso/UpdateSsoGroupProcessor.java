package com.vortex.sso;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.f1.base.ValuedParam;
import com.f1.container.OutputPort;
import com.f1.container.RequestMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicRequestProcessor;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.VH;
import com.sso.messages.SsoGroup;
import com.sso.messages.SsoGroupAttribute;
import com.sso.messages.SsoGroupMember;
import com.sso.messages.SsoUpdateEvent;
import com.sso.messages.SsoUser;
import com.sso.messages.UpdateSsoGroupRequest;
import com.sso.messages.UpdateSsoGroupResponse;

public class UpdateSsoGroupProcessor extends BasicRequestProcessor<UpdateSsoGroupRequest, SsoState, UpdateSsoGroupResponse> {

	public final OutputPort<SsoUpdateEvent> broadcastPort = newOutputPort(SsoUpdateEvent.class);

	public UpdateSsoGroupProcessor() {
		super(UpdateSsoGroupRequest.class, SsoState.class, UpdateSsoGroupResponse.class);
	}

	@Override
	protected UpdateSsoGroupResponse processRequest(RequestMessage<UpdateSsoGroupRequest> action, SsoState state, ThreadScope threadScope) throws Exception {
		final SsoDbService dbservice = (SsoDbService) getServices().getService("DB");
		final UpdateSsoGroupRequest request = action.getAction();
		final SsoGroup group = request.getGroup();
		final long groupId = request.getGroupId();
		String userName = null;
		List<SsoGroupAttribute> attributes = request.getGroupAttributes();
		String session = request.getSession();
		UpdateSsoGroupResponse r = nw(UpdateSsoGroupResponse.class);
		//r.setGroupId(groupId);
		if (group != null) {
			SsoUser user = state.getUserByGroupId(group.getId());
			if (user != null)
				userName = user.getUserName();

			SsoGroup existingGroup = state.getGroup(request.getGroupId());
			if (existingGroup == null) {
				r.setMessage("group not found: " + request.getGroupId());
				r.setOk(false);
				broadcastSsoEvent(request, r, session, threadScope, null);
				return r;
			}

			//handle delete
			if (group.getRevision() == 65535) {
				Connection con = dbservice.getConnection();
				SsoGroup existingGroup2 = existingGroup.clone();
				try {
					existingGroup2.setRevision(65535);
					dbservice.insertGroup(existingGroup2, con);
					state.removeGroup(existingGroup);

					//parent relationships
					for (SsoGroupMember groupMember : CH.l(state.getGroupMembersByMemberId(group.getId()))) {
						SsoGroupMember groupMember2 = groupMember.clone();
						groupMember2.setRevision(65535);
						dbservice.insertGroupMember(groupMember2, con);
						state.removeGroupMember(groupMember);
					}
					//child relationships
					for (SsoGroupMember groupMember : CH.l(state.getGroupMembersByGroupId(group.getId()))) {
						SsoGroupMember groupMember2 = groupMember.clone();
						groupMember2.setRevision(65535);
						dbservice.insertGroupMember(groupMember2, con);
						state.removeGroupMember(groupMember);
					}
					//attributes
					for (SsoGroupAttribute attribute : CH.l(state.getGroupAttributes(group.getId()))) {
						SsoGroupAttribute attribute2 = attribute.clone();
						attribute2.setRevision(65535);
						dbservice.insertGroupAttribute(attribute2, con);
						state.removeGroupAttribute(attribute);
					}

					//specializations
					if (group.getType() == SsoGroup.GROUP_TYPE_USER) {
						user = state.getUserByGroupId(group.getId());
						if (user == null)
							LH.warning(log, "user not found for group: ", group);
						else {
							final SsoUser user2 = user.clone();
							user2.setRevision(65535);
							dbservice.insertUser(user2, con);
							state.removeUser(user);
							userName = user.getUserName();
						}
					}
				} finally {
					con.close();
				}
				r.setSsoGroup(existingGroup2);
				r.setMessage("group removed: " + existingGroup.getId());
				r.setOk(true);
				broadcastSsoEvent(request, r, session, threadScope, userName);
				return r;
			}
			if (OH.ne(group.getName(), existingGroup.getName()) || OH.ne(group.getType(), existingGroup.getType())) {
				if (OH.ne(group.getName(), existingGroup.getName()) && group.getType() != existingGroup.getType()) {
					if (state.getGroupByTypeAndName(group.getType(), group.getName()) != null) {
						r.setMessage("groupname exists ");
						r.setOk(false);
						broadcastSsoEvent(request, r, session, threadScope, userName);
						return r;
					}
				}

				StringBuilder sb = new StringBuilder();
				CreateSsoGroupProcessor.validate(group, Collections.EMPTY_LIST, sb);
				if (sb.length() > 0) {
					r.setSsoGroup(null);
					r.setMessage(sb.toString());
					r.setOk(false);
					broadcastSsoEvent(request, r, session, threadScope, userName);
					return r;
				}

				// Update non-supplied values with the existing values (if its not
				// supplied, then it means it didn't change!)
				state.removeGroup(existingGroup);
				for (ValuedParam<SsoGroup> param : VH.getSchema(group).askValuedParams()) {
					if (!group.askExists(param.getPid()))
						param.copy(existingGroup, group);
				}
				group.setRevision(existingGroup.getRevision() + 1);
				state.addGroup(group);
				Connection con = dbservice.getConnection();
				try {
					dbservice.insertGroup(group, con);
				} catch (RuntimeException e) {
					// roll back to original user
					state.removeGroup(group);
					state.addGroup(existingGroup);
					throw e;
				} finally {
					IOH.close(con);
				}
			}
			r.setOk(true);
		}
		if (CH.isntEmpty(attributes)) {
			if (userName == null) {
				SsoUser user = state.getUserByGroupId(attributes.get(0).getGroupId());
				if (user != null)
					userName = user.getUserName();
			}
			r.setOk(false);
			Connection con = dbservice.getConnection();
			List<SsoGroupAttribute> rAttributes = new ArrayList<SsoGroupAttribute>();
			try {
				for (SsoGroupAttribute att : attributes) {
					att.setGroupId(groupId);
					SsoGroupAttribute existing = state.getGroupAttribute(groupId, att.getKey());
					if (existing == null) {
						if (att.getRevision() != 65535) {
							dbservice.insertGroupAttribute(att, con);
							state.addGroupAttribute(att);
							rAttributes.add(att);
						}
					} else {
						//delete
						if (att.getRevision() == 65535) {
							state.removeGroupAttribute(existing);
							SsoGroupAttribute existing2 = existing.clone();
							existing2.setRevision(65535);
							dbservice.insertGroupAttribute(existing2, con);
							rAttributes.add(existing2);
						} else if (OH.ne(att.getValue(), existing.getValue()) || OH.ne(att.getType(), existing.getType())) {//update if different
							state.removeGroupAttribute(existing);
							SsoGroupAttribute existing2 = existing.clone();
							existing2.setValue(att.getValue());
							existing2.setType(att.getType());
							existing2.setRevision(existing.getRevision() + 1);
							dbservice.insertGroupAttribute(existing2, con);
							state.addGroupAttribute(existing2);
							rAttributes.add(existing2);
						} else {//no change
							rAttributes.add(existing);
						}
					}
				}
				r.setGroupAttributes(rAttributes);
			} finally {
				IOH.close(con);
			}
			r.setOk(true);
		}

		r.setSsoGroup(group);
		broadcastSsoEvent(request, r, session, threadScope, userName);
		return r;
	}
	private void broadcastSsoEvent(UpdateSsoGroupRequest req, UpdateSsoGroupResponse r, String session, ThreadScope threadScope, String userName) {
		SsoUpdateEvent event = nw(SsoUpdateEvent.class);
		event.setOk(r.getOk());
		event.setNamespace(req.getNamespace());
		event.setClientLocation(req.getClientLocation());
		event.setName(userName);
		event.setMessage(r.getMessage());
		event.setType(SsoUpdateEvent.GROUP_UPDATE);
		if (r.getSsoGroup() != null) {
			event.setGroups(CH.l(r.getSsoGroup()));
		}
		event.setNow(getTools().getNow());
		event.setSession(session);
		event.setGroupAttributes(r.getGroupAttributes());
		broadcastPort.send(event, threadScope);
	}
}
