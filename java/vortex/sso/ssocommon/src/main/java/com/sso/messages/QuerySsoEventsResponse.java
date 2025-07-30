package com.sso.messages;

import java.util.List;

import com.f1.base.PID;

public interface QuerySsoEventsResponse extends SsoResponse {

	byte PID_EVENTS = 4;

	@PID(PID_EVENTS)
	public List<SsoUpdateEvent> getEvents();
	public void setEvents(List<SsoUpdateEvent> events);
}
