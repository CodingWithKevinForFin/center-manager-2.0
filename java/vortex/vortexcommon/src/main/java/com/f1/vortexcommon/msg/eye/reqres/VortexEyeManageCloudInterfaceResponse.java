package com.f1.vortexcommon.msg.eye.reqres;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.eye.VortexEyeCloudInterface;

@VID("F1.VE.MCIR")
public interface VortexEyeManageCloudInterfaceResponse extends VortexEyeResponse {

	@PID(10)
	public VortexEyeCloudInterface getCloudInterface();
	public void setCloudInterface(VortexEyeCloudInterface deployment);

}
