package com.f1.vortexcommon.msg.eye.reqres;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VE.RDIQ")
public interface VortexEyeRunDbInspectionRequest extends VortexEyeRequest {

	@PID(1)
	public long getDbServerId();
	public void setDbServerId(long dbServerId);
}
