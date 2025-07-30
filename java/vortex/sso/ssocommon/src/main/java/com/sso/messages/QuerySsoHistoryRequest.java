package com.sso.messages;

import java.util.List;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.SS.HQ")
public interface QuerySsoHistoryRequest extends SsoRequest {

	public static final byte TYPE_USER = 1;
	public static final byte TYPE_USER_PARENT_GROUPS = 2;
	public static final byte TYPE_GROUP_PARENT_GROUPS = 3;

	
	byte PID_IDS=2;
	byte PID_TYPE=3;
	
	@PID(PID_IDS)
	public List<Long> getIds();
	public void setIds(List<Long> ids);

	@PID(PID_TYPE)
	public byte getType();
	public void setType(byte type);

}
