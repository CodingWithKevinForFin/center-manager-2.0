package com.vortex.eye.messages;

import java.util.Map;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeResponse;

@VID("F1.VE.VVSR")
public interface VortexVaultResponse extends VortexEyeResponse {

	@PID(21)
	public Map<Long, byte[]> getData();
	public void setData(Map<Long, byte[]> data);
}
