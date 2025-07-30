package com.f1.vortexcommon.msg.eye;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VO.NWSC")
public interface VortexEyeNetworkScan extends VortexEyeEntity {

	@PID(1)
	public String getHostname();
	public void setHostname(String hostName);

	@PID(2)
	public int getIp4();
	public void setIp4(int ip4);

	@PID(3)
	public int[] getPortsFound();
	public void setPortsFound(int ports[]);

	@PID(4)
	public boolean getPingable();
	public void setPingable(boolean pingable);

}
