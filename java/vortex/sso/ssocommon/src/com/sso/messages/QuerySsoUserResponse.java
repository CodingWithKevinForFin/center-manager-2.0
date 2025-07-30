package com.sso.messages;

import java.util.List;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.SS.QR")
public interface QuerySsoUserResponse extends SsoResponse {

	
	byte PID_USERS=3;
	byte PID_GROUPS=5;
	byte PID_ATTRIBUTES=4;
	
	@PID(PID_USERS)
	public List<SsoUser> getUsers();
	public void setUsers(List<SsoUser> user);

	@PID(PID_GROUPS)
	public List<SsoGroup> getGroups();
	public void setGroups(List<SsoGroup> user);

	// populated if exactly one user is found
	@PID(PID_ATTRIBUTES)
	public List<SsoGroupAttribute> getAttributes();
	public void setAttributes(List<SsoGroupAttribute> attributes);

}
