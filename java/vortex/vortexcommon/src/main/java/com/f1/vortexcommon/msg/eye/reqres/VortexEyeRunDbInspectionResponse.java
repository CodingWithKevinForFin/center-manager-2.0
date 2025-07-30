package com.f1.vortexcommon.msg.eye.reqres;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VE.RDIR")
public interface VortexEyeRunDbInspectionResponse extends VortexEyeResponse {

	@PID(1)
	public long getDbServerId();
	public void setDbServerId(long dbServerId);
}
