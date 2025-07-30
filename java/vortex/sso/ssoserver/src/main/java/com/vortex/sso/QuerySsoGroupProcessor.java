package com.vortex.sso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.f1.container.RequestMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicRequestProcessor;
import com.f1.utils.CH;
import com.sso.messages.QuerySsoGroupRequest;
import com.sso.messages.QuerySsoGroupResponse;
import com.sso.messages.SsoGroup;
import com.sso.messages.SsoGroupAttribute;
import com.sso.messages.SsoUpdateEvent;
import com.sso.messages.SsoUser;

public class QuerySsoGroupProcessor extends BasicRequestProcessor<QuerySsoGroupRequest, SsoState, QuerySsoGroupResponse> {

	private DataSource dbsource;

	public QuerySsoGroupProcessor(DataSource dbsource) {
		super(QuerySsoGroupRequest.class, SsoState.class, QuerySsoGroupResponse.class);
		this.dbsource = dbsource;
		// TODO Auto-generated constructor stub
	}

	public void init() {
		super.init();
	}

	@Override
	protected QuerySsoGroupResponse processRequest(RequestMessage<QuerySsoGroupRequest> action, SsoState state, ThreadScope threadScope) throws Exception {
		final QuerySsoGroupResponse response = nw(QuerySsoGroupResponse.class);

		final List<SsoGroup> groups = new ArrayList<SsoGroup>(state.getGroupsCount());
		for (SsoGroup group : state.getGroups())
			groups.add(group);
		final List<SsoGroupAttribute> groupAttributes = new ArrayList<SsoGroupAttribute>(state.getGroupAttributesCount());
		for (SsoGroupAttribute groupAttribute : state.getGroupAttributes())
			groupAttributes.add(groupAttribute);

		final List<SsoUser> users = new ArrayList<SsoUser>(state.getUsersCount());
		for (SsoUser user : state.getUsers())
			users.add(user);

		response.setEvents(new ArrayList<SsoUpdateEvent>(state.getEvents()));
		response.setGroups(groups);
		response.setUsers(users);
		response.setGroupMembers(CH.l(state.getGroupMembers()));
		response.setGroupAttributes(groupAttributes);

		Map<Short, String> grouptypes = new HashMap<Short, String>();
		grouptypes.put(SsoGroup.GROUP_TYPE_REGION, "Region");
		grouptypes.put(SsoGroup.GROUP_TYPE_PROCESS, "Process");
		grouptypes.put(SsoGroup.GROUP_TYPE_HOST, "Host");
		grouptypes.put(SsoGroup.GROUP_TYPE_ENVIRONMENT, "Environment");
		grouptypes.put(SsoGroup.GROUP_TYPE_DEPLOYMENT, "Deployment");
		grouptypes.put(SsoGroup.GROUP_TYPE_ACCOUNT, "Account");
		grouptypes.put(SsoGroup.GROUP_TYPE_USER, "User");
		response.setGroupTypes(grouptypes);

		response.setOk(true);
		return response;
	}
}
