package com.f1.suite.web.portal;

import java.util.Map;
import java.util.Set;

import com.f1.base.Action;
import com.f1.container.ResultMessage;
import com.f1.suite.web.HttpRequestAction;

public interface PortletService {

	public String getServiceId();

	public void onBackendAction(Action action);

	public void onBackendResponse(ResultMessage<Action> action);

	public Set<Class<? extends Action>> getInterestedBackendMessages();

	public void close();

	public void handleCallback(Map<String, String> attributes, HttpRequestAction action);

}
