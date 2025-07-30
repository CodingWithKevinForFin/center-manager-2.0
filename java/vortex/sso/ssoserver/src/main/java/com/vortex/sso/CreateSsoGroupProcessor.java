package com.vortex.sso;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.f1.container.OutputPort;
import com.f1.container.RequestMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicRequestProcessor;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.sso.messages.CreateSsoGroupRequest;
import com.sso.messages.CreateSsoGroupResponse;
import com.sso.messages.SsoGroup;
import com.sso.messages.SsoGroupAttribute;
import com.sso.messages.SsoGroupMember;
import com.sso.messages.SsoRequest;
import com.sso.messages.SsoUpdateEvent;

public class CreateSsoGroupProcessor extends BasicRequestProcessor<CreateSsoGroupRequest, SsoState, CreateSsoGroupResponse> {

	public final OutputPort<SsoUpdateEvent> broadcastPort = newOutputPort(SsoUpdateEvent.class);

	public CreateSsoGroupProcessor() {
		super(CreateSsoGroupRequest.class, SsoState.class, CreateSsoGroupResponse.class);
	}

	@Override
	protected CreateSsoGroupResponse processRequest(RequestMessage<CreateSsoGroupRequest> action, SsoState state, ThreadScope threadScope) throws Exception {
		CreateSsoGroupRequest request = action.getAction();
		SsoGroup group = request.getGroup();
		final SsoDbService dbservice = (SsoDbService) getServices().getService("DB");
		final CreateSsoGroupResponse r = nw(CreateSsoGroupResponse.class);
		r.setOk(false);
		try {
			if (state.getGroupByTypeAndName(group.getType(), group.getName()) != null) {
				r.setMessage("group already exists.");
			} else {
				StringBuilder errors = new StringBuilder();
				validate(request.getGroup(), request.getGroupAttributes(), errors);
				if (errors.length() > 0) {
					r.setMessage(errors.toString());
				} else {
					Connection con = dbservice.getConnection();
					try {
						createGroup(request, state, dbservice, con, r);
					} finally {
						IOH.close(con);
					}
					r.setMessage(null);
					r.setOk(true);
				}
			}
		} catch (Exception e) {
			LH.warning(log, "Error creating group: ", group, e);
			r.setOk(false);
			r.setMessage("internal error");
		}
		broadcastSsoEvent(group, r.getGroupAttributes(), r.getGroupMembers(), r.getMessage(), request.getSession(), r.getOk(), group.getName(), threadScope, request);
		return r;
	}

	public static void validate(SsoGroup group, List<SsoGroupAttribute> groupAttributes, StringBuilder errors) {
		if (SH.isnt(group.getName()))
			errors.append("Group must have a name ");
		Set<String> existing = new HashSet<String>();
		for (SsoGroupAttribute attribute : CH.i(groupAttributes)) {
			if (attribute.getType() == 0)
				errors.append("invalid type for " + attribute.getKey() + ": 0");
			if (SH.isnt(attribute.getKey()))
				errors.append("key required for all attributes. ");
			if (!existing.add(attribute.getKey()))
				errors.append("Duplicate attribute: " + attribute.getKey() + ". ");
		}
	}

	private void broadcastSsoEvent(SsoGroup responseGroup, List<SsoGroupAttribute> attributes, List<SsoGroupMember> members, String message, String session, boolean okay,
			String name, ThreadScope threadScope, SsoRequest request) {
		SsoUpdateEvent event = nw(SsoUpdateEvent.class);
		event.setMessage(message);
		event.setOk(okay);
		event.setSession(session);
		event.setType(SsoUpdateEvent.GROUP_CREATE);
		event.setNow(this.getTools().getNow());
		event.setGroups(CH.l(responseGroup));
		event.setName(name);
		event.setNamespace(request.getNamespace());
		event.setClientLocation(request.getClientLocation());
		event.setGroupAttributes(attributes);
		event.setGroupMembers(members);
		broadcastPort.send(event, threadScope);
	}

	public static void createGroup(CreateSsoGroupRequest request, SsoState state, SsoDbService dbservice, Connection con, CreateSsoGroupResponse r) throws Exception {

		SsoGroup group = request.getGroup();
		ArrayList<SsoGroupMember> members = new ArrayList<SsoGroupMember>();

		// database
		dbservice.insertGroup(group, con);
		for (long g : AH.i(request.getParentGroups())) {
			SsoGroupMember gm = dbservice.nw(SsoGroupMember.class);
			gm.setGroupId(g);
			gm.setMemberId(group.getId());
			dbservice.insertGroupMember(gm, con);
			members.add(gm);
		}
		for (long g : AH.i(request.getChildGroups())) {
			SsoGroupMember gm = dbservice.nw(SsoGroupMember.class);
			gm.setMemberId(g);
			gm.setGroupId(group.getId());
			dbservice.insertGroupMember(gm, con);
			members.add(gm);
		}
		ArrayList<SsoGroupAttribute> attributes = null;
		if (request.getGroupAttributes() != null) {
			attributes = new ArrayList<SsoGroupAttribute>(request.getGroupAttributes().size());
			for (SsoGroupAttribute e : request.getGroupAttributes()) {
				e.setGroupId(group.getId());
				dbservice.insertGroupAttribute(e, con);
				attributes.add(e);
			}
		}

		//In-memory State
		state.addGroup(group);
		for (SsoGroupMember ga : members)
			state.addGroupMember(ga);
		for (SsoGroupAttribute ga : CH.i(attributes))
			state.addGroupAttribute(ga);

		r.setGroup(group);
		r.setGroupAttributes(request.getGroupAttributes());
		r.setGroupMembers(members);
	}

}
