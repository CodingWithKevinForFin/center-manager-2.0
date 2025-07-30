package com.f1.vortexcommon.msg.agent.reqres;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VA.CQ")
public interface VortexAgentChangesRequest extends VortexAgentRequest {

	short MACHINE = 1;
	short PROCESSES = 4;
	short FILESYSTEMS = 8;
	short NET_CONNECTIONS = 16;
	short NET_LINKS = 32;
	short NET_ADDRESSES = 64;
	short CRON = 128;
	short AGENT_MACHINE_EVENTS = 256;
	short SNAPSHOT = (short) 0xFFFF;

	byte PID_MASK = 1;
	@PID(PID_MASK)
	public short getMask();
	public void setMask(short mask);

}
