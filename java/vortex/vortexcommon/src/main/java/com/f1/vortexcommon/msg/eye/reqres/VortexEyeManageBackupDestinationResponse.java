package com.f1.vortexcommon.msg.eye.reqres;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.eye.VortexEyeBackupDestination;

@VID("F1.VE.MBDR")
public interface VortexEyeManageBackupDestinationResponse extends VortexEyeResponse {

	@PID(10)
	public VortexEyeBackupDestination getBackupDestination();
	public void setBackupDestination(VortexEyeBackupDestination deployment);

}
