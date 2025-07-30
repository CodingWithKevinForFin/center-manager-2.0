package com.sso.messages;

import java.util.List;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.SS.UG")
public interface UpdateSsoGroupResponse extends SsoResponse {

	byte PID_SSO_GROUP = 3;
	byte PID_GROUP_ATTRIBUTES = 4;

	@PID(PID_SSO_GROUP)
	public SsoGroup getSsoGroup();
	public void setSsoGroup(SsoGroup ssoGroup);

	@PID(PID_GROUP_ATTRIBUTES)
	public List<SsoGroupAttribute> getGroupAttributes();
	public void setGroupAttributes(List<SsoGroupAttribute> ssoUserAttributes);
}
