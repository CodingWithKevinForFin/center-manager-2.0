package com.f1.ami.amicommon.msg;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VA.OCR")
public interface AmiRelayOnConnectResponse extends AmiRelayResponse {

	@PID(2)
	public String getMachineUid();
	public void setMachineUid(String machineUid);

	@PID(3)
	public long getStartTime();
	public void setStartTime(long starttime);

	@PID(4)
	public int getAmiServerPort();
	public void setAmiServerPort(int amiServerPort);

	@PID(5)
	public String getRelayId();
	public void setRelayId(String relayId);

	@PID(6)
	public String getHostname();
	public void setHostname(String relayId);

	@PID(7)
	public String getProcessUid();
	public void setProcessUid(String processUid);

	@PID(8)
	public byte getCenterId();
	public void setCenterId(byte centerId);

	@PID(9)
	public boolean getGuaranteedMessagingEnabled();
	public void setGuaranteedMessagingEnabled(boolean guaranteedMessaging);
}
