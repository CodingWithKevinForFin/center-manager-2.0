package com.sso.messages;

import java.util.List;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.SS.CG")
public interface CreateSsoGroupResponse extends SsoResponse {

	
	byte PID_GROUP=3;
	byte PID_GROUP_ATTRIBUTES=4;
	byte PID_GROUP_MEMBERS=5;
	
	@PID(PID_GROUP)
	public SsoGroup getGroup();
	public void setGroup(SsoGroup group);

	@PID(PID_GROUP_ATTRIBUTES)
	public List<SsoGroupAttribute> getGroupAttributes();
	public void setGroupAttributes(List<SsoGroupAttribute> attributes);

	@PID(PID_GROUP_MEMBERS)
	public List<SsoGroupMember> getGroupMembers();
	public void setGroupMembers(List<SsoGroupMember> groupMembers);
}
