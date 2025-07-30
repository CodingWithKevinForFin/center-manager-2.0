package com.vortex.sso;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import com.f1.container.RequestMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicRequestProcessor;
import com.f1.utils.SH;
import com.f1.utils.TextMatcher;
import com.sso.messages.QuerySsoUserRequest;
import com.sso.messages.QuerySsoUserResponse;
import com.sso.messages.SsoGroupAttribute;
import com.sso.messages.SsoUser;

public class QuerySsoUserProcessor extends BasicRequestProcessor<QuerySsoUserRequest, SsoState, QuerySsoUserResponse> {

	public QuerySsoUserProcessor() {
		super(QuerySsoUserRequest.class, SsoState.class, QuerySsoUserResponse.class);
	}

	@Override
	protected QuerySsoUserResponse processRequest(RequestMessage<QuerySsoUserRequest> action, SsoState state, ThreadScope threadScope) throws Exception {
		final QuerySsoUserRequest request = action.getAction();
		final QuerySsoUserResponse response = nw(QuerySsoUserResponse.class);
		final List<SsoUser> users = new ArrayList<SsoUser>();

		final String se = request.getSearchExpression();
		final byte sf = request.getStatusFilter();
		switch (request.getSearchField()) {
			case QuerySsoUserRequest.FIELD_ID:
				addFilterByEnabled(users, sf, state.getUser(Long.parseLong(se)));
				break;
			case QuerySsoUserRequest.FIELD_EMAIL:
				if (request.getIsPattern()) {
					TextMatcher m = SH.m(request.getSearchExpression());
					for (SsoUser user : state.getUsers())
						if (user.getEmail() != null && m.matches(user.getEmail()))
							addFilterByEnabled(users, sf, user);
				} else
					addFilterByEnabled(users, sf, state.getUserByEmail(se));
				break;
			case QuerySsoUserRequest.FIELD_USER_NAME:
				if (request.getIsPattern()) {
					TextMatcher m = SH.m(request.getSearchExpression());
					for (SsoUser user : state.getUsers())
						if (user.getUserName() != null && m.matches(user.getUserName()))
							addFilterByEnabled(users, sf, user);
				} else
					addFilterByEnabled(users, sf, state.getUserByUserName(se));
		}
		for (int i = 0; i < users.size(); i++)
			users.set(i, users.get(i));
		response.setUsers(users);
		final List<SsoGroupAttribute> attributes = new ArrayList<SsoGroupAttribute>();
		for (SsoUser user : users)
			for (SsoGroupAttribute ga : state.getGroupAttributes(user.getGroupId()))
				attributes.add(ga);
		response.setAttributes(attributes);
		response.setOk(true);
		return response;
	}
	private void addFilterByEnabled(List<SsoUser> users, byte sf, SsoUser ssoUser) {
		if (ssoUser == null)
			return;
		switch (sf) {
			case QuerySsoUserRequest.STATUS_ALL:
				break;
			case QuerySsoUserRequest.STATUS_ONLY_DISABLED:
				if (ssoUser.getStatus() == SsoUser.STATUS_ENABLED)
					return;
				break;
			case QuerySsoUserRequest.STATUS_ONLY_ENABLED:
				if (ssoUser.getStatus() != SsoUser.STATUS_ENABLED)
					return;
				break;
			default:
				throw new NoSuchElementException("Unknown status type:" + sf);
		}
		users.add(ssoUser);
	}
}
