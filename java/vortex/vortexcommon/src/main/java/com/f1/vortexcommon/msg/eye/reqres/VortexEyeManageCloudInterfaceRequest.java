package com.f1.vortexcommon.msg.eye.reqres;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.eye.VortexEyeCloudInterface;

@VID("F1.VE.MCIQ")
public interface VortexEyeManageCloudInterfaceRequest extends VortexEyeRequest {

	@PID(10)
	public VortexEyeCloudInterface getCloudInterface();
	public void setCloudInterface(VortexEyeCloudInterface deployment);

	@PID(11)
	public void setOnlyTest(boolean b);
	public boolean getOnlyTest();

}
