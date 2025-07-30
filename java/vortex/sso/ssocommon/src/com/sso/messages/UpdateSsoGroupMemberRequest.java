package com.sso.messages;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.SS.MQ")
public interface UpdateSsoGroupMemberRequest extends SsoRequest {

	byte PID_SSO_GROUP_MEMBER = 2;
	@PID(PID_SSO_GROUP_MEMBER)
	public SsoGroupMember getSsoGroupMember();
	public void setSsoGroupMember(SsoGroupMember groupMember);

}
