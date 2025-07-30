package com.sso.messages;

import java.util.List;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.SS.GU")
public interface UpdateSsoGroupRequest extends SsoRequest {

	
	byte PID_GROUP_ID=2;
	byte PID_GROUP=3;
	byte PID_GROUP_ATTRIBUTES=4;
	
	@PID(PID_GROUP_ID)
	public long getGroupId();
	public void setGroupId(long ssoGroupId);

	@PID(PID_GROUP)
	public SsoGroup getGroup();
	public void setGroup(SsoGroup group);

	@PID(PID_GROUP_ATTRIBUTES)
	public List<SsoGroupAttribute> getGroupAttributes();
	public void setGroupAttributes(List<SsoGroupAttribute> ssoUserAttributes);
}
