package com.vortex.sso;

import java.sql.Connection;

import com.f1.container.OutputPort;
import com.f1.container.RequestMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicRequestProcessor;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.sso.messages.SsoGroupMember;
import com.sso.messages.SsoUpdateEvent;
import com.sso.messages.UpdateSsoGroupMemberRequest;
import com.sso.messages.UpdateSsoGroupMemberResponse;

public class UpdateSsoGroupMemberProcessor extends BasicRequestProcessor<UpdateSsoGroupMemberRequest, SsoState, UpdateSsoGroupMemberResponse> {

	public final OutputPort<SsoUpdateEvent> broadcastPort = newOutputPort(SsoUpdateEvent.class);

	public UpdateSsoGroupMemberProcessor() {
		super(UpdateSsoGroupMemberRequest.class, SsoState.class, UpdateSsoGroupMemberResponse.class);
	}

	@Override
	protected UpdateSsoGroupMemberResponse processRequest(RequestMessage<UpdateSsoGroupMemberRequest> action, SsoState state, ThreadScope threadScope) throws Exception {
		final SsoDbService dbservice = (SsoDbService) getServices().getService("DB");
		final UpdateSsoGroupMemberRequest request = action.getAction();
		SsoGroupMember member = request.getSsoGroupMember();
		UpdateSsoGroupMemberResponse r = nw(UpdateSsoGroupMemberResponse.class);
		r.setOk(false);
		if (state.getGroup(member.getGroupId()) == null) {
			r.setMessage("parent group not found: " + member.getGroupId());
		} else if (state.getGroup(member.getMemberId()) == null) {
			r.setMessage("member group not found: " + member.getMemberId());
		} else if (member.getRevision() == 65535) {//delete
			final SsoGroupMember existing = state.getGroupMember(member.getId());
			if (existing == null) {
				r.setMessage("can not find group for delete: " + member.getId());
			} else {
				Connection con = dbservice.getConnection();
				try {
					SsoGroupMember existing2 = existing.clone();
					existing2.setRevision(65535);
					dbservice.insertGroupMember(existing2, con);
					state.removeGroupMember(existing);
				} finally {
					IOH.close(con);
				}
				r.setOk(true);
			}
		} else if (state.isGroupMemberCircRef(member)) {
			r.setMessage("This link would cause a circular reference");
		} else if (member.getId() == 0) {//insert
			Connection con = dbservice.getConnection();
			try {
				dbservice.insertGroupMember(member, con);

				state.addGroupMember(member);
			} finally {
				IOH.close(con);
			}
			r.setOk(true);
		} else {
			r.setMessage("can not update groups");
		}
		r.setSsoGroupMember(member);
		broadcastSsoEvent(request, r, request.getSession(), threadScope);
		return r;
	}
	private void broadcastSsoEvent(UpdateSsoGroupMemberRequest request, UpdateSsoGroupMemberResponse r, String session, ThreadScope threadScope) {
		SsoUpdateEvent event = nw(SsoUpdateEvent.class);
		if (r.getSsoGroupMember() != null)
			event.setGroupMembers(CH.l(r.getSsoGroupMember()));
		event.setOk(r.getOk());
		event.setMessage(r.getMessage());
		event.setType(SsoUpdateEvent.USER_UPDATE);
		event.setSession(session);
		event.setNow(this.getTools().getNow());
		//event.setName(r.getSsoUser().getUserName());
		event.setNamespace(request.getNamespace());
		event.setClientLocation(request.getClientLocation());
		//event.setMemberId(r.getSsoUser() == null ? -1 : r.getSsoUser().getId());
		broadcastPort.send(event, threadScope);
	}
}
