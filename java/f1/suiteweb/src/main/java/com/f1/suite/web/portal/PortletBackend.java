package com.f1.suite.web.portal;

import com.f1.base.Action;

public interface PortletBackend {

	public void sendMessageToBackend(String backendServiceId, String partitionId, Action m);

	public void sendRequestToBackend(String backendServiceId, String partitionid, Object correlationId, Action m);

	public void subscribe(String partitionId);

	public void unsubscribe(String partitionId);

	public void sendMessageToPortletManager(String partitionId, Action nw, long delayMs);

}
