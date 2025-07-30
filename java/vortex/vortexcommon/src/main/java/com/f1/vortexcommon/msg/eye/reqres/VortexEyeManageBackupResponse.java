package com.f1.vortexcommon.msg.eye.reqres;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.eye.VortexEyeBackup;

@VID("F1.VE.MBUR")
public interface VortexEyeManageBackupResponse extends VortexEyeResponse {

	@PID(10)
	public VortexEyeBackup getBackup();
	public void setBackup(VortexEyeBackup deployment);

}
