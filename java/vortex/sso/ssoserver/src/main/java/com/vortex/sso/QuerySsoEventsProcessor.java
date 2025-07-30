package com.vortex.sso;

import java.util.List;

import com.f1.container.RequestMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicRequestProcessor;
import com.f1.utils.CH;
import com.sso.messages.QuerySsoEventsRequest;
import com.sso.messages.QuerySsoEventsResponse;
import com.sso.messages.SsoUpdateEvent;

public class QuerySsoEventsProcessor extends BasicRequestProcessor<QuerySsoEventsRequest, SsoState, QuerySsoEventsResponse> {

	public QuerySsoEventsProcessor() {
		super(QuerySsoEventsRequest.class, SsoState.class, QuerySsoEventsResponse.class);
	}

	@Override
	protected QuerySsoEventsResponse processRequest(RequestMessage<QuerySsoEventsRequest> action, SsoState state, ThreadScope threadScope) throws Exception {
		List<SsoUpdateEvent> events = state.getEvents();
		QuerySsoEventsResponse r = nw(QuerySsoEventsResponse.class);
		r.setEvents(CH.l(events));
		r.setOk(true);
		return r;
	}
}
