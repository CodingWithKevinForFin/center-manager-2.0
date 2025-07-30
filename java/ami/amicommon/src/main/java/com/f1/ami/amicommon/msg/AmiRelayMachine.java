package com.f1.ami.amicommon.msg;

import com.f1.base.Lockable;
import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VA.M")
public interface AmiRelayMachine extends Message, Lockable {

	@PID(8)
	public void setMachineUid(String machineUid);
	public String getMachineUid();

	@PID(11)
	public long getStartTime();
	public void setStartTime(long startTime);

	@PID(13)
	public String getHostName();
	public void setHostName(String hostName);

	@PID(47)
	public void setRelayProcessUid(String machineUid);
	public String getRelayProcessUid();

	@PID(49)
	public void setAmiServerPort(int port);
	public int getAmiServerPort();

	@PID(22)
	public long getId();
	public void setId(long id);

	@PID(23)
	public long getModifiedOn();
	public void setModifiedOn(long now);

	public AmiRelayMachine clone();

}
