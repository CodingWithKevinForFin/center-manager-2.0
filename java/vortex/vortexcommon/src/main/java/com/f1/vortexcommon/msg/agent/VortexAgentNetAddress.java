package com.f1.vortexcommon.msg.agent;

import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;

@VID("F1.VA.ANA")
public interface VortexAgentNetAddress extends PartialMessage, VortexAgentEntity {

	byte SCOPE_GLOBAL = 1;
	byte SCOPE_SITE = 2;
	byte SCOPE_LINK = 3;
	byte SCOPE_HOST = 4;

	byte TYPE_INET = 1;
	byte TYPE_INET6 = 2;

	byte PID_LINK_NAME = 1;
	byte PID_TYPE = 2;
	byte PID_ADDRESS = 4;
	byte PID_BROADCAST = 5;
	byte PID_SCOPE = 6;

	@PID(PID_LINK_NAME)
	public String getLinkName();
	public void setLinkName(String type);

	@PID(PID_TYPE)
	public byte getType();
	public void setType(byte state);

	@PID(PID_ADDRESS)
	public String getAddress();
	public void setAddress(String type);

	@PID(PID_BROADCAST)
	public String getBroadcast();
	public void setBroadcast(String type);

	@PID(PID_SCOPE)
	public byte getScope();
	public void setScope(byte type);
}
