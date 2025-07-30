package com.sso.messages;

import java.util.List;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.SS.GQ")
public interface CreateSsoGroupRequest extends SsoRequest {

	
	byte PID_GROUP=2;
	byte PID_PARENT_GROUPS=3;
	byte PID_CHILD_GROUPS=5;
	byte PID_GROUP_ATTRIBUTES=6;
	
	@PID(PID_GROUP)
	public SsoGroup getGroup();
	public void setGroup(SsoGroup group);

	@PID(PID_PARENT_GROUPS)
	public long[] getParentGroups();
	public void setParentGroups(long[] groups);

	@PID(PID_CHILD_GROUPS)
	public long[] getChildGroups();
	public void setChildGroups(long[] groups);

	@PID(PID_GROUP_ATTRIBUTES)
	public List<SsoGroupAttribute> getGroupAttributes();
	public void setGroupAttributes(List<SsoGroupAttribute> groupAttributes);
}
