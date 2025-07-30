package com.f1.vortexcommon.msg.eye.reqres;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VE.RCIAQ")
public interface VortexEyeRunCloudInterfaceActionRequest extends VortexEyeRequest {

	byte ACTION_GET_HOST_NAMES = 1;

	@PID(10)
	public long getCloudInterfaceId();
	public void setCloudInterfaceId(long id);

	@PID(11)
	public void setAction(byte b);
	public byte getAction();

}
