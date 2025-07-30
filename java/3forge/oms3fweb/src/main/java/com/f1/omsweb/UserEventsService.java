package com.f1.omsweb;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.base.Action;
import com.f1.base.Message;
import com.f1.container.ResultMessage;
import com.f1.povo.standard.BatchMessage;
import com.f1.povo.standard.TimestampedMessage;
import com.f1.suite.web.HttpRequestAction;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.PortletService;
import com.f1.utils.CH;

public class UserEventsService implements PortletService {

	private Map<Message, String> requestToPortletId = new IdentityHashMap<Message, String>();
	public static final String ID = "USER_EVENTS";
	private Map<String, UserEventsTablePortlet> userEventsTable = new HashMap<String, UserEventsTablePortlet>();
	private PortletManager manager;

	@Override
	public String getServiceId() {
		return ID;
	}
	public UserEventsService(PortletManager manager) {
		this.manager = manager;
	}

	@Override
	public void onBackendAction(Action action) {
		TimestampedMessage tsm = (TimestampedMessage) action;
		for (UserEventsTablePortlet t : userEventsTable.values()) {
			t.onTimestampedMessage(tsm);
		}
	}

	@Override
	public void onBackendResponse(ResultMessage<Action> action) {
		String portletId = requestToPortletId.remove(action.getRequestMessage().getAction());
		UserEventsTablePortlet portlet = userEventsTable.get(portletId);
		if (portlet != null) {
			BatchMessage bm = (BatchMessage) action.getAction();
			List<TimestampedMessage> messages = (List) bm.getMessages();
			portlet.addMessages(messages);
		}
	}

	@Override
	public Set<Class<? extends Action>> getInterestedBackendMessages() {
		return (Set) CH.s(TimestampedMessage.class, BatchMessage.class);
	}

	public void addUserEventsTable(UserEventsTablePortlet userEventsTablePortlet) {
		CH.putOrThrow(userEventsTable, userEventsTablePortlet.getPortletId(), userEventsTablePortlet);
		Message msg = manager.getGenerator().nw(Message.class);
		requestToPortletId.put(msg, userEventsTablePortlet.getPortletId());
		manager.sendRequestToBackend("AUDIT", msg);
	}
	public void removePortlet(UserEventsTablePortlet portlet) {
		CH.removeOrThrow(this.userEventsTable, portlet.getPortletId());

	}
	@Override
	public void close() {
		// TODO Auto-generated method stub

	}
	@Override
	public void handleCallback(Map<String, String> attributes, HttpRequestAction action) {
		// TODO Auto-generated method stub

	}

}
