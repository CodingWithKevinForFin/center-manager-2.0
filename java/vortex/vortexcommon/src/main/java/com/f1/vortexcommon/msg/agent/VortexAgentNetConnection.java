package com.f1.vortexcommon.msg.agent;

import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;

@VID("F1.VA.ANC")
public interface VortexAgentNetConnection extends PartialMessage, VortexAgentEntity {

	byte STATE_LISTEN = 1;
	byte STATE_ESTABLISHED = 2;
	byte STATE_TIME_WAIT = 4;
	byte STATE_CLOSE_WAIT = 5;
	byte STATE_CLOSED = 9;
	byte STATE_SYN_SENT = 11;
	byte STATE_SYN_RECV = 13;
	byte STATE_FIN_WAIT1 = 15;
	byte STATE_FIN_WAIT2 = 17;
	byte STATE_CLOSING = 19;
	byte STATE_LAST_ACK = 21;
	byte STATE_DELETE_TCB = 23;

	byte PID_STATE = 1;
	byte PID_LOCAL_HOST = 2;
	byte PID_LOCAL_PORT = 3;
	byte PID_LOCAL_PID = 4;
	byte PID_LOCAL_APP_NAME = 5;
	byte PID_FOREIGN_HOST = 6;
	byte PID_FOREIGN_PORT = 7;

	@PID(PID_STATE)
	public byte getState();
	public void setState(byte type);

	@PID(PID_LOCAL_HOST)
	public String getLocalHost();
	public void setLocalHost(String localHost);

	@PID(PID_LOCAL_PORT)
	public int getLocalPort();
	public void setLocalPort(int port);

	@PID(PID_LOCAL_PID)
	public String getLocalPid();
	public void setLocalPid(String localPid);

	@PID(PID_LOCAL_APP_NAME)
	public String getLocalAppName();
	public void setLocalAppName(String localAppName);

	@PID(PID_FOREIGN_HOST)
	public String getForeignHost();
	public void setForeignHost(String remotePost);

	@PID(PID_FOREIGN_PORT)
	public int getForeignPort();
	public void setForeignPort(int remotePort);
}
