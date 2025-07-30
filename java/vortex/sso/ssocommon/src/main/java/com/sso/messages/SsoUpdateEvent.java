package com.sso.messages;

import java.util.List;

import com.f1.base.Lockable;
import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.SS.UE")
public interface SsoUpdateEvent extends SsoResponse, Lockable {

	final static byte USER_LOGIN = 1;
	final static byte USER_CREATE = 2;
	final static byte USER_UPDATE = 4;
	final static byte USER_RESET = 8;
	final static byte GROUP_CREATE = 16;
	final static byte GROUP_UPDATE = 32;
	static final byte USER_EMAIL = 64;

	byte PID_ID = 0;
	byte PID_TYPE = 4;
	byte PID_SESSION = 5;
	byte PID_NAME = 6;
	byte PID_NOW = 7;
	byte PID_NAMESPACE = 8;
	byte PID_MEMBER_ID = 9;
	byte PID_GROUPS = 10;
	byte PID_GROUP_ATTRIBUTES = 11;
	byte PID_USERS = 12;
	byte PID_GROUP_MEMBERS = 14;
	byte PID_REMOVED_GROUPS = 15;
	byte PID_REMOVED_USERS = 16;
	byte PID_REMOVED_GROUP_MEMBERS = 17;
	static final byte PID_CLIENT_LOCATION = 18;

	@PID(PID_ID)
	public long getId();
	public void setId(long id);

	@PID(PID_TYPE)
	public byte getType();
	public void setType(byte type);

	@PID(PID_SESSION)
	public String getSession();
	public void setSession(String session);

	@PID(PID_NAME)
	public String getName();
	public void setName(String name);

	@PID(PID_NOW)
	public long getNow();
	public void setNow(long now);

	@PID(PID_NAMESPACE)
	public String getNamespace();
	public void setNamespace(String namespace);

	@PID(PID_MEMBER_ID)
	public long getMemberId();
	public void setMemberId(long memberId);

	@PID(PID_GROUPS)
	public void setGroups(List<SsoGroup> groups);
	public List<SsoGroup> getGroups();

	@PID(PID_GROUP_ATTRIBUTES)
	public void setGroupAttributes(List<SsoGroupAttribute> groupsAttributes);
	public List<SsoGroupAttribute> getGroupAttributes();

	@PID(PID_USERS)
	public void setUsers(List<SsoUser> users);
	public List<SsoUser> getUsers();

	@PID(PID_GROUP_MEMBERS)
	public List<SsoGroupMember> getGroupMembers();
	public void setGroupMembers(List<SsoGroupMember> members);

	@PID(PID_REMOVED_GROUPS)
	public long[] getRemovedGroups();
	public void setRemovedGroups(long[] groupIds);

	@PID(PID_REMOVED_USERS)
	public long[] getRemovedUsers();
	public void setRemovedUsers(long[] userIds);

	@PID(PID_REMOVED_GROUP_MEMBERS)
	public long[] getRemovedGroupMembers();
	public void setRemovedGroupMembers(long[] groupMembers);

	@PID(PID_CLIENT_LOCATION)
	public void setClientLocation(String clientLocation);
	public String getClientLocation();

}
