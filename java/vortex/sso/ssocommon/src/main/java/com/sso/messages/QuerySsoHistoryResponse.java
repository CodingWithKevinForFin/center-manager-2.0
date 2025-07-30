package com.sso.messages;

import java.util.List;
import java.util.Map;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.SS.HR")
public interface QuerySsoHistoryResponse extends SsoResponse {

	
	byte PID_USER=3;
	byte PID_PARENT_GROUPS=4;
	
	@PID(PID_USER)
	public Map<Long, List<SsoUser>> getUser();
	public void setUser(Map<Long, List<SsoUser>> users);

	@PID(PID_PARENT_GROUPS)
	public Map<Long, List<SsoGroup>> getParentGroups();
	public void setParentGroups(Map<Long, List<SsoGroup>> map);

}
