package com.sso.messages;

import java.util.Collection;
import java.util.Map;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.SS.QG")
public interface QuerySsoGroupResponse extends SsoResponse {

	
	byte PID_GROUPS=3;
	byte PID_EVENTS=4;
	byte PID_GROUP_MEMBERS=5;
	byte PID_USERS=7;
	byte PID_GROUP_ATTRIBUTES=8;
	byte PID_GROUP_TYPES=9;
	
	@PID(PID_GROUPS)
	public Collection<SsoGroup> getGroups();
	public void setGroups(Collection<SsoGroup> groups);

	@PID(PID_EVENTS)
	public Collection<SsoUpdateEvent> getEvents();
	public void setEvents(Collection<SsoUpdateEvent> events);

	@PID(PID_GROUP_MEMBERS)
	public Collection<SsoGroupMember> getGroupMembers();
	public void setGroupMembers(Collection<SsoGroupMember> parentChildren);

	@PID(PID_USERS)
	public void setUsers(Collection<SsoUser> users);
	public Collection<SsoUser> getUsers();

	@PID(PID_GROUP_ATTRIBUTES)
	public Collection<SsoGroupAttribute> getGroupAttributes();
	public void setGroupAttributes(Collection<SsoGroupAttribute> attributes);

	@PID(PID_GROUP_TYPES)
	public void setGroupTypes(Map<Short, String> types);
	public Map<Short, String> getGroupTypes();
}
