/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.povo.msg;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.ST.MC")
public interface MsgStatusMessage extends Message {

	byte PID_TOPIC = 1;
	byte PID_SUFFIX = 2;
	byte PID_IS_WRITE = 3;
	byte PID_IS_CONNECTED = 4;
	byte PID_REMOTE_HOST = 5;
	byte PID_REMOTE_PORT = 6;
	byte PID_SOURCE = 7;
	byte PID_REMOTE_PROCESS_UID = 8;

	@PID(PID_TOPIC)
	public void setTopic(String topic);
	public String getTopic();

	@PID(PID_SUFFIX)
	public void setSuffix(String suffix);
	public String getSuffix();

	@PID(PID_IS_WRITE)
	public void setIsWrite(boolean isWrite);
	public boolean getIsWrite();

	@PID(PID_IS_CONNECTED)
	public void setIsConnected(boolean b);
	public boolean getIsConnected();

	@PID(PID_REMOTE_HOST)
	public String getRemoteHost();
	public void setRemoteHost(String remoteHost);

	@PID(PID_REMOTE_PORT)
	public int getRemotePort();
	public void setRemotePort(int remotePort);

	@PID(PID_SOURCE)
	public void setSource(String r);
	public String getSource();

	@PID(PID_REMOTE_PROCESS_UID)
	public void setRemoteProcessUid(String r);
	public String getRemoteProcessUid();
}
