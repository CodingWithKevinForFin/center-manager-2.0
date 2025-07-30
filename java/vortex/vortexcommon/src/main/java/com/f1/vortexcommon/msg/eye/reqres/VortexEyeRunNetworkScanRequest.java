package com.f1.vortexcommon.msg.eye.reqres;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VE.RNSQ")
public interface VortexEyeRunNetworkScanRequest extends VortexEyeRequest {

	@PID(1)
	public int[] getIp4s();
	public void setIp4s(int[] startIp4);

	@PID(3)
	public int[] getPortsToScan();
	public void setPortsToScan(int[] pi4);

	@PID(4)
	public int getTimeoutMs();
	public void setTimeoutMs(int timeoutMs);
}
