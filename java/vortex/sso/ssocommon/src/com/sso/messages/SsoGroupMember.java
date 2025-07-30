package com.sso.messages;

import com.f1.base.Lockable;
import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;

@VID("F1.SS.GM")
public interface SsoGroupMember extends PartialMessage, Lockable {

	
	byte PID_ID=1;
	byte PID_REVISION=2;
	byte PID_MEMBER_ID=6;
	byte PID_GROUP_ID=7;
	
	@PID(PID_ID)
	public long getId();
	public void setId(long id);

	@PID(PID_REVISION)
	public long getRevision();
	public void setRevision(long id);

	@PID(PID_MEMBER_ID)
	public long getMemberId();
	public void setMemberId(long memberId);

	@PID(PID_GROUP_ID)
	public long getGroupId();
	public void setGroupId(long groupId);

	public SsoGroupMember clone();
}
