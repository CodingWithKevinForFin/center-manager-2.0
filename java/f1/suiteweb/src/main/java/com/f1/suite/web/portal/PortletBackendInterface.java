package com.f1.suite.web.portal;

import com.f1.base.Action;
import com.f1.base.Message;
import com.f1.container.ResultMessage;

public interface PortletBackendInterface {

	public void onBackendAction(Action action);
	public void onBackendResponse(ResultMessage<Action> action, Object correlationId);
	public void sendRequestToBackend(String backendServiceId, Message nw);
	public void sendRequestToBackend(String backendServiceId, BackendResponseListener listener, Message nw);
	public void sendRequestToBackend(String backendServiceId, String portletId, Message nw);
	public void sendMessageToBackend(String backendServiceId, Message nw);
}
