package com.f1.vortexcommon.msg.eye.reqres;

import java.util.List;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VE.RBUQ")
public interface VortexEyeRunBackupRequest extends VortexEyeRequest {

	@PID(10)
	public List<Long> getBackups();
	public void setBackups(List<Long> backups);
}
