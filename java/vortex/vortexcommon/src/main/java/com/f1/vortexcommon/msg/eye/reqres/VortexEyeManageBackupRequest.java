package com.f1.vortexcommon.msg.eye.reqres;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.eye.VortexEyeBackup;

@VID("F1.VE.MBUQ")
public interface VortexEyeManageBackupRequest extends VortexEyeRequest {

	@PID(10)
	public VortexEyeBackup getBackup();
	public void setBackup(VortexEyeBackup backupDestination);

}
