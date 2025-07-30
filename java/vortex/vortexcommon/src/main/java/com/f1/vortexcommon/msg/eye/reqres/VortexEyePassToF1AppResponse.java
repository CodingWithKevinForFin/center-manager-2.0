package com.f1.vortexcommon.msg.eye.reqres;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.povo.f1app.reqres.F1AppResponse;

@VID("F1.VE.PTFAR")
public interface VortexEyePassToF1AppResponse extends VortexEyeResponse {

	@PID(1)
	public void setF1AppResponse(F1AppResponse agentResponse);
	public F1AppResponse getF1AppResponse();
}
